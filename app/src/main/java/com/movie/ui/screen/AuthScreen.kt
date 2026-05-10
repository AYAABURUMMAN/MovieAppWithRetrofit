package com.movie.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movie.viewmodel.AuthState
import com.movie.viewmodel.AuthViewModel

private val Purple        = Color(0xFF4A0E8F)
private val PurpleButton  = Color(0xFF3D0C7A)
private val PurpleText    = Color(0xFF7B5EA7)
private val White         = Color.White
private val BorderColor   = Color(0xFFE0E0E0)
private val TextDark      = Color(0xFF1A1A2E)
private val TextGray      = Color(0xFF888888)

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onAuthSuccess: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onAuthSuccess()
            viewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        // ── Purple Header ────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Purple),
            contentAlignment = Alignment.BottomStart
        ) {
            Column(modifier = Modifier.padding(start = 28.dp, bottom = 28.dp)) {
                Text(
                    text = if (selectedTab == 0) "Welcome Back !" else "Welcome !",
                    color = White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Let's Skill-Up!",
                    color = White.copy(alpha = 0.85f),
                    fontSize = 16.sp
                )
            }
        }

        // ── White Card ───────────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-20).dp),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Tabs
                Row(modifier = Modifier.fillMaxWidth()) {
                    AuthTab(
                        text = "Sign in",
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0; viewModel.resetState() },
                        modifier = Modifier.weight(1f)
                    )
                    AuthTab(
                        text = "Sign Up",
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1; viewModel.resetState() },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                if (selectedTab == 0) {
                    SignInForm(
                        viewModel = viewModel,
                        authState = authState,
                        onSwitchToSignUp = { selectedTab = 1; viewModel.resetState() }
                    )
                } else {
                    SignUpForm(
                        viewModel = viewModel,
                        authState = authState,
                        onSwitchToSignIn = { selectedTab = 0; viewModel.resetState() }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SignInForm(
    viewModel: AuthViewModel,
    authState: AuthState,
    onSwitchToSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoading = authState is AuthState.Loading
    val errorMsg = (authState as? AuthState.Error)?.message

    AuthInputField(
        label = "Email",
        value = email,
        onValueChange = { email = it },
        placeholder = "example@email.com",
        keyboardType = KeyboardType.Email
    )
    Spacer(modifier = Modifier.height(16.dp))
    AuthInputField(
        label = "Password",
        value = password,
        onValueChange = { password = it },
        placeholder = "••••••••••••",
        isPassword = true
    )
    Spacer(modifier = Modifier.height(8.dp))

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
        Text(
            text = "Forgot Password ?",
            color = PurpleText,
            fontSize = 13.sp,
            modifier = Modifier.clickable { }
        )
    }

    Spacer(modifier = Modifier.height(8.dp))
    ErrorMessage(errorMsg)
    Spacer(modifier = Modifier.height(20.dp))

    AuthButton(text = "Sign in", isLoading = isLoading) {
        viewModel.login(email, password)
    }
    Spacer(modifier = Modifier.height(16.dp))
    SwitchAuthText(
        text = "Don't have an account ? ",
        actionText = "Sign Up!",
        onClick = onSwitchToSignUp
    )
}

@Composable
private fun SignUpForm(
    viewModel: AuthViewModel,
    authState: AuthState,
    onSwitchToSignIn: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val isLoading = authState is AuthState.Loading
    val errorMsg = (authState as? AuthState.Error)?.message

    AuthInputField(
        label = "Email",
        value = email,
        onValueChange = { email = it },
        placeholder = "example@email.com",
        keyboardType = KeyboardType.Email
    )
    Spacer(modifier = Modifier.height(16.dp))
    AuthInputField(
        label = "Password",
        value = password,
        onValueChange = { password = it },
        placeholder = "••••••••••••",
        isPassword = true
    )
    Spacer(modifier = Modifier.height(16.dp))
    AuthInputField(
        label = "Confirm Password",
        value = confirmPassword,
        onValueChange = { confirmPassword = it },
        placeholder = "••••••••••••",
        isPassword = true
    )
    Spacer(modifier = Modifier.height(8.dp))
    ErrorMessage(errorMsg)
    Spacer(modifier = Modifier.height(20.dp))

    AuthButton(text = "Sign up", isLoading = isLoading) {
        viewModel.register(email, password, confirmPassword)
    }
    Spacer(modifier = Modifier.height(16.dp))
    SwitchAuthText(
        text = "Already have an account ? ",
        actionText = "Sign In!",
        onClick = onSwitchToSignIn
    )
}

// ── Reusable components ───────────────────────────────────────────────────────

@Composable
private fun AuthTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            color = if (selected) Purple else TextGray,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(3.dp)
                .background(
                    if (selected) Purple else Color.Transparent,
                    RoundedCornerShape(2.dp)
                )
        )
    }
}

@Composable
private fun AuthInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(
            text = label,
            color = TextDark,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = TextGray, fontSize = 14.sp) },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isPassword) KeyboardType.Password else keyboardType
            ),
            shape = RoundedCornerShape(50.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Purple,
                unfocusedBorderColor = BorderColor,
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                cursorColor = Purple
            ),
            singleLine = true
        )
    }
}

@Composable
private fun AuthButton(
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PurpleButton),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
        } else {
            Text(text, color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SwitchAuthText(text: String, actionText: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = text, color = TextGray, fontSize = 13.sp)
        Text(
            text = actionText,
            color = PurpleText,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable(onClick = onClick)
        )
    }
}

@Composable
private fun ErrorMessage(message: String?) {
    AnimatedVisibility(visible = message != null, enter = fadeIn(), exit = fadeOut()) {
        message?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            )
        }
    }
}