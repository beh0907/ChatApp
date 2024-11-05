package com.skymilk.chatapp.store.presentation.screen.auth.signIn

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.skymilk.chatapp.R
import com.skymilk.chatapp.store.presentation.common.HandleDoubleBackPress
import com.skymilk.chatapp.store.presentation.screen.auth.AuthEvent
import com.skymilk.chatapp.store.presentation.screen.auth.AuthState
import com.skymilk.chatapp.store.presentation.screen.auth.AuthViewModel
import com.skymilk.chatapp.store.presentation.screen.auth.components.AuthTextField
import com.skymilk.chatapp.store.presentation.utils.CredentialUtil
import com.skymilk.chatapp.ui.theme.dimens
import com.skymilk.chatapp.ui.theme.isAppInDarkTheme
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
    onEvent: (AuthEvent) -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    //뒤로가기 더블클릭 종료 처리
    HandleDoubleBackPress()

    //로그인 상태일때 자동으로 홈 화면으로 이동시킨다
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) onNavigateToHome()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.5f)
                .align(CenterHorizontally),
            painter = if (isAppInDarkTheme()) painterResource(R.drawable.bg_chat_dark)
            else painterResource(R.drawable.bg_chat),
            contentDescription = ""
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp)
        ) {
            SignInSection(
                onSignInWithEmailAndPassword = { email, password ->
                    onEvent(AuthEvent.SignInWithEmailAndPassword(email, password))
                }
            )

            Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))

            SocialSection(
                onSignInWithGoogle = { googleIdTokenCredential ->
                    onEvent(AuthEvent.SignInWithGoogle(googleIdTokenCredential))
                },
                onSignInWithKaKao = { oAuthToken ->
                    onEvent(AuthEvent.SignInWithKakao(oAuthToken))
                }
            )
        }

        CreateSection(onNavigateToSignUp)
    }
}

//ID,비밀번호 입력 로그인 영역
@Composable
private fun SignInSection(onSignInWithEmailAndPassword: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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

    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(MaterialTheme.dimens.buttonHeight),
        onClick = {
            onSignInWithEmailAndPassword(email, password)
        },
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.White,
            containerColor = MaterialTheme.colorScheme.inversePrimary
        ),
        shape = RoundedCornerShape(size = 4.dp)
    ) {
        Text(text = "로그인", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}


//소셜 로그인 OR 회원가입 영역
@Composable
private fun SocialSection(
    onSignInWithGoogle: (GoogleIdTokenCredential?) -> Unit,
    onSignInWithKaKao: (OAuthToken?) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(horizontalAlignment = CenterHorizontally) {
        Text(
            text = "다른 방식으로 로그인 하시겠습니까?",
            style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF64748B))
        )

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            SignInSocialMedia(
                modifier = Modifier.weight(weight = 1f),
                icon = R.drawable.ic_google,
                text = "Google 로그인",
                backgroundColor = Color.White,
            ) {
                scope.launch {
                    val googleIdTokenCredential = CredentialUtil.getGoogleIdTokenCredential(context)
                    onSignInWithGoogle(googleIdTokenCredential)
                }
            }

            Spacer(modifier = Modifier.width(MaterialTheme.dimens.small1))

            SignInSocialMedia(
                modifier = Modifier.weight(weight = 1f),
                icon = R.drawable.ic_kakao,
                text = "카카오 로그인",
                backgroundColor = Color(0xFFFEE500),
            ) {
                scope.launch {
                    val kakaoToken =
                        CredentialUtil.getKakaoToken(context as Activity, UserApiClient.instance)
                    onSignInWithKaKao(kakaoToken)
                }
            }
        }
    }
}

//하단 계정 생성 문구
@Composable
private fun ColumnScope.CreateSection(onNavigateToSignUp: () -> Unit) {
    TextButton(
        modifier = Modifier.align(alignment = CenterHorizontally),
        onClick = { onNavigateToSignUp() }) {

        Text(text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = Color(0xff94a3b8),
                    fontStyle = MaterialTheme.typography.labelMedium.fontStyle,
                    fontWeight = FontWeight.Normal
                )
            ) {
                append("계정이 없으신가요?")
            }

            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontStyle = MaterialTheme.typography.labelLarge.fontStyle,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(" ")
                append("계정 등록")
            }
        })
    }
}