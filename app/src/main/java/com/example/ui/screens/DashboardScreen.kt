package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SessionManager
import com.example.data.local.entity.BrandEvaluationEntity
import com.example.data.local.entity.BrandProfileEntity
import com.example.data.local.entity.GeneratedBioEntity
import com.example.data.local.entity.SocialMediaPostEntity
import com.example.data.local.entity.UserEntity
import com.example.data.repository.BrandProfileRepository
import com.example.data.repository.ContentRepository
import com.example.data.repository.EvaluationRepository
import com.example.data.repository.InterviewRepository
import com.example.data.repository.UserRepository
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

@Composable
fun DashboardScreen(
    user: UserEntity?,
    profile: BrandProfileEntity?,
    latestEvaluation: BrandEvaluationEntity?,
    recentBios: List<GeneratedBioEntity>,
    recentPosts: List<SocialMediaPostEntity>,
    sessionManager: SessionManager,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val hasApiKey = sessionManager.hasApiKey()
    val completionPct = profile?.completionPercentage ?: 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PersonaWarmOffWhite)
            .padding(horizontal = 20.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item { Spacer(modifier = Modifier.height(6.dp)) }

        // API Key notice if missing
        item {
            ApiKeyNoticeBanner(
                hasKey = hasApiKey,
                onConfigureClick = { onNavigate("settings") }
            )
        }

        // Welcome Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hello, ${user?.fullName ?: "Creator"} 👋",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PersonaDarkOlive
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (profile?.currentRole.isNullOrBlank())
                            "Let's craft your personal branding identity"
                        else
                            profile?.currentRole ?: "",
                        fontSize = 14.sp,
                        color = PersonaMutedGray
                    )
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(PersonaGreen)
                        .clickable { onNavigate("profile") },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (user?.fullName?.take(1) ?: "P").uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }

        // Profile Completion Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, PersonaBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate("profile") }
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Brand Profile Completion",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = PersonaDarkOlive
                        )
                        Surface(
                            color = if (completionPct >= 80) PersonaLightLime else PersonaButterCream,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "$completionPct%",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = PersonaDarkOlive,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { completionPct / 100f },
                        color = PersonaGreen,
                        trackColor = PersonaWarmOffWhite,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (completionPct < 100)
                            "Complete your profile to unlock high-fidelity AI bios and interview feedback."
                        else
                            "Profile complete! Your AI branding outputs will be sharply targeted.",
                        fontSize = 12.sp,
                        color = PersonaMutedGray
                    )
                }
            }
        }

        // Personal Branding Strength Overview Card
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Brand Strength Overview",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = PersonaDarkOlive
                        )
                        Text(
                            text = "Audit Now",
                            color = PersonaGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.clickable { onNavigate("evaluator") }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (latestEvaluation != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            ScoreCircularGauge(
                                score = latestEvaluation.overallScore,
                                label = "Overall",
                                size = 70.dp
                            )
                            ScoreCircularGauge(
                                score = latestEvaluation.clarityScore,
                                label = "Clarity",
                                size = 60.dp
                            )
                            ScoreCircularGauge(
                                score = latestEvaluation.uvpScore,
                                label = "UVP",
                                size = 60.dp
                            )
                            ScoreCircularGauge(
                                score = latestEvaluation.evidenceScore,
                                label = "Proof",
                                size = 60.dp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = latestEvaluation.summaryFeedback,
                            fontSize = 12.sp,
                            color = PersonaCharcoal,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        // Empty evaluation state
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(PersonaCreamSurface, RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = PersonaDarkOlive,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "No Evaluation Yet",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = PersonaDarkOlive
                                )
                                Text(
                                    text = "Take a 2-minute brand audit to measure clarity, consistency, and UVP.",
                                    fontSize = 12.sp,
                                    color = PersonaCharcoal
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Access Grid
        item {
            Text(
                text = "Quick Access",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = PersonaDarkOlive
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "My Brand Profile",
                        subtitle = "Skills, UVP & Identity",
                        icon = Icons.Default.Person,
                        color = PersonaLightLime.copy(alpha = 0.5f),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("profile") }
                    )
                    QuickActionCard(
                        title = "AI Bio Generator",
                        subtitle = "Bios, elevator pitch",
                        icon = Icons.Default.AutoAwesome,
                        color = PersonaAiBadgeBg,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("bio_generator") }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "Social Studio",
                        subtitle = "Hooks, posts, calendar",
                        icon = Icons.Default.Share,
                        color = PersonaButterCream,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("social_studio") }
                    )
                    QuickActionCard(
                        title = "Interview Simulator",
                        subtitle = "Interactive practice",
                        icon = Icons.Default.QuestionAnswer,
                        color = PersonaLightLime.copy(alpha = 0.5f),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("interview_simulator") }
                    )
                }
            }
        }

        // Upcoming Interview Practice Reminder Card
        item {
            Surface(
                color = PersonaDarkOlive,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate("interview_simulator") }
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(PersonaGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Simulate Your Next Interview",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Practice behavioral questions and get instant STAR method scoring.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Start",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Branding Improvement Suggestions (Warm Butter Cream)
        item {
            TipCard(
                title = "Branding Tip of the Week",
                message = "Ground every claim with a tangible metric. Instead of 'skilled in communication', highlight 'facilitated cross-functional design sprints that cut turnaround by 20%'.",
                icon = Icons.Default.Lightbulb
            )
        }

        // Recently Generated Content
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Creations",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = PersonaDarkOlive
                )
                Text(
                    text = "View All",
                    color = PersonaGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { onNavigate("history") }
                )
            }
        }

        if (recentBios.isEmpty() && recentPosts.isEmpty()) {
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
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = PersonaMutedGray,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No generated content yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = PersonaDarkOlive
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Generate a bio or social media post to see it here.",
                            fontSize = 12.sp,
                            color = PersonaMutedGray
                        )
                    }
                }
            }
        } else {
            recentBios.take(2).forEach { bio ->
                item {
                    RecentItemCard(
                        title = bio.title,
                        type = bio.bioType,
                        snippet = bio.content,
                        onCopy = {
                            clipboard.setText(AnnotatedString(bio.content))
                            Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
            recentPosts.take(2).forEach { post ->
                item {
                    RecentItemCard(
                        title = post.title,
                        type = "${post.platform} • ${post.contentType}",
                        snippet = "${post.hook}\n${post.caption}",
                        onCopy = {
                            clipboard.setText(AnnotatedString("${post.hook}\n\n${post.caption}\n\n${post.callToAction}\n\n${post.hashtags}"))
                            Toast.makeText(context, "Copied post to clipboard!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, PersonaBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PersonaDarkOlive,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PersonaDarkOlive
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = PersonaMutedGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun RecentItemCard(
    title: String,
    type: String,
    snippet: String,
    onCopy: () -> Unit
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AiBadge(label = type)
                }
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy text",
                        tint = PersonaDarkOlive,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = PersonaDarkOlive
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = snippet,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = PersonaCharcoal,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
