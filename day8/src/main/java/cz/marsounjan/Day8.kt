package cz.marsounjan

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8

class Day8 {

    private val puzzleInput = File("./day8/input.txt")
    private val puzzleInputString by lazy {
        puzzleInput.readBytes().toString(UTF_8)
    }

    data class Tree(
        val x: Int,
        val y: Int,
        val legth: Int
    )

    val grid: List<List<Tree>> by lazy {
        val inputLines = puzzleInputString.lines()
        inputLines.mapIndexed { y, line ->
            line.mapIndexed { x, tree ->
                Tree(
                    x = x,
                    y = y,
                    legth = tree.digitToInt()
                )
            }
        }
    }

    fun findVisibleTree(
        coordinateLine : List<Pair<Int, Int>>
    ): List<Tree> {
        val visibleTrees = mutableListOf<Tree>()
        var tallest: Tree? = null
        var current : Tree

        coordinateLine.forEach { (x , y)->
            current = grid[y][x]
            when {
                tallest == null -> {
                    tallest = current
                    visibleTrees.add(current)
                }
                else -> {
                    if (tallest!!.legth < current.legth) {
                        tallest = current
                        visibleTrees.add(current)
                    }
                }
            }
        }

        return visibleTrees
    }

    fun partOne(): Int {
        val height = grid.size
        val width = grid.first().size
        val visibleTrees = hashSetOf<Tree>()

        val horizontalLines = (0 until height).map { y ->
            (0 until width).map { x ->
                Pair(x, y)
            }
        }
        val horizontalLinesReversed = (0 until height).map { y ->
            (width - 1 downTo 0).map { x ->
                Pair(x, y)
            }
        }

        val verticalLines = (0 until width).map { x ->
            (0 until height).map { y ->
                Pair(x, y)
            }
        }
        val verticalLinesReversed = (0 until width).map { x ->
            (height - 1 downTo 0).map { y ->
                Pair(x, y)
            }
        }

        horizontalLines.forEach { line -> visibleTrees.addAll(findVisibleTree(line)) }
        horizontalLinesReversed.forEach { line -> visibleTrees.addAll(findVisibleTree(line)) }
        verticalLines.forEach { line -> visibleTrees.addAll(findVisibleTree(line)) }
        verticalLinesReversed.forEach { line -> visibleTrees.addAll(findVisibleTree(line)) }
        return visibleTrees.size
    }

    fun partTwo(): Int {
        return 0
    }

}

fun main(args: Array<String>) {
    val puzzle = Day8()
    println("Puzzle output Part 1: ${puzzle.partOne()}")
    println("Puzzle output Part 2: ${puzzle.partTwo()}")
}