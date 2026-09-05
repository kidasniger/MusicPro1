package com.example.ui.navigation

sealed class Screen(val route: String) {
    // Identité
    object Logo : Screen("logo")
    object Splash : Screen("splash")

    // Onboarding
    object Onboarding : Screen("onboarding")
    object Permissions : Screen("permissions")

    // Core
    object Home : Screen("home")
    object Search : Screen("search")

    // Player
    object Player : Screen("player")
    object PlayerVariants : Screen("playerVar")

    // Paroles
    object Lyrics : Screen("lyrics")
    object LyricsEmpty : Screen("lyricsEmpty")
    object SourceSheet : Screen("sourceSheet")
    object Lrclib : Screen("lrclib")
    object GroqTrans : Screen("groqTrans")
    object GroqConfig : Screen("groqConfig")
    object Translate : Screen("translate")
    object Integrate : Screen("integrate")
    object Sync : Screen("sync")

    // Library
    object Queue : Screen("queue")
    object Favorites : Screen("fav")
    object History : Screen("history")
    object Playlists : Screen("playlists")
    object PlaylistDetail : Screen("playlistDetail/{playlistId}") {
        fun createRoute(playlistId: Long) = "playlistDetail/$playlistId"
    }
    object Artists : Screen("artists")
    object Albums : Screen("albums")

    // Utils
    object SleepTimer : Screen("sleep")
    object Equalizer : Screen("eq")
    object TrackInfo : Screen("infos")
    object Settings : Screen("settings")

    // System
    object Notification : Screen("notif")
    object LockScreen : Screen("lock")
    object Bluetooth : Screen("bt")

    // Widgets
    object WidgetCompact : Screen("wCompact")
    object WidgetStandard : Screen("wStandard")
    object WidgetLyrics : Screen("wLyrics")

    // États
    object PermStates : Screen("permStates")
    object ErrorStates : Screen("errorStates")
    object EmptyStates : Screen("emptyStates")
    object Offline : Screen("offline")
}
