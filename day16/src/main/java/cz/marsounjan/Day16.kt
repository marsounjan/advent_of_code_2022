package cz.marsounjan

import java.io.File
import java.lang.RuntimeException
import java.nio.charset.StandardCharsets.UTF_8

class Day16 {

    private val puzzleInput = File("./day16/input.txt")
    private val puzzleInputString by lazy {
        puzzleInput.readBytes().toString(UTF_8)
    }

    val inputLineRegex = "Valve ([A-Z]+) has flow rate=(\\d+); tunnel[s]? lead[s]? to valve[s]? (.+)".toRegex()

    data class Valve(
        val name: String,
        val flowRate: Int,
        var open: Boolean,
        val tunnels: List<String>
    )

    data class ValveSystem(
        val minute: Int,
        val valves: List<Valve>,
        var position: Valve,
        val releasedPressure: Int
    ) {
        fun shiftTimeByMinute(min: Int = 1): ValveSystem {
            val newValves = valves.map { it.copy() }
            return ValveSystem(
                minute = minute + min,
                valves = newValves,
                position = newValves.find { it.name == position.name }!!,
                releasedPressure = releasedPressure + valves.sumOf { if (it.open) it.flowRate else 0 } * min
            )
        }

    }

    fun getInitialValveSystem(initialValve: String): ValveSystem {
        val valves = puzzleInputString.lines()
            .map { inputLineRegex.find(it)!! }
            .map { result ->
                val name = result.groups[1]!!.value
                val rate = result.groups[2]!!.value.toInt()
                val tunnels = result.groups[3]!!.value.split(",").map { it.trim() }

                Valve(
                    name = name,
                    flowRate = rate,
                    open = false,
                    tunnels = tunnels
                )
            }

        return ValveSystem(
            minute = 0,
            valves = valves,
            position = valves.find { it.name == initialValve }!!,
            releasedPressure = 0,
        )
    }

    class Cache {

        data class Item(
            val state: ValveSystem,
            val result: ValveSystem
        )

        private val ValveSystem.id: Int
            get() {
                var result = 31 * minute
                result = 31 * result + valves.hashCode()
                result = 31 * result + position.hashCode()
                //result = 31 * result + releasedPressure.hashCode()
                return result
            }

        val results = hashMapOf<Int, Item>()

        fun isBestAt(state: ValveSystem): Boolean {
            val currentBest = results[state.id]?.state
            val isBestAt = currentBest == null || state.releasedPressure >= currentBest.releasedPressure
            return isBestAt
        }

        fun getResultFor(state: ValveSystem): ValveSystem? {
            return results[state.id]?.let {
                if (it.state == state) {
                    it.result
                } else {
                    null
                }
            }
        }

        fun store(state: ValveSystem, result: ValveSystem) {
            results[state.id] = Item(
                state = state,
                result = result
            )
            println("Stored item. Size: ${results.size}")
        }

    }

    fun ValveSystem.findBestPressure(minutes: Int, cache: Cache): ValveSystem? {
        if (!cache.isBestAt(this)) {
            return null
        }

        cache.getResultFor(this)?.let {
            println("Using cached result for min: ${minute} - pressure:${releasedPressure}")
            return it
        }

        if (this.minute >= minutes) {
            return this
        }

        val options = mutableListOf<ValveSystem>()
        //try open valve if not open
        if (!this.position.open && this.position.flowRate > 0) {
            options.add(
                shiftTimeByMinute().apply {
                    position.open = true
                }
            )
        }
        //try all routes
        this.position.tunnels.forEach { tunnelName ->
            options.add(
                shiftTimeByMinute().apply {
                    val newValve = valves.find { it.name == tunnelName }!!
                    position = newValve
                }
            )
        }

        val bestRoute = options
            .mapNotNull { option ->
                option.findBestPressure(minutes, cache)
            }
            .maxByOrNull { it.releasedPressure }

        if (bestRoute != null) {
            cache.store(state = this, result = bestRoute)
        }

        return bestRoute
    }

    fun partOne(): Int {
        val system = getInitialValveSystem(initialValve = "AA")
        val cache = Cache()
        val bestPressure = system.findBestPressure(minutes = 30, cache)
        return bestPressure!!.releasedPressure
    }

    fun partTwo(): Int {
        val system = getInitialValveSystem(initialValve = "AA")
        val cache = Cache()
        val bestPressure = system.findBestPressure(minutes = 26, cache)
        return bestPressure!!.releasedPressure
    }

}

fun main(args: Array<String>) {
    val puzzle = Day16()
    //println("Puzzle output Part 1: ${puzzle.partOne()}")
    println("Puzzle output Part 2: ${puzzle.partTwo()}")
}