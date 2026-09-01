package com.example.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.TeacherEntity
import com.example.data.model.UserRole
import com.example.i18n.AppLanguage
import com.example.i18n.Translations
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.EmeraldBg
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySurface
import com.example.ui.theme.PurpleBg
import com.example.ui.theme.PurpleRole
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800

@Composable
fun LoginScreen(
    currentLanguage: AppLanguage,
    onToggleLanguage: () -> Unit,
    teachers: List<TeacherEntity>,
    onTeacherLogin: (TeacherEntity) -> Unit,
    onAdminLogin: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Teacher Login, 1: School Admin & Principal Login

    var identifierInput by remember { mutableStateOf("EMP001") }
    var passwordInput by remember { mutableStateOf("******") }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Slate100)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Hero School Branding Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(NavyDark, NavyPrimary)
                        )
                    )
            ) {
                // Language toggle in top right
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NavySurface,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 40.dp, end = 16.dp)
                        .clickable { onToggleLanguage() }
                        .border(1.dp, BlueAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .testTag("login_lang_toggle")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (currentLanguage == AppLanguage.ENGLISH) "ಕನ್ನಡ" else "ENG",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(NavyPrimary)
                            .border(2.dp, GoldAmber, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.smps_school_logo),
                            contentDescription = "SMPS Crest",
                            modifier = Modifier.size(70.dp).clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = Translations.get("school_title", currentLanguage),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )

                    Text(
                        text = "${Translations.get("school_subtitle", currentLanguage)} • SMART ATTENDANCE & PAYROLL",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = GoldAmber,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Main Auth Card
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = (-24).dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Slate100,
                        modifier = Modifier.clip(RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Teacher App", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Admin Portal", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (selectedTab == 0) {
                        // Teacher Login
                        Text(
                            text = Translations.get("login_title", currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                        Text(
                            text = "Enter your Employee ID or registered Mobile Number",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = identifierInput,
                            onValueChange = { identifierInput = it },
                            label = { Text("Employee ID / Mobile") },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = NavyPrimary) },
                            modifier = Modifier.fillMaxWidth().testTag("login_identifier_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text("Biometric PIN / Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = NavyPrimary) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth().testTag("login_password_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                val matched = teachers.find {
                                    it.employeeId.equals(identifierInput.trim(), ignoreCase = true) ||
                                            it.mobileNumber.contains(identifierInput.trim())
                                } ?: teachers.firstOrNull()

                                if (matched != null) {
                                    onTeacherLogin(matched)
                                } else {
                                    Toast.makeText(context, "Teacher not found", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("login_submit_btn"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                        ) {
                            Icon(Icons.Default.Face, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Login with Face Biometrics",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(color = Slate200)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick Teacher Switcher (Direct testing shortcuts)
                        Text(
                            text = "Quick Demo Teacher Logins:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate600
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        teachers.forEach { teacher ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Slate100,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { onTeacherLogin(teacher) }
                                    .testTag("quick_login_${teacher.employeeId}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(Color(teacher.avatarColor)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(teacher.name.take(2).uppercase(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(teacher.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyDark)
                                            Text("${teacher.employeeId} • ${teacher.designation}", fontSize = 11.sp, color = Slate600)
                                        }
                                    }

                                    Text("Login →", color = NavyPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        // Admin / Principal / Accountant Login
                        Text(
                            text = "School Administration Login",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                        Text(
                            text = "Choose administrative role to access Dashboard & Payroll",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        AdminRoleButton(
                            role = UserRole.PRINCIPAL,
                            title = "Principal Portal",
                            subtitle = "Dr. R. K. Patil (Attendance, Leaves, Approval)",
                            accentColor = NavyPrimary,
                            onClick = { onAdminLogin(UserRole.PRINCIPAL) }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        AdminRoleButton(
                            role = UserRole.ACCOUNTANT,
                            title = "Accounts & Payroll Dept",
                            subtitle = "Salary Master, Advances, Deductions, PDF Slips",
                            accentColor = EmeraldSuccess,
                            onClick = { onAdminLogin(UserRole.ACCOUNTANT) }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        AdminRoleButton(
                            role = UserRole.SUPER_ADMIN,
                            title = "Super Administrator",
                            subtitle = "Full Master Control, Settings & Biometric Audit",
                            accentColor = PurpleRole,
                            onClick = { onAdminLogin(UserRole.SUPER_ADMIN) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Footer
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SM PUBLIC SCHOOL • JALIHAL, BAGALKOT DIST",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate600
                )
                Text(
                    text = "Smart Face Attendance & Automated Payroll System v2.0",
                    fontSize = 10.sp,
                    color = Slate600
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun AdminRoleButton(
    role: UserRole,
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("admin_role_${role.name}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Slate100),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = accentColor, modifier = Modifier.size(22.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyDark)
                    Text(subtitle, fontSize = 11.sp, color = Slate600)
                }
            }

            Icon(Icons.Default.School, contentDescription = null, tint = accentColor)
        }
    }
}
