package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.PersonaTipBg
import com.example.ui.theme.PersonaTipText
import com.example.ui.theme.PersonaWarmOffWhite

@Composable
fun PersonaPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null,
    testTag: String = "primary_button"
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PersonaGreen,
            contentColor = Color.White,
            disabledContainerColor = PersonaGreen.copy(alpha = 0.5f),
            disabledContentColor = Color.White.copy(alpha = 0.8f)
        ),
        modifier = modifier
            .height(50.dp)
            .testTag(testTag)
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.5.dp,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Processing...",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        } else {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun PersonaSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    testTag: String = "secondary_button"
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = PersonaDarkOlive
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(PersonaBorder)
        ),
        modifier = modifier
            .height(50.dp)
            .testTag(testTag)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PersonaDarkOlive,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun AiBadge(
    label: String = "AI Generated",
    modifier: Modifier = Modifier
) {
    Surface(
        color = PersonaAiBadgeBg,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = PersonaAiBadgeText,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = PersonaAiBadgeText,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun TipCard(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Info
) {
    Surface(
        color = PersonaCreamSurface,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, PersonaButterCream),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(PersonaButterCream),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PersonaTipText,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = PersonaDarkOlive
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = message,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = PersonaCharcoal
                )
            }
        }
    }
}

@Composable
fun ApiKeyNoticeBanner(
    hasKey: Boolean,
    onConfigureClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!hasKey) {
        Surface(
            color = PersonaButterCream.copy(alpha = 0.85f),
            shape = RoundedCornerShape(14.dp),
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clickable { onConfigureClick() }
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = null,
                    tint = PersonaDarkOlive,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AI Key Setup Required",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = PersonaDarkOlive
                    )
                    Text(
                        text = "Tap to configure your Gemini API Key in Settings for live AI generation.",
                        fontSize = 12.sp,
                        color = PersonaDarkOlive.copy(alpha = 0.85f)
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Configure",
                    tint = PersonaDarkOlive,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun ScoreCircularGauge(
    score: Int,
    label: String,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    when {
                        score >= 80 -> PersonaGreen.copy(alpha = 0.2f)
                        score >= 60 -> PersonaButterCream
                        else -> PersonaCoralPink.copy(alpha = 0.3f)
                    }
                )
                .border(
                    width = 3.dp,
                    color = when {
                        score >= 80 -> PersonaGreen
                        score >= 60 -> Color(0xFFD6A828)
                        else -> PersonaCoralPink
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$score",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PersonaDarkOlive
                )
                Text(
                    text = "/100",
                    fontSize = 10.sp,
                    color = PersonaMutedGray
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = PersonaCharcoal,
            textAlign = TextAlign.Center
        )
    }
}
