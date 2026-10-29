package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import com.example.data.local.entity.BrandEvaluationEntity
import com.example.data.local.entity.GeneratedBioEntity
import com.example.data.local.entity.InterviewSessionEntity
import com.example.data.local.entity.SocialMediaPostEntity
import com.example.data.repository.ContentRepository
import com.example.data.repository.EvaluationRepository
import com.example.data.repository.InterviewRepository
import com.example.ui.components.AiBadge
import com.example.ui.components.ScoreCircularGauge
import com.example.ui.theme.PersonaAiBadgeBg
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

@Composable
fun ContentHistoryScreen(
    userId: Long,
    bios: List<GeneratedBioEntity>,
    posts: List<SocialMediaPostEntity>,
    sessions: List<InterviewSessionEntity>,
    evaluations: List<BrandEvaluationEntity>,
    contentRepository: ContentRepository,
    interviewRepository: InterviewRepository,
    evaluationRepository: EvaluationRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val tabs = listOf("All", "Bios & Taglines", "Social Posts", "Interviews", "Evaluations")

    var itemToDelete by remember { mutableStateOf<Any?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PersonaWarmOffWhite)
            .padding(horizontal = 20.dp)
            .testTag("content_history_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(6.dp)) }

        // Title
        item {
            Column {
                Text(
                    text = "Content History",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PersonaDarkOlive
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Access, copy, and manage your saved bios, posts, interview sessions, and evaluations.",
                    fontSize = 13.sp,
                    color = PersonaMutedGray
                )
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by title, keyword, or topic...", fontSize = 13.sp, color = PersonaMutedGray) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = PersonaGreen
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PersonaGreen,
                    unfocusedBorderColor = PersonaBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("history_search_input")
            )
        }

        // Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = PersonaDarkOlive,
                edgePadding = 8.dp,
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
                tabs.forEachIndexed { index, tabTitle ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text(
                            text = tabTitle,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp,
                            color = if (selectedTab == index) PersonaDarkOlive else PersonaMutedGray
                        )
                    }
                }
            }
        }

        // Filter and display items based on selected tab and search
        val filteredBios = bios.filter {
            selectedTab in listOf(0, 1) &&
                    (searchQuery.isBlank() || it.title.contains(searchQuery, true) || it.content.contains(searchQuery, true))
        }

        val filteredPosts = posts.filter {
            selectedTab in listOf(0, 2) &&
                    (searchQuery.isBlank() || it.title.contains(searchQuery, true) || it.hook.contains(searchQuery, true) || it.caption.contains(searchQuery, true))
        }

        val filteredSessions = sessions.filter {
            selectedTab in listOf(0, 3) &&
                    (searchQuery.isBlank() || it.targetRole.contains(searchQuery, true) || it.overallFeedback.contains(searchQuery, true))
        }

        val filteredEvaluations = evaluations.filter {
            selectedTab in listOf(0, 4) &&
                    (searchQuery.isBlank() || it.summaryFeedback.contains(searchQuery, true) || it.strengths.contains(searchQuery, true))
        }

        val totalCount = filteredBios.size + filteredPosts.size + filteredSessions.size + filteredEvaluations.size

        if (totalCount == 0) {
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PersonaBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No saved content found",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = PersonaDarkOlive
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "Try a different search term." else "Your saved content will appear here.",
                            fontSize = 12.sp,
                            color = PersonaMutedGray
                        )
                    }
                }
            }
        } else {
            // Bios
            if (filteredBios.isNotEmpty()) {
                item {
                    Text(
                        text = "Bios & Introductions (${filteredBios.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = PersonaDarkOlive
                    )
                }
                items(filteredBios, key = { "bio_${it.id}" }) { bio ->
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
                                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = PersonaDarkOlive, modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = { itemToDelete = bio },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFC94A4A), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = bio.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PersonaDarkOlive)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = bio.content, fontSize = 12.sp, lineHeight = 17.sp, color = PersonaCharcoal)
                        }
                    }
                }
            }

            // Social Posts
            if (filteredPosts.isNotEmpty()) {
                item {
                    Text(
                        text = "Social Media Posts (${filteredPosts.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = PersonaDarkOlive
                    )
                }
                items(filteredPosts, key = { "post_${it.id}" }) { post ->
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
                                        text = "${post.scheduledDay} • ${post.platform}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PersonaDarkOlive,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Row {
                                    IconButton(
                                        onClick = {
                                            clipboard.setText(AnnotatedString("${post.hook}\n\n${post.caption}\n\n${post.callToAction}\n\n${post.hashtags}"))
                                            Toast.makeText(context, "Copied post!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = PersonaDarkOlive, modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = { itemToDelete = post },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFC94A4A), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = post.hook, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PersonaDarkOlive)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = post.caption, fontSize = 12.sp, lineHeight = 17.sp, color = PersonaCharcoal)
                        }
                    }
                }
            }

            // Interview Sessions
            if (filteredSessions.isNotEmpty()) {
                item {
                    Text(
                        text = "Interview Practice (${filteredSessions.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = PersonaDarkOlive
                    )
                }
                items(filteredSessions, key = { "session_${it.id}" }) { s ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PersonaBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = s.targetRole, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PersonaDarkOlive)
                                Text(text = "${s.interviewType} • ${s.difficulty}", fontSize = 11.sp, color = PersonaMutedGray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = s.overallFeedback, fontSize = 12.sp, color = PersonaCharcoal, maxLines = 2)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            ScoreCircularGauge(score = s.overallScore, label = "Score", size = 52.dp)
                        }
                    }
                }
            }

            // Evaluations
            if (filteredEvaluations.isNotEmpty()) {
                item {
                    Text(
                        text = "Brand Evaluations (${filteredEvaluations.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = PersonaDarkOlive
                    )
                }
                items(filteredEvaluations, key = { "eval_${it.id}" }) { ev ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PersonaBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Brand Audit (${ev.overallScore}/100)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PersonaDarkOlive)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = ev.summaryFeedback, fontSize = 12.sp, color = PersonaCharcoal, maxLines = 2)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            ScoreCircularGauge(score = ev.overallScore, label = "Audit", size = 52.dp)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    // Delete dialog
    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this saved item? This action is permanent.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val item = itemToDelete
                        scope.launch {
                            when (item) {
                                is GeneratedBioEntity -> contentRepository.deleteBio(item.id, userId)
                                is SocialMediaPostEntity -> contentRepository.deleteSocialPost(item.id, userId)
                            }
                            itemToDelete = null
                            Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Delete", color = Color(0xFFC94A4A), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancel", color = PersonaCharcoal)
                }
            }
        )
    }
}
