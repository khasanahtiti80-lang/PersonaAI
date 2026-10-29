package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.UserRepository
import com.example.ui.components.PersonaPrimaryButton
import com.example.ui.components.TipCard
import com.example.ui.theme.PersonaBorder
import com.example.ui.theme.PersonaButterCream
import com.example.ui.theme.PersonaCharcoal
import com.example.ui.theme.PersonaCoralPink
import com.example.ui.theme.PersonaCreamSurface
import com.example.ui.theme.PersonaDarkOlive
import com.example.ui.theme.PersonaGreen
import com.example.ui.theme.PersonaMutedGray
import com.example.ui.theme.PersonaWarmOffWhite
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    userRepository: UserRepository,
    initialIsSignUp: Boolean = false,
    onAuthSuccess: () -> Unit,
    onBackToLanding: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(if (initialIsSignUp) 1 else 0) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PersonaWarmOffWhite)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .testTag("auth_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Back Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackToLanding,
                modifier = Modifier.testTag("auth_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to landing",
                    tint = PersonaDarkOlive
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Brand Logo
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(PersonaGreen),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "P",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (selectedTab == 0) "Welcome Back" else "Create Your Account",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PersonaDarkOlive
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (selectedTab == 0)
                "Log in to continue building your personal brand"
            else
                "Start discovering and growing your professional identity",
            fontSize = 13.sp,
            color = PersonaMutedGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tabs: Log In / Sign Up
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = PersonaDarkOlive,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = PersonaGreen,
                    height = 3.dp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, PersonaBorder, RoundedCornerShape(14.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = {
                    selectedTab = 0
                    errorMessage = null
                },
                modifier = Modifier
                    .height(48.dp)
                    .testTag("tab_login")
            ) {
                Text(
                    text = "Log In",
                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedTab == 0) PersonaDarkOlive else PersonaMutedGray,
                    fontSize = 14.sp
                )
            }
            Tab(
                selected = selectedTab == 1,
                onClick = {
                    selectedTab = 1
                    errorMessage = null
                },
                modifier = Modifier
                    .height(48.dp)
                    .testTag("tab_signup")
            ) {
                Text(
                    text = "Sign Up",
                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedTab == 1) PersonaDarkOlive else PersonaMutedGray,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Auth Card Form
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, PersonaBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // If Sign Up: Full Name
                AnimatedVisibility(visible = selectedTab == 1) {
                    Column {
                        Text(
                            text = "Full Name",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PersonaDarkOlive
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            placeholder = { Text("e.g. Alex Morgan", color = PersonaMutedGray) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = PersonaGreen
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PersonaGreen,
                                unfocusedBorderColor = PersonaBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_name_input")
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }

                // Email Field
                Text(
                    text = "Email Address",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PersonaDarkOlive
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = null
                    },
                    placeholder = { Text("you@example.com", color = PersonaMutedGray) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = PersonaGreen
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PersonaGreen,
                        unfocusedBorderColor = PersonaBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_email_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Password Field
                Text(
                    text = "Password",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PersonaDarkOlive
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    placeholder = { Text("••••••••", color = PersonaMutedGray) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = PersonaGreen
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { isPasswordVisible = !isPasswordVisible },
                            modifier = Modifier.testTag("password_visibility_toggle")
                        ) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                                tint = PersonaMutedGray
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PersonaGreen,
                        unfocusedBorderColor = PersonaBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_password_input")
                )

                // Error Message Display
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = PersonaCoralPink.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = Color(0xFF9E2A2B),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Primary Submit Button
                PersonaPrimaryButton(
                    text = if (selectedTab == 0) "Log In" else "Create Account",
                    loading = isLoading,
                    onClick = {
                        errorMessage = null
                        isLoading = true
                        scope.launch {
                            val result = if (selectedTab == 0) {
                                userRepository.login(email, password)
                            } else {
                                userRepository.signUp(email, password, fullName)
                            }

                            isLoading = false
                            result.onSuccess {
                                onAuthSuccess()
                            }.onFailure { e ->
                                errorMessage = e.localizedMessage ?: "Authentication failed."
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "auth_submit_button"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Demo Fill (for seamless testing & review)
                Surface(
                    color = PersonaCreamSurface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PersonaButterCream),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (selectedTab == 0) {
                                email = "alex.creator@persona.ai"
                                password = "Password123!"
                            } else {
                                fullName = "Alex Morgan"
                                email = "alex.creator@persona.ai"
                                password = "Password123!"
                            }
                            errorMessage = null
                        }
                        .padding(10.dp)
                        .testTag("quick_fill_credentials_button")
                ) {
                    Text(
                        text = "💡 Quick Fill Demo Account (tap to auto-fill credentials)",
                        fontSize = 12.sp,
                        color = PersonaDarkOlive,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Policy / Privacy note
        Text(
            text = "Your credentials and brand data are stored securely in a local database with SHA-256 encryption.",
            fontSize = 11.sp,
            color = PersonaMutedGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
