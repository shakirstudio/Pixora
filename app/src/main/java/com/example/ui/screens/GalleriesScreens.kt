package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.*
import com.example.ui.components.EmptyStateView
import com.example.ui.components.PhotoGridItem
import com.example.ui.components.PixoraDeleteConfirmDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.PixoraViewModel
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun GalleriesScreen(
    viewModel: PixoraViewModel,
    modifier: Modifier = Modifier
) {
    val galleries by viewModel.galleries.collectAsState()
    val clients by viewModel.clients.collectAsState()
    val editingGallery by viewModel.editingGallery.collectAsState()
    val deletingGallery by viewModel.deletingGallery.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(galleries, searchQuery) {
        if (searchQuery.isBlank()) galleries
        else galleries.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.clientName.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Search & Add Gallery
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search client galleries...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldAccent) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("galleries_search_input"),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldAccent,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                onClick = {
                    if (clients.isEmpty()) {
                        viewModel.showToast("Please add a client first before creating a gallery.")
                        viewModel.isCreateClientOpen.value = true
                    } else {
                        viewModel.isCreateGalleryOpen.value = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .height(52.dp)
                    .testTag("create_gallery_button")
            ) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Gallery", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filtered.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.Collections,
                title = if (searchQuery.isBlank()) "No Galleries Found" else "No matching galleries",
                description = if (searchQuery.isBlank()) "Create your first client gallery with custom branding, proofing, and delivery options." else "No galleries match \"$searchQuery\".",
                actionButtonText = "+ Create New Gallery",
                onActionClick = {
                    if (clients.isEmpty()) {
                        viewModel.isCreateClientOpen.value = true
                    } else {
                        viewModel.isCreateGalleryOpen.value = true
                    }
                }
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered, key = { it.id }) { gallery ->
                    GalleryCardItem(
                        gallery = gallery,
                        onOpenEditor = { viewModel.navigateTo(ScreenRoute.GalleryEditor(gallery.id)) },
                        onClientPreview = { viewModel.navigateTo(ScreenRoute.ClientLanding(gallery.id)) },
                        onEdit = { viewModel.editingGallery.value = gallery },
                        onDelete = { viewModel.deletingGallery.value = gallery }
                    )
                }
            }
        }
    }

    // Edit Gallery Dialog
    if (editingGallery != null) {
        EditGalleryDialog(
            gallery = editingGallery!!,
            onDismiss = { viewModel.editingGallery.value = null },
            onConfirm = { updated -> viewModel.updateGallery(updated) }
        )
    }

    // Delete Gallery Confirmation
    if (deletingGallery != null) {
        PixoraDeleteConfirmDialog(
            isOpen = true,
            title = "Delete Gallery",
            itemName = deletingGallery!!.title,
            onDismiss = { viewModel.deletingGallery.value = null },
            onConfirmDelete = { viewModel.confirmDeleteGallery(deletingGallery!!) }
        )
    }
}

@Composable
fun GalleryCardItem(
    gallery: Gallery,
    onOpenEditor: () -> Unit,
    onClientPreview: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenEditor() }
            .testTag("gallery_card_${gallery.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = gallery.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Client: ${gallery.clientName} • ${gallery.eventDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = gallery.privacy.name,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text("Photos", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${gallery.photoCount}", fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Videos", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${gallery.videoCount}", fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Proofing", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${gallery.selectionsCount}/${gallery.selectionLimit}", fontWeight = FontWeight.Bold, color = GoldAccent)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Gallery", tint = GoldAccent, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Gallery", tint = LuxuryRed, modifier = Modifier.size(18.dp))
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onOpenEditor,
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("Manage Photos", style = MaterialTheme.typography.labelMedium)
                    }

                    Button(
                        onClick = onClientPreview,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Client View", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CreateGalleryDialog(
    isOpen: Boolean,
    clients: List<Client>,
    projects: List<Project>,
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        client: Client,
        project: Project,
        eventDate: String,
        description: String,
        privacy: GalleryPrivacy,
        allowDownloads: Boolean,
        watermarkEnabled: Boolean
    ) -> Unit
) {
    if (!isOpen) return

    var title by remember { mutableStateOf("") }
    var selectedClient by remember { mutableStateOf(clients.firstOrNull()) }
    val matchingProjects = remember(selectedClient, projects) {
        if (selectedClient == null) projects
        else projects.filter { it.clientId == selectedClient?.id }
    }
    var selectedProject by remember { mutableStateOf(matchingProjects.firstOrNull() ?: projects.firstOrNull()) }
    var eventDate by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var privacy by remember { mutableStateOf(GalleryPrivacy.PUBLIC) }
    var allowDownloads by remember { mutableStateOf(true) }
    var watermarkEnabled by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create New Client Gallery", style = MaterialTheme.typography.headlineSmall, fontFamily = FontFamily.Serif)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Gallery Title *") },
                    placeholder = { Text("e.g. Wedding Day Highlights") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("new_gallery_title")
                )

                Text("Select Client *:", style = MaterialTheme.typography.labelSmall)
                clients.forEach { client ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedClient = client },
                        shape = RoundedCornerShape(6.dp),
                        color = if (selectedClient?.id == client.id) GoldSubtle else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = client.name,
                            modifier = Modifier.padding(10.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (selectedClient?.id == client.id) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                OutlinedTextField(
                    value = eventDate,
                    onValueChange = { eventDate = it },
                    label = { Text("Event Date") },
                    placeholder = { Text("e.g. 12 Dec 2026") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Welcome Message for Client") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Privacy Setting:", style = MaterialTheme.typography.labelSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(GalleryPrivacy.PUBLIC, GalleryPrivacy.PASSWORD, GalleryPrivacy.PIN).forEach { p ->
                        FilterChip(
                            selected = privacy == p,
                            onClick = { privacy = p },
                            label = { Text(p.name) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Allow Client Downloads", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = allowDownloads, onCheckedChange = { allowDownloads = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val proj = selectedProject ?: projects.firstOrNull() ?: Project(
                        clientId = selectedClient!!.id,
                        clientName = selectedClient!!.name,
                        name = title,
                        eventType = "General",
                        eventDate = eventDate,
                        location = ""
                    )
                    if (title.isNotBlank() && selectedClient != null) {
                        onConfirm(title, selectedClient!!, proj, eventDate, description, privacy, allowDownloads, watermarkEnabled)
                    }
                },
                enabled = title.isNotBlank() && selectedClient != null,
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                modifier = Modifier.testTag("submit_create_gallery_button")
            ) {
                Text("Create Gallery", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditGalleryDialog(
    gallery: Gallery,
    onDismiss: () -> Unit,
    onConfirm: (Gallery) -> Unit
) {
    var title by remember { mutableStateOf(gallery.title) }
    var eventDate by remember { mutableStateOf(gallery.eventDate) }
    var description by remember { mutableStateOf(gallery.description) }
    var privacy by remember { mutableStateOf(gallery.privacy) }
    var allowDownloads by remember { mutableStateOf(gallery.allowDownloads) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit Gallery", style = MaterialTheme.typography.headlineSmall, fontFamily = FontFamily.Serif)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Gallery Title *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = eventDate,
                    onValueChange = { eventDate = it },
                    label = { Text("Event Date") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Message") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Privacy:", style = MaterialTheme.typography.labelSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(GalleryPrivacy.PUBLIC, GalleryPrivacy.PASSWORD, GalleryPrivacy.PIN).forEach { p ->
                        FilterChip(selected = privacy == p, onClick = { privacy = p }, label = { Text(p.name) })
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Allow Photo Downloads")
                    Switch(checked = allowDownloads, onCheckedChange = { allowDownloads = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            gallery.copy(
                                title = title,
                                eventDate = eventDate,
                                description = description,
                                privacy = privacy,
                                allowDownloads = allowDownloads
                            )
                        )
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900)
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun GalleryEditorScreen(
    galleryId: String,
    viewModel: PixoraViewModel,
    modifier: Modifier = Modifier
) {
    val galleries by viewModel.galleries.collectAsState()
    val albums by viewModel.albums.collectAsState()
    val mediaItems by viewModel.mediaItems.collectAsState()
    val gallery = galleries.find { it.id == galleryId }
    val galleryAlbums = remember(albums, galleryId) { albums.filter { it.galleryId == galleryId } }
    val galleryPhotos = remember(mediaItems, galleryId) { mediaItems.filter { it.galleryId == galleryId } }

    val deletingMediaItem by viewModel.deletingMediaItem.collectAsState()
    var selectedAlbumId by remember { mutableStateOf<String?>("All") }
    var isCreateAlbumOpen by remember { mutableStateOf(false) }

    val filteredPhotos = remember(galleryPhotos, selectedAlbumId) {
        if (selectedAlbumId == "All" || selectedAlbumId == null) galleryPhotos
        else galleryPhotos.filter { it.albumId == selectedAlbumId }
    }

    if (gallery == null) {
        EmptyStateView(
            icon = Icons.Default.CollectionsBookmark,
            title = "Gallery Not Found",
            description = "The requested gallery could not be loaded or was removed.",
            actionButtonText = "Back to Galleries",
            onActionClick = { viewModel.navigateTo(ScreenRoute.Galleries) }
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Toolbar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = gallery.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${galleryPhotos.size} Total Photos in Gallery",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { isCreateAlbumOpen = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Charcoal800, contentColor = GoldLight),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("+ Album")
                    }

                    Button(
                        onClick = { viewModel.navigateTo(ScreenRoute.QuickUpload) },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Upload", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.navigateTo(ScreenRoute.ClientLanding(gallery.id)) },
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Client View")
                    }
                }
            }
        }

        // Album Pills Row
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
                    label = { Text("All Photos (${galleryPhotos.size})") }
                )
            }
            items(galleryAlbums) { album ->
                FilterChip(
                    selected = selectedAlbumId == album.id,
                    onClick = { selectedAlbumId = album.id },
                    label = { Text("${album.name} (${album.photoCount})") }
                )
            }
        }

        // Photo Grid or Empty State
        if (filteredPhotos.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.AddPhotoAlternate,
                title = "No Photos Uploaded Yet",
                description = "Upload high-res photos to start delivering to your client.",
                actionButtonText = "+ Upload Photos",
                onActionClick = { viewModel.navigateTo(ScreenRoute.QuickUpload) }
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredPhotos, key = { it.id }) { photo ->
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

    // Create Album Dialog
    if (isCreateAlbumOpen) {
        var albumName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { isCreateAlbumOpen = false },
            title = { Text("Create Album") },
            text = {
                OutlinedTextField(
                    value = albumName,
                    onValueChange = { albumName = it },
                    label = { Text("Album Name") },
                    placeholder = { Text("e.g. Highlights, Ceremony, Reception") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (albumName.isNotBlank()) {
                            viewModel.repository.createAlbum(galleryId, albumName)
                            viewModel.showToast("Album '$albumName' Created Successfully")
                            isCreateAlbumOpen = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900)
                ) {
                    Text("Create Album", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { isCreateAlbumOpen = false }) { Text("Cancel") }
            }
        )
    }

    // Delete Media Confirmation Dialog
    if (deletingMediaItem != null) {
        PixoraDeleteConfirmDialog(
            isOpen = true,
            title = "Delete Photo",
            itemName = deletingMediaItem!!.title,
            onDismiss = { viewModel.deletingMediaItem.value = null },
            onConfirmDelete = { viewModel.confirmDeleteMedia(deletingMediaItem!!) }
        )
    }
}

@Composable
fun GallerySettingsScreen(
    galleryId: String,
    viewModel: PixoraViewModel,
    modifier: Modifier = Modifier
) {
    val galleries by viewModel.galleries.collectAsState()
    val gallery = galleries.find { it.id == galleryId }

    if (gallery == null) return

    var title by remember { mutableStateOf(gallery.title) }
    var privacy by remember { mutableStateOf(gallery.privacy) }
    var allowDownloads by remember { mutableStateOf(gallery.allowDownloads) }
    var watermarkEnabled by remember { mutableStateOf(gallery.watermarkEnabled) }
    var watermarkText by remember { mutableStateOf(gallery.watermarkText) }
    var expiryDate by remember { mutableStateOf(gallery.expiryDate) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Gallery Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("GENERAL", style = MaterialTheme.typography.labelSmall, color = GoldAccent, fontWeight = FontWeight.Bold)
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Gallery Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = expiryDate, onValueChange = { expiryDate = it }, label = { Text("Expiration Date") }, modifier = Modifier.fillMaxWidth())

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                Text("PRIVACY & ACCESS", style = MaterialTheme.typography.labelSmall, color = GoldAccent, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(GalleryPrivacy.PUBLIC, GalleryPrivacy.PASSWORD, GalleryPrivacy.PIN).forEach { p ->
                        FilterChip(selected = privacy == p, onClick = { privacy = p }, label = { Text(p.name) })
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                Text("DOWNLOAD RULES", style = MaterialTheme.typography.labelSmall, color = GoldAccent, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Allow Client Downloads")
                    Switch(checked = allowDownloads, onCheckedChange = { allowDownloads = it })
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                Text("WATERMARKING", style = MaterialTheme.typography.labelSmall, color = GoldAccent, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Apply Watermark on Previews")
                    Switch(checked = watermarkEnabled, onCheckedChange = { watermarkEnabled = it })
                }
                if (watermarkEnabled) {
                    OutlinedTextField(value = watermarkText, onValueChange = { watermarkText = it }, label = { Text("Watermark Text") }, modifier = Modifier.fillMaxWidth())
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val updated = gallery.copy(
                    title = title,
                    privacy = privacy,
                    allowDownloads = allowDownloads,
                    watermarkEnabled = watermarkEnabled,
                    watermarkText = watermarkText,
                    expiryDate = expiryDate
                )
                viewModel.updateGallery(updated)
                viewModel.navigateBack()
            },
            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Save Changes", fontWeight = FontWeight.Bold)
        }
    }
}
