package com.jervisffb.test

import com.jervisffb.engine.GameEngineController
import com.jervisffb.engine.actions.CompositeGameAction
import com.jervisffb.engine.actions.Revert
import com.jervisffb.engine.actions.SetBallLocation
import com.jervisffb.engine.actions.SetPlayerLocation
import com.jervisffb.engine.actions.SetPlayerState
import com.jervisffb.engine.actions.Undo
import com.jervisffb.engine.ext.d3
import com.jervisffb.engine.model.BallState
import com.jervisffb.engine.model.PlayerId
import com.jervisffb.engine.model.PlayerIntermediateState
import com.jervisffb.engine.model.PlayerState
import com.jervisffb.engine.model.locations.PitchCoordinate
import com.jervisffb.engine.rules.Rules
import com.jervisffb.engine.rules.StandardBB2020Rules
import com.jervisffb.engine.rules.builder.UndoActionBehavior
import com.jervisffb.engine.rules.common.procedures.FanFactorRolls
import com.jervisffb.engine.rules.common.procedures.FullGame
import com.jervisffb.engine.rules.common.procedures.WeatherRoll
import com.jervisffb.engine.utils.InvalidActionException
import com.jervisffb.test.bb2020.createDefaultGameStateBB2020
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Controller tests that don't fit in a more specific category.
 */
class GameEngineControllerTests {

    private lateinit var controller: GameEngineController

    private fun createGameController(rules: Rules = StandardBB2020Rules().update {
        allowPlayerEditsDuringGame = true
    }): GameEngineController {
        val state = createDefaultGameStateBB2020(rules)
        controller = GameEngineController(state)
        controller.startTestMode(FullGame)
        return controller
    }

    private fun createGameControllerForDevMode(rules: Rules = devModeRules()): GameEngineController {
        return createGameController(rules)
    }

    private fun devModeRules(): Rules = StandardBB2020Rules().update {
        allowPlayerEditsDuringGame = true
    }

    private fun devModeRulesWithUndo(): Rules = StandardBB2020Rules().update {
        allowPlayerEditsDuringGame = true
        undoActionBehavior = UndoActionBehavior.ALLOWED
    }

    @Test
    fun undoIncrementActionId() {
        val rules = StandardBB2020Rules().update {
            undoActionBehavior = UndoActionBehavior.ALLOWED
        }
        val controller = createGameController(rules)

        // Verify that undoing actions keep incrementing the delta id
        assertEquals(0, controller.currentActionIndex().value)
        controller.handleAction(1.d3)
        assertEquals(1, controller.currentActionIndex().value)
        controller.handleAction(2.d3)
        assertEquals(2, controller.currentActionIndex().value)
        assertTrue(controller.isUndoAvailable(controller.state.awayTeam.id))
        controller.handleAction(Undo)
        assertEquals(3, controller.currentActionIndex().value)
        controller.handleAction(Undo)
        assertEquals(4, controller.currentActionIndex().value)
        controller.handleAction(2.d3)
        assertEquals(5, controller.currentActionIndex().value)
    }

    @Test
    fun cannotUndoDiceRollsIfNotInEnabled() {
        val rules = StandardBB2020Rules().update {
            undoActionBehavior = UndoActionBehavior.ONLY_NON_RANDOM_ACTIONS
        }
        val controller = createGameController(rules)

        // Verify that undoing actions keep incrementing the delta id
        assertEquals(0, controller.currentActionIndex().value)
        controller.handleAction(1.d3)
        assertEquals(1, controller.currentActionIndex().value)
        assertFalse(controller.isUndoAvailable(controller.state.homeTeam.id))
        assertFailsWith<InvalidActionException> {
            controller.handleAction(Undo)
        }
        assertEquals(1, controller.currentActionIndex().value)
    }

    @Test
    fun revertDecrementsActionId() {
        val rules = StandardBB2020Rules().update {
            undoActionBehavior = UndoActionBehavior.NOT_ALLOWED // Revert is always allowed
        }
        val controller = createGameController(rules)

        // Verify that reverting actions also decrement the action id
        assertEquals(0, controller.currentActionIndex().value)
        controller.handleAction(1.d3)
        assertEquals(1, controller.currentActionIndex().value)
        controller.handleAction(2.d3)
        assertEquals(2, controller.currentActionIndex().value)
        controller.handleAction(Revert)
        assertEquals(1, controller.currentActionIndex().value)
        controller.handleAction(Revert)
        assertEquals(0, controller.currentActionIndex().value)
    }

    @Test
    fun undoCompositeCommandsUndoAll() {
        val rules = StandardBB2020Rules().update {
            undoActionBehavior = UndoActionBehavior.ALLOWED // Revert is always allowed
        }
        val controller = createGameController(rules)
        assertEquals(FanFactorRolls.SetFanFactorForHomeTeam, controller.currentNode())
        controller.handleAction(
            CompositeGameAction(1.d3, 2.d3)
        )
        assertEquals(WeatherRoll.RollWeatherDice, controller.currentNode())
        controller.handleAction(Undo)
        assertEquals(FanFactorRolls.SetFanFactorForHomeTeam, controller.currentNode())
    }

    @Test
    fun setPlayerLocationMovesPlayerToTargetCoordinate() {
        val controller = createGameControllerForDevMode()
        val player = controller.state.homeTeam[PlayerId("H1")]

        controller.handleAction(SetPlayerLocation(PlayerId("H1"), x = 5, y = 10))

        assertEquals(PitchCoordinate(5, 10), player.location)
        val square = controller.state.pitch[PitchCoordinate(5, 10)]
        assertEquals(player, square.player)
    }

    @Test
    fun setPlayerLocationRejectsOutOfBoundsCoordinate() {
        val controller = createGameControllerForDevMode()

        assertFailsWith<InvalidActionException> {
            controller.handleAction(SetPlayerLocation(PlayerId("H1"), x = -1, y = 5))
        }
    }

    @Test
    fun setPlayerLocationUsesSetPlayerLocationCommandAndIsUndoable() {
        val controller = createGameControllerForDevMode(devModeRulesWithUndo())
        val player = controller.state.homeTeam[PlayerId("H1")]
        val originalLocation = player.location

        controller.handleAction(SetPlayerLocation(PlayerId("H1"), x = 7, y = 7))
        assertEquals(PitchCoordinate(7, 7), player.location)

        controller.handleAction(Undo)
        assertEquals(originalLocation, player.location)
    }

    @Test
    fun setPlayerStateChangesPlayerStateAndTackleZones() {
        val controller = createGameControllerForDevMode()
        val player = controller.state.homeTeam[PlayerId("H1")]

        controller.handleAction(SetPlayerState(PlayerId("H1"), PlayerState.PRONE, hasTackleZones = false))

        assertEquals(PlayerState.PRONE, player.state)
        assertFalse(player.hasTackleZones)
    }

    @Test
    fun setPlayerStateResetsIntermediateState() {
        val controller = createGameControllerForDevMode()
        val player = controller.state.homeTeam[PlayerId("H1")]
        player.intermediateState = PlayerIntermediateState.KNOCKED_DOWN

        controller.handleAction(SetPlayerState(PlayerId("H1"), PlayerState.STANDING))

        assertEquals(PlayerState.STANDING, player.state)
        assertNull(player.intermediateState)
    }

    @Test
    fun setPlayerStateLeavesTackleZonesUnchangedWhenNull() {
        val controller = createGameControllerForDevMode()
        val player = controller.state.homeTeam[PlayerId("H1")]
        player.hasTackleZones = true

        controller.handleAction(SetPlayerState(PlayerId("H1"), PlayerState.PRONE))

        assertEquals(PlayerState.PRONE, player.state)
        assertTrue(player.hasTackleZones)
    }

    @Test
    fun setPlayerStateIsUndoable() {
        val controller = createGameControllerForDevMode(devModeRulesWithUndo())
        val player = controller.state.homeTeam[PlayerId("H1")]
        val originalState = player.state
        val originalHasTackleZones = player.hasTackleZones

        controller.handleAction(SetPlayerState(PlayerId("H1"), PlayerState.STUNNED, hasTackleZones = false))
        assertEquals(PlayerState.STUNNED, player.state)

        controller.handleAction(Undo)
        assertEquals(originalState, player.state)
        assertEquals(originalHasTackleZones, player.hasTackleZones)
    }

    @Test
    fun setBallLocationPlacesBallOnPitch() {
        val controller = createGameControllerForDevMode()
        val ball = controller.state.balls.first()

        controller.handleAction(SetBallLocation(x = 8, y = 8, ballState = BallState.ON_GROUND))

        assertEquals(PitchCoordinate(8, 8), ball.coordinates)
        assertEquals(BallState.ON_GROUND, ball.state)
        val square = controller.state.pitch[PitchCoordinate(8, 8)]
        assertTrue(square.balls.contains(ball))
    }

    @Test
    fun setBallLocationCanPlaceBallWithCarrier() {
        val controller = createGameControllerForDevMode()
        val ball = controller.state.balls.first()
        val carrierId = PlayerId("H1")

        controller.handleAction(SetPlayerLocation(carrierId, x = 5, y = 5))
        controller.handleAction(SetBallLocation(x = 5, y = 5, playerId = carrierId, ballState = BallState.CARRIED))

        assertEquals(BallState.CARRIED, ball.state)
        assertNotNull(ball.carriedBy)
        assertEquals(carrierId, ball.carriedBy?.id)
        assertEquals(PitchCoordinate(5, 5), ball.resolvedLocation())
    }

    @Test
    fun setBallLocationKeepsExistingBallStateWhenNotSpecified() {
        val controller = createGameControllerForDevMode()
        val ball = controller.state.balls.first()
        val originalState = ball.state
        val originalLocation = ball.coordinates

        controller.handleAction(SetBallLocation(x = 10, y = 5))

        assertEquals(PitchCoordinate(10, 5), ball.coordinates)
        assertEquals(originalState, ball.state)
    }

    @Test
    fun setBallLocationIsUndoable() {
        val controller = createGameControllerForDevMode(devModeRulesWithUndo())
        val ball = controller.state.balls.first()
        val originalCoordinates = ball.coordinates
        val originalState = ball.state

        controller.handleAction(SetBallLocation(x = 12, y = 3, ballState = BallState.ON_GROUND))
        assertEquals(PitchCoordinate(12, 3), ball.coordinates)
        assertEquals(BallState.ON_GROUND, ball.state)

        controller.handleAction(Undo)
        assertEquals(originalCoordinates, ball.coordinates)
        assertEquals(originalState, ball.state)
    }
}
