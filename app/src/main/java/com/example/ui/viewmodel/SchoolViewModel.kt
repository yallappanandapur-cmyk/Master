package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.biometrics.VerificationResult
import com.example.data.db.AppDatabase
import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.AttendanceStatus
import com.example.data.model.AuditLogEntity
import com.example.data.model.LeaveRequestEntity
import com.example.data.model.LeaveType
import com.example.data.model.MonthlyPayrollEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.SchoolSettingsEntity
import com.example.data.model.TeacherEntity
import com.example.data.model.UserRole
import com.example.data.model.WhatsAppMessageEntity
import com.example.data.repository.SchoolRepository
import com.example.i18n.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    LOGIN,
    TEACHER_HOME,
    FACE_ATTENDANCE,
    MY_ATTENDANCE,
    MY_SALARY,
    APPLY_LEAVE,
    NOTIFICATIONS,
    TEACHER_PROFILE,
    ADMIN_DASHBOARD
}

class SchoolViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = SchoolRepository(db)

    val teachers: StateFlow<List<TeacherEntity>> = repository.allTeachers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val attendanceRecords: StateFlow<List<AttendanceRecordEntity>> = repository.allAttendance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val leaves: StateFlow<List<LeaveRequestEntity>> = repository.allLeaves
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payrolls: StateFlow<List<MonthlyPayrollEntity>> = repository.allPayrolls
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val whatsAppLogs: StateFlow<List<WhatsAppMessageEntity>> = repository.allWhatsAppLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<SchoolSettingsEntity?> = repository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val auditLogs: StateFlow<List<AuditLogEntity>> = repository.auditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Session State
    private val _currentScreen = MutableStateFlow(AppScreen.LOGIN)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _currentTeacher = MutableStateFlow<TeacherEntity?>(null)
    val currentTeacher: StateFlow<TeacherEntity?> = _currentTeacher.asStateFlow()

    private val _currentAdminRole = MutableStateFlow<UserRole?>(null)
    val currentAdminRole: StateFlow<UserRole?> = _currentAdminRole.asStateFlow()

    private val _isFaceLoginMode = MutableStateFlow(true)
    val isFaceLoginMode: StateFlow<Boolean> = _isFaceLoginMode.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun toggleLanguage() {
        _currentLanguage.value = if (_currentLanguage.value == AppLanguage.ENGLISH)
            AppLanguage.KANNADA else AppLanguage.ENGLISH
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun loginTeacher(teacher: TeacherEntity) {
        _currentTeacher.value = teacher
        _currentAdminRole.value = null
        _currentScreen.value = AppScreen.TEACHER_HOME
    }

    fun loginAdmin(role: UserRole) {
        _currentAdminRole.value = role
        _currentTeacher.value = null
        _currentScreen.value = AppScreen.ADMIN_DASHBOARD
    }

    fun openFaceAttendance(isLogin: Boolean) {
        _isFaceLoginMode.value = isLogin
        _currentScreen.value = AppScreen.FACE_ATTENDANCE
    }

    suspend fun recordFaceAttendance(teacherId: String, isLogin: Boolean): VerificationResult {
        return repository.recordFaceAttendance(teacherId, isLogin, _currentLanguage.value)
    }

    fun applyLeave(leaveType: LeaveType, fromDate: String, toDate: String, totalDays: Double, reason: String) {
        val teacherId = _currentTeacher.value?.teacherId ?: return
        viewModelScope.launch {
            repository.submitLeaveRequest(teacherId, leaveType, fromDate, toDate, totalDays, reason)
        }
    }

    fun reviewLeave(leave: LeaveRequestEntity, isApproved: Boolean, comment: String) {
        viewModelScope.launch {
            val updated = leave.copy(
                status = if (isApproved) com.example.data.model.LeaveStatus.APPROVED else com.example.data.model.LeaveStatus.REJECTED,
                reviewedBy = _currentAdminRole.value?.name ?: "Principal",
                reviewComment = comment
            )
            repository.updateLeaveEntity(updated)
        }
    }

    fun generatePayroll() {
        viewModelScope.launch {
            repository.generatePayrollForStaff("2026-09", "September", 2026)
        }
    }

    fun lockPayroll(monthYearKey: String) {
        viewModelScope.launch {
            val role = _currentAdminRole.value?.name ?: "ADMIN"
            repository.lockPayroll(monthYearKey, role, "Admin Office")
        }
    }

    fun correctAttendanceManually(recordId: String, newStatus: AttendanceStatus, reason: String) {
        viewModelScope.launch {
            repository.correctAttendanceManually(recordId, newStatus, reason, _currentAdminRole.value?.name ?: "Principal")
        }
    }

    fun registerTeacherFace(teacherId: String) {
        viewModelScope.launch {
            repository.registerTeacherFace(teacherId)
        }
    }

    fun addTeacher(teacher: TeacherEntity) {
        viewModelScope.launch {
            val user = com.example.data.model.UserEntity(
                id = "U_${teacher.teacherId}",
                username = teacher.employeeId,
                name = teacher.name,
                role = UserRole.TEACHER,
                mobile = teacher.mobileNumber,
                email = teacher.email,
                teacherId = teacher.teacherId
            )
            repository.addTeacher(teacher, user)
        }
    }

    fun saveSettings(newSettings: SchoolSettingsEntity) {
        viewModelScope.launch {
            repository.saveSettings(newSettings)
        }
    }
}
