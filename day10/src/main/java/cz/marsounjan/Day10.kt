package cz.marsounjan

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8

class Day10 {

    private val puzzleInput = File("./day10/input.txt")
    private val puzzleInputString by lazy {
        puzzleInput.readBytes().toString(UTF_8)
    }

    data class CycleState(
        val num: Int,
        val x: Int
    ) {
        val signal: Int = num * x
    }

    data class OperationOutput(
        val state: CycleState,
        val cycles: List<CycleState>
    )

    sealed class Operation {

        abstract val length: Int

        abstract fun run(state: CycleState): OperationOutput

        object Noop : Operation() {
            override val length = 1
            override fun run(state: CycleState) = OperationOutput(
                state = state.copy(num = state.num + 1),
                cycles = listOf(state)
            )
        }

        data class Add(val addition: Int) : Operation() {
            override val length = 2
            override fun run(state: CycleState): OperationOutput {
                return OperationOutput(
                    state = state.copy(
                        num = state.num + length,
                        x = state.x + addition
                    ),
                    cycles = (0 until length).map {
                        state.copy(num = state.num + it)
                    }
                )
            }
        }
    }

    val ops: List<Operation> =
        puzzleInputString.lines()
            .map { line -> line.split(" ") }
            .map {
                val op = it[0]
                val arg = it.getOrNull(1)

                when (op) {
                    "noop" -> Operation.Noop
                    "addx" -> Operation.Add(arg!!.toInt())
                    else -> throw IllegalStateException("illegal operation")
                }
            }

    fun getCycles(): List<CycleState> {
        var state = CycleState(
            num = 1,
            x = 1
        )
        val cycles = mutableListOf<CycleState>()
        ops.forEach { operation ->
            val out = operation.run(state)
            state = out.state
            cycles.addAll(out.cycles)
        }

        return cycles
    }

    fun partOne(): Int {
        return getCycles()
            .filter {
                when (it.num) {
                    20,
                    60,
                    100,
                    140,
                    180,
                    220 -> true

                    else -> false
                }
            }
            .sumOf { it.signal }
    }

    fun partTwo(): Int {
        return 0
    }

}

fun main(args: Array<String>) {
    val puzzle = Day10()
    println("Puzzle output Part 1: ${puzzle.partOne()}")
    println("Puzzle output Part 2: ${puzzle.partTwo()}")
}