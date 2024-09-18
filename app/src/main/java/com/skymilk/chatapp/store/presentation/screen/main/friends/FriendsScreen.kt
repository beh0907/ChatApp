package com.skymilk.chatapp.store.presentation.screen.main.friends

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun FriendsScreen(
    modifier: Modifier,
    viewModel: FriendsViewModel
) {

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("FriendsScreen")
    }

}