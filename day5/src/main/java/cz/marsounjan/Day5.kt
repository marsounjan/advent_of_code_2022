package cz.marsounjan

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8

class Day5 {

    private val puzzleInput = File("./day5/input.txt")
    private val puzzleInputString by lazy {
        puzzleInput.readBytes().toString(UTF_8)
    }

    private val cratesInput = File("./day5/cratesInput.txt")
    private val cratesInputString by lazy {
        cratesInput.readBytes().toString(UTF_8)
    }

    private val moveRegex = "move (\\d+) from (\\d+) to (\\d+)".toRegex()

    fun crates(): MutableList<MutableList<String>> = mutableListOf<MutableList<String>>().apply {
        cratesInputString.lines()
            .reversed()
            .forEachIndexed { lineIndex, line ->
                line.chunked(4)
                    .map { it.trim().removePrefix("[").removeSuffix("]") }
                    .forEachIndexed { crateIndex, crate ->
                        if (crate.isNotBlank()) {
                            println("Crate $crate at $lineIndex:$crateIndex")
                            if (lineIndex == 0) add(mutableListOf(crate)) else this.get(crateIndex).add(crate)
                        } else {
                            println("No crate at $lineIndex:$crateIndex")
                        }
                    }
            }
    }

    private fun moves() : List<Triple<Int, Int, Int>> = puzzleInputString.lines()
            .map { line ->
                val matchResult = moveRegex.find(line)!!
                Triple(
                    matchResult.groups[1]!!.value.toInt(),
                    matchResult.groups[2]!!.value.toInt(),
                    matchResult.groups[3]!!.value.toInt()
                )
            }

    fun partOne(): String {
        val crates = crates()
        val moves = moves()
        moves.forEach { (amount, from, to) ->
            (0 until amount).forEach {
                crates[to-1].add(crates[from-1].removeLast())
            }
        }
        return crates.map { it.last() }.joinToString(separator = "")
    }

    fun partTwo(): Int {
        return 0
    }

}

fun main(args: Array<String>) {
    val puzzle = Day5()
    println("Puzzle output Part 1: ${puzzle.partOne()}")
    println("Puzzle output Part 2: ${puzzle.partTwo()}")
}