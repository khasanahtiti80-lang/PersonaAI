package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SessionManager
import com.example.data.local.entity.BrandProfileEntity
import com.example.data.local.entity.SocialMediaPostEntity
import com.example.data.repository.ContentRepository
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SocialMediaStudioScreen(
    userId: Long,
    profile: BrandProfileEntity?,
    savedPosts: List<SocialMediaPostEntity>,
    contentRepository: ContentRepository,
    sessionManager: SessionManager,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    var viewModeTab by remember { mutableIntStateOf(0) } // 0: Generator & List, 1: Weekly Calendar

    val contentCategories = listOf(
        "Educational",
        "Professional Tips",
        "Career Journey Story",
        "Portfolio Showcase",
        "Achievement Announcement",
        "Industry Insights",
        "Engagement Post",
        "Short-Form Video Script"
    )

    val platforms = listOf("LinkedIn", "X / Twitter", "Instagram", "Threads", "YouTube Shorts")
    val objectives = listOf("Build Authority", "Drive Engagement", "Attract Opportunities", "Educate & Inspire")
    val tones = listOf("Actionable & Direct", "Reflective & Human", "Authoritative", "Story-driven")
    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")

    var selectedPlatform by remember { mutableStateOf(platforms[0]) }
    var selectedCategory by remember { mutableStateOf(contentCategories[0]) }
    var selectedObjective by remember { mutableStateOf(objectives[0]) }
    var selectedTone by remember { mutableStateOf(tones[0]) }
    var selectedDay by remember { mutableStateOf(daysOfWeek[0]) }
    var topicInput by remember { mutableStateOf("") }

    var isGeneratingPost by remember { mutableStateOf(false) }
    var isGeneratingCalendar by remember { mutableStateOf(false) }
    var generatedPost by remember { mutableStateOf<SocialMediaPostEntity?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var postToDelete by remember { mutableStateOf<SocialMediaPostEntity?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PersonaWarmOffWhite)
            .padding(horizontal = 20.dp)
            .testTag("social_media_studio_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(6.dp)) }

        // Header
        item {
            Column {
                Text(
                    text = "AI Social Media Studio",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PersonaDarkOlive
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Craft high-engagement posts, hooks, carousels, video scripts, and structured calendars.",
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

        // View Mode Toggle (Generator & List vs Calendar View)
        item {
            TabRow(
                selectedTabIndex = viewModeTab,
                containerColor = Color.White,
                contentColor = PersonaDarkOlive,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[viewModeTab]),
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
                    selected = viewModeTab == 0,
                    onClick = { viewModeTab = 0 },
                    modifier = Modifier.height(48.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = null,
                            tint = if (viewModeTab == 0) PersonaGreen else PersonaMutedGray,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Post Creator & List",
                            fontWeight = if (viewModeTab == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp,
                            color = if (viewModeTab == 0) PersonaDarkOlive else PersonaMutedGray
                        )
                    }
                }

                Tab(
                    selected = viewModeTab == 1,
                    onClick = { viewModeTab = 1 },
                    modifier = Modifier.height(48.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = if (viewModeTab == 1) PersonaGreen else PersonaMutedGray,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Content Calendar",
                            fontWeight = if (viewModeTab == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp,
                            color = if (viewModeTab == 1) PersonaDarkOlive else PersonaMutedGray
                        )
                    }
                }
            }
        }

        if (viewModeTab == 0) {
            // POST CREATOR VIEW
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
                            text = "1. Content Parameters",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = PersonaDarkOlive
                        )

                        // Platform
                        Text(text = "Platform", fontSize = 12.sp, color = PersonaMutedGray)
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
                                        selectedContainerColor = PersonaLightLime,
                                        selectedLabelColor = PersonaDarkOlive
                                    )
                                )
                            }
                        }

                        // Content Type
                        Text(text = "Content Type", fontSize = 12.sp, color = PersonaMutedGray)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            contentCategories.forEach { cat ->
                                FilterChip(
                                    selected = selectedCategory == cat,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(cat, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PersonaAiBadgeBg,
                                        selectedLabelColor = PersonaDarkOlive
                                    )
                                )
                            }
                        }

                        // Objective & Scheduled Day
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Objective", fontSize = 12.sp, color = PersonaMutedGray)
                                Spacer(modifier = Modifier.height(4.dp))
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    objectives.take(2).forEach { o ->
                                        FilterChip(
                                            selected = selectedObjective == o,
                                            onClick = { selectedObjective = o },
                                            label = { Text(o, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = PersonaButterCream,
                                                selectedLabelColor = PersonaDarkOlive
                                            )
                                        )
                                    }
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Scheduled Day", fontSize = 12.sp, color = PersonaMutedGray)
                                Spacer(modifier = Modifier.height(4.dp))
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    daysOfWeek.take(3).forEach { d ->
                                        FilterChip(
                                            selected = selectedDay == d,
                                            onClick = { selectedDay = d },
                                            label = { Text(d, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = PersonaCreamSurface,
                                                selectedLabelColor = PersonaDarkOlive
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // Topic
                        OutlinedTextField(
                            value = topicInput,
                            onValueChange = { topicInput = it },
                            label = { Text("Topic or Theme") },
                            placeholder = { Text("e.g. 3 lessons learned scaling a design system from 0 to 1") },
                            minLines = 2,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PersonaGreen,
                                unfocusedBorderColor = PersonaBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_social_topic")
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
                            text = "Generate Post with Hook & CTA",
                            icon = Icons.Default.AutoAwesome,
                            loading = isGeneratingPost,
                            onClick = {
                                if (topicInput.isBlank()) {
                                    errorMessage = "Please enter a topic or theme."
                                    return@PersonaPrimaryButton
                                }
                                scope.launch {
                                    isGeneratingPost = true
                                    errorMessage = null
                                    val res = contentRepository.generateSocialContent(
                                        userId = userId,
                                        profile = profile ?: BrandProfileEntity(userId = userId),
                                        platform = selectedPlatform,
                                        contentType = selectedCategory,
                                        topic = topicInput,
                                        objective = selectedObjective,
                                        tone = selectedTone,
                                        scheduledDay = selectedDay
                                    )
                                    isGeneratingPost = false
                                    res.onSuccess { post ->
                                        generatedPost = post
                                    }.onFailure { e ->
                                        errorMessage = e.localizedMessage ?: "Failed to generate post."
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "generate_post_button"
                        )
                    }
                }
            }

            // Generated Post Result
            if (generatedPost != null) {
                item {
                    val post = generatedPost!!
                    var editableHook by remember(post) { mutableStateOf(post.hook) }
                    var editableCaption by remember(post) { mutableStateOf(post.caption) }
                    var editableCta by remember(post) { mutableStateOf(post.callToAction) }
                    var editableHashtags by remember(post) { mutableStateOf(post.hashtags) }
                    var editableExtra by remember(post) { mutableStateOf(post.carouselOrScript) }

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
                                AiBadge(label = "${post.platform} • ${post.contentType}")
                                IconButton(
                                    onClick = {
                                        val fullText = "$editableHook\n\n$editableCaption\n\n$editableCta\n\n$editableHashtags"
                                        clipboard.setText(AnnotatedString(fullText))
                                        Toast.makeText(context, "Full post copied!", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = PersonaDarkOlive
                                    )
                                }
                            }

                            // Hook
                            OutlinedTextField(
                                value = editableHook,
                                onValueChange = { editableHook = it },
                                label = { Text("Attention-Grabbing Hook (First Line)") },
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PersonaGreen,
                                    unfocusedBorderColor = PersonaBorder
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Caption Body
                            OutlinedTextField(
                                value = editableCaption,
                                onValueChange = { editableCaption = it },
                                label = { Text("Post Caption / Body") },
                                minLines = 4,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PersonaGreen,
                                    unfocusedBorderColor = PersonaBorder
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // CTA
                            OutlinedTextField(
                                value = editableCta,
                                onValueChange = { editableCta = it },
                                label = { Text("Call to Action (CTA)") },
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PersonaGreen,
                                    unfocusedBorderColor = PersonaBorder
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Hashtags
                            OutlinedTextField(
                                value = editableHashtags,
                                onValueChange = { editableHashtags = it },
                                label = { Text("Hashtags") },
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PersonaGreen,
                                    unfocusedBorderColor = PersonaBorder
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Carousel / Script if present
                            if (editableExtra.isNotBlank()) {
                                OutlinedTextField(
                                    value = editableExtra,
                                    onValueChange = { editableExtra = it },
                                    label = { Text("Carousel Outlines / Script Cues") },
                                    minLines = 3,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PersonaGreen,
                                        unfocusedBorderColor = PersonaBorder
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            PersonaPrimaryButton(
                                text = "Save to Content Calendar",
                                icon = Icons.Default.Save,
                                onClick = {
                                    scope.launch {
                                        contentRepository.saveSocialPost(
                                            post.copy(
                                                hook = editableHook,
                                                caption = editableCaption,
                                                callToAction = editableCta,
                                                hashtags = editableHashtags,
                                                carouselOrScript = editableExtra,
                                                status = "Scheduled"
                                            )
                                        )
                                        Toast.makeText(context, "Saved to your calendar!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "save_social_post_button"
                            )
                        }
                    }
                }
            }

            // Saved Posts List
            item {
                Text(
                    text = "Saved Posts (${savedPosts.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = PersonaDarkOlive
                )
            }

            if (savedPosts.isEmpty()) {
                item {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PersonaBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No saved posts yet. Generate your first post above!",
                            fontSize = 13.sp,
                            color = PersonaMutedGray,
                            modifier = Modifier.padding(18.dp)
                        )
                    }
                }
            } else {
                items(savedPosts, key = { it.id }) { post ->
                    SocialPostCard(
                        post = post,
                        onCopy = {
                            val full = "${post.hook}\n\n${post.caption}\n\n${post.callToAction}\n\n${post.hashtags}"
                            clipboard.setText(AnnotatedString(full))
                            Toast.makeText(context, "Copied post to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        onDelete = { postToDelete = post }
                    )
                }
            }
        } else {
            // WEEKLY CALENDAR VIEW
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Weekly Content Calendar",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = PersonaDarkOlive
                            )
                            AiBadge(label = "5-Day Schedule")
                        }

                        Text(
                            text = "Generate a full Monday-to-Friday schedule of strategic personal branding posts aligned with your goals.",
                            fontSize = 13.sp,
                            color = PersonaCharcoal
                        )

                        PersonaPrimaryButton(
                            text = "Generate 5-Day Weekly Calendar",
                            icon = Icons.Default.CalendarMonth,
                            loading = isGeneratingCalendar,
                            onClick = {
                                scope.launch {
                                    isGeneratingCalendar = true
                                    val theme = if (topicInput.isNotBlank()) topicInput else "Thought Leadership & Industry Trends in ${profile?.currentRole ?: "Tech"}"
                                    val res = contentRepository.generateWeeklyCalendar(
                                        userId = userId,
                                        profile = profile ?: BrandProfileEntity(userId = userId),
                                        platform = selectedPlatform,
                                        theme = theme
                                    )
                                    isGeneratingCalendar = false
                                    res.onSuccess {
                                        Toast.makeText(context, "Weekly calendar generated!", Toast.LENGTH_SHORT).show()
                                    }.onFailure { e ->
                                        errorMessage = e.localizedMessage
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "generate_calendar_button"
                        )
                    }
                }
            }

            // Calendar Days List
            daysOfWeek.forEach { day ->
                val dayPosts = savedPosts.filter { it.scheduledDay.equals(day, ignoreCase = true) }
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(PersonaGreen)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = day,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = PersonaDarkOlive
                            )
                        }

                        if (dayPosts.isEmpty()) {
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PersonaBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "No post scheduled for $day.",
                                    fontSize = 12.sp,
                                    color = PersonaMutedGray,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        } else {
                            dayPosts.forEach { post ->
                                SocialPostCard(
                                    post = post,
                                    onCopy = {
                                        val full = "${post.hook}\n\n${post.caption}\n\n${post.callToAction}\n\n${post.hashtags}"
                                        clipboard.setText(AnnotatedString(full))
                                        Toast.makeText(context, "Copied post to clipboard!", Toast.LENGTH_SHORT).show()
                                    },
                                    onDelete = { postToDelete = post }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    // Delete confirmation dialog
    if (postToDelete != null) {
        AlertDialog(
            onDismissRequest = { postToDelete = null },
            title = { Text("Delete Post") },
            text = { Text("Are you sure you want to delete '${postToDelete?.title}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val item = postToDelete
                        if (item != null) {
                            scope.launch {
                                contentRepository.deleteSocialPost(item.id, userId)
                                postToDelete = null
                                Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                ) {
                    Text("Delete", color = Color(0xFFC94A4A), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { postToDelete = null }) {
                    Text("Cancel", color = PersonaCharcoal)
                }
            }
        )
    }
}

@Composable
private fun SocialPostCard(
    post: SocialMediaPostEntity,
    onCopy: () -> Unit,
    onDelete: () -> Unit
) {
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
                Surface(
                    color = PersonaCreamSurface,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${post.scheduledDay} • ${post.platform} • ${post.contentType}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PersonaDarkOlive,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row {
                    IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = PersonaDarkOlive,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFC94A4A),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = post.hook,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = PersonaDarkOlive
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = post.caption,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = PersonaCharcoal
            )

            if (post.callToAction.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "👉 ${post.callToAction}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = PersonaDarkOlive
                )
            }

            if (post.hashtags.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = post.hashtags,
                    fontSize = 11.sp,
                    color = PersonaGreen,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
