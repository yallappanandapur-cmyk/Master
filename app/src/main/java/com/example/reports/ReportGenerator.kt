package com.example.reports

import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.LeaveRequestEntity
import com.example.data.model.MonthlyPayrollEntity
import com.example.data.model.SalaryMasterEntity
import com.example.data.model.TeacherEntity

object ReportGenerator {

    data class SalarySlipData(
        val schoolName: String = "SM PUBLIC SCHOOL",
        val schoolSubtitle: String = "JALIHAL - 587118, BAGALKOT DIST",
        val month: String,
        val year: Int,
        val teacherName: String,
        val employeeId: String,
        val designation: String,
        val department: String,
        val bankAccount: String,
        val workingDays: Int,
        val presentDays: Double,
        val leaveDays: Double,
        val absentDays: Double,
        val lateMinutes: Int,
        val basicSalary: Double,
        val allowances: Double,
        val bonus: Double,
        val grossSalary: Double,
        val advanceDeduction: Double,
        val absentDeduction: Double,
        val lateDeduction: Double,
        val otherDeductions: Double,
        val totalDeductions: Double,
        val netSalary: Double,
        val netSalaryWords: String,
        val generatedDate: String,
        val principalName: String
    )

    fun convertNumberToWords(num: Long): String {
        if (num == 0L) return "Zero Rupees Only"
        val units = arrayOf("", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
            "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen")
        val tens = arrayOf("", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety")

        fun helper(n: Long): String {
            return when {
                n < 20 -> units[n.toInt()]
                n < 100 -> tens[(n / 10).toInt()] + (if (n % 10 != 0L) " " + units[(n % 10).toInt()] else "")
                n < 1000 -> helper(n / 100) + " Hundred" + (if (n % 100 != 0L) " and " + helper(n % 100) else "")
                n < 100000 -> helper(n / 1000) + " Thousand" + (if (n % 1000 != 0L) " " + helper(n % 1000) else "")
                n < 10000000 -> helper(n / 100000) + " Lakh" + (if (n % 100000 != 0L) " " + helper(n % 100000) else "")
                else -> helper(n / 10000000) + " Crore" + (if (n % 10000000 != 0L) " " + helper(n % 10000000) else "")
            }
        }
        return helper(num).trim() + " Rupees Only"
    }

    /**
     * Generates a 5-Sheet formatted CSV / Workbook export bundle
     */
    fun generateExcelWorkbookBundle(
        teachers: List<TeacherEntity>,
        attendance: List<AttendanceRecordEntity>,
        leaves: List<LeaveRequestEntity>,
        salaries: List<SalaryMasterEntity>,
        payroll: List<MonthlyPayrollEntity>
    ): Map<String, String> {
        val sheets = mutableMapOf<String, String>()

        // Sheet 1: Teacher Master
        val sheet1 = StringBuilder()
        sheet1.append("SHEET 1: TEACHER MASTER - SM PUBLIC SCHOOL, JALIHAL\n")
        sheet1.append("Employee ID,Teacher Name,Mobile,Designation,Joining Date,Basic Salary (INR),Status,Face Registered\n")
        teachers.forEach { t ->
            sheet1.append("${t.employeeId},\"${t.name}\",${t.mobileNumber},\"${t.designation}\",${t.joiningDate},${t.basicSalary},${t.status},${t.isFaceRegistered}\n")
        }
        sheets["SHEET1_Teacher_Master.csv"] = sheet1.toString()

        // Sheet 2: Daily Attendance
        val sheet2 = StringBuilder()
        sheet2.append("SHEET 2: DAILY ATTENDANCE RECORD - SM PUBLIC SCHOOL, JALIHAL\n")
        sheet2.append("Date,Employee ID,Teacher Name,Login Time,Logout Time,Status,Late Minutes,Early Minutes,Face Verified\n")
        attendance.forEach { a ->
            sheet2.append("${a.date},${a.employeeId},\"${a.teacherName}\",${a.loginTime ?: "--"},${a.logoutTime ?: "--"},${a.status},${a.lateMinutes},${a.earlyMinutes},${a.faceVerified}\n")
        }
        sheets["SHEET2_Daily_Attendance.csv"] = sheet2.toString()

        // Sheet 3: Leave
        val sheet3 = StringBuilder()
        sheet3.append("SHEET 3: LEAVE MANAGEMENT - SM PUBLIC SCHOOL, JALIHAL\n")
        sheet3.append("Leave ID,Employee ID,Teacher Name,Leave From,Leave To,Total Days,Leave Type,Reason,Status\n")
        leaves.forEach { l ->
            sheet3.append("${l.leaveId},${l.employeeId},\"${l.teacherName}\",${l.fromDate},${l.toDate},${l.totalDays},${l.leaveType},\"${l.reason}\",${l.status}\n")
        }
        sheets["SHEET3_Leave_Records.csv"] = sheet3.toString()

        // Sheet 4: Salary
        val sheet4 = StringBuilder()
        sheet4.append("SHEET 4: SALARY MASTER - SM PUBLIC SCHOOL, JALIHAL\n")
        sheet4.append("Employee ID,Basic Salary,HRA,DA,Special Allowance,Bonus,Late Rate/Min\n")
        salaries.forEach { s ->
            sheet4.append("${s.employeeId},${s.basicSalary},${s.hra},${s.da},${s.specialAllowance},${s.bonus},${s.lateDeductionPerMin}\n")
        }
        sheets["SHEET4_Salary_Master.csv"] = sheet4.toString()

        // Sheet 5: Monthly Summary
        val sheet5 = StringBuilder()
        sheet5.append("SHEET 5: MONTHLY PAYROLL SUMMARY - SM PUBLIC SCHOOL, JALIHAL\n")
        sheet5.append("Employee ID,Teacher Name,Designation,Working Days,Present,Leave,Absent,Gross Salary,Advance,Late Ded,Absent Ded,Total Deductions,Net Salary,Status\n")
        payroll.forEach { p ->
            sheet5.append("${p.employeeId},\"${p.teacherName}\",\"${p.designation}\",${p.totalWorkingDays},${p.presentDays},${p.leaveDays},${p.absentDays},${p.grossSalary},${p.advanceDeduction},${p.lateDeduction},${p.absentDeduction},${p.totalDeductions},${p.netSalary},${p.status}\n")
        }
        sheets["SHEET5_Monthly_Summary.csv"] = sheet5.toString()

        return sheets
    }
}
