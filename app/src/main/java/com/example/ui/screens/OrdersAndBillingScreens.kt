package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Invoice
import com.example.data.model.InvoiceStatus
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*
import com.example.ui.viewmodel.PixoraViewModel
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun OrdersScreen(
    viewModel: PixoraViewModel,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.orders.collectAsState()
    var selectedStatus by remember { mutableStateOf<OrderStatus?>(null) }
    val statuses = listOf(null, OrderStatus.PROCESSING, OrderStatus.PAID, OrderStatus.COMPLETED)

    val filtered = remember(orders, selectedStatus) {
        if (selectedStatus == null) orders
        else orders.filter { it.status == selectedStatus }
    }

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
                Text(
                    text = "Studio Orders",
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Track album, print and digital download purchases",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Status Filter Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(statuses) { st ->
                FilterChip(
                    selected = selectedStatus == st,
                    onClick = { selectedStatus = st },
                    label = { Text(st?.name ?: "All Orders") }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (filtered.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.ShoppingBag,
                title = "No Orders Found",
                description = "Client print and album orders will automatically populate here."
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filtered, key = { it.id }) { order ->
                    OrderCardItem(
                        order = order,
                        onClick = { viewModel.navigateTo(ScreenRoute.OrderDetail(order.id)) }
                    )
                }
            }
        }
    }
}

@Composable
fun OrderCardItem(
    order: Order,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("order_card_${order.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(order.id, fontWeight = FontWeight.Bold, color = GoldAccent)
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (order.status) {
                        OrderStatus.COMPLETED -> LuxuryGreenLight
                        OrderStatus.PROCESSING -> LuxuryAmberLight
                        else -> LuxuryBlueLight
                    }
                ) {
                    Text(
                        text = order.status.name,
                        color = when (order.status) {
                            OrderStatus.COMPLETED -> LuxuryGreen
                            OrderStatus.PROCESSING -> LuxuryAmber
                            else -> LuxuryBlue
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(order.clientName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("${order.items.size} item(s) • ${order.paymentMethod}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(order.date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
fun OrderDetailScreen(
    orderId: String,
    viewModel: PixoraViewModel,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.orders.collectAsState()
    val order = orders.find { it.id == orderId } ?: orders.firstOrNull()

    if (order == null) return

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
            Text("Order ${order.id}", style = MaterialTheme.typography.headlineSmall, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("CLIENT DETAILS", style = MaterialTheme.typography.labelSmall, color = GoldAccent, fontWeight = FontWeight.Bold)
                Text(order.clientName, fontWeight = FontWeight.Bold)
                Text(order.clientEmail, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(order.clientPhone, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), modifier = Modifier.padding(vertical = 4.dp))
                Text("PAYMENT TRANSACTION", style = MaterialTheme.typography.labelSmall, color = GoldAccent, fontWeight = FontWeight.Bold)
                Text("Method: ${order.paymentMethod}")
                Text("Ref: ${order.transactionRef}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Total Paid: ₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("PURCHASED ITEMS", style = MaterialTheme.typography.labelSmall, color = GoldAccent, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        order.items.forEach { item ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.padding(vertical = 4.dp)) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.product.name, fontWeight = FontWeight.Bold)
                        Text(item.variant, style = MaterialTheme.typography.bodySmall, color = GoldAccent)
                        Text("Qty: ${item.quantity}", style = MaterialTheme.typography.bodySmall)
                    }
                    Text("₹${(item.product.price * item.quantity).toInt()}", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.showToast("Generated PDF invoice for order ${order.id}") },
            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Icon(Icons.Default.Download, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Download Lab Order & Invoice PDF", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun InvoicesScreen(
    viewModel: PixoraViewModel,
    modifier: Modifier = Modifier
) {
    val invoices by viewModel.invoices.collectAsState()

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
                Text(
                    text = "Billing & Invoices",
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
                Text("Create and send GST invoices to clients", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Button(
                onClick = { viewModel.isCreateInvoiceOpen.value = true },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Create Invoice", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(invoices, key = { it.id }) { invoice ->
                InvoiceCardItem(
                    invoice = invoice,
                    onDownloadPdf = { viewModel.showToast("Downloading ${invoice.id} PDF...") }
                )
            }
        }
    }
}

@Composable
fun InvoiceCardItem(
    invoice: Invoice,
    onDownloadPdf: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(invoice.id, fontWeight = FontWeight.Bold, color = GoldAccent)
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (invoice.status == InvoiceStatus.PAID) LuxuryGreenLight else LuxuryAmberLight
                ) {
                    Text(
                        text = invoice.status.name,
                        color = if (invoice.status == InvoiceStatus.PAID) LuxuryGreen else LuxuryAmber,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(invoice.clientName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(invoice.projectName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Due: ${invoice.dueDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("₹${invoice.totalAmount.toInt()} (Incl. 18% GST)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
                OutlinedButton(onClick = onDownloadPdf, shape = RoundedCornerShape(6.dp)) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PDF")
                }
            }
        }
    }
}

@Composable
fun CreateInvoiceDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (clientName: String, clientEmail: String, projectName: String, description: String, amount: Double) -> Unit
) {
    if (!isOpen) return

    var clientName by remember { mutableStateOf("Rahul Sharma & Priya Verma") }
    var clientEmail by remember { mutableStateOf("rahul.priya@weddings.com") }
    var projectName by remember { mutableStateOf("Rahul & Priya - Royal Udaipur Wedding") }
    var description by remember { mutableStateOf("Signature 3-Day Wedding Photography & 4K Cinema Coverage") }
    var amountText by remember { mutableStateOf("450000") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create GST Invoice", style = MaterialTheme.typography.headlineSmall, fontFamily = FontFamily.Serif)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = clientName, onValueChange = { clientName = it }, label = { Text("Client Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = clientEmail, onValueChange = { clientEmail = it }, label = { Text("Client Email") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = projectName, onValueChange = { projectName = it }, label = { Text("Project") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Services Description") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Base Subtotal Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "GST @ 18%: ₹${((amountText.toDoubleOrNull() ?: 0.0) * 0.18).toInt()} | Total: ₹${((amountText.toDoubleOrNull() ?: 0.0) * 1.18).toInt()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = GoldAccent
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (clientName.isNotBlank() && amount > 0) {
                        onConfirm(clientName, clientEmail, projectName, description, amount)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900)
            ) {
                Text("Generate & Send", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
