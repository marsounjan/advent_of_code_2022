package cz.marsounjan

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8
import java.text.DecimalFormat
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

class Day15 {

    private val puzzleInput = File("./day15/input.txt")
    private val puzzleInputString by lazy {
        puzzleInput.readBytes().toString(UTF_8)
    }
    val inputRegex = "Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)".toRegex()

    data class Coordinates(val x: Int, val y: Int)

    sealed class Thing {

        abstract val coordinates: Coordinates

        data class Sensor(
            override val coordinates: Coordinates,
            val closestBeacon: Beacon
        ) : Thing() {

            val manhattanDistance: Int = getManhattan(closestBeacon.coordinates)
            fun getManhattan(point: Coordinates): Int =
                (coordinates.x - point.x).absoluteValue +
                        (coordinates.y - point.y).absoluteValue

            fun isCovered(point: Coordinates): Boolean {
                return getManhattan(point) <= manhattanDistance
            }

            fun getYCoverage(y: Int): IntRange? {
                val distanceOnY = max(0, manhattanDistance - (y - coordinates.y).absoluteValue)
                if (distanceOnY > 0) {
                    val xLeft = coordinates.x - distanceOnY
                    val xRight = coordinates.x + distanceOnY
                    return xLeft..xRight
                } else {
                    return null
                }
            }

        }

        data class Beacon(
            override val coordinates: Coordinates
        ) : Thing()

    }

    fun parseInput(): List<Thing.Sensor> =
        puzzleInputString.lines()
            .map { inputRegex.matchEntire(it)!! }
            .map { result ->
                val beacon = Thing.Beacon(
                    coordinates = Coordinates(
                        x = result.groups[3]!!.value.toInt(),
                        y = result.groups[4]!!.value.toInt()
                    )
                )

                Thing.Sensor(
                    coordinates = Coordinates(
                        x = result.groups[1]!!.value.toInt(),
                        y = result.groups[2]!!.value.toInt()
                    ),
                    closestBeacon = beacon
                )
            }

    fun IntRange.isOverlaping(other: IntRange): Boolean =
        other.contains(start) || other.contains(last) ||
                this.contains(other.start) || this.contains(other.last)

    fun mergeRanges(a: IntRange, b: IntRange): IntRange = IntRange(
        start = min(a.start, b.start),
        endInclusive = max(a.last, b.last)
    )

    fun List<IntRange>.mergeNotOverlaping(): List<IntRange> {
        val merged = mutableListOf<IntRange>()
        forEach {
            var current: IntRange = it
            var shouldBeMergedWith: IntRange? = null
            shouldBeMergedWith = merged.find { it.isOverlaping(current) }
            merged.remove(shouldBeMergedWith)
            while (shouldBeMergedWith != null) {
                current = mergeRanges(current, shouldBeMergedWith)

                shouldBeMergedWith = merged.find { it.isOverlaping(current) }
                merged.remove(shouldBeMergedWith)
            }

            merged.add(current)
        }
        return merged
    }

    fun partOne(): Int {
        val y = 2000000
        val sensors = parseInput()
        val ranges = sensors
            .mapNotNull { it.getYCoverage(y) }
        val mergedRanges = ranges.mergeNotOverlaping()
        val yCoverage = mergedRanges
            .map { it.count() }
            .sum()
        val yBeacons = sensors
            .filter { it.closestBeacon.coordinates.y == y }
            .map { it.closestBeacon }
            .distinct()
            .size
        return yCoverage - yBeacons
    }

    fun IntRange.isIn(other: IntRange): Boolean =
        other.contains(start) && other.contains(last)

    fun partTwo(): Long {
        val range = 0..4000000
        val sensors = parseInput()
        var xRanges: List<IntRange>
        range.forEach { y ->
            val ranges = sensors
                .mapNotNull { it.getYCoverage(y) }
            xRanges = ranges.mergeNotOverlaping()
            if (!xRanges.any { range.isIn(it) }) {
                var x = -999
                xRanges.forEach {
                        when {
                            range.contains(it.start - 1) -> x = it.start - 1
                            range.contains(it.endInclusive + 1) -> x = it.endInclusive + 1
                        }
                    }
                if(x != -999){
                    return x.toLong() * 4000000L + y.toLong()
                }
            }
        }
        throw IllegalStateException()
    }

}

fun main(args: Array<String>) {
    val puzzle = Day15()
    println("Puzzle output Part 1: ${puzzle.partOne()}")
    println("Puzzle output Part 2: ${puzzle.partTwo()}")
}