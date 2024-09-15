package com.skymilk.chatapp.store.presentation.screen.main.home

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skymilk.chatapp.store.presentation.screen.AuthState
import com.skymilk.chatapp.store.presentation.screen.auth.AuthViewModel
import com.skymilk.chatapp.ui.theme.Black
import com.skymilk.chatapp.ui.theme.BlueGray
import com.skymilk.chatapp.ui.theme.dimens

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val currentUser = (authState as AuthState.Authenticated).user

    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            currentUser.let { user ->
                //프로필 이미지
                user.photoUrl?.let {
                    //                    AsyncImage(
                    //                        modifier = Modifier
                    //                            .size(140.dp)
                    //                            .clip(RoundedCornerShape(4.dp)),
                    //                        model = ImageRequest.Builder(LocalContext.current)
                    //                            .data(it)
                    //                            .crossfade(true)
                    //                            .build(),
                    //                        contentDescription = null,
                    //                    )

                    Spacer(modifier = Modifier.size(16.dp))
                }

                //유저 이름
                user.displayName?.let { name ->
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.size(16.dp))
                }

                //유저 이메일
                user.email?.let { email ->
                    Text(
                        text = "Email : $email",
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.size(16.dp))
                }

                //유저 아이디
                Text(
                    text = "ID : ${currentUser.uid}",
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.size(16.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(MaterialTheme.dimens.buttonHeight),
                    onClick = {
                        onSignOut()
                        authViewModel.signOut()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSystemInDarkTheme()) BlueGray else Black,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(size = 4.dp)
                ) {
                    Text(text = "로그아웃", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}