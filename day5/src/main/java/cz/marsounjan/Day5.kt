package cz.marsounjan

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8

class Day5 {

    private val puzzleInput = File("./day5/input.txt")
    private val puzzleInputString by lazy {
        puzzleInput.readBytes().toString(UTF_8)
    }

    private val elvenRanges by lazy {
        puzzleInputString.lines()
            .map { it.split(",")
                .map { it.split("-") }
                .map { it.map { it.toInt() } }
                .map { it[0] .. it[1] }
            }
    }

    fun partOne(): Int {
        return 0
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