package cz.marsounjan

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8

class Day3 {

    private val puzzleInput = File("./day3/input.txt")
    private val puzzleInputString by lazy {
        puzzleInput.readBytes().toString(UTF_8)
    }

    fun priorityOfItem(itemType: Char): Int {
        var i = 'a'
        var priority = 1
        while (i <= 'z') {
            if (itemType == i) {
                return priority
            }
            i++
            priority++
        }

        i = 'A'
        while (i <= 'Z') {
            if (itemType == i) {
                return priority
            }
            i++
            priority++
        }

        throw IllegalStateException("invalid char $itemType")
    }

    fun partOne(): Int {
        return puzzleInputString.lines()
            .map {
                it.substring(0 until it.length / 2) to it.substring(it.length / 2 until it.length)
            }
            .map { (compartment1, compartment2) ->
                compartment1.asIterable()
                    .intersect(compartment2.asIterable())
                    .first()
            }
            .map { errorItemType ->
                priorityOfItem(errorItemType)
            }
            .sum()
    }

    fun partTwo(): Int {
        return puzzleInputString.lines()
            .chunked(3)
            .map {(first, second, third) ->
            first.asIterable()
                .intersect(second.asIterable())
                .intersect(third.asIterable())
                .first()
            }
            .map { errorItemType ->
                priorityOfItem(errorItemType)
            }
            .sum()
    }

}

fun main(args: Array<String>) {
    val puzzle = Day3()
    println("Puzzle output Part 1: ${puzzle.partOne()}")
    println("Puzzle output Part 2: ${puzzle.partTwo()}")
}