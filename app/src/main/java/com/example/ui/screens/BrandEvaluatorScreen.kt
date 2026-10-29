package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SessionManager
import com.example.data.local.entity.BrandEvaluationEntity
import com.example.data.local.entity.BrandProfileEntity
import com.example.data.repository.EvaluationRepository
import com.example.data.repository.SelfAssessmentAnswers
import com.example.ui.components.AiBadge
import com.example.ui.components.ApiKeyNoticeBanner
import com.example.ui.components.PersonaPrimaryButton
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

@Composable
fun BrandEvaluatorScreen(
    userId: Long,
    profile: BrandProfileEntity?,
    pastEvaluations: List<BrandEvaluationEntity>,
    evaluationRepository: EvaluationRepository,
    sessionManager: SessionManager,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    var onlinePresenceSlider by remember { mutableFloatStateOf(3f) }
    var consistencySlider by remember { mutableFloatStateOf(3f) }
    var engagementSlider by remember { mutableFloatStateOf(3f) }
    var portfolioSlider by remember { mutableFloatStateOf(3f) }

    var optionalBioOrPortfolioText by remember { mutableStateOf("") }

    var isEvaluating by remember { mutableStateOf(false) }
    var latestEvaluation by remember { mutableStateOf<BrandEvaluationEntity?>(pastEvaluations.firstOrNull()) }
    var errorText by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PersonaWarmOffWhite)
            .padding(horizontal = 20.dp)
            .testTag("brand_evaluator_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(6.dp)) }

        // Title
        item {
            Column {
                Text(
                    text = "Personal Brand Evaluator",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PersonaDarkOlive
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Evaluate your brand across 7 critical dimensions and unlock a practical 7-day action plan.",
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

        // Self-Assessment Questionnaire Card
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
                        text = "1. Self-Assessment Questionnaire",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = PersonaDarkOlive
                    )

                    AssessmentSlider(
                        title = "Online Presence & Activity",
                        description = "How active and consistent is your professional footprint?",
                        value = onlinePresenceSlider,
                        onValueChange = { onlinePresenceSlider = it }
                    )

                    AssessmentSlider(
                        title = "Messaging & Value Consistency",
                        description = "Do your profiles deliver a coherent, singular message?",
                        value = consistencySlider,
                        onValueChange = { consistencySlider = it }
                    )

                    AssessmentSlider(
                        title = "Target Audience Engagement",
                        description = "How regularly do you engage and share value with your peers?",
                        value = engagementSlider,
                        onValueChange = { engagementSlider = it }
                    )

                    AssessmentSlider(
                        title = "Portfolio & Evidence Clarity",
                        description = "How tangible are the project artifacts & achievements you share?",
                        value = portfolioSlider,
                        onValueChange = { portfolioSlider = it }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "2. Supplemental Text for Audit (Optional)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = PersonaDarkOlive
                    )

                    OutlinedTextField(
                        value = optionalBioOrPortfolioText,
                        onValueChange = { optionalBioOrPortfolioText = it },
                        placeholder = { Text("Paste your current LinkedIn About, resume summary, or portfolio intro for AI inspection...", fontSize = 13.sp, color = PersonaMutedGray) },
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PersonaGreen,
                            unfocusedBorderColor = PersonaBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("evaluator_text_input")
                    )

                    if (errorText != null) {
                        Surface(
                            color = PersonaCoralPink.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = errorText ?: "",
                                color = Color(0xFF9E2A2B),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    PersonaPrimaryButton(
                        text = "Run AI Brand Audit",
                        icon = Icons.Default.TrendingUp,
                        loading = isEvaluating,
                        onClick = {
                            scope.launch {
                                isEvaluating = true
                                errorText = null
                                val answers = SelfAssessmentAnswers(
                                    onlinePresenceScore = onlinePresenceSlider.toInt(),
                                    messageConsistencyScore = consistencySlider.toInt(),
                                    networkEngagementScore = engagementSlider.toInt(),
                                    portfolioClarityScore = portfolioSlider.toInt()
                                )
                                val res = evaluationRepository.evaluateBrand(
                                    userId = userId,
                                    profile = profile ?: BrandProfileEntity(userId = userId),
                                    selfAssessment = answers,
                                    optionalText = optionalBioOrPortfolioText
                                )
                                isEvaluating = false
                                res.onSuccess { eval ->
                                    latestEvaluation = eval
                                    Toast.makeText(context, "Evaluation complete!", Toast.LENGTH_SHORT).show()
                                }.onFailure { e ->
                                    errorText = e.localizedMessage ?: "Evaluation failed."
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "run_audit_button"
                    )
                }
            }
        }

        // EVALUATION RESULTS
        if (latestEvaluation != null) {
            val eval = latestEvaluation!!

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PersonaGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Brand Audit Results",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = PersonaDarkOlive
                            )
                            AiBadge(label = "7 Dimensions")
                        }

                        // Overall Gauge & Summary
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ScoreCircularGauge(
                                score = eval.overallScore,
                                label = "Overall",
                                size = 80.dp
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = eval.summaryFeedback,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    color = PersonaCharcoal
                                )
                            }
                        }

                        HorizontalDivider(color = PersonaBorder)

                        // 7 Dimension Score Breakdown
                        Text(
                            text = "7 DIMENSION SCORE BREAKDOWN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PersonaDarkOlive,
                            letterSpacing = 1.sp
                        )

                        DimensionScoreBar(name = "1. Identity Clarity", score = eval.clarityScore)
                        DimensionScoreBar(name = "2. Message Consistency", score = eval.consistencyScore)
                        DimensionScoreBar(name = "3. Unique Value Proposition", score = eval.uvpScore)
                        DimensionScoreBar(name = "4. Target Relevance", score = eval.relevanceScore)
                        DimensionScoreBar(name = "5. Communication Quality", score = eval.communicationScore)
                        DimensionScoreBar(name = "6. Evidence & Achievements", score = eval.evidenceScore)
                        DimensionScoreBar(name = "7. Goal Alignment", score = eval.alignmentScore)

                        HorizontalDivider(color = PersonaBorder)

                        // Strengths & Areas for Improvement
                        Surface(
                            color = PersonaLightLime.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "🌟 IDENTIFIED STRENGTHS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PersonaDarkOlive,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = eval.strengths,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    color = PersonaCharcoal
                                )
                            }
                        }

                        Surface(
                            color = PersonaAiBadgeBg,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "⚠️ AREAS REQUIRING IMPROVEMENT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PersonaDarkOlive,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = eval.areasForImprovement,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    color = PersonaCharcoal
                                )
                            }
                        }

                        // Prioritized Recommendations
                        Surface(
                            color = PersonaCreamSurface,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PersonaButterCream),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "⚡ PRIORITIZED RECOMMENDATIONS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PersonaDarkOlive,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = eval.prioritizedRecommendations,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    color = PersonaDarkOlive
                                )
                            }
                        }

                        // 7-Day Action Plan
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(PersonaWarmOffWhite, RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📅 PRACTICAL 7-DAY IMPROVEMENT PLAN",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PersonaDarkOlive,
                                    letterSpacing = 1.sp
                                )
                                IconButton(
                                    onClick = {
                                        clipboard.setText(AnnotatedString(eval.sevenDayPlanJson))
                                        Toast.makeText(context, "Plan copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Plan",
                                        tint = PersonaDarkOlive,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Parse JSON array of 7 days if valid, else display string
                            val planItems = remember(eval.sevenDayPlanJson) {
                                try {
                                    val arr = JSONArray(eval.sevenDayPlanJson)
                                    (0 until arr.length()).map { arr.getString(it) }
                                } catch (_: Exception) {
                                    eval.sevenDayPlanJson.lines().filter { it.isNotBlank() }
                                }
                            }

                            planItems.forEach { dayStep ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(PersonaGreen)
                                            .padding(top = 4.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = dayStep,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp,
                                        color = PersonaCharcoal
                                    )
                                }
                            }
                        }

                        // Legal / Guidance Disclaimer
                        TipCard(
                            title = "AI Self-Assessment Notice",
                            message = "This evaluation is an AI-assisted self-assessment based strictly on your submitted info to guide personal growth. It is not an objective measurement of employability or guarantee of career outcomes.",
                            icon = Icons.Default.Info
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
private fun AssessmentSlider(
    title: String,
    description: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = PersonaDarkOlive
            )
            Surface(
                color = PersonaCreamSurface,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "${value.toInt()} / 5",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PersonaDarkOlive,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
        Text(
            text = description,
            fontSize = 11.sp,
            color = PersonaMutedGray
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 1f..5f,
            steps = 3,
            colors = SliderDefaults.colors(
                thumbColor = PersonaGreen,
                activeTrackColor = PersonaGreen,
                inactiveTrackColor = PersonaBorder
            )
        )
    }
}

@Composable
private fun DimensionScoreBar(
    name: String,
    score: Int
) {
    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = PersonaCharcoal
            )
            Text(
                text = "$score/100",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = when {
                    score >= 80 -> PersonaGreen
                    score >= 60 -> Color(0xFFD6A828)
                    else -> PersonaCoralPink
                }
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { score / 100f },
            color = when {
                score >= 80 -> PersonaGreen
                score >= 60 -> PersonaButterCream
                else -> PersonaCoralPink
            },
            trackColor = PersonaWarmOffWhite,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
        )
    }
}
