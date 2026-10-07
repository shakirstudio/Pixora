package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.*
import com.example.ui.components.PixoraStatCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.PixoraViewModel
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun DashboardScreen(
    viewModel: PixoraViewModel,
    modifier: Modifier = Modifier
) {
    val projects by viewModel.projects.collectAsState()
    val galleries by viewModel.galleries.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val settings by viewModel.studioSettings.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val mediaItems by viewModel.mediaItems.collectAsState()
    val invoices by viewModel.invoices.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Studio Greeting Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Good morning,",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = settings.photographerName,
                    style = MaterialTheme.typography.headlineLarge,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = settings.studioName,
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp,
                    color = GoldAccent
                )
            }

            // Quick Client Preview Button
            FilledTonalButton(
                onClick = {
                    viewModel.navigateTo(ScreenRoute.ClientLanding("gal_1"))
                },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Charcoal800,
                    contentColor = GoldAccent
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("preview_as_client_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Client Preview", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Actions Row
        Text(
            text = "QUICK ACTIONS",
            style = MaterialTheme.typography.labelSmall,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                QuickActionButton(
                    icon = Icons.Default.PersonAdd,
                    label = "+ Client",
                    onClick = { viewModel.isCreateClientOpen.value = true }
                )
            }
            item {
                QuickActionButton(
                    icon = Icons.Default.CreateNewFolder,
                    label = "+ Project",
                    onClick = { viewModel.isCreateProjectOpen.value = true }
                )
            }
            item {
                QuickActionButton(
                    icon = Icons.Default.AddPhotoAlternate,
                    label = "+ Gallery",
                    onClick = { viewModel.isCreateGalleryOpen.value = true }
                )
            }
            item {
                QuickActionButton(
                    icon = Icons.Default.CloudUpload,
                    label = "Upload Photos",
                    onClick = { viewModel.navigateTo(ScreenRoute.QuickUpload) }
                )
            }
            item {
                QuickActionButton(
                    icon = Icons.Default.Receipt,
                    label = "Create Invoice",
                    onClick = { viewModel.isCreateInvoiceOpen.value = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stats Grid
        Text(
            text = "STUDIO OVERVIEW",
            style = MaterialTheme.typography.labelSmall,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            val activeProjCount = projects.count { it.status == ProjectStatus.ACTIVE }
            PixoraStatCard(
                title = "Active Projects",
                value = "${projects.size}",
                subtitle = if (projects.isEmpty()) "No projects yet" else "$activeProjCount active shoots",
                icon = Icons.Default.FolderOpen,
                iconTint = GoldAccent,
                modifier = Modifier.weight(1f)
            )
            val totalViews = galleries.sumOf { it.viewsCount }
            PixoraStatCard(
                title = "Live Galleries",
                value = "${galleries.size}",
                subtitle = if (galleries.isEmpty()) "No galleries yet" else "$totalViews views total",
                icon = Icons.Default.Collections,
                iconTint = LuxuryGreen,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            val pendingSelections = galleries.sumOf { it.selectionsCount }
            PixoraStatCard(
                title = "Client Proofing",
                value = "$pendingSelections",
                subtitle = if (pendingSelections == 0) "No pending proofing" else "Photos selected",
                icon = Icons.Default.CheckCircle,
                iconTint = LuxuryAmber,
                modifier = Modifier.weight(1f)
            )
            val totalStorageMb = mediaItems.sumOf { it.sizeBytes } / (1024.0 * 1024.0)
            PixoraStatCard(
                title = "Cloud Storage",
                value = if (totalStorageMb < 1024) "${String.format("%.1f", totalStorageMb)} MB" else "${String.format("%.2f", totalStorageMb / 1024.0)} GB",
                subtitle = "of 1000 GB limit",
                icon = Icons.Default.CloudQueue,
                iconTint = LuxuryBlue,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            val totalRevenue = orders.sumOf { it.totalAmount } + invoices.filter { it.status == InvoiceStatus.PAID }.sumOf { it.totalAmount }
            PixoraStatCard(
                title = "Studio Revenue",
                value = "₹${totalRevenue.toInt()}",
                subtitle = "${invoices.count { it.status == InvoiceStatus.PAID }} paid invoices",
                icon = Icons.Default.Payments,
                iconTint = GoldAccent,
                modifier = Modifier.weight(1f)
            )
            val totalSales = orders.sumOf { it.totalAmount }
            PixoraStatCard(
                title = "Store Orders",
                value = "${orders.size} Orders",
                subtitle = "₹${totalSales.toInt()} sales",
                icon = Icons.Default.ShoppingBag,
                iconTint = LuxuryGreen,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Featured Active Gallery Card
        Text(
            text = "FEATURED LIVE CLIENT GALLERY",
            style = MaterialTheme.typography.labelSmall,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        val featuredGal = galleries.firstOrNull()
        if (featuredGal != null) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(ScreenRoute.GalleryEditor(featuredGal.id)) }
                    .testTag("featured_gallery_card")
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(170.dp)) {
                    AsyncImage(
                        model = featuredGal.coverImageUrl,
                        contentDescription = featuredGal.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Text(
                            text = featuredGal.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(text = "${featuredGal.photoCount} Photos", color = GoldLight, style = MaterialTheme.typography.bodySmall)
                            Text(text = "•", color = Color.White.copy(alpha = 0.6f))
                            Text(text = "${featuredGal.viewsCount} Views", color = Color.White, style = MaterialTheme.typography.bodySmall)
                            Text(text = "•", color = Color.White.copy(alpha = 0.6f))
                            Text(text = "${featuredGal.favoritesCount} Favorites", color = Color.White, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Client: ${featuredGal.clientName} | ${featuredGal.eventDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row {
                        TextButton(onClick = { viewModel.navigateTo(ScreenRoute.GallerySettings(featuredGal.id)) }) {
                            Text("Settings")
                        }
                        Button(
                            onClick = { viewModel.navigateTo(ScreenRoute.ClientLanding(featuredGal.id)) },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Open Client View", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Collections, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No Client Galleries Yet", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Create your first client gallery to showcase and deliver photos.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            if (projects.isEmpty()) {
                                viewModel.isCreateClientOpen.value = true
                            } else {
                                viewModel.isCreateGalleryOpen.value = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("+ Create Gallery", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recent Activity & Notifications
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RECENT ACTIVITY",
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "View All",
                style = MaterialTheme.typography.bodySmall,
                color = GoldAccent,
                modifier = Modifier.clickable { viewModel.navigateTo(ScreenRoute.Notifications) }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (notifications.isEmpty()) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No recent activity recorded yet.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Actions, client proofing and orders will automatically appear here.", color = Charcoal400, style = MaterialTheme.typography.bodySmall, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
        } else {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    notifications.take(3).forEach { notif ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(GoldSubtle),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = notif.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = notif.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = notif.timeAgo,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable { onClick() }
            .testTag("quick_action_${label.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = Brush.horizontalGradient(listOf(GoldAccent.copy(alpha = 0.4f), GoldLight.copy(alpha = 0.4f)))
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GoldAccent,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
