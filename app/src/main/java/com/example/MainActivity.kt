package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.data.model.AttendanceStatus
import com.example.data.model.UserRole
import com.example.ui.components.SchoolHeader
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.teacher.FaceAttendanceScreen
import com.example.ui.screens.teacher.NotificationsScreen
import com.example.ui.screens.teacher.TeacherAttendanceScreen
import com.example.ui.screens.teacher.TeacherHomeScreen
import com.example.ui.screens.teacher.TeacherLeaveScreen
import com.example.ui.screens.teacher.TeacherProfileScreen
import com.example.ui.screens.teacher.TeacherSalaryScreen
import com.example.ui.theme.SMPSTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SchoolViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: SchoolViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SMPSTheme {
                MainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainApp(viewModel: SchoolViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val currentTeacher by viewModel.currentTeacher.collectAsState()
    val currentAdminRole by viewModel.currentAdminRole.collectAsState()
    val isFaceLoginMode by viewModel.isFaceLoginMode.collectAsState()

    val teachers by viewModel.teachers.collectAsState()
    val attendanceRecords by viewModel.attendanceRecords.collectAsState()
    val leaves by viewModel.leaves.collectAsState()
    val payrolls by viewModel.payrolls.collectAsState()
    val whatsAppLogs by viewModel.whatsAppLogs.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()

    val activeTeacher = currentTeacher ?: teachers.firstOrNull()

    // Handle back button navigation
    BackHandler(enabled = currentScreen != AppScreen.LOGIN) {
        when (currentScreen) {
            AppScreen.LOGIN -> {}
            AppScreen.TEACHER_HOME, AppScreen.ADMIN_DASHBOARD -> viewModel.navigateTo(AppScreen.LOGIN)
            else -> {
                if (currentTeacher != null) {
                    viewModel.navigateTo(AppScreen.TEACHER_HOME)
                } else {
                    viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (currentScreen) {
                AppScreen.LOGIN -> {
                    LoginScreen(
                        currentLanguage = currentLanguage,
                        onToggleLanguage = { viewModel.toggleLanguage() },
                        teachers = teachers,
                        onTeacherLogin = { viewModel.loginTeacher(it) },
                        onAdminLogin = { viewModel.loginAdmin(it) }
                    )
                }

                AppScreen.TEACHER_HOME -> {
                    if (activeTeacher != null) {
                        val teacherAttendance = attendanceRecords.filter { it.teacherId == activeTeacher.teacherId }
                        val todayRecord = teacherAttendance.firstOrNull { it.date == "2026-09-01" } ?: teacherAttendance.firstOrNull()

                        Column(modifier = Modifier.fillMaxSize()) {
                            SchoolHeader(
                                currentLanguage = currentLanguage,
                                onToggleLanguage = { viewModel.toggleLanguage() },
                                userRoleName = "Teacher",
                                onRoleClick = { viewModel.navigateTo(AppScreen.LOGIN) }
                            )

                            TeacherHomeScreen(
                                teacher = activeTeacher,
                                todayAttendance = todayRecord,
                                currentLanguage = currentLanguage,
                                onFaceLoginClick = { viewModel.openFaceAttendance(isLogin = true) },
                                onFaceLogoutClick = { viewModel.openFaceAttendance(isLogin = false) },
                                onMyAttendanceClick = { viewModel.navigateTo(AppScreen.MY_ATTENDANCE) },
                                onMySalaryClick = { viewModel.navigateTo(AppScreen.MY_SALARY) },
                                onApplyLeaveClick = { viewModel.navigateTo(AppScreen.APPLY_LEAVE) },
                                onNotificationsClick = { viewModel.navigateTo(AppScreen.NOTIFICATIONS) },
                                onProfileClick = { viewModel.navigateTo(AppScreen.TEACHER_PROFILE) }
                            )
                        }
                    }
                }

                AppScreen.FACE_ATTENDANCE -> {
                    if (activeTeacher != null) {
                        FaceAttendanceScreen(
                            teacher = activeTeacher,
                            isLoginMode = isFaceLoginMode,
                            currentLanguage = currentLanguage,
                            onBackClick = { viewModel.navigateTo(AppScreen.TEACHER_HOME) },
                            onAttendanceRecorded = { teacherId, isLogin ->
                                viewModel.recordFaceAttendance(teacherId, isLogin)
                            }
                        )
                    }
                }

                AppScreen.MY_ATTENDANCE -> {
                    if (activeTeacher != null) {
                        val records = attendanceRecords.filter { it.teacherId == activeTeacher.teacherId }
                        TeacherAttendanceScreen(
                            teacher = activeTeacher,
                            attendanceRecords = records,
                            currentLanguage = currentLanguage,
                            onBackClick = { viewModel.navigateTo(AppScreen.TEACHER_HOME) }
                        )
                    }
                }

                AppScreen.MY_SALARY -> {
                    if (activeTeacher != null) {
                        val payroll = payrolls.firstOrNull { it.teacherId == activeTeacher.teacherId }
                        TeacherSalaryScreen(
                            teacher = activeTeacher,
                            latestPayroll = payroll,
                            currentLanguage = currentLanguage,
                            onBackClick = { viewModel.navigateTo(AppScreen.TEACHER_HOME) }
                        )
                    }
                }

                AppScreen.APPLY_LEAVE -> {
                    if (activeTeacher != null) {
                        val teacherLeaves = leaves.filter { it.teacherId == activeTeacher.teacherId }
                        TeacherLeaveScreen(
                            teacher = activeTeacher,
                            leaveHistory = teacherLeaves,
                            currentLanguage = currentLanguage,
                            onBackClick = { viewModel.navigateTo(AppScreen.TEACHER_HOME) },
                            onSubmitLeave = { type, from, to, days, reason ->
                                viewModel.applyLeave(type, from, to, days, reason)
                            }
                        )
                    }
                }

                AppScreen.NOTIFICATIONS -> {
                    val teacherWaLogs = if (activeTeacher != null) {
                        whatsAppLogs.filter { it.recipientMobile == activeTeacher.mobileNumber }
                    } else whatsAppLogs

                    NotificationsScreen(
                        notifications = notifications,
                        whatsAppLogs = if (teacherWaLogs.isNotEmpty()) teacherWaLogs else whatsAppLogs,
                        currentLanguage = currentLanguage,
                        onBackClick = { viewModel.navigateTo(AppScreen.TEACHER_HOME) }
                    )
                }

                AppScreen.TEACHER_PROFILE -> {
                    if (activeTeacher != null) {
                        TeacherProfileScreen(
                            teacher = activeTeacher,
                            currentLanguage = currentLanguage,
                            onBackClick = { viewModel.navigateTo(AppScreen.TEACHER_HOME) },
                            onReEnrollFace = { viewModel.registerTeacherFace(activeTeacher.teacherId) }
                        )
                    }
                }

                AppScreen.ADMIN_DASHBOARD -> {
                    AdminDashboardScreen(
                        currentRole = currentAdminRole ?: UserRole.PRINCIPAL,
                        teachers = teachers,
                        todayAttendance = attendanceRecords,
                        leaves = leaves,
                        payrolls = payrolls,
                        settings = settings,
                        auditLogs = auditLogs,
                        currentLanguage = currentLanguage,
                        onSwitchRole = { viewModel.navigateTo(AppScreen.LOGIN) },
                        onApproveLeave = { leave, approved, comment ->
                            viewModel.reviewLeave(leave, approved, comment)
                        },
                        onGeneratePayroll = { viewModel.generatePayroll() },
                        onLockPayroll = { viewModel.lockPayroll(it) },
                        onManualAttendanceCorrection = { recordId, status, reason ->
                            viewModel.correctAttendanceManually(recordId, status, reason)
                        },
                        onRegisterTeacherFace = { viewModel.registerTeacherFace(it) },
                        onAddTeacher = { viewModel.addTeacher(it) },
                        onSaveSettings = { viewModel.saveSettings(it) }
                    )
                }
            }
        }
    }
}

