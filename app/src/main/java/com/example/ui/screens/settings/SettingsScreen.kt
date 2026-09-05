package com.example.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.PurpleDeep
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.SurfaceGlass
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    onNavigateToGroqConfig: () -> Unit,
    onNavigateToEqualizer: () -> Unit,
    onNavigateToNotificationDemo: () -> Unit = {},
    onNavigateToLockScreenDemo: () -> Unit = {},
    onNavigateToBluetooth: () -> Unit = {},
    onNavigateToWidgets: (String) -> Unit = {},
    onNavigateToStateGallery: (String) -> Unit = {},
    onNavigateToOffline: () -> Unit = {},
    onNavigateToLogo: () -> Unit = {},
    onNavigateToPlayerVariants: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val audioQuality by viewModel.audioQuality.collectAsState()
    val gaplessPlayback by viewModel.gaplessPlayback.collectAsState()
    val crossfadeSeconds by viewModel.crossfadeSeconds.collectAsState()
    val autoDownloadLyrics by viewModel.autoDownloadLyrics.collectAsState()
    val lyricsCacheCount by viewModel.lyricsCacheCount.collectAsState()
    val groqApiKey by viewModel.groqApiKey.collectAsState()
    val groqModel by viewModel.groqModel.collectAsState()

    var showClearCacheDialog by remember { mutableStateOf(false) }
    var showAudioQualityDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
            .testTag("settings_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SurfaceGlass)
                        .testTag("settings_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Retour",
                        tint = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Paramètres",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Système, Rendu & Intelligence Artificielle",
                        fontSize = 12.sp,
                        color = CyanAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // SECTION 1: APPARENCE & THÈME
            SettingsSectionHeader(title = "APPARENCE & THÈME", icon = Icons.Default.Palette)

            SettingsCard {
                SettingsClickableItem(
                    title = "Thème visuel",
                    subtitle = themeMode,
                    icon = Icons.Default.Palette,
                    accentColor = CyanAccent,
                    onClick = { showThemeDialog = true },
                    testTag = "settings_theme_item"
                )
                HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)
                SettingsInfoItem(
                    title = "Accents de couleur",
                    subtitle = "Cyan Cyberpunk • Violet Astral • Ambre AI",
                    icon = Icons.Default.GraphicEq,
                    accentColor = PurpleAccent
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 2: QUALITÉ AUDIO & RENDU
            SettingsSectionHeader(title = "QUALITÉ AUDIO & LECTURE", icon = Icons.Default.Headphones)

            SettingsCard {
                SettingsClickableItem(
                    title = "Format & Résolution",
                    subtitle = audioQuality,
                    icon = Icons.Default.Radio,
                    accentColor = CyanAccent,
                    onClick = { showAudioQualityDialog = true },
                    testTag = "settings_audio_quality_item"
                )

                HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)

                SettingsSwitchItem(
                    title = "Lecture enchaînée (Gapless)",
                    subtitle = "Transition inaudible et sans silence entre les pistes",
                    icon = Icons.Default.Equalizer,
                    accentColor = PurpleAccent,
                    checked = gaplessPlayback,
                    onCheckedChange = { viewModel.setGaplessPlayback(it) },
                    testTag = "settings_gapless_switch"
                )

                HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AmberAccent.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = AmberAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Fondu enchaîné (Crossfade)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (crossfadeSeconds == 0) "Désactivé" else "${crossfadeSeconds}s de transition",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }
                        Text(
                            text = "${crossfadeSeconds}s",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = crossfadeSeconds.toFloat(),
                        onValueChange = { viewModel.setCrossfadeSeconds(it.toInt()) },
                        valueRange = 0f..10f,
                        steps = 9,
                        colors = SliderDefaults.colors(
                            thumbColor = AmberAccent,
                            activeTrackColor = AmberAccent,
                            inactiveTrackColor = Color.White.copy(alpha = 0.12f)
                        ),
                        modifier = Modifier.testTag("settings_crossfade_slider")
                    )
                }

                HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)

                SettingsClickableItem(
                    title = "Égaliseur matériel",
                    subtitle = "Profils de fréquences 5 bandes & Bass Boost",
                    icon = Icons.Default.Equalizer,
                    accentColor = CyanAccent,
                    onClick = onNavigateToEqualizer,
                    testTag = "settings_equalizer_item"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 3: PAROLES & CACHE LRC
            SettingsSectionHeader(title = "PAROLES & CACHE SYNCHRO", icon = Icons.Default.CloudDownload)

            SettingsCard {
                SettingsSwitchItem(
                    title = "Téléchargement auto LRCLIB",
                    subtitle = "Récupère automatiquement les fichiers LRC synchronisés",
                    icon = Icons.Default.CloudDownload,
                    accentColor = CyanAccent,
                    checked = autoDownloadLyrics,
                    onCheckedChange = { viewModel.setAutoDownloadLyrics(it) },
                    testTag = "settings_auto_download_lyrics_switch"
                )

                HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PurpleAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cached,
                                contentDescription = null,
                                tint = PurpleAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Cache des paroles locales",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                            Text(
                                text = "$lyricsCacheCount morceaux en mémoire hors-ligne",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }

                    TextButton(
                        onClick = { showClearCacheDialog = true },
                        modifier = Modifier.testTag("settings_clear_cache_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = null,
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Vider",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF5252)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 4: INTELLIGENCE ARTIFICIELLE GROQ
            SettingsSectionHeader(title = "INTELLIGENCE ARTIFICIELLE GROQ", icon = Icons.Default.AutoAwesome)

            SettingsCard {
                SettingsClickableItem(
                    title = "Configuration Clé API & Modèle",
                    subtitle = if (groqApiKey.isNotEmpty()) "Clé chiffrée • $groqModel" else "Non configuré (cliquez pour configurer)",
                    icon = Icons.Default.VpnKey,
                    accentColor = AmberAccent,
                    onClick = onNavigateToGroqConfig,
                    testTag = "settings_groq_config_item"
                )

                HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)

                SettingsInfoItem(
                    title = "Sécurité & Chiffrement",
                    subtitle = "Clé stockée localement avec salage XOR + DataStore AES",
                    icon = Icons.Default.Shield,
                    accentColor = CyanAccent
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 5: SYSTÈME & INTÉGRATIONS
            SettingsSectionHeader(title = "NOTIFICATIONS & ÉCRAN VERROUILLÉ", icon = Icons.Default.Notifications)

            SettingsCard {
                SettingsClickableItem(
                    title = "Aperçu Notification MediaStyle",
                    subtitle = "Vérifier le rendu MediaSession Android 13+",
                    icon = Icons.Default.Notifications,
                    accentColor = CyanAccent,
                    onClick = onNavigateToNotificationDemo,
                    testTag = "settings_notif_preview_item"
                )

                HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)

                SettingsClickableItem(
                    title = "Aperçu Écran Verrouillé (Lock Screen)",
                    subtitle = "Pochette plein écran, scrub bar & contrôles MediaSession",
                    icon = Icons.Default.Lock,
                    accentColor = PurpleAccent,
                    onClick = onNavigateToLockScreenDemo,
                    testTag = "settings_lock_preview_item"
                )

                HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)

                SettingsClickableItem(
                    title = "Casque & Bluetooth (id=\"bt\")",
                    subtitle = "Gestion audio sans fil, déconnexion & auto-pause immédiate",
                    icon = Icons.Default.Bluetooth,
                    accentColor = CyanAccent,
                    onClick = onNavigateToBluetooth,
                    testTag = "settings_bt_item"
                )

                HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)

                SettingsClickableItem(
                    title = "App Widgets Android (wCompact, wStandard, wLyrics)",
                    subtitle = "Widgets Glance pour l'écran d'accueil avec contrôles & paroles",
                    icon = Icons.Default.Widgets,
                    accentColor = PurpleAccent,
                    onClick = { onNavigateToWidgets("compact") },
                    testTag = "settings_widgets_item"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 6: ÉTATS & HORS-LIGNE (id="permStates", "errorStates", "emptyStates", "offline")
            SettingsSectionHeader(title = "ÉTATS DE L'APPLICATION & MODE HORS-LIGNE", icon = Icons.Default.Security)

            SettingsCard {
                SettingsClickableItem(
                    title = "Mode Hors-ligne (id=\"offline\")",
                    subtitle = "Vérifier la disponibilité des services locaux et Cloud",
                    icon = Icons.Default.WifiOff,
                    accentColor = AmberAccent,
                    onClick = onNavigateToOffline,
                    testTag = "settings_offline_item"
                )

                HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)

                SettingsClickableItem(
                    title = "Galerie États de Permissions (id=\"permStates\")",
                    subtitle = "Accordée, refusée, redemander l'accès",
                    icon = Icons.Default.Security,
                    accentColor = PurpleAccent,
                    onClick = { onNavigateToStateGallery("perm") },
                    testTag = "settings_perm_states_item"
                )

                HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)

                SettingsClickableItem(
                    title = "Galerie États d'Erreur (id=\"errorStates\")",
                    subtitle = "LRCLIB indisponible, Groq IA indisponible, erreur décodage",
                    icon = Icons.Default.Warning,
                    accentColor = Color(0xFFFF5252),
                    onClick = { onNavigateToStateGallery("error") },
                    testTag = "settings_error_states_item"
                )

                HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)

                SettingsClickableItem(
                    title = "Galerie États Vides (id=\"emptyStates\")",
                    subtitle = "Bibliothèque vide, recherche vide, favoris, file, paroles",
                    icon = Icons.Default.HourglassEmpty,
                    accentColor = CyanAccent,
                    onClick = { onNavigateToStateGallery("empty") },
                    testTag = "settings_empty_states_item"
                )

                HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)

                SettingsClickableItem(
                    title = "Variantes du Lecteur (id=\"playerVar\")",
                    subtitle = "Modes Vinyle 33T, Spectre Audio DSP, Pochette Artistique",
                    icon = Icons.Default.Tune,
                    accentColor = PurpleAccent,
                    onClick = onNavigateToPlayerVariants,
                    testTag = "settings_player_variants_item"
                )

                HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)

                SettingsClickableItem(
                    title = "Identité Visuelle & Logo (id=\"logo\")",
                    subtitle = "Charte graphique, icône néon cyan/violet, variantes",
                    icon = Icons.Default.Palette,
                    accentColor = CyanAccent,
                    onClick = onNavigateToLogo,
                    testTag = "settings_logo_item"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 7: INFOS LOGICIEL
            SettingsCard {
                SettingsInfoItem(
                    title = "MusicPro v2.4.0 High-End",
                    subtitle = "Architecture Kotlin Compose • ExoPlayer Media3 • M3 Dynamic",
                    icon = Icons.Default.Info,
                    accentColor = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Dialog choix thème
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Choisir un thème", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    listOf("Dark Cyberpunk", "OLED Pure Black", "Deep Astral Purple").forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setThemeMode(mode)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = mode, color = if (themeMode == mode) CyanAccent else TextPrimary)
                            if (themeMode == mode) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = CyanAccent)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Fermer", color = CyanAccent)
                }
            },
            containerColor = SurfaceDark
        )
    }

    // Dialog choix qualité audio
    if (showAudioQualityDialog) {
        AlertDialog(
            onDismissRequest = { showAudioQualityDialog = false },
            title = { Text("Qualité & Résolution Audio", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    listOf(
                        "Hi-Res Lossless (FLAC 24-bit 96kHz)",
                        "CD Quality (16-bit 44.1kHz)",
                        "Haute qualité (AAC 320 kbps)",
                        "Économie de données (128 kbps)"
                    ).forEach { quality ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setAudioQuality(quality)
                                    showAudioQualityDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = quality,
                                color = if (audioQuality == quality) CyanAccent else TextPrimary,
                                fontSize = 13.sp
                            )
                            if (audioQuality == quality) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = CyanAccent)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAudioQualityDialog = false }) {
                    Text("Fermer", color = CyanAccent)
                }
            },
            containerColor = SurfaceDark
        )
    }

    // Dialog confirmation vider le cache
    if (showClearCacheDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheDialog = false },
            title = { Text("Vider le cache des paroles ?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Les paroles hors-ligne ($lyricsCacheCount fichiers) seront effacées de l'espace de stockage local.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearLyricsCache()
                        showClearCacheDialog = false
                    }
                ) {
                    Text("Vider le cache", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCacheDialog = false }) {
                    Text("Annuler", color = TextMuted)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
private fun SettingsSectionHeader(
    title: String,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = CyanAccent,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            color = TextMuted
        )
    }
}

@Composable
private fun SettingsCard(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
    ) {
        Column {
            content()
        }
    }
}

@Composable
private fun SettingsClickableItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun SettingsSwitchItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CyanAccent,
                checkedTrackColor = CyanAccent.copy(alpha = 0.35f),
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = SurfaceElevated
            )
        )
    }
}

@Composable
private fun SettingsInfoItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextMuted
            )
        }
    }
}
