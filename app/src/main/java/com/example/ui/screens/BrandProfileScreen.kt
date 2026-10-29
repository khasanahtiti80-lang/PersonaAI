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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SessionManager
import com.example.data.local.entity.BrandProfileEntity
import com.example.data.repository.BrandProfileRepository
import com.example.ui.components.AiBadge
import com.example.ui.components.ApiKeyNoticeBanner
import com.example.ui.components.PersonaPrimaryButton
import com.example.ui.components.PersonaSecondaryButton
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BrandProfileScreen(
    userId: Long,
    profile: BrandProfileEntity?,
    profileRepository: BrandProfileRepository,
    sessionManager: SessionManager,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    var fullName by remember(profile) { mutableStateOf(profile?.fullName ?: "") }
    var education by remember(profile) { mutableStateOf(profile?.education ?: "") }
    var currentRole by remember(profile) { mutableStateOf(profile?.currentRole ?: "") }
    var skills by remember(profile) { mutableStateOf(profile?.skills ?: "") }
    var interests by remember(profile) { mutableStateOf(profile?.interests ?: "") }
    var experienceAndAchievements by remember(profile) { mutableStateOf(profile?.experienceAndAchievements ?: "") }
    var personalValues by remember(profile) { mutableStateOf(profile?.personalValues ?: "") }
    var careerGoals by remember(profile) { mutableStateOf(profile?.careerGoals ?: "") }
    var targetAudience by remember(profile) { mutableStateOf(profile?.targetAudience ?: "") }
    var communicationStyle by remember(profile) { mutableStateOf(profile?.communicationStyle ?: "Confident & Approachable") }
    var preferredPlatform by remember(profile) { mutableStateOf(profile?.preferredPlatform ?: "LinkedIn") }

    var isSaving by remember { mutableStateOf(false) }
    var isGenerating by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }
    var saveSuccessMsg by remember { mutableStateOf<String?>(null) }

    val communicationStyles = listOf(
        "Confident & Authoritative",
        "Warm & Approachable",
        "Thought-Leader & Visionary",
        "Storyteller & Empathetic",
        "Concise, Analytical & Direct"
    )

    val platforms = listOf("LinkedIn", "Instagram", "Substack", "X / Twitter", "Personal Website")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PersonaWarmOffWhite)
            .padding(horizontal = 20.dp)
            .testTag("brand_profile_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(6.dp)) }

        // Screen Header
        item {
            Column {
                Text(
                    text = "My Brand Profile",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PersonaDarkOlive
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Define your foundation. PersonaAI uses these facts to generate your authentic personal brand.",
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

        // Form Fields in Grouped Card
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
                        text = "1. Professional Identity",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = PersonaDarkOlive
                    )

                    ProfileTextField(
                        label = "Full or Professional Name",
                        value = fullName,
                        placeholder = "e.g. Maya Chen",
                        onValueChange = { fullName = it },
                        testTag = "input_full_name"
                    )

                    ProfileTextField(
                        label = "Current Role or Status",
                        value = currentRole,
                        placeholder = "e.g. Senior Product Designer | Aspiring AI Product Lead",
                        onValueChange = { currentRole = it },
                        testTag = "input_current_role"
                    )

                    ProfileTextField(
                        label = "Education & Field of Study",
                        value = education,
                        placeholder = "e.g. B.S. Human-Computer Interaction, University of Washington",
                        onValueChange = { education = it },
                        testTag = "input_education"
                    )
                }
            }
        }

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
                        text = "2. Skills, Passions & Achievements",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = PersonaDarkOlive
                    )

                    ProfileTextField(
                        label = "Core Skills & Competencies",
                        value = skills,
                        placeholder = "e.g. Figma, Design Systems, User Research, Product Strategy, Cross-team leadership",
                        onValueChange = { skills = it },
                        minLines = 2,
                        testTag = "input_skills"
                    )

                    ProfileTextField(
                        label = "Professional Passions & Interests",
                        value = interests,
                        placeholder = "e.g. Accessibility, EdTech, Climate Tech, Ethical AI UI",
                        onValueChange = { interests = it },
                        testTag = "input_interests"
                    )

                    ProfileTextField(
                        label = "Key Projects, Experience & Achievements",
                        value = experienceAndAchievements,
                        placeholder = "e.g. Led redesign of mobile banking app for 1.2M users; published design system with 98% adoption.",
                        onValueChange = { experienceAndAchievements = it },
                        minLines = 3,
                        testTag = "input_achievements"
                    )
                }
            }
        }

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
                        text = "3. Values, Goals & Audience",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = PersonaDarkOlive
                    )

                    ProfileTextField(
                        label = "Personal Core Values",
                        value = personalValues,
                        placeholder = "e.g. Transparency, User Empathy, Continuous Learning, Rigor",
                        onValueChange = { personalValues = it },
                        testTag = "input_values"
                    )

                    ProfileTextField(
                        label = "Target Career Goals",
                        value = careerGoals,
                        placeholder = "e.g. Land a Staff Product Designer role at a growth-stage company; speak at Config.",
                        onValueChange = { careerGoals = it },
                        testTag = "input_goals"
                    )

                    ProfileTextField(
                        label = "Target Audience",
                        value = targetAudience,
                        placeholder = "e.g. Hiring managers in tech, startup founders, junior designers seeking mentorship",
                        onValueChange = { targetAudience = it },
                        testTag = "input_audience"
                    )

                    // Communication Style Selector
                    Text(
                        text = "Communication Style",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PersonaDarkOlive
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        communicationStyles.forEach { style ->
                            FilterChip(
                                selected = communicationStyle == style,
                                onClick = { communicationStyle = style },
                                label = { Text(style, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PersonaLightLime,
                                    selectedLabelColor = PersonaDarkOlive
                                )
                            )
                        }
                    }

                    // Preferred Platform
                    Text(
                        text = "Primary Professional Platform",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PersonaDarkOlive
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        platforms.forEach { p ->
                            FilterChip(
                                selected = preferredPlatform == p,
                                onClick = { preferredPlatform = p },
                                label = { Text(p, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PersonaButterCream,
                                    selectedLabelColor = PersonaDarkOlive
                                )
                            )
                        }
                    }
                }
            }
        }

        // Action Buttons: Save Profile & Generate Brand Identity
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (errorText != null) {
                    Surface(
                        color = PersonaCoralPink.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorText ?: "",
                            color = Color(0xFF9E2A2B),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                if (saveSuccessMsg != null) {
                    Surface(
                        color = PersonaLightLime.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = saveSuccessMsg ?: "",
                            color = PersonaDarkOlive,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PersonaSecondaryButton(
                        text = "Save Profile",
                        icon = Icons.Default.Save,
                        onClick = {
                            scope.launch {
                                isSaving = true
                                errorText = null
                                val updated = (profile ?: BrandProfileEntity(userId = userId)).copy(
                                    fullName = fullName,
                                    education = education,
                                    currentRole = currentRole,
                                    skills = skills,
                                    interests = interests,
                                    experienceAndAchievements = experienceAndAchievements,
                                    personalValues = personalValues,
                                    careerGoals = careerGoals,
                                    targetAudience = targetAudience,
                                    communicationStyle = communicationStyle,
                                    preferredPlatform = preferredPlatform
                                )
                                profileRepository.saveProfile(updated)
                                isSaving = false
                                saveSuccessMsg = "Profile saved successfully!"
                            }
                        },
                        modifier = Modifier.weight(1f),
                        testTag = "save_profile_button"
                    )

                    PersonaPrimaryButton(
                        text = "Generate Brand Identity",
                        icon = Icons.Default.AutoAwesome,
                        loading = isGenerating,
                        onClick = {
                            scope.launch {
                                isGenerating = true
                                errorText = null
                                saveSuccessMsg = null
                                val current = (profile ?: BrandProfileEntity(userId = userId)).copy(
                                    fullName = fullName,
                                    education = education,
                                    currentRole = currentRole,
                                    skills = skills,
                                    interests = interests,
                                    experienceAndAchievements = experienceAndAchievements,
                                    personalValues = personalValues,
                                    careerGoals = careerGoals,
                                    targetAudience = targetAudience,
                                    communicationStyle = communicationStyle,
                                    preferredPlatform = preferredPlatform
                                )
                                val res = profileRepository.generateBrandIdentity(current)
                                isGenerating = false
                                res.onSuccess {
                                    saveSuccessMsg = "Brand Identity synthesized!"
                                }.onFailure { e ->
                                    errorText = e.localizedMessage ?: "Failed to generate identity."
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        testTag = "generate_identity_button"
                    )
                }
            }
        }

        // AI Generated Brand Architecture Showcase
        if (!profile?.professionalIdentity.isNullOrBlank() || !profile?.uniqueValueProposition.isNullOrBlank()) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PersonaGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Your Synthesized Brand Architecture",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = PersonaDarkOlive
                            )
                            AiBadge(label = "Verified AI")
                        }

                        // UVP Highlight
                        Surface(
                            color = PersonaCreamSurface,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PersonaButterCream),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "UNIQUE VALUE PROPOSITION (UVP)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PersonaDarkOlive,
                                        letterSpacing = 1.sp
                                    )
                                    IconButton(
                                        onClick = {
                                            clipboard.setText(AnnotatedString(profile?.uniqueValueProposition.orEmpty()))
                                            Toast.makeText(context, "UVP copied!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy UVP",
                                            tint = PersonaDarkOlive,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = profile?.uniqueValueProposition.orEmpty(),
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = PersonaDarkOlive
                                )
                            }
                        }

                        // Personal Branding Statement
                        if (!profile?.brandingStatement.isNullOrBlank()) {
                            InsightBlock(
                                title = "PERSONAL BRANDING STATEMENT",
                                body = profile.brandingStatement,
                                onCopy = {
                                    clipboard.setText(AnnotatedString(profile.brandingStatement))
                                    Toast.makeText(context, "Statement copied!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                        // Core Strengths
                        if (!profile?.coreStrengths.isNullOrBlank()) {
                            InsightBlock(
                                title = "CORE PERSONAL STRENGTHS",
                                body = profile.coreStrengths
                            )
                        }

                        // Key Differentiators
                        if (!profile?.keyDifferentiators.isNullOrBlank()) {
                            InsightBlock(
                                title = "KEY DIFFERENTIATORS",
                                body = profile.keyDifferentiators
                            )
                        }

                        // Recommendations
                        if (!profile?.developmentRecommendations.isNullOrBlank()) {
                            InsightBlock(
                                title = "PROFESSIONAL DEVELOPMENT RECOMMENDATIONS",
                                body = profile.developmentRecommendations
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
private fun ProfileTextField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    minLines: Int = 1,
    testTag: String = ""
) {
    Column {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = PersonaDarkOlive
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = PersonaMutedGray, fontSize = 13.sp) },
            minLines = minLines,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PersonaGreen,
                unfocusedBorderColor = PersonaBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
        )
    }
}

@Composable
private fun InsightBlock(
    title: String,
    body: String,
    onCopy: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PersonaWarmOffWhite, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = PersonaDarkOlive,
                letterSpacing = 1.sp
            )
            if (onCopy != null) {
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = PersonaDarkOlive,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = body,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color = PersonaCharcoal
        )
    }
}
