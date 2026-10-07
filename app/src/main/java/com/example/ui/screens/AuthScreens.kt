package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.PixoraViewModel
import com.example.ui.viewmodel.ScreenRoute
import com.example.util.SecurityUtils

@Composable
fun SplashScreen(
    viewModel: PixoraViewModel,
    onContinue: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Charcoal900)
            .clickable { onContinue() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(Charcoal800)
                    .border(2.dp, GoldAccent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Pixora Shutter Monogram",
                    tint = GoldAccent,
                    modifier = Modifier.size(46.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "PIXORA",
                style = MaterialTheme.typography.displayMedium,
                fontFamily = FontFamily.Serif,
                letterSpacing = 6.sp,
                fontWeight = FontWeight.Light,
                color = GoldLight
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "PHOTOGRAPHY CLIENT GALLERIES & STUDIO CRM",
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 2.sp,
                color = Charcoal300
            )
        }

        Text(
            text = "Tap to continue",
            style = MaterialTheme.typography.bodySmall,
            color = Charcoal400,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}

@Composable
fun LoginScreen(
    viewModel: PixoraViewModel,
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Charcoal900)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Charcoal800)
                    .border(1.5.dp, GoldAccent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "PIXORA",
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontFamily.Serif,
                letterSpacing = 4.sp,
                color = GoldLight,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = "Sign In",
            style = MaterialTheme.typography.headlineLarge,
            fontFamily = FontFamily.Serif,
            color = Color.White
        )
        Text(
            text = "Enter your credentials to access your studio portal.",
            style = MaterialTheme.typography.bodyMedium,
            color = Charcoal300
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (errorMessage != null) {
            Surface(
                color = LuxuryRedLight,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = LuxuryRed, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = errorMessage!!, color = LuxuryRed, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                }
            }
        }

        OutlinedTextField(
            value = identifier,
            onValueChange = {
                identifier = it
                errorMessage = null
            },
            label = { Text("Login ID / Email / Username") },
            placeholder = { Text("Enter your username or email") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_identifier_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GoldAccent,
                unfocusedBorderColor = Charcoal600
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = null
            },
            label = { Text("Password") },
            placeholder = { Text("Enter your password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle password visibility",
                        tint = Charcoal300
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_password_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GoldAccent,
                unfocusedBorderColor = Charcoal600
            ),
            singleLine = true
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(checkedColor = GoldAccent, checkmarkColor = Charcoal900)
                )
                Text(text = "Remember me", style = MaterialTheme.typography.bodySmall, color = Charcoal200)
            }
            Text(
                text = "Forgot Password?",
                style = MaterialTheme.typography.bodySmall,
                color = GoldAccent,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onNavigateToForgotPassword() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (identifier.isBlank() || password.isBlank()) {
                    errorMessage = "Please enter both Login ID and Password."
                } else {
                    val success = viewModel.login(identifier, password)
                    if (!success) {
                        errorMessage = "Invalid credentials. If this is your first time, please create an account below."
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("login_submit_button"),
            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Google Sign In Option
        OutlinedButton(
            onClick = {
                viewModel.loginWithGoogle("Studio Admin", "admin@pixorastudios.com")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("login_google_button"),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = Brush.horizontalGradient(listOf(GoldAccent.copy(alpha = 0.6f), GoldLight.copy(alpha = 0.6f)))
            )
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = GoldAccent,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text("Continue with Google", fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Don't have an account? ", color = Charcoal300, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Sign Up",
                color = GoldAccent,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToSignUp() }
            )
        }
    }
}

@Composable
fun SignUpScreen(
    viewModel: PixoraViewModel,
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var studioName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Charcoal900)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineLarge,
            fontFamily = FontFamily.Serif,
            color = Color.White
        )
        Text(
            text = "Register your photography studio to get started.",
            style = MaterialTheme.typography.bodyMedium,
            color = Charcoal300
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (errorMessage != null) {
            Surface(
                color = LuxuryRedLight,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = LuxuryRed, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = errorMessage!!, color = LuxuryRed, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                }
            }
        }

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it; errorMessage = null },
            label = { Text("Full Name *") },
            placeholder = { Text("Enter your full name") },
            modifier = Modifier.fillMaxWidth().testTag("signup_name_input"),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = studioName,
            onValueChange = { studioName = it; errorMessage = null },
            label = { Text("Studio Name *") },
            placeholder = { Text("Enter your studio or brand name") },
            modifier = Modifier.fillMaxWidth().testTag("signup_studio_input"),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it; errorMessage = null },
            label = { Text("Username / Login ID *") },
            placeholder = { Text("Choose a unique username") },
            modifier = Modifier.fillMaxWidth().testTag("signup_username_input"),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; errorMessage = null },
            label = { Text("Email Address *") },
            placeholder = { Text("e.g. name@domain.com") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().testTag("signup_email_input"),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it; errorMessage = null },
            label = { Text("Mobile Number *") },
            placeholder = { Text("e.g. +91 9876543210") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth().testTag("signup_phone_input"),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorMessage = null },
            label = { Text("Password (Min 6 characters) *") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle password",
                        tint = Charcoal300
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().testTag("signup_password_input"),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it; errorMessage = null },
            label = { Text("Confirm Password *") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().testTag("signup_confirm_password_input"),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = termsAccepted,
                onCheckedChange = { termsAccepted = it },
                colors = CheckboxDefaults.colors(checkedColor = GoldAccent, checkmarkColor = Charcoal900)
            )
            Text(
                text = "I agree to Terms of Service & Privacy Policy.",
                style = MaterialTheme.typography.bodySmall,
                color = Charcoal200
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                // Validation checks
                if (fullName.isBlank()) {
                    errorMessage = "Please enter your Full Name."
                } else if (studioName.isBlank()) {
                    errorMessage = "Please enter your Studio Name."
                } else if (username.isBlank()) {
                    errorMessage = "Please choose a Username."
                } else if (!SecurityUtils.isValidEmail(email)) {
                    errorMessage = "Please enter a valid email address."
                } else if (!SecurityUtils.isValidPhone(phone)) {
                    errorMessage = "Please enter a valid mobile number (7-15 digits)."
                } else if (!SecurityUtils.isPasswordStrong(password)) {
                    errorMessage = "Password must be at least 6 characters and contain letters & numbers."
                } else if (password != confirmPassword) {
                    errorMessage = "Passwords do not match."
                } else if (!termsAccepted) {
                    errorMessage = "Please accept the Terms & Privacy Policy."
                } else {
                    val success = viewModel.register(
                        fullName = fullName,
                        email = email,
                        phone = phone,
                        username = username,
                        passwordPlain = password,
                        studioName = studioName,
                        role = UserRole.OWNER
                    )
                    if (success) {
                        onSignUpSuccess()
                    } else {
                        errorMessage = "Registration could not be completed."
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("signup_submit_button"),
            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Create Account", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedButton(
            onClick = {
                viewModel.loginWithGoogle("Google User", "studio@google.com")
                onSignUpSuccess()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = GoldAccent)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Up with Google")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Already have an account? ", color = Charcoal300, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Sign In",
                color = GoldAccent,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }
    }
}

@Composable
fun ForgotPasswordScreen(
    onSendResetLink: (String) -> Unit,
    onBackToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var emailSent by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Charcoal900)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(GoldSubtle),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.LockReset, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(34.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Reset Password",
            style = MaterialTheme.typography.headlineMedium,
            fontFamily = FontFamily.Serif,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (emailSent) "Instructions to reset your password have been sent to $email."
            else "Enter your registered email address to receive password reset instructions.",
            style = MaterialTheme.typography.bodyMedium,
            color = Charcoal300,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        if (!emailSent) {
            if (error != null) {
                Text(text = error!!, color = LuxuryRed, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 8.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; error = null },
                label = { Text("Registered Email Address") },
                placeholder = { Text("name@example.com") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("forgot_email_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldAccent,
                    unfocusedBorderColor = Charcoal600
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (!SecurityUtils.isValidEmail(email)) {
                        error = "Please enter a valid email address."
                    } else {
                        emailSent = true
                        onSendResetLink(email)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("forgot_submit_button"),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Send Reset Link", fontWeight = FontWeight.Bold)
            }
        } else {
            Button(
                onClick = onBackToLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Back to Sign In", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Cancel",
            style = MaterialTheme.typography.bodyMedium,
            color = Charcoal400,
            modifier = Modifier.clickable { onBackToLogin() }
        )
    }
}

@Composable
fun WelcomeLandingScreen(
    onGetStarted: () -> Unit,
    onLogin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Charcoal900)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 28.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Charcoal800)
                        .border(1.5.dp, GoldAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Pixora Monogram",
                        tint = GoldAccent,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "PIXORA",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 6.sp,
                    color = GoldLight,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "STUDIO SUITE",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 3.sp,
                    color = Charcoal300
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 28.dp)
            ) {
                Text(
                    text = "Your Photography.\nBeautifully Delivered.",
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily.Serif,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 36.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Create stunning client galleries, deliver photos and videos, proof selections, manage studio shoots and grow your business — all in one place.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Charcoal300,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(28.dp))

                // Feature Highlights Pills
                val features = listOf(
                    "Client Galleries & Proofing",
                    "High-Res Delivery & Downloads",
                    "Print & Digital Store",
                    "Studio Shoots & CRM",
                    "Quotation & Billing"
                )
                features.forEach { feat ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(feat, color = Charcoal200, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Button(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("welcome_get_started_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Get Started", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("welcome_login_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(listOf(GoldAccent.copy(alpha = 0.5f), GoldLight.copy(alpha = 0.5f)))
                    )
                ) {
                    Text("Sign In to Existing Studio", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun OtpVerifyScreen(
    onVerifySuccess: () -> Unit,
    onResend: () -> Unit
) {
    var otpCode by remember { mutableStateOf("") }
    var secondsLeft by remember { mutableIntStateOf(60) }

    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            kotlinx.coroutines.delay(1000)
            secondsLeft--
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Charcoal900)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(GoldSubtle),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.MarkEmailRead, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(34.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Verify Your Account",
            style = MaterialTheme.typography.headlineMedium,
            fontFamily = FontFamily.Serif,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Enter the 6-digit confirmation code sent to your email address.",
            style = MaterialTheme.typography.bodyMedium,
            color = Charcoal300,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        OutlinedTextField(
            value = otpCode,
            onValueChange = { if (it.length <= 6) otpCode = it },
            label = { Text("6-Digit Verification Code") },
            placeholder = { Text("123456") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("otp_code_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GoldAccent,
                unfocusedBorderColor = Charcoal600
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (otpCode.length >= 4) {
                    onVerifySuccess()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("otp_verify_button"),
            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Verify & Continue", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (secondsLeft > 0) {
                Text("Resend code in ${secondsLeft}s", color = Charcoal400, style = MaterialTheme.typography.bodySmall)
            } else {
                Text(
                    text = "Resend Verification Code",
                    color = GoldAccent,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        secondsLeft = 60
                        onResend()
                    }
                )
            }
        }
    }
}

@Composable
fun OnboardingWizardScreen(
    onFinish: () -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    var studioBrandName by remember { mutableStateOf("Signature Studios") }
    var watermarkText by remember { mutableStateOf("PIXORA") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Charcoal900)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 32.dp)
        ) {
            Text(
                text = "Welcome to PIXORA",
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = FontFamily.Serif,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Let's personalize your studio identity in 3 simple steps.",
                style = MaterialTheme.typography.bodyMedium,
                color = Charcoal300,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            when (step) {
                1 -> {
                    Text("STEP 1 OF 3: BRANDING", style = MaterialTheme.typography.labelSmall, color = GoldAccent, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = studioBrandName,
                        onValueChange = { studioBrandName = it },
                        label = { Text("Studio Display Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                2 -> {
                    Text("STEP 2 OF 3: WATERMARK", style = MaterialTheme.typography.labelSmall, color = GoldAccent, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = watermarkText,
                        onValueChange = { watermarkText = it },
                        label = { Text("Default Watermark Label") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                else -> {
                    Text("STEP 3 OF 3: READY TO DELIVER", style = MaterialTheme.typography.labelSmall, color = GoldAccent, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Your studio is ready! You can now create projects, upload high-res galleries, share proofing links, and generate client invoices.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Charcoal200,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
            Button(
                onClick = {
                    if (step < 3) step++
                    else onFinish()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(if (step < 3) "Continue" else "Launch Studio Dashboard", fontWeight = FontWeight.Bold)
            }
        }
    }
}
