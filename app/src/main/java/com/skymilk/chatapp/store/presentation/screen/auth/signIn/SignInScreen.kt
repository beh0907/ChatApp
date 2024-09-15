package com.skymilk.chatapp.store.presentation.screen.auth.signIn

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skymilk.chatapp.R
import com.skymilk.chatapp.store.presentation.screen.AuthState
import com.skymilk.chatapp.store.presentation.screen.auth.AuthViewModel
import com.skymilk.chatapp.store.presentation.screen.auth.components.LoginTextField
import com.skymilk.chatapp.ui.theme.Black
import com.skymilk.chatapp.ui.theme.BlueGray
import com.skymilk.chatapp.ui.theme.HannaPro
import com.skymilk.chatapp.ui.theme.dimens

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    onNavigateToSignUp: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    //로그인 상태일때 자동으로 홈 화면으로 이동시킨다
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            onNavigateToHome()
        }
    }

    Surface(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            TopSection()

            Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium2))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(30.dp)
            ) {
                LoginSection()
                Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))
                SocialSection { viewModel.signInWithGoogle() }
            }

            Spacer(modifier = Modifier.weight(0.8f))
            CreateSection(onNavigateToSignUp)
            Spacer(modifier = Modifier.weight(0.3f))
        }
    }
}


//상단 이미지 및 로고 영역
@Composable
private fun TopSection() {
    val uiColor = if (isSystemInDarkTheme()) Color.White else Black
    val screenHeight = LocalConfiguration.current.screenHeightDp

    //상단 이미지, 로고 타이틀
    Box(contentAlignment = Alignment.TopCenter) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .height((screenHeight / 2.12).dp),
            painter = painterResource(id = R.drawable.shape),
            contentScale = ContentScale.FillBounds,
            contentDescription = "shape"
        )

        Row(
            modifier = Modifier.padding(top = (screenHeight / 9).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(MaterialTheme.dimens.logoSize),
                painter = painterResource(id = R.drawable.logo),
                contentDescription = stringResource(
                    id = R.string.app_logo
                ),
                tint = uiColor
            )

            Spacer(modifier = Modifier.width(MaterialTheme.dimens.small2))

            Column {
                Text(
                    text = stringResource(id = R.string.title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = uiColor
                )

                Text(
                    text = stringResource(id = R.string.find_house),
                    style = MaterialTheme.typography.titleMedium,
                    color = uiColor
                )
            }
        }

        Text(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .align(alignment = Alignment.BottomCenter),
            text = stringResource(id = R.string.login),
            style = MaterialTheme.typography.headlineLarge,
            color = uiColor
        )
    }
}

//ID,비밀번호 입력 로그인 영역
@Composable
private fun LoginSection() {
    LoginTextField(modifier = Modifier.fillMaxWidth(), label = "이메일", trailing = "")

    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small2))

    LoginTextField(
        modifier = Modifier.fillMaxWidth(),
        label = "비밀번호",
        trailing = "찾기?"
    )

    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(MaterialTheme.dimens.buttonHeight),
        onClick = {

        },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSystemInDarkTheme()) BlueGray else Black,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(size = 4.dp)
    ) {
        Text(text = "로그인", style = MaterialTheme.typography.labelMedium)
    }
}


//소셜 로그인 OR 회원가입 영역
@Composable
private fun SocialSection(onSignInWithGoogleClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "다른 방식으로 로그인 하시겠습니까?",
            style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF64748B))
        )

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            SignInSocialMedia(
                modifier = Modifier.weight(weight = 1f),
                icon = R.drawable.google,
                text = "google"
            ) {
                onSignInWithGoogleClick()
            }

            Spacer(modifier = Modifier.width(MaterialTheme.dimens.small1))

            SignInSocialMedia(
                modifier = Modifier.weight(weight = 1f),
                icon = R.drawable.facebook,
                text = "face book"
            ) {

            }
        }
    }
}

//하단 계정 생성 문구
@Composable
private fun ColumnScope.CreateSection(onNavigateToSignUp: () -> Unit) {
    val uiColor = if (isSystemInDarkTheme()) Color.White else Black

    TextButton(onClick = { onNavigateToSignUp() }) { }

    Text(modifier = Modifier.align(alignment = CenterHorizontally),
        text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = Color(0xff94a3b8),
                    fontSize = MaterialTheme.typography.labelMedium.fontSize,
                    fontFamily = HannaPro,
                    fontWeight = FontWeight.Normal
                )
            ) {
                append("계정이 없으신가요?")
            }

            withStyle(
                style = SpanStyle(
                    color = uiColor,
                    fontSize = MaterialTheme.typography.labelMedium.fontSize,
                    fontFamily = HannaPro,
                    fontWeight = FontWeight.Medium
                )
            ) {
                append(" ")
                append("계정 등록")
            }
        })
}