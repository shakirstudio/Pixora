package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PixoraStatCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.PixoraViewModel

@Composable
fun WebsiteBuilderScreen(
    viewModel: PixoraViewModel,
    modifier: Modifier = Modifier
) {
    val config by viewModel.websiteConfig.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Sections", "Branding", "Custom Domain")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Studio Website Builder", style = MaterialTheme.typography.headlineMedium, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
                Text("Design your public luxury portfolio website", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(
                onClick = { viewModel.showToast("Published latest portfolio website to ${config.subdomain}!") },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Publish Live", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TabRow(selectedTabIndex = selectedTab, containerColor = Color.Transparent, contentColor = GoldAccent) {
            tabs.forEachIndexed { i, title ->
                Tab(selected = selectedTab == i, onClick = { selectedTab = i }, text = { Text(title) })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> {
                // Sections manager
                Text("WEBSITE SECTIONS", style = MaterialTheme.typography.labelSmall, color = GoldAccent, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                config.sections.forEach { sec ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(sec.name, fontWeight = FontWeight.Bold)
                                if (sec.title.isNotBlank()) {
                                    Text(sec.title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Switch(checked = sec.isEnabled, onCheckedChange = { viewModel.showToast("Toggled section ${sec.name}") })
                        }
                    }
                }
            }
            1 -> {
                // Branding customizer
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("DESIGN & TYPOGRAPHY", style = MaterialTheme.typography.labelSmall, color = GoldAccent, fontWeight = FontWeight.Bold)
                        OutlinedTextField(value = config.studioName, onValueChange = {}, label = { Text("Portfolio Title") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = config.tagline, onValueChange = {}, label = { Text("Subheading Tagline") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = config.fontChoice, onValueChange = {}, label = { Text("Font Pairing") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = config.buttonStyle, onValueChange = {}, label = { Text("Button Styling") }, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
            2 -> {
                // Domain manager
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("DOMAINS & SSL", style = MaterialTheme.typography.labelSmall, color = GoldAccent, fontWeight = FontWeight.Bold)
                        Text("Pixora Subdomain: https://${config.subdomain}", fontWeight = FontWeight.Bold)
                        Text("Custom Domain: https://${config.customDomain}", fontWeight = FontWeight.Bold, color = GoldAccent)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = LuxuryGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SSL Certificate Active & Verified", color = LuxuryGreen, style = MaterialTheme.typography.bodySmall)
                        }
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        Text("DNS Instructions:", style = MaterialTheme.typography.labelSmall)
                        Text("CNAME Record: @ points to cdn.pixora.com", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsScreen(
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
        Text("Studio Analytics", style = MaterialTheme.typography.headlineMedium, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
        Text("Real-time performance across client galleries and print store.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PixoraStatCard("Total Views", "14,820", "+38% this month", Icons.Default.Visibility, GoldAccent, Modifier.weight(1f))
            PixoraStatCard("Downloads", "3,410", "4K print files", Icons.Default.CloudDownload, LuxuryBlue, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PixoraStatCard("Favorites Marked", "482", "Client favorites", Icons.Default.Favorite, LuxuryAmber, Modifier.weight(1f))
            PixoraStatCard("Print Store Sales", "₹1,03,442", "2 orders placed", Icons.Default.Payments, LuxuryGreen, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("MOST VIEWED GALLERIES", style = MaterialTheme.typography.labelSmall, color = GoldAccent, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        val topGalleries = listOf(
            Triple("Rahul & Priya — Wedding Celebration", "2,840 Views", "37 Proofing Selected"),
            Triple("Elena & David — Tuscany Moments", "1,120 Views", "45 Proofing Selected"),
            Triple("Arjun & Meera — Palace Teaser", "890 Views", "20 Proofing Selected")
        )

        topGalleries.forEach { (title, views, status) ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(title, fontWeight = FontWeight.Bold)
                        Text(status, style = MaterialTheme.typography.bodySmall, color = GoldAccent)
                    }
                    Text(views, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
