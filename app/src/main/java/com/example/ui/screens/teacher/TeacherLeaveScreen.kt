package com.example.ui.screens.teacher

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TimeToLeave
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeaveRequestEntity
import com.example.data.model.LeaveStatus
import com.example.data.model.LeaveType
import com.example.data.model.TeacherEntity
import com.example.i18n.AppLanguage
import com.example.i18n.Translations
import com.example.ui.theme.AmberBg
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldBg
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.PurpleBg
import com.example.ui.theme.PurpleRole
import com.example.ui.theme.RoseBg
import com.example.ui.theme.RoseError
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherLeaveScreen(
    teacher: TeacherEntity,
    leaveHistory: List<LeaveRequestEntity>,
    currentLanguage: AppLanguage,
    onBackClick: () -> Unit,
    onSubmitLeave: (LeaveType, String, String, Double, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Apply, 1: History

    var selectedLeaveType by remember { mutableStateOf(LeaveType.CASUAL) }
    var leaveTypeExpanded by remember { mutableStateOf(false) }

    var fromDate by remember { mutableStateOf("2026-09-10") }
    var toDate by remember { mutableStateOf("2026-09-11") }
    var totalDays by remember { mutableStateOf("2.0") }
    var reasonText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = Translations.get("apply_leave", currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Leave Balance: 12 CL • 8 SL • 15 EL",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldAmber
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("leave_back_btn")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
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
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = NavyPrimary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Apply Leave", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Leave History (${leaveHistory.size})", fontWeight = FontWeight.Bold) }
                )
            }

            if (selectedTab == 0) {
                // Apply Form
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "New Leave Application",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            // Leave Type Dropdown
                            ExposedDropdownMenuBox(
                                expanded = leaveTypeExpanded,
                                onExpandedChange = { leaveTypeExpanded = !leaveTypeExpanded }
                            ) {
                                OutlinedTextField(
                                    value = selectedLeaveType.name + " LEAVE",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Leave Type") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = leaveTypeExpanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                        .testTag("leave_type_dropdown"),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = leaveTypeExpanded,
                                    onDismissRequest = { leaveTypeExpanded = false }
                                ) {
                                    LeaveType.values().forEach { type ->
                                        DropdownMenuItem(
                                            text = { Text(type.name + " LEAVE") },
                                            onClick = {
                                                selectedLeaveType = type
                                                leaveTypeExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = fromDate,
                                    onValueChange = { fromDate = it },
                                    label = { Text("From Date") },
                                    modifier = Modifier.weight(1f).testTag("leave_from_input"),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                OutlinedTextField(
                                    value = toDate,
                                    onValueChange = { toDate = it },
                                    label = { Text("To Date") },
                                    modifier = Modifier.weight(1f).testTag("leave_to_input"),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = totalDays,
                                onValueChange = { totalDays = it },
                                label = { Text("Total Days") },
                                modifier = Modifier.fillMaxWidth().testTag("leave_days_input"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = reasonText,
                                onValueChange = { reasonText = it },
                                label = { Text("Reason for Leave") },
                                placeholder = { Text("Enter detailed reason...") },
                                minLines = 3,
                                modifier = Modifier.fillMaxWidth().testTag("leave_reason_input"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    if (reasonText.isBlank()) {
                                        Toast.makeText(context, "Please enter leave reason", Toast.LENGTH_SHORT).show()
                                    } else {
                                        val days = totalDays.toDoubleOrNull() ?: 1.0
                                        onSubmitLeave(selectedLeaveType, fromDate, toDate, days, reasonText)
                                        Toast.makeText(context, "Leave Application Submitted to Principal", Toast.LENGTH_LONG).show()
                                        reasonText = ""
                                        selectedTab = 1
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("submit_leave_btn"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Submit Application", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }
            } else {
                // History List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(leaveHistory) { leave ->
                        LeaveHistoryCard(leave = leave)
                    }
                }
            }
        }
    }
}

@Composable
fun LeaveHistoryCard(leave: LeaveRequestEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${leave.leaveType} LEAVE (${leave.totalDays} Day(s))",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = NavyDark
                )

                val (badgeBg, badgeText) = when (leave.status) {
                    LeaveStatus.APPROVED -> Pair(EmeraldBg, EmeraldSuccess)
                    LeaveStatus.REJECTED -> Pair(RoseBg, RoseError)
                    LeaveStatus.PENDING -> Pair(AmberBg, AmberWarning)
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeBg
                ) {
                    Text(
                        text = leave.status.name,
                        color = badgeText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Period: ${leave.fromDate} to ${leave.toDate}",
                style = MaterialTheme.typography.bodySmall,
                color = Slate600
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Reason: ${leave.reason}",
                style = MaterialTheme.typography.bodyMedium,
                color = Slate800
            )

            if (leave.reviewComment != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Slate100,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Admin Note: ${leave.reviewComment}",
                        fontSize = 11.sp,
                        color = Slate600,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
