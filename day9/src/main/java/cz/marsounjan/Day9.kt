package cz.marsounjan

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8
import kotlin.math.absoluteValue

class Day9 {

    private val puzzleInput = File("./day9/input.txt")
    private val puzzleInputString by lazy {
        puzzleInput.readBytes().toString(UTF_8)
    }

    sealed class Move {

        abstract val steps: Int
        abstract fun newPosition(pos: Position): Position

        data class Up(override val steps: Int) : Move() {
            override fun newPosition(pos: Position): Position = pos.copy(y = pos.y + 1)
        }

        data class Down(override val steps: Int) : Move() {
            override fun newPosition(pos: Position): Position = pos.copy(y = pos.y - 1)
        }

        data class Left(override val steps: Int) : Move() {
            override fun newPosition(pos: Position): Position = pos.copy(x = pos.x - 1)
        }

        data class Right(override val steps: Int) : Move() {
            override fun newPosition(pos: Position): Position = pos.copy(x = pos.x + 1)
        }
    }

    val moves: List<Move> by lazy {
        puzzleInputString.lines()
            .map { line ->
                val (move, steps) = line.split(" ")
                when (move) {
                    "U" -> Move.Up(steps.toInt())
                    "D" -> Move.Down(steps.toInt())
                    "L" -> Move.Left(steps.toInt())
                    "R" -> Move.Right(steps.toInt())
                    else -> throw IllegalStateException("unknown move")
                }
            }
    }

    data class Position(val x: Int, val y: Int)
    data class RopeState(
        val knots: List<Position>
    )

    fun moveTailInDirection(head: Int, tail: Int): Int {
        return if (head > tail + 1) {
            tail + 1
        } else if (head < tail - 1) {
            tail - 1
        } else {
            tail
        }
    }

    fun moveTail(
        head: Position,
        tail: Position
    ): Position {
        return when {
            //don't move
            (head.y - tail.y).absoluteValue <= 1 && (head.x - tail.x).absoluteValue <= 1 -> tail
            //move vertically
            head.x == tail.x -> tail.copy(y = moveTailInDirection(head = head.y, tail.y))
            //move horizontally
            head.y == tail.y -> tail.copy(x = moveTailInDirection(head = head.x, tail.x))
            //move diagonally
            (head.x == tail.x + 1 || head.x == tail.x - 1) && (head.y == tail.y + 2 || head.y == tail.y - 2) ->
                Position(
                    x = head.x,
                    y = moveTailInDirection(head = head.y, tail.y)
                )
            (head.x == tail.x + 2 || head.x == tail.x - 2) && (head.y == tail.y + 1 || head.y == tail.y - 1) ->
                Position(
                    x = moveTailInDirection(head = head.x, tail.x),
                    y = head.y
                )
            //move diagonally longest move (just in part 2)
            else -> Position(
                x = moveTailInDirection(head = head.x, tail.x),
                y = moveTailInDirection(head = head.y, tail.y)
            )
        }
    }

    fun move(
        rope: RopeState,
        move: Move
    ): RopeState {
        return RopeState(
            knots = mutableListOf<Position>().apply {
                rope.knots.forEachIndexed { index, knot ->
                    if (index == 0) {
                        add(move.newPosition(knot))
                    } else {
                        add(
                            moveTail(
                                head = last(),
                                tail = knot
                            )
                        )
                    }
                }
            }
        )
    }

    fun newRopePositions(initialRopeState: RopeState): List<RopeState> {
        var current = initialRopeState
        val positions = mutableListOf(current)

        moves.forEach { m ->
            (0 until m.steps).forEach {
                current = move(current, m)
                positions += current
            }
        }

        return positions
    }

    fun partOne(): Int {
        return newRopePositions(
            RopeState((0 until 2).map { Position(0, 0) })
        )
            .map { it.knots.last() }
            .toSet()
            .size
    }

    fun partTwo(): Int {
        return newRopePositions(
            RopeState((0 until 10).map { Position(0, 0) })
        )
            .map { it.knots.last() }
            .toSet()
            .size
    }

}

fun main(args: Array<String>) {
    val puzzle = Day9()
    println("Puzzle output Part 1: ${puzzle.partOne()}")
    println("Puzzle output Part 2: ${puzzle.partTwo()}")
}