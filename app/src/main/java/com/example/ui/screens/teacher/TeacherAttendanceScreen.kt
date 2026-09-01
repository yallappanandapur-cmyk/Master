package com.example.ui.screens.teacher

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherAttendanceScreen(
    teacher: TeacherEntity,
    attendanceRecords: List<AttendanceRecordEntity>,
    currentLanguage: AppLanguage,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val presentCount = attendanceRecords.count { it.status == AttendanceStatus.PRESENT }
    val lateCount = attendanceRecords.count { it.status == AttendanceStatus.LATE }
    val leaveCount = attendanceRecords.count { it.status == AttendanceStatus.LEAVE }
    val absentCount = attendanceRecords.count { it.status == AttendanceStatus.ABSENT }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = Translations.get("my_attendance", currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${teacher.name} (${teacher.employeeId})",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldAmber
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("attendance_back_btn")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDark)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Slate100)
                .padding(16.dp)
        ) {
            // Month Metric Summary Bar
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("attendance_summary_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "September 2026 Summary",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                        Text(
                            text = "26 Working Days",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate600,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AttendanceMetricPill("Present", presentCount.toString(), EmeraldBg, EmeraldSuccess)
                        AttendanceMetricPill("Late", lateCount.toString(), AmberBg, AmberWarning)
                        AttendanceMetricPill("Leave", leaveCount.toString(), PurpleBg, PurpleRole)
                        AttendanceMetricPill("Absent", absentCount.toString(), RoseBg, RoseError)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Daily Attendance Log",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = NavyDark,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(attendanceRecords) { record ->
                    AttendanceLogItem(record = record, lang = currentLanguage)
                }
            }
        }
    }
}

@Composable
fun AttendanceMetricPill(
    label: String,
    value: String,
    bg: Color,
    text: Color
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bg,
        modifier = Modifier.border(1.dp, text.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = text)
            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = text)
        }
    }
}

@Composable
fun AttendanceLogItem(
    record: AttendanceRecordEntity,
    lang: AppLanguage
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(NavySurface.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = NavyPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = record.date,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "In: ${record.loginTime ?: "--"} • Out: ${record.logoutTime ?: "--"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                    if (record.faceVerified) {
                        Text(
                            text = "✓ Biometric Face Verified (98.5% confidence)",
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldSuccess,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            StatusChip(status = record.status, lang = lang)
        }
    }
}
