package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.PixoraRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ScreenRoute {
    // Auth & Onboarding
    data object Splash : ScreenRoute()
    data object Welcome : ScreenRoute()
    data object SignUp : ScreenRoute()
    data object OtpVerify : ScreenRoute()
    data object Login : ScreenRoute()
    data object ForgotPassword : ScreenRoute()
    data object Onboarding : ScreenRoute()

    // Photographer Management
    data object Dashboard : ScreenRoute()
    data object Clients : ScreenRoute()
    data class ClientProfile(val clientId: String) : ScreenRoute()
    data object Projects : ScreenRoute()
    data class ProjectDashboard(val projectId: String) : ScreenRoute()
    data object Tasks : ScreenRoute()
    data object Galleries : ScreenRoute()
    data class GalleryEditor(val galleryId: String) : ScreenRoute()
    data class GallerySettings(val galleryId: String) : ScreenRoute()
    data object QuickUpload : ScreenRoute()
    data object UploadCenter : ScreenRoute()
    data object Orders : ScreenRoute()
    data class OrderDetail(val orderId: String) : ScreenRoute()
    data object Invoices : ScreenRoute()
    data object Storage : ScreenRoute()
    data object Subscription : ScreenRoute()
    data object WebsiteBuilder : ScreenRoute()
    data object Analytics : ScreenRoute()
    data object Team : ScreenRoute()
    data object Notifications : ScreenRoute()
    data object Messages : ScreenRoute()
    data object Settings : ScreenRoute()
    data object AdminPortal : ScreenRoute()

    // Client-Facing Gallery
    data class ClientLanding(val galleryId: String) : ScreenRoute()
    data class ClientGalleryHome(val galleryId: String) : ScreenRoute()
    data class ClientPhotoGrid(val galleryId: String, val albumId: String? = null) : ScreenRoute()
    data class ClientPhotoViewer(val photoId: String) : ScreenRoute()
    data class ClientFavorites(val galleryId: String) : ScreenRoute()
    data class ClientProofing(val galleryId: String) : ScreenRoute()
    data class ClientVideoGallery(val galleryId: String) : ScreenRoute()
    data class ClientDownloadCenter(val galleryId: String) : ScreenRoute()
    data class ClientStore(val galleryId: String) : ScreenRoute()
    data class ClientProductDetail(val productId: String) : ScreenRoute()
    data object ClientCart : ScreenRoute()
    data object ClientCheckout : ScreenRoute()
    data class ClientPaymentSuccess(val orderId: String) : ScreenRoute()
}

class PixoraViewModel(
    val repository: PixoraRepository = PixoraRepository()
) : ViewModel() {

    private val navBackStack = mutableListOf<ScreenRoute>()

    private val _currentRoute = MutableStateFlow<ScreenRoute>(ScreenRoute.Splash)
    val currentRoute: StateFlow<ScreenRoute> = _currentRoute.asStateFlow()

    // Authentication & Current User
    val currentUser = repository.currentUser
    val users = repository.users

    // Repositories States
    val clients = repository.clients
    val projects = repository.projects
    val galleries = repository.galleries
    val albums = repository.albums
    val mediaItems = repository.mediaItems
    val tasks = repository.tasks
    val comments = repository.comments
    val storeProducts = repository.storeProducts
    val cart = repository.cart
    val orders = repository.orders
    val invoices = repository.invoices
    val teamMembers = repository.teamMembers
    val notifications = repository.notifications
    val messageThreads = repository.messageThreads
    val uploadTasks = repository.uploadTasks
    val websiteConfig = repository.websiteConfig
    val studioSettings = repository.studioSettings

    // Active Selection State
    val selectedMediaIdForDetail = MutableStateFlow<String?>(null)
    val selectedAlbumFilter = MutableStateFlow<String?>("All")
    val selectedNotificationCategory = MutableStateFlow(NotificationCategory.ALL)
    val selectedOrderFilter = MutableStateFlow<OrderStatus?>(null)
    val activeChatThreadId = MutableStateFlow<String?>(null)

    // UI Feedback & Dialogs
    val toastMessage = MutableStateFlow<String?>(null)

    // Creation Dialogs
    val isCreateClientOpen = MutableStateFlow(false)
    val isCreateProjectOpen = MutableStateFlow(false)
    val isCreateGalleryOpen = MutableStateFlow(false)
    val isCreateInvoiceOpen = MutableStateFlow(false)
    val isCreateTaskOpen = MutableStateFlow(false)
    val isCreateTeamMemberOpen = MutableStateFlow(false)

    // Edit Item States
    val editingClient = MutableStateFlow<Client?>(null)
    val editingProject = MutableStateFlow<Project?>(null)
    val editingGallery = MutableStateFlow<Gallery?>(null)
    val editingInvoice = MutableStateFlow<Invoice?>(null)
    val editingTask = MutableStateFlow<StudioTask?>(null)
    val editingTeamMember = MutableStateFlow<TeamMember?>(null)

    // Delete Item States (with confirmation popups)
    val deletingClient = MutableStateFlow<Client?>(null)
    val deletingProject = MutableStateFlow<Project?>(null)
    val deletingGallery = MutableStateFlow<Gallery?>(null)
    val deletingInvoice = MutableStateFlow<Invoice?>(null)
    val deletingTask = MutableStateFlow<StudioTask?>(null)
    val deletingTeamMember = MutableStateFlow<TeamMember?>(null)
    val deletingOrder = MutableStateFlow<Order?>(null)
    val deletingMediaItem = MutableStateFlow<MediaItem?>(null)

    val isExifSheetOpen = MutableStateFlow(false)
    val isCommentsSheetOpen = MutableStateFlow(false)

    // Last completed order
    val lastCompletedOrder = MutableStateFlow<Order?>(null)

    init {
        // Auto transition from Splash directly to Login interface
        viewModelScope.launch {
            delay(1200)
            if (_currentRoute.value is ScreenRoute.Splash) {
                if (repository.currentUser.value != null) {
                    navigateTo(ScreenRoute.Dashboard, addToBackStack = false)
                } else {
                    navigateTo(ScreenRoute.Login, addToBackStack = false)
                }
            }
        }
    }

    fun navigateTo(route: ScreenRoute, addToBackStack: Boolean = true) {
        if (addToBackStack && _currentRoute.value !is ScreenRoute.Splash) {
            navBackStack.add(_currentRoute.value)
        }
        _currentRoute.value = route
    }

    fun navigateBack(): Boolean {
        if (navBackStack.isNotEmpty()) {
            val previous = navBackStack.removeAt(navBackStack.lastIndex)
            _currentRoute.value = previous
            return true
        }
        return false
    }

    fun showToast(message: String) {
        viewModelScope.launch {
            toastMessage.value = message
            delay(3000)
            if (toastMessage.value == message) {
                toastMessage.value = null
            }
        }
    }

    // -------------------------------------------------------------
    // AUTHENTICATION
    // -------------------------------------------------------------

    fun login(identifier: String, passwordPlain: String): Boolean {
        val result = repository.loginUser(identifier, passwordPlain)
        return if (result.isSuccess) {
            showToast("Login Successful")
            navigateTo(ScreenRoute.Dashboard, addToBackStack = false)
            true
        } else {
            showToast(result.exceptionOrNull()?.message ?: "Login Failed")
            false
        }
    }

    fun register(
        fullName: String,
        email: String,
        phone: String,
        username: String,
        passwordPlain: String,
        studioName: String,
        role: UserRole = UserRole.OWNER
    ): Boolean {
        val result = repository.registerUser(fullName, email, phone, username, passwordPlain, studioName, role)
        return if (result.isSuccess) {
            showToast("User Created Successfully")
            navigateTo(ScreenRoute.Dashboard, addToBackStack = false)
            true
        } else {
            showToast(result.exceptionOrNull()?.message ?: "Registration Failed")
            false
        }
    }

    fun loginWithGoogle(displayName: String, email: String) {
        val user = repository.loginWithGoogle(displayName, email)
        showToast("Signed in as ${user.fullName}")
        navigateTo(ScreenRoute.Dashboard, addToBackStack = false)
    }

    fun logout() {
        repository.logout()
        navBackStack.clear()
        showToast("Logged out successfully")
        navigateTo(ScreenRoute.Login, addToBackStack = false)
    }

    // -------------------------------------------------------------
    // CLIENT CRUD & UPDATE EVERYWHERE
    // -------------------------------------------------------------

    fun createClient(name: String, email: String, phone: String, address: String, eventType: String, notes: String) {
        val client = repository.createClient(name, email, phone, address, eventType, notes)
        isCreateClientOpen.value = false
        showToast("Client '${client.name}' Added Successfully")
    }

    fun updateClient(client: Client) {
        repository.updateClient(client)
        editingClient.value = null
        showToast("Client '${client.name}' Updated Successfully")
    }

    fun confirmDeleteClient(client: Client) {
        repository.deleteClient(client.id)
        deletingClient.value = null
        showToast("Client Deleted Successfully")
        if (_currentRoute.value is ScreenRoute.ClientProfile) {
            navigateTo(ScreenRoute.Clients)
        }
    }

    // -------------------------------------------------------------
    // PROJECT CRUD & UPDATE EVERYWHERE
    // -------------------------------------------------------------

    fun createProject(name: String, client: Client, eventType: String, eventDate: String, location: String, budget: String, description: String) {
        val project = repository.createProject(name, client, eventType, eventDate, location, budget, description)
        isCreateProjectOpen.value = false
        showToast("Project '${project.name}' Created Successfully")
        navigateTo(ScreenRoute.ProjectDashboard(project.id))
    }

    fun updateProject(project: Project) {
        repository.updateProject(project)
        editingProject.value = null
        showToast("Project '${project.name}' Updated Successfully")
    }

    fun confirmDeleteProject(project: Project) {
        repository.deleteProject(project.id)
        deletingProject.value = null
        showToast("Project Deleted Successfully")
        if (_currentRoute.value is ScreenRoute.ProjectDashboard) {
            navigateTo(ScreenRoute.Projects)
        }
    }

    // -------------------------------------------------------------
    // GALLERY CRUD
    // -------------------------------------------------------------

    fun createGallery(
        title: String,
        client: Client,
        project: Project,
        eventDate: String,
        description: String,
        privacy: GalleryPrivacy,
        allowDownloads: Boolean,
        watermarkEnabled: Boolean
    ) {
        val gallery = repository.createGallery(
            title, client, project, eventDate, description, privacy, allowDownloads, watermarkEnabled
        )
        isCreateGalleryOpen.value = false
        showToast("Gallery '${gallery.title}' Created Successfully")
        navigateTo(ScreenRoute.GalleryEditor(gallery.id))
    }

    fun updateGallery(gallery: Gallery) {
        repository.updateGallery(gallery)
        editingGallery.value = null
        showToast("Gallery Updated Successfully")
    }

    fun confirmDeleteGallery(gallery: Gallery) {
        repository.deleteGallery(gallery.id)
        deletingGallery.value = null
        showToast("Gallery Deleted Successfully")
        if (_currentRoute.value is ScreenRoute.GalleryEditor || _currentRoute.value is ScreenRoute.GallerySettings) {
            navigateTo(ScreenRoute.Galleries)
        }
    }

    // -------------------------------------------------------------
    // TASK / SHOOT CRUD
    // -------------------------------------------------------------

    fun createTask(
        title: String,
        description: String,
        client: Client?,
        project: Project?,
        assignedTo: TeamMember?,
        dueDate: String,
        priority: String
    ) {
        val task = repository.createTask(title, description, client, project, assignedTo, dueDate, priority)
        isCreateTaskOpen.value = false
        showToast("Task '${task.title}' Created Successfully")
    }

    fun updateTask(task: StudioTask) {
        repository.updateTask(task)
        editingTask.value = null
        showToast("Task '${task.title}' Updated Successfully")
    }

    fun confirmDeleteTask(task: StudioTask) {
        repository.deleteTask(task.id)
        deletingTask.value = null
        showToast("Task Deleted Successfully")
    }

    // -------------------------------------------------------------
    // INVOICE CRUD
    // -------------------------------------------------------------

    fun createInvoice(clientName: String, clientEmail: String, projectName: String, description: String, amount: Double, dueDate: String = "") {
        val inv = repository.createInvoice(clientName, clientEmail, projectName, description, amount, dueDate)
        isCreateInvoiceOpen.value = false
        showToast("Invoice ${inv.id} Generated Successfully")
    }

    fun updateInvoice(invoice: Invoice) {
        repository.updateInvoice(invoice)
        editingInvoice.value = null
        showToast("Invoice ${invoice.id} Updated Successfully")
    }

    fun confirmDeleteInvoice(invoice: Invoice) {
        repository.deleteInvoice(invoice.id)
        deletingInvoice.value = null
        showToast("Invoice Deleted Successfully")
    }

    // -------------------------------------------------------------
    // TEAM MEMBER CRUD
    // -------------------------------------------------------------

    fun addTeamMember(name: String, email: String, role: TeamRole) {
        val member = repository.addTeamMember(name, email, role)
        isCreateTeamMemberOpen.value = false
        showToast("Team Member '${member.name}' Added Successfully")
    }

    fun updateTeamMember(member: TeamMember) {
        repository.updateTeamMember(member)
        editingTeamMember.value = null
        showToast("Team Member '${member.name}' Updated Successfully")
    }

    fun confirmDeleteTeamMember(member: TeamMember) {
        repository.deleteTeamMember(member.id)
        deletingTeamMember.value = null
        showToast("Team Member Removed Successfully")
    }

    // -------------------------------------------------------------
    // ORDERS & MEDIA
    // -------------------------------------------------------------

    fun updateOrderStatus(orderId: String, status: OrderStatus) {
        repository.updateOrderStatus(orderId, status)
        showToast("Order Status Updated to ${status.name}")
    }

    fun confirmDeleteOrder(order: Order) {
        repository.deleteOrder(order.id)
        deletingOrder.value = null
        showToast("Order Deleted Successfully")
        if (_currentRoute.value is ScreenRoute.OrderDetail) {
            navigateTo(ScreenRoute.Orders)
        }
    }

    fun confirmDeleteMedia(media: MediaItem) {
        repository.deleteMediaItem(media.id)
        deletingMediaItem.value = null
        showToast("Photo Deleted Successfully")
    }

    // Photo Interaction
    fun toggleFavorite(photoId: String) {
        repository.toggleFavorite(photoId)
        val isFav = repository.mediaItems.value.find { it.id == photoId }?.isFavorite == true
        showToast(if (isFav) "Added to Favorites" else "Removed from Favorites")
    }

    fun toggleSelection(photoId: String) {
        repository.toggleSelection(photoId)
        val isSelected = repository.mediaItems.value.find { it.id == photoId }?.isSelectedForProofing == true
        showToast(if (isSelected) "Selected for Proofing" else "Deselected from Proofing")
    }

    fun addComment(photoId: String, text: String) {
        if (text.isNotBlank()) {
            val author = currentUser.value?.fullName ?: "Client"
            repository.addComment(photoId, author, "Studio Client", text)
            showToast("Comment posted")
        }
    }

    fun submitProofingSelection(galleryId: String) {
        repository.submitSelection(galleryId)
        showToast("Proofing selection submitted to studio!")
    }

    fun getFavoriteMedia(): List<MediaItem> = repository.getFavoriteMedia()
    fun getSelectedMedia(): List<MediaItem> = repository.getSelectedMedia()

    // Cart & Store
    fun addToCart(product: StoreProduct, variant: String, quantity: Int = 1) {
        repository.addToCart(product, variant, quantity)
        showToast("Added ${product.name} to cart")
    }

    fun updateCartQuantity(cartItemId: String, quantity: Int) {
        repository.updateCartItemQuantity(cartItemId, quantity)
    }

    fun processCheckout(name: String, email: String, phone: String, paymentMethod: String) {
        viewModelScope.launch {
            val order = repository.checkoutOrder(name, email, phone, paymentMethod)
            lastCompletedOrder.value = order
            navigateTo(ScreenRoute.ClientPaymentSuccess(order.id))
        }
    }

    // Upload simulation
    fun triggerUpload(filename: String, type: String, sizeBytes: Long) {
        repository.addUploadTask(filename, type, sizeBytes)
        showToast("Started uploading $filename")
        navigateTo(ScreenRoute.UploadCenter)
    }

    fun sendMessage(threadId: String, text: String) {
        if (text.isNotBlank()) {
            val sender = currentUser.value?.fullName ?: "Studio Lead"
            repository.sendChatMessage(threadId, text, sender)
        }
    }
}
