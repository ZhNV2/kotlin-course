package ru.spbau.mit
import org.junit.Test
import kotlin.test.assertEquals

class TestSource {

    @Test
    fun testDocument() {
        val test =
                document {

                }.toString()
        assertEquals(
                """
                    |\begin{document}
                    |\end{document}
                    |
                    """.trimMargin(),
                test)
    }

    @Test
    fun testDocumentClass() {
        val test =
                document {
                    documentClass("article")
                }.toString()
        assertEquals(
                """
                    |\documentclass{article}
                    |\begin{document}
                    |\end{document}
                    |
                    """.trimMargin(),
                test)
    }

    @Test
    fun testUsePackage() {
        val test =
                document {
                    usepackage("babel", "russian", "english")
                }.toString()
        assertEquals(
                """
                    |\usepackage[russian,english]{babel}
                    |\begin{document}
                    |\end{document}
                    |
                    """.trimMargin(),
                test)
    }

    @Test
    fun testFrame() {
        val test =
                document {
                    frame(frameTitle="frametitle", defines = "arg1" to "arg2") {
                    }
                }.toString()
        assertEquals(
                """
                    |\begin{document}
                    |\begin{frame}[arg1=arg2]
                    |\frametitle{frametitle}
                    |\end{frame}
                    |\end{document}
                    |
                    """.trimMargin(),
                test)
    }

    @Test
    fun testEnumerate() {
        val test =
                document {
                    enumerate {
                        item {
                            + "a"
                        }
                        item {
                            + "b"
                        }

                    }
                }.toString()
        assertEquals(
                """
                    |\begin{document}
                    |\begin{enumerate}
                    |\item
                    |a
                    |\item
                    |b
                    |\end{enumerate}
                    |\end{document}
                    |
                    """.trimMargin(),
                test)
    }

    @Test
    fun testItemize() {
        val test =
                document {
                    itemize {
                        item {
                            + "a"
                        }
                        item {
                            + "b"
                        }
                    }
                }.toString()
        assertEquals(
                """
                    |\begin{document}
                    |\begin{itemize}
                    |\item
                    |a
                    |\item
                    |b
                    |\end{itemize}
                    |\end{document}
                    |
                    """.trimMargin(),
                test)
    }

    @Test
    fun testMath() {
        val test =
                document {
                    math("x^2")
                    math("y+x")
                }.toString()
        assertEquals(
                """
                    |\begin{document}
                    |\math{x^2}
                    |\math{y+x}
                    |\end{document}
                    |
                    """.trimMargin(),
                test)
    }

    @Test
    fun testAlignment() {
        val test =
                document {
                    alignment {
                        center {
                            + "a"
                        }
                        left {
                            + "b"
                        }
                        right {
                            + "c"
                        }
                    }
                }.toString()
        assertEquals(
                """
                    |\begin{document}
                    |\begin{alignment}
                    |\center
                    |a
                    |\left
                    |b
                    |\right
                    |c
                    |\end{alignment}
                    |\end{document}
                    |
                    """.trimMargin(),
                test)
    }

    @Test
    fun testCustomTag() {
        val test =
                document {
                    customTag(name = "pyglist", defines = "language" to "kotlin") {
                        +"""
                            |val a = 1
                            |
                            """.trimMargin()
                    }
                }.toString()
        assertEquals(
                """
                    |\begin{document}
                    |\begin{pyglist}[language=kotlin]
                    |val a = 1
                    |
                    |\end{pyglist}
                    |\end{document}
                    |
                    """.trimMargin(),
                test)
    }




    @Test
    fun testComplex() {
        val test =
            document {
                documentClass("article")
                usepackage("babel", "russian", "english")
                customTag(name = "pyglist", defines = "language" to "kotlin") {
                    +"""
                   |val a = 1
                   |
                """.trimMargin()
                }
                frame(frameTitle = "frame1") {


                }
                frame(frameTitle="frametitle", defines = "arg1" to "arg2") {

                }
                enumerate {
                    item {
                        + "a"
                    }
                    item {
                        + "b"
                    }
                }
                itemize {
                    item {
                        + "c"
                    }
                    item {
                        + "d"
                    }
                }
                + "d"
                math("x^2")
                alignment {
                    left {
                        + "d"
                    }
                    right {
                        + "e"
                    }
                    center {
                        + "aa"
                    }
                }
            }.toString()
        assertEquals(
                """
                    |\documentclass{article}
                    |\usepackage[russian,english]{babel}
                    |\begin{document}
                    |\begin{pyglist}[language=kotlin]
                    |val a = 1
                    |
                    |\end{pyglist}
                    |\begin{frame}
                    |\frametitle{frame1}
                    |\end{frame}
                    |\begin{frame}[arg1=arg2]
                    |\frametitle{frametitle}
                    |\end{frame}
                    |\begin{enumerate}
                    |\item
                    |a
                    |\item
                    |b
                    |\end{enumerate}
                    |\begin{itemize}
                    |\item
                    |c
                    |\item
                    |d
                    |\end{itemize}
                    |d
                    |\math{x^2}
                    |\begin{alignment}
                    |\left
                    |d
                    |\right
                    |e
                    |\center
                    |aa
                    |\end{alignment}
                    |\end{document}
                    |
                    """.trimMargin(),
                test)
    }
}
