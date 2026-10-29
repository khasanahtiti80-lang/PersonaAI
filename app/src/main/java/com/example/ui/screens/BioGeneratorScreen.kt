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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.local.entity.GeneratedBioEntity
import com.example.data.repository.ContentRepository
import com.example.ui.components.AiBadge
import com.example.ui.components.ApiKeyNoticeBanner
import com.example.ui.components.PersonaPrimaryButton
import com.example.ui.components.PersonaSecondaryButton
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BioGeneratorScreen(
    userId: Long,
    profile: BrandProfileEntity?,
    savedBios: List<GeneratedBioEntity>,
    contentRepository: ContentRepository,
    sessionManager: SessionManager,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    val bioTypes = listOf(
        "LinkedIn About",
        "Short Bio",
        "Professional Biography",
        "Elevator Pitch",
        "Personal Tagline",
        "Instagram Bio",
        "Portfolio Intro",
        "CV Profile Summary"
    )

    val platforms = listOf("LinkedIn", "Instagram", "Portfolio", "Resume / CV", "X / Twitter")
    val tones = listOf("Professional", "Inspiring", "Casual & Warm", "Authoritative", "Storyteller")
    val lengths = listOf("Short (~50w)", "Medium (~150w)", "Comprehensive (~300w)")

    var selectedBioType by remember { mutableStateOf(bioTypes[0]) }
    var selectedPlatform by remember { mutableStateOf(platforms[0]) }
    var selectedTone by remember { mutableStateOf(tones[0]) }
    var selectedLength by remember { mutableStateOf(lengths[1]) }
    var customGoal by remember { mutableStateOf("") }

    var generatedText by remember { mutableStateOf("") }
    var draftTitle by remember { mutableStateOf("My ${bioTypes[0]}") }
    var isGenerating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successNotice by remember { mutableStateOf<String?>(null) }

    // Delete confirmation dialog
    var bioToDelete by remember { mutableStateOf<GeneratedBioEntity?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PersonaWarmOffWhite)
            .padding(horizontal = 20.dp)
            .testTag("bio_generator_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(6.dp)) }

        item {
            Column {
                Text(
                    text = "AI Profile & Bio Generator",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PersonaDarkOlive
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Generate compelling, tailored bios, LinkedIn summaries, and elevator pitches grounded in your profile.",
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

        // Generator Config Card
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
                    // Bio Type
                    Text(
                        text = "Select Content Type",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = PersonaDarkOlive
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        bioTypes.forEach { type ->
                            FilterChip(
                                selected = selectedBioType == type,
                                onClick = {
                                    selectedBioType = type
                                    draftTitle = "My $type"
                                },
                                label = { Text(type, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PersonaLightLime,
                                    selectedLabelColor = PersonaDarkOlive
                                )
                            )
                        }
                    }

                    // Platform & Tone
                    Text(
                        text = "Target Platform & Tone",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = PersonaDarkOlive
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        platforms.forEach { p ->
                            FilterChip(
                                selected = selectedPlatform == p,
                                onClick = { selectedPlatform = p },
                                label = { Text(p, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PersonaButterCream,
                                    selectedLabelColor = PersonaDarkOlive
                                )
                            )
                        }
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        tones.forEach { t ->
                            FilterChip(
                                selected = selectedTone == t,
                                onClick = { selectedTone = t },
                                label = { Text(t, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PersonaAiBadgeBg,
                                    selectedLabelColor = PersonaDarkOlive
                                )
                            )
                        }
                    }

                    // Length
                    Text(
                        text = "Desired Length",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = PersonaDarkOlive
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        lengths.forEach { l ->
                            FilterChip(
                                selected = selectedLength == l,
                                onClick = { selectedLength = l },
                                label = { Text(l, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PersonaGreen.copy(alpha = 0.2f),
                                    selectedLabelColor = PersonaDarkOlive
                                )
                            )
                        }
                    }

                    // Optional Custom Goal
                    OutlinedTextField(
                        value = customGoal,
                        onValueChange = { customGoal = it },
                        label = { Text("Special Instructions / Career Goal (Optional)") },
                        placeholder = { Text("e.g. Focus heavily on EdTech leadership and user growth metrics") },
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PersonaGreen,
                            unfocusedBorderColor = PersonaBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorMessage != null) {
                        Surface(
                            color = PersonaCoralPink.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                color = Color(0xFF9E2A2B),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    PersonaPrimaryButton(
                        text = "Generate Bio with AI",
                        icon = Icons.Default.AutoAwesome,
                        loading = isGenerating,
                        onClick = {
                            scope.launch {
                                isGenerating = true
                                errorMessage = null
                                successNotice = null
                                val currentProfile = profile ?: BrandProfileEntity(userId = userId)
                                val result = contentRepository.generateBio(
                                    userId = userId,
                                    profile = currentProfile,
                                    bioType = selectedBioType,
                                    platform = selectedPlatform,
                                    tone = selectedTone,
                                    length = selectedLength,
                                    customInstruction = customGoal
                                )
                                isGenerating = false
                                result.onSuccess { text ->
                                    generatedText = text
                                    draftTitle = "My $selectedBioType ($selectedPlatform)"
                                }.onFailure { e ->
                                    errorMessage = e.localizedMessage ?: "Generation failed."
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "generate_bio_button"
                    )
                }
            }
        }

        // Generated Result Showcase & Editor
        if (generatedText.isNotBlank()) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PersonaGreen),
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
                                AiBadge(label = selectedBioType)
                            }
                            Row {
                                IconButton(
                                    onClick = {
                                        clipboard.setText(AnnotatedString(generatedText))
                                        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy text",
                                        tint = PersonaDarkOlive
                                    )
                                }
                            }
                        }

                        // Editable Title
                        OutlinedTextField(
                            value = draftTitle,
                            onValueChange = { draftTitle = it },
                            label = { Text("Draft Title") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PersonaGreen,
                                unfocusedBorderColor = PersonaBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Editable Bio Content
                        OutlinedTextField(
                            value = generatedText,
                            onValueChange = { generatedText = it },
                            label = { Text("Generated Bio (Freely Editable)") },
                            minLines = 5,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PersonaGreen,
                                unfocusedBorderColor = PersonaBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (successNotice != null) {
                            Text(
                                text = successNotice ?: "",
                                color = PersonaDarkOlive,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            PersonaPrimaryButton(
                                text = "Save Draft",
                                icon = Icons.Default.Save,
                                onClick = {
                                    scope.launch {
                                        contentRepository.saveBio(
                                            GeneratedBioEntity(
                                                userId = userId,
                                                title = draftTitle,
                                                bioType = selectedBioType,
                                                platform = selectedPlatform,
                                                tone = selectedTone,
                                                length = selectedLength,
                                                content = generatedText
                                            )
                                        )
                                        successNotice = "Saved to Content History!"
                                        Toast.makeText(context, "Draft saved!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                testTag = "save_bio_draft_button"
                            )

                            PersonaSecondaryButton(
                                text = "Regenerate",
                                icon = Icons.Default.Refresh,
                                onClick = {
                                    scope.launch {
                                        isGenerating = true
                                        val res = contentRepository.generateBio(
                                            userId = userId,
                                            profile = profile ?: BrandProfileEntity(userId = userId),
                                            bioType = selectedBioType,
                                            platform = selectedPlatform,
                                            tone = selectedTone,
                                            length = selectedLength,
                                            customInstruction = customGoal
                                        )
                                        isGenerating = false
                                        res.onSuccess { text ->
                                            generatedText = text
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                testTag = "regenerate_bio_button"
                            )
                        }
                    }
                }
            }
        }

        // Saved Bios List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saved Bio Drafts (${savedBios.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = PersonaDarkOlive
                )
            }
        }

        if (savedBios.isEmpty()) {
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PersonaBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No saved drafts yet. Generate your first bio above!",
                        fontSize = 13.sp,
                        color = PersonaMutedGray,
                        modifier = Modifier.padding(18.dp)
                    )
                }
            }
        } else {
            items(savedBios, key = { it.id }) { bio ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PersonaBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AiBadge(label = "${bio.platform} • ${bio.bioType}")
                            Row {
                                IconButton(
                                    onClick = {
                                        clipboard.setText(AnnotatedString(bio.content))
                                        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = PersonaDarkOlive,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = {
                                        generatedText = bio.content
                                        draftTitle = bio.title
                                        selectedBioType = bio.bioType
                                        selectedPlatform = bio.platform
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = PersonaGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = { bioToDelete = bio },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color(0xFFC94A4A),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = bio.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = PersonaDarkOlive
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = bio.content,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = PersonaCharcoal
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    // Delete confirmation dialog
    if (bioToDelete != null) {
        AlertDialog(
            onDismissRequest = { bioToDelete = null },
            title = { Text("Delete Bio Draft") },
            text = { Text("Are you sure you want to delete '${bioToDelete?.title}'? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val item = bioToDelete
                        if (item != null) {
                            scope.launch {
                                contentRepository.deleteBio(item.id, userId)
                                bioToDelete = null
                                Toast.makeText(context, "Draft deleted", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                ) {
                    Text("Delete", color = Color(0xFFC94A4A), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { bioToDelete = null }) {
                    Text("Cancel", color = PersonaCharcoal)
                }
            }
        )
    }
}
