package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.GeminiAiService
import com.example.data.local.SessionManager
import com.example.data.local.entity.UserEntity
import com.example.data.repository.ContentRepository
import com.example.data.repository.UserRepository
import com.example.ui.components.AiBadge
import com.example.ui.components.PersonaPrimaryButton
import com.example.ui.components.PersonaSecondaryButton
import com.example.ui.components.TipCard
import com.example.ui.theme.PersonaBorder
import com.example.ui.theme.PersonaButterCream
import com.example.ui.theme.PersonaCharcoal
import com.example.ui.theme.PersonaCoralPink
import com.example.ui.theme.PersonaCreamSurface
import com.example.ui.theme.PersonaDarkOlive
import com.example.ui.theme.PersonaGreen
import com.example.ui.theme.PersonaLightLime
import com.example.ui.theme.PersonaMutedGray
import com.example.ui.theme.PersonaWarmOffWhite
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
    user: UserEntity?,
    userRepository: UserRepository,
    contentRepository: ContentRepository,
    sessionManager: SessionManager,
    aiService: GeminiAiService,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val currentCustomKey by sessionManager.customApiKey.collectAsState()
    var inputApiKey by remember(currentCustomKey) { mutableStateOf(currentCustomKey) }
    var isKeyVisible by remember { mutableStateOf(false) }

    var isTestingConnection by remember { mutableStateOf(false) }
    var connectionTestResult by remember { mutableStateOf<String?>(null) }
    var isConnectionSuccess by remember { mutableStateOf(false) }

    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PersonaWarmOffWhite)
            .padding(horizontal = 20.dp)
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(6.dp)) }

        // Title
        item {
            Column {
                Text(
                    text = "Settings & Privacy",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PersonaDarkOlive
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Manage your account, AI credentials, and data privacy.",
                    fontSize = 13.sp,
                    color = PersonaMutedGray
                )
            }
        }

        // Account Profile Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, PersonaBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(PersonaGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (user?.fullName?.take(1) ?: "U").uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = user?.fullName ?: "User",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = PersonaDarkOlive
                            )
                            Text(
                                text = user?.email ?: "",
                                fontSize = 13.sp,
                                color = PersonaMutedGray
                            )
                            val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                .format(Date(user?.createdAt ?: System.currentTimeMillis()))
                            Text(
                                text = "Member since $dateStr",
                                fontSize = 11.sp,
                                color = PersonaMutedGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    PersonaSecondaryButton(
                        text = "Log Out",
                        icon = Icons.Default.ExitToApp,
                        onClick = {
                            userRepository.logout()
                            onLogout()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "settings_logout_button"
                    )
                }
            }
        }

        // Gemini AI Configuration Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, PersonaBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = PersonaGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Gemini AI Configuration",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = PersonaDarkOlive
                            )
                        }
                        val hasKey = sessionManager.hasApiKey()
                        Surface(
                            color = if (hasKey) PersonaLightLime else PersonaButterCream,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (hasKey) "Connected" else "Key Missing",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PersonaDarkOlive,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Text(
                        text = "PersonaAI connects to Google's Gemini generative model for live bios, social content, and interview simulations. You can supply your API key below or configure it in the Secrets panel in AI Studio.",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = PersonaCharcoal
                    )

                    OutlinedTextField(
                        value = inputApiKey,
                        onValueChange = {
                            inputApiKey = it
                            connectionTestResult = null
                        },
                        label = { Text("Gemini API Key") },
                        placeholder = { Text("AIzaSy...") },
                        singleLine = true,
                        visualTransformation = if (isKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isKeyVisible = !isKeyVisible }) {
                                Icon(
                                    imageVector = if (isKeyVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = PersonaMutedGray
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PersonaGreen,
                            unfocusedBorderColor = PersonaBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_api_key_input")
                    )

                    if (connectionTestResult != null) {
                        Surface(
                            color = if (isConnectionSuccess) PersonaLightLime.copy(alpha = 0.4f) else PersonaCoralPink.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isConnectionSuccess) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (isConnectionSuccess) PersonaDarkOlive else Color(0xFF9E2A2B),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = connectionTestResult ?: "",
                                    fontSize = 12.sp,
                                    color = if (isConnectionSuccess) PersonaDarkOlive else Color(0xFF9E2A2B)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PersonaPrimaryButton(
                            text = "Save Key",
                            onClick = {
                                sessionManager.saveCustomApiKey(inputApiKey)
                                Toast.makeText(context, "API Key saved successfully!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "save_api_key_button"
                        )

                        PersonaSecondaryButton(
                            text = "Test Connection",
                            onClick = {
                                scope.launch {
                                    isTestingConnection = true
                                    connectionTestResult = null
                                    val keyToTest = inputApiKey.ifBlank { sessionManager.getEffectiveApiKey() }
                                    val res = aiService.testConnection(keyToTest)
                                    isTestingConnection = false
                                    res.onSuccess {
                                        isConnectionSuccess = true
                                        connectionTestResult = "Connection successful! Gemini API is active."
                                    }.onFailure { e ->
                                        isConnectionSuccess = false
                                        connectionTestResult = e.localizedMessage ?: "Connection failed. Please check key."
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "test_connection_button"
                        )
                    }
                }
            }
        }

        // Privacy & Data Controls
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, PersonaBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = PersonaDarkOlive,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Data Management & Privacy",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = PersonaDarkOlive
                        )
                    }

                    Text(
                        text = "PersonaAI stores your data locally on your device with high-grade security. You have complete control to wipe content or permanently delete your account at any time.",
                        fontSize = 12.sp,
                        color = PersonaCharcoal
                    )

                    PersonaSecondaryButton(
                        text = "Clear Content History",
                        icon = Icons.Default.Delete,
                        onClick = { showClearHistoryDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "clear_history_button"
                    )

                    PersonaSecondaryButton(
                        text = "Delete Account & All Data",
                        icon = Icons.Default.Delete,
                        onClick = { showDeleteAccountDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "delete_account_button"
                    )
                }
            }
        }

        // About & Version Info
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "PersonaAI v1.0",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = PersonaDarkOlive
                )
                Text(
                    text = "AI-Powered Personal Branding Platform",
                    fontSize = 11.sp,
                    color = PersonaMutedGray
                )
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    // Clear History Dialog
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Clear Content History") },
            text = { Text("This will delete all your saved bios, taglines, and social media posts. Your core Brand Profile will be preserved. Proceed?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val userId = user?.id
                        if (userId != null) {
                            scope.launch {
                                contentRepository.clearAllContent(userId)
                                showClearHistoryDialog = false
                                Toast.makeText(context, "Content history cleared", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                ) {
                    Text("Clear", color = Color(0xFFC94A4A), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel", color = PersonaCharcoal)
                }
            }
        )
    }

    // Delete Account Dialog
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Delete Account Permanently") },
            text = { Text("This will permanently wipe your account, credentials, brand profile, bios, social calendars, and interview evaluations. This action cannot be reversed.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            userRepository.deleteAccount()
                            showDeleteAccountDialog = false
                            onLogout()
                            Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Delete Everything", color = Color(0xFFC94A4A), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancel", color = PersonaCharcoal)
                }
            }
        )
    }
}
