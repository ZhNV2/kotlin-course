package ru.spbau.mit
import kotlin.test.assertEquals
import org.junit.Test


class TestSource {

    @Test
    fun testCase0() {
        val ans = Solver(
            listOf(1, 5, 6, 2),
            listOf(
                Edge(1, 3), Edge(3, 2), Edge(4, 5),
                Edge(3, 7), Edge(4, 3), Edge(4, 6)
            )
        ).solve()
        assertEquals(6, ans)
    }

    @Test
    fun testCase1() {
        val ans = Solver(
            listOf(3, 2, 1, 6, 5, 9),
            listOf(
                Edge(8, 9), Edge(3, 2), Edge(2, 7), Edge(3, 4),
                Edge(7, 6), Edge(4, 5), Edge(2, 1), Edge(2, 8)
            )
        ).solve()
        assertEquals(9, ans)
    }
}
