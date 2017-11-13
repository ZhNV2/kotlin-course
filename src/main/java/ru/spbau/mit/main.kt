package ru.spbau.mit

data class Edge(val firstVertex: Int, val secondVertex: Int)

class Solver(private val universities: List<Int>, private val edges: List<Edge>) {

    private fun Boolean.toInt(): Int = if (this) 1 else 0

    private val n: Int = edges.size + 1

    private val isUniversity: List<Boolean>

    init {
        val isUniversityMutable: MutableList<Boolean> = MutableList(n + 1, { false }).apply {
            universities.forEach {
                this[it] = true
            }
        }
        isUniversity = isUniversityMutable.toList()
    }

    private fun dfsBuildTree(node: Int, depth: Int, used: MutableList<Boolean>,
                             neighbours: List<MutableList<Int>>): TreeNode {
        val children: MutableList<TreeNode> = mutableListOf()
        used[node] = true
        val universitiesInSubTree: Int = isUniversity[node].toInt() + neighbours[node]
            .filterNot { used[it] }
            .sumBy {
                val child: TreeNode = dfsBuildTree(it, depth + 1, used, neighbours)
                children.add(child)
                child.universitiesInSubtree
            }
        return TreeNode(children, depth, universitiesInSubTree, node)
    }

    private fun buildTree(): TreeNode {
        val neighbours: List<MutableList<Int>> = List(n + 1, { mutableListOf<Int>() })
        edges.forEach {
            neighbours[it.firstVertex].add(it.secondVertex)
            neighbours[it.secondVertex].add(it.firstVertex)
        }
        val used: MutableList<Boolean> = MutableList(n + 1, { false })
        val root: TreeNode = dfsBuildTree(1, 0, used, neighbours)
        assert(used.subList(1, n + 1).all { it }) {"given list of edges does not describe tree"}
        return root
    }

    private fun dfsGetLcaAnswer(root: TreeNode, toConnect: Int): Long {
        root.children
                .firstOrNull { it.universitiesInSubtree * 2 > universities.size }
                ?.let {
                    val new = universities.size - toConnect - it.universitiesInSubtree
                    return new.toLong() * 2 * root.depth +
                            dfsGetLcaAnswer(it, new + toConnect)
                }
        return (universities.size.toLong() - 2 * toConnect) * root.depth
    }

    private fun dfsGetSumOfDepths(root: TreeNode): Long {
        var sum = if (isUniversity[root.index]) root.depth.toLong() else 0L
        root.children
            .forEach {
                sum += dfsGetSumOfDepths(it)
            }
        return sum
    }

    fun solve(): Long {
        val root: TreeNode = buildTree()
        return dfsGetSumOfDepths(root) - dfsGetLcaAnswer(root, 0)
    }

    private class TreeNode(
        val children: List<TreeNode>,
        val depth: Int,
        val universitiesInSubtree: Int,
        val index: Int
    )
}

fun readListInt(expectedSize: Int): List<Int> {
    val list = readLine()!!.split(" ").map(String::toInt)
    if (list.size != expectedSize) {
        throw IllegalArgumentException("expected $expectedSize numbers, actually read ${list.size} numbers");
    }
    return list
}

fun main(args: Array<String>) {
    val (n, k) = readListInt(2)
    val universities: List<Int> = readListInt(2 * k)
    val edges: List<Edge> = List(n - 1, {
        val (firstVertex, secondVertex) = readListInt(2)
        Edge(firstVertex, secondVertex)
    })
    val answer = Solver(universities, edges).solve()
    println(answer)
}
