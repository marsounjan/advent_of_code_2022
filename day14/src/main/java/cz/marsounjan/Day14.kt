package cz.marsounjan

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8
import java.text.DecimalFormat

class Day14 {

    private val puzzleInput = File("./day14/input.txt")
    private val puzzleInputString by lazy {
        puzzleInput.readBytes().toString(UTF_8)
    }

    data class Point(
        val x: Int,
        val y: Int
    )

    data class RockLine(
        val pointA: Point,
        val pointB: Point
    )

    sealed class Particle {
        object Rock : Particle()
        object Sand : Particle()
    }

    class Cave(rocks: List<RockLine>) {

        val particles: HashMap<Point, Particle> = hashMapOf()
        val rockBoundTopLeft: Point
        val rockBoundBottomRight: Point
        var floorY : Int? = null

        init {
            var topLeft: Point? = null
            var bottomRight: Point? = null

            fun updateCaveBounds(point: Point) {
                if (topLeft == null) topLeft = point
                if (bottomRight == null) bottomRight = point

                if (point.x < topLeft!!.x) topLeft = topLeft!!.copy(x = point.x)
                if (point.y < topLeft!!.y) topLeft = topLeft!!.copy(y = point.y)
                if (point.x > bottomRight!!.x) bottomRight = bottomRight!!.copy(x = point.x)
                if (point.y > bottomRight!!.y) bottomRight = bottomRight!!.copy(y = point.y)
            }

            rocks.forEach {
                updateCaveBounds(it.pointA)
                updateCaveBounds(it.pointB)
                insertRockLine(it.pointA, it.pointB)
            }

            rockBoundTopLeft = topLeft!!
            rockBoundBottomRight = bottomRight!!
        }

        private fun insertRockLine(pointA: Point, pointB: Point) {
            if (pointA.x == pointB.x) {
                //vertical line
                if(pointA.y <= pointB.y){
                    (pointA.y..pointB.y).forEach { particles[Point(x = pointA.x, y = it)] = Particle.Rock }
                } else {
                    (pointB.y..pointA.y).forEach { particles[Point(x = pointA.x, y = it)] = Particle.Rock }
                }
            } else if(pointA.y == pointB.y){
                //horizontal line
                if(pointA.x <= pointB.x){
                    (pointA.x..pointB.x).forEach { particles[Point(x = it, y = pointA.y)] = Particle.Rock }
                } else {
                    (pointB.x..pointA.x).forEach { particles[Point(x = it, y = pointA.y)] = Particle.Rock }
                }
            } else {
                throw IllegalStateException()
            }
        }

        private val sandPouringPoint = Point(500, 0)

        fun processSandParticle(pouringPoint: Point): Boolean {
            var position: Point = pouringPoint
            while (true) {
                if(floorY == null && position.y > rockBoundBottomRight.y){
                    return false
                }

                if(floorY != null && position.y == floorY!! - 1){
                    break
                }

                if (particles[position.copy(y = position.y + 1)] == null) {
                    position = position.copy(y = position.y + 1)
                    continue
                }
                if (particles[position.copy(x = position.x - 1, y = position.y + 1)] == null) {
                    position = position.copy(x = position.x - 1, y = position.y + 1)
                    continue
                }
                if (particles[position.copy(x = position.x + 1, y = position.y + 1)] == null) {
                    position = position.copy(x = position.x + 1, y = position.y + 1)
                    continue
                }

                break
            }

            particles[position] = Particle.Sand
            if (position == pouringPoint) {
                return false
            }
            return true
        }

        fun fillWithSand() {
            while (processSandParticle(sandPouringPoint)) {
                //printOut()
            }
        }

        fun printOut(){
            var line : String
            println("\n\n==============\n\n")
            (0 .. rockBoundBottomRight.y).forEach {y ->
                line = "${DecimalFormat("###").format(y)} "
                (rockBoundTopLeft.x .. rockBoundBottomRight.x).forEach {x ->
                    line += when(particles[Point(x,y)]){
                        is Particle.Sand -> "o"
                        is Particle.Rock -> "#"
                        null -> "."
                    }
                }
                println(line)
            }
        }

    }

    fun getRockLines(): List<RockLine> {
        return puzzleInputString.lines()
            .map {line ->
                line.split("->")
                    .map {
                        val coordinates = it.trim().split(",").map { it.toInt() }
                        Point(coordinates.first(), coordinates.last())
                    }
                    .windowed(2)
                    .map {
                        val pointA = it.first()
                        val pointB = it.last()
                        RockLine(pointA, pointB)
                    }
            }
            .flatten()
    }

    fun partOne(): Int {
        val rockLines = getRockLines()
        val cave = Cave(rockLines)
        cave.fillWithSand()
        cave.printOut()
        val sand = cave.particles.values.filter { it is Particle.Sand }.size
        return sand
    }

    fun partTwo(): Int {
        val rockLines = getRockLines()
        val cave = Cave(rockLines)
        cave.floorY = cave.rockBoundBottomRight.y + 2
        cave.fillWithSand()
        cave.printOut()
        val sand = cave.particles.values.filter { it is Particle.Sand }.size
        return sand
    }

}

fun main(args: Array<String>) {
    val puzzle = Day14()
    println("Puzzle output Part 1: ${puzzle.partOne()}")
    println("Puzzle output Part 2: ${puzzle.partTwo()}")
}