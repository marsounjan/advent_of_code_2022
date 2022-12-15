package cz.marsounjan

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8

class Day13 {

    private val puzzleInput = File("./day13/input.txt")
    private val puzzleInputString by lazy {
        puzzleInput.readBytes().toString(UTF_8)
    }

    sealed class Packet : Comparable<Packet> {
        data class ValueSet(
            val items: List<Packet>
        ) : Packet()

        data class Value(
            val value: Int
        ) : Packet()

        private fun compareSets(first: Packet.ValueSet, second: Packet.ValueSet): Int {
            var result: Int
            return when {
                first.items.isEmpty() && second.items.isNotEmpty() -> -1
                first.items.isNotEmpty() && second.items.isEmpty() -> 1
                else -> {
                    for (i in 0 until first.items.size) {
                        if (second.items.getOrNull(i) == null) {
                            return 1
                        } else {
                            result = first.items[i].compareTo(second.items[i])
                            if (result != 0) {
                                return result
                            }
                        }
                    }

                    return if (second.items.size > first.items.size) -1 else 0
                }
            }
        }

        override fun compareTo(other: Packet): Int =
            when (this) {
                is ValueSet -> when (other) {
                    is ValueSet -> compareSets(this, other)
                    is Value -> compareSets(this, ValueSet(listOf(other)))
                }

                is Value -> when (other) {
                    is ValueSet -> compareSets(ValueSet(listOf(this)), other)
                    is Value -> value.compareTo(other.value)
                }
            }
    }

    fun parsePacket(line: String): Packet {
        var openBracketPosition: Int = 0
        var openBracketCount: Int = 0
        var number: String = ""
        val packets = mutableListOf<Packet>()
        line.forEachIndexed { index, c ->
            when {
                c == '[' -> {
                    if (openBracketCount == 0) {
                        openBracketPosition = index
                    }
                    openBracketCount++
                }

                c == ']' -> {
                    openBracketCount--
                    if (openBracketCount == 0) {
                        packets.add(parsePacket(line.substring(openBracketPosition + 1, index)))
                    }
                }

                c.isDigit() -> if (openBracketCount == 0) number += c
                c == ',' -> if (openBracketCount == 0) {
                    number.toIntOrNull()?.let { packets.add(Packet.Value(it)) }
                    number = ""
                }
            }
        }
        if (number.isNotBlank()) {
            packets.add(Packet.Value(number.toInt()))
        }

        return Packet.ValueSet(packets)
    }

    fun getPacketPairs(): List<Pair<Packet, Packet>> {
        return puzzleInputString.lines()
            .filter { it.isNotBlank() }
            .chunked(size = 2)
            .map {
                parsePacket(it[0]) to parsePacket(it[1])
            }
    }

    fun partOne(): Int {
        val packets = getPacketPairs()
        return packets
            .map { (first, second) -> first.compareTo(second) }
            .mapIndexed { index, result ->
                if (result <= 0) {
                    index + 1
                } else {
                    0
                }
            }
            .sum()
    }

    fun partTwo(): Int {
        val divider1 = parsePacket("[[2]]")
        val divider2 = parsePacket("[[6]]")

        val packets = getPacketPairs()
            .map { listOf(it.first, it.second) }
            .flatten()+divider1+divider2
        val sortedPackets = packets.sorted()
        val dividerIndex1 = sortedPackets.indexOf(divider1) + 1
        val dividerIndex2 = sortedPackets.indexOf(divider2) + 1

        return dividerIndex1 * dividerIndex2
    }

}

fun main(args: Array<String>) {
    val puzzle = Day13()
    println("Puzzle output Part 1: ${puzzle.partOne()}")
    println("Puzzle output Part 2: ${puzzle.partTwo()}")
}