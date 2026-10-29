package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.AiBadge
import com.example.ui.components.PersonaPrimaryButton
import com.example.ui.components.PersonaSecondaryButton
import com.example.ui.theme.PersonaAiBadgeBg
import com.example.ui.theme.PersonaAiBadgeText
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LandingScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PersonaWarmOffWhite)
            .testTag("landing_screen")
    ) {
        // 1. Top Navigation Bar
        item {
            Surface(
                color = Color.White,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Logo + Brand Name
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { /* Home */ }
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
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PersonaAI",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PersonaDarkOlive
                        )
                    }

                    // Quick Nav Actions
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(
                            onClick = onNavigateToLogin,
                            modifier = Modifier.testTag("nav_login_button")
                        ) {
                            Text(
                                text = "Log In",
                                color = PersonaDarkOlive,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        PersonaPrimaryButton(
                            text = "Get Started",
                            onClick = onNavigateToSignUp,
                            modifier = Modifier.height(40.dp),
                            testTag = "nav_get_started_button"
                        )
                    }
                }
            }
        }

        // 2. Hero Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AiBadge(label = "AI-Powered Personal Branding")
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Build Your Personal Brand with AI.",
                    fontSize = 32.sp,
                    lineHeight = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PersonaDarkOlive,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Discover your core strengths, craft tailored bios, generate social calendars, and ace interviews with objective AI simulations.",
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = PersonaCharcoal,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // CTA Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PersonaPrimaryButton(
                        text = "Start Building My Brand",
                        onClick = onNavigateToSignUp,
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                        modifier = Modifier.weight(1f),
                        testTag = "hero_start_building_button"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Hero Graphic / Visual Preview
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.persona_hero),
                            contentDescription = "Personal branding visual illustration",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Live preview highlight
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(PersonaCreamSurface, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = PersonaDarkOlive,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Grounded exclusively in your actual credentials & achievements.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = PersonaDarkOlive
                            )
                        }
                    }
                }
            }
        }

        // 3. How It Works (Three-Step Process)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                Text(
                    text = "How It Works",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PersonaGreen,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Three Steps to a High-Impact Brand",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PersonaDarkOlive
                )

                Spacer(modifier = Modifier.height(20.dp))

                StepCard(
                    stepNumber = "1",
                    title = "Discover Yourself",
                    description = "Fill your interactive profile with your real skills, passions, achievements, and communication style.",
                    icon = Icons.Default.Person,
                    badgeColor = PersonaLightLime
                )

                Spacer(modifier = Modifier.height(14.dp))

                StepCard(
                    stepNumber = "2",
                    title = "Build Your Identity",
                    description = "PersonaAI synthesizes your unique value proposition, bios, and platform-specific headlines with zero fabricated claims.",
                    icon = Icons.Default.Lightbulb,
                    badgeColor = PersonaCoralPink
                )

                Spacer(modifier = Modifier.height(14.dp))

                StepCard(
                    stepNumber = "3",
                    title = "Grow Your Brand",
                    description = "Publish tailored social media calendars, simulate high-stakes interviews, and audit your personal brand score.",
                    icon = Icons.Default.TrendingUp,
                    badgeColor = PersonaButterCream
                )
            }
        }

        // 4. Feature Showcase Cards
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                Text(
                    text = "Core Features",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PersonaGreen,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Everything You Need to Stand Out",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PersonaDarkOlive
                )

                Spacer(modifier = Modifier.height(20.dp))

                FeatureCard(
                    title = "Profile & Bio Generator",
                    description = "Generate LinkedIn About sections, taglines, Instagram bios, and elevator pitches in any tone.",
                    icon = Icons.Default.AutoAwesome,
                    tag = "Instant Bio",
                    color = PersonaAiBadgeBg
                )

                Spacer(modifier = Modifier.height(14.dp))

                FeatureCard(
                    title = "Social Media Studio",
                    description = "Produce educational posts, hooks, carousel outlines, video scripts, and weekly calendars.",
                    icon = Icons.Default.Share,
                    tag = "Content Engine",
                    color = PersonaLightLime.copy(alpha = 0.4f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                FeatureCard(
                    title = "AI Interview Simulator",
                    description = "Practice behavioral and competency questions with real-time STAR feedback and refined answers.",
                    icon = Icons.Default.QuestionAnswer,
                    tag = "Interactive Practice",
                    color = PersonaButterCream.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                FeatureCard(
                    title = "Personal Brand Evaluator",
                    description = "Get an objective 7-dimension score from 0-100 with a concrete 7-day action improvement plan.",
                    icon = Icons.Default.CheckCircle,
                    tag = "Audit & Growth",
                    color = PersonaAiBadgeBg
                )
            }
        }

        // 5. Final Call to Action
        item {
            Surface(
                color = PersonaDarkOlive,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ready to own your narrative?",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Join PersonaAI today and start building a personal brand that opens doors.",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    PersonaPrimaryButton(
                        text = "Get Started Free",
                        onClick = onNavigateToSignUp,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "footer_cta_button"
                    )
                }
            }
        }

        // 6. Footer
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PersonaGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "P",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PersonaAI",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PersonaDarkOlive
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Professional AI-Powered Personal Branding Platform. Built with security, ethics, and truth in mind.",
                    fontSize = 12.sp,
                    color = PersonaMutedGray
                )

                HorizontalDivider(
                    color = PersonaBorder,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "© 2026 PersonaAI. All rights reserved.",
                        fontSize = 11.sp,
                        color = PersonaMutedGray
                    )
                    Text(
                        text = "Privacy & Terms",
                        fontSize = 11.sp,
                        color = PersonaGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun StepCard(
    stepNumber: String,
    title: String,
    description: String,
    icon: ImageVector,
    badgeColor: Color
) {
    Surface(
        color = PersonaWarmOffWhite,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, PersonaBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(badgeColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNumber,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PersonaDarkOlive
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PersonaDarkOlive
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = PersonaCharcoal
                )
            }
        }
    }
}

@Composable
private fun FeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    tag: String,
    color: Color
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, PersonaBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = PersonaDarkOlive,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Surface(
                    color = PersonaCreamSurface,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = tag,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PersonaDarkOlive,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = PersonaDarkOlive
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = PersonaMutedGray
            )
        }
    }
}
