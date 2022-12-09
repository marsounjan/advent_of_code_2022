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
        abstract fun newPosition(pos : Position) : Position

        data class Up(override val steps: Int) : Move(){
            override fun newPosition(pos: Position): Position = pos.copy(y = pos.y + 1)
        }
        data class Down(override val steps: Int) : Move(){
            override fun newPosition(pos: Position): Position = pos.copy(y = pos.y - 1)
        }
        data class Left(override val steps: Int) : Move(){
            override fun newPosition(pos: Position): Position = pos.copy(x = pos.x - 1)
        }
        data class Right(override val steps: Int) : Move(){
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

    data class Position(val x : Int, val y : Int)
    data class RopeState(
        val head : Position,
        val tail : Position
    )

    fun move(
        rope : RopeState,
        move : Move
    ) : RopeState{
        val newHead = move.newPosition(rope.head)

        val newTail = when{
            //we don't move
            (newHead.y - rope.tail.y).absoluteValue <= 1 && (newHead.x - rope.tail.x).absoluteValue <= 1 -> rope.tail
            //move vertically
            newHead.x == rope.tail.x -> move.newPosition(rope.tail)
            //move horizontally
            newHead.y == rope.tail.y -> move.newPosition(rope.tail)
            //move diagonally
            else -> if((newHead.x - rope.tail.x).absoluteValue > 1){
                move.newPosition(rope.tail.copy(y = newHead.y))
            } else {
                move.newPosition(rope.tail.copy(x = newHead.x))
            }
        }

        return RopeState(
            head = newHead,
            tail = newTail
        )
    }

    val ropePositions : List<RopeState> by lazy{
        var current = RopeState(
            head = Position(0, 0),
            tail = Position(0,0)
        )

        val positions = mutableListOf(current)

        moves.forEach { m ->
            (0 until m.steps).forEach {
                current = move(current, m)
                positions += current
            }
        }

        positions
    }

    fun partOne(): Int {
        return ropePositions
            .map { it.tail }
            .toSet()
            .size
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