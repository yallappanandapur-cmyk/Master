package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.AuditLogEntity
import com.example.data.model.FaceProfileEntity
import com.example.data.model.LeaveRequestEntity
import com.example.data.model.MonthlyPayrollEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.SalaryAdvanceEntity
import com.example.data.model.SalaryDeductionEntity
import com.example.data.model.SalaryMasterEntity
import com.example.data.model.SchoolSettingsEntity
import com.example.data.model.TeacherEntity
import com.example.data.model.UserEntity
import com.example.data.model.WhatsAppMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)
}

@Dao
interface TeacherDao {
    @Query("SELECT * FROM teachers ORDER BY employeeId ASC")
    fun getAllTeachers(): Flow<List<TeacherEntity>>

    @Query("SELECT * FROM teachers WHERE status = 'ACTIVE' ORDER BY employeeId ASC")
    fun getActiveTeachers(): Flow<List<TeacherEntity>>

    @Query("SELECT * FROM teachers WHERE teacherId = :teacherId LIMIT 1")
    suspend fun getTeacherById(teacherId: String): TeacherEntity?

    @Query("SELECT * FROM teachers WHERE employeeId = :employeeId OR mobileNumber = :employeeId LIMIT 1")
    suspend fun getTeacherByEmpIdOrMobile(employeeId: String): TeacherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeacher(teacher: TeacherEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeachers(teachers: List<TeacherEntity>)

    @Update
    suspend fun updateTeacher(teacher: TeacherEntity)

    @Query("UPDATE teachers SET isFaceRegistered = :isRegistered, faceRegisteredAt = :registeredAt WHERE teacherId = :teacherId")
    suspend fun updateFaceStatus(teacherId: String, isRegistered: Boolean, registeredAt: Long?)

    @Delete
    suspend fun deleteTeacher(teacher: TeacherEntity)
}

@Dao
interface FaceProfileDao {
    @Query("SELECT * FROM face_profiles WHERE teacherId = :teacherId LIMIT 1")
    suspend fun getFaceProfileByTeacherId(teacherId: String): FaceProfileEntity?

    @Query("SELECT * FROM face_profiles")
    suspend fun getAllFaceProfiles(): List<FaceProfileEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFaceProfile(profile: FaceProfileEntity)

    @Query("DELETE FROM face_profiles WHERE teacherId = :teacherId")
    suspend fun deleteFaceProfile(teacherId: String)
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance ORDER BY date DESC, createdTime DESC")
    fun getAllAttendance(): Flow<List<AttendanceRecordEntity>>

    @Query("SELECT * FROM attendance WHERE date = :date ORDER BY createdTime DESC")
    fun getAttendanceForDate(date: String): Flow<List<AttendanceRecordEntity>>

    @Query("SELECT * FROM attendance WHERE teacherId = :teacherId ORDER BY date DESC")
    fun getAttendanceForTeacher(teacherId: String): Flow<List<AttendanceRecordEntity>>

    @Query("SELECT * FROM attendance WHERE teacherId = :teacherId AND date = :date LIMIT 1")
    suspend fun getTodayAttendance(teacherId: String, date: String): AttendanceRecordEntity?

    @Query("SELECT * FROM attendance WHERE date LIKE :monthPrefix || '%' ORDER BY date DESC")
    fun getMonthlyAttendance(monthPrefix: String): Flow<List<AttendanceRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(record: AttendanceRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAttendance(records: List<AttendanceRecordEntity>)

    @Update
    suspend fun updateAttendance(record: AttendanceRecordEntity)
}

@Dao
interface LeaveDao {
    @Query("SELECT * FROM leave_requests ORDER BY appliedOn DESC")
    fun getAllLeaves(): Flow<List<LeaveRequestEntity>>

    @Query("SELECT * FROM leave_requests WHERE teacherId = :teacherId ORDER BY appliedOn DESC")
    fun getLeavesForTeacher(teacherId: String): Flow<List<LeaveRequestEntity>>

    @Query("SELECT * FROM leave_requests WHERE status = 'PENDING' ORDER BY appliedOn ASC")
    fun getPendingLeaves(): Flow<List<LeaveRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeave(leave: LeaveRequestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaves(leaves: List<LeaveRequestEntity>)

    @Update
    suspend fun updateLeave(leave: LeaveRequestEntity)
}

@Dao
interface SalaryDao {
    @Query("SELECT * FROM salary_master WHERE teacherId = :teacherId LIMIT 1")
    suspend fun getSalaryMaster(teacherId: String): SalaryMasterEntity?

    @Query("SELECT * FROM salary_master")
    fun getAllSalaryMasters(): Flow<List<SalaryMasterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSalaryMaster(salary: SalaryMasterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSalaryMasters(salaries: List<SalaryMasterEntity>)

    @Query("SELECT * FROM salary_advances WHERE teacherId = :teacherId AND deductionMonth = :month")
    suspend fun getAdvancesForTeacher(teacherId: String, month: String): List<SalaryAdvanceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdvance(advance: SalaryAdvanceEntity)

    @Query("SELECT * FROM salary_deductions WHERE teacherId = :teacherId AND month = :month")
    suspend fun getDeductionsForTeacher(teacherId: String, month: String): List<SalaryDeductionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeduction(deduction: SalaryDeductionEntity)
}

@Dao
interface PayrollDao {
    @Query("SELECT * FROM monthly_payroll ORDER BY year DESC, month ASC, employeeId ASC")
    fun getAllPayroll(): Flow<List<MonthlyPayrollEntity>>

    @Query("SELECT * FROM monthly_payroll WHERE monthYearKey = :monthYearKey ORDER BY employeeId ASC")
    fun getPayrollForMonth(monthYearKey: String): Flow<List<MonthlyPayrollEntity>>

    @Query("SELECT * FROM monthly_payroll WHERE teacherId = :teacherId ORDER BY year DESC, monthYearKey DESC")
    fun getPayrollForTeacher(teacherId: String): Flow<List<MonthlyPayrollEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayrollRecords(records: List<MonthlyPayrollEntity>)

    @Update
    suspend fun updatePayrollRecord(record: MonthlyPayrollEntity)

    @Query("UPDATE monthly_payroll SET status = :status, lockedAt = :lockedAt, lockedBy = :lockedBy WHERE monthYearKey = :monthYearKey")
    suspend fun lockPayroll(monthYearKey: String, status: com.example.data.model.PayrollStatus, lockedAt: Long, lockedBy: String)
}

@Dao
interface WhatsAppDao {
    @Query("SELECT * FROM whatsapp_messages ORDER BY sentAt DESC")
    fun getAllMessages(): Flow<List<WhatsAppMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: WhatsAppMessageEntity)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE targetTeacherId IS NULL OR targetTeacherId = :teacherId ORDER BY timestamp DESC")
    fun getNotificationsForTeacher(teacherId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM school_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<SchoolSettingsEntity?>

    @Query("SELECT * FROM school_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): SchoolSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: SchoolSettingsEntity)
}

@Dao
interface AuditDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLogEntity)
}
