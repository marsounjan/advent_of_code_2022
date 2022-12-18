package cz.marsounjan

import java.io.File
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
        var positions: List<Valve>,
        val releasedPressure: Int
    ) {
        fun shiftTimeByMinute(min: Int = 1): ValveSystem {
            val newValves = valves.map { it.copy() }
            return ValveSystem(
                minute = minute + min,
                valves = newValves,
                positions = positions.map { pos -> newValves.find { it.name == pos.name }!! },
                releasedPressure = releasedPressure + valves.sumOf { if (it.open) it.flowRate else 0 } * min
            )
        }

    }

    fun getInitialValveSystem(vararg initialValve: String): ValveSystem {
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
            positions = initialValve.map { name -> valves.find { it.name == name }!! },
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
                result = 31 * result + positions.hashCode()
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
        if(positions.size == 2){
            if(valves.any { !it.open }){
                val possibleMoves = lazyCartesian(positions[0].tunnels + positions[0].name, positions[1].tunnels + positions[1].name)
                var tempSystem  : ValveSystem
                var valveA : Valve
                var valveB : Valve
                possibleMoves.forEach { (valveAName, valveBName) ->
                    var skip = false
                    tempSystem = shiftTimeByMinute().apply {
                        valveA = valves.find { it.name == valveAName }!!
                        if(valveA.name == positions[0].name || valveA.name == positions[1].name){
                            if(!valveA.open){
                                valveA.open = true
                            } else{
                                skip = true
                            }
                        }
                        valveB = valves.find { it.name == valveBName }!!
                        if(valveB.name == positions[0].name || valveB.name == positions[1].name){
                            if(!valveB.open){
                                valveB.open = true
                            } else{
                                skip = true
                            }
                        }
                        positions = listOf(valveA, valveB).sortedBy { it.name }
                    }
                    if(!skip){
                        options.add(tempSystem)
                    }
                }
            } else {
                options.add(shiftTimeByMinute())
            }
        } else {
            val p = positions[0]
            //try open valve if not open
            if (!p.open && p.flowRate > 0) {
                options.add(
                    shiftTimeByMinute().apply {
                        positions[0].open = true
                    }
                )
            }
            //try all routes
            p.tunnels.forEach { tunnelName ->
                options.add(
                    shiftTimeByMinute().apply {
                        val newValve = valves.find { it.name == tunnelName }!!
                        positions = listOf(newValve)
                    }
                )
            }
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
        val system = getInitialValveSystem("AA")
        val cache = Cache()
        val bestPressure = system.findBestPressure(minutes = 30, cache)
        return bestPressure!!.releasedPressure
    }

    fun partTwo(): Int {
        val system = getInitialValveSystem("AA", "AA")
        val cache = Cache()
        val bestPressure = system.findBestPressure(minutes = 26, cache)
        return bestPressure!!.releasedPressure
    }

    private fun <A, B> lazyCartesian(
        listA: Iterable<A>,
        listB: Iterable<B>
    ): Sequence<Pair<A, B>> =
        sequence {
            listA.forEach { a ->
                listB.forEach { b ->
                    yield(a to b)
                }
            }
        }

}

fun main(args: Array<String>) {
    val puzzle = Day16()
    //println("Puzzle output Part 1: ${puzzle.partOne()}")
    println("Puzzle output Part 2: ${puzzle.partTwo()}")
}