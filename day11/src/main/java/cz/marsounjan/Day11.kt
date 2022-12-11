package cz.marsounjan

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8
import kotlin.math.floor

class Day11 {

    private val puzzleInput = File("./day11/input.txt")
    private val puzzleInputString by lazy {
        puzzleInput.readBytes().toString(UTF_8)
    }

    data class Item(
        var worryLevel: Int
    )

    class Monkey(
        val operation: (Int) -> Int,
        val test: (Item) -> Boolean,
        val action_true: (Item) -> Unit,
        val action_false: (Item) -> Unit,
        vararg initialItems: Int
    ) {

        val items = mutableListOf<Item>().apply {
            addAll(initialItems.map { Item(it) })
        }

        var inspectedItemCount: Int = 0

        fun catch(item: Item) {
            items.add(item)
        }

        fun round() {
            var item = items.removeFirstOrNull()
            while (item != null) {
                item.worryLevel = floor(operation(item.worryLevel) / 3f).toInt()
                if (test(item)) {
                    action_true(item)
                } else {
                    action_false(item)
                }

                inspectedItemCount++
                item = items.removeFirstOrNull()
            }
        }

    }

    /**
     * Monkey 0:
     *   Starting items: 83, 97, 95, 67
     *   Operation: new = old * 19
     *   Test: divisible by 17
     *     If true: throw to monkey 2
     *     If false: throw to monkey 7
     *
     * Monkey 1:
     *   Starting items: 71, 70, 79, 88, 56, 70
     *   Operation: new = old + 2
     *   Test: divisible by 19
     *     If true: throw to monkey 7
     *     If false: throw to monkey 0
     *
     * Monkey 2:
     *   Starting items: 98, 51, 51, 63, 80, 85, 84, 95
     *   Operation: new = old + 7
     *   Test: divisible by 7
     *     If true: throw to monkey 4
     *     If false: throw to monkey 3
     *
     * Monkey 3:
     *   Starting items: 77, 90, 82, 80, 79
     *   Operation: new = old + 1
     *   Test: divisible by 11
     *     If true: throw to monkey 6
     *     If false: throw to monkey 4
     *
     * Monkey 4:
     *   Starting items: 68
     *   Operation: new = old * 5
     *   Test: divisible by 13
     *     If true: throw to monkey 6
     *     If false: throw to monkey 5
     *

     * Monkey 7:
     *   Starting items: 98, 81, 63, 65, 84, 71, 84
     *   Operation: new = old + 3
     *   Test: divisible by 2
     *     If true: throw to monkey 2
     *     If false: throw to monkey 3
     */

    val monkeys: List<Monkey>
        get() = mutableListOf<Monkey>().apply {
            // 0
            add(Monkey(
                operation = { it * 19 },
                test = { it.worryLevel.mod(17) == 0 },
                action_true = { item -> this[2].catch(item) },
                action_false = { item -> this[7].catch(item) },
                83, 97, 95, 67
            ))
            // 1
            add(Monkey(
                operation = { it + 2 },
                test = { it.worryLevel.mod(19) == 0 },
                action_true = { item -> this[7].catch(item) },
                action_false = { item -> this[0].catch(item) },
                71, 70, 79, 88, 56, 70
            ))
            // 2
                    add(Monkey(
                operation = { it + 7 },
                test = { it.worryLevel.mod(7) == 0 },
                action_true = { item -> this[4].catch(item) },
                action_false = { item -> this[3].catch(item) },
                98, 51, 51, 63, 80, 85, 84, 95
            ))
            // 3
            add(Monkey(
                operation = { it + 1 },
                test = { it.worryLevel.mod(11) == 0 },
                action_true = { item -> this[6].catch(item) },
                action_false = { item -> this[4].catch(item) },
                77, 90, 82, 80, 79
            ))
            // 4
                    add(Monkey(
                operation = { it * 5 },
                test = { it.worryLevel.mod(13) == 0 },
                action_true = { item -> this[6].catch(item) },
                action_false = { item -> this[5].catch(item) },
                68
            ))
            // 5
                    add(Monkey(
                operation = { it + 5 },
                test = { it.worryLevel.mod(3) == 0 },
                action_true = { item -> this[1].catch(item) },
                action_false = { item -> this[0].catch(item) },
                60, 94
            ))
            //6
            add(Monkey(
                operation = { it * it },
                test = { it.worryLevel.mod(5) == 0 },
                action_true = { item -> this[5].catch(item) },
                action_false = { item -> this[1].catch(item) },
                81, 51, 85
            ))
            // 7
            add(Monkey(
                operation = { it + 3 },
                test = { it.worryLevel.mod(2) == 0 },
                action_true = { item -> this[2].catch(item) },
                action_false = { item -> this[3].catch(item) },
                98, 81, 63, 65, 84, 71, 84
            ))
        }

    fun partOne(): Int {
        val mks = monkeys
        (0 until 20).forEach { round ->
            mks.forEach { monkey ->
                monkey.round()
            }
        }

        val mksTop = mks.sortedByDescending { it.inspectedItemCount }
            .take(2)
        return mksTop.first().inspectedItemCount * mksTop.last().inspectedItemCount
    }

    fun partTwo(): Int {
        return 0
    }

}

fun main(args: Array<String>) {
    val puzzle = Day11()
    println("Puzzle output Part 1: ${puzzle.partOne()}")
    println("Puzzle output Part 2: ${puzzle.partTwo()}")
}