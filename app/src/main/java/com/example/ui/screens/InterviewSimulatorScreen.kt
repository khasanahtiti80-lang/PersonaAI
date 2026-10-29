package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SessionManager
import com.example.data.local.entity.BrandProfileEntity
import com.example.data.local.entity.InterviewSessionEntity
import com.example.data.repository.InterviewRepository
import com.example.data.repository.InterviewTurn
import com.example.ui.components.AiBadge
import com.example.ui.components.ApiKeyNoticeBanner
import com.example.ui.components.PersonaPrimaryButton
import com.example.ui.components.PersonaSecondaryButton
import com.example.ui.components.ScoreCircularGauge
import com.example.ui.components.TipCard
import com.example.ui.theme.PersonaAiBadgeBg
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
import org.json.JSONArray
import org.json.JSONObject

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InterviewSimulatorScreen(
    userId: Long,
    profile: BrandProfileEntity?,
    pastSessions: List<InterviewSessionEntity>,
    interviewRepository: InterviewRepository,
    sessionManager: SessionManager,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val interviewTypes = listOf(
        "Internship",
        "First Job",
        "Scholarship",
        "Organizational Position",
        "General Professional"
    )
    val difficulties = listOf("Entry", "Intermediate", "Advanced")
    val focuses = listOf("Communication", "Competencies", "Experience", "Behavioral (STAR)")

    var selectedType by remember { mutableStateOf(interviewTypes[0]) }
    var targetRole by remember(profile) { mutableStateOf(profile?.currentRole?.ifBlank { "Software Engineer" } ?: "Software Engineer") }
    var selectedDifficulty by remember { mutableStateOf(difficulties[1]) }
    var selectedFocus by remember { mutableStateOf(focuses[3]) }

    // Simulation State: 0 = Setup, 1 = In Progress Question, 2 = Feedback on Current Question, 3 = Session Completed Summary
    var simulationState by remember { mutableIntStateOf(0) }
    val totalQuestions = 4
    var currentQuestionNumber by remember { mutableIntStateOf(1) }
    var currentQuestionText by remember { mutableStateOf("") }
    var userAnswerText by remember { mutableStateOf("") }
    var currentTurnFeedback by remember { mutableStateOf<InterviewTurn?>(null) }
    val completedTurns = remember { mutableStateListOf<InterviewTurn>() }

    var nextQuestionText by remember { mutableStateOf<String?>(null) }
    var overallSessionSummary by remember { mutableStateOf<Triple<Int, String, String>?>(null) }

    var isAiLoading by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PersonaWarmOffWhite)
            .padding(horizontal = 20.dp)
            .testTag("interview_simulator_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(6.dp)) }

        // Screen Title
        item {
            Column {
                Text(
                    text = "AI Interview Simulator",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PersonaDarkOlive
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Practice real interview questions one by one with targeted STAR feedback and zero fake claims.",
                    fontSize = 13.sp,
                    color = PersonaMutedGray
                )
            }
        }

        item {
            ApiKeyNoticeBanner(
                hasKey = sessionManager.hasApiKey(),
                onConfigureClick = onNavigateToSettings
            )
        }

        if (simulationState == 0) {
            // STEP 0: INTERVIEW SETUP
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
                        Text(
                            text = "Configure Your Simulation",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = PersonaDarkOlive
                        )

                        // Target Role
                        OutlinedTextField(
                            value = targetRole,
                            onValueChange = { targetRole = it },
                            label = { Text("Target Position / Opportunity") },
                            placeholder = { Text("e.g. Product Manager, Fulbright Scholar, Junior Developer") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PersonaGreen,
                                unfocusedBorderColor = PersonaBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("interview_role_input")
                        )

                        // Interview Type
                        Text(text = "Interview Type", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PersonaDarkOlive)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            interviewTypes.forEach { type ->
                                FilterChip(
                                    selected = selectedType == type,
                                    onClick = { selectedType = type },
                                    label = { Text(type, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PersonaLightLime,
                                        selectedLabelColor = PersonaDarkOlive
                                    )
                                )
                            }
                        }

                        // Difficulty Level
                        Text(text = "Difficulty Level", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PersonaDarkOlive)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            difficulties.forEach { diff ->
                                FilterChip(
                                    selected = selectedDifficulty == diff,
                                    onClick = { selectedDifficulty = diff },
                                    label = { Text(diff, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PersonaButterCream,
                                        selectedLabelColor = PersonaDarkOlive
                                    )
                                )
                            }
                        }

                        // Focus
                        Text(text = "Question Focus", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PersonaDarkOlive)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            focuses.forEach { f ->
                                FilterChip(
                                    selected = selectedFocus == f,
                                    onClick = { selectedFocus = f },
                                    label = { Text(f, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PersonaAiBadgeBg,
                                        selectedLabelColor = PersonaDarkOlive
                                    )
                                )
                            }
                        }

                        if (errorText != null) {
                            Text(
                                text = errorText ?: "",
                                color = Color(0xFF9E2A2B),
                                fontSize = 12.sp
                            )
                        }

                        PersonaPrimaryButton(
                            text = "Start Live Simulation",
                            icon = Icons.Default.PlayArrow,
                            loading = isAiLoading,
                            onClick = {
                                scope.launch {
                                    isAiLoading = true
                                    errorText = null
                                    completedTurns.clear()
                                    currentQuestionNumber = 1
                                    userAnswerText = ""

                                    val res = interviewRepository.generateFirstQuestion(
                                        profile = profile ?: BrandProfileEntity(userId = userId),
                                        interviewType = selectedType,
                                        targetRole = targetRole,
                                        difficulty = selectedDifficulty,
                                        focus = selectedFocus,
                                        language = "English"
                                    )
                                    isAiLoading = false
                                    res.onSuccess { q ->
                                        currentQuestionText = q
                                        simulationState = 1
                                    }.onFailure { e ->
                                        errorText = e.localizedMessage ?: "Failed to initialize interview."
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "start_interview_button"
                        )
                    }
                }
            }

            // Past Interview Sessions History
            if (pastSessions.isNotEmpty()) {
                item {
                    Text(
                        text = "Past Practice Sessions (${pastSessions.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = PersonaDarkOlive
                    )
                }

                items(pastSessions, key = { it.id }) { session ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PersonaBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = session.targetRole,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = PersonaDarkOlive
                                )
                                Text(
                                    text = "${session.interviewType} • ${session.focus}",
                                    fontSize = 12.sp,
                                    color = PersonaMutedGray
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = session.overallFeedback.take(90) + "...",
                                    fontSize = 11.sp,
                                    color = PersonaCharcoal
                                )
                            }
                            ScoreCircularGauge(
                                score = session.overallScore,
                                label = "Score",
                                size = 52.dp
                            )
                        }
                    }
                }
            }
        } else if (simulationState == 1 || simulationState == 2) {
            // STEP 1 or 2: IN PROGRESS (Question & Answer / Feedback)
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
                        // Progress Counter
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Question $currentQuestionNumber of $totalQuestions",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = PersonaDarkOlive
                            )
                            AiBadge(label = "$selectedDifficulty • $selectedFocus")
                        }

                        LinearProgressIndicator(
                            progress = { currentQuestionNumber / totalQuestions.toFloat() },
                            color = PersonaGreen,
                            trackColor = PersonaWarmOffWhite,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )

                        // The Question Bubble
                        Surface(
                            color = PersonaCreamSurface,
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PersonaButterCream),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "INTERVIEWER QUESTION:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PersonaDarkOlive,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = currentQuestionText,
                                    fontSize = 15.sp,
                                    lineHeight = 21.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = PersonaDarkOlive
                                )
                            }
                        }

                        if (simulationState == 1) {
                            // User Answer Input Box
                            OutlinedTextField(
                                value = userAnswerText,
                                onValueChange = { userAnswerText = it },
                                label = { Text("Your Spoken / Written Answer") },
                                placeholder = { Text("Type your answer clearly. Structure with Situation, Task, Action, and Result...") },
                                minLines = 5,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PersonaGreen,
                                    unfocusedBorderColor = PersonaBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("interview_answer_input")
                            )

                            if (errorText != null) {
                                Text(
                                    text = errorText ?: "",
                                    color = Color(0xFF9E2A2B),
                                    fontSize = 12.sp
                                )
                            }

                            PersonaPrimaryButton(
                                text = "Submit Answer for AI Evaluation",
                                icon = Icons.Default.Send,
                                loading = isAiLoading,
                                onClick = {
                                    if (userAnswerText.isBlank()) {
                                        errorText = "Please write your answer before submitting."
                                        return@PersonaPrimaryButton
                                    }
                                    scope.launch {
                                        isAiLoading = true
                                        errorText = null
                                        val res = interviewRepository.evaluateAnswerAndGetNextQuestion(
                                            profile = profile ?: BrandProfileEntity(userId = userId),
                                            interviewType = selectedType,
                                            targetRole = targetRole,
                                            focus = selectedFocus,
                                            questionIndex = currentQuestionNumber,
                                            totalQuestions = totalQuestions,
                                            currentQuestion = currentQuestionText,
                                            userAnswer = userAnswerText
                                        )
                                        isAiLoading = false
                                        res.onSuccess { (turn, nextQ) ->
                                            currentTurnFeedback = turn
                                            completedTurns.add(turn)
                                            nextQuestionText = nextQ
                                            simulationState = 2
                                        }.onFailure { e ->
                                            errorText = e.localizedMessage ?: "Evaluation failed."
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "submit_answer_button"
                            )
                        } else {
                            // SIMULATION STATE 2: SHOW FEEDBACK ON THIS ANSWER
                            val turn = currentTurnFeedback
                            if (turn != null) {
                                Surface(
                                    color = PersonaWarmOffWhite,
                                    shape = RoundedCornerShape(14.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, PersonaBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "AI FEEDBACK & SCORING",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = PersonaDarkOlive,
                                                letterSpacing = 1.sp
                                            )
                                            Surface(
                                                color = if (turn.score >= 80) PersonaLightLime else PersonaButterCream,
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = "${turn.score}/100",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    color = PersonaDarkOlive,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = turn.feedback,
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp,
                                            color = PersonaCharcoal
                                        )

                                        if (turn.suggestedImprovement.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Surface(
                                                color = Color.White,
                                                shape = RoundedCornerShape(10.dp),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, PersonaLightLime),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.padding(10.dp)) {
                                                    Text(
                                                        text = "💡 Suggested Refinement (keeping your real experience):",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = PersonaDarkOlive
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = turn.suggestedImprovement,
                                                        fontSize = 12.sp,
                                                        lineHeight = 17.sp,
                                                        color = PersonaCharcoal
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                PersonaPrimaryButton(
                                    text = if (currentQuestionNumber < totalQuestions) "Next Question →" else "Finish & View Overall Evaluation",
                                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                                    loading = isAiLoading,
                                    onClick = {
                                        if (currentQuestionNumber < totalQuestions && !nextQuestionText.isNullOrBlank()) {
                                            currentQuestionNumber++
                                            currentQuestionText = nextQuestionText!!
                                            userAnswerText = ""
                                            currentTurnFeedback = null
                                            simulationState = 1
                                        } else {
                                            // Finish session & compute overall evaluation
                                            scope.launch {
                                                isAiLoading = true
                                                val summaryRes = interviewRepository.generateFinalSummary(
                                                    targetRole = targetRole,
                                                    interviewType = selectedType,
                                                    turns = completedTurns.toList()
                                                )
                                                isAiLoading = false
                                                summaryRes.onSuccess { triple ->
                                                    overallSessionSummary = triple
                                                    simulationState = 3

                                                    // Save session to local database
                                                    val questionsJson = JSONArray().apply {
                                                        completedTurns.forEach { t ->
                                                            put(
                                                                JSONObject().apply {
                                                                    put("question", t.questionText)
                                                                    put("answer", t.userAnswer)
                                                                    put("score", t.score)
                                                                    put("feedback", t.feedback)
                                                                }
                                                            )
                                                        }
                                                    }.toString()

                                                    interviewRepository.saveSession(
                                                        InterviewSessionEntity(
                                                            userId = userId,
                                                            interviewType = selectedType,
                                                            targetRole = targetRole,
                                                            difficulty = selectedDifficulty,
                                                            focus = selectedFocus,
                                                            questionsAndAnswersJson = questionsJson,
                                                            overallScore = triple.first,
                                                            overallFeedback = triple.second,
                                                            improvementPlan = triple.third,
                                                            isCompleted = true
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    testTag = "next_question_button"
                                )
                            }
                        }
                    }
                }
            }
        } else if (simulationState == 3) {
            // STEP 3: SESSION COMPLETED SUMMARY
            item {
                val summary = overallSessionSummary
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PersonaGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AiBadge(label = "Simulation Completed")

                        Text(
                            text = "Interview Performance Summary",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PersonaDarkOlive
                        )

                        ScoreCircularGauge(
                            score = summary?.first ?: 80,
                            label = "Overall Score",
                            size = 85.dp
                        )

                        // Evaluation breakdown
                        Surface(
                            color = PersonaWarmOffWhite,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "EVALUATION SUMMARY",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = PersonaDarkOlive,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = summary?.second ?: "",
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    color = PersonaCharcoal
                                )
                            }
                        }

                        // Actionable Improvement Plan
                        Surface(
                            color = PersonaCreamSurface,
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PersonaButterCream),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "🎯 PRIORITIZED IMPROVEMENT PLAN",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = PersonaDarkOlive,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = summary?.third ?: "",
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    color = PersonaDarkOlive
                                )
                            }
                        }

                        PersonaPrimaryButton(
                            text = "Start Another Practice Session",
                            icon = Icons.Default.Refresh,
                            onClick = { simulationState = 0 },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "restart_interview_button"
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}
