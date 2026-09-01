package com.example.whatsapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.LeaveRequestEntity
import com.example.data.model.MonthlyPayrollEntity
import com.example.i18n.AppLanguage
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object WhatsAppService {

    fun generateLoginMessage(
        teacherName: String,
        date: String,
        time: String,
        status: String,
        lang: AppLanguage = AppLanguage.ENGLISH
    ): String {
        return if (lang == AppLanguage.KANNADA) {
            """
            *ಎಸ್.ಎಂ. ಪಬ್ಲಿಕ್ ಶಾಲೆ, ಜಾಲಿಹಾಳ*
            
            ಆತ್ಮೀಯ ${teacherName},
            
            ನಿಮ್ಮ ಹಾಜರಾತಿ ಯಶಸ್ವಿಯಾಗಿ ದಾಖಲಾಗಿದೆ.
            
            📅 ದಿನಾಂಕ: ${date}
            ⏰ ಲಾಗಿನ್ ಸಮಯ: ${time}
            📊 ಸ್ಥಿತಿ: ${if (status == "PRESENT") "ಹಾಜರು (PRESENT)" else status}
            
            ಧನ್ಯವಾದಗಳು.
            """.trimIndent()
        } else {
            """
            *SM PUBLIC SCHOOL, JALIHAL*
            
            Dear ${teacherName},
            
            Your attendance has been successfully recorded.
            
            Date: ${date}
            Login Time: ${time}
            Status: ${status}
            
            Thank you.
            """.trimIndent()
        }
    }

    fun generateLogoutMessage(
        teacherName: String,
        date: String,
        time: String,
        status: String,
        lang: AppLanguage = AppLanguage.ENGLISH
    ): String {
        return if (lang == AppLanguage.KANNADA) {
            """
            *ಎಸ್.ಎಂ. ಪಬ್ಲಿಕ್ ಶಾಲೆ, ಜಾಲಿಹಾಳ*
            
            ಆತ್ಮೀಯ ${teacherName},
            
            ನಿಮ್ಮ ಲಾಗ್ಔಟ್ ಯಶಸ್ವಿಯಾಗಿ ದಾಖಲಾಗಿದೆ.
            
            📅 ದಿನಾಂಕ: ${date}
            ⏰ ಲಾಗ್ಔಟ್ ಸಮಯ: ${time}
            📊 ಸ್ಥಿತಿ: ${status}
            
            ಧನ್ಯವಾದಗಳು.
            """.trimIndent()
        } else {
            """
            *SM PUBLIC SCHOOL, JALIHAL*
            
            Dear ${teacherName},
            
            Your logout has been successfully recorded.
            
            Date: ${date}
            Logout Time: ${time}
            Status: ${status}
            
            Thank you.
            """.trimIndent()
        }
    }

    fun generateLateMessage(
        teacherName: String,
        time: String,
        lateMinutes: Int,
        lang: AppLanguage = AppLanguage.ENGLISH
    ): String {
        return if (lang == AppLanguage.KANNADA) {
            """
            *ಎಸ್.ಎಂ. ಪಬ್ಲಿಕ್ ಶಾಲೆ, ಜಾಲಿಹಾಳ*
            
            ಆತ್ಮೀಯ ${teacherName},
            
            ಇಂದು ನಿಮ್ಮ ಲಾಗಿನ್ ಸಮಯ ${time}.
            
            ಸ್ಥಿತಿ: ತಡವಾಗಿ ಬಂದವರು (LATE)
            ತಡವಾದ ಅವಧಿ: ${lateMinutes} ನಿಮಿಷಗಳು.
            
            ಅಗತ್ಯವಿದ್ದಲ್ಲಿ ದಯವಿಟ್ಟು ಶಾಲಾ ಆಡಳಿತ ಕಚೇರಿಯನ್ನು ಸಂಪರ್ಕಿಸಿ.
            """.trimIndent()
        } else {
            """
            *SM PUBLIC SCHOOL, JALIHAL*
            
            Dear ${teacherName},
            
            Your login today was at ${time}.
            
            Status: LATE
            Late Duration: ${lateMinutes} minutes.
            
            Please contact the administration if required.
            """.trimIndent()
        }
    }

    fun generateLeaveStatusMessage(
        teacherName: String,
        leave: LeaveRequestEntity,
        lang: AppLanguage = AppLanguage.ENGLISH
    ): String {
        val statusText = if (leave.status.name == "APPROVED") "APPROVED" else "REJECTED"
        return """
            *SM PUBLIC SCHOOL, JALIHAL*
            
            Dear ${teacherName},
            
            Your leave application for ${leave.fromDate} to ${leave.toDate} (${leave.totalDays} day(s)) has been *${statusText}*.
            
            Reason: ${leave.reason}
            Review Note: ${leave.reviewComment ?: "Processed by Administration"}
            
            Thank you.
        """.trimIndent()
    }

    fun generatePayrollMessage(
        payroll: MonthlyPayrollEntity,
        lang: AppLanguage = AppLanguage.ENGLISH
    ): String {
        val formattedGross = String.format("%,.2f", payroll.grossSalary)
        val formattedDeductions = String.format("%,.2f", payroll.totalDeductions)
        val formattedNet = String.format("%,.2f", payroll.netSalary)

        return """
            *SM PUBLIC SCHOOL, JALIHAL*
            
            Dear ${payroll.teacherName},
            
            Your salary for ${payroll.month} ${payroll.year} has been generated.
            
            Working Days: ${payroll.totalWorkingDays}
            Present: ${payroll.presentDays}
            Leave: ${payroll.leaveDays}
            Absent: ${payroll.absentDays}
            
            Gross Salary: ₹${formattedGross}
            Deductions: ₹${formattedDeductions}
            
            *Net Salary: ₹${formattedNet}*
            
            Salary Slip: https://smpsjalihal.edu.in/portal/salary-slip/${payroll.payrollId}
            
            SM Public School Administration
        """.trimIndent()
    }

    fun openWhatsAppIntent(context: Context, mobile: String, messageText: String): Boolean {
        return try {
            val cleanPhone = mobile.replace("+", "").replace(" ", "").replace("-", "")
            val encodedMsg = URLEncoder.encode(messageText, StandardCharsets.UTF_8.toString())
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=$encodedMsg")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }
}
