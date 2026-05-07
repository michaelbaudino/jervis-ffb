package com.jervisffb.ui.game.state.decorators

import com.jervisffb.engine.ActionRequest
import com.jervisffb.engine.actions.GameActionDescriptor
import com.jervisffb.engine.actions.SelectPitchLocation
import com.jervisffb.engine.model.Game
import com.jervisffb.engine.model.Team
import com.jervisffb.ui.game.UiGameSnapshot
import com.jervisffb.ui.game.UiSnapshotAccumulator
import com.jervisffb.ui.game.state.ManualActionProvider

/**
 * Interface responsible for setting up event handlers in the UI so it can generate
 * the available actions.
 *
 * This is done by mapping a specific [GameActionDescriptor] to a change in the
 * [UiGameSnapshot].
 *
 * E.g., a [SelectPitchLocation] descriptor should define the on-click listener
 * for the given squares.
 */
interface PitchActionDecorator<T: GameActionDescriptor> {
    fun isApplicable(state: Game, request: ActionRequest) = true
    fun decorate(
        actionProvider: ManualActionProvider,
        state: Game,
        descriptor: T,
        owner: Team?,
        acc: UiSnapshotAccumulator
    )
}
