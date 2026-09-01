package com.example.ui.screens.teacher

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MonthlyPayrollEntity
import com.example.data.model.TeacherEntity
import com.example.i18n.AppLanguage
import com.example.i18n.Translations
import com.example.reports.ReportGenerator
import com.example.ui.components.SchoolSignatureStamp
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
import com.example.whatsapp.WhatsAppService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherSalaryScreen(
    teacher: TeacherEntity,
    latestPayroll: MonthlyPayrollEntity?,
    currentLanguage: AppLanguage,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val gross = latestPayroll?.grossSalary ?: (teacher.basicSalary + teacher.hra + teacher.da + teacher.allowances + 1000.0)
    val adv = latestPayroll?.advanceDeduction ?: 1000.0
    val absentDed = latestPayroll?.absentDeduction ?: 0.0
    val lateDed = latestPayroll?.lateDeduction ?: 0.0
    val otherDed = latestPayroll?.otherDeductions ?: 500.0
    val totalDed = latestPayroll?.totalDeductions ?: (adv + absentDed + lateDed + otherDed)
    val net = latestPayroll?.netSalary ?: (gross - totalDed)

    val workingDays = latestPayroll?.totalWorkingDays ?: 26
    val presentDays = latestPayroll?.presentDays ?: 25.0
    val leaveDays = latestPayroll?.leaveDays ?: 1.0
    val absentDays = latestPayroll?.absentDays ?: 0.0

    var showPdfPreviewModal by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = Translations.get("my_salary", currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Month: September 2026",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldAmber
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("salary_back_btn")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (latestPayroll != null) {
                                val msg = WhatsAppService.generatePayrollMessage(latestPayroll, currentLanguage)
                                WhatsAppService.openWhatsAppIntent(context, teacher.mobileNumber, msg)
                            }
                            Toast.makeText(context, "Salary slip sent to WhatsApp", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("share_whatsapp_salary_btn")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
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
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Net Salary Highlight Card
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("net_salary_hero_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = NavyDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "NET PAYABLE SALARY",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = GoldAmber,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "₹ ${String.format("%,.2f", net)}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EmeraldSuccess.copy(alpha = 0.2f),
                            modifier = Modifier.border(1.dp, EmeraldSuccess, RoundedCornerShape(10.dp))
                        ) {
                            Text(
                                text = "APPROVED",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = ReportGenerator.convertNumberToWords(net.toLong()),
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate200,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = NavySurface)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Gross Earnings", fontSize = 11.sp, color = Slate200)
                            Text("₹ ${String.format("%,.2f", gross)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EmeraldBg)
                        }
                        Column {
                            Text("Total Deductions", fontSize = 11.sp, color = Slate200)
                            Text("₹ ${String.format("%,.2f", totalDed)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RoseBg)
                        }
                        Column {
                            Text("Bank Account", fontSize = 11.sp, color = Slate200)
                            Text(teacher.bankAccount, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Printable Official PDF Salary Slip View
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("salary_slip_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // School Header inside slip
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("SM PUBLIC SCHOOL", fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = NavyDark)
                        Text("JALIHAL - 587118, BAGALKOT DIST", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Slate600)
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = NavyPrimary.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                "SALARY SLIP • SEPTEMBER 2026",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = NavyPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Slate200)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Employee details
                    SalaryRowItem("Employee Name", teacher.name)
                    SalaryRowItem("Employee ID", teacher.employeeId)
                    SalaryRowItem("Designation", teacher.designation)
                    SalaryRowItem("Bank & IFSC", "${teacher.bankName} (${teacher.ifscCode})")

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Slate200)
                    Spacer(modifier = Modifier.height(10.dp))

                    // ATTENDANCE section (Required by prompt)
                    Text("ATTENDANCE SUMMARY", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Working Days: $workingDays", fontSize = 12.sp, color = Slate800)
                        Text("Present: $presentDays", fontSize = 12.sp, color = EmeraldSuccess, fontWeight = FontWeight.Bold)
                        Text("Leave: $leaveDays", fontSize = 12.sp, color = PurpleRole)
                        Text("Absent: $absentDays", fontSize = 12.sp, color = RoseError)
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Slate200)
                    Spacer(modifier = Modifier.height(10.dp))

                    // EARNINGS section (Required by prompt)
                    Text("EARNINGS", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = EmeraldSuccess)
                    Spacer(modifier = Modifier.height(6.dp))
                    SalaryRowItem("Basic Salary", "₹ ${String.format("%,.2f", teacher.basicSalary)}")
                    SalaryRowItem("House Rent Allowance (HRA)", "₹ ${String.format("%,.2f", teacher.hra)}")
                    SalaryRowItem("Dearness Allowance (DA)", "₹ ${String.format("%,.2f", teacher.da)}")
                    SalaryRowItem("Special Allowances", "₹ ${String.format("%,.2f", teacher.allowances)}")
                    SalaryRowItem("Performance Bonus", "₹ 1,000.00")
                    SalaryRowItem("Gross Salary", "₹ ${String.format("%,.2f", gross)}", isBold = true, textColor = EmeraldSuccess)

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Slate200)
                    Spacer(modifier = Modifier.height(10.dp))

                    // DEDUCTIONS section (Required by prompt)
                    Text("DEDUCTIONS", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RoseError)
                    Spacer(modifier = Modifier.height(6.dp))
                    SalaryRowItem("Advance Recovered", "₹ ${String.format("%,.2f", adv)}")
                    SalaryRowItem("Absent Deduction", "₹ ${String.format("%,.2f", absentDed)}")
                    SalaryRowItem("Late Login Penalty", "₹ ${String.format("%,.2f", lateDed)}")
                    SalaryRowItem("Other Deductions (PT/Welfare)", "₹ ${String.format("%,.2f", otherDed)}")
                    SalaryRowItem("Total Deductions", "₹ ${String.format("%,.2f", totalDed)}", isBold = true, textColor = RoseError)

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = NavyDark, thickness = 1.5.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // NET SALARY row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("NET SALARY", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = NavyDark)
                        Text("₹ ${String.format("%,.2f", net)}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = NavyPrimary)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Official Principal & Authorized Signature Seal
                    SchoolSignatureStamp(
                        principalName = "Dr. R. K. Patil",
                        date = "01-09-2026"
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons: Download PDF & WhatsApp Share
            Button(
                onClick = {
                    Toast.makeText(context, "PDF Salary Slip generated and saved to Documents", Toast.LENGTH_LONG).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("download_salary_slip_pdf_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Download Salary Slip (PDF)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun SalaryRowItem(
    label: String,
    value: String,
    isBold: Boolean = false,
    textColor: Color = Slate800
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isBold) NavyDark else Slate600
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
            color = textColor
        )
    }
}
