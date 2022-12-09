package cz.marsounjan

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8

class Day9 {

    private val puzzleInput = File("./day9/input.txt")
    private val puzzleInputString by lazy {
        puzzleInput.readBytes().toString(UTF_8)
    }

    fun partOne(): Int {
       return 0
    }

    fun partTwo(): Int {
       return 0
    }

}

fun main(args: Array<String>) {
    val puzzle = Day9()
    println("Puzzle output Part 1: ${puzzle.partOne()}")
    println("Puzzle output Part 2: ${puzzle.partTwo()}")
}