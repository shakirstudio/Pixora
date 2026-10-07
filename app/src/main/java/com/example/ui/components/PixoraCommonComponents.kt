package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.MediaItem
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PixoraTopBar(
    title: String,
    subtitle: String? = null,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    unreadNotificationCount: Int = 0,
    onNotificationsClick: () -> Unit = {},
    onModeSwitchClick: () -> Unit = {},
    modeLabel: String = "Studio",
    onLogoutClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (subtitle != null) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.testTag("top_bar_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Go Back"
                            )
                        }
                    } else {
                        // Studio Aperture Monogram Icon
                        Box(
                            modifier = Modifier
                                .padding(start = 16.dp, end = 8.dp)
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Charcoal900)
                                .border(1.dp, GoldAccent, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Pixora Logo",
                                tint = GoldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                actions = {
                    // Mode Switcher pill (e.g. Studio vs Client Preview vs Admin)
                    AssistChip(
                        onClick = onModeSwitchClick,
                        label = {
                            Text(
                                text = modeLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Charcoal800,
                            labelColor = GoldAccent
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldDark.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .testTag("mode_switch_chip")
                    )

                    IconButton(
                        onClick = onNotificationsClick,
                        modifier = Modifier.testTag("notifications_icon_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotificationCount > 0) {
                                    Badge(
                                        containerColor = GoldAccent,
                                        contentColor = Charcoal900
                                    ) {
                                        Text("$unreadNotificationCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications"
                            )
                        }
                    }
                    if (onLogoutClick != null) {
                        IconButton(
                            onClick = onLogoutClick,
                            modifier = Modifier.testTag("top_bar_logout_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Log Out",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    actions()
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
            Divider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                thickness = 0.75.dp
            )
        }
    }
}

@Composable
fun PixoraBottomNav(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("pixora_bottom_nav"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        val items = listOf(
            Triple("Dashboard", Icons.Default.Dashboard, Icons.Outlined.Dashboard),
            Triple("Projects", Icons.Default.Folder, Icons.Outlined.Folder),
            Triple("Galleries", Icons.Default.Collections, Icons.Outlined.Collections),
            Triple("Clients", Icons.Default.People, Icons.Outlined.People),
            Triple("More", Icons.Default.Menu, Icons.Outlined.Menu)
        )

        items.forEach { (route, selectedIcon, unselectedIcon) ->
            val isSelected = selectedTab == route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(route) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) selectedIcon else unselectedIcon,
                        contentDescription = route
                    )
                },
                label = {
                    Text(
                        text = route,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Charcoal900,
                    selectedTextColor = GoldAccent,
                    indicatorColor = GoldAccent,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("nav_item_${route.lowercase()}")
            )
        }
    }
}

@Composable
fun ClientBottomNav(
    selectedTab: String,
    cartItemCount: Int = 0,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("client_bottom_nav"),
        containerColor = Charcoal900,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple("Home", Icons.Default.Home, Icons.Outlined.Home),
            Triple("Albums", Icons.Default.PhotoLibrary, Icons.Outlined.PhotoLibrary),
            Triple("Favorites", Icons.Default.Favorite, Icons.Outlined.FavoriteBorder),
            Triple("Selection", Icons.Default.CheckCircle, Icons.Outlined.CheckCircleOutline),
            Triple("Store", Icons.Default.ShoppingBag, Icons.Outlined.ShoppingBag)
        )

        items.forEach { (name, selectedIcon, unselectedIcon) ->
            val isSelected = selectedTab == name
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(name) },
                icon = {
                    if (name == "Store" && cartItemCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = GoldAccent, contentColor = Charcoal900) {
                                    Text("$cartItemCount")
                                }
                            }
                        ) {
                            Icon(imageVector = if (isSelected) selectedIcon else unselectedIcon, contentDescription = name)
                        }
                    } else {
                        Icon(imageVector = if (isSelected) selectedIcon else unselectedIcon, contentDescription = name)
                    }
                },
                label = {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) GoldAccent else Charcoal300
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Charcoal900,
                    selectedTextColor = GoldAccent,
                    indicatorColor = GoldAccent,
                    unselectedIconColor = Charcoal400,
                    unselectedTextColor = Charcoal400
                ),
                modifier = Modifier.testTag("client_nav_${name.lowercase()}")
            )
        }
    }
}

@Composable
fun PixoraStatCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector,
    iconTint: Color = GoldAccent,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.testTag("stat_card_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun PhotoGridItem(
    photo: MediaItem,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onSelectionToggle: () -> Unit,
    onCommentClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 180.dp
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clickable { onClick() }
            .testTag("photo_item_${photo.id}"),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = photo.url,
                contentDescription = photo.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Subtle gradient overlay for controls
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.4f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.65f)
                            )
                        )
                    )
            )

            // Top action buttons: Favorite & Proofing Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Favorite Button
                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f))
                        .testTag("fav_button_${photo.id}")
                ) {
                    Icon(
                        imageVector = if (photo.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (photo.isFavorite) GoldAccent else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Proofing Selection Checkmark
                IconButton(
                    onClick = onSelectionToggle,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            if (photo.isSelectedForProofing) GoldAccent else Color.Black.copy(alpha = 0.45f)
                        )
                        .testTag("select_button_${photo.id}")
                ) {
                    Icon(
                        imageVector = if (photo.isSelectedForProofing) Icons.Default.Check else Icons.Outlined.Check,
                        contentDescription = "Select for proofing",
                        tint = if (photo.isSelectedForProofing) Charcoal900 else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Bottom title & indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = photo.title,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = photo.albumName,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                        color = WarmWhite.copy(alpha = 0.8f)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (photo.isVideo) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Charcoal900.copy(alpha = 0.8f),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text(
                                text = "4K VIDEO",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                                color = GoldAccent,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Comment button
                    IconButton(
                        onClick = onCommentClick,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("comment_button_${photo.id}")
                    ) {
                        BadgedBox(
                            badge = {
                                if (photo.commentCount > 0) {
                                    Badge(
                                        containerColor = GoldAccent,
                                        contentColor = Charcoal900
                                    ) {
                                        Text("${photo.commentCount}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ChatBubbleOutline,
                                contentDescription = "Comments",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(
    icon: ImageVector,
    title: String,
    description: String,
    actionButtonText: String? = null,
    onActionClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(GoldSubtle),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GoldAccent,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionButtonText != null) {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldAccent,
                    contentColor = Charcoal900
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = actionButtonText, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ToastNotificationBanner(
    message: String?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = message != null,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        if (message != null) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Charcoal800,
                tonalElevation = 8.dp,
                shadowElevation = 6.dp,
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.horizontalGradient(listOf(GoldAccent, GoldLight))
                ),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun PixoraDeleteConfirmDialog(
    isOpen: Boolean,
    title: String = "Delete Record?",
    itemName: String = "",
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    if (!isOpen) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = LuxuryRed
            )
        },
        text = {
            Text(
                text = if (itemName.isNotBlank())
                    "Are you sure you want to delete \"$itemName\"? This record will be permanently deleted and cannot be undone."
                else
                    "Are you sure you want to delete this record? This action cannot be undone.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmDelete,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LuxuryRed,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("confirm_delete_button")
            ) {
                Text("Delete Record", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancel")
            }
        }
    )
}
