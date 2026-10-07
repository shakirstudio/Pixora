package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UploadFileTask
import com.example.data.model.UploadStatus
import com.example.ui.theme.*
import com.example.ui.viewmodel.PixoraViewModel
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun QuickUploadScreen(
    viewModel: PixoraViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Upload Media",
            style = MaterialTheme.typography.headlineMedium,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "High-speed cloud processing with RAW, TIFF and 4K ProRes support.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Large Drag and Drop Upload Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    2.dp,
                    Brush.linearGradient(listOf(GoldAccent, GoldLight)),
                    RoundedCornerShape(14.dp)
                )
                .clickable {
                    viewModel.triggerUpload("RP_UDAIPUR_CEREMONY_RAW_0945.CR3", "Canon RAW (CR3)", 48_500_000)
                }
                .testTag("upload_dropzone"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(20.dp)) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(GoldSubtle),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Drop your photos and videos here",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Supports RAW (CR3, ARW, NEF, DNG), JPG, TIFF, HEIC, MP4, MOV, ProRes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            viewModel.triggerUpload("RP_PHERAS_4K_REEL.MOV", "ProRes 422 Cinema", 180_000_000)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Select Files", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.triggerUpload("WEDDING_HALDI_SESSION_BATCH.ZIP", "Folder Archive", 320_000_000)
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Select Folder")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Upload Queue Status preview
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("ACTIVE UPLOAD QUEUE", style = MaterialTheme.typography.labelSmall, color = GoldAccent, fontWeight = FontWeight.Bold)
            Text(
                text = "Open Upload Center",
                style = MaterialTheme.typography.bodySmall,
                color = GoldAccent,
                modifier = Modifier.clickable { viewModel.navigateTo(ScreenRoute.UploadCenter) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Currently Uploading: 2 files active", fontWeight = FontWeight.Bold)
                Text("Speed: ~18.4 MB/s • Estimated completion: 22s", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(progress = { 0.65f }, modifier = Modifier.fillMaxWidth(), color = GoldAccent)
            }
        }
    }
}

@Composable
fun UploadCenterScreen(
    viewModel: PixoraViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.uploadTasks.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Active Queue (${tasks.count { it.status == UploadStatus.UPLOADING }})", "Completed (${tasks.count { it.status == UploadStatus.COMPLETED }})")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("Upload Center", style = MaterialTheme.typography.headlineMedium, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(14.dp))

        TabRow(selectedTabIndex = selectedTab, containerColor = Color.Transparent, contentColor = GoldAccent) {
            tabs.forEachIndexed { i, title ->
                Tab(selected = selectedTab == i, onClick = { selectedTab = i }, text = { Text(title) })
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        val displayTasks = if (selectedTab == 0) tasks.filter { it.status != UploadStatus.COMPLETED } else tasks.filter { it.status == UploadStatus.COMPLETED }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(displayTasks, key = { it.id }) { task ->
                UploadTaskCard(task = task)
            }
        }
    }
}

@Composable
fun UploadTaskCard(task: UploadFileTask) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(task.filename, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text("${task.fileType} • ${(task.sizeBytes / 1_000_000)} MB", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (task.status == UploadStatus.COMPLETED) LuxuryGreenLight else LuxuryAmberLight
                ) {
                    Text(
                        text = task.status.name,
                        color = if (task.status == UploadStatus.COMPLETED) LuxuryGreen else LuxuryAmber,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { task.progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = GoldAccent
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${(task.progress * 100).toInt()}% • ${task.speedMbps}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (task.status != UploadStatus.COMPLETED) {
                    Text("${task.remainingSecs}s remaining", style = MaterialTheme.typography.bodySmall, color = GoldAccent)
                }
            }
        }
    }
}

@Composable
fun StorageScreen(
    viewModel: PixoraViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.studioSettings.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Cloud Storage & CDN", style = MaterialTheme.typography.headlineMedium, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
        Text("High-throughput global NVMe cloud storage.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(20.dp))

        // Large Visualization Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("USED STORAGE", style = MaterialTheme.typography.labelSmall, color = GoldAccent, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("${settings.storageUsedGb.toInt()} GB", style = MaterialTheme.typography.displayMedium, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
                    Text(" / ${settings.storageLimitGb.toInt()} GB", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 6.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { (settings.storageUsedGb / settings.storageLimitGb).toFloat() },
                    modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                    color = GoldAccent,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Breakdown categories
                val breakdown = listOf(
                    Triple("Full-Res RAWs & Photos", "480 GB", LuxuryBlue),
                    Triple("4K Cinema Video Reels", "210 GB", GoldAccent),
                    Triple("Archival Scans & TIFFs", "42 GB", LuxuryGreen),
                    Triple("Web Proofing & Thumbnails", "10 GB", Charcoal400)
                )

                breakdown.forEach { (cat, size, color) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(cat, style = MaterialTheme.typography.bodyMedium)
                        }
                        Text(size, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { viewModel.navigateTo(ScreenRoute.Subscription) },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Upgrade Storage Tier (2 TB / 5 TB)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SubscriptionScreen(
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("Subscription Plan", style = MaterialTheme.typography.headlineMedium, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(GoldAccent, GoldLight)))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = GoldAccent
                ) {
                    Text("CURRENT PLAN", color = Charcoal900, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("PRO STUDIO TIER", style = MaterialTheme.typography.headlineSmall, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
                Text("₹4,999 / month (Billed Annually)", color = GoldAccent, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(16.dp))
                listOf("1,000 GB High-Speed Cloud NVMe Storage", "Unlimited Client Galleries & Proofing Portals", "Custom Studio Domain & Free SSL", "Zero Commission on Online Store Sales", "5 Active Studio Team Members").forEach { feat ->
                    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(feat, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { viewModel.showToast("Redirecting to Razorpay Billing Portal...") },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Manage Billing & Invoices", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
