package com.jervisffb.test.bb2025.skills

import com.jervisffb.engine.actions.BlockTypeSelected
import com.jervisffb.engine.actions.Cancel
import com.jervisffb.engine.actions.DiceRollResults
import com.jervisffb.engine.actions.DirectionSelected
import com.jervisffb.engine.actions.PlayerSelected
import com.jervisffb.engine.actions.RerollOptionSelected
import com.jervisffb.engine.actions.SelectDicePoolResult
import com.jervisffb.engine.actions.SelectRerollOption
import com.jervisffb.engine.ext.d6
import com.jervisffb.engine.ext.dblock
import com.jervisffb.engine.ext.playerId
import com.jervisffb.engine.model.Direction
import com.jervisffb.engine.model.context.BlockContext
import com.jervisffb.engine.model.context.getContext
import com.jervisffb.engine.model.getSkill
import com.jervisffb.engine.rules.bb2025.skills.Brawler
import com.jervisffb.engine.rules.bb2025.skills.Pro
import com.jervisffb.engine.rules.common.actions.BlockType
import com.jervisffb.engine.rules.common.actions.PlayerStandardActionType
import com.jervisffb.engine.rules.common.rerolls.TeamReroll
import com.jervisffb.engine.rules.common.skills.SkillType
import com.jervisffb.test.JervisGameBB2025Test
import com.jervisffb.test.activatePlayer
import com.jervisffb.test.ext.rollForward
import com.jervisffb.test.utils.SelectSingleBlockDieResult
import com.jervisffb.test.utils.assertActiveTeam
import com.jervisffb.test.utils.assertNoActivePlayer
import com.jervisffb.test.utils.assertProne
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Class testing usage of the [Brawler] skill.
 */
class BrawlerTests: JervisGameBB2025Test() {

    @BeforeTest
    override fun setUp() {
        super.setUp()
        startDefaultGame()
        awayTeam["A1".playerId].apply {
            addSkill(SkillType.BRAWLER)
            baseStrength = 4
            strength = 4
        }
    }

    @Test
    fun worksOnBlock() {
        val attacker = awayTeam["A1".playerId]
        val defender = homeTeam["H1".playerId]
        controller.rollForward(
            *activatePlayer(attacker, PlayerStandardActionType.BLOCK),
            PlayerSelected(defender),
            DiceRollResults(1.dblock, 2.dblock),
        )
        val rerollOptions = controller.getAvailableActions().get<SelectRerollOption>().options
        assertEquals(2, rerollOptions.size)
        assertEquals(1, rerollOptions.count { it.getRerollSource(state) is Brawler && it.dice.size == 1 })
        controller.rollForward(
            RerollOptionSelected(rerollOptions.first()),
            6.dblock
        )
        val block = state.getContext<BlockContext>()
        assertEquals(1.dblock, block.roll.first().result)
        assertEquals(6.dblock, block.roll.last().result)
        controller.rollForward(
            SelectSingleBlockDieResult(index = 1), // Select POW
            DirectionSelected(Direction.LEFT),
            Cancel, // Do not follow up
        )
        assertTrue(attacker.getSkill<Brawler>().rerollUsed)
        controller.rollForward(
            DiceRollResults(1.d6, 1.d6)
        )
        defender.assertProne()
        state.assertNoActivePlayer()
        state.assertActiveTeam(awayTeam)
    }

    @Test
    fun doesNotWorkOnBlitz() {
        val attacker = awayTeam["A1".playerId]
        val defender = homeTeam["H1".playerId]
        controller.rollForward(
            *activatePlayer(attacker, PlayerStandardActionType.BLITZ),
            PlayerSelected(defender),
            PlayerSelected(defender),
            BlockTypeSelected(BlockType.STANDARD),
            DiceRollResults(1.dblock, 2.dblock),
        )
        val rerollOptions = controller.getAvailableActions().get<SelectRerollOption>().options
        assertEquals(1, rerollOptions.size)
        assertTrue(rerollOptions.single().getRerollSource(state) is TeamReroll)
    }

    @Test
    fun onlyWorksOnBothDown() {
        val attacker = awayTeam["A1".playerId]
        val defender = homeTeam["H1".playerId]
        controller.rollForward(
            *activatePlayer(attacker, PlayerStandardActionType.BLOCK),
            PlayerSelected(defender),
            DiceRollResults(1.dblock, 6.dblock),
        )
        val rerollOptions = controller.getAvailableActions().get<SelectRerollOption>().options
        assertTrue(rerollOptions.none { it.getRerollSource(state) is Brawler })
    }

    // Cannot use Brawler, if Pro was used to reroll the other die (in a 2 dice block)
    @Test
    fun cannotUseIfOtherDieWasRerolled() {
        val attacker = awayTeam["A1".playerId]
        attacker.addSkill(SkillType.PRO)
        val defender = homeTeam["H1".playerId]
        controller.rollForward(
            *activatePlayer(attacker, PlayerStandardActionType.BLOCK),
            PlayerSelected(defender),
            DiceRollResults(2.dblock, 2.dblock),
        )
        val rerollOptions = controller.getAvailableActions().get<SelectRerollOption>().options
        assertEquals(5, rerollOptions.size)
        assertEquals(2, rerollOptions.count { it.getRerollSource(state) is Brawler })
        assertEquals(2, rerollOptions.count { it.getRerollSource(state) is Pro })
        val rerollOption = rerollOptions.first() // Select Brawler reroll
        assertTrue(rerollOption.getRerollSource(state) is Brawler)
        controller.rollForward(
            RerollOptionSelected(rerollOption),
            6.dblock // Reroll first die using Brawler, we cannot reroll other dice
        )
        val actions = controller.getAvailableActions()
        assertIs<SelectDicePoolResult>(actions.single())
        controller.rollForward(
            SelectSingleBlockDieResult(index = 0), // Select POW
            DirectionSelected(Direction.LEFT),
            Cancel, // Do not follow up
        )
        assertTrue(attacker.getSkill<Brawler>().rerollUsed)
        controller.rollForward(
            DiceRollResults(1.d6, 1.d6)
        )
        defender.assertProne()
        state.assertNoActivePlayer()
        state.assertActiveTeam(awayTeam)
    }
}
