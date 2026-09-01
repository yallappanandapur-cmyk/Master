package com.example.ui.screens.teacher

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TimeToLeave
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.AttendanceStatus
import com.example.data.model.TeacherEntity
import com.example.i18n.AppLanguage
import com.example.i18n.Translations
import com.example.ui.components.StatusChip
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
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TeacherHomeScreen(
    teacher: TeacherEntity,
    todayAttendance: AttendanceRecordEntity?,
    currentLanguage: AppLanguage,
    onFaceLoginClick: () -> Unit,
    onFaceLogoutClick: () -> Unit,
    onMyAttendanceClick: () -> Unit,
    onMySalaryClick: () -> Unit,
    onApplyLeaveClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTimeString by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val format = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        while (true) {
            currentTimeString = format.format(Date())
            delay(1000)
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate100)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Teacher Welcome Card
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("teacher_welcome_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(teacher.avatarColor)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = teacher.name.take(2).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${Translations.get("welcome", currentLanguage)},",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.KANNADA && teacher.nameKannada.isNotEmpty())
                            teacher.nameKannada else teacher.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                    Text(
                        text = "${teacher.employeeId} • ${teacher.designation}",
                        style = MaterialTheme.typography.bodySmall,
                        color = GoldAmber,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(onClick = onProfileClick, modifier = Modifier.testTag("profile_icon_btn")) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        tint = NavyPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Today's Attendance Card (Prompt requested exact structure)
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("today_attendance_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = NavyPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Translations.get("todays_attendance", currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NavySurface.copy(alpha = 0.08f)
                    ) {
                        Text(
                            text = currentTimeString.ifEmpty { "09:05 AM" },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Slate200)
                Spacer(modifier = Modifier.height(16.dp))

                // Login, Logout, Status Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Login Column
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Slate100, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = Translations.get("login_time", currentLanguage),
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = todayAttendance?.loginTime ?: "--:--",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (todayAttendance?.loginTime != null) EmeraldSuccess else Slate600
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Logout Column
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Slate100, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = Translations.get("logout_time", currentLanguage),
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = todayAttendance?.logoutTime ?: "--:--",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (todayAttendance?.logoutTime != null) BlueAccent else Slate600
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Status Column
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Slate100, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = Translations.get("status", currentLanguage),
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        StatusChip(
                            status = todayAttendance?.status ?: AttendanceStatus.NOT_MARKED,
                            lang = currentLanguage
                        )
                    }
                }

                if (todayAttendance?.lateMinutes != null && todayAttendance.lateMinutes > 0) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = AmberBg,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Late by ${todayAttendance.lateMinutes} mins (Login: ${todayAttendance.loginTime})",
                                style = MaterialTheme.typography.labelSmall,
                                color = AmberWarning,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Large Primary Face Action Buttons (Prompt requested)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onFaceLoginClick,
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .testTag("face_login_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Translations.get("face_login", currentLanguage),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }

            Button(
                onClick = onFaceLogoutClick,
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .testTag("face_logout_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAmber),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Translations.get("face_logout", currentLanguage),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid of 4 Feature Buttons: [MY ATTENDANCE], [MY SALARY], [APPLY LEAVE], [NOTIFICATIONS]
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TeacherMenuButton(
                    title = Translations.get("my_attendance", currentLanguage),
                    subtitle = "Monthly Logs & Records",
                    icon = Icons.Default.CalendarMonth,
                    accentColor = BlueAccent,
                    onClick = onMyAttendanceClick,
                    modifier = Modifier.weight(1f).testTag("my_attendance_btn")
                )
                TeacherMenuButton(
                    title = Translations.get("my_salary", currentLanguage),
                    subtitle = "Payslip & Breakdown",
                    icon = Icons.Default.Paid,
                    accentColor = EmeraldSuccess,
                    onClick = onMySalaryClick,
                    modifier = Modifier.weight(1f).testTag("my_salary_btn")
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TeacherMenuButton(
                    title = Translations.get("apply_leave", currentLanguage),
                    subtitle = "Request & History",
                    icon = Icons.Default.DateRange,
                    accentColor = PurpleRole,
                    onClick = onApplyLeaveClick,
                    modifier = Modifier.weight(1f).testTag("apply_leave_btn")
                )
                TeacherMenuButton(
                    title = Translations.get("notifications", currentLanguage),
                    subtitle = "WhatsApp & Notices",
                    icon = Icons.Default.Notifications,
                    accentColor = GoldAmber,
                    onClick = onNotificationsClick,
                    modifier = Modifier.weight(1f).testTag("notifications_btn")
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Biometric Security & WhatsApp Auto-Notice Assurance Footer
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = NavyDark.copy(alpha = 0.05f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = EmeraldSuccess,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Encrypted Biometric & WhatsApp Cloud Verified",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                    Text(
                        text = "Real-time attendance logs & salary slips dispatched to WhatsApp.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate600
                    )
                }
            }
        }
    }
}

@Composable
fun TeacherMenuButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(108.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = NavyDark,
                    fontSize = 13.sp
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate600,
                    fontSize = 10.sp
                )
            }
        }
    }
}
