package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.model.*
import com.example.ui.components.EmptyStateView
import com.example.ui.components.PixoraDeleteConfirmDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.PixoraViewModel

@Composable
fun TasksScreen(
    viewModel: PixoraViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.tasks.collectAsState()
    val clients by viewModel.clients.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val teamMembers by viewModel.teamMembers.collectAsState()
    val isCreateTaskOpen by viewModel.isCreateTaskOpen.collectAsState()
    val editingTask by viewModel.editingTask.collectAsState()
    val deletingTask by viewModel.deletingTask.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    val tabTitles = listOf("All", "Pending", "In Progress", "Completed", "Cancelled")

    val filteredTasks = remember(tasks, selectedTab, searchQuery) {
        tasks.filter { task ->
            val matchesTab = when (selectedTab) {
                1 -> task.status == TaskStatus.PENDING
                2 -> task.status == TaskStatus.IN_PROGRESS
                3 -> task.status == TaskStatus.COMPLETED
                4 -> task.status == TaskStatus.CANCELLED
                else -> true
            }
            val matchesSearch = if (searchQuery.isBlank()) true
            else task.title.contains(searchQuery, ignoreCase = true) ||
                    task.clientName.contains(searchQuery, ignoreCase = true) ||
                    task.projectName.contains(searchQuery, ignoreCase = true) ||
                    task.assignedToName.contains(searchQuery, ignoreCase = true)
            matchesTab && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Search & Add Task Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search studio tasks & shoots...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldAccent) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("tasks_search_input"),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldAccent,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                onClick = { viewModel.isCreateTaskOpen.value = true },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .height(52.dp)
                    .testTag("create_task_button")
            ) {
                Icon(Icons.Default.AddTask, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Task", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Status Tabs
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

        if (filteredTasks.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.Checklist,
                title = if (searchQuery.isBlank()) "No Tasks Found" else "No matching tasks",
                description = if (searchQuery.isBlank())
                    "Add tasks to assign responsibilities to photographers, editors, and track shoot milestones."
                else "No tasks found matching \"$searchQuery\". Try another query or add a new task.",
                actionButtonText = "+ Add New Task",
                onActionClick = { viewModel.isCreateTaskOpen.value = true }
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredTasks, key = { it.id }) { task ->
                    TaskCardItem(
                        task = task,
                        onStatusToggle = {
                            val nextStatus = when (task.status) {
                                TaskStatus.PENDING -> TaskStatus.IN_PROGRESS
                                TaskStatus.IN_PROGRESS -> TaskStatus.COMPLETED
                                TaskStatus.COMPLETED -> TaskStatus.PENDING
                                TaskStatus.CANCELLED -> TaskStatus.PENDING
                            }
                            viewModel.updateTask(task.copy(status = nextStatus))
                        },
                        onEdit = { viewModel.editingTask.value = task },
                        onDelete = { viewModel.deletingTask.value = task }
                    )
                }
            }
        }
    }

    // Create Task Dialog
    if (isCreateTaskOpen) {
        CreateTaskDialog(
            clients = clients,
            projects = projects,
            teamMembers = teamMembers,
            onDismiss = { viewModel.isCreateTaskOpen.value = false },
            onConfirm = { title, desc, client, project, member, dueDate, priority ->
                viewModel.createTask(title, desc, client, project, member, dueDate, priority)
            }
        )
    }

    // Edit Task Dialog
    if (editingTask != null) {
        EditTaskDialog(
            task = editingTask!!,
            clients = clients,
            projects = projects,
            teamMembers = teamMembers,
            onDismiss = { viewModel.editingTask.value = null },
            onConfirm = { updated -> viewModel.updateTask(updated) }
        )
    }

    // Delete Task Confirmation Dialog
    if (deletingTask != null) {
        PixoraDeleteConfirmDialog(
            isOpen = true,
            title = "Delete Task",
            itemName = deletingTask!!.title,
            onDismiss = { viewModel.deletingTask.value = null },
            onConfirmDelete = { viewModel.confirmDeleteTask(deletingTask!!) }
        )
    }
}

@Composable
fun TaskCardItem(
    task: StudioTask,
    onStatusToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_card_${task.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    IconButton(
                        onClick = onStatusToggle,
                        modifier = Modifier.size(28.dp)
                    ) {
                        val icon = when (task.status) {
                            TaskStatus.COMPLETED -> Icons.Default.CheckCircle
                            TaskStatus.IN_PROGRESS -> Icons.Default.Timelapse
                            TaskStatus.CANCELLED -> Icons.Default.Cancel
                            TaskStatus.PENDING -> Icons.Default.RadioButtonUnchecked
                        }
                        val tint = when (task.status) {
                            TaskStatus.COMPLETED -> LuxuryGreen
                            TaskStatus.IN_PROGRESS -> LuxuryAmber
                            TaskStatus.CANCELLED -> LuxuryRed
                            TaskStatus.PENDING -> Charcoal400
                        }
                        Icon(imageVector = icon, contentDescription = "Toggle status", tint = tint)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (task.status) {
                        TaskStatus.COMPLETED -> LuxuryGreenLight
                        TaskStatus.IN_PROGRESS -> LuxuryAmberLight
                        TaskStatus.CANCELLED -> LuxuryRedLight
                        TaskStatus.PENDING -> Charcoal700
                    }
                ) {
                    Text(
                        text = task.status.name.replace("_", " "),
                        color = when (task.status) {
                            TaskStatus.COMPLETED -> LuxuryGreen
                            TaskStatus.IN_PROGRESS -> LuxuryAmber
                            TaskStatus.CANCELLED -> LuxuryRed
                            TaskStatus.PENDING -> Charcoal200
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metadata Row: Client, Project, Assignee, Due Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    if (task.clientName.isNotBlank() || task.projectName.isNotBlank()) {
                        Text(
                            text = listOfNotNull(
                                task.clientName.ifBlank { null },
                                task.projectName.ifBlank { null }
                            ).joinToString(" • "),
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldAccent,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    val assignee = if (task.assignedToName.isNotBlank()) "Assigned: ${task.assignedToName}" else "Unassigned"
                    val due = if (task.dueDate.isNotBlank()) " | Due: ${task.dueDate}" else ""
                    Text(
                        text = "$assignee$due",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit task", tint = Charcoal300, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete task", tint = LuxuryRed, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CreateTaskDialog(
    clients: List<Client>,
    projects: List<Project>,
    teamMembers: List<TeamMember>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Client?, Project?, TeamMember?, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedClient by remember { mutableStateOf<Client?>(clients.firstOrNull()) }
    var selectedProject by remember { mutableStateOf<Project?>(projects.firstOrNull()) }
    var selectedMember by remember { mutableStateOf<TeamMember?>(teamMembers.firstOrNull()) }
    var dueDate by remember { mutableStateOf("Tomorrow") }
    var priority by remember { mutableStateOf("Normal") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Studio Task", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (error != null) {
                    Text(text = error!!, color = LuxuryRed, style = MaterialTheme.typography.bodySmall)
                }
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; error = null },
                    label = { Text("Task Title *") },
                    placeholder = { Text("e.g. Color grade Haldi photos") },
                    modifier = Modifier.fillMaxWidth().testTag("task_title_input"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text("Additional notes, instructions") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date / Deadline") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        error = "Please enter a task title."
                    } else {
                        onConfirm(title, description, selectedClient, selectedProject, selectedMember, dueDate, priority)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900)
            ) {
                Text("Create Task", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditTaskDialog(
    task: StudioTask,
    clients: List<Client>,
    projects: List<Project>,
    teamMembers: List<TeamMember>,
    onDismiss: () -> Unit,
    onConfirm: (StudioTask) -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var dueDate by remember { mutableStateOf(task.dueDate) }
    var priority by remember { mutableStateOf(task.priority) }
    var status by remember { mutableStateOf(task.status) }
    var assignedToName by remember { mutableStateOf(task.assignedToName) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Task", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (error != null) {
                    Text(text = error!!, color = LuxuryRed, style = MaterialTheme.typography.bodySmall)
                }
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; error = null },
                    label = { Text("Task Title *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = assignedToName,
                    onValueChange = { assignedToName = it },
                    label = { Text("Assigned Employee / Retoucher") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Status:", style = MaterialTheme.typography.bodyMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = status == TaskStatus.PENDING,
                            onClick = { status = TaskStatus.PENDING },
                            label = { Text("Pending") }
                        )
                        FilterChip(
                            selected = status == TaskStatus.IN_PROGRESS,
                            onClick = { status = TaskStatus.IN_PROGRESS },
                            label = { Text("Progress") }
                        )
                        FilterChip(
                            selected = status == TaskStatus.COMPLETED,
                            onClick = { status = TaskStatus.COMPLETED },
                            label = { Text("Done") }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        error = "Please enter a task title."
                    } else {
                        onConfirm(
                            task.copy(
                                title = title.trim(),
                                description = description.trim(),
                                assignedToName = assignedToName.trim(),
                                dueDate = dueDate.trim(),
                                priority = priority,
                                status = status
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900)
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
