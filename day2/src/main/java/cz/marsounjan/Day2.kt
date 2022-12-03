package cz.marsounjan

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8

class Day2{

    private val puzzleInput = File("./day2/input.txt")
    private val puzzleInputString by lazy{
        puzzleInput.readBytes().toString(UTF_8)
    }

    enum class RPS(val opponentID : String, val myID: String,  val score : Int) {
        ROCK("A", "X", 1),
        PAPER("B", "Y", 2),
        SCISSORS("C", "Z", 3)
    }

    enum class RPSRoundOutcome(val score : Int, val id : String){
        WIN(6, "Z"),
        DRAW(3, "Y"),
        LOSS(0, "X")
    }

    class RPSRound(
        val opponent : RPS,
        val myself : RPS
    ){
        val outcome : RPSRoundOutcome = when{
            opponent == RPS.ROCK && myself == RPS.PAPER -> RPSRoundOutcome.WIN
            opponent == RPS.SCISSORS && myself == RPS.ROCK -> RPSRoundOutcome.WIN
            opponent == RPS.PAPER && myself == RPS.SCISSORS -> RPSRoundOutcome.WIN
            opponent == myself -> RPSRoundOutcome.DRAW
            else -> RPSRoundOutcome.LOSS
        }

        val score : Int = myself.score + outcome.score
    }

    class RPSRoundPart2(
        val opponent : RPS,
        val outcome : RPSRoundOutcome
    ){
        val myself : RPS = when{
            //win
            opponent == RPS.ROCK && outcome == RPSRoundOutcome.WIN -> RPS.PAPER
            opponent == RPS.SCISSORS && outcome == RPSRoundOutcome.WIN -> RPS.ROCK
            opponent == RPS.PAPER && outcome == RPSRoundOutcome.WIN -> RPS.SCISSORS
            //draw
            outcome == RPSRoundOutcome.DRAW -> opponent
            //loss
            else -> when (opponent){
                RPS.ROCK -> RPS.SCISSORS
                RPS.SCISSORS -> RPS.PAPER
                RPS.PAPER -> RPS.ROCK
            }
        }

        val score : Int = myself.score + outcome.score
    }

    private val opponentMapping = RPS.values().associateBy { it.opponentID }
    private val myselfMapping = RPS.values().associateBy { it.myID }
    private val outcomeMapping = RPSRoundOutcome.values().associateBy { it.id }

    val gameRounds :List<RPSRound> by lazy {
        puzzleInputString.lines()
            .map { line ->
                val (opponent, myself) = line.split(" ")
                RPSRound(
                    opponent =  opponentMapping[opponent]!!,
                    myself = myselfMapping[myself]!!
                )
            }
    }

    val gameRoundsPart2 :List<RPSRoundPart2> by lazy {
        puzzleInputString.lines()
            .map { line ->
                val (opponent, outcome) = line.split(" ")
                RPSRoundPart2(
                    opponent =  opponentMapping[opponent]!!,
                    outcome = outcomeMapping[outcome]!!
                )
            }
    }

    fun partOne() : Int {
        return gameRounds.sumBy { it.score }
    }

    fun partTwo() : Int {
        return gameRoundsPart2.sumBy { it.score }
    }

}

fun main(args: Array<String>) {
    val puzzle = Day2()
    println("Puzzle output Part 1: ${puzzle.partOne()}")
    println("Puzzle output Part 2: ${puzzle.partTwo()}")
}