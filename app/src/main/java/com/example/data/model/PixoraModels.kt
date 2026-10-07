package com.example.data.model

import java.util.UUID

enum class UserRole {
    OWNER, ADMIN, PHOTOGRAPHER, EDITOR, CLIENT
}

data class User(
    val id: String = UUID.randomUUID().toString(),
    val fullName: String,
    val email: String,
    val phone: String,
    val username: String,
    val passwordHash: String,
    val role: UserRole = UserRole.OWNER,
    val studioName: String = "",
    val avatarUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class ProjectStatus {
    ACTIVE, UPCOMING, COMPLETED, ARCHIVED
}

enum class GalleryPrivacy {
    PUBLIC, PASSWORD, PIN, PRIVATE_INVITE
}

enum class DownloadResolution {
    ORIGINAL, HIGH_RES, WEB_SIZE
}

enum class UploadStatus {
    QUEUED, UPLOADING, PROCESSING, COMPLETED, FAILED, PAUSED
}

enum class OrderStatus {
    PENDING, PAID, PROCESSING, COMPLETED, CANCELLED, REFUNDED
}

enum class InvoiceStatus {
    DRAFT, SENT, PAID, OVERDUE, CANCELLED
}

enum class TeamRole {
    OWNER, ADMIN, EDITOR, PHOTOGRAPHER, VIEWER
}

enum class TaskStatus {
    PENDING, IN_PROGRESS, COMPLETED, CANCELLED
}

enum class NotificationCategory {
    ALL, GALLERIES, CLIENTS, PROOFING, ORDERS, PAYMENTS, SYSTEM
}

data class Client(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val email: String,
    val phone: String,
    val avatarUrl: String = "",
    val address: String = "",
    val eventType: String = "",
    val tags: List<String> = emptyList(),
    val notes: String = "",
    val activeProjectsCount: Int = 0,
    val galleriesCount: Int = 0,
    val lastActivity: String = "Just added",
    val createdAt: Long = System.currentTimeMillis()
)

data class Project(
    val id: String = UUID.randomUUID().toString(),
    val clientId: String,
    val clientName: String,
    val name: String,
    val eventType: String,
    val eventDate: String,
    val location: String,
    val coverImageUrl: String = "",
    val description: String = "",
    val budget: String = "₹0",
    val status: ProjectStatus = ProjectStatus.ACTIVE,
    val galleryCount: Int = 0,
    val progressPercent: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

data class Gallery(
    val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val clientId: String,
    val clientName: String,
    val title: String,
    val eventDate: String,
    val coverImageUrl: String = "",
    val description: String = "",
    val privacy: GalleryPrivacy = GalleryPrivacy.PUBLIC,
    val password: String = "",
    val pin: String = "",
    val allowDownloads: Boolean = true,
    val downloadResolutions: List<DownloadResolution> = listOf(
        DownloadResolution.ORIGINAL,
        DownloadResolution.HIGH_RES,
        DownloadResolution.WEB_SIZE
    ),
    val watermarkEnabled: Boolean = false,
    val watermarkText: String = "PIXORA",
    val expiryDate: String = "",
    val accentColorHex: String = "#D4AF37",
    val photoCount: Int = 0,
    val videoCount: Int = 0,
    val viewsCount: Int = 0,
    val favoritesCount: Int = 0,
    val selectionsCount: Int = 0,
    val selectionLimit: Int = 50,
    val isSelectionSubmitted: Boolean = false,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class Album(
    val id: String = UUID.randomUUID().toString(),
    val galleryId: String,
    val name: String,
    val coverImageUrl: String = "",
    val photoCount: Int = 0,
    val videoCount: Int = 0,
    val sortOrder: Int = 0
)

data class ExifData(
    val camera: String = "",
    val lens: String = "",
    val focalLength: String = "",
    val aperture: String = "",
    val shutterSpeed: String = "",
    val iso: String = "",
    val captureDate: String = ""
)

data class MediaItem(
    val id: String = UUID.randomUUID().toString(),
    val galleryId: String,
    val albumId: String,
    val albumName: String = "",
    val url: String,
    val thumbnailUrl: String = url,
    val title: String,
    val filename: String,
    val isVideo: Boolean = false,
    val durationSeconds: Int = 0,
    val width: Int = 6000,
    val height: Int = 4000,
    val sizeBytes: Long = 0,
    val isFavorite: Boolean = false,
    val isSelectedForProofing: Boolean = false,
    val commentCount: Int = 0,
    val exif: ExifData = ExifData(),
    val downloadUrl: String = url,
    val createdAt: Long = System.currentTimeMillis()
)

data class StudioTask(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val clientId: String = "",
    val clientName: String = "",
    val projectId: String = "",
    val projectName: String = "",
    val assignedToName: String = "",
    val assignedToId: String = "",
    val dueDate: String = "",
    val priority: String = "Normal",
    val status: TaskStatus = TaskStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)

data class PhotoComment(
    val id: String = UUID.randomUUID().toString(),
    val mediaId: String,
    val authorName: String,
    val authorRole: String = "Client",
    val message: String,
    val timestamp: String = "Just now",
    val isResolved: Boolean = false,
    val replies: List<PhotoComment> = emptyList()
)

data class ProofingSelection(
    val id: String = UUID.randomUUID().toString(),
    val galleryId: String,
    val clientId: String,
    val clientName: String,
    val selectedMediaIds: List<String> = emptyList(),
    val targetCount: Int = 50,
    val isSubmitted: Boolean = false,
    val submissionDate: String = "",
    val notes: String = ""
)

data class StoreProduct(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String,
    val price: Double,
    val imageUrl: String = "",
    val description: String,
    val variants: List<String> = listOf("Standard", "Premium", "Grand"),
    val selectedVariant: String = "Standard"
)

data class CartItem(
    val id: String = UUID.randomUUID().toString(),
    val product: StoreProduct,
    val variant: String,
    val quantity: Int = 1
)

data class Order(
    val id: String = "ORD-" + UUID.randomUUID().toString().take(6).uppercase(),
    val clientId: String,
    val clientName: String,
    val clientEmail: String,
    val clientPhone: String,
    val items: List<CartItem>,
    val subtotal: Double,
    val tax: Double,
    val discount: Double,
    val totalAmount: Double,
    val status: OrderStatus = OrderStatus.PAID,
    val paymentMethod: String = "",
    val transactionRef: String = "TXN_" + UUID.randomUUID().toString().take(8).uppercase(),
    val date: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class Invoice(
    val id: String = "INV-" + UUID.randomUUID().toString().take(6).uppercase(),
    val clientId: String,
    val clientName: String,
    val clientEmail: String,
    val projectName: String,
    val itemsDescription: String = "",
    val subtotal: Double = 0.0,
    val taxRatePercent: Double = 18.0,
    val taxAmount: Double = 0.0,
    val discountAmount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val status: InvoiceStatus = InvoiceStatus.DRAFT,
    val issueDate: String = "",
    val dueDate: String = "",
    val terms: String = "Payment due within 10 days of issue."
)

data class TeamMember(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val email: String,
    val role: TeamRole = TeamRole.PHOTOGRAPHER,
    val avatarUrl: String = "",
    val status: String = "Active",
    val canUpload: Boolean = true,
    val canEditGalleries: Boolean = true,
    val canViewBilling: Boolean = false,
    val canManageClients: Boolean = true
)

data class NotificationItem(
    val id: String = UUID.randomUUID().toString(),
    val category: NotificationCategory,
    val title: String,
    val message: String,
    val timeAgo: String = "Just now",
    val isRead: Boolean = false,
    val relatedEntityId: String = ""
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val senderName: String,
    val isFromPhotographer: Boolean,
    val text: String,
    val time: String = "Just now"
)

data class MessageThread(
    val id: String = UUID.randomUUID().toString(),
    val clientId: String = "",
    val clientName: String,
    val clientAvatar: String = "",
    val projectTitle: String,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Int = 0,
    val messages: List<ChatMessage> = emptyList()
)

data class UploadFileTask(
    val id: String = UUID.randomUUID().toString(),
    val filename: String,
    val fileType: String,
    val sizeBytes: Long,
    val progress: Float = 0f,
    val speedMbps: String = "0 MB/s",
    val remainingSecs: Int = 0,
    val status: UploadStatus = UploadStatus.UPLOADING,
    val thumbnailUri: String = ""
)

data class WebsiteSection(
    val id: String,
    val name: String,
    val isEnabled: Boolean = true,
    val title: String = "",
    val subtitle: String = ""
)

data class WebsiteConfig(
    val studioName: String = "",
    val tagline: String = "",
    val primaryColorHex: String = "#101114",
    val accentColorHex: String = "#D4AF37",
    val fontChoice: String = "Playfair & Inter",
    val buttonStyle: String = "Refined Pill",
    val galleryLayout: String = "Editorial Masonry",
    val subdomain: String = "",
    val customDomain: String = "",
    val isDomainConnected: Boolean = false,
    val sslActive: Boolean = false,
    val sections: List<WebsiteSection> = listOf(
        WebsiteSection("hero", "Hero Showcase", true),
        WebsiteSection("about", "About The Studio", true),
        WebsiteSection("portfolio", "Portfolio Highlights", true),
        WebsiteSection("services", "Signature Packages", true),
        WebsiteSection("testimonials", "Love Stories", true),
        WebsiteSection("pricing", "Investment Guide", true),
        WebsiteSection("contact", "Reserve Your Date", true),
        WebsiteSection("footer", "Footer & Socials", true)
    )
)

data class StudioSettings(
    val studioName: String = "",
    val photographerName: String = "",
    val email: String = "",
    val phone: String = "",
    val bio: String = "",
    val logoUrl: String = "",
    val watermarkText: String = "PIXORA",
    val currentPlan: String = "PRO STUDIO",
    val storageUsedGb: Double = 0.0,
    val storageLimitGb: Double = 1000.0,
    val galleriesCreated: Int = 0,
    val activeTeamMembers: Int = 0,
    val monthlyRevenue: String = "₹0",
    val twoFactorEnabled: Boolean = false
)
