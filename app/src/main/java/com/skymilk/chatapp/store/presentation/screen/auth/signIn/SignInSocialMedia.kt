package com.skymilk.chatapp.store.presentation.screen.auth.signIn

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.skymilk.chatapp.ui.theme.CookieRunFont
import com.skymilk.chatapp.ui.theme.LightBlueWhite
import com.skymilk.chatapp.ui.theme.dimens

@Composable
fun SignInSocialMedia(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    text: String,
    backgroundColor: Color = LightBlueWhite,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .shadow(1.dp, RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .height(MaterialTheme.dimens.buttonHeight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .padding(start = MaterialTheme.dimens.small2)
                .size(16.dp),
            painter = painterResource(id = icon),
            contentDescription = null,
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(color = Color.Black.copy(alpha = 0.85f)),
            fontFamily = CookieRunFont
        )
    }
}