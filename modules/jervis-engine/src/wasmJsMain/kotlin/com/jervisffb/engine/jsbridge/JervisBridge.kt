package com.jervisffb.engine.jsbridge

import com.jervisffb.BuildConfig
import com.jervisffb.engine.ActionRequest
import com.jervisffb.engine.GameEngineController
import com.jervisffb.engine.actions.BlockTypeSelected
import com.jervisffb.engine.actions.Cancel
import com.jervisffb.engine.actions.CancelWhenReady
import com.jervisffb.engine.actions.CoinSideSelected
import com.jervisffb.engine.actions.CoinTossResult
import com.jervisffb.engine.actions.Confirm
import com.jervisffb.engine.actions.ConfirmWhenReady
import com.jervisffb.engine.actions.Continue
import com.jervisffb.engine.actions.ContinueWhenReady
import com.jervisffb.engine.actions.D2Result
import com.jervisffb.engine.actions.D3Result
import com.jervisffb.engine.actions.D4Result
import com.jervisffb.engine.actions.D6Result
import com.jervisffb.engine.actions.D8Result
import com.jervisffb.engine.actions.D12Result
import com.jervisffb.engine.actions.D16Result
import com.jervisffb.engine.actions.D20Result
import com.jervisffb.engine.actions.DBlockResult
import com.jervisffb.engine.actions.DieResult
import com.jervisffb.engine.actions.DiceRollResults
import com.jervisffb.engine.actions.DirectionSelected
import com.jervisffb.engine.actions.EndAction
import com.jervisffb.engine.actions.EndActionWhenReady
import com.jervisffb.engine.actions.EndSetup
import com.jervisffb.engine.actions.EndSetupWhenReady
import com.jervisffb.engine.actions.EndTurn
import com.jervisffb.engine.actions.EndTurnWhenReady
import com.jervisffb.engine.actions.PitchSquareSelected
import com.jervisffb.engine.actions.ForegoActivationSelected
import com.jervisffb.engine.actions.GameAction
import com.jervisffb.engine.actions.InducementSelected
import com.jervisffb.engine.actions.MoveType
import com.jervisffb.engine.actions.MoveTypeSelected
import com.jervisffb.engine.actions.BlockDicePool
import com.jervisffb.engine.actions.DicePoolChoice
import com.jervisffb.engine.actions.DicePoolResultsSelected
import com.jervisffb.engine.actions.NoRerollSelected
import com.jervisffb.engine.actions.PassTypeSelected
import com.jervisffb.engine.actions.PlayerActionSelected
import com.jervisffb.engine.actions.PlayerDeselected
import com.jervisffb.engine.actions.PlayerSelected
import com.jervisffb.engine.actions.PlayersSelected
import com.jervisffb.engine.actions.RandomPlayersSelected
import com.jervisffb.engine.actions.SelectDicePoolResult
import com.jervisffb.engine.actions.SelectDirection
import com.jervisffb.engine.actions.SelectPitchLocation
import com.jervisffb.engine.actions.SelectMoveType
import com.jervisffb.engine.actions.SelectNoReroll
import com.jervisffb.engine.actions.SelectPlayer
import com.jervisffb.engine.actions.SelectPlayerAction
import com.jervisffb.engine.actions.RerollOptionSelected
import com.jervisffb.engine.actions.SelectRerollOption
import com.jervisffb.engine.actions.SelectSkill
import com.jervisffb.engine.actions.SkillSelected
import com.jervisffb.engine.actions.Undo
import com.jervisffb.engine.model.Coin
import com.jervisffb.engine.model.DicePoolId
import com.jervisffb.engine.model.DieId
import com.jervisffb.engine.model.Direction
import com.jervisffb.engine.model.PlayerId
import com.jervisffb.engine.model.PlayerState
import com.jervisffb.engine.model.SkillId
import com.jervisffb.engine.model.locations.PitchCoordinate
import com.jervisffb.engine.rules.common.actions.BlockType
import com.jervisffb.engine.rules.common.actions.PassType
import com.jervisffb.engine.rules.common.actions.PlayerStandardActionType
import com.jervisffb.engine.rules.common.rerolls.DiceRerollOption
import com.jervisffb.engine.serialize.JervisSerialization
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Kotlin/WASM ↔ JavaScript facade over a single [GameEngineController].
 *
 * This file is the only `@JsExport` surface of the jervis-engine module and is
 * intended to be consumed from any browser-hosted Jervis UI. It hides
 * Kotlin/kotlinx-serialization specifics behind a JSON-in / JSON-out API so
 * JavaScript callers never have to reach across the WASM boundary for a
 * Kotlin type.
 *
 * ## Lifecycle
 *
 * The bridge owns one [GameEngineController] at module scope. Call
 * [loadFromFileContent] with a full Jervis game-file JSON (the same format
 * [JervisSerialization.serializeGameStateToJson] produces) to replace the
 * current controller. Subsequent calls to [getSnapshot], [getAvailableActions],
 * and [handleAction] operate on that controller. [dispose] clears the
 * controller; [loadFromFileContent] implicitly disposes any previous one.
 *
 * ## Error envelope
 *
 * Every function returns a JSON string. Failures are reported as
 * `{"error": "<code>", "detail": "<message>"}`. Callers MUST check for the
 * `error` property before using the result.
 */

/**
 * Entry point required by `wasmJs { binaries.executable() }`. This module is
 * a library consumed from JS — nothing happens at startup.
 */
fun main() {}

private var controller: GameEngineController? = null

private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    serializersModule = JervisSerialization.jervisEngineModule
    useArrayPolymorphism = true
}

/**
 * Loads a Jervis game-file JSON produced by
 * [JervisSerialization.serializeGameStateToJson] and positions the controller
 * at the state the file describes. Replaces any previously loaded controller.
 *
 * Returns the resulting [Snapshot] as JSON, or an error envelope.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
fun loadFromFileContent(gameFileJson: String): String {
    return runCatching {
        val data = JervisSerialization.loadFromFileContent(gameFileJson).getOrElse {
            return@runCatching errorJson("load_failed", it.message ?: it::class.simpleName ?: "unknown")
        }
        val c = data.game
        c.startManualMode(logAvailableActions = false)
        controller = c
        json.encodeToString(Snapshot.serializer(), snapshotOf(c))
    }.getOrElse { errorJson("load_failed", it.message ?: it::class.simpleName ?: "unknown") }
}

/** Returns a JSON [Snapshot] of the current controller state. */
@OptIn(ExperimentalJsExport::class)
@JsExport
fun getSnapshot(): String {
    val c = controller ?: return errorJson("not_initialized", "")
    return runCatching {
        json.encodeToString(Snapshot.serializer(), snapshotOf(c))
    }.getOrElse { errorJson("snapshot_failed", it.message ?: "") }
}

/** Returns the current [AvailableActions] (what the engine is waiting on) as JSON. */
@OptIn(ExperimentalJsExport::class)
@JsExport
fun getAvailableActions(): String {
    val c = controller ?: return errorJson("not_initialized", "")
    return runCatching {
        json.encodeToString(AvailableActions.serializer(), availableActionsOf(c.getAvailableActions()))
    }.getOrElse { errorJson("available_actions_failed", it.message ?: "") }
}

/**
 * Applies one declared [GameAction] and returns the updated [Snapshot].
 *
 * The wire format is a JS-ergonomic content-discriminated object:
 * `{"type": "PlayerSelected", "playerId": "H1"}`. See [ActionDto] for the full
 * field list and [toGameAction] for the set of supported `type` values.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
fun handleAction(actionJson: String): String {
    val c = controller ?: return errorJson("not_initialized", "")
    return runCatching {
        val dto = json.decodeFromString(ActionDto.serializer(), actionJson)
        val action = toGameAction(dto)
        c.handleAction(action)
        json.encodeToString(Snapshot.serializer(), snapshotOf(c))
    }.getOrElse { errorJson("action_failed", it.message ?: it::class.simpleName ?: "unknown") }
}

/** Drops the current controller so [loadFromFileContent] can start fresh. */
@OptIn(ExperimentalJsExport::class)
@JsExport
fun dispose() {
    controller = null
}

/**
 * Returns the compiled Jervis engine version, e.g. `"0.5.3 (a1b2c3d)"`.
 * Sourced from `BuildConfig` so it matches whatever is baked into the WASM.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
fun engineVersion(): String = "${BuildConfig.releaseVersion} (${BuildConfig.gitHash})"

// --------------------------------------------------------------------------
// Action dispatch — JS-ergonomic content-discriminated format
// --------------------------------------------------------------------------
//
// kotlinx-serialization's polymorphic support for GameAction uses FQN-based
// array polymorphism (e.g. `["com.jervisffb.engine.actions.PlayerSelected",
// {...}]`) which is tedious to construct from JavaScript. Rather than burden
// every action class with `@SerialName` annotations, the bridge accepts a
// flat discriminated object and maps it to the typed GameAction here.
//
// Adding a new action type: add one case branch below, one field to ActionDto
// if needed, and document the wire format in the top-of-file KDoc.

private fun toGameAction(dto: ActionDto): GameAction {
    return when (dto.type) {
        // Zero-arg data objects ------------------------------------------------
        "Cancel" -> Cancel
        "Confirm" -> Confirm
        "Continue" -> Continue
        "EndAction" -> EndAction
        "EndSetup" -> EndSetup
        "EndTurn" -> EndTurn
        "Undo" -> Undo

        // Player selections ---------------------------------------------------
        "PlayerSelected" -> PlayerSelected(PlayerId(dto.playerId ?: required("playerId", dto.type)))
        "PlayerDeselected" -> PlayerDeselected(PlayerId(dto.playerId ?: required("playerId", dto.type)))
        "ForegoActivationSelected" -> ForegoActivationSelected(
            PlayerId(dto.playerId ?: required("playerId", dto.type))
        )
        "PlayersSelected" -> PlayersSelected(
            (dto.playerIds ?: required("playerIds", dto.type)).map { PlayerId(it) }
        )
        "RandomPlayersSelected" -> RandomPlayersSelected(
            (dto.playerIds ?: required("playerIds", dto.type)).map { PlayerId(it) }
        )

        // Spatial -------------------------------------------------------------
        "PitchSquareSelected" -> PitchSquareSelected(
            dto.x ?: required("x", dto.type),
            dto.y ?: required("y", dto.type),
        )
        "DirectionSelected" -> DirectionSelected(
            Direction(
                dto.x ?: required("x", dto.type),
                dto.y ?: required("y", dto.type),
            )
        )

        // Typed selections ----------------------------------------------------
        "CoinSideSelected" -> CoinSideSelected(Coin.valueOf(dto.coin ?: required("coin", dto.type)))
        "CoinTossResult" -> CoinTossResult(Coin.valueOf(dto.coin ?: required("coin", dto.type)))
        "PlayerActionSelected" -> PlayerActionSelected(
            PlayerStandardActionType.valueOf(dto.actionType ?: required("actionType", dto.type))
        )
        "MoveTypeSelected" -> MoveTypeSelected(MoveType.valueOf(dto.moveType ?: required("moveType", dto.type)))
        "BlockTypeSelected" -> BlockTypeSelected(BlockType.valueOf(dto.blockType ?: required("blockType", dto.type)))
        "PassTypeSelected" -> PassTypeSelected(PassType.valueOf(dto.passType ?: required("passType", dto.type)))
        "InducementSelected" -> InducementSelected(dto.name ?: required("name", dto.type))

        // Rerolls -------------------------------------------------------------
        "NoRerollSelected" -> NoRerollSelected(dto.dicePoolId ?: 0)

        // Dice results --------------------------------------------------------
        "D2Result" -> D2Result(dto.value ?: required("value", dto.type))
        "D3Result" -> D3Result(dto.value ?: required("value", dto.type))
        "D4Result" -> D4Result(dto.value ?: required("value", dto.type))
        "D6Result" -> D6Result(dto.value ?: required("value", dto.type))
        "D8Result" -> D8Result(dto.value ?: required("value", dto.type))
        "D12Result" -> D12Result(dto.value ?: required("value", dto.type))
        "D16Result" -> D16Result(dto.value ?: required("value", dto.type))
        "D20Result" -> D20Result(dto.value ?: required("value", dto.type))
        "DiceRollResults" -> DiceRollResults(
            (dto.rolls ?: required("rolls", dto.type)).map(::toDieResult)
        )
        "DicePoolResultsSelected" -> DicePoolResultsSelected(
            (dto.pools ?: required("pools", dto.type)).map { pool ->
                DicePoolChoice(
                    DicePoolId(pool.poolId),
                    pool.dice.map { die ->
                        DicePoolChoice.SelectedDiceRoll(DieId(die.dieId), toDieResult(die))
                    }
                )
            }
        )

        // Reroll / Skill --------------------------------------------------------
        "RerollOptionSelected" -> RerollOptionSelected(
            json.decodeFromString(
                DiceRerollOption.serializer(),
                dto.rerollOptionJson ?: required("rerollOptionJson", dto.type)
            ),
            dto.dicePoolId ?: 0,
        )
        "SkillSelected" -> SkillSelected(
            json.decodeFromString(
                SkillId.serializer(),
                dto.skillJson ?: required("skillJson", dto.type)
            ),
        )

        else -> error("Unsupported action type: ${dto.type}")
    }
}

private fun toDieResult(dto: DicePoolDieChoiceDto): DieResult = toDieResult(dto.die, dto.value)

private fun toDieResult(dto: DieResultDto): DieResult = toDieResult(dto.die, dto.value)

private fun toDieResult(die: String, value: Int): DieResult = when (die) {
    "D2" -> D2Result(value)
    "D3" -> D3Result(value)
    "D4" -> D4Result(value)
    "D6" -> D6Result(value)
    "D8" -> D8Result(value)
    "D12" -> D12Result(value)
    "D16" -> D16Result(value)
    "D20" -> D20Result(value)
    "BLOCK" -> DBlockResult(value)
    else -> error("Unsupported die type: $die")
}

private fun required(field: String, type: String): Nothing =
    error("Field '$field' is required for action type '$type'")

private fun dieName(result: DieResult): String = when (result) {
    is D2Result -> "D2"
    is D3Result -> "D3"
    is D4Result -> "D4"
    is D6Result -> "D6"
    is D8Result -> "D8"
    is D12Result -> "D12"
    is D16Result -> "D16"
    is D20Result -> "D20"
    is DBlockResult -> "BLOCK"
    else -> "D6"
}

// --------------------------------------------------------------------------
// Snapshot / AvailableActions construction
// --------------------------------------------------------------------------

private fun errorJson(code: String, detail: String): String =
    json.encodeToString(ErrorResult.serializer(), ErrorResult(code, detail))

private fun snapshotOf(c: GameEngineController): Snapshot {
    val g = c.state
    val players = buildList {
        (g.homeTeam + g.awayTeam).forEach { p ->
            val loc = p.location
            val coord = if (loc is PitchCoordinate) loc else null
            add(
                PlayerDto(
                    id = p.id.value,
                    teamId = p.team.id.value,
                    number = p.number.value,
                    name = p.name,
                    x = coord?.x ?: -1,
                    y = coord?.y ?: -1,
                    onField = coord != null,
                    state = p.state.name,
                    movesLeft = p.movesLeft,
                    hasBall = p.hasBall(),
                    hasTackleZones = p.hasTackleZones && p.state == PlayerState.STANDING,
                    positionTitle = p.position.titleSingular,
                    positionShortHand = p.position.shortHand,
                    rosterName = p.team.roster.name,
                    stats = StatsDto(
                        ma = p.move,
                        st = p.strength,
                        ag = p.agility,
                        pa = p.passing,
                        av = p.armorValue,
                    ),
                    skills = p.skills.map { it.type.description },
                )
            )
        }
    }
    val ball = g.balls.firstOrNull()?.let { b ->
        val loc = b.resolvedLocation()
        BallDto(
            x = loc.x,
            y = loc.y,
            state = b.state.name,
            carriedByPlayerId = b.carriedBy?.id?.value,
        )
    }
    val activeTurn = g.activeTeam?.turnMarker ?: 0
    return Snapshot(
        halfNo = g.halfNo,
        driveNo = g.driveNo,
        turnNo = activeTurn,
        weather = g.weather.name,
        homeScore = g.homeScore,
        awayScore = g.awayScore,
        activeTeamId = g.activeTeam?.id?.value,
        homeTeamId = g.homeTeam.id.value,
        awayTeamId = g.awayTeam.id.value,
        players = players,
        ball = ball,
    )
}

private fun availableActionsOf(req: ActionRequest): AvailableActions {
    val dtos = req.actions.map { desc ->
        when (desc) {
            is SelectPlayer -> DescriptorDto(
                kind = "SelectPlayer",
                playerIds = desc.players.map { it.value },
            )
            is SelectPitchLocation -> DescriptorDto(
                kind = "SelectPitchLocation",
                squares = desc.squares.map {
                    SquareDto(
                        x = it.x,
                        y = it.y,
                        type = it.type.name,
                        requiresRush = it.requiresRush,
                        requiresDodge = it.requiresDodge,
                        requiresJump = it.requiresJump,
                    )
                },
            )
            is SelectPlayerAction -> DescriptorDto(
                kind = "SelectPlayerAction",
                actionTypes = desc.actions.map { it.type.toString() },
            )
            is SelectMoveType -> DescriptorDto(
                kind = "SelectMoveType",
                moveTypes = desc.types.map { it.name },
            )
            EndActionWhenReady -> DescriptorDto(kind = "EndActionWhenReady")
            EndTurnWhenReady -> DescriptorDto(kind = "EndTurnWhenReady")
            EndSetupWhenReady -> DescriptorDto(kind = "EndSetupWhenReady")
            ConfirmWhenReady -> DescriptorDto(kind = "ConfirmWhenReady")
            CancelWhenReady -> DescriptorDto(kind = "CancelWhenReady")
            ContinueWhenReady -> DescriptorDto(kind = "ContinueWhenReady")
            is SelectNoReroll -> DescriptorDto(kind = "SelectNoReroll", dicePoolId = desc.dicePoolId)
            is SelectDicePoolResult -> DescriptorDto(
                kind = "SelectDicePoolResult",
                pools = desc.pools.map { pool ->
                    DicePoolDto(
                        poolId = pool.id.value,
                        selectDice = pool.selectDice,
                        dice = pool.dice.map { die ->
                            DicePoolDieDto(dieId = die.id.id, die = dieName(die.result), value = die.result.value)
                        }
                    )
                }
            )
            is SelectDirection -> {
                val origin = desc.origin as? PitchCoordinate
                DescriptorDto(
                    kind = "SelectDirection",
                    squares = desc.directions.mapNotNull { dir ->
                        origin?.move(dir, 1)?.let { target ->
                            SquareDto(x = target.x, y = target.y, type = "DIRECTION")
                        }
                    },
                    originX = origin?.x,
                    originY = origin?.y,
                )
            }
            is SelectRerollOption -> DescriptorDto(
                kind = "SelectRerollOption",
                dicePoolId = desc.dicePoolId,
                rerollOptions = desc.options.map {
                    json.encodeToString(DiceRerollOption.serializer(), it)
                },
            )
            is SelectSkill -> DescriptorDto(
                kind = "SelectSkill",
                skills = desc.skills.map {
                    json.encodeToString(SkillId.serializer(), it)
                },
            )
            else -> DescriptorDto(kind = "Unsupported:${desc::class.simpleName ?: "?"}")
        }
    }
    return AvailableActions(teamId = req.team?.id?.value, descriptors = dtos)
}

// --------------------------------------------------------------------------
// DTOs
// --------------------------------------------------------------------------

@Serializable
internal data class ErrorResult(val error: String, val detail: String)

@Serializable
internal data class ActionDto(
    val type: String,
    val playerId: String? = null,
    val playerIds: List<String>? = null,
    val x: Int? = null,
    val y: Int? = null,
    val actionType: String? = null,
    val moveType: String? = null,
    val blockType: String? = null,
    val passType: String? = null,
    val coin: String? = null,
    val name: String? = null,
    val dicePoolId: Int? = null,
    val value: Int? = null,
    val rolls: List<DieResultDto>? = null,
    val pools: List<DicePoolChoiceDto>? = null,
    val rerollOptionJson: String? = null,
    val skillJson: String? = null,
)

@Serializable
internal data class DieResultDto(val die: String, val value: Int)

@Serializable
internal data class DicePoolChoiceDto(val poolId: Int = 0, val dice: List<DicePoolDieChoiceDto>)

@Serializable
internal data class DicePoolDieChoiceDto(val dieId: String, val die: String, val value: Int)

@Serializable
internal data class DicePoolDto(val poolId: Int, val selectDice: Int, val dice: List<DicePoolDieDto>)

@Serializable
internal data class DicePoolDieDto(val dieId: String, val die: String, val value: Int)

@Serializable
internal data class Snapshot(
    val halfNo: Int,
    val driveNo: Int,
    val turnNo: Int,
    val weather: String,
    val homeScore: Int,
    val awayScore: Int,
    val activeTeamId: String?,
    val homeTeamId: String,
    val awayTeamId: String,
    val players: List<PlayerDto>,
    val ball: BallDto?,
)

@Serializable
internal data class PlayerDto(
    val id: String,
    val teamId: String,
    val number: Int,
    val name: String,
    val x: Int,
    val y: Int,
    val onField: Boolean,
    val state: String,
    val movesLeft: Int,
    val hasBall: Boolean,
    val hasTackleZones: Boolean,
    val positionTitle: String,
    val positionShortHand: String,
    val rosterName: String,
    val stats: StatsDto,
    val skills: List<String>,
)

@Serializable
internal data class StatsDto(
    val ma: Int,
    val st: Int,
    val ag: Int,
    val pa: Int?,
    val av: Int,
)

@Serializable
internal data class BallDto(
    val x: Int,
    val y: Int,
    val state: String,
    val carriedByPlayerId: String?,
)

@Serializable
internal data class AvailableActions(
    val teamId: String?,
    val descriptors: List<DescriptorDto>,
)

@Serializable
internal data class DescriptorDto(
    val kind: String,
    val playerIds: List<String>? = null,
    val squares: List<SquareDto>? = null,
    val moveTypes: List<String>? = null,
    val actionTypes: List<String>? = null,
    val dicePoolId: Int? = null,
    val originX: Int? = null,
    val originY: Int? = null,
    val pools: List<DicePoolDto>? = null,
    val rerollOptions: List<String>? = null,
    val skills: List<String>? = null,
)

@Serializable
internal data class SquareDto(
    val x: Int,
    val y: Int,
    val type: String,
    val requiresRush: Boolean = false,
    val requiresDodge: Boolean = false,
    val requiresJump: Boolean = false,
)
