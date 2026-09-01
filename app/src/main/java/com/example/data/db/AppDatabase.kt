package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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

@Database(
    entities = [
        UserEntity::class,
        TeacherEntity::class,
        FaceProfileEntity::class,
        AttendanceRecordEntity::class,
        LeaveRequestEntity::class,
        SalaryMasterEntity::class,
        SalaryAdvanceEntity::class,
        SalaryDeductionEntity::class,
        MonthlyPayrollEntity::class,
        WhatsAppMessageEntity::class,
        NotificationEntity::class,
        SchoolSettingsEntity::class,
        AuditLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun teacherDao(): TeacherDao
    abstract fun faceProfileDao(): FaceProfileDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun leaveDao(): LeaveDao
    abstract fun salaryDao(): SalaryDao
    abstract fun payrollDao(): PayrollDao
    abstract fun whatsAppDao(): WhatsAppDao
    abstract fun notificationDao(): NotificationDao
    abstract fun settingsDao(): SettingsDao
    abstract fun auditDao(): AuditDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smps_school_management.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
