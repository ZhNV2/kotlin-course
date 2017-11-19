package ru.spbau.mit

import java.lang.String.join

interface Element {
    fun render(builder: StringBuilder)
}

class ElementWithText(private val text: String): Element {
    override fun render(builder: StringBuilder) {
        builder.append("$text\n")
    }
}

class BasicLine(
        private val title: String,
        private val param: String,
        private val additionalParam: String
): Element {
    override fun render(builder: StringBuilder) {
        val aParam = if (additionalParam == "") "" else "[$additionalParam]"
        val parm = if (param == "" ) "" else "{$param}"
        builder.append("\\$title$parm$aParam\n")
    }
}

abstract class ElementWithChildren : Element {

    protected val children = arrayListOf<Element>()

    protected fun <T : Element> initChild(child: T, init: T.() -> Unit): T {
        child.init()
        children.add(child)
        return child
    }

    override fun render(builder: StringBuilder) {
        for (c in children) {
            c.render(builder)
        }
    }
}

abstract class BasicSection : ElementWithChildren() {

    operator fun String.unaryPlus() {
        initChild(ElementWithText(this)) {}
    }

    fun math(text: String) {
        initChild(BasicLine("math", text, "")) {}
    }

    fun alignment(init: Alignment.() -> Unit) = initChild(Alignment(), init)
}

abstract class BeginEndSection(private val title: String,
                               private val defines: List<Pair<String, String> >): BasicSection() {

    override fun render(builder: StringBuilder) {
        val concatDefines = defines.map { it.first + "=" + it.second }
        BasicLine("begin", title, join(",", concatDefines)).render(builder)
        super.render(builder)
        BasicLine("end", title, "").render(builder)
    }

}

abstract class ExtendedBeginEndSection(title: String,
                                       defines: List<Pair<String, String> >): BeginEndSection(title, defines) {
    fun frame(
            frameTitle: String,
            vararg defines: Pair<String, String>,
            init: Frame.() -> Unit
    ) = initChild(Frame(frameTitle, defines.asList()), init)

    fun itemize(init: ItemList.() -> Unit) = initChild(ItemList("itemize"), init)

    fun enumerate(init: ItemList.() -> Unit) = initChild(ItemList("enumerate"), init)

    fun customTag(
            name: String,
            vararg defines: Pair<String, String>,
            init: CustomTag.() -> Unit
    ) = initChild(CustomTag(name, defines = defines.asList()), init)
}

class CustomTag(title: String, defines: List<Pair<String, String> >) : ExtendedBeginEndSection(title, defines)


class Frame(frameTitle: String, defines: List<Pair<String, String>>) : ExtendedBeginEndSection("frame", defines) {
    init {
        children.add(BasicLine("frametitle", frameTitle, ""))
    }
}

class ItemList(title: String): ExtendedBeginEndSection(title, listOf()) {
    fun item(init: Item.() -> Unit) = initChild(Item(), init)
}

abstract class TagSection(tag: String): BasicSection() {
    init {
        initChild(BasicLine(tag, "", "")) {}
    }
}

class Item : TagSection("item")
class Left : TagSection("left")
class Right : TagSection("right")
class Center : TagSection("center")

class Alignment: BeginEndSection("alignment", listOf()) {

    fun left(init: Left.() -> Unit) = initChild(Left(), init)

    fun right(init: Right.() -> Unit) = initChild(Right(), init)

    fun center(init: Center.() -> Unit) = initChild(Center(), init)
}


class Document : ExtendedBeginEndSection("document", listOf()) {

    private val preambleChildren = arrayListOf<BasicLine>()

    fun documentClass(param: String) {
        preambleChildren.add(BasicLine("documentclass", param, ""))
    }

    fun usepackage(param: String, vararg additionalParams: String) {
        preambleChildren.add(
                BasicLine("usepackage[" + join(",", additionalParams.asList()) + "]", param, "")
        )
    }

    override fun render(builder: StringBuilder) {
        for (c in preambleChildren) {
            c.render(builder)
        }
        super.render(builder)
    }

    override fun toString(): String {
        val s = StringBuilder("")
        render(s)
        return s.toString()
    }

}

fun document(init: Document.() -> Unit): Document {
    val document = Document()
    document.init()
    return document
}

