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

    fun findVisibleTreeCount(
        coordinateLine: List<Pair<Int, Int>>
    ): List<Tree> {
        val visibleTrees = mutableListOf<Tree>()
        var tallest: Tree? = null
        var current: Tree

        coordinateLine.forEach { (x, y) ->
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

        horizontalLines.forEach { line -> visibleTrees.addAll(findVisibleTreeCount(line)) }
        horizontalLinesReversed.forEach { line -> visibleTrees.addAll(findVisibleTreeCount(line)) }
        verticalLines.forEach { line -> visibleTrees.addAll(findVisibleTreeCount(line)) }
        verticalLinesReversed.forEach { line -> visibleTrees.addAll(findVisibleTreeCount(line)) }
        return visibleTrees.size
    }

    fun findLowerTreeCount(
        baseline: Tree,
        coordinateLine: List<Pair<Int, Int>>
    ): List<Tree> {
        val visibleTrees = mutableListOf<Tree>()
        var current: Tree

        coordinateLine.forEach { (x, y) ->
            current = grid[y][x]
            when {
                else -> {
                    when {
                        baseline.legth > current.legth -> visibleTrees.add(current)
                        baseline.legth == current.legth -> {
                            visibleTrees.add(current)
                            return visibleTrees
                        }
                    }
                }
            }
        }

        return visibleTrees
    }

    fun partTwo(): Int {
        val height = grid.size
        val width = grid.first().size

        var highestTreeScore = 0

        (0 until width).forEach { x ->
            (0 until height).forEach { y ->
                val tree = grid[y][x]
                val left = (x - 1 downTo 0).map { x -> Pair(x, y) }
                val right = (x +1 until width).map { x -> Pair(x, y) }
                val up = (y - 1 downTo 0).map { y -> Pair(x, y) }
                val down = (y + 1 until height).map { y -> Pair(x, y) }

                val lt = findLowerTreeCount(tree, left).size
                val rt =findLowerTreeCount(tree, right).size
                val ut = findLowerTreeCount(tree, up).size
                val bt = findLowerTreeCount(tree, down).size

                val score = lt * rt * ut * bt

                if(highestTreeScore < score){
                    highestTreeScore = score
                }

            }
        }

        return highestTreeScore
    }

}

fun main(args: Array<String>) {
    val puzzle = Day8()
    println("Puzzle output Part 1: ${puzzle.partOne()}")
    println("Puzzle output Part 2: ${puzzle.partTwo()}")
}