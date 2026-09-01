package com.example.ui.screens.teacher

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biometrics.LivenessChallenge
import com.example.biometrics.VerificationResult
import com.example.data.model.TeacherEntity
import com.example.i18n.AppLanguage
import com.example.i18n.Translations
import com.example.ui.components.BiometricScanOverlay
import com.example.ui.theme.AmberBg
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.EmeraldBg
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySurface
import com.example.ui.theme.RoseBg
import com.example.ui.theme.RoseError
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import com.example.whatsapp.WhatsAppService
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FaceAttendanceScreen(
    teacher: TeacherEntity,
    isLoginMode: Boolean,
    currentLanguage: AppLanguage,
    onBackClick: () -> Unit,
    onAttendanceRecorded: suspend (teacherId: String, isLogin: Boolean) -> VerificationResult,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var scanStage by remember { mutableIntStateOf(0) } // 0: Detection, 1: Liveness 1, 2: Liveness 2, 3: Verifying, 4: Done, 5: Error
    var challengeIndex by remember { mutableIntStateOf(0) }
    val challenges = listOf(
        LivenessChallenge.LOOK_STRAIGHT,
        LivenessChallenge.BLINK_EYES,
        LivenessChallenge.SMILE
    )

    var progressValue by remember { mutableFloatStateOf(0.1f) }
    var verificationResult by remember { mutableStateOf<VerificationResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var recordedDate by remember { mutableStateOf("") }
    var recordedTime by remember { mutableStateOf("") }
    var recordedStatus by remember { mutableStateOf("PRESENT") }

    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())

    // Simulated Biometric Scan Cycle
    LaunchedEffect(scanStage) {
        when (scanStage) {
            0 -> {
                progressValue = 0.2f
                delay(1200)
                challengeIndex = 1
                scanStage = 1
            }
            1 -> {
                progressValue = 0.5f
                delay(1400)
                challengeIndex = 2
                scanStage = 2
            }
            2 -> {
                progressValue = 0.8f
                delay(1300)
                scanStage = 3
            }
            3 -> {
                progressValue = 0.95f
                val now = Date()
                recordedDate = dateFormat.format(now)
                recordedTime = timeFormat.format(now)

                val result = onAttendanceRecorded(teacher.teacherId, isLoginMode)
                verificationResult = result
                if (result.isSuccess) {
                    progressValue = 1.0f
                    recordedStatus = if (isLoginMode) "PRESENT" else "LOGGED_OUT"
                    scanStage = 4
                } else {
                    errorMessage = result.message
                    scanStage = 5
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDark)
            .testTag("face_attendance_screen")
    ) {
        // Simulated Camera Viewfinder with dark gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            NavyDark,
                            Color(0xFF0F274A),
                            NavyDark
                        )
                    )
                )
        )

        // Biometric Face Scanner Overlay (Oval guide, laser scanning animation, brackets)
        BiometricScanOverlay(
            isScanning = scanStage in 0..3,
            scanSuccess = scanStage == 4,
            confidence = verificationResult?.confidence ?: 0.98f
        )

        // Top Navigation & Title Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .testTag("face_back_btn")
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isLoginMode) NavyPrimary.copy(alpha = 0.85f) else GoldAmber.copy(alpha = 0.85f),
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = if (isLoginMode) Translations.get("face_login", currentLanguage) else Translations.get("face_logout", currentLanguage),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            // Camera Flip / Sensor Indicator
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }

        // Center / Bottom Stage Prompts and Live Feedback
        if (scanStage in 0..3) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface.copy(alpha = 0.92f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LinearProgressIndicator(
                            progress = { progressValue },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = GoldAmber,
                            trackColor = Color.DarkGray
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        val currentPrompt = if (currentLanguage == AppLanguage.KANNADA)
                            challenges[challengeIndex].promptKn else challenges[challengeIndex].promptEn

                        Text(
                            text = currentPrompt,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = BlueAccent,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (scanStage == 3) Translations.get("recording_attendance", currentLanguage)
                                else "${Translations.get("liveness_check", currentLanguage)} (${challengeIndex + 1}/3)",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate200
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${teacher.name} • ${teacher.employeeId}",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldAmber,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Success Confirmation Dialog / Card (Exact prompt requirements)
        AnimatedVisibility(
            visible = scanStage == 4,
            enter = fadeIn(tween(400)) + scaleIn(tween(400)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .testTag("attendance_success_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(EmeraldBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = Translations.get("face_verified_success", currentLanguage),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Slate200)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Detail rows (Date, Time, Teacher Name, Employee ID, Attendance Status)
                    SuccessDetailRow(label = Translations.get("date", currentLanguage), value = recordedDate)
                    SuccessDetailRow(label = Translations.get("time", currentLanguage), value = recordedTime)
                    SuccessDetailRow(label = Translations.get("teacher_name", currentLanguage), value = teacher.name)
                    SuccessDetailRow(label = Translations.get("emp_id", currentLanguage), value = teacher.employeeId)
                    SuccessDetailRow(
                        label = Translations.get("attendance_status", currentLanguage),
                        value = if (isLoginMode) "PRESENT" else "LOGGED OUT",
                        valueColor = EmeraldSuccess
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // WhatsApp dispatched notification banner
                    Surface(
                        color = EmeraldBg,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "WhatsApp notice automatically dispatched to ${teacher.mobileNumber}",
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldSuccess,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onBackClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("attendance_done_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) {
                        Text(
                            text = "Back to Home",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Error / Duplicate Prevention Card
        AnimatedVisibility(
            visible = scanStage == 5,
            enter = fadeIn(tween(300)) + scaleIn(tween(300)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .testTag("attendance_error_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(RoseBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = RoseError,
                            modifier = Modifier.size(46.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Attendance Notice",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = errorMessage ?: "Verification could not be completed.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate600,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onBackClick,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Back")
                        }

                        Button(
                            onClick = {
                                scanStage = 0
                                challengeIndex = 0
                                progressValue = 0.1f
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SuccessDetailRow(
    label: String,
    value: String,
    valueColor: Color = Slate900()
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Slate600,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun Slate900(): Color = Color(0xFF0F172A)
