package cz.marsounjan

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8
import kotlin.streams.toList

class Day12 {

    private val puzzleInput = File("./day12/input.txt")
    private val puzzleInputString by lazy {
        puzzleInput.readBytes().toString(UTF_8)
    }

    sealed class Square {

        abstract val x: Int
        abstract val y: Int
        abstract val height: Int
        var shortestPathSteps: Int? = null

        data class Start(
            override val x: Int,
            override val y: Int
        ) : Square() {
            override val height = 0
        }

        data class End(
            override val x: Int,
            override val y: Int
        ) : Square() {
            override val height = 25
        }

        data class Plain(
            override val x: Int,
            override val y: Int,
            override val height: Int
        ) : Square()

    }

    class Map(
        val squares: List<List<Square>>,
        val start: Square,
        val end: Square
    ) {

        init {
            start.shortestPathSteps = 0
        }

        fun getSquare(x: Int, y: Int): Square? {
            return squares.getOrNull(y)?.getOrNull(x)
        }

        fun Square.canHeightBeReached(from: Square): Boolean = height <= from.height + 1
        fun Square.willHaveShorterPath(from: Square): Boolean = shortestPathSteps == null || shortestPathSteps!! > from.shortestPathSteps!! + 1

        fun Square.visit(from: Square): Square {
            shortestPathSteps = from.shortestPathSteps!! + 1
            return this
        }

        fun Square.shouldIVisit(from: Square): Boolean = canHeightBeReached(from) && willHaveShorterPath(from)
        fun goAllDirections(start : Square) {
            var currentPathRound = mutableSetOf<Square>(start)
            var nextPathRound = mutableSetOf<Square>()

            while (currentPathRound.isNotEmpty()){
                currentPathRound.forEach { from ->
                    getSquare(from.x - 1, from.y)?.takeIf { it.shouldIVisit(from) }?.visit(from)?.let { nextPathRound.add(it) }
                    getSquare(from.x, from.y - 1)?.takeIf { it.shouldIVisit(from) }?.visit(from)?.let { nextPathRound.add(it) }
                    getSquare(from.x + 1, from.y)?.takeIf { it.shouldIVisit(from) }?.visit(from)?.let { nextPathRound.add(it) }
                    getSquare(from.x, from.y + 1)?.takeIf { it.shouldIVisit(from) }?.visit(from)?.let { nextPathRound.add(it) }
                }

                currentPathRound = nextPathRound
                nextPathRound = mutableSetOf()
            }
        }

        fun findShortestTrack(start : Square = this.start): Int? {
            goAllDirections(start)
            return end.shortestPathSteps
        }
    }

    fun getMap(): Map {
        val squares = mutableListOf<MutableList<Square>>()
        lateinit var start: Square.Start
        lateinit var end: Square.End

        puzzleInputString.lines()
            .forEachIndexed { y, line ->
                squares.add(mutableListOf())
                line.chars().toList()
                    .forEachIndexed { x, i ->
                        squares[y].add(
                            when (i) {
                                'S'.code -> {
                                    start = Square.Start(x = x, y = y)
                                    start
                                }

                                'E'.code -> {
                                    end = Square.End(x = x, y = y)
                                    end
                                }

                                else -> Square.Plain(x = x, y = y, height = i - 'a'.code)
                            }
                        )
                    }
            }

        return Map(squares = squares, start = start, end = end)
    }

    fun partOne(): Int {
        return getMap().findShortestTrack()!!
    }

    fun partTwo(): Int {
        val map = getMap()
        val zeroHeightSquares = map.squares.map { it.mapNotNull { if(it.height == 0) it else null } }.flatten()

        return zeroHeightSquares
            .mapNotNull { s ->
                val m = getMap()
                val startingSquare = m.getSquare(s.x, s.y)!!
                startingSquare.shortestPathSteps = 0
                m.findShortestTrack(startingSquare)
            }.minOf { it }
    }

}

fun main(args: Array<String>) {
    val puzzle = Day12()
    println("Puzzle output Part 1: ${puzzle.partOne()}")
    println("Puzzle output Part 2: ${puzzle.partTwo()}")
}