package com.example

import com.example.biometrics.FaceRecognitionEngine
import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.AttendanceStatus
import com.example.data.model.LeaveRequestEntity
import com.example.data.model.LeaveStatus
import com.example.data.model.LeaveType
import com.example.data.model.MonthlyPayrollEntity
import com.example.data.model.SalaryAdvanceEntity
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
        val teacher = TeacherEntity(
            teacherId = "T001",
            employeeId = "EMP001",
            name = "Anand Patil",
            nameKannada = "ಆನಂದ್ ಪಾಟೀಲ್",
            mobileNumber = "+91 98450 11111",
            email = "anand.patil@smpsjalihal.edu.in",
            designation = "Senior Mathematics Teacher",
            department = "Mathematics",
            basicSalary = 28000.0,
            hra = 3500.0,
            da = 2000.0,
            allowances = 1000.0,
            isFaceRegistered = true
        )

        val profile = FaceRecognitionEngine.generateEnrolledProfile(teacher)
        assertNotNull(profile)
        assertEquals("EMP001", profile.employeeId)

        // Matching with sample probe from same teacher
        val probeVectors = FaceRecognitionEngine.simulateFaceCapture("EMP001")
        val result = FaceRecognitionEngine.verifyFaceProbe(profile, probeVectors)

        assertTrue("Biometric verification should match with high confidence", result.isSuccess)
        assertTrue("Confidence score should be above 0.80", result.confidenceScore >= 0.80f)
        assertTrue("Liveness score should be above 0.90", result.livenessScore >= 0.90f)
    }

    @Test
    fun testWhatsAppNotificationFormatting() {
        val loginMsg = WhatsAppService.formatAttendanceLoginMessage(
            teacherName = "Anand Patil",
            teacherNameKannada = "ಆನಂದ್ ಪಾಟೀಲ್",
            employeeId = "EMP001",
            loginTime = "08:55 AM",
            date = "01-Sep-2026",
            status = "PRESENT",
            lang = AppLanguage.ENGLISH
        )
        assertTrue(loginMsg.contains("SM PUBLIC SCHOOL, JALIHAL"))
        assertTrue(loginMsg.contains("Anand Patil"))
        assertTrue(loginMsg.contains("08:55 AM"))

        val salaryMsg = WhatsAppService.formatSalaryDisbursedMessage(
            teacherName = "Anand Patil",
            monthYear = "September 2026",
            netSalary = 33800.0,
            presentDays = 26,
            totalDays = 26,
            bankAccount = "SBI •••• 1234",
            lang = AppLanguage.ENGLISH
        )
        assertTrue(salaryMsg.contains("₹ 33,800.00"))
        assertTrue(salaryMsg.contains("September 2026"))
    }

    @Test
    fun testNumberToWordsConversion() {
        val words1 = ReportGenerator.convertNumberToWords(33800.0)
        assertEquals("Thirty-Three Thousand Eight Hundred Rupees Only", words1)

        val words2 = ReportGenerator.convertNumberToWords(114500.0)
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
                isBiometricVerified = true
            )
        )

        val payrolls = listOf(
            MonthlyPayrollEntity(
                payrollId = "PAY001",
                monthYearKey = "2026-09",
                monthName = "September",
                year = 2026,
                teacherId = "T001",
                employeeId = "EMP001",
                teacherName = "Anand Patil",
                designation = "Senior Mathematics Teacher",
                totalWorkingDays = 26,
                presentDays = 26,
                absentDays = 0,
                leaveDays = 0,
                lateDays = 0,
                basicSalary = 28000.0,
                hra = 3500.0,
                da = 2000.0,
                allowances = 1000.0,
                grossSalary = 34500.0,
                totalDeductions = 700.0,
                netSalary = 33800.0
            )
        )

        val bundle = ReportGenerator.generateExcelWorkbookBundle(
            teachers = teachers,
            attendance = attendance,
            leaves = emptyList(),
            advances = emptyList(),
            payrolls = payrolls
        )

        assertEquals(5, bundle.size)
        assertTrue(bundle.containsKey("1_Monthly_Summary"))
        assertTrue(bundle.containsKey("2_Teacher_Master"))
        assertTrue(bundle.containsKey("3_Daily_Attendance"))
        assertTrue(bundle.containsKey("4_Leave_Register"))
        assertTrue(bundle.containsKey("5_Payroll_Statement"))

        val payrollSheet = bundle["5_Payroll_Statement"]
        assertNotNull(payrollSheet)
        assertTrue(payrollSheet!!.contains("Anand Patil"))
        assertTrue(payrollSheet.contains("33800.00"))
    }
}
