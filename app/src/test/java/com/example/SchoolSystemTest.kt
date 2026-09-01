package com.example

import com.example.biometrics.FaceRecognitionEngine
import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.AttendanceStatus
import com.example.data.model.MonthlyPayrollEntity
import com.example.data.model.PayrollStatus
import com.example.data.model.SalaryMasterEntity
import com.example.data.model.TeacherEntity
import com.example.i18n.AppLanguage
import com.example.reports.ReportGenerator
import com.example.whatsapp.WhatsAppService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SchoolSystemTest {

    @Test
    fun testBiometricFaceMatching() {
        val empId = "EMP001"
        val enrolledEmbedding = FaceRecognitionEngine.extractEmbedding(empId)
        assertNotNull(enrolledEmbedding)
        assertEquals(128, enrolledEmbedding.vector.size)

        // Live capture simulation
        val liveEmbedding = FaceRecognitionEngine.extractEmbedding(empId, noiseFactor = 0.01f)
        val verifyResult = FaceRecognitionEngine.verifyFace(
            liveEmbedding = liveEmbedding.vector,
            storedSerializedEmbedding = enrolledEmbedding.toSerializedString(),
            targetTeacherId = "T001"
        )

        assertTrue("Biometric verification should match with high confidence", verifyResult.isSuccess)
        assertTrue("Confidence score should be above 0.80", verifyResult.confidence >= 0.80f)
        assertTrue("Liveness score should be above 0.90", verifyResult.livenessScore >= 0.90f)
    }

    @Test
    fun testWhatsAppNotificationFormatting() {
        val loginMsg = WhatsAppService.generateLoginMessage(
            teacherName = "Anand Patil",
            date = "2026-09-01",
            time = "08:55 AM",
            status = "PRESENT",
            lang = AppLanguage.ENGLISH
        )
        assertTrue(loginMsg.contains("SM PUBLIC SCHOOL, JALIHAL"))
        assertTrue(loginMsg.contains("Anand Patil"))
        assertTrue(loginMsg.contains("08:55 AM"))

        val lateMsg = WhatsAppService.generateLateMessage(
            teacherName = "Sunita Kulkarni",
            time = "09:22 AM",
            lateMinutes = 7,
            lang = AppLanguage.ENGLISH
        )
        assertTrue(lateMsg.contains("LATE"))
        assertTrue(lateMsg.contains("7 minutes"))
    }

    @Test
    fun testNumberToWordsConversion() {
        val words1 = ReportGenerator.convertNumberToWords(33800L)
        assertEquals("Thirty Three Thousand Eight Hundred Rupees Only", words1)

        val words2 = ReportGenerator.convertNumberToWords(114500L)
        assertEquals("One Lakh Fourteen Thousand Five Hundred Rupees Only", words2)
    }

    @Test
    fun testReportWorkbookBundleGeneration() {
        val teachers = listOf(
            TeacherEntity(
                teacherId = "T001",
                employeeId = "EMP001",
                name = "Anand Patil",
                nameKannada = "ಆನಂದ್ ಪಾಟೀಲ್",
                mobileNumber = "+91 98450 11111",
                email = "anand@smps.edu",
                designation = "Senior Mathematics Teacher",
                basicSalary = 28000.0,
                hra = 3500.0,
                da = 2000.0,
                allowances = 1000.0,
                isFaceRegistered = true
            )
        )

        val attendance = listOf(
            AttendanceRecordEntity(
                attendanceId = "ATT001",
                teacherId = "T001",
                employeeId = "EMP001",
                teacherName = "Anand Patil",
                date = "2026-09-01",
                loginTime = "08:55 AM",
                logoutTime = "05:05 PM",
                status = AttendanceStatus.PRESENT,
                faceVerified = true
            )
        )

        val salaries = listOf(
            SalaryMasterEntity(
                salaryId = "SM001",
                teacherId = "T001",
                employeeId = "EMP001",
                basicSalary = 28000.0,
                hra = 3500.0,
                da = 2000.0,
                specialAllowance = 1000.0
            )
        )

        val payrolls = listOf(
            MonthlyPayrollEntity(
                payrollId = "PAY001",
                month = "September",
                year = 2026,
                monthYearKey = "2026-09",
                teacherId = "T001",
                employeeId = "EMP001",
                teacherName = "Anand Patil",
                designation = "Senior Mathematics Teacher",
                totalWorkingDays = 26,
                presentDays = 26.0,
                absentDays = 0.0,
                leaveDays = 0.0,
                lateMinutesTotal = 0,
                basicSalary = 28000.0,
                allowances = 6500.0,
                bonus = 0.0,
                grossSalary = 34500.0,
                totalDeductions = 700.0,
                netSalary = 33800.0,
                status = PayrollStatus.APPROVED
            )
        )

        val bundle = ReportGenerator.generateExcelWorkbookBundle(
            teachers = teachers,
            attendance = attendance,
            leaves = emptyList(),
            salaries = salaries,
            payroll = payrolls
        )

        assertEquals(5, bundle.size)
        assertTrue(bundle.containsKey("SHEET1_Teacher_Master.csv"))
        assertTrue(bundle.containsKey("SHEET2_Daily_Attendance.csv"))
        assertTrue(bundle.containsKey("SHEET3_Leave_Records.csv"))
        assertTrue(bundle.containsKey("SHEET4_Salary_Master.csv"))
        assertTrue(bundle.containsKey("SHEET5_Monthly_Summary.csv"))

        val payrollSheet = bundle["SHEET5_Monthly_Summary.csv"]
        assertNotNull(payrollSheet)
        assertTrue(payrollSheet!!.contains("Anand Patil"))
        assertTrue(payrollSheet.contains("33800.0"))
    }
}

