package cz.marsounjan

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8

class Day6 {

    private val puzzleInput = File("./day6/input.txt")
    private val puzzleInputString by lazy {
        puzzleInput.readBytes().toString(UTF_8)
    }

    fun partOne(): Int {
        var markerLocation : Int = 0
        for (i in 0 until puzzleInputString.length - 4) {
            val marker = puzzleInputString.substring(i).take(4)
            if(marker.length == 4 && marker.toCharArray().distinct().size == 4){
                markerLocation = i + 4
                break
            }
        }

        return markerLocation
    }

    fun partTwo(): Int {
        var markerLocation : Int = 0
        for (i in 0 until puzzleInputString.length - 14) {
            val marker = puzzleInputString.substring(i).take(14)
            if(marker.length == 14 && marker.toCharArray().distinct().size == 14){
                markerLocation = i + 14
                break
            }
        }

        return markerLocation
    }

}

fun main(args: Array<String>) {
    val puzzle = Day6()
    println("Puzzle output Part 1: ${puzzle.partOne()}")
    println("Puzzle output Part 2: ${puzzle.partTwo()}")
}