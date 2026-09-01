package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    SUPER_ADMIN,
    PRINCIPAL,
    ACCOUNTANT,
    TEACHER
}

enum class AttendanceStatus {
    PRESENT,
    ABSENT,
    LATE,
    HALF_DAY,
    LEAVE,
    HOLIDAY,
    NOT_MARKED
}

enum class LeaveType {
    CASUAL,
    SICK,
    EARNED,
    MATERNITY,
    OTHER
}

enum class LeaveStatus {
    PENDING,
    APPROVED,
    REJECTED
}

enum class PayrollStatus {
    DRAFT,
    APPROVED,
    LOCKED
}

enum class PaymentStatus {
    PENDING,
    PAID
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val name: String,
    val role: UserRole,
    val mobile: String,
    val email: String,
    val teacherId: String? = null,
    val active: Boolean = true
)

@Entity(tableName = "teachers")
data class TeacherEntity(
    @PrimaryKey val teacherId: String,
    val employeeId: String,
    val name: String,
    val nameKannada: String = "",
    val mobileNumber: String,
    val email: String,
    val address: String = "Jalihal, Karnataka",
    val designation: String,
    val department: String = "Academics",
    val joiningDate: String = "2024-06-01",
    val basicSalary: Double,
    val hra: Double = 0.0,
    val da: Double = 0.0,
    val allowances: Double = 0.0,
    val bankName: String = "State Bank of India",
    val bankAccount: String = "XXXX XXXX 1234",
    val ifscCode: String = "SBIN0001234",
    val status: String = "ACTIVE",
    val isFaceRegistered: Boolean = false,
    val faceRegisteredAt: Long? = null,
    val avatarColor: Long = 0xFF1E3A8A
)

@Entity(tableName = "face_profiles")
data class FaceProfileEntity(
    @PrimaryKey val profileId: String,
    val teacherId: String,
    val employeeId: String,
    val embeddingVector: String, // Comma-separated or JSON float vector
    val sampleCount: Int = 3,
    val livenessConfidence: Float = 0.98f,
    val registeredAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "attendance")
data class AttendanceRecordEntity(
    @PrimaryKey val attendanceId: String,
    val teacherId: String,
    val employeeId: String,
    val teacherName: String,
    val date: String, // "YYYY-MM-DD"
    val loginTime: String? = null, // "HH:mm:ss"
    val logoutTime: String? = null, // "HH:mm:ss"
    val status: AttendanceStatus = AttendanceStatus.PRESENT,
    val lateMinutes: Int = 0,
    val earlyMinutes: Int = 0,
    val faceVerified: Boolean = true,
    val livenessScore: Float = 0.99f,
    val deviceId: String = "SMPS-KIOSK-01",
    val createdTime: Long = System.currentTimeMillis(),
    val updatedTime: Long = System.currentTimeMillis(),
    val manualCorrected: Boolean = false,
    val correctionReason: String? = null,
    val correctedBy: String? = null
)

@Entity(tableName = "leave_requests")
data class LeaveRequestEntity(
    @PrimaryKey val leaveId: String,
    val teacherId: String,
    val employeeId: String,
    val teacherName: String,
    val leaveType: LeaveType,
    val fromDate: String,
    val toDate: String,
    val totalDays: Double = 1.0,
    val reason: String,
    val status: LeaveStatus = LeaveStatus.PENDING,
    val appliedOn: Long = System.currentTimeMillis(),
    val reviewedBy: String? = null,
    val reviewComment: String? = null,
    val reviewedOn: Long? = null
)

@Entity(tableName = "salary_master")
data class SalaryMasterEntity(
    @PrimaryKey val salaryId: String,
    val teacherId: String,
    val employeeId: String,
    val basicSalary: Double,
    val hra: Double,
    val da: Double,
    val specialAllowance: Double,
    val bonus: Double = 0.0,
    val lateDeductionPerMin: Double = 5.0,
    val absentDeductionPerDay: Double = 0.0,
    val otherDeductions: Double = 0.0
)

@Entity(tableName = "salary_advances")
data class SalaryAdvanceEntity(
    @PrimaryKey val advanceId: String,
    val teacherId: String,
    val employeeId: String,
    val amount: Double,
    val requestDate: String,
    val reason: String,
    val status: String = "APPROVED", // APPROVED, RECOVERED, PENDING
    val deductionMonth: String // "2026-09"
)

@Entity(tableName = "salary_deductions")
data class SalaryDeductionEntity(
    @PrimaryKey val deductionId: String,
    val teacherId: String,
    val employeeId: String,
    val amount: Double,
    val title: String,
    val description: String,
    val month: String // "2026-09"
)

@Entity(tableName = "monthly_payroll")
data class MonthlyPayrollEntity(
    @PrimaryKey val payrollId: String,
    val month: String, // "September"
    val year: Int, // 2026
    val monthYearKey: String, // "2026-09"
    val teacherId: String,
    val employeeId: String,
    val teacherName: String,
    val designation: String,
    val totalWorkingDays: Int = 26,
    val presentDays: Double = 25.0,
    val leaveDays: Double = 1.0,
    val absentDays: Double = 0.0,
    val lateMinutesTotal: Int = 15,
    val basicSalary: Double,
    val allowances: Double,
    val bonus: Double = 0.0,
    val grossSalary: Double,
    val advanceDeduction: Double = 0.0,
    val absentDeduction: Double = 0.0,
    val lateDeduction: Double = 0.0,
    val otherDeductions: Double = 0.0,
    val totalDeductions: Double = 0.0,
    val netSalary: Double,
    val status: PayrollStatus = PayrollStatus.DRAFT,
    val lockedAt: Long? = null,
    val lockedBy: String? = null,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val paidAt: Long? = null,
    val paymentRef: String? = null
)

@Entity(tableName = "whatsapp_messages")
data class WhatsAppMessageEntity(
    @PrimaryKey val messageId: String,
    val recipientMobile: String,
    val recipientName: String,
    val messageType: String, // "LOGIN", "LOGOUT", "LATE", "LEAVE_STATUS", "PAYROLL", "SALARY_PAID"
    val messageText: String,
    val sentAt: Long = System.currentTimeMillis(),
    val deliveryStatus: String = "DELIVERED",
    val payloadRef: String? = null
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val notificationId: String,
    val targetTeacherId: String? = null, // null for broadcast
    val title: String,
    val body: String,
    val type: String = "GENERAL",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "school_settings")
data class SchoolSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val schoolName: String = "SM PUBLIC SCHOOL",
    val schoolSubtitle: String = "JALIHAL",
    val address: String = "Near Bus Stand, Jalihal - 587118, Karnataka",
    val phone: String = "+91 98450 12345",
    val email: String = "principal@smpsjalihal.edu.in",
    val principalName: String = "Dr. R. K. Patil, M.Sc., B.Ed., Ph.D.",
    val expectedLoginTime: String = "09:00 AM",
    val lateAfterTime: String = "09:15 AM",
    val expectedLogoutTime: String = "05:00 PM",
    val earlyLogoutThreshold: String = "04:45 PM",
    val workingDaysPerMonth: Int = 26,
    val latePenaltyPerMinute: Double = 5.0,
    val absentDeductionFormula: String = "PER_DAY_BASIC",
    val whatsappApiConfigured: Boolean = true,
    val whatsappSenderPhone: String = "+91 98450 12345",
    val defaultLanguage: String = "EN"
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val logId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val actorRole: String,
    val actorName: String,
    val actionType: String,
    val targetEntity: String,
    val targetId: String,
    val description: String
)
