package cz.marsounjan

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8

class Day7 {

    private val puzzleInput = File("./day7/input.txt")
    private val puzzleInputString by lazy {
        puzzleInput.readBytes().toString(UTF_8)
    }

    private val root: Record.Dir by lazy {
        val root = Record.Dir(parent = null, name = "/")
        var currentDir: Record.Dir = root

        puzzleInputString.lines()
            .forEach { line ->
                when {
                    line.startsWith("\$ ") -> line.removePrefix("\$ ")
                        .split(" ")
                        .let {
                            val cmd = it.get(0)
                            val arg = it.getOrNull(1)
                            when (cmd) {
                                "ls" -> {}
                                "cd" -> when (arg) {
                                    ".." -> currentDir = currentDir.parent!!
                                    else -> {
                                        currentDir = currentDir!!.content.find {
                                            it is Record.Dir && it.name == arg
                                        }!! as Record.Dir
                                    }
                                }

                                else -> throw IllegalStateException()
                            }
                        }

                    else -> line.split(" ")
                        .let {
                            val a = it.get(0)
                            val b = it.get(1)
                            when (a) {
                                "dir" -> {
                                    currentDir.content.add((Record.Dir(parent = currentDir, name = b)))
                                }

                                else -> {
                                    currentDir.content.add(
                                        (Record.File(
                                            parent = currentDir,
                                            name = b,
                                            size = a.toLong()
                                        ))
                                    )
                                }
                            }
                        }
                }
            }

        root
    }

    sealed class Record {

        abstract val parent: Dir?
        abstract val name: String
        abstract val size: Long

        class File(
            override val parent: Dir,
            override val name: String,
            override val size: Long
        ) : Record()

        class Dir(
            override val parent: Dir?,
            override val name: String,
            val content: MutableList<Record> = mutableListOf()
        ) : Record() {

            override val size: Long
                get() = content.sumOf {
                    when (it) {
                        is File -> it.size
                        is Dir -> it.size
                    }
                }
        }

    }


    fun contentRecursively(record: Record): List<Record> {
        val c = mutableListOf<Record>()
        c.add(record)
        when (record) {
            is Record.File -> {}
            is Record.Dir -> {
                record.content.forEach {
                    c.addAll(contentRecursively(it))
                }
            }
        }

        return c
    }

    fun find(matcher: (Record) -> Boolean): List<Record> {
        val allContent = contentRecursively(root)

        return allContent.filter(matcher)
    }

    fun partOne(): Long {
        val matchedFiles = find {
            it is Record.Dir && it.size < 100000
        }

        return matchedFiles.sumOf { it.size }
    }

    fun partTwo(): Long {
        val totalSpace = 70000000
        val necessarySpace = 30000000
        val freeSpace = totalSpace - root.size
        val needToFree = necessarySpace - freeSpace
        val directoriesThatWouldFreedEnough = find {
            it is Record.Dir && it.size >= needToFree
        }

        return directoriesThatWouldFreedEnough.sortedBy { it.size }.first().size
    }

}

fun main(args: Array<String>) {
    val puzzle = Day7()
    println("Puzzle output Part 1: ${puzzle.partOne()}")
    println("Puzzle output Part 2: ${puzzle.partTwo()}")
}