package com.jervisffb.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.jervisffb.shared.generated.resources.Res
import com.jervisffb.shared.generated.resources.trump_town_pro
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font
import org.jetbrains.skia.Data
import org.jetbrains.skia.FontMgr
import org.jetbrains.skia.Typeface

@Composable
fun TrumpTownPro() = FontFamily(
    Font(Res.font.trump_town_pro, weight = FontWeight.Normal)
)

// TODO Find a better way to cache this
var cachedFont: Typeface? = null
suspend fun loadTrumpTownSkiaFont(): Typeface {
    cachedFont?.let { return it }
    val bytes = Res.readBytes("font/trump_town_pro.otf")
    val data = Data.makeFromBytes(bytes)
    return FontMgr.default.makeFromData(data)?.also {
        cachedFont = it
    } ?: error("Could not parse font bytes")
}
