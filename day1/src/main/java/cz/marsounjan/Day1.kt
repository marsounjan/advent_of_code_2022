package cz.marsounjan

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8

class Day1{

    private val puzzleInput = File("./day1/input.txt")
    private val puzzleInputString by lazy{
        puzzleInput.readBytes().toString(UTF_8)
    }

    fun partOne() : Int {
        var mostCaloricElfIndex = 0
        var mostCaloricElfCalories = 0
        var currentElfIndex = 0

        var currentElfCalories : Int = 0
        puzzleInputString.lines()
            .forEach { line ->
                val cal = line.toIntOrNull()
                if(cal != null){
                    currentElfCalories += cal
                } else {
                    if(currentElfCalories > mostCaloricElfCalories){
                        mostCaloricElfCalories = currentElfCalories
                        mostCaloricElfIndex = currentElfIndex
                    }

                    currentElfCalories = 0
                    currentElfIndex++
                }
            }

        return mostCaloricElfCalories
    }

    fun partTwo() : Int {
        val elves = mutableListOf<Int>()

        var elfIndex = 0
        var currentElfCalories = 0
        puzzleInputString.lines()
            .forEach { line ->
                val cal = line.toIntOrNull()
                if(cal != null){
                    currentElfCalories += cal
                } else {
                    elves += currentElfCalories
                    elfIndex++
                    currentElfCalories = 0
                }
            }
        elves += currentElfCalories

        return elves.sortedDescending()
            .take(3)
            .sum()
    }

}

fun main(args: Array<String>) {
    val puzzle = Day1()
    println("Puzzle output Part 1: ${puzzle.partOne()}")
    println("Puzzle output Part 2: ${puzzle.partTwo()}")
}