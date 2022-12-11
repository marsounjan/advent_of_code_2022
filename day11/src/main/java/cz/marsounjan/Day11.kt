package cz.marsounjan

import java.io.File
import java.math.BigInteger
import java.nio.charset.StandardCharsets.UTF_8

class Day11 {

    private val puzzleInput = File("./day11/input.txt")
    private val puzzleInputString by lazy {
        puzzleInput.readBytes().toString(UTF_8)
    }

    class Item(
        val worryLevel: Int,
        private val monkeys: List<Monkey>
    ) {

        var worryLevelReminders = IntArray(monkeys.size)
            private set

        fun monkeyPlayWithItem(operation: (Int) -> Int): Item =
            Item(
                worryLevel = operation(worryLevel),
                monkeys = monkeys
            )
                .apply {
                    this.worryLevelReminders = this@Item.worryLevelReminders
                        .mapIndexed { index, i ->
                            monkeys[index].test_mod(operation(i))
                        }.toIntArray()
                }

        init {
            monkeys.forEachIndexed { index, monkey ->
                worryLevelReminders[index] = monkey.test_mod(worryLevel)
            }
        }

    }

    class Monkey(
        val index: Int,
        val operation: (Int) -> Int,
        val test_mod: (Int) -> Int,
        val action_true: (Item) -> Unit,
        val action_false: (Item) -> Unit
    ) {

        val items = mutableListOf<Item>()

        fun addInitialItems(monkeys: List<Monkey>, vararg item: Int) {
            item.forEach {
                items.add(
                    Item(
                        worryLevel = it,
                        monkeys = monkeys
                    )
                )
            }
        }

        var inspectedItemCount: BigInteger = 0.toBigInteger()

        fun catch(item: Item) {
            items.add(item)
        }

        fun round(worryLevelDivider: Int) {
            var item = items.removeFirstOrNull()
            var newItem: Item
            while (item != null) {
                newItem = item.monkeyPlayWithItem(operation)
                /*newItem = Item(
                    worryLevel = operation(item.worryLevel) / worryLevelDivider,
                    monkeys = allMonkeys
                )*/

                if (newItem.worryLevelReminders[index] == 0) {
                    action_true(newItem)
                } else {
                    action_false(newItem)
                }

                inspectedItemCount++
                item = items.removeFirstOrNull()
            }
        }

    }

    val monkeys: List<Monkey>
        get() = mutableListOf<Monkey>().apply {
            // 0
            add(
                Monkey(
                    index = 0,
                    operation = { it * 19 },
                    test_mod = { it.mod(17) },
                    action_true = { item -> this[2].catch(item) },
                    action_false = { item -> this[7].catch(item) }
                )
            )
            // 1
            add(
                Monkey(
                    index = 1,
                    operation = { it + 2 },
                    test_mod = { it.mod(19) },
                    action_true = { item -> this[7].catch(item) },
                    action_false = { item -> this[0].catch(item) },

                    )
            )
            // 2
            add(
                Monkey(
                    index = 2,
                    operation = { it + 7 },
                    test_mod = { it.mod(7) },
                    action_true = { item -> this[4].catch(item) },
                    action_false = { item -> this[3].catch(item) },

                    )
            )
            // 3
            add(
                Monkey(
                    index = 3,
                    operation = { it + 1 },
                    test_mod = { it.mod(11) },
                    action_true = { item -> this[6].catch(item) },
                    action_false = { item -> this[4].catch(item) },

                    )
            )
            // 4
            add(
                Monkey(
                    index = 4,
                    operation = { it * 5 },
                    test_mod = { it.mod(13) },
                    action_true = { item -> this[6].catch(item) },
                    action_false = { item -> this[5].catch(item) },

                    )
            )
            // 5
            add(
                Monkey(
                    index = 5,
                    operation = { it + 5 },
                    test_mod = { it.mod(3) },
                    action_true = { item -> this[1].catch(item) },
                    action_false = { item -> this[0].catch(item) },

                    )
            )
            //6
            add(
                Monkey(
                    index = 6,
                    operation = { it * it },
                    test_mod = { it.mod(5) },
                    action_true = { item -> this[5].catch(item) },
                    action_false = { item -> this[1].catch(item) },

                    )
            )
            // 7
            add(
                Monkey(
                    index = 7,
                    operation = { it + 3 },
                    test_mod = { it.mod(2) },
                    action_true = { item -> this[2].catch(item) },
                    action_false = { item -> this[3].catch(item) },

                    )
            )


            this[0].addInitialItems(this, 83, 97, 95, 67)
            this[1].addInitialItems(this, 71, 70, 79, 88, 56, 70)
            this[2].addInitialItems(this, 98, 51, 51, 63, 80, 85, 84, 95)
            this[3].addInitialItems(this, 77, 90, 82, 80, 79)
            this[4].addInitialItems(this, 68)
            this[5].addInitialItems(this, 60, 94)
            this[6].addInitialItems(this, 81, 51, 85)
            this[7].addInitialItems(this, 98, 81, 63, 65, 84, 71, 84)

        }

    fun partOne(): BigInteger {
        val mks = monkeys
        (0 until 20).forEach { round ->
            mks.forEach { monkey ->
                monkey.round(worryLevelDivider = 3)
            }
        }

        val mksTop = mks.sortedByDescending { it.inspectedItemCount }
            .take(2)
        return mksTop.first().inspectedItemCount * mksTop.last().inspectedItemCount
    }

    fun partTwo(): BigInteger {
        val mks = monkeys
        (0 until 10_000).forEach { round ->
            mks.forEach { monkey ->
                monkey.round(worryLevelDivider = 1)
            }
        }

        val mksTop = mks.sortedByDescending { it.inspectedItemCount }
            .take(2)
        return mksTop.first().inspectedItemCount * mksTop.last().inspectedItemCount
    }

}

fun main(args: Array<String>) {
    val puzzle = Day11()
    println("Puzzle output Part 1: ${puzzle.partOne()}")
    println("Puzzle output Part 2: ${puzzle.partTwo()}")
}