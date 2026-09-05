package com.example.receiver

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import com.example.MusicProApplication

class HeadphoneBluetoothReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        val app = context.applicationContext as? MusicProApplication ?: return
        val playerManager = app.container.playerManager

        when (action) {
            // Cas 1: Écouteurs filaires ou Bluetooth débranchés brutalement (Become Noisy)
            AudioManager.ACTION_AUDIO_BECOMING_NOISY -> {
                Log.d("HeadphoneReceiver", "Audio becoming noisy -> Auto-pause playback")
                playerManager.pause()
                playerManager.onBluetoothDeviceDisconnected("Casque débranché")
            }

            // Cas 2: Casque Bluetooth déconnecté
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val deviceName = try {
                    device?.name ?: "Casque Bluetooth"
                } catch (e: SecurityException) {
                    "Casque Bluetooth"
                }
                Log.d("HeadphoneReceiver", "Bluetooth disconnected: $deviceName -> Auto-pause playback")
                playerManager.pause()
                playerManager.onBluetoothDeviceDisconnected(deviceName)
            }

            // Cas 3: Casque Bluetooth connecté
            BluetoothDevice.ACTION_ACL_CONNECTED -> {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val deviceName = try {
                    device?.name ?: "Casque Bluetooth"
                } catch (e: SecurityException) {
                    "Casque Bluetooth"
                }
                Log.d("HeadphoneReceiver", "Bluetooth connected: $deviceName")
                playerManager.onBluetoothDeviceConnected(deviceName)
            }
        }
    }
}
