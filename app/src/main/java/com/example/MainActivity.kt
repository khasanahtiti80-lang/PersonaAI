package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.ai.GeminiAiService
import com.example.data.local.PersonaDatabase
import com.example.data.local.SessionManager
import com.example.data.repository.BrandProfileRepository
import com.example.data.repository.ContentRepository
import com.example.data.repository.EvaluationRepository
import com.example.data.repository.InterviewRepository
import com.example.data.repository.UserRepository
import com.example.ui.MainAppScaffold
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.LandingScreen
import com.example.ui.theme.PersonaAITheme
import com.example.ui.theme.PersonaWarmOffWhite

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = PersonaDatabase.getInstance(applicationContext)
        val sessionManager = SessionManager(applicationContext)
        val aiService = GeminiAiService(sessionManager)

        val userRepository = UserRepository(database, sessionManager)
        val profileRepository = BrandProfileRepository(database, aiService)
        val contentRepository = ContentRepository(database, aiService)
        val interviewRepository = InterviewRepository(database, aiService)
        val evaluationRepository = EvaluationRepository(database, aiService)

        setContent {
            PersonaAITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = PersonaWarmOffWhite
                ) {
                    val currentUserId by sessionManager.currentUserId.collectAsState()
                    val currentUser by userRepository.observeCurrentUser().collectAsState(initial = null)

                    var authFlowState by remember { mutableStateOf<String?>("landing") } // "landing", "login", "signup"

                    AnimatedContent(
                        targetState = currentUserId != null,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "AuthOrAppTransition"
                    ) { isLoggedIn ->
                        if (isLoggedIn && currentUser != null) {
                            MainAppScaffold(
                                user = currentUser!!,
                                userRepository = userRepository,
                                profileRepository = profileRepository,
                                contentRepository = contentRepository,
                                interviewRepository = interviewRepository,
                                evaluationRepository = evaluationRepository,
                                sessionManager = sessionManager,
                                aiService = aiService,
                                onLogout = {
                                    authFlowState = "landing"
                                }
                            )
                        } else {
                            when (authFlowState) {
                                "login" -> AuthScreen(
                                    userRepository = userRepository,
                                    initialIsSignUp = false,
                                    onAuthSuccess = { authFlowState = null },
                                    onBackToLanding = { authFlowState = "landing" }
                                )

                                "signup" -> AuthScreen(
                                    userRepository = userRepository,
                                    initialIsSignUp = true,
                                    onAuthSuccess = { authFlowState = null },
                                    onBackToLanding = { authFlowState = "landing" }
                                )

                                else -> LandingScreen(
                                    onNavigateToLogin = { authFlowState = "login" },
                                    onNavigateToSignUp = { authFlowState = "signup" }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
