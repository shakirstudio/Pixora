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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Client
import com.example.ui.components.EmptyStateView
import com.example.ui.components.PixoraDeleteConfirmDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.PixoraViewModel
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun ClientsScreen(
    viewModel: PixoraViewModel,
    modifier: Modifier = Modifier
) {
    val clients by viewModel.clients.collectAsState()
    val editingClient by viewModel.editingClient.collectAsState()
    val deletingClient by viewModel.deletingClient.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredClients = remember(clients, searchQuery) {
        if (searchQuery.isBlank()) clients
        else clients.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.email.contains(searchQuery, ignoreCase = true) ||
                    it.phone.contains(searchQuery)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Search & Add Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search clients by name, email, phone...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldAccent) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("clients_search_input"),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldAccent,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                onClick = { viewModel.isCreateClientOpen.value = true },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .height(52.dp)
                    .testTag("create_client_button")
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Client", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredClients.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.PeopleOutline,
                title = if (searchQuery.isBlank()) "No Clients Found" else "No matching clients",
                description = if (searchQuery.isBlank()) "Start by adding your first client. All project shoots, galleries, and invoices will be linked here." else "No clients match \"$searchQuery\". Try another query or add a new client.",
                actionButtonText = "+ Add New Client",
                onActionClick = { viewModel.isCreateClientOpen.value = true }
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredClients, key = { it.id }) { client ->
                    ClientCardItem(
                        client = client,
                        onClick = { viewModel.navigateTo(ScreenRoute.ClientProfile(client.id)) },
                        onEdit = { viewModel.editingClient.value = client },
                        onDelete = { viewModel.deletingClient.value = client }
                    )
                }
            }
        }
    }

    // Edit Client Dialog
    if (editingClient != null) {
        EditClientDialog(
            client = editingClient!!,
            onDismiss = { viewModel.editingClient.value = null },
            onConfirm = { updated -> viewModel.updateClient(updated) }
        )
    }

    // Delete Client Confirmation Dialog
    if (deletingClient != null) {
        PixoraDeleteConfirmDialog(
            isOpen = true,
            title = "Delete Client",
            itemName = deletingClient!!.name,
            onDismiss = { viewModel.deletingClient.value = null },
            onConfirmDelete = { viewModel.confirmDeleteClient(deletingClient!!) }
        )
    }
}

@Composable
fun ClientCardItem(
    client: Client,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("client_card_${client.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(GoldSubtle)
                        .border(1.dp, GoldAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = client.name.take(2).uppercase().ifBlank { "CL" },
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = client.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = client.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (client.phone.isNotBlank()) {
                        Text(
                            text = client.phone,
                            style = MaterialTheme.typography.bodySmall,
                            color = GoldAccent
                        )
                    }
                }

                if (client.eventType.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = client.eventType,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
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
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column {
                        Text(text = "Projects", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "${client.activeProjectsCount}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text(text = "Galleries", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "${client.galleriesCount}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(34.dp).testTag("edit_client_${client.id}")) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Client", tint = GoldAccent, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(34.dp).testTag("delete_client_${client.id}")) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Client", tint = LuxuryRed, modifier = Modifier.size(18.dp))
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
fun CreateClientDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (name: String, email: String, phone: String, address: String, eventType: String, notes: String) -> Unit
) {
    if (!isOpen) return

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var eventType by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add New Client", style = MaterialTheme.typography.headlineSmall, fontFamily = FontFamily.Serif)
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
                    label = { Text("Client Full Name *") },
                    placeholder = { Text("Enter client name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("new_client_name")
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address *") },
                    placeholder = { Text("client@domain.com") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("new_client_email")
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone / WhatsApp") },
                    placeholder = { Text("+91 9876543210") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("new_client_phone")
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Location / Venue") },
                    placeholder = { Text("City, State or Venue") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = eventType,
                    onValueChange = { eventType = it },
                    label = { Text("Event Type") },
                    placeholder = { Text("e.g. Wedding, Pre-Wedding, Fashion") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Requirements") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank()) {
                        onConfirm(name, email, phone, address, eventType, notes)
                    }
                },
                enabled = name.isNotBlank() && email.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                modifier = Modifier.testTag("submit_create_client_button")
            ) {
                Text("Create Client", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditClientDialog(
    client: Client,
    onDismiss: () -> Unit,
    onConfirm: (Client) -> Unit
) {
    var name by remember { mutableStateOf(client.name) }
    var email by remember { mutableStateOf(client.email) }
    var phone by remember { mutableStateOf(client.phone) }
    var address by remember { mutableStateOf(client.address) }
    var eventType by remember { mutableStateOf(client.eventType) }
    var notes by remember { mutableStateOf(client.notes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit Client Details", style = MaterialTheme.typography.headlineSmall, fontFamily = FontFamily.Serif)
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
                    label = { Text("Client Full Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_client_name")
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address / Venue") },
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
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank()) {
                        onConfirm(
                            client.copy(
                                name = name,
                                email = email,
                                phone = phone,
                                address = address,
                                eventType = eventType,
                                notes = notes
                            )
                        )
                    }
                },
                enabled = name.isNotBlank() && email.isNotBlank(),
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
fun ClientProfileScreen(
    clientId: String,
    viewModel: PixoraViewModel,
    modifier: Modifier = Modifier
) {
    val clients by viewModel.clients.collectAsState()
    val client = clients.find { it.id == clientId }
    val projects by viewModel.projects.collectAsState()
    val galleries by viewModel.galleries.collectAsState()
    val invoices by viewModel.invoices.collectAsState()

    val clientProjects = remember(projects, clientId) { projects.filter { it.clientId == clientId } }
    val clientGalleries = remember(galleries, clientId) { galleries.filter { it.clientId == clientId } }
    val clientInvoices = remember(invoices, clientId) { invoices.filter { it.clientId == clientId } }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Projects (${clientProjects.size})", "Galleries (${clientGalleries.size})", "Invoices (${clientInvoices.size})")

    if (client == null) {
        EmptyStateView(
            icon = Icons.Default.PersonOff,
            title = "Client Not Found",
            description = "This client record was removed or does not exist.",
            actionButtonText = "Back to Clients",
            onActionClick = { viewModel.navigateTo(ScreenRoute.Clients) }
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
        // Client Header Card
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(GoldSubtle)
                                .border(2.dp, GoldAccent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = client.name.take(2).uppercase().ifBlank { "CL" },
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = client.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = client.email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (client.phone.isNotBlank()) {
                                Text(
                                    text = client.phone,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = GoldAccent
                                )
                            }
                        }
                    }

                    Row {
                        IconButton(onClick = { viewModel.editingClient.value = client }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Client", tint = GoldAccent)
                        }
                        IconButton(onClick = { viewModel.deletingClient.value = client }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Client", tint = LuxuryRed)
                        }
                    }
                }

                if (client.address.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Location: ${client.address}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (client.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Notes: ${client.notes}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.isCreateProjectOpen.value = true },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+ New Project", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { viewModel.isCreateInvoiceOpen.value = true },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Create Invoice")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Profile Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = GoldAccent,
            edgePadding = 0.dp
        ) {
            tabs.forEachIndexed { index, title ->
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

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> {
                // Overview
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("CLIENT SUMMARY", style = MaterialTheme.typography.labelSmall, color = GoldAccent, fontWeight = FontWeight.Bold)
                        Text("Associated Projects: ${clientProjects.size}")
                        Text("Associated Galleries: ${clientGalleries.size}")
                        Text("Total Invoices: ${clientInvoices.size}")
                        Text("Client Event Type: ${client.eventType.ifBlank { "Not Specified" }}")
                    }
                }
            }
            1 -> {
                if (clientProjects.isEmpty()) {
                    EmptyStateView(
                        icon = Icons.Default.FolderOpen,
                        title = "No Projects for this Client",
                        description = "Create a project shoot for ${client.name}.",
                        actionButtonText = "+ Add Project",
                        onActionClick = { viewModel.isCreateProjectOpen.value = true }
                    )
                } else {
                    clientProjects.forEach { proj ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable {
                                viewModel.navigateTo(ScreenRoute.ProjectDashboard(proj.id))
                            }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(proj.name, fontWeight = FontWeight.Bold)
                                Text("${proj.eventType} • ${proj.eventDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
            2 -> {
                if (clientGalleries.isEmpty()) {
                    EmptyStateView(
                        icon = Icons.Default.Collections,
                        title = "No Galleries for this Client",
                        description = "Create a gallery to share client photos.",
                        actionButtonText = "+ Add Gallery",
                        onActionClick = { viewModel.isCreateGalleryOpen.value = true }
                    )
                } else {
                    clientGalleries.forEach { gal ->
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
            else -> {
                if (clientInvoices.isEmpty()) {
                    EmptyStateView(
                        icon = Icons.Default.Receipt,
                        title = "No Invoices for this Client",
                        description = "Generate an invoice or quotation for ${client.name}.",
                        actionButtonText = "+ Create Invoice",
                        onActionClick = { viewModel.isCreateInvoiceOpen.value = true }
                    )
                } else {
                    clientInvoices.forEach { inv ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(inv.id, fontWeight = FontWeight.Bold, color = GoldAccent)
                                Text("Total: ₹${inv.totalAmount.toInt()} • Status: ${inv.status.name}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
