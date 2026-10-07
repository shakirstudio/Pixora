package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Album
import com.example.data.model.Gallery
import com.example.data.model.MediaItem
import com.example.ui.components.EmptyStateView
import com.example.ui.components.PhotoGridItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.PixoraViewModel
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun ClientLandingScreen(
    galleryId: String,
    viewModel: PixoraViewModel
) {
    val galleries by viewModel.galleries.collectAsState()
    val gallery = galleries.find { it.id == galleryId } ?: galleries.firstOrNull()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Fullscreen Cinematic Hero Photo
        AsyncImage(
            model = gallery?.coverImageUrl ?: "https://images.unsplash.com/photo-1519741497674-611481863552?auto=format&fit=crop&w=1200&q=80",
            contentDescription = "Wedding Hero",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Luxury Vignette Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.5f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.9f)
                        )
                    )
                )
        )

        // Studio Crest & Return to Studio Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Charcoal900)
                        .border(1.dp, GoldAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "PIXORA STUDIOS",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 3.sp,
                    color = GoldLight,
                    fontWeight = FontWeight.Bold
                )
            }

            // Return to Studio Admin button
            FilledTonalButton(
                onClick = { viewModel.navigateTo(ScreenRoute.Dashboard) },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Charcoal900.copy(alpha = 0.8f),
                    contentColor = GoldLight
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("exit_client_preview_button")
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Exit Preview", style = MaterialTheme.typography.labelSmall)
            }
        }

        // Center / Bottom Editorial Wedding Title
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 48.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "WEDDING CELEBRATION",
                style = MaterialTheme.typography.labelMedium,
                letterSpacing = 4.sp,
                color = GoldAccent
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = gallery?.title?.replace(" — Wedding Celebration", "") ?: "Rahul & Priya",
                style = MaterialTheme.typography.displayMedium,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Light,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${gallery?.eventDate ?: "12 December 2026"} • The Oberoi Udaivilas, Udaipur",
                style = MaterialTheme.typography.bodyMedium,
                color = WarmWhite.copy(alpha = 0.85f),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.navigateTo(ScreenRoute.ClientGalleryHome(gallery?.id ?: "gal_1"))
                },
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(52.dp)
                    .testTag("view_gallery_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldAccent,
                    contentColor = Charcoal900
                ),
                shape = RoundedCornerShape(26.dp)
            ) {
                Text(
                    text = "View Gallery",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun ClientGalleryHomeScreen(
    galleryId: String,
    viewModel: PixoraViewModel
) {
    val galleries by viewModel.galleries.collectAsState()
    val albums by viewModel.albums.collectAsState()
    val mediaItems by viewModel.mediaItems.collectAsState()
    val gallery = galleries.find { it.id == galleryId } ?: galleries.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Charcoal900)
            .verticalScroll(rememberScrollState())
    ) {
        // Studio & Gallery Header Bar
        Surface(
            color = Charcoal900,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PIXORA LUXE STUDIOS",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 2.sp,
                        color = GoldAccent
                    )
                    Text(
                        text = gallery?.title ?: "Rahul & Priya — Wedding",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily.Serif,
                        color = Color.White
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenRoute.ClientFavorites(galleryId)) },
                        modifier = Modifier.testTag("client_home_favorites_icon")
                    ) {
                        BadgedBox(badge = {
                            Badge(containerColor = GoldAccent, contentColor = Charcoal900) {
                                Text("${viewModel.getFavoriteMedia().size}")
                            }
                        }) {
                            Icon(Icons.Default.Favorite, contentDescription = "Favorites", tint = GoldAccent)
                        }
                    }

                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenRoute.ClientProofing(galleryId)) },
                        modifier = Modifier.testTag("client_home_selection_icon")
                    ) {
                        BadgedBox(badge = {
                            Badge(containerColor = GoldAccent, contentColor = Charcoal900) {
                                Text("${viewModel.getSelectedMedia().size}")
                            }
                        }) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Proofing", tint = GoldAccent)
                        }
                    }
                }
            }
        }

        // Feature Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            AsyncImage(
                model = gallery?.coverImageUrl ?: "https://images.unsplash.com/photo-1519741497674-611481863552?auto=format&fit=crop&w=1200&q=80",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Charcoal900.copy(alpha = 0.9f), Charcoal900)
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                Text(
                    text = "Welcome to your personal storybook",
                    style = MaterialTheme.typography.bodySmall,
                    color = GoldLight
                )
                Text(
                    text = gallery?.description ?: "Relive each unforgettable moment from Udaipur.",
                    style = MaterialTheme.typography.headlineSmall,
                    fontFamily = FontFamily.Serif,
                    color = Color.White
                )
            }
        }

        // Albums Section
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "ALBUMS & CEREMONIES",
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 2.sp,
                color = GoldAccent
            )
            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(albums) { album ->
                    AlbumCardItem(
                        album = album,
                        onClick = {
                            viewModel.navigateTo(ScreenRoute.ClientPhotoGrid(galleryId, album.id))
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Gallery Highlights Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FEATURED HIGHLIGHTS (${mediaItems.size})",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 2.sp,
                    color = GoldAccent
                )
                Text(
                    text = "View All Grid",
                    style = MaterialTheme.typography.bodySmall,
                    color = GoldLight,
                    modifier = Modifier.clickable {
                        viewModel.navigateTo(ScreenRoute.ClientPhotoGrid(galleryId))
                    }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                mediaItems.take(4).forEach { photo ->
                    PhotoGridItem(
                        photo = photo,
                        onClick = { viewModel.navigateTo(ScreenRoute.ClientPhotoViewer(photo.id)) },
                        onFavoriteToggle = { viewModel.toggleFavorite(photo.id) },
                        onSelectionToggle = { viewModel.toggleSelection(photo.id) },
                        onCommentClick = { viewModel.navigateTo(ScreenRoute.ClientPhotoViewer(photo.id)) },
                        height = 200.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Client Actions Card (Videos, Downloads, Store)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Charcoal800),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("MORE OPTIONS", style = MaterialTheme.typography.labelSmall, color = GoldAccent, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.navigateTo(ScreenRoute.ClientVideoGallery(galleryId)) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = null, tint = GoldAccent)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Cinematic Video Teasers", fontWeight = FontWeight.Bold, color = Color.White)
                            Text("4K UHD Streaming Videos", style = MaterialTheme.typography.bodySmall, color = Charcoal300)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Charcoal400)
                    }

                    Divider(color = Charcoal700, thickness = 0.5.dp)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.navigateTo(ScreenRoute.ClientDownloadCenter(galleryId)) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, tint = GoldAccent)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Download Center", fontWeight = FontWeight.Bold, color = Color.White)
                            Text("High-res originals & web-size packages", style = MaterialTheme.typography.bodySmall, color = Charcoal300)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Charcoal400)
                    }

                    Divider(color = Charcoal700, thickness = 0.5.dp)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.navigateTo(ScreenRoute.ClientStore(galleryId)) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = GoldAccent)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Shop Fine Art Prints & Albums", fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Handcrafted Tuscan leather keepsakes", style = MaterialTheme.typography.bodySmall, color = Charcoal300)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Charcoal400)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun AlbumCardItem(
    album: Album,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() }
            .testTag("album_card_${album.name.lowercase()}"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Charcoal800)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(110.dp)) {
                AsyncImage(
                    model = album.coverImageUrl,
                    contentDescription = album.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = album.name,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
                Text(
                    text = "${album.photoCount} photos",
                    style = MaterialTheme.typography.bodySmall,
                    color = GoldLight
                )
            }
        }
    }
}

@Composable
fun ClientPhotoGridScreen(
    galleryId: String,
    initialAlbumId: String?,
    viewModel: PixoraViewModel
) {
    val mediaItems by viewModel.mediaItems.collectAsState()
    val albums by viewModel.albums.collectAsState()
    var selectedAlbumId by remember { mutableStateOf(initialAlbumId ?: "All") }
    var isLargeGrid by remember { mutableStateOf(false) }

    val filtered = remember(mediaItems, selectedAlbumId) {
        if (selectedAlbumId == "All") mediaItems
        else mediaItems.filter { it.albumId == selectedAlbumId }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Charcoal900)
    ) {
        // Header
        Surface(color = Charcoal900, tonalElevation = 3.dp, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Text(
                    text = "Photo Gallery",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Serif,
                    color = Color.White
                )

                IconButton(onClick = { isLargeGrid = !isLargeGrid }) {
                    Icon(
                        imageVector = if (isLargeGrid) Icons.Default.GridView else Icons.Default.ViewAgenda,
                        contentDescription = "Toggle Grid Size",
                        tint = GoldAccent
                    )
                }
            }
        }

        // Album Pills
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedAlbumId == "All",
                    onClick = { selectedAlbumId = "All" },
                    label = { Text("All (${mediaItems.size})", color = if (selectedAlbumId == "All") Charcoal900 else WarmWhite) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldAccent
                    )
                )
            }
            items(albums) { album ->
                FilterChip(
                    selected = selectedAlbumId == album.id,
                    onClick = { selectedAlbumId = album.id },
                    label = { Text(album.name, color = if (selectedAlbumId == album.id) Charcoal900 else WarmWhite) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldAccent
                    )
                )
            }
        }

        // Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(if (isLargeGrid) 1 else 2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filtered, key = { it.id }) { photo ->
                PhotoGridItem(
                    photo = photo,
                    onClick = { viewModel.navigateTo(ScreenRoute.ClientPhotoViewer(photo.id)) },
                    onFavoriteToggle = { viewModel.toggleFavorite(photo.id) },
                    onSelectionToggle = { viewModel.toggleSelection(photo.id) },
                    onCommentClick = { viewModel.navigateTo(ScreenRoute.ClientPhotoViewer(photo.id)) },
                    height = if (isLargeGrid) 280.dp else 190.dp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullscreenPhotoViewerScreen(
    photoId: String,
    viewModel: PixoraViewModel
) {
    val mediaItems by viewModel.mediaItems.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val currentIndex = mediaItems.indexOfFirst { it.id == photoId }.coerceAtLeast(0)
    var activeIndex by remember { mutableStateOf(currentIndex) }
    val photo = mediaItems.getOrNull(activeIndex) ?: mediaItems.firstOrNull()

    var showExifSheet by remember { mutableStateOf(false) }
    var showCommentSheet by remember { mutableStateOf(false) }
    var scale by remember { mutableStateOf(1f) }

    if (photo == null) return

    val photoComments = comments.filter { it.mediaId == photo.id }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Edge to Edge Image with Pinch to Zoom gesture
        AsyncImage(
            model = photo.url,
            contentDescription = photo.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, _ ->
                        scale = (scale * zoom).coerceIn(1f, 3.5f)
                    }
                }
        )

        // Top Controls Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .testTag("viewer_close_button")
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }

            Text(
                text = "${activeIndex + 1} / ${mediaItems.size}",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = { showExifSheet = true },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .testTag("viewer_info_button")
                ) {
                    Icon(Icons.Default.Info, contentDescription = "EXIF Info", tint = GoldAccent)
                }

                IconButton(
                    onClick = {
                        viewModel.showToast("Starting secure high-res download for ${photo.filename}...")
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .testTag("viewer_download_button")
                ) {
                    Icon(Icons.Default.CloudDownload, contentDescription = "Download", tint = Color.White)
                }
            }
        }

        // Prev & Next Touch Arrows
        if (activeIndex > 0) {
            IconButton(
                onClick = { activeIndex-- },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 12.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous", tint = Color.White, modifier = Modifier.size(32.dp))
            }
        }

        if (activeIndex < mediaItems.size - 1) {
            IconButton(
                onClick = { activeIndex++ },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = Color.White, modifier = Modifier.size(32.dp))
            }
        }

        // Bottom Action Bar
        Surface(
            color = Charcoal900.copy(alpha = 0.85f),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = photo.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${photo.albumName} • ${photo.filename}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Charcoal300
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Favorite Toggle
                    IconButton(
                        onClick = { viewModel.toggleFavorite(photo.id) },
                        modifier = Modifier.testTag("viewer_fav_button")
                    ) {
                        Icon(
                            imageVector = if (photo.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (photo.isFavorite) GoldAccent else Color.White
                        )
                    }

                    // Selection Toggle
                    IconButton(
                        onClick = { viewModel.toggleSelection(photo.id) },
                        modifier = Modifier.testTag("viewer_select_button")
                    ) {
                        Icon(
                            imageVector = if (photo.isSelectedForProofing) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                            contentDescription = "Proofing Selection",
                            tint = if (photo.isSelectedForProofing) GoldAccent else Color.White
                        )
                    }

                    // Comments Button
                    IconButton(
                        onClick = { showCommentSheet = true },
                        modifier = Modifier.testTag("viewer_comments_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (photoComments.isNotEmpty()) {
                                    Badge(containerColor = GoldAccent, contentColor = Charcoal900) {
                                        Text("${photoComments.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Comments", tint = Color.White)
                        }
                    }
                }
            }
        }

        // EXIF Bottom Sheet
        if (showExifSheet) {
            ModalBottomSheet(
                onDismissRequest = { showExifSheet = false },
                containerColor = Charcoal800
            ) {
                Column(modifier = Modifier.padding(20.dp).navigationBarsPadding()) {
                    Text("CAMERA & EXIF METADATA", style = MaterialTheme.typography.labelSmall, color = GoldAccent, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Camera: ${photo.exif.camera}", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Lens: ${photo.exif.lens}", color = Charcoal200)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Focal: ${photo.exif.focalLength}", color = Charcoal200)
                        Text("Aperture: ${photo.exif.aperture}", color = Charcoal200)
                        Text("Shutter: ${photo.exif.shutterSpeed}", color = Charcoal200)
                        Text("ISO: ${photo.exif.iso}", color = Charcoal200)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Captured: ${photo.exif.captureDate}", color = Charcoal300, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Comments Bottom Sheet
        if (showCommentSheet) {
            ModalBottomSheet(
                onDismissRequest = { showCommentSheet = false },
                containerColor = Charcoal800
            ) {
                var commentText by remember { mutableStateOf("") }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .navigationBarsPadding()
                ) {
                    Text("PHOTO COMMENTS & NOTES", style = MaterialTheme.typography.labelSmall, color = GoldAccent, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (photoComments.isEmpty()) {
                        Text("No comments on this photo yet. Leave retouching notes or feedback below.", color = Charcoal300, style = MaterialTheme.typography.bodySmall)
                    } else {
                        photoComments.forEach { comment ->
                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(comment.authorName, fontWeight = FontWeight.Bold, color = GoldLight)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("(${comment.authorRole})", style = MaterialTheme.typography.bodySmall, color = Charcoal400)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("• ${comment.timestamp}", style = MaterialTheme.typography.bodySmall, color = Charcoal400)
                                }
                                Text(comment.message, color = Color.White)
                            }
                            Divider(color = Charcoal700, thickness = 0.5.dp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            placeholder = { Text("Write a comment or retouch note...") },
                            modifier = Modifier.weight(1f).testTag("comment_input_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldAccent,
                                unfocusedBorderColor = Charcoal600
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (commentText.isNotBlank()) {
                                    viewModel.addComment(photo.id, commentText)
                                    commentText = ""
                                }
                            },
                            modifier = Modifier.clip(CircleShape).background(GoldAccent).testTag("send_comment_button")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = Charcoal900)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClientFavoritesScreen(
    galleryId: String,
    viewModel: PixoraViewModel
) {
    val mediaItems by viewModel.mediaItems.collectAsState()
    val favorites = mediaItems.filter { it.isFavorite }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Charcoal900)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "My Favorites",
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily.Serif,
                    color = Color.White
                )
                Text(
                    text = "${favorites.size} Photos favorited",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GoldAccent
                )
            }

            Button(
                onClick = { viewModel.showToast("Preparing ZIP archive of ${favorites.size} favorites...") },
                enabled = favorites.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Download All")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (favorites.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.FavoriteBorder,
                title = "No Favorites Yet",
                description = "Tap the heart icon on any photo in the gallery to save your favorites here."
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favorites, key = { it.id }) { photo ->
                    PhotoGridItem(
                        photo = photo,
                        onClick = { viewModel.navigateTo(ScreenRoute.ClientPhotoViewer(photo.id)) },
                        onFavoriteToggle = { viewModel.toggleFavorite(photo.id) },
                        onSelectionToggle = { viewModel.toggleSelection(photo.id) },
                        onCommentClick = { viewModel.navigateTo(ScreenRoute.ClientPhotoViewer(photo.id)) }
                    )
                }
            }
        }
    }
}

@Composable
fun ClientProofingScreen(
    galleryId: String,
    viewModel: PixoraViewModel
) {
    val mediaItems by viewModel.mediaItems.collectAsState()
    val galleries by viewModel.galleries.collectAsState()
    val gallery = galleries.find { it.id == galleryId } ?: galleries.firstOrNull()

    val selectedPhotos = mediaItems.filter { it.isSelectedForProofing }
    val targetLimit = gallery?.selectionLimit ?: 50
    var showConfirmDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Charcoal900)
            .padding(16.dp)
    ) {
        Text(
            text = "Your Album Selection",
            style = MaterialTheme.typography.headlineMedium,
            fontFamily = FontFamily.Serif,
            color = Color.White
        )
        Text(
            text = "Select your favorite photos for the master wedding album layout.",
            style = MaterialTheme.typography.bodySmall,
            color = Charcoal300
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Bar Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Charcoal800)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Selected ${selectedPhotos.size} of $targetLimit Photos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (gallery?.isSelectionSubmitted == true) "SUBMITTED" else "IN PROGRESS",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (gallery?.isSelectionSubmitted == true) LuxuryGreen else GoldAccent,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { (selectedPhotos.size.toFloat() / targetLimit).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = GoldAccent,
                    trackColor = Charcoal700
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { showConfirmDialog = true },
                    enabled = selectedPhotos.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("submit_selection_button")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (gallery?.isSelectionSubmitted == true) "Re-Submit Selection to Studio" else "Submit Final Selection to Studio",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedPhotos.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.CheckCircleOutline,
                title = "No Photos Selected",
                description = "Tap the checkmark icon on any photo in the gallery to select it for your album."
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedPhotos, key = { it.id }) { photo ->
                    PhotoGridItem(
                        photo = photo,
                        onClick = { viewModel.navigateTo(ScreenRoute.ClientPhotoViewer(photo.id)) },
                        onFavoriteToggle = { viewModel.toggleFavorite(photo.id) },
                        onSelectionToggle = { viewModel.toggleSelection(photo.id) },
                        onCommentClick = { viewModel.navigateTo(ScreenRoute.ClientPhotoViewer(photo.id)) }
                    )
                }
            }
        }

        // Submission Confirmation Dialog
        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("Submit Album Selection?") },
                text = {
                    Text("You have selected ${selectedPhotos.size} photos. Once submitted, your photographer Vikram will review the choices and begin custom layout design.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.submitProofingSelection(galleryId)
                            showConfirmDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900)
                    ) {
                        Text("Confirm & Submit")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) { Text("Review More") }
                }
            )
        }
    }
}

@Composable
fun ClientVideoGalleryScreen(
    galleryId: String,
    viewModel: PixoraViewModel
) {
    val mediaItems by viewModel.mediaItems.collectAsState()
    val videos = mediaItems.filter { it.isVideo }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Charcoal900)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "Cinematic Video Gallery",
                style = MaterialTheme.typography.headlineSmall,
                fontFamily = FontFamily.Serif,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(videos) { video ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Charcoal800),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                            AsyncImage(
                                model = video.url,
                                contentDescription = video.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.4f))
                            )

                            // Play Button in center
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(GoldAccent)
                                    .align(Alignment.Center)
                                    .clickable {
                                        viewModel.showToast("Streaming 4K video player for ${video.title}")
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Charcoal900, modifier = Modifier.size(36.dp))
                            }

                            // Duration badge
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color.Black.copy(alpha = 0.8f),
                                modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
                            ) {
                                Text(
                                    text = "3:04 • 4K UHD",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(video.title, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Apple ProRes 422 Cinema Master • Stereo 24-bit audio", style = MaterialTheme.typography.bodySmall, color = Charcoal300)
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { viewModel.showToast("Downloading 4K Master Video...") },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Download 4K Master Film")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClientDownloadCenterScreen(
    galleryId: String,
    viewModel: PixoraViewModel
) {
    var selectedRes by remember { mutableStateOf("Original") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Charcoal900)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "Secure Download Center",
                style = MaterialTheme.typography.headlineSmall,
                fontFamily = FontFamily.Serif,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(colors = CardDefaults.cardColors(containerColor = Charcoal800)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("DOWNLOAD ENTIRE GALLERY (142 PHOTOS)", fontWeight = FontWeight.Bold, color = GoldLight)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Select your preferred download format:", style = MaterialTheme.typography.bodySmall, color = Charcoal300)
                Spacer(modifier = Modifier.height(10.dp))

                listOf("Original RAW & Uncompressed (18.4 GB)", "High Resolution 4K Print (4.2 GB)", "Web & Social Media Ready (850 MB)").forEach { opt ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedRes = opt }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedRes == opt,
                            onClick = { selectedRes = opt },
                            colors = RadioButtonDefaults.colors(selectedColor = GoldAccent)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(opt, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.showToast("Generated secure signed download URL. Archive download initiated!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Download Full Gallery Archive", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
