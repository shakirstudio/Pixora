package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.data.model.Client
import com.example.data.model.Project
import com.example.data.model.ProjectStatus
import com.example.ui.components.EmptyStateView
import com.example.ui.components.PixoraDeleteConfirmDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.PixoraViewModel
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun ProjectsScreen(
    viewModel: PixoraViewModel,
    modifier: Modifier = Modifier
) {
    val projects by viewModel.projects.collectAsState()
    val clients by viewModel.clients.collectAsState()
    val editingProject by viewModel.editingProject.collectAsState()
    val deletingProject by viewModel.deletingProject.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    val tabTitles = listOf("All", "Active", "Upcoming", "Completed", "Archived")

    val filteredProjects = remember(projects, selectedTab, searchQuery) {
        projects.filter { proj ->
            val matchesTab = when (selectedTab) {
                1 -> proj.status == ProjectStatus.ACTIVE
                2 -> proj.status == ProjectStatus.UPCOMING
                3 -> proj.status == ProjectStatus.COMPLETED
                4 -> proj.status == ProjectStatus.ARCHIVED
                else -> true
            }
            val matchesSearch = if (searchQuery.isBlank()) true
            else proj.name.contains(searchQuery, ignoreCase = true) ||
                    proj.clientName.contains(searchQuery, ignoreCase = true) ||
                    proj.location.contains(searchQuery, ignoreCase = true)
            matchesTab && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Search & Add Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search projects / shoots...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldAccent) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("projects_search_input"),
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
                        viewModel.showToast("Please add a client first before creating a project.")
                        viewModel.isCreateClientOpen.value = true
                    } else {
                        viewModel.isCreateProjectOpen.value = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .height(52.dp)
                    .testTag("create_project_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Project", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = GoldAccent,
            edgePadding = 0.dp
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (filteredProjects.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.FolderOpen,
                title = if (searchQuery.isBlank()) "No Projects Found" else "No matching projects",
                description = if (searchQuery.isBlank()) "Create a project shoot to track deliverables, milestones, budgets, and galleries." else "No projects found matching \"$searchQuery\".",
                actionButtonText = "+ Create New Project",
                onActionClick = {
                    if (clients.isEmpty()) {
                        viewModel.isCreateClientOpen.value = true
                    } else {
                        viewModel.isCreateProjectOpen.value = true
                    }
                }
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredProjects, key = { it.id }) { project ->
                    ProjectCardItem(
                        project = project,
                        onClick = { viewModel.navigateTo(ScreenRoute.ProjectDashboard(project.id)) },
                        onEdit = { viewModel.editingProject.value = project },
                        onDelete = { viewModel.deletingProject.value = project }
                    )
                }
            }
        }
    }

    // Edit Project Dialog
    if (editingProject != null) {
        EditProjectDialog(
            project = editingProject!!,
            onDismiss = { viewModel.editingProject.value = null },
            onConfirm = { updated -> viewModel.updateProject(updated) }
        )
    }

    // Delete Project Confirmation Dialog
    if (deletingProject != null) {
        PixoraDeleteConfirmDialog(
            isOpen = true,
            title = "Delete Project",
            itemName = deletingProject!!.name,
            onDismiss = { viewModel.deletingProject.value = null },
            onConfirmDelete = { viewModel.confirmDeleteProject(deletingProject!!) }
        )
    }
}

@Composable
fun ProjectCardItem(
    project: Project,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("project_card_${project.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = project.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Client: ${project.clientName} • ${project.eventType}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (project.status) {
                        ProjectStatus.ACTIVE -> GoldAccent
                        ProjectStatus.UPCOMING -> LuxuryBlue
                        ProjectStatus.COMPLETED -> LuxuryGreen
                        ProjectStatus.ARCHIVED -> Charcoal500
                    }
                ) {
                    Text(
                        text = project.status.name,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Charcoal900,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (project.eventDate.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = project.eventDate, style = MaterialTheme.typography.bodySmall)
                    }
                }
                if (project.location.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = project.location, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Budget: ${project.budget}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Project", tint = GoldAccent, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Project", tint = LuxuryRed, modifier = Modifier.size(18.dp))
                    }
                    OutlinedButton(
                        onClick = onClick,
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("View", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun CreateProjectDialog(
    isOpen: Boolean,
    clients: List<Client>,
    onDismiss: () -> Unit,
    onConfirm: (name: String, client: Client, eventType: String, eventDate: String, location: String, budget: String, description: String) -> Unit
) {
    if (!isOpen) return

    var name by remember { mutableStateOf("") }
    var selectedClient by remember { mutableStateOf(clients.firstOrNull()) }
    var eventType by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create New Project / Shoot", style = MaterialTheme.typography.headlineSmall, fontFamily = FontFamily.Serif)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Project Title *") },
                    placeholder = { Text("e.g. Winter Wedding Shoot") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("new_project_name")
                )

                Text(text = "Assign to Client *:", style = MaterialTheme.typography.labelSmall)
                clients.forEach { client ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedClient = client },
                        shape = RoundedCornerShape(6.dp),
                        color = if (selectedClient?.id == client.id) GoldSubtle else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (selectedClient?.id == client.id) ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(GoldAccent, GoldLight))) else null
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
                    value = eventType,
                    onValueChange = { eventType = it },
                    label = { Text("Shoot / Event Type") },
                    placeholder = { Text("e.g. Wedding, Pre-Wedding, Commercial") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = eventDate,
                    onValueChange = { eventDate = it },
                    label = { Text("Event Date") },
                    placeholder = { Text("e.g. 15 Dec 2026") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location Venue") },
                    placeholder = { Text("e.g. Resort, Studio or City") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = budget,
                    onValueChange = { budget = it },
                    label = { Text("Agreed Budget") },
                    placeholder = { Text("e.g. ₹1,50,000") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Shoot Description / Brief") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && selectedClient != null) {
                        onConfirm(name, selectedClient!!, eventType, eventDate, location, budget, description)
                    }
                },
                enabled = name.isNotBlank() && selectedClient != null,
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                modifier = Modifier.testTag("submit_create_project_button")
            ) {
                Text("Create Project", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditProjectDialog(
    project: Project,
    onDismiss: () -> Unit,
    onConfirm: (Project) -> Unit
) {
    var name by remember { mutableStateOf(project.name) }
    var eventType by remember { mutableStateOf(project.eventType) }
    var eventDate by remember { mutableStateOf(project.eventDate) }
    var location by remember { mutableStateOf(project.location) }
    var budget by remember { mutableStateOf(project.budget) }
    var description by remember { mutableStateOf(project.description) }
    var status by remember { mutableStateOf(project.status) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit Project Shoot", style = MaterialTheme.typography.headlineSmall, fontFamily = FontFamily.Serif)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Project Title *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = eventType,
                    onValueChange = { eventType = it },
                    label = { Text("Event Type") },
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
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = budget,
                    onValueChange = { budget = it },
                    label = { Text("Budget") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Status:", style = MaterialTheme.typography.labelSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(ProjectStatus.ACTIVE, ProjectStatus.UPCOMING, ProjectStatus.COMPLETED, ProjectStatus.ARCHIVED).forEach { st ->
                        FilterChip(
                            selected = status == st,
                            onClick = { status = st },
                            label = { Text(st.name) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(
                            project.copy(
                                name = name,
                                eventType = eventType,
                                eventDate = eventDate,
                                location = location,
                                budget = budget,
                                description = description,
                                status = status
                            )
                        )
                    }
                },
                enabled = name.isNotBlank(),
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
fun ProjectDashboardScreen(
    projectId: String,
    viewModel: PixoraViewModel,
    modifier: Modifier = Modifier
) {
    val projects by viewModel.projects.collectAsState()
    val project = projects.find { it.id == projectId }
    val galleries by viewModel.galleries.collectAsState()
    val projectGalleries = remember(galleries, projectId) { galleries.filter { it.projectId == projectId } }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Galleries (${projectGalleries.size})")

    if (project == null) {
        EmptyStateView(
            icon = Icons.Default.FolderOff,
            title = "Project Not Found",
            description = "The requested project could not be found or was deleted.",
            actionButtonText = "Back to Projects",
            onActionClick = { viewModel.navigateTo(ScreenRoute.Projects) }
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Project Header Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = project.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Client: ${project.clientName} • ${project.eventType}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GoldAccent
                        )
                    }

                    Row {
                        IconButton(onClick = { viewModel.editingProject.value = project }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Project", tint = GoldAccent)
                        }
                        IconButton(onClick = { viewModel.deletingProject.value = project }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Project", tint = LuxuryRed)
                        }
                    }
                }

                if (project.eventDate.isNotBlank() || project.location.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Date: ${project.eventDate} | Location: ${project.location}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (project.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = project.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.isCreateGalleryOpen.value = true },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+ Create Gallery", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { viewModel.navigateTo(ScreenRoute.QuickUpload) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Upload Media")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = GoldAccent
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("PROJECT DETAILS", style = MaterialTheme.typography.labelSmall, color = GoldAccent, fontWeight = FontWeight.Bold)
                        Text("Client Name: ${project.clientName}")
                        Text("Budget: ${project.budget}")
                        Text("Status: ${project.status.name}")
                        Text("Associated Galleries: ${projectGalleries.size}")
                    }
                }
            }
            else -> {
                if (projectGalleries.isEmpty()) {
                    EmptyStateView(
                        icon = Icons.Default.Collections,
                        title = "No Galleries in this Project",
                        description = "Create a gallery to organize photos for this shoot.",
                        actionButtonText = "+ Create Gallery",
                        onActionClick = { viewModel.isCreateGalleryOpen.value = true }
                    )
                } else {
                    projectGalleries.forEach { gal ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable {
                                viewModel.navigateTo(ScreenRoute.GalleryEditor(gal.id))
                            }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(gal.title, fontWeight = FontWeight.Bold)
                                Text("${gal.photoCount} photos • Privacy: ${gal.privacy.name}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}
