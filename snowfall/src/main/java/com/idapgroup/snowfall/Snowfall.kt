package com.idapgroup.snowfall

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import com.idapgroup.snowfall.types.AnimType
import com.idapgroup.snowfall.types.FlakeType
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.nanoseconds

/**
 * Creates flakes falling animation base on `FlakeType` param.
 * @param type - type of flake.
 */
fun Modifier.snowfall(type: FlakeType = FlakeType.Snowflakes) = letItSnow(type, AnimType.Falling)

/**
 * Creates flakes melting animation base on `FlakeType` param.
 * @param type - type of flake.
 */
fun Modifier.snowmelt(type: FlakeType = FlakeType.Snowflakes) = letItSnow(type, AnimType.Melting)


private fun Modifier.letItSnow(
    flakeType: FlakeType,
    animType: AnimType,
)  = composed {
    val flakes = when (flakeType) {
        is FlakeType.Custom -> flakeType.data
        is FlakeType.Snowflakes -> getDefaultFlakes()
    }
    var snowAnimState by remember {
        mutableStateOf(
            SnowAnimState(
                tick = -1,
                canvasSize = IntSize(0, 0),
                painters = flakes,
                animType = animType
            )
        )
    }
    LaunchedEffect(Unit) {
        while (isActive) {
            withFrameNanos { frameTimeNanos ->
                val elapsedMillis =
                    (frameTimeNanos - snowAnimState.tickNanos).nanoseconds.inWholeMilliseconds
                val isFirstRun = snowAnimState.tickNanos < 0
                snowAnimState.tickNanos = frameTimeNanos

                if (isFirstRun) return@withFrameNanos
                snowAnimState.snowflakes.forEach { it.update(elapsedMillis) }
            }
        }
    }
    onSizeChanged { newSize -> snowAnimState = snowAnimState.resize(newSize) }
        .clipToBounds()
        .drawWithContent {
            drawContent()
            snowAnimState.draw(this)
        }
}

@Composable
private fun getDefaultFlakes(): List<Painter> = listOf(
    painterResource(id = R.drawable.ic_flake_1),
    painterResource(id = R.drawable.ic_flake_2),
    painterResource(id = R.drawable.ic_flake_3),
    painterResource(id = R.drawable.ic_flake_4),
    painterResource(id = R.drawable.ic_flake_5),
    painterResource(id = R.drawable.ic_flake_6),
    painterResource(id = R.drawable.ic_flake_7),
    painterResource(id = R.drawable.ic_flake_8),
    painterResource(id = R.drawable.ic_flake_9),
    painterResource(id = R.drawable.ic_flake_10),
)