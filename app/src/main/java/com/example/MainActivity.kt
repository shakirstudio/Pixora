package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Client
import com.example.data.model.GalleryPrivacy
import com.example.data.model.Project
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.Charcoal900
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.PixoraViewModel
import com.example.ui.viewmodel.ScreenRoute

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: PixoraViewModel = viewModel()
            val currentRoute by viewModel.currentRoute.collectAsState()
            val notifications by viewModel.notifications.collectAsState()
            val cart by viewModel.cart.collectAsState()
            val clients by viewModel.clients.collectAsState()
            val projects by viewModel.projects.collectAsState()
            val toastMessage by viewModel.toastMessage.collectAsState()

            val isCreateClientOpen by viewModel.isCreateClientOpen.collectAsState()
            val isCreateProjectOpen by viewModel.isCreateProjectOpen.collectAsState()
            val isCreateGalleryOpen by viewModel.isCreateGalleryOpen.collectAsState()
            val isCreateInvoiceOpen by viewModel.isCreateInvoiceOpen.collectAsState()

            var showMoreSheet by remember { mutableStateOf(false) }

            // Back button handling
            BackHandler(enabled = currentRoute !is ScreenRoute.Dashboard && currentRoute !is ScreenRoute.Splash) {
                if (!viewModel.navigateBack()) {
                    viewModel.navigateTo(ScreenRoute.Dashboard, addToBackStack = false)
                }
            }

            // Determine if current screen is in Client Mode
            val isClientRoute = currentRoute is ScreenRoute.ClientLanding ||
                    currentRoute is ScreenRoute.ClientGalleryHome ||
                    currentRoute is ScreenRoute.ClientPhotoGrid ||
                    currentRoute is ScreenRoute.ClientPhotoViewer ||
                    currentRoute is ScreenRoute.ClientFavorites ||
                    currentRoute is ScreenRoute.ClientProofing ||
                    currentRoute is ScreenRoute.ClientVideoGallery ||
                    currentRoute is ScreenRoute.ClientDownloadCenter ||
                    currentRoute is ScreenRoute.ClientStore ||
                    currentRoute is ScreenRoute.ClientProductDetail ||
                    currentRoute is ScreenRoute.ClientCart ||
                    currentRoute is ScreenRoute.ClientCheckout ||
                    currentRoute is ScreenRoute.ClientPaymentSuccess

            val isAuthRoute = currentRoute is ScreenRoute.Splash ||
                    currentRoute is ScreenRoute.Welcome ||
                    currentRoute is ScreenRoute.SignUp ||
                    currentRoute is ScreenRoute.OtpVerify ||
                    currentRoute is ScreenRoute.Login ||
                    currentRoute is ScreenRoute.ForgotPassword ||
                    currentRoute is ScreenRoute.Onboarding

            MyApplicationTheme(darkTheme = isClientRoute || currentRoute is ScreenRoute.Splash || currentRoute is ScreenRoute.Welcome) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (!isAuthRoute && !isClientRoute) {
                            PixoraTopBar(
                                title = when (currentRoute) {
                                    is ScreenRoute.Dashboard -> "PIXORA STUDIOS"
                                    is ScreenRoute.Projects, is ScreenRoute.ProjectDashboard -> "Projects"
                                    is ScreenRoute.Galleries, is ScreenRoute.GalleryEditor, is ScreenRoute.GallerySettings -> "Galleries"
                                    is ScreenRoute.Clients, is ScreenRoute.ClientProfile -> "Clients"
                                    is ScreenRoute.Orders, is ScreenRoute.OrderDetail -> "Store Orders"
                                    is ScreenRoute.Invoices -> "Billing & Invoices"
                                    is ScreenRoute.QuickUpload, is ScreenRoute.UploadCenter -> "Upload Media"
                                    is ScreenRoute.Storage -> "Cloud Storage"
                                    is ScreenRoute.Subscription -> "Subscription"
                                    is ScreenRoute.WebsiteBuilder -> "Website Builder"
                                    is ScreenRoute.Analytics -> "Studio Analytics"
                                    is ScreenRoute.Team -> "Team Members"
                                    is ScreenRoute.Notifications -> "Notifications"
                                    is ScreenRoute.Messages -> "Client Messages"
                                    is ScreenRoute.Settings -> "Studio Settings"
                                    is ScreenRoute.AdminPortal -> "Platform Admin"
                                    else -> "PIXORA"
                                },
                                subtitle = "Fine Art Client Delivery",
                                showBackButton = currentRoute !is ScreenRoute.Dashboard,
                                onBackClick = {
                                    if (!viewModel.navigateBack()) {
                                        viewModel.navigateTo(ScreenRoute.Dashboard, addToBackStack = false)
                                    }
                                },
                                unreadNotificationCount = notifications.count { !it.isRead },
                                onNotificationsClick = { viewModel.navigateTo(ScreenRoute.Notifications) },
                                onModeSwitchClick = {
                                    // Cycle between Studio, Client Preview, and Super-Admin
                                    when (currentRoute) {
                                        is ScreenRoute.AdminPortal -> viewModel.navigateTo(ScreenRoute.Dashboard)
                                        is ScreenRoute.Dashboard -> viewModel.navigateTo(ScreenRoute.ClientLanding("gal_1"))
                                        else -> viewModel.navigateTo(ScreenRoute.AdminPortal)
                                    }
                                },
                                modeLabel = when (currentRoute) {
                                    is ScreenRoute.AdminPortal -> "Super-Admin"
                                    else -> "Studio Portal"
                                }
                            )
                        }
                    },
                    bottomBar = {
                        if (!isAuthRoute) {
                            if (isClientRoute && currentRoute !is ScreenRoute.ClientLanding && currentRoute !is ScreenRoute.ClientPhotoViewer) {
                                val clientSelectedTab = when (currentRoute) {
                                    is ScreenRoute.ClientGalleryHome -> "Home"
                                    is ScreenRoute.ClientPhotoGrid -> "Albums"
                                    is ScreenRoute.ClientFavorites -> "Favorites"
                                    is ScreenRoute.ClientProofing -> "Selection"
                                    is ScreenRoute.ClientStore, is ScreenRoute.ClientProductDetail, is ScreenRoute.ClientCart -> "Store"
                                    else -> "Home"
                                }

                                ClientBottomNav(
                                    selectedTab = clientSelectedTab,
                                    cartItemCount = cart.sumOf { it.quantity },
                                    onTabSelected = { tab ->
                                        when (tab) {
                                            "Home" -> viewModel.navigateTo(ScreenRoute.ClientGalleryHome("gal_1"))
                                            "Albums" -> viewModel.navigateTo(ScreenRoute.ClientPhotoGrid("gal_1"))
                                            "Favorites" -> viewModel.navigateTo(ScreenRoute.ClientFavorites("gal_1"))
                                            "Selection" -> viewModel.navigateTo(ScreenRoute.ClientProofing("gal_1"))
                                            "Store" -> viewModel.navigateTo(ScreenRoute.ClientStore("gal_1"))
                                        }
                                    }
                                )
                            } else if (!isClientRoute) {
                                val selectedTab = when (currentRoute) {
                                    is ScreenRoute.Dashboard -> "Dashboard"
                                    is ScreenRoute.Projects, is ScreenRoute.ProjectDashboard -> "Projects"
                                    is ScreenRoute.Galleries, is ScreenRoute.GalleryEditor, is ScreenRoute.GallerySettings -> "Galleries"
                                    is ScreenRoute.Clients, is ScreenRoute.ClientProfile -> "Clients"
                                    else -> "More"
                                }

                                PixoraBottomNav(
                                    selectedTab = selectedTab,
                                    onTabSelected = { tab ->
                                        when (tab) {
                                            "Dashboard" -> viewModel.navigateTo(ScreenRoute.Dashboard)
                                            "Projects" -> viewModel.navigateTo(ScreenRoute.Projects)
                                            "Galleries" -> viewModel.navigateTo(ScreenRoute.Galleries)
                                            "Clients" -> viewModel.navigateTo(ScreenRoute.Clients)
                                            "More" -> showMoreSheet = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Route Switcher
                        when (val route = currentRoute) {
                            // Auth Stack
                            is ScreenRoute.Splash -> SplashScreen(viewModel) {
                                if (viewModel.currentUser.value != null) {
                                    viewModel.navigateTo(ScreenRoute.Dashboard, addToBackStack = false)
                                } else {
                                    viewModel.navigateTo(ScreenRoute.Login, addToBackStack = false)
                                }
                            }
                            is ScreenRoute.Welcome -> WelcomeLandingScreen(
                                onGetStarted = { viewModel.navigateTo(ScreenRoute.SignUp) },
                                onLogin = { viewModel.navigateTo(ScreenRoute.Login) }
                            )
                            is ScreenRoute.SignUp -> SignUpScreen(
                                viewModel = viewModel,
                                onSignUpSuccess = { viewModel.navigateTo(ScreenRoute.Dashboard, addToBackStack = false) },
                                onNavigateToLogin = { viewModel.navigateTo(ScreenRoute.Login) }
                            )
                            is ScreenRoute.OtpVerify -> OtpVerifyScreen(
                                onVerifySuccess = { viewModel.navigateTo(ScreenRoute.Dashboard, addToBackStack = false) },
                                onResend = { viewModel.showToast("OTP resent to email") }
                            )
                            is ScreenRoute.Login -> LoginScreen(
                                viewModel = viewModel,
                                onNavigateToSignUp = { viewModel.navigateTo(ScreenRoute.SignUp) },
                                onNavigateToForgotPassword = { viewModel.navigateTo(ScreenRoute.ForgotPassword) }
                            )
                            is ScreenRoute.ForgotPassword -> ForgotPasswordScreen(
                                onSendResetLink = { viewModel.showToast("Reset link sent to $it") },
                                onBackToLogin = { viewModel.navigateTo(ScreenRoute.Login) }
                            )
                            is ScreenRoute.Onboarding -> OnboardingWizardScreen {
                                viewModel.navigateTo(ScreenRoute.Dashboard, addToBackStack = false)
                            }

                            // Photographer App Stack
                            is ScreenRoute.Dashboard -> DashboardScreen(viewModel)
                            is ScreenRoute.Clients -> ClientsScreen(viewModel)
                            is ScreenRoute.ClientProfile -> ClientProfileScreen(route.clientId, viewModel)
                            is ScreenRoute.Projects -> ProjectsScreen(viewModel)
                            is ScreenRoute.ProjectDashboard -> ProjectDashboardScreen(route.projectId, viewModel)
                            is ScreenRoute.Tasks -> TasksScreen(viewModel)
                            is ScreenRoute.Galleries -> GalleriesScreen(viewModel)
                            is ScreenRoute.GalleryEditor -> GalleryEditorScreen(route.galleryId, viewModel)
                            is ScreenRoute.GallerySettings -> GallerySettingsScreen(route.galleryId, viewModel)
                            is ScreenRoute.QuickUpload -> QuickUploadScreen(viewModel)
                            is ScreenRoute.UploadCenter -> UploadCenterScreen(viewModel)
                            is ScreenRoute.Orders -> OrdersScreen(viewModel)
                            is ScreenRoute.OrderDetail -> OrderDetailScreen(route.orderId, viewModel)
                            is ScreenRoute.Invoices -> InvoicesScreen(viewModel)
                            is ScreenRoute.Storage -> StorageScreen(viewModel)
                            is ScreenRoute.Subscription -> SubscriptionScreen(viewModel)
                            is ScreenRoute.WebsiteBuilder -> WebsiteBuilderScreen(viewModel)
                            is ScreenRoute.Analytics -> AnalyticsScreen(viewModel)
                            is ScreenRoute.Team -> TeamScreen(viewModel)
                            is ScreenRoute.Notifications -> NotificationCenterScreen(viewModel)
                            is ScreenRoute.Messages -> MessagesScreen(viewModel)
                            is ScreenRoute.Settings -> SettingsScreen(viewModel)
                            is ScreenRoute.AdminPortal -> AdminPortalScreen(viewModel)

                            // Client-Facing Gallery Stack
                            is ScreenRoute.ClientLanding -> ClientLandingScreen(route.galleryId, viewModel)
                            is ScreenRoute.ClientGalleryHome -> ClientGalleryHomeScreen(route.galleryId, viewModel)
                            is ScreenRoute.ClientPhotoGrid -> ClientPhotoGridScreen(route.galleryId, route.albumId, viewModel)
                            is ScreenRoute.ClientPhotoViewer -> FullscreenPhotoViewerScreen(route.photoId, viewModel)
                            is ScreenRoute.ClientFavorites -> ClientFavoritesScreen(route.galleryId, viewModel)
                            is ScreenRoute.ClientProofing -> ClientProofingScreen(route.galleryId, viewModel)
                            is ScreenRoute.ClientVideoGallery -> ClientVideoGalleryScreen(route.galleryId, viewModel)
                            is ScreenRoute.ClientDownloadCenter -> ClientDownloadCenterScreen(route.galleryId, viewModel)
                            is ScreenRoute.ClientStore -> ClientStoreScreen(route.galleryId, viewModel)
                            is ScreenRoute.ClientProductDetail -> ProductDetailScreen(route.productId, viewModel)
                            is ScreenRoute.ClientCart -> CartScreen(viewModel)
                            is ScreenRoute.ClientCheckout -> CheckoutScreen(viewModel)
                            is ScreenRoute.ClientPaymentSuccess -> PaymentSuccessScreen(route.orderId, viewModel)
                        }

                        // Floating Toast Banner
                        ToastNotificationBanner(
                            message = toastMessage,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    }
                }

                // Global Creation Dialogs
                CreateClientDialog(
                    isOpen = isCreateClientOpen,
                    onDismiss = { viewModel.isCreateClientOpen.value = false },
                    onConfirm = { name, email, phone, addr, evt, notes ->
                        viewModel.createClient(name, email, phone, addr, evt, notes)
                    }
                )

                CreateProjectDialog(
                    isOpen = isCreateProjectOpen,
                    clients = clients,
                    onDismiss = { viewModel.isCreateProjectOpen.value = false },
                    onConfirm = { name, client, eventType, eventDate, location, budget, description ->
                        viewModel.createProject(name, client, eventType, eventDate, location, budget, description)
                    }
                )

                CreateGalleryDialog(
                    isOpen = isCreateGalleryOpen,
                    clients = clients,
                    projects = projects,
                    onDismiss = { viewModel.isCreateGalleryOpen.value = false },
                    onConfirm = { title, client, project, eventDate, description, privacy, allowDownloads, watermarkEnabled ->
                        viewModel.createGallery(title, client, project, eventDate, description, privacy, allowDownloads, watermarkEnabled)
                    }
                )

                CreateInvoiceDialog(
                    isOpen = isCreateInvoiceOpen,
                    onDismiss = { viewModel.isCreateInvoiceOpen.value = false },
                    onConfirm = { name, email, proj, desc, amt ->
                        viewModel.createInvoice(name, email, proj, desc, amt)
                    }
                )

                // More Menu Bottom Sheet
                if (showMoreSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showMoreSheet = false },
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                                .navigationBarsPadding()
                        ) {
                            Text(
                                text = "STUDIO OPERATIONS",
                                style = MaterialTheme.typography.labelSmall,
                                letterSpacing = 2.sp,
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            val moreOptions: List<Triple<String, ImageVector, () -> Unit>> = listOf(
                                Triple("Studio Tasks & Shoots", Icons.Default.Checklist) {
                                    viewModel.navigateTo(ScreenRoute.Tasks)
                                    showMoreSheet = false
                                },
                                Triple("Store Orders & Fulfillment", Icons.Default.ShoppingBag) {
                                    viewModel.navigateTo(ScreenRoute.Orders)
                                    showMoreSheet = false
                                },
                                Triple("Billing & GST Invoices", Icons.Default.Receipt) {
                                    viewModel.navigateTo(ScreenRoute.Invoices)
                                    showMoreSheet = false
                                },
                                Triple("Quick Upload Center", Icons.Default.CloudUpload) {
                                    viewModel.navigateTo(ScreenRoute.UploadCenter)
                                    showMoreSheet = false
                                },
                                Triple("Cloud Storage & CDN", Icons.Default.CloudQueue) {
                                    viewModel.navigateTo(ScreenRoute.Storage)
                                    showMoreSheet = false
                                },
                                Triple("Studio Website Builder", Icons.Default.Language) {
                                    viewModel.navigateTo(ScreenRoute.WebsiteBuilder)
                                    showMoreSheet = false
                                },
                                Triple("Performance Analytics", Icons.Default.BarChart) {
                                    viewModel.navigateTo(ScreenRoute.Analytics)
                                    showMoreSheet = false
                                },
                                Triple("Team & Staff Permissions", Icons.Default.Groups) {
                                    viewModel.navigateTo(ScreenRoute.Team)
                                    showMoreSheet = false
                                },
                                Triple("Client Messages & Live Chat", Icons.Default.Chat) {
                                    viewModel.navigateTo(ScreenRoute.Messages)
                                    showMoreSheet = false
                                },
                                Triple("Studio Profile & Branding", Icons.Default.Settings) {
                                    viewModel.navigateTo(ScreenRoute.Settings)
                                    showMoreSheet = false
                                },
                                Triple("Platform Super-Admin Portal", Icons.Default.AdminPanelSettings) {
                                    viewModel.navigateTo(ScreenRoute.AdminPortal)
                                    showMoreSheet = false
                                },
                                Triple("Client Gallery Portal Preview", Icons.Default.Visibility) {
                                    val firstGal = viewModel.galleries.value.firstOrNull()
                                    if (firstGal != null) {
                                        viewModel.navigateTo(ScreenRoute.ClientLanding(firstGal.id))
                                    } else {
                                        viewModel.showToast("No galleries available. Please create one first.")
                                        viewModel.navigateTo(ScreenRoute.Galleries)
                                    }
                                    showMoreSheet = false
                                }
                            )

                            moreOptions.forEach { (label, icon, action) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { action() }
                                        .padding(vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = GoldAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
