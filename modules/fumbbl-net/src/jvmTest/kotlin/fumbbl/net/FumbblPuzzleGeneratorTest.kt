package fumbbl.net

import com.jervisffb.engine.GameEngineController
import com.jervisffb.engine.actions.Cancel
import com.jervisffb.engine.actions.CoinSideSelected
import com.jervisffb.engine.actions.CoinTossResult
import com.jervisffb.engine.actions.D3Result
import com.jervisffb.engine.actions.D6Result
import com.jervisffb.engine.actions.D8Result
import com.jervisffb.engine.actions.DiceRollResults
import com.jervisffb.engine.actions.EndSetup
import com.jervisffb.engine.actions.GameAction
import com.jervisffb.engine.actions.PitchSquareSelected
import com.jervisffb.engine.actions.PlayerSelected
import com.jervisffb.engine.actions.SetBallPosition
import com.jervisffb.engine.actions.SetPlayerPosition
import com.jervisffb.engine.actions.SetPlayerStateAction
import com.jervisffb.engine.model.Coin
import com.jervisffb.engine.model.Game
import com.jervisffb.engine.model.Pitch
import com.jervisffb.engine.model.PlayerId
import com.jervisffb.engine.model.PlayerState
import com.jervisffb.engine.rules.StandardBB2020Rules
import com.jervisffb.engine.serialize.JervisSerialization
import com.jervisffb.fumbbl.net.api.commands.ServerCommandGameState
import com.jervisffb.fumbbl.net.utils.fromFumbblState
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import java.io.File
import kotlin.test.Test

data class PlayerPlacement(
    val playerId: String,
    val x: Int,
    val y: Int,
    val state: PlayerState,
    val hasTackleZones: Boolean,
)

data class BoardState(
    val ballX: Int?,
    val ballY: Int?,
    val ballCarrierId: String?,
    val players: List<PlayerPlacement>,
)

class FumbblPuzzleGeneratorTest {

    private fun standing(id: String, x: Int, y: Int) = PlayerPlacement(id, x, y, PlayerState.STANDING, true)
    private fun prone(id: String, x: Int, y: Int) = PlayerPlacement(id, x, y, PlayerState.PRONE, false)
    private fun stunned(id: String, x: Int, y: Int) = PlayerPlacement(id, x, y, PlayerState.STUNNED, false)

    private val td1Board = BoardState(
        ballX = 21, ballY = 3, ballCarrierId = null,
        players = listOf(
            standing("17263175", 21, 2),
            standing("17263176", 20, 2),
            standing("17263177", 14, 8),
            standing("17263178", 21, 5),
            standing("17263179", 21, 1),
            standing("17263180", 19, 1),
            standing("17263181", 20, 8),
            standing("17263182", 18, 4),
            prone("17263183", 15, 2),
            standing("17263184", 19, 2),
            standing("17302221", 15, 3),
            standing("17302222", 19, 3),
            standing("17302223", 14, 5),
            standing("17302224", 16, 8),
            standing("17302226", 16, 2),
            prone("17302230", 14, 3),
            standing("17302232", 14, 2),
        )
    )

    private val td2Board = BoardState(
        ballX = 20, ballY = 4, ballCarrierId = null,
        players = listOf(
            standing("17263175", 10, 6),
            standing("17263176", 10, 5),
            standing("17263177", 12, 8),
            standing("17263178", 10, 4),
            standing("17263179", 10, 12),
            standing("17263180", 10, 8),
            standing("17263181", 10, 10),
            standing("17263182", 10, 14),
            standing("17263183", 12, 9),
            standing("17263184", 12, 10),
            standing("17302221", 13, 12),
            standing("17302222", 14, 8),
            standing("17302223", 13, 10),
            standing("17302224", 19, 7),
            standing("17302227", 22, 7),
            standing("17302228", 13, 8),
            standing("17302229", 13, 4),
            standing("17302230", 13, 11),
            standing("17302232", 14, 9),
        )
    )

    private val td3Board = BoardState(
        ballX = 24, ballY = 5, ballCarrierId = "17263178",
        players = listOf(
            standing("17263175", 23, 4),
            standing("17263176", 22, 8),
            prone("17263177", 16, 6),
            standing("17263178", 24, 5),
            standing("17263180", 25, 6),
            standing("17263181", 18, 10),
            standing("17263182", 25, 4),
            standing("17263183", 24, 6),
            standing("17263184", 23, 6),
            prone("17302221", 23, 9),
            prone("17302222", 21, 8),
            standing("17302223", 19, 9),
            standing("17302224", 19, 10),
            prone("17302227", 23, 8),
            stunned("17302228", 23, 5),
            standing("17302232", 19, 12),
        )
    )

    private data class PuzzleSpec(
        val label: String,
        val fileName: String,
        val board: BoardState,
    )

    private val game1857766Puzzles = listOf(
        PuzzleSpec("TD1", "puzzle-fumbbl-1857766-td1.json", td1Board),
        PuzzleSpec("TD2", "puzzle-fumbbl-1857766-td2.json", td2Board),
        PuzzleSpec("TD3", "puzzle-fumbbl-1857766-td3.json", td3Board),
    )

    private val game1871869Td1Board = BoardState(
        ballX = 21, ballY = 11, ballCarrierId = "17298117",
        players = listOf(
            standing("17292256", 20, 9),
            standing("17292257", 22, 12),
            standing("17292258", 19, 9),
            standing("17292259", 19, 12),
            standing("17292260", 22, 11),
            standing("17292261", 19, 11),
            standing("17292262", 20, 7),
            standing("17292263", 18, 5),
            standing("17292264", 21, 9),
            standing("17292266", 16, 7),
            standing("17292267", 16, 11),
            standing("17298116", 19, 10),
            standing("17298117", 21, 11),
            prone("17298118", 21, 10),
            prone("17298119", 11, 10),
            standing("17298120", 16, 4),
            standing("17298121", 20, 13),
            prone("17298122", 17, 5),
            prone("17298123", 12, 6),
            standing("17298124", 15, 10),
            prone("17298127", 15, 5),
        )
    )

    private val game1871869Td2Board = BoardState(
        ballX = 6, ballY = 1, ballCarrierId = "17292256",
        players = listOf(
            standing("17292256", 6, 1),
            standing("17292257", 13, 10),
            standing("17292258", 11, 5),
            standing("17292259", 10, 3),
            standing("17292260", 12, 8),
            prone("17292261", 13, 13),
            standing("17292262", 10, 8),
            standing("17292264", 8, 2),
            standing("17292265", 5, 3),
            standing("17292266", 12, 4),
            prone("17292267", 10, 7),
            standing("17298116", 10, 4),
            standing("17298117", 9, 4),
            standing("17298119", 11, 9),
            prone("17298120", 8, 6),
            standing("17298121", 10, 6),
            standing("17298122", 11, 13),
            standing("17298123", 12, 13),
            standing("17298125", 6, 4),
            standing("17298127", 11, 10),
        )
    )

    private val game1871869Td3Board = BoardState(
        ballX = 21, ballY = 4, ballCarrierId = "17298117",
        players = listOf(
            standing("17292256", 7, 6),
            standing("17292257", 21, 9),
            standing("17292258", 18, 12),
            standing("17292259", 12, 6),
            standing("17292260", 14, 10),
            standing("17292261", 22, 4),
            standing("17292263", 19, 5),
            standing("17292264", 21, 3),
            standing("17292266", 18, 11),
            standing("17292267", 13, 11),
            standing("17298116", 15, 10),
            standing("17298117", 21, 4),
            prone("17298120", 12, 5),
            prone("17298121", 19, 12),
            standing("17298122", 18, 10),
            standing("17298123", 13, 12),
            standing("17298124", 12, 9),
            standing("17298125", 20, 10),
            standing("17298126", 17, 12),
        )
    )

    private val game1871869Puzzles = listOf(
        PuzzleSpec("TD1", "puzzle-fumbbl-1871869-td1.json", game1871869Td1Board),
        PuzzleSpec("TD2", "puzzle-fumbbl-1871869-td2.json", game1871869Td2Board),
        PuzzleSpec("TD3", "puzzle-fumbbl-1871869-td3.json", game1871869Td3Board),
    )

    private val game1857619Td1Board = BoardState(
        ballX = 0, ballY = 12, ballCarrierId = null,
        players = listOf(
            standing("17291907", 14, 9),
            standing("17291908", 13, 9),
            standing("17291906", 15, 9),
            standing("17291912", 13, 4),
            standing("17291909", 13, 10),
            standing("17291910", 13, 7),
            standing("17291915", 13, 1),
            standing("17291911", 12, 4),
            prone("17291914", 11, 10),
            standing("17291905", 14, 11),
            prone("17291903", 5, 10),
            standing("17419721", 20, 11),
            standing("17419711", 14, 10),
            prone("17419714", 15, 10),
            prone("17419712", 14, 7),
            standing("17419715", 12, 11),
            prone("17419717", 14, 6),
            standing("17419720", 0, 12),
            prone("17419710", 14, 1),
            prone("17419716", 14, 4),
            standing("17419718", 13, 13),
        )
    )

    private val game1857619Puzzles = listOf(
        PuzzleSpec("TD1", "puzzle-fumbbl-1857619-td1.json", game1857619Td1Board),
    )

    @Test
    fun generatePuzzlesFromGame1857766() {
        generatePuzzles(game1857766Puzzles)
    }

    @Test
    fun generatePuzzlesFromGame1871869() {
        generatePuzzles(game1871869Puzzles)
    }

    @Test
    fun generatePuzzlesFromGame1857619() {
        generatePuzzles(game1857619Puzzles)
    }

    private fun generatePuzzles(puzzles: List<PuzzleSpec>) {
        val outDir = System.getProperty("PUZZLE_OUT_DIR")?.ifBlank { null } ?: return
        val replayPath = System.getProperty("FUMBBL_REPLAY")?.ifBlank { null } ?: return

        println("[FumbblPuzzle] outDir=$outDir, replayPath=$replayPath")

        val json = Json {
            prettyPrint = false
            ignoreUnknownKeys = true
            isLenient = true
        }

        val fileContent = File(replayPath).readText().trim()
        val firstElement = if (fileContent.startsWith("[")) {
            json.parseToJsonElement(fileContent).jsonArray[0]
        } else {
            json.parseToJsonElement(fileContent.lines().first())
        }
        val gameState = json.decodeFromJsonElement(ServerCommandGameState.serializer(), firstElement.jsonObject).game

        val rules = StandardBB2020Rules().update {
            allowPlayerEditsDuringGame = true
            prayersToNuffleEnabled = false
        }
        val baseGame = Game.fromFumbblState(rules, gameState)
        val homeTeam = baseGame.homeTeam
        val awayTeam = baseGame.awayTeam

        println("[FumbblPuzzle] Game created. Home: ${homeTeam.name} (${homeTeam.size} players), Away: ${awayTeam.name} (${awayTeam.size} players)")

        val homeSetupActions: List<GameAction> = homeTeam.toList().take(11).flatMapIndexed { i, player ->
            val x = when { i < 3 -> 12; i < 7 -> 11; else -> 10 }
            val y = when (i) {
                0 -> 5; 1 -> 7; 2 -> 9
                3 -> 1; 4 -> 3; 5 -> 11; 6 -> 13
                else -> (i - 7) * 2 + 2
            }
            listOf(PlayerSelected(player.id), PitchSquareSelected(x, y))
        } + EndSetup

        val awaySetupActions: List<GameAction> = awayTeam.toList().take(11).flatMapIndexed { i, player ->
            val x = when { i < 3 -> 13; i < 7 -> 14; else -> 15 }
            val y = when (i) {
                0 -> 5; 1 -> 7; 2 -> 9
                3 -> 1; 4 -> 3; 5 -> 11; 6 -> 13
                else -> (i - 7) * 2 + 2
            }
            listOf(PlayerSelected(player.id), PitchSquareSelected(x, y))
        } + EndSetup

        val pregameActions: List<GameAction> = buildList {
            addAll(listOf(D3Result(1), D3Result(2)))
            add(DiceRollResults(D6Result(3), D6Result(4)))
            addAll(listOf(CoinSideSelected(Coin.HEAD), CoinTossResult(Coin.HEAD), Cancel))
            addAll(homeSetupActions)
            addAll(awaySetupActions)
            add(PlayerSelected(homeTeam.toList().first().id))
            add(PitchSquareSelected(19, 7))
            add(DiceRollResults(D8Result(4), D6Result(1)))
            add(DiceRollResults(D6Result(3), D6Result(4)))
            add(D6Result(1))
            add(D6Result(1))
            add(D8Result(4))
        }

        for (puzzle in puzzles) {
            val game = Game(rules, baseGame.homeTeam, baseGame.awayTeam, Pitch.createForRuleset(rules))
            val controller = GameEngineController(game, cacheActionDescriptor = false, validateActions = false)
            controller.startManualMode(logAvailableActions = false)

            pregameActions.forEach { controller.handleAction(it) }

            for (p in puzzle.board.players) {
                val playerId = PlayerId(p.playerId)
                controller.handleAction(SetPlayerPosition(playerId, p.x, p.y))
                controller.handleAction(SetPlayerStateAction(playerId, p.state, p.hasTackleZones))
            }

            val board = puzzle.board
            if (board.ballX != null && board.ballY != null) {
                val carrierId = board.ballCarrierId?.let { PlayerId(it) }
                controller.handleAction(SetBallPosition(board.ballX, board.ballY, carrierId))
            }

            val jsonStr = JervisSerialization.serializeGameStateToJson(controller, includeDebugInformation = false)
            val outFile = File(outDir, puzzle.fileName)
            outFile.parentFile?.mkdirs()
            outFile.writeText(jsonStr)
            println("[FumbblPuzzle] ${puzzle.label}: → ${outFile.absolutePath} (${jsonStr.length} chars)")
        }

        println("[FumbblPuzzle] All ${puzzles.size} puzzles generated.")
    }
}
