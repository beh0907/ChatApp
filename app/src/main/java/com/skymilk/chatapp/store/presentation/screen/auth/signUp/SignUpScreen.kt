package com.skymilk.chatapp.store.presentation.screen.auth.signUp

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skymilk.chatapp.R
import com.skymilk.chatapp.store.presentation.screen.auth.AuthState
import com.skymilk.chatapp.store.presentation.screen.auth.AuthViewModel
import com.skymilk.chatapp.store.presentation.screen.auth.components.AuthTextField
import com.skymilk.chatapp.ui.theme.LeeSeoYunFont
import com.skymilk.chatapp.ui.theme.dimens

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToSignIn: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    //로그인 상태일때 자동으로 홈 화면으로 이동시킨다
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) onNavigateToHome()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.5f)
                .align(CenterHorizontally),
            painter = if (isSystemInDarkTheme()) painterResource(R.drawable.bg_chat_dark)
            else painterResource(R.drawable.bg_chat),
            contentDescription = ""
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp)
        ) {

            SignUpSection(viewModel::signUpWithEmailAndPassword)
        }

        CreateSection(onNavigateToSignIn)
    }
}

//회원가입 영역
@Composable
private fun SignUpSection(onSignUpWithEmailAndPassword: (String, String, String, String) -> Unit) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }

    AuthTextField(
        modifier = Modifier.fillMaxWidth(),
        label = "이름",
        leadingIcon = Icons.Rounded.Person,
        keyboardType = KeyboardType.Email,
        value = name,
        onValueChange = { name = it }
    )

    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small2))

    AuthTextField(
        modifier = Modifier.fillMaxWidth(),
        label = "이메일",
        leadingIcon = Icons.Rounded.Email,
        keyboardType = KeyboardType.Email,
        value = email,
        onValueChange = { email = it }
    )

    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small2))

    AuthTextField(
        modifier = Modifier.fillMaxWidth(),
        label = "비밀번호",
        leadingIcon = Icons.Rounded.Lock,
        keyboardType = KeyboardType.Password,
        value = password,
        onValueChange = { password = it }
    )

    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small2))

    AuthTextField(
        modifier = Modifier.fillMaxWidth(),
        label = "비밀번호 확인",
        leadingIcon = Icons.Rounded.Lock,
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
            onSignUpWithEmailAndPassword(name, email, password, passwordConfirm)
        },
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(size = 4.dp)
    ) {
        Text(text = "회원가입", style = MaterialTheme.typography.labelMedium)
    }
}


//하단 계정 생성 문구
@Composable
private fun ColumnScope.CreateSection(onNavigateToSignIn: () -> Unit) {
    TextButton(
        modifier = Modifier.align(alignment = CenterHorizontally),
        onClick = { onNavigateToSignIn() }) {

        Text(text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = Color(0xff94a3b8),
                    fontSize = MaterialTheme.typography.labelMedium.fontSize,
                    fontFamily = LeeSeoYunFont,
                    fontWeight = FontWeight.Normal
                )
            ) {
                append("이미 가입된 회원이신가요?")
            }

            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = MaterialTheme.typography.labelMedium.fontSize,
                    fontFamily = LeeSeoYunFont,
                    fontWeight = FontWeight.Medium
                )
            ) {
                append(" ")
                append("로그인")
            }
        })
    }
}