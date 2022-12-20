package cz.marsounjan

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8
import kotlin.math.absoluteValue

class Day18 {

    private val puzzleInput = File("./day18/input.txt")
    private val puzzleInputString by lazy {
        puzzleInput.readBytes().toString(UTF_8)
    }

    data class Cube(val x: Int, val y: Int, val z: Int)

    sealed class Coordinates(
        private val c1: Int,
        private val c2: Int
    ) {
        data class TopBottom(val x: Int, val z: Int) : Coordinates(x, z)
        data class FrontBack(val x: Int, val y: Int) : Coordinates(x, y)
        data class LeftRight(val y: Int, val z: Int) : Coordinates(y, z)
    }

    val topBottom = hashMapOf<Coordinates.TopBottom, MutableList<Cube>>()
    val leftRight = hashMapOf<Coordinates.LeftRight, MutableList<Cube>>()
    val frontBack = hashMapOf<Coordinates.FrontBack, MutableList<Cube>>()

    fun Cube.insert() {
        topBottom.getOrPut(Coordinates.TopBottom(x = x, z = z)) { mutableListOf() }.add(this)
        frontBack.getOrPut(Coordinates.FrontBack(x = x, y = y)) { mutableListOf() }.add(this)
        leftRight.getOrPut(Coordinates.LeftRight(y = y, z = z)) { mutableListOf() }.add(this)
    }

    fun getCubes(): List<Cube> {
        return puzzleInputString.lines()
            .map { it.split(",") }
            .map { (x, y, z) ->
                Cube(x.toInt(), y.toInt(), z.toInt())
            }
    }

    fun List<Cube>.countSurface(axis: (Cube) -> Int): Int {
        var surfaceCount = 0
        var previousCubeOn: Int = 0
        var cubePos: Int
        forEachIndexed { index, cube ->
            cubePos = axis(cube)
            if (index == 0) {
                previousCubeOn = cubePos
                surfaceCount += 2
            } else if ((previousCubeOn - cubePos).absoluteValue > 1) {
                surfaceCount += 2
            }

            previousCubeOn = cubePos
        }


        return surfaceCount
    }

    fun partOne(): Int {
        val cubes = getCubes()
        cubes.forEach { it.insert() }

        val sortedTopBottom = topBottom.values.map { it.sortBy { it.y }; it }
        val sortedFrontBack = frontBack.values.map { it.sortBy { it.z }; it }
        val sortedLeftRight = leftRight.values.map { it.sortBy { it.x }; it }

        val tb = sortedTopBottom.map { it.countSurface { it.y } }.sum()
        val fb = sortedFrontBack.map { it.countSurface { it.z } }.sum()
        val lr = sortedLeftRight.map { it.countSurface { it.x } }.sum()

        return tb + fb + lr
    }

    fun partTwo(): Int {
        return 0
    }

}

fun main(args: Array<String>) {
    println("Puzzle output Part 1: ${Day18().partOne()}")
    println("Puzzle output Part 2: ${Day18().partTwo()}")
}