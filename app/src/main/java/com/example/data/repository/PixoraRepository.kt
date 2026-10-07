package com.example.data.repository

import com.example.data.model.*
import com.example.util.SecurityUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class PixoraRepository {

    // Authentication & Users (No dummy users)
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Clean initial state (Zero dummy data)
    private val _clients = MutableStateFlow<List<Client>>(emptyList())
    val clients: StateFlow<List<Client>> = _clients.asStateFlow()

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    private val _galleries = MutableStateFlow<List<Gallery>>(emptyList())
    val galleries: StateFlow<List<Gallery>> = _galleries.asStateFlow()

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums.asStateFlow()

    private val _mediaItems = MutableStateFlow<List<MediaItem>>(emptyList())
    val mediaItems: StateFlow<List<MediaItem>> = _mediaItems.asStateFlow()

    private val _tasks = MutableStateFlow<List<StudioTask>>(emptyList())
    val tasks: StateFlow<List<StudioTask>> = _tasks.asStateFlow()

    private val _comments = MutableStateFlow<List<PhotoComment>>(emptyList())
    val comments: StateFlow<List<PhotoComment>> = _comments.asStateFlow()

    private val _storeProducts = MutableStateFlow<List<StoreProduct>>(emptyList())
    val storeProducts: StateFlow<List<StoreProduct>> = _storeProducts.asStateFlow()

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _invoices = MutableStateFlow<List<Invoice>>(emptyList())
    val invoices: StateFlow<List<Invoice>> = _invoices.asStateFlow()

    private val _teamMembers = MutableStateFlow<List<TeamMember>>(emptyList())
    val teamMembers: StateFlow<List<TeamMember>> = _teamMembers.asStateFlow()

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _messageThreads = MutableStateFlow<List<MessageThread>>(emptyList())
    val messageThreads: StateFlow<List<MessageThread>> = _messageThreads.asStateFlow()

    private val _uploadTasks = MutableStateFlow<List<UploadFileTask>>(emptyList())
    val uploadTasks: StateFlow<List<UploadFileTask>> = _uploadTasks.asStateFlow()

    private val _websiteConfig = MutableStateFlow(WebsiteConfig())
    val websiteConfig: StateFlow<WebsiteConfig> = _websiteConfig.asStateFlow()

    private val _studioSettings = MutableStateFlow(StudioSettings())
    val studioSettings: StateFlow<StudioSettings> = _studioSettings.asStateFlow()

    // -------------------------------------------------------------
    // AUTHENTICATION & USER MANAGEMENT
    // -------------------------------------------------------------

    fun registerUser(
        fullName: String,
        email: String,
        phone: String,
        username: String,
        passwordPlain: String,
        studioName: String,
        role: UserRole = UserRole.OWNER
    ): Result<User> {
        val cleanUsername = username.trim()
        val cleanEmail = email.trim().lowercase()

        if (_users.value.any { it.username.equals(cleanUsername, ignoreCase = true) }) {
            return Result.failure(Exception("Username already taken. Please choose another."))
        }
        if (_users.value.any { it.email.equals(cleanEmail, ignoreCase = true) }) {
            return Result.failure(Exception("Email already registered. Please sign in."))
        }

        val hashedPassword = SecurityUtils.hashPassword(passwordPlain)
        val newUser = User(
            fullName = fullName.trim(),
            email = cleanEmail,
            phone = phone.trim(),
            username = cleanUsername,
            passwordHash = hashedPassword,
            role = role,
            studioName = studioName.trim()
        )

        _users.value = _users.value + newUser
        _currentUser.value = newUser

        // Setup Studio Identity for this user
        _studioSettings.value = _studioSettings.value.copy(
            studioName = studioName.ifBlank { "${fullName}'s Studio" },
            photographerName = fullName,
            email = cleanEmail,
            phone = phone
        )

        _websiteConfig.value = _websiteConfig.value.copy(
            studioName = studioName.ifBlank { "${fullName}'s Studio" }
        )

        // Add as Owner in Team
        val ownerMember = TeamMember(
            id = newUser.id,
            name = fullName,
            email = cleanEmail,
            role = TeamRole.OWNER,
            status = "Active"
        )
        _teamMembers.value = listOf(ownerMember)

        addNotification(
            NotificationCategory.SYSTEM,
            "Welcome to PIXORA",
            "Studio account created successfully for $fullName."
        )

        return Result.success(newUser)
    }

    fun loginUser(identifier: String, passwordPlain: String): Result<User> {
        val cleanId = identifier.trim()
        val user = _users.value.find {
            it.username.equals(cleanId, ignoreCase = true) ||
                    it.email.equals(cleanId, ignoreCase = true) ||
                    it.phone == cleanId
        } ?: return Result.failure(Exception("No account found with this identifier."))

        val inputHash = SecurityUtils.hashPassword(passwordPlain)
        if (user.passwordHash != inputHash) {
            return Result.failure(Exception("Incorrect password. Please try again."))
        }

        _currentUser.value = user
        return Result.success(user)
    }

    fun loginWithGoogle(displayName: String, email: String): User {
        val cleanEmail = email.trim().lowercase()
        val existing = _users.value.find { it.email.equals(cleanEmail, ignoreCase = true) }
        if (existing != null) {
            _currentUser.value = existing
            return existing
        }

        // Auto-provision Google-authenticated user
        val newUser = User(
            fullName = displayName.ifBlank { "Google User" },
            email = cleanEmail,
            phone = "",
            username = cleanEmail.substringBefore("@") + "_" + UUID.randomUUID().toString().take(4),
            passwordHash = SecurityUtils.hashPassword(UUID.randomUUID().toString()),
            role = UserRole.OWNER,
            studioName = "${displayName}'s Studio"
        )
        _users.value = _users.value + newUser
        _currentUser.value = newUser

        _studioSettings.value = _studioSettings.value.copy(
            studioName = newUser.studioName,
            photographerName = newUser.fullName,
            email = newUser.email
        )

        _teamMembers.value = listOf(
            TeamMember(
                id = newUser.id,
                name = newUser.fullName,
                email = newUser.email,
                role = TeamRole.OWNER
            )
        )

        return newUser
    }

    fun logout() {
        _currentUser.value = null
    }

    // -------------------------------------------------------------
    // CLIENTS: ADD, EDIT, DELETE & UPDATE EVERYWHERE
    // -------------------------------------------------------------

    fun createClient(
        name: String,
        email: String,
        phone: String,
        address: String,
        eventType: String,
        notes: String
    ): Client {
        val client = Client(
            name = name.trim(),
            email = email.trim(),
            phone = phone.trim(),
            address = address.trim(),
            eventType = eventType.trim(),
            notes = notes.trim()
        )
        _clients.value = listOf(client) + _clients.value

        addNotification(
            NotificationCategory.CLIENTS,
            "New Client Added",
            "Added ${client.name} to studio client directory."
        )
        return client
    }

    fun updateClient(updated: Client) {
        // 1. Update client record
        _clients.value = _clients.value.map { if (it.id == updated.id) updated else it }

        // 2. UPDATE EVERYWHERE: cascade client name & contact to related entities
        _projects.value = _projects.value.map { proj ->
            if (proj.clientId == updated.id) proj.copy(clientName = updated.name) else proj
        }
        _galleries.value = _galleries.value.map { gal ->
            if (gal.clientId == updated.id) gal.copy(clientName = updated.name) else gal
        }
        _invoices.value = _invoices.value.map { inv ->
            if (inv.clientId == updated.id) inv.copy(clientName = updated.name, clientEmail = updated.email) else inv
        }
        _orders.value = _orders.value.map { ord ->
            if (ord.clientId == updated.id) ord.copy(clientName = updated.name, clientEmail = updated.email, clientPhone = updated.phone) else ord
        }
        _tasks.value = _tasks.value.map { task ->
            if (task.clientId == updated.id) task.copy(clientName = updated.name) else task
        }
        _messageThreads.value = _messageThreads.value.map { th ->
            if (th.clientId == updated.id) th.copy(clientName = updated.name) else th
        }
    }

    fun deleteClient(clientId: String) {
        _clients.value = _clients.value.filter { it.id != clientId }
        // Remove or unlink related records
        _projects.value = _projects.value.filter { it.clientId != clientId }
        _galleries.value = _galleries.value.filter { it.clientId != clientId }
        _invoices.value = _invoices.value.filter { it.clientId != clientId }
        _tasks.value = _tasks.value.filter { it.clientId != clientId }
        _messageThreads.value = _messageThreads.value.filter { it.clientId != clientId }
    }

    // -------------------------------------------------------------
    // PROJECTS / SHOOTS: ADD, EDIT, DELETE & UPDATE EVERYWHERE
    // -------------------------------------------------------------

    fun createProject(
        name: String,
        client: Client,
        eventType: String,
        eventDate: String,
        location: String,
        budget: String,
        description: String
    ): Project {
        val project = Project(
            clientId = client.id,
            clientName = client.name,
            name = name.trim(),
            eventType = eventType.trim(),
            eventDate = eventDate.trim(),
            location = location.trim(),
            budget = budget.trim(),
            description = description.trim(),
            progressPercent = 0
        )
        _projects.value = listOf(project) + _projects.value

        // Increment client active project count
        _clients.value = _clients.value.map {
            if (it.id == client.id) it.copy(activeProjectsCount = it.activeProjectsCount + 1) else it
        }

        addNotification(
            NotificationCategory.GALLERIES,
            "Project Created",
            "Project '${project.name}' initialized for ${client.name}."
        )
        return project
    }

    fun updateProject(updated: Project) {
        // 1. Update Project
        _projects.value = _projects.value.map { if (it.id == updated.id) updated else it }

        // 2. UPDATE EVERYWHERE: cascade project name in galleries, invoices & tasks
        _galleries.value = _galleries.value.map { gal ->
            if (gal.projectId == updated.id) gal.copy(eventDate = updated.eventDate) else gal
        }
        _invoices.value = _invoices.value.map { inv ->
            if (inv.projectName == _projects.value.find { it.id == updated.id }?.name) inv.copy(projectName = updated.name) else inv
        }
        _tasks.value = _tasks.value.map { task ->
            if (task.projectId == updated.id) task.copy(projectName = updated.name) else task
        }
    }

    fun deleteProject(projectId: String) {
        val proj = _projects.value.find { it.id == projectId }
        _projects.value = _projects.value.filter { it.id != projectId }
        // Decrement client count
        if (proj != null) {
            _clients.value = _clients.value.map {
                if (it.id == proj.clientId) it.copy(activeProjectsCount = (it.activeProjectsCount - 1).coerceAtLeast(0)) else it
            }
        }
        // Remove associated galleries and tasks
        _galleries.value = _galleries.value.filter { it.projectId != projectId }
        _tasks.value = _tasks.value.filter { it.projectId != projectId }
    }

    // -------------------------------------------------------------
    // GALLERIES: ADD, EDIT, DELETE & UPDATE EVERYWHERE
    // -------------------------------------------------------------

    fun createGallery(
        title: String,
        client: Client,
        project: Project,
        eventDate: String,
        description: String,
        privacy: GalleryPrivacy,
        allowDownloads: Boolean,
        watermarkEnabled: Boolean,
        coverImageUrl: String = ""
    ): Gallery {
        val gallery = Gallery(
            projectId = project.id,
            clientId = client.id,
            clientName = client.name,
            title = title.trim(),
            eventDate = eventDate.trim(),
            coverImageUrl = coverImageUrl,
            description = description.trim(),
            privacy = privacy,
            allowDownloads = allowDownloads,
            watermarkEnabled = watermarkEnabled
        )
        _galleries.value = listOf(gallery) + _galleries.value

        // Update gallery count in project & client
        _projects.value = _projects.value.map {
            if (it.id == project.id) it.copy(galleryCount = it.galleryCount + 1) else it
        }
        _clients.value = _clients.value.map {
            if (it.id == client.id) it.copy(galleriesCount = it.galleriesCount + 1) else it
        }

        _studioSettings.value = _studioSettings.value.copy(
            galleriesCreated = _studioSettings.value.galleriesCreated + 1
        )

        return gallery
    }

    fun updateGallery(updated: Gallery) {
        _galleries.value = _galleries.value.map { if (it.id == updated.id) updated else it }
    }

    fun deleteGallery(galleryId: String) {
        val gal = _galleries.value.find { it.id == galleryId }
        _galleries.value = _galleries.value.filter { it.id != galleryId }
        _mediaItems.value = _mediaItems.value.filter { it.galleryId != galleryId }
        _albums.value = _albums.value.filter { it.galleryId != galleryId }

        if (gal != null) {
            _projects.value = _projects.value.map {
                if (it.id == gal.projectId) it.copy(galleryCount = (it.galleryCount - 1).coerceAtLeast(0)) else it
            }
            _clients.value = _clients.value.map {
                if (it.id == gal.clientId) it.copy(galleriesCount = (it.galleriesCount - 1).coerceAtLeast(0)) else it
            }
        }
    }

    // -------------------------------------------------------------
    // ALBUMS & MEDIA
    // -------------------------------------------------------------

    fun createAlbum(galleryId: String, name: String, coverImageUrl: String = ""): Album {
        val album = Album(galleryId = galleryId, name = name.trim(), coverImageUrl = coverImageUrl)
        _albums.value = _albums.value + album
        return album
    }

    fun deleteAlbum(albumId: String) {
        _albums.value = _albums.value.filter { it.id != albumId }
        _mediaItems.value = _mediaItems.value.filter { it.albumId != albumId }
    }

    fun addMediaItem(item: MediaItem) {
        _mediaItems.value = listOf(item) + _mediaItems.value
        // Update gallery photo/video count
        _galleries.value = _galleries.value.map { gal ->
            if (gal.id == item.galleryId) {
                if (item.isVideo) gal.copy(videoCount = gal.videoCount + 1)
                else gal.copy(photoCount = gal.photoCount + 1)
            } else gal
        }
    }

    fun deleteMediaItem(mediaId: String) {
        val item = _mediaItems.value.find { it.id == mediaId }
        _mediaItems.value = _mediaItems.value.filter { it.id != mediaId }
        if (item != null) {
            _galleries.value = _galleries.value.map { gal ->
                if (gal.id == item.galleryId) {
                    if (item.isVideo) gal.copy(videoCount = (gal.videoCount - 1).coerceAtLeast(0))
                    else gal.copy(photoCount = (gal.photoCount - 1).coerceAtLeast(0))
                } else gal
            }
        }
    }

    // -------------------------------------------------------------
    // TASKS / SHOOTS MANAGEMENT
    // -------------------------------------------------------------

    fun createTask(
        title: String,
        description: String,
        client: Client?,
        project: Project?,
        assignedTo: TeamMember?,
        dueDate: String,
        priority: String
    ): StudioTask {
        val task = StudioTask(
            title = title.trim(),
            description = description.trim(),
            clientId = client?.id ?: "",
            clientName = client?.name ?: "",
            projectId = project?.id ?: "",
            projectName = project?.name ?: "",
            assignedToName = assignedTo?.name ?: "",
            assignedToId = assignedTo?.id ?: "",
            dueDate = dueDate.trim(),
            priority = priority
        )
        _tasks.value = listOf(task) + _tasks.value
        return task
    }

    fun updateTask(updated: StudioTask) {
        _tasks.value = _tasks.value.map { if (it.id == updated.id) updated else it }
    }

    fun deleteTask(taskId: String) {
        _tasks.value = _tasks.value.filter { it.id != taskId }
    }

    // -------------------------------------------------------------
    // INVOICES & QUOTATIONS: ADD, EDIT, DELETE & UPDATE EVERYWHERE
    // -------------------------------------------------------------

    fun createInvoice(
        clientName: String,
        clientEmail: String,
        projectName: String,
        description: String,
        amount: Double,
        dueDate: String = "",
        status: InvoiceStatus = InvoiceStatus.SENT
    ): Invoice {
        val tax = amount * 0.18
        val client = _clients.value.find { it.name.equals(clientName.trim(), ignoreCase = true) }
        val invoice = Invoice(
            clientId = client?.id ?: "",
            clientName = clientName.trim(),
            clientEmail = clientEmail.trim(),
            projectName = projectName.trim(),
            itemsDescription = description.trim(),
            subtotal = amount,
            taxAmount = tax,
            discountAmount = 0.0,
            totalAmount = amount + tax,
            status = status,
            dueDate = dueDate.ifBlank { "Within 10 days" }
        )
        _invoices.value = listOf(invoice) + _invoices.value

        addNotification(
            NotificationCategory.PAYMENTS,
            "Invoice Generated (${invoice.id})",
            "Created invoice for ₹${invoice.totalAmount.toInt()} addressed to $clientName."
        )
        return invoice
    }

    fun updateInvoice(updated: Invoice) {
        _invoices.value = _invoices.value.map { if (it.id == updated.id) updated else it }
    }

    fun deleteInvoice(invoiceId: String) {
        _invoices.value = _invoices.value.filter { it.id != invoiceId }
    }

    // -------------------------------------------------------------
    // ORDERS & PAYMENTS
    // -------------------------------------------------------------

    fun addToCart(product: StoreProduct, variant: String, quantity: Int = 1) {
        val current = _cart.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.product.id == product.id && it.variant == variant }
        if (existingIndex != -1) {
            val existing = current[existingIndex]
            current[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
        } else {
            current.add(CartItem(product = product, variant = variant, quantity = quantity))
        }
        _cart.value = current
    }

    fun updateCartItemQuantity(cartItemId: String, quantity: Int) {
        if (quantity <= 0) {
            _cart.value = _cart.value.filter { it.id != cartItemId }
        } else {
            _cart.value = _cart.value.map { if (it.id == cartItemId) it.copy(quantity = quantity) else it }
        }
    }

    fun clearCart() {
        _cart.value = emptyList()
    }

    fun checkoutOrder(clientName: String, clientEmail: String, clientPhone: String, paymentMethod: String): Order {
        val items = _cart.value
        val subtotal = items.sumOf { it.product.price * it.quantity }
        val tax = subtotal * 0.18
        val discount = 0.0
        val total = subtotal + tax - discount

        val client = _clients.value.find { it.name.equals(clientName.trim(), ignoreCase = true) }

        val newOrder = Order(
            clientId = client?.id ?: "",
            clientName = clientName.trim(),
            clientEmail = clientEmail.trim(),
            clientPhone = clientPhone.trim(),
            items = items,
            subtotal = subtotal,
            tax = tax,
            discount = discount,
            totalAmount = total,
            status = OrderStatus.PAID,
            paymentMethod = paymentMethod,
            transactionRef = "TXN_PRX_" + UUID.randomUUID().toString().take(8).uppercase()
        )
        _orders.value = listOf(newOrder) + _orders.value
        clearCart()

        addNotification(
            NotificationCategory.ORDERS,
            "Order Confirmed (${newOrder.id})",
            "Received order of ₹${total.toInt()} from $clientName."
        )

        return newOrder
    }

    fun updateOrderStatus(orderId: String, status: OrderStatus) {
        _orders.value = _orders.value.map { if (it.id == orderId) it.copy(status = status) else it }
    }

    fun deleteOrder(orderId: String) {
        _orders.value = _orders.value.filter { it.id != orderId }
    }

    // -------------------------------------------------------------
    // TEAM & EMPLOYEES: ADD, EDIT, DELETE & UPDATE EVERYWHERE
    // -------------------------------------------------------------

    fun addTeamMember(name: String, email: String, role: TeamRole): TeamMember {
        val member = TeamMember(name = name.trim(), email = email.trim(), role = role)
        _teamMembers.value = _teamMembers.value + member
        return member
    }

    fun updateTeamMember(updated: TeamMember) {
        val old = _teamMembers.value.find { it.id == updated.id }
        _teamMembers.value = _teamMembers.value.map { if (it.id == updated.id) updated else it }

        // UPDATE EVERYWHERE: cascade employee name in assigned tasks
        if (old != null && old.name != updated.name) {
            _tasks.value = _tasks.value.map { task ->
                if (task.assignedToId == updated.id || task.assignedToName == old.name) {
                    task.copy(assignedToName = updated.name)
                } else task
            }
        }
    }

    fun deleteTeamMember(memberId: String) {
        _teamMembers.value = _teamMembers.value.filter { it.id != memberId }
        // Unassign in tasks
        _tasks.value = _tasks.value.map {
            if (it.assignedToId == memberId) it.copy(assignedToId = "", assignedToName = "Unassigned") else it
        }
    }

    // -------------------------------------------------------------
    // STORE PRODUCTS: ADD, EDIT, DELETE
    // -------------------------------------------------------------

    fun createStoreProduct(name: String, category: String, price: Double, description: String, variants: List<String>): StoreProduct {
        val prod = StoreProduct(name = name.trim(), category = category.trim(), price = price, description = description.trim(), variants = variants)
        _storeProducts.value = _storeProducts.value + prod
        return prod
    }

    fun updateStoreProduct(updated: StoreProduct) {
        _storeProducts.value = _storeProducts.value.map { if (it.id == updated.id) updated else it }
    }

    fun deleteStoreProduct(productId: String) {
        _storeProducts.value = _storeProducts.value.filter { it.id != productId }
    }

    // -------------------------------------------------------------
    // PROOFING, FAVORITES & COMMENTS
    // -------------------------------------------------------------

    fun toggleFavorite(mediaId: String) {
        _mediaItems.value = _mediaItems.value.map { item ->
            if (item.id == mediaId) item.copy(isFavorite = !item.isFavorite) else item
        }
    }

    fun toggleSelection(mediaId: String) {
        _mediaItems.value = _mediaItems.value.map { item ->
            if (item.id == mediaId) item.copy(isSelectedForProofing = !item.isSelectedForProofing) else item
        }
    }

    fun submitSelection(galleryId: String) {
        _galleries.value = _galleries.value.map { gal ->
            if (gal.id == galleryId) gal.copy(isSelectionSubmitted = true) else gal
        }
        val gal = _galleries.value.find { it.id == galleryId }
        addNotification(
            NotificationCategory.PROOFING,
            "Proofing Selection Submitted",
            "Client submitted proofing selections for gallery '${gal?.title ?: ""}'.",
            relatedId = galleryId
        )
    }

    fun addComment(mediaId: String, authorName: String, authorRole: String, messageText: String) {
        val comment = PhotoComment(
            mediaId = mediaId,
            authorName = authorName,
            authorRole = authorRole,
            message = messageText
        )
        _comments.value = listOf(comment) + _comments.value
        _mediaItems.value = _mediaItems.value.map {
            if (it.id == mediaId) it.copy(commentCount = it.commentCount + 1) else it
        }
    }

    fun getFavoriteMedia(): List<MediaItem> = _mediaItems.value.filter { it.isFavorite }
    fun getSelectedMedia(): List<MediaItem> = _mediaItems.value.filter { it.isSelectedForProofing }

    // -------------------------------------------------------------
    // NOTIFICATIONS & CHAT
    // -------------------------------------------------------------

    fun addNotification(category: NotificationCategory, title: String, message: String, relatedId: String = "") {
        val notif = NotificationItem(
            category = category,
            title = title,
            message = message,
            relatedEntityId = relatedId
        )
        _notifications.value = listOf(notif) + _notifications.value
    }

    fun markNotificationAsRead(id: String) {
        _notifications.value = _notifications.value.map { if (it.id == id) it.copy(isRead = true) else it }
    }

    fun deleteNotification(id: String) {
        _notifications.value = _notifications.value.filter { it.id != id }
    }

    fun sendChatMessage(threadId: String, text: String, senderName: String) {
        _messageThreads.value = _messageThreads.value.map { thread ->
            if (thread.id == threadId) {
                val newMsg = ChatMessage(
                    senderName = senderName,
                    isFromPhotographer = true,
                    text = text
                )
                thread.copy(
                    lastMessage = text,
                    lastMessageTime = "Just now",
                    messages = thread.messages + newMsg
                )
            } else thread
        }
    }

    fun createMessageThread(clientId: String, clientName: String, projectTitle: String): MessageThread {
        val thread = MessageThread(
            clientId = clientId,
            clientName = clientName,
            projectTitle = projectTitle,
            lastMessage = "Conversation started",
            lastMessageTime = "Just now"
        )
        _messageThreads.value = listOf(thread) + _messageThreads.value
        return thread
    }

    // -------------------------------------------------------------
    // UPLOADS & SETTINGS
    // -------------------------------------------------------------

    fun addUploadTask(filename: String, type: String, sizeBytes: Long) {
        val task = UploadFileTask(
            filename = filename,
            fileType = type,
            sizeBytes = sizeBytes,
            progress = 0.25f,
            speedMbps = "18.5 MB/s",
            remainingSecs = 8,
            status = UploadStatus.UPLOADING
        )
        _uploadTasks.value = listOf(task) + _uploadTasks.value
    }

    fun removeUploadTask(taskId: String) {
        _uploadTasks.value = _uploadTasks.value.filter { it.id != taskId }
    }

    fun updateWebsiteConfig(config: WebsiteConfig) {
        _websiteConfig.value = config
    }

    fun updateStudioSettings(settings: StudioSettings) {
        _studioSettings.value = settings
    }
}
