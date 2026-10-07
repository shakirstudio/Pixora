package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CartItem
import com.example.data.model.StoreProduct
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*
import com.example.ui.viewmodel.PixoraViewModel
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun ClientStoreScreen(
    galleryId: String,
    viewModel: PixoraViewModel
) {
    val products by viewModel.storeProducts.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val categories = listOf("All", "Albums", "Prints", "Frames", "Digital Downloads", "Packages")
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredProducts = remember(products, selectedCategory) {
        if (selectedCategory == "All") products
        else products.filter { it.category == selectedCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Charcoal900)
    ) {
        // Store Header Bar
        Surface(color = Charcoal900, tonalElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Shop Your Memories",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Serif,
                        color = Color.White
                    )
                }

                // Cart Icon with count
                IconButton(
                    onClick = { viewModel.navigateTo(ScreenRoute.ClientCart) },
                    modifier = Modifier.testTag("store_cart_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (cart.isNotEmpty()) {
                                Badge(containerColor = GoldAccent, contentColor = Charcoal900) {
                                    Text("${cart.sumOf { it.quantity }}")
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.ShoppingBag, contentDescription = "Cart", tint = GoldAccent)
                    }
                }
            }
        }

        // Category Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat, color = if (selectedCategory == cat) Charcoal900 else WarmWhite) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldAccent)
                )
            }
        }

        // Product Catalog
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredProducts, key = { it.id }) { product ->
                StoreProductCard(
                    product = product,
                    onOpenDetail = { viewModel.navigateTo(ScreenRoute.ClientProductDetail(product.id)) },
                    onAddToCart = { viewModel.addToCart(product, product.variants.first()) }
                )
            }
        }
    }
}

@Composable
fun StoreProductCard(
    product: StoreProduct,
    onOpenDetail: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenDetail() }
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Charcoal800)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Charcoal900.copy(alpha = 0.85f),
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                ) {
                    Text(
                        text = product.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = GoldAccent,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Charcoal300,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₹${product.price.toInt()}",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )

                    Button(
                        onClick = onAddToCart,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add to Cart", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductDetailScreen(
    productId: String,
    viewModel: PixoraViewModel
) {
    val products by viewModel.storeProducts.collectAsState()
    val product = products.find { it.id == productId } ?: products.firstOrNull()

    if (product == null) return

    var selectedVariant by remember { mutableStateOf(product.variants.firstOrNull() ?: "") }
    var quantity by remember { mutableStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Charcoal900)
            .verticalScroll(rememberScrollState())
    ) {
        // Image Header
        Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(12.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = product.category.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = GoldAccent,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = FontFamily.Serif,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "₹${product.price.toInt()}",
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = GoldLight
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Charcoal200,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "SELECT VARIANT / SIZE",
                style = MaterialTheme.typography.labelSmall,
                color = GoldAccent,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            product.variants.forEach { v ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedVariant = v },
                    shape = RoundedCornerShape(8.dp),
                    color = if (selectedVariant == v) Charcoal800 else Charcoal900,
                    border = if (selectedVariant == v) ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(GoldAccent)) else ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Charcoal700))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(v, color = Color.White, fontWeight = if (selectedVariant == v) FontWeight.Bold else FontWeight.Normal)
                        if (selectedVariant == v) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = GoldAccent)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "QUANTITY",
                style = MaterialTheme.typography.labelSmall,
                color = GoldAccent,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { if (quantity > 1) quantity-- },
                    modifier = Modifier.clip(CircleShape).background(Charcoal800)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("$quantity", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(
                    onClick = { quantity++ },
                    modifier = Modifier.clip(CircleShape).background(Charcoal800)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.addToCart(product, selectedVariant, quantity)
                    viewModel.navigateTo(ScreenRoute.ClientCart)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("product_add_to_cart_button"),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.ShoppingBag, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add to Cart • ₹${(product.price * quantity).toInt()}", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CartScreen(
    viewModel: PixoraViewModel
) {
    val cart by viewModel.cart.collectAsState()
    val subtotal = cart.sumOf { it.product.price * it.quantity }
    val tax = subtotal * 0.18
    val discount = if (subtotal > 20000) 2500.0 else 0.0
    val total = subtotal + tax - discount

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
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Shopping Cart",
                style = MaterialTheme.typography.headlineSmall,
                fontFamily = FontFamily.Serif,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (cart.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.ShoppingBag,
                title = "Your Cart is Empty",
                description = "Explore fine art prints, handcrafted Italian leather albums, and wall frames."
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cart, key = { it.id }) { item ->
                    CartItemCard(
                        item = item,
                        onQuantityChange = { q -> viewModel.updateCartQuantity(item.id, q) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Order Summary Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Charcoal800)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", color = Charcoal300)
                        Text("₹${subtotal.toInt()}", color = Color.White)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("GST (18%)", color = Charcoal300)
                        Text("₹${tax.toInt()}", color = Color.White)
                    }
                    if (discount > 0) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Luxury Wedding Discount", color = LuxuryGreen)
                            Text("-₹${discount.toInt()}", color = LuxuryGreen)
                        }
                    }
                    Divider(color = Charcoal700, modifier = Modifier.padding(vertical = 4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Amount", fontWeight = FontWeight.Bold, color = Color.White, style = MaterialTheme.typography.titleMedium)
                        Text("₹${total.toInt()}", fontWeight = FontWeight.Bold, color = GoldLight, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.navigateTo(ScreenRoute.ClientCheckout) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("cart_proceed_checkout_button"),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Proceed to Checkout", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    onQuantityChange: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Charcoal800)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.product.imageUrl,
                contentDescription = item.product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.product.name, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
                Text(item.variant, style = MaterialTheme.typography.bodySmall, color = GoldLight)
                Text("₹${item.product.price.toInt()} each", style = MaterialTheme.typography.bodySmall, color = Charcoal300)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onQuantityChange(item.quantity - 1) }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Remove, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
                Text("${item.quantity}", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp))
                IconButton(onClick = { onQuantityChange(item.quantity + 1) }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun CheckoutScreen(
    viewModel: PixoraViewModel
) {
    val cart by viewModel.cart.collectAsState()
    val subtotal = cart.sumOf { it.product.price * it.quantity }
    val tax = subtotal * 0.18
    val discount = if (subtotal > 20000) 2500.0 else 0.0
    val total = subtotal + tax - discount

    var name by remember { mutableStateOf("Priya Verma") }
    var email by remember { mutableStateOf("rahul.priya@weddings.com") }
    var phone by remember { mutableStateOf("+91 98201 11223") }
    var address by remember { mutableStateOf("Villa 4, Palm Jumeirah, Dubai / Oberoi Udaivilas") }
    var selectedPayment by remember { mutableStateOf("Razorpay / Credit Card") }

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
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Checkout & Payment",
                style = MaterialTheme.typography.headlineSmall,
                fontFamily = FontFamily.Serif,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Client & Delivery Info
        Card(colors = CardDefaults.cardColors(containerColor = Charcoal800)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("SHIPPING & CLIENT DETAILS", style = MaterialTheme.typography.labelSmall, color = GoldAccent, letterSpacing = 1.sp)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth().testTag("checkout_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email for Receipt & Tracking") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth().testTag("checkout_email_input"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent)
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth().testTag("checkout_phone_input"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent)
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Delivery Address for Prints & Albums") },
                    modifier = Modifier.fillMaxWidth().testTag("checkout_address_input"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Payment Method
        Card(colors = CardDefaults.cardColors(containerColor = Charcoal800)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("PAYMENT GATEWAY", style = MaterialTheme.typography.labelSmall, color = GoldAccent, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))

                listOf("Razorpay / Credit Card", "UPI / Google Pay", "Stripe / Apple Pay", "Bank Wire / NEFT").forEach { method ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPayment = method }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPayment == method,
                            onClick = { selectedPayment = method },
                            colors = RadioButtonDefaults.colors(selectedColor = GoldAccent)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(method, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.processCheckout(name, email, phone, selectedPayment)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("checkout_pay_button"),
            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Pay Securely ₹${total.toInt()}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun PaymentSuccessScreen(
    orderId: String,
    viewModel: PixoraViewModel
) {
    val order = viewModel.lastCompletedOrder.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Charcoal900)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(LuxuryGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Payment Successful",
            style = MaterialTheme.typography.headlineMedium,
            fontFamily = FontFamily.Serif,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Thank you! Your order has been placed and transmitted to the studio print lab.",
            style = MaterialTheme.typography.bodyMedium,
            color = Charcoal300,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(colors = CardDefaults.cardColors(containerColor = Charcoal800), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Order Reference", color = Charcoal300)
                    Text(orderId, fontWeight = FontWeight.Bold, color = GoldLight)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Amount Paid", color = Charcoal300)
                    Text("₹${order?.totalAmount?.toInt() ?: 73942}", fontWeight = FontWeight.Bold, color = Color.White)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Transaction Ref", color = Charcoal300)
                    Text(order?.transactionRef ?: "TXN_PRX_9921", color = Charcoal300, style = MaterialTheme.typography.bodySmall)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Status", color = Charcoal300)
                    Text("PAID & PROCESSING", color = LuxuryGreen, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.navigateTo(ScreenRoute.ClientGalleryHome("gal_1")) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Charcoal900),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Back to Gallery", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { viewModel.navigateTo(ScreenRoute.Orders) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("View Studio Orders Dashboard", color = GoldLight)
        }
    }
}
