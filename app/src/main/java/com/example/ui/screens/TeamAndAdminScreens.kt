package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationCategory
import com.example.data.model.TeamMember
import com.example.data.model.TeamRole
import com.example.ui.components.EmptyStateView
import com.example.ui.components.PixoraStatCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.PixoraViewModel

@Composable
fun TeamScreen(
    viewModel: PixoraViewModel,
    modifier: Modifier = Modifier
) {
    val team by viewModel.teamMembers.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Studio Team & Roles", style = MaterialTheme.typography.headlineMedium, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
                Text("Manage permissions for assistant photographers & retouchers", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(
                onClick = { viewModel.showToast("Invite link copied to clipboard!") },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("+ Invite Member", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(team, key = { it.id }) { member ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(GoldSubtle),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = member.name.take(2).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(member.name, fontWeight = FontWeight.Bold)
                            Text(member.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = member.role.name,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCenterScreen(
    viewModel: PixoraViewModel,
    modifier: Modifier = Modifier
) {
    val notifications by viewModel.notifications.collectAsState()
    var selectedCat by remember { mutableStateOf(NotificationCategory.ALL) }

    val filtered = remember(notifications, selectedCat) {
        if (selectedCat == NotificationCategory.ALL) notifications
        else notifications.filter { it.category == selectedCat }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text("Notification Center", style = MaterialTheme.typography.headlineMedium, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
        Text("Alerts on proofing submissions, store orders, and gallery milestones.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(NotificationCategory.entries.toTypedArray()) { cat ->
                FilterChip(
                    selected = selectedCat == cat,
                    onClick = { selectedCat = cat },
                    label = { Text(cat.name) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(filtered, key = { it.id }) { notif ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(38.dp).clip(CircleShape).background(GoldSubtle),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(notif.title, fontWeight = FontWeight.Bold)
                            Text(notif.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(notif.timeAgo, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun MessagesScreen(
    viewModel: PixoraViewModel,
    modifier: Modifier = Modifier
) {
    val threads by viewModel.messageThreads.collectAsState()
    var activeThreadId by remember { mutableStateOf(threads.firstOrNull()?.id ?: "") }
    val activeThread = threads.find { it.id == activeThreadId } ?: threads.firstOrNull()
    var messageInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text("Client Conversations", style = MaterialTheme.typography.headlineMedium, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        // Client Thread Selector
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(threads) { th ->
                FilterChip(
                    selected = activeThreadId == th.id,
                    onClick = { activeThreadId = th.id },
                    label = { Text(th.clientName) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Chat Message Log
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "${activeThread?.clientName ?: ""} • ${activeThread?.projectTitle ?: ""}",
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), modifier = Modifier.padding(vertical = 8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(activeThread?.messages ?: emptyList()) { msg ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalAlignment = if (msg.isFromPhotographer) Alignment.End else Alignment.Start
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (msg.isFromPhotographer) GoldAccent else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = msg.text,
                                    color = if (msg.isFromPhotographer) Charcoal900 else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Text(msg.time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        placeholder = { Text("Reply to client...") },
                        modifier = Modifier.weight(1f).testTag("chat_input_field"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (messageInput.isNotBlank()) {
                                viewModel.sendMessage(activeThreadId, messageInput)
                                messageInput = ""
                            }
                        },
                        modifier = Modifier.clip(CircleShape).background(GoldAccent)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Charcoal900)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    viewModel: PixoraViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.studioSettings.collectAsState()

    var studioName by remember { mutableStateOf(settings.studioName) }
    var photographerName by remember { mutableStateOf(settings.photographerName) }
    var email by remember { mutableStateOf(settings.email) }
    var watermarkText by remember { mutableStateOf(settings.watermarkText) }
    var twoFactor by remember { mutableStateOf(settings.twoFactorEnabled) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Studio Settings", style = MaterialTheme.typography.headlineMedium, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
        Text("Configure studio branding, credentials, and security preferences.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(20.dp))

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("STUDIO PROFILE", style = MaterialTheme.typography.labelSmall, color = GoldAccent, fontWeight = FontWeight.Bold)
                OutlinedTextField(value = studioName, onValueChange = { studioName = it }, label = { Text("Studio Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = photographerName, onValueChange = { photographerName = it }, label = { Text("Lead Photographer") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Contact Email") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = watermarkText, onValueChange = { watermarkText = it }, label = { Text("Default Watermark") }, modifier = Modifier.fillMaxWidth())

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                Text("SECURITY & SESSIONS", style = MaterialTheme.typography.labelSmall, color = GoldAccent, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Two-Factor Authentication (2FA)")
                    Switch(checked = twoFactor, onCheckedChange = { twoFactor = it })
                }
                Text("Active Sessions: 2 Devices (MacBook Pro & Android Studio App)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.repository.updateStudioSettings(
                    settings.copy(
                        studioName = studioName,
                        photographerName = photographerName,
                        email = email,
                        watermarkText = watermarkText,
                        twoFactorEnabled = twoFactor
                    )
                )
                viewModel.showToast("Studio settings saved successfully")
            },
            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Save Profile Changes", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AdminPortalScreen(
    viewModel: PixoraViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("PIXORA Master Admin", style = MaterialTheme.typography.headlineMedium, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
                Text("Global multi-tenant platform infrastructure", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = LuxuryGreenLight
            ) {
                Text("SUPER-ADMIN", color = LuxuryGreen, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PixoraStatCard("Total Users", "12,480", "Photographers & Clients", Icons.Default.People, GoldAccent, Modifier.weight(1f))
            PixoraStatCard("Platform ARR", "₹4.8 Cr", "+18% growth", Icons.Default.TrendingUp, LuxuryGreen, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PixoraStatCard("Total Storage", "1.4 PB", "Global Cloud NVMe", Icons.Default.Cloud, LuxuryBlue, Modifier.weight(1f))
            PixoraStatCard("System Health", "99.99%", "All CDN edges operational", Icons.Default.CheckCircle, LuxuryGreen, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("PLATFORM SYSTEM CONTROLS", style = MaterialTheme.typography.labelSmall, color = GoldAccent, fontWeight = FontWeight.Bold)
                Text("CDN Edge Cache: Purged 10m ago across 24 edge nodes")
                Text("Razorpay / Stripe Gateway Webhooks: 100% Delivery rate")
                Text("Database Transactions: 42,000 queries/sec peak load")
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.showToast("System health check verified! All clusters operational.") },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900)
                ) {
                    Text("Run Platform Health Diagnostics")
                }
            }
        }
    }
}
