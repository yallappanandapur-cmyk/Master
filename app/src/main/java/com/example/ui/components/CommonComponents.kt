package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AttendanceStatus
import com.example.i18n.AppLanguage
import com.example.i18n.Translations
import com.example.ui.theme.AmberBg
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.EmeraldBg
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySurface
import com.example.ui.theme.PurpleBg
import com.example.ui.theme.PurpleRole
import com.example.ui.theme.RoseBg
import com.example.ui.theme.RoseError
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800

@Composable
fun SchoolHeader(
    currentLanguage: AppLanguage,
    onToggleLanguage: () -> Unit,
    userRoleName: String? = null,
    onRoleClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("school_header_card"),
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
        colors = CardDefaults.cardColors(containerColor = NavyDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // School crest badge
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(NavyPrimary)
                        .border(1.5.dp, GoldAmber, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.smps_school_logo),
                        contentDescription = "SMPS Crest",
                        modifier = Modifier.size(44.dp).clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = Translations.get("school_title", currentLanguage),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = Translations.get("school_subtitle", currentLanguage) + " • SMART ATTENDANCE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = GoldAmber,
                        letterSpacing = 1.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Language Switcher Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NavySurface,
                    modifier = Modifier
                        .clickable { onToggleLanguage() }
                        .border(1.dp, BlueAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .testTag("language_toggle_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (currentLanguage == AppLanguage.ENGLISH) "ಕನ್ನಡ" else "ENG",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (userRoleName != null && onRoleClick != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GoldAmber.copy(alpha = 0.2f),
                        modifier = Modifier
                            .clickable { onRoleClick() }
                            .border(1.dp, GoldAmber, RoundedCornerShape(12.dp))
                            .testTag("role_switcher_chip")
                    ) {
                        Text(
                            text = userRoleName,
                            color = GoldAmber,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(
    status: AttendanceStatus,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon) = when (status) {
        AttendanceStatus.PRESENT -> Triple(EmeraldBg, EmeraldSuccess, Icons.Default.CheckCircle)
        AttendanceStatus.LATE -> Triple(AmberBg, AmberWarning, Icons.Default.Schedule)
        AttendanceStatus.ABSENT -> Triple(RoseBg, RoseError, Icons.Default.Close)
        AttendanceStatus.LEAVE -> Triple(PurpleBg, PurpleRole, Icons.Default.VerifiedUser)
        AttendanceStatus.HALF_DAY -> Triple(AmberBg, AmberWarning, Icons.Default.Schedule)
        AttendanceStatus.HOLIDAY -> Triple(BlueAccent.copy(alpha = 0.1f), BlueAccent, Icons.Default.School)
        AttendanceStatus.NOT_MARKED -> Triple(Color.LightGray.copy(alpha = 0.2f), Color.DarkGray, Icons.Default.Warning)
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        modifier = modifier.border(1.dp, textColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = Translations.get(status.name, lang),
                color = textColor,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate600,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Slate900(),
                    fontWeight = FontWeight.ExtraBold
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun Slate900(): Color = Color(0xFF0F172A)

@Composable
fun BiometricScanOverlay(
    modifier: Modifier = Modifier,
    isScanning: Boolean = true,
    scanSuccess: Boolean = false,
    confidence: Float = 0.98f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scan_y"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.35f)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val ovalWidth = width * 0.72f * pulseScale
            val ovalHeight = height * 0.60f * pulseScale
            val ovalLeft = (width - ovalWidth) / 2f
            val ovalTop = (height - ovalHeight) / 2f

            val strokeColor = when {
                scanSuccess -> EmeraldSuccess
                isScanning -> BlueAccent
                else -> Color.White
            }

            // Draw Face Oval Guide
            drawOval(
                color = strokeColor.copy(alpha = 0.85f),
                topLeft = Offset(ovalLeft, ovalTop),
                size = Size(ovalWidth, ovalHeight),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw Corner Brackets
            val cornerLen = 32.dp.toPx()
            val strokeW = 4.dp.toPx()
            val bracketColor = if (scanSuccess) EmeraldSuccess else GoldAmber

            // Top-Left
            drawLine(bracketColor, Offset(ovalLeft - 10f, ovalTop + cornerLen), Offset(ovalLeft - 10f, ovalTop - 10f), strokeW)
            drawLine(bracketColor, Offset(ovalLeft - 10f, ovalTop - 10f), Offset(ovalLeft + cornerLen, ovalTop - 10f), strokeW)

            // Top-Right
            drawLine(bracketColor, Offset(ovalLeft + ovalWidth + 10f - cornerLen, ovalTop - 10f), Offset(ovalLeft + ovalWidth + 10f, ovalTop - 10f), strokeW)
            drawLine(bracketColor, Offset(ovalLeft + ovalWidth + 10f, ovalTop - 10f), Offset(ovalLeft + ovalWidth + 10f, ovalTop + cornerLen), strokeW)

            // Bottom-Left
            drawLine(bracketColor, Offset(ovalLeft - 10f, ovalTop + ovalHeight - cornerLen), Offset(ovalLeft - 10f, ovalTop + ovalHeight + 10f), strokeW)
            drawLine(bracketColor, Offset(ovalLeft - 10f, ovalTop + ovalHeight + 10f), Offset(ovalLeft + cornerLen, ovalTop + ovalHeight + 10f), strokeW)

            // Bottom-Right
            drawLine(bracketColor, Offset(ovalLeft + ovalWidth + 10f - cornerLen, ovalTop + ovalHeight + 10f), Offset(ovalLeft + ovalWidth + 10f, ovalTop + ovalHeight + 10f), strokeW)
            drawLine(bracketColor, Offset(ovalLeft + ovalWidth + 10f, ovalTop + ovalHeight + 10f), Offset(ovalLeft + ovalWidth + 10f, ovalTop + ovalHeight - cornerLen), strokeW)

            // Animated Laser Scanner Line
            if (isScanning && !scanSuccess) {
                val currentY = ovalTop + (ovalHeight * scanLineY)
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            BlueAccent.copy(alpha = 0.3f),
                            Color(0xFF38BDF8),
                            BlueAccent.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    ),
                    start = Offset(ovalLeft, currentY),
                    end = Offset(ovalLeft + ovalWidth, currentY),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
fun SchoolSignatureStamp(
    principalName: String,
    date: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(60.dp, 30.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("ACCOUNTS", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.width(100.dp).height(1.dp).background(Color.Gray))
            Spacer(modifier = Modifier.height(2.dp))
            Text("Authorized Sign", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Slate600)
        }

        // Seal stamp
        Box(
            modifier = Modifier
                .size(70.dp)
                .border(2.dp, NavyPrimary.copy(alpha = 0.6f), CircleShape)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("SM PUBLIC", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                Text("★ JALIHAL ★", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = GoldAmber)
                Text("SEAL", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Dr. R. K. Patil", fontFamily = FontFamily.Cursive, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier.width(120.dp).height(1.dp).background(NavyPrimary))
            Spacer(modifier = Modifier.height(2.dp))
            Text("Principal / Secretary", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
            Text("Date: $date", fontSize = 9.sp, color = Slate600)
        }
    }
}
