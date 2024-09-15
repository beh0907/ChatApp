package com.skymilk.chatapp.store.presentation.screen.auth.signUp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skymilk.chatapp.store.presentation.screen.AuthState
import com.skymilk.chatapp.store.presentation.screen.auth.AuthViewModel
import com.skymilk.chatapp.store.presentation.screen.auth.components.AuthTextField
import com.skymilk.chatapp.ui.theme.Black
import com.skymilk.chatapp.ui.theme.BlueGray
import com.skymilk.chatapp.ui.theme.dimens

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToSignIn: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    // 회원가입 성공 시 홈 화면으로 이동
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            onNavigateToHome()
        }
    }

    Column(
        modifier = modifier
    ) {
        TopSection(onNavigateToSignIn)

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium2))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center // 화면 중앙에 정렬
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp)
            ) {

                SignUpSection(viewModel::signUpWithEmailAndPassword)
            }
        }
    }

}

// 상단 뒤로가기 버튼
@Composable
private fun TopSection(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(onClick = { onNavigateBack() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "",
                tint = if (isSystemInDarkTheme()) Color.White else Black
            )
        }
    }
}

//회원가입 영역
@Composable
private fun SignUpSection(onSignUpWithEmailAndPassword: (String, String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }

    AuthTextField(
        modifier = Modifier.fillMaxWidth(),
        label = "이메일",
        leadingIcon = Icons.Default.Email,
        keyboardType = KeyboardType.Email,
        value = email,
        onValueChange = { email = it }
    )

    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small2))

    AuthTextField(
        modifier = Modifier.fillMaxWidth(),
        label = "비밀번호",
        leadingIcon = Icons.Default.Lock,
        keyboardType = KeyboardType.Password,
        value = password,
        onValueChange = { password = it }
    )

    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small2))

    AuthTextField(
        modifier = Modifier.fillMaxWidth(),
        label = "비밀번호 확인",
        leadingIcon = Icons.Default.Lock,
        keyboardType = KeyboardType.Password,
        value = passwordConfirm,
        onValueChange = { passwordConfirm = it }
    )

    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(MaterialTheme.dimens.buttonHeight),
        onClick = {
            onSignUpWithEmailAndPassword(email, password, passwordConfirm)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSystemInDarkTheme()) BlueGray else Black,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(size = 4.dp)
    ) {
        Text(text = "회원가입", style = MaterialTheme.typography.labelMedium)
    }
}