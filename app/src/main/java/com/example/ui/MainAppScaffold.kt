package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.GeminiAiService
import com.example.data.local.SessionManager
import com.example.data.local.entity.BrandEvaluationEntity
import com.example.data.local.entity.BrandProfileEntity
import com.example.data.local.entity.GeneratedBioEntity
import com.example.data.local.entity.InterviewSessionEntity
import com.example.data.local.entity.SocialMediaPostEntity
import com.example.data.local.entity.UserEntity
import com.example.data.repository.BrandProfileRepository
import com.example.data.repository.ContentRepository
import com.example.data.repository.EvaluationRepository
import com.example.data.repository.InterviewRepository
import com.example.data.repository.UserRepository
import com.example.ui.screens.BioGeneratorScreen
import com.example.ui.screens.BrandEvaluatorScreen
import com.example.ui.screens.BrandProfileScreen
import com.example.ui.screens.ContentHistoryScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.InterviewSimulatorScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SocialMediaStudioScreen
import com.example.ui.theme.PersonaBorder
import com.example.ui.theme.PersonaButterCream
import com.example.ui.theme.PersonaCharcoal
import com.example.ui.theme.PersonaCreamSurface
import com.example.ui.theme.PersonaDarkOlive
import com.example.ui.theme.PersonaGreen
import com.example.ui.theme.PersonaLightLime
import com.example.ui.theme.PersonaMutedGray
import com.example.ui.theme.PersonaWarmOffWhite
import kotlinx.coroutines.launch

data class NavItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val badge: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(
    user: UserEntity,
    userRepository: UserRepository,
    profileRepository: BrandProfileRepository,
    contentRepository: ContentRepository,
    interviewRepository: InterviewRepository,
    evaluationRepository: EvaluationRepository,
    sessionManager: SessionManager,
    aiService: GeminiAiService,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var currentScreen by remember { mutableStateOf("dashboard") }

    val profile by profileRepository.observeProfile(user.id).collectAsState(initial = null)
    val latestEvaluation by evaluationRepository.observeLatestEvaluation(user.id).collectAsState(initial = null)
    val allEvaluations by evaluationRepository.observeEvaluations(user.id).collectAsState(initial = emptyList())
    val bios by contentRepository.observeBios(user.id).collectAsState(initial = emptyList())
    val recentBios by contentRepository.observeRecentBios(user.id).collectAsState(initial = emptyList())
    val posts by contentRepository.observeSocialPosts(user.id).collectAsState(initial = emptyList())
    val recentPosts by contentRepository.observeRecentSocialPosts(user.id).collectAsState(initial = emptyList())
    val interviewSessions by interviewRepository.observeSessions(user.id).collectAsState(initial = emptyList())

    // BackHandler: return to dashboard if on sub-screens
    BackHandler(enabled = currentScreen != "dashboard") {
        currentScreen = "dashboard"
    }

    val navItems = listOf(
        NavItem("dashboard", "Dashboard", Icons.Default.Dashboard),
        NavItem("profile", "My Brand Profile", Icons.Default.Person),
        NavItem("bio_generator", "AI Bio Generator", Icons.Default.AutoAwesome),
        NavItem("social_studio", "Social Media Studio", Icons.Default.Share),
        NavItem("interview_simulator", "Interview Simulator", Icons.Default.QuestionAnswer),
        NavItem("evaluator", "Brand Evaluation", Icons.Default.TrendingUp),
        NavItem("history", "Content History", Icons.Default.History),
        NavItem("settings", "Settings", Icons.Default.Settings)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                modifier = Modifier.width(300.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PersonaGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "P",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "PersonaAI",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = PersonaDarkOlive
                            )
                            Text(
                                text = user.fullName,
                                fontSize = 12.sp,
                                color = PersonaMutedGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = PersonaBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    navItems.forEach { item ->
                        val isSelected = currentScreen == item.id
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = if (isSelected) PersonaDarkOlive else PersonaMutedGray,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) PersonaDarkOlive else PersonaCharcoal
                                )
                            },
                            selected = isSelected,
                            onClick = {
                                currentScreen = item.id
                                scope.launch { drawerState.close() }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = PersonaLightLime.copy(alpha = 0.5f),
                                unselectedContainerColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .padding(vertical = 2.dp)
                                .testTag("drawer_item_${item.id}")
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                Surface(
                    color = Color.White,
                    shadowElevation = 1.dp
                ) {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PersonaGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "P",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when (currentScreen) {
                                        "dashboard" -> "PersonaAI"
                                        "profile" -> "Brand Profile"
                                        "bio_generator" -> "Bio Generator"
                                        "social_studio" -> "Social Studio"
                                        "interview_simulator" -> "Interview Simulator"
                                        "evaluator" -> "Brand Evaluation"
                                        "history" -> "Content History"
                                        "settings" -> "Settings"
                                        else -> "PersonaAI"
                                    },
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PersonaDarkOlive
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier.testTag("app_bar_menu_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Open menu",
                                    tint = PersonaDarkOlive
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = { currentScreen = "history" },
                                modifier = Modifier.testTag("top_bar_history_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = "History",
                                    tint = if (currentScreen == "history") PersonaGreen else PersonaDarkOlive
                                )
                            }
                            IconButton(
                                onClick = { currentScreen = "settings" },
                                modifier = Modifier.testTag("top_bar_settings_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = if (currentScreen == "settings") PersonaGreen else PersonaDarkOlive
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.White
                        )
                    )
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = PersonaDarkOlive,
                    tonalElevation = 4.dp
                ) {
                    val bottomItems = listOf(
                        NavItem("dashboard", "Dashboard", Icons.Default.Dashboard),
                        NavItem("profile", "Profile", Icons.Default.Person),
                        NavItem("bio_generator", "Bio AI", Icons.Default.AutoAwesome),
                        NavItem("social_studio", "Social", Icons.Default.Share),
                        NavItem("interview_simulator", "Interview", Icons.Default.QuestionAnswer)
                    )

                    bottomItems.forEach { item ->
                        val selected = currentScreen == item.id
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            selected = selected,
                            onClick = { currentScreen = item.id },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PersonaDarkOlive,
                                selectedTextColor = PersonaDarkOlive,
                                indicatorColor = PersonaLightLime.copy(alpha = 0.5f),
                                unselectedIconColor = PersonaMutedGray,
                                unselectedTextColor = PersonaMutedGray
                            ),
                            modifier = Modifier.testTag("bottom_nav_${item.id}")
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentScreen) {
                    "dashboard" -> DashboardScreen(
                        user = user,
                        profile = profile,
                        latestEvaluation = latestEvaluation,
                        recentBios = recentBios,
                        recentPosts = recentPosts,
                        sessionManager = sessionManager,
                        onNavigate = { currentScreen = it }
                    )

                    "profile" -> BrandProfileScreen(
                        userId = user.id,
                        profile = profile,
                        profileRepository = profileRepository,
                        sessionManager = sessionManager,
                        onNavigateToSettings = { currentScreen = "settings" }
                    )

                    "bio_generator" -> BioGeneratorScreen(
                        userId = user.id,
                        profile = profile,
                        savedBios = bios,
                        contentRepository = contentRepository,
                        sessionManager = sessionManager,
                        onNavigateToSettings = { currentScreen = "settings" }
                    )

                    "social_studio" -> SocialMediaStudioScreen(
                        userId = user.id,
                        profile = profile,
                        savedPosts = posts,
                        contentRepository = contentRepository,
                        sessionManager = sessionManager,
                        onNavigateToSettings = { currentScreen = "settings" }
                    )

                    "interview_simulator" -> InterviewSimulatorScreen(
                        userId = user.id,
                        profile = profile,
                        pastSessions = interviewSessions,
                        interviewRepository = interviewRepository,
                        sessionManager = sessionManager,
                        onNavigateToSettings = { currentScreen = "settings" }
                    )

                    "evaluator" -> BrandEvaluatorScreen(
                        userId = user.id,
                        profile = profile,
                        pastEvaluations = allEvaluations,
                        evaluationRepository = evaluationRepository,
                        sessionManager = sessionManager,
                        onNavigateToSettings = { currentScreen = "settings" }
                    )

                    "history" -> ContentHistoryScreen(
                        userId = user.id,
                        bios = bios,
                        posts = posts,
                        sessions = interviewSessions,
                        evaluations = allEvaluations,
                        contentRepository = contentRepository,
                        interviewRepository = interviewRepository,
                        evaluationRepository = evaluationRepository
                    )

                    "settings" -> SettingsScreen(
                        user = user,
                        userRepository = userRepository,
                        contentRepository = contentRepository,
                        sessionManager = sessionManager,
                        aiService = aiService,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}
