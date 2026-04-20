package com.jervisffb.resources.bb2020

import com.jervisffb.engine.model.PlayerSize
import com.jervisffb.engine.model.PositionId
import com.jervisffb.engine.model.RosterId
import com.jervisffb.engine.rules.common.roster.RegionalSpecialRule
import com.jervisffb.engine.rules.common.roster.Roster
import com.jervisffb.engine.rules.common.roster.RosterPosition
import com.jervisffb.engine.rules.common.skills.SkillCategory.AGILITY
import com.jervisffb.engine.rules.common.skills.SkillCategory.GENERAL
import com.jervisffb.engine.rules.common.skills.SkillCategory.PASSING
import com.jervisffb.engine.rules.common.skills.SkillCategory.STRENGTH
import com.jervisffb.engine.rules.common.skills.SkillType.BLOCK
import com.jervisffb.engine.rules.common.skills.SkillType.CATCH
import com.jervisffb.engine.rules.common.skills.SkillType.DODGE
import com.jervisffb.engine.rules.common.skills.SkillType.GRAB
import com.jervisffb.engine.rules.common.skills.SkillType.LEAP
import com.jervisffb.engine.rules.common.skills.SkillType.LONER
import com.jervisffb.engine.rules.common.skills.SkillType.MIGHTY_BLOW
import com.jervisffb.engine.rules.common.skills.SkillType.PASS
import com.jervisffb.engine.rules.common.skills.SkillType.STAND_FIRM
import com.jervisffb.engine.rules.common.skills.SkillType.STRONG_ARM
import com.jervisffb.engine.rules.common.skills.SkillType.TAKE_ROOT
import com.jervisffb.engine.rules.common.skills.SkillType.THICK_SKULL
import com.jervisffb.engine.rules.common.skills.SkillType.THROW_TEAMMATE
import com.jervisffb.engine.serialize.RosterLogo
import com.jervisffb.engine.serialize.SingleSprite
import com.jervisffb.engine.serialize.SpriteSheet
import com.jervisffb.resources.iconRootPath
import com.jervisffb.resources.portraitRootPath
import kotlinx.serialization.Serializable

val WOOD_ELF_LINEMAN =
    RosterPosition(
        PositionId("wood-elf-lineman"),
        12,
        "Wood Elf Linemen",
        "Wood Elf Lineman",
        "L",
        70_000,
        7, 3, 2, 4, 8,
        emptyList(),
        listOf(AGILITY, GENERAL),
        listOf(STRENGTH),
        emptyList(),
        emptyList(),
        PlayerSize.STANDARD,
        SpriteSheet.ini("${iconRootPath}/woodelf_lineman.png", 9),
        SingleSprite.ini("${portraitRootPath}/woodelf_lineman.png")
    )

val WOOD_ELF_THROWER =
    RosterPosition(
        PositionId("wood-elf-thrower"),
        2,
        "Throwers",
        "Thrower",
        "T",
        95_000,
        7, 3, 2, 2, 8,
        listOf(PASS.id()),
        listOf(AGILITY, GENERAL, PASSING),
        listOf(STRENGTH),
        emptyList(),
        emptyList(),
        PlayerSize.STANDARD,
        SpriteSheet.ini("${iconRootPath}/woodelf_thrower.png", 2),
        SingleSprite.ini("${portraitRootPath}/woodelf_thrower.png")
    )

val WOOD_ELF_CATCHER =
    RosterPosition(
        PositionId("wood-elf-catcher"),
        4,
        "Catchers",
        "Catcher",
        "C",
        90_000,
        8, 2, 2, 4, 8,
        listOf(CATCH.id(), DODGE.id()),
        listOf(AGILITY, GENERAL),
        listOf(PASSING, STRENGTH),
        emptyList(),
        emptyList(),
        PlayerSize.STANDARD,
        SpriteSheet.ini("${iconRootPath}/woodelf_catcher.png", 4),
        SingleSprite.ini("${portraitRootPath}/woodelf_catcher.png")
    )

val WOOD_ELF_WARDANCER =
    RosterPosition(
        PositionId("wood-elf-wardancer"),
        2,
        "Wardancers",
        "Wardancer",
        "W",
        125_000,
        8, 3, 2, 4, 8,
        listOf(BLOCK.id(), DODGE.id(), LEAP.id()),
        listOf(AGILITY, GENERAL),
        listOf(PASSING, STRENGTH),
        emptyList(),
        emptyList(),
        PlayerSize.STANDARD,
        SpriteSheet.ini("${iconRootPath}/woodelf_wardancer.png", 2),
        SingleSprite.ini("${portraitRootPath}/woodelf_wardancer.png")
    )

val LOREN_FOREST_TREEMAN =
    RosterPosition(
        PositionId("wood-elf-treeman"),
        1,
        "Loren Forest Treeman",
        "Loren Forest Treeman",
        "Tr",
        120_000,
        2, 6, 5, 5, 11,
        listOf(
            LONER.id(4),
            MIGHTY_BLOW.id(1),
            STAND_FIRM.id(),
            STRONG_ARM.id(),
            TAKE_ROOT.id(),
            THICK_SKULL.id(),
            THROW_TEAMMATE.id()
        ),
        listOf(STRENGTH),
        listOf(AGILITY, GENERAL),
        emptyList(),
        emptyList(),
        PlayerSize.BIG_GUY,
        SpriteSheet.ini("${iconRootPath}/woodelf_treeman.png", 1),
        SingleSprite.ini("${portraitRootPath}/woodelf_treeman.png")
    )

@Serializable
val WOOD_ELF_TEAM_BB2020 = Roster(
    id = RosterId("jervis-wood-elf"),
    name = "Wood Elf",
    tier = 1,
    numberOfRerolls = 8,
    rerollCost = 50_000,
    allowApothecary = true,
    leagues = emptyList(),
    specialRules = listOf(RegionalSpecialRule.ELVEN_KINGDOMS_LEAGUE),
    positions = listOf(
        WOOD_ELF_LINEMAN,
        WOOD_ELF_THROWER,
        WOOD_ELF_CATCHER,
        WOOD_ELF_WARDANCER,
        LOREN_FOREST_TREEMAN,
    ),
    logo = RosterLogo.NONE
)
