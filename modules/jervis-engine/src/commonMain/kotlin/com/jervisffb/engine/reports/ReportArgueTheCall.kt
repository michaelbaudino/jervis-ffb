package com.jervisffb.engine.reports

import com.jervisffb.engine.rules.common.procedures.actions.foul.BeingSentOffContext

class ReportArgueTheCall(val context: BeingSentOffContext) : LogEntry() {
    override val category: LogCategory = LogCategory.GAME_PROGRESS
    override val message: String = buildString {
        append("${context.player.name} was spotted by the ref.")
    }
}
