package me.knighthat.button

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp

open class Button(
    val iconId: Int,
    val color: Color,
    val padding: Dp,
    val size: Dp,
    var modifier: Modifier = Modifier
) {

    fun clickEvent( onClick: () -> Unit ) {
        modifier = modifier.clickable { onClick() }
    }

    @Composable
    open fun Draw() {
        Image(
            painter = painterResource( iconId ),
            contentDescription = null,
            colorFilter = ColorFilter.tint( color ),
            modifier = modifier
                .padding( all = padding )
                .size( size )
        )
    }
}