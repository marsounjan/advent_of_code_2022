package cz.marsounjan

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8

class Day17 {

    private val puzzleInput = File("./day17/input.txt")
    private val puzzleInputString by lazy {
        puzzleInput.readBytes().toString(UTF_8)
    }

    class Shape(description: String) {
        val points: Array<Array<Boolean>>

        val height : Int
        val width : Int

        init {
            val lines = description.lines()
            points = Array(lines.size) { y -> Array(lines[y].length) { x -> lines[y][x] == '#' } }
            height = points.size
            width = points[0].size
        }

    }

    val shapes = listOf<Shape>(
        Shape("####"),
        Shape(
            ".#.\n" +
                    "###\n" +
                    ".#."
        ),
        Shape(
            "..#\n" +
                    "..#\n" +
                    "###"
        ),
        Shape(
            "#\n" +
                    "#\n" +
                    "#\n" +
                    "#"
        ),
        Shape(
            "##\n" +
                    "##"
        )
    )

    sealed class Direction {

        object Left : Direction()
        object Right : Direction()
        object Down : Direction()

    }

    fun getMoves(): List<Direction> =
        puzzleInputString.chunked(1)
            .map {
                when (it) {
                    "<" -> Direction.Left
                    ">" -> Direction.Right
                    else -> throw IllegalStateException()
                }
            }

    class World(
        private val wide: Int,
        private val initialShapeX: Int = 2,
        private val initialShapeY: Int = 3,
        private val moves: List<Direction>
    ) {

        var moveIndex : Int = 0
        val lines: MutableList<Array<Boolean>> = mutableListOf()

        fun move() : Direction {
            val direction = moves[moveIndex++.mod(moves.size)]
            return direction
        }

        fun Shape.canShapeBePlacedAt(
            shapeBottomLeftX: Int,
            shapeBottomLeftY: Int
        ) : Boolean {
            when{
                shapeBottomLeftX < 0 -> return false
                shapeBottomLeftX + width > wide -> return false
                shapeBottomLeftY > highestPoint -> return true
                shapeBottomLeftY < 0 -> return false
            }

            (0 until height).forEach { y ->
                (0 until width).forEach { x ->
                    lines.getOrNull(shapeBottomLeftY + y)?.let { line ->
                        if(line[shapeBottomLeftX + x] == true && points[height - 1 -y][x] == true){
                            return false
                        }
                    }
                }
            }

            return true
        }
        fun Shape.canMove(
            direction: Direction,
            shapeBottomLeftX: Int,
            shapeBottomLeftY: Int
        ) = when (direction) {
            is Direction.Left -> canShapeBePlacedAt(
                shapeBottomLeftX = shapeBottomLeftX - 1,
                shapeBottomLeftY = shapeBottomLeftY
            )

            is Direction.Right -> canShapeBePlacedAt(
                shapeBottomLeftX = shapeBottomLeftX + 1,
                shapeBottomLeftY = shapeBottomLeftY
            )

            is Direction.Down -> canShapeBePlacedAt(
                shapeBottomLeftX = shapeBottomLeftX,
                shapeBottomLeftY = shapeBottomLeftY - 1
            )
        }
        fun Shape.store(shapeBottomLeftX: Int, shapeBottomLeftY: Int){
            (0 until height).forEach { y ->
                (0 until width).forEach { x ->
                    if(lines.getOrNull(shapeBottomLeftY + y) == null){
                        lines.add(Array(wide) { false })
                    }

                    if(points[height -1 - y][x]){
                        lines[shapeBottomLeftY + y][shapeBottomLeftX + x] = true
                    }

                }
            }
        }

        fun insert(shape: Shape) {
            var x = initialShapeX
            var y = highestPoint + initialShapeY

            var move = move()
            while(true){
                if(shape.canMove(direction = move, shapeBottomLeftX = x, shapeBottomLeftY = y)) {
                    when(move){
                        is Direction.Left -> x--
                        is Direction.Right -> x++
                        is Direction.Down -> y--
                    }
                }

                if(shape.canMove(direction = Direction.Down, shapeBottomLeftX = x, shapeBottomLeftY = y)) {
                    y--
                } else {
                    break
                }

                move = move()
            }

            shape.store(shapeBottomLeftX = x, shapeBottomLeftY = y)
        }

        val highestPoint: Int
            get() = lines.size

        fun printOut(){
            println("\n=======\n")
            lines.reversed().forEachIndexed { index, line ->
                println(line.map { if (it) "#" else "." }.joinToString(separator = "") + " - $index")
            }
            println("\n=======\n")
        }
    }


    fun partOne(): Int {
        val moves = getMoves()
        val shapes = shapes
        val world = World(
            wide = 7,
            moves = moves
        )

        (0 until 2022).forEach {
            world.insert(shapes[it.mod(shapes.size)])
        }
        //world.printOut()

        return world.highestPoint
    }

    fun partTwo(): Int {
        val moves = getMoves()
        val shapes = shapes
        val world = World(
            wide = 7,
            moves = moves
        )

        (0 until  1000000000000).forEach {
            world.insert(shapes[it.mod(shapes.size)])
        }
        //world.printOut()

        return world.highestPoint
    }

}

fun main(args: Array<String>) {
    val puzzle = Day17()
    println("Puzzle output Part 1: ${puzzle.partOne()}")
    println("Puzzle output Part 2: ${puzzle.partTwo()}")
}