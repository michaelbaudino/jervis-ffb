package com.jervisffb.test.bb2025.skills

import com.jervisffb.engine.actions.PlayerSelected
import com.jervisffb.engine.commands.SetBallState
import com.jervisffb.engine.ext.d6
import com.jervisffb.engine.ext.playerId
import com.jervisffb.engine.rules.bb2025.skills.SecretWeapon
import com.jervisffb.engine.rules.common.procedures.SetupTeam
import com.jervisffb.engine.rules.common.skills.SkillType
import com.jervisffb.test.JervisGameBB2025Test
import com.jervisffb.test.argueTheCall
import com.jervisffb.test.defaultKickOffHomeTeam
import com.jervisffb.test.defaultPregame
import com.jervisffb.test.defaultSetup
import com.jervisffb.test.ext.rollForward
import com.jervisffb.test.skipTurns
import com.jervisffb.test.utils.assertBanned
import com.jervisffb.test.utils.assertReserves
import com.jervisffb.test.utils.assertStanding
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Class testing usage of the [SecretWeapon] skill.
 */
class SecretWeaponTests: JervisGameBB2025Test() {

    @BeforeTest
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun doNotTriggerIfNotOnPitchDuringSetup() {
        val player = homeTeam["H12".playerId]
        player.addSkill(SkillType.SECRET_WEAPON)
        controller.rollForward(
            *defaultPregame(),
            *defaultSetup(),
            *defaultKickOffHomeTeam(),
        )
        player.assertReserves()
        controller.rollForward(
            *skipTurns(16),
        )
        player.assertReserves()
        controller.rollForward(
            *defaultSetup(homeFirst = false)
        )
        player.assertReserves()
    }

    @Test
    fun triggersAtEndOfDrive() {
        val player = homeTeam["H1".playerId]
        player.addSkill(SkillType.SECRET_WEAPON)
        startDefaultGame()
        player.assertStanding()
        controller.rollForward(
            *skipTurns(16),
        )
        player.assertStanding()
        controller.rollForward(
            PlayerSelected(player.id),
            argueTheCall(false)
        )
        player.assertBanned()
    }

    @Test
    fun receivingThenKickingTeamsChooseOrder() {
        val homePlayer1 = homeTeam["H1".playerId].also { it.addSkill(SkillType.SECRET_WEAPON) }
        val homePlayer2 = homeTeam["H2".playerId].also { it.addSkill(SkillType.SECRET_WEAPON) }
        val awayPlayer1 = awayTeam["A1".playerId].also { it.addSkill(SkillType.SECRET_WEAPON) }
        val awayPlayer2 = awayTeam["A2".playerId].also { it.addSkill(SkillType.SECRET_WEAPON) }
        startDefaultGame()
        controller.rollForward(
            *skipTurns(16),
        )

        var actions = controller.getAvailableActions()
        assertEquals(awayTeam, actions.team)

        controller.rollForward(
            PlayerSelected(awayPlayer2),
            argueTheCall(false),
            PlayerSelected(awayPlayer1),
            argueTheCall(false),
        )
        awayPlayer1.assertBanned()
        awayPlayer2.assertBanned()
        homePlayer1.assertStanding()
        homePlayer2.assertStanding()

        actions = controller.getAvailableActions()
        assertEquals(homeTeam, actions.team)

        controller.rollForward(
            PlayerSelected(homePlayer1),
            argueTheCall(false),
            PlayerSelected(homePlayer2),
            argueTheCall(false),
        )

        awayPlayer1.assertBanned()
        awayPlayer2.assertBanned()
        homePlayer1.assertBanned()
        homePlayer2.assertBanned()
        assertEquals(SetupTeam.SelectPlayerOrEndSetup, controller.currentNode())
    }

    @Test
    fun cannotArgueIfCoachIsBanned() {
        val player1 = homeTeam["H1".playerId].also { it.addSkill(SkillType.SECRET_WEAPON) }
        val player2 = homeTeam["H2".playerId].also { it.addSkill(SkillType.SECRET_WEAPON) }
        startDefaultGame()
        controller.rollForward(
            *skipTurns(16),
        )
        controller.rollForward(
            PlayerSelected(player1),
            argueTheCall(true),
            1.d6,
            PlayerSelected(player2),
        )
        player1.assertBanned()
        player2.assertBanned()
        assertEquals(SetupTeam.SelectPlayerOrEndSetup, controller.currentNode())
    }

    // Verify that we correctly handle rolling for a Secret Player, even if they are holding the ball
    // at the end of a drive.
    @Test
    fun playerWithBallEndsDrive() {
        val player = homeTeam["H1".playerId]
        player.addSkill(SkillType.SECRET_WEAPON)
        startDefaultGame()
        SetBallState.carried(state.singleBall(), player).execute(state)
        controller.rollForward(
            *skipTurns(16),
        )
        player.assertStanding()
        assertTrue(player.hasBall())
        controller.rollForward(
            PlayerSelected(player.id),
            argueTheCall(true),
            4.d6,
        )
        player.assertBanned()
        assertEquals(SetupTeam.SelectPlayerOrEndSetup, controller.currentNode())
    }
}
