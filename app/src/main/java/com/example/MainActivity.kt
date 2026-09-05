package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.navigation.MusicProBottomBar
import com.example.ui.navigation.Screen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.library.FavoritesScreen
import com.example.ui.screens.library.HistoryScreen
import com.example.ui.screens.library.PlaylistDetailScreen
import com.example.ui.screens.library.PlaylistsScreen
import com.example.ui.screens.lyrics.GroqConfigScreen
import com.example.ui.screens.lyrics.GroqTransScreen
import com.example.ui.screens.lyrics.IntegrateScreen
import com.example.ui.screens.lyrics.LrclibScreen
import com.example.ui.screens.lyrics.LyricsScreen
import com.example.ui.screens.lyrics.SourceSheetScreen
import com.example.ui.screens.lyrics.SyncScreen
import com.example.ui.screens.lyrics.TranslateScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.permissions.PermissionsScreen
import com.example.ui.screens.player.PlayerScreen
import com.example.ui.screens.search.SearchScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.screens.system.BluetoothHeadphoneScreen
import com.example.ui.screens.system.LockScreenPreview
import com.example.ui.screens.system.NotificationPreviewScreen
import com.example.ui.screens.system.WidgetsGalleryScreen
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.MusicProTheme
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.MainViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        val appContainer = (application as MusicProApplication).container
        MainViewModelFactory(
            musicRepository = appContainer.musicRepository,
            lyricsRepository = appContainer.lyricsRepository,
            playerManager = appContainer.playerManager,
            userPreferencesRepository = appContainer.userPreferencesRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicProTheme {
                MusicProApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicProApp(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Splash.route
    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activeTrack by viewModel.activeTrack.collectAsStateWithLifecycle()

    var showSourceSheet by remember { mutableStateOf(false) }
    val sourceSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Barre inférieure affichée sur les onglets principaux
    val bottomBarScreens = listOf(
        Screen.Home.route,
        Screen.Search.route,
        Screen.Lyrics.route,
        Screen.Playlists.route,
        Screen.Settings.route
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        bottomBar = {
            if (currentRoute in bottomBarScreens) {
                MusicProBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { targetRoute ->
                        navController.navigate(targetRoute) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. Splash Screen
            composable(Screen.Splash.route) {
                SplashScreen(
                    onSplashFinished = {
                        val destination = if (isOnboardingCompleted == true) {
                            Screen.Home.route
                        } else {
                            Screen.Onboarding.route
                        }
                        navController.navigate(destination) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            // 2. Onboarding Screen
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onOnboardingFinished = {
                        viewModel.completeOnboarding()
                        navController.navigate(Screen.Permissions.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            // 3. Demande de Permissions
            composable(Screen.Permissions.route) {
                PermissionsScreen(
                    onPermissionsComplete = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Permissions.route) { inclusive = true }
                        }
                    }
                )
            }

            // 4. Accueil (Bibliothèque locale & Sections)
            composable(Screen.Home.route) {
                val tracks by viewModel.allTracks.collectAsStateWithLifecycle()
                val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
                val currentPositionMs by viewModel.currentPositionMs.collectAsStateWithLifecycle()
                val durationMs by viewModel.durationMs.collectAsStateWithLifecycle()

                HomeScreen(
                    tracks = tracks,
                    activeTrack = activeTrack,
                    isPlaying = isPlaying,
                    currentPositionMs = currentPositionMs,
                    durationMs = durationMs,
                    onTrackSelected = { track -> viewModel.playTrack(track) },
                    onTogglePlayPause = { viewModel.togglePlayPause() },
                    onNext = { viewModel.next() },
                    onToggleFavorite = { track -> viewModel.toggleFavorite(track) },
                    onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                    onNavigateToPlayer = { navController.navigate(Screen.Player.route) },
                    onNavigateToFavorites = { navController.navigate(Screen.Favorites.route) },
                    onNavigateToHistory = { navController.navigate(Screen.History.route) },
                    onNavigateToBluetooth = { navController.navigate(Screen.Bluetooth.route) }
                )
            }

            // 5. Recherche active
            composable(Screen.Search.route) {
                val tracks by viewModel.allTracks.collectAsStateWithLifecycle()
                val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
                val currentPositionMs by viewModel.currentPositionMs.collectAsStateWithLifecycle()
                val durationMs by viewModel.durationMs.collectAsStateWithLifecycle()

                SearchScreen(
                    tracks = tracks,
                    activeTrack = activeTrack,
                    isPlaying = isPlaying,
                    currentPositionMs = currentPositionMs,
                    durationMs = durationMs,
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChange = { q -> viewModel.updateSearchQuery(q) },
                    onTrackSelected = { track -> viewModel.playTrack(track) },
                    onTogglePlayPause = { viewModel.togglePlayPause() },
                    onNext = { viewModel.next() },
                    onToggleFavorite = { track -> viewModel.toggleFavorite(track) },
                    onNavigateToPlayer = { navController.navigate(Screen.Player.route) }
                )
            }

            // 6. Lecteur Plein Écran
            composable(Screen.Player.route) {
                val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
                val currentPositionMs by viewModel.currentPositionMs.collectAsStateWithLifecycle()
                val durationMs by viewModel.durationMs.collectAsStateWithLifecycle()
                val queue by viewModel.queue.collectAsStateWithLifecycle()
                val sleepRemainingSeconds by viewModel.sleepRemainingSeconds.collectAsStateWithLifecycle()
                val isEndOfTrackSleepActive by viewModel.isEndOfTrackSleepActive.collectAsStateWithLifecycle()

                PlayerScreen(
                    activeTrack = activeTrack,
                    isPlaying = isPlaying,
                    currentPositionMs = currentPositionMs,
                    durationMs = durationMs,
                    queue = queue,
                    sleepRemainingSeconds = sleepRemainingSeconds,
                    isEndOfTrackSleepActive = isEndOfTrackSleepActive,
                    onTogglePlayPause = { viewModel.togglePlayPause() },
                    onSeekTo = { pos -> viewModel.seekTo(pos) },
                    onNext = { viewModel.next() },
                    onPrevious = { viewModel.previous() },
                    onToggleFavorite = { track -> viewModel.toggleFavorite(track) },
                    onTrackFromQueueSelected = { track -> viewModel.playTrack(track) },
                    onMoveQueueItem = { from, to -> viewModel.moveQueueItem(from, to) },
                    onRemoveQueueItem = { index -> viewModel.removeQueueItem(index) },
                    onClearQueue = { viewModel.clearQueue() },
                    onSetSleepTimerMinutes = { mins -> viewModel.setSleepTimerMinutes(mins) },
                    onSetSleepAtEndOfTrack = { viewModel.setSleepAtEndOfTrack() },
                    onCancelSleepTimer = { viewModel.cancelSleepTimer() },
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToLyrics = { navController.navigate(Screen.Lyrics.route) }
                )
            }

            // 7. Paroles Synchronisées (LyricsScreen)
            composable(Screen.Lyrics.route) {
                LyricsScreen(
                    viewModel = viewModel,
                    onBackClick = {
                        if (navController.previousBackStackEntry != null) {
                            navController.popBackStack()
                        } else {
                            navController.navigate(Screen.Home.route)
                        }
                    },
                    onOpenSourceSheet = { showSourceSheet = true },
                    onOpenTranslate = { navController.navigate(Screen.Translate.route) },
                    onOpenSync = { navController.navigate(Screen.Sync.route) },
                    onOpenLrclibSearch = { navController.navigate(Screen.Lrclib.route) },
                    onOpenGroqConfig = { navController.navigate(Screen.GroqConfig.route) }
                )
            }

            // 8. Recherche Manuelle LRCLIB
            composable(Screen.Lrclib.route) {
                LrclibScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 9. Transcription Groq AI
            composable(Screen.GroqTrans.route) {
                GroqTransScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onOpenGroqConfig = { navController.navigate(Screen.GroqConfig.route) },
                    onTranscriptionComplete = {
                        navController.popBackStack()
                        navController.navigate(Screen.Lyrics.route)
                    }
                )
            }

            // 10. Configuration Groq (Clé API chiffrée & Modèle)
            composable(Screen.GroqConfig.route) {
                GroqConfigScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 11. Traduction des paroles (bilingue)
            composable(Screen.Translate.route) {
                TranslateScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 12. Réglage fin du timing (Sync)
            composable(Screen.Sync.route) {
                SyncScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 13. Intégration & Gestion du fichier LRC
            composable(Screen.Integrate.route) {
                IntegrateScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToLrclib = { navController.navigate(Screen.Lrclib.route) },
                    onNavigateToGroq = { navController.navigate(Screen.GroqTrans.route) },
                    onNavigateToSync = { navController.navigate(Screen.Sync.route) }
                )
            }

            // 14. Playlists
            composable(Screen.Playlists.route) {
                PlaylistsScreen(
                    viewModel = viewModel,
                    onPlaylistClick = { playlistId ->
                        navController.navigate(Screen.PlaylistDetail.createRoute(playlistId))
                    },
                    onNavigateToFavorites = { navController.navigate(Screen.Favorites.route) },
                    onNavigateToHistory = { navController.navigate(Screen.History.route) },
                    onNavigateToPlayer = { navController.navigate(Screen.Player.route) }
                )
            }

            // 15. Détail d'une Playlist (Ajout/Retrait & Réordonnancement)
            composable(Screen.PlaylistDetail.route) { backStackEntry ->
                val playlistIdStr = backStackEntry.arguments?.getString("playlistId") ?: "0"
                val playlistId = playlistIdStr.toLongOrNull() ?: 0L
                PlaylistDetailScreen(
                    playlistId = playlistId,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToPlayer = { navController.navigate(Screen.Player.route) }
                )
            }

            // 16. Favoris (Bibliothèque)
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToPlayer = { navController.navigate(Screen.Player.route) }
                )
            }

            // 17. Historique d'écoute (Aujourd'hui, Hier, Cette semaine)
            composable(Screen.History.route) {
                HistoryScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToPlayer = { navController.navigate(Screen.Player.route) }
                )
            }

            // 18. Paramètres
            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.navigate(Screen.Home.route) },
                    onNavigateToGroqConfig = { navController.navigate(Screen.GroqConfig.route) },
                    onNavigateToEqualizer = { navController.navigate(Screen.Player.route) },
                    onNavigateToNotificationDemo = { navController.navigate(Screen.Notification.route) },
                    onNavigateToLockScreenDemo = { navController.navigate(Screen.LockScreen.route) },
                    onNavigateToBluetooth = { navController.navigate(Screen.Bluetooth.route) },
                    onNavigateToWidgets = { tab ->
                        when (tab) {
                            "standard" -> navController.navigate(Screen.WidgetStandard.route)
                            "lyrics" -> navController.navigate(Screen.WidgetLyrics.route)
                            else -> navController.navigate(Screen.WidgetCompact.route)
                        }
                    }
                )
            }

            // 19. Configuration Groq AI détaillée
            composable(Screen.GroqConfig.route) {
                GroqConfigScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 20. Notification de lecture média (MediaStyle / MediaSession)
            composable(Screen.Notification.route) {
                NotificationPreviewScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 21. Contrôles média sur écran verrouillé (Lock screen MediaSession)
            composable(Screen.LockScreen.route) {
                LockScreenPreview(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 22. Casque / Bluetooth (id="bt") avec gestion déconnexion & auto-pause
            composable(Screen.Bluetooth.route) {
                BluetoothHeadphoneScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 23. Widgets Android (id="wCompact")
            composable(Screen.WidgetCompact.route) {
                WidgetsGalleryScreen(
                    viewModel = viewModel,
                    initialTab = "compact",
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 24. Widgets Android (id="wStandard")
            composable(Screen.WidgetStandard.route) {
                WidgetsGalleryScreen(
                    viewModel = viewModel,
                    initialTab = "standard",
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 25. Widgets Android (id="wLyrics")
            composable(Screen.WidgetLyrics.route) {
                WidgetsGalleryScreen(
                    viewModel = viewModel,
                    initialTab = "lyrics",
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        // Bottom Sheet Source des paroles
        if (showSourceSheet) {
            SourceSheetScreen(
                sheetState = sourceSheetState,
                currentSource = uiState.lyricsSource,
                onDismiss = {
                    scope.launch { sourceSheetState.hide() }.invokeOnCompletion {
                        showSourceSheet = false
                    }
                },
                onSelectLrclibAuto = {
                    activeTrack?.let { viewModel.loadLyricsForTrack(it) }
                },
                onSelectLrclibManual = {
                    navController.navigate(Screen.Lrclib.route)
                },
                onSelectGroqWhisper = {
                    navController.navigate(Screen.GroqTrans.route)
                },
                onSelectTimingSync = {
                    navController.navigate(Screen.Sync.route)
                }
            )
        }
    }
}
