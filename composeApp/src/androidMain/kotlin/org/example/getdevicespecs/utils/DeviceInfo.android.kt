package org.example.getdevicespecs.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AndriodDeviceInfoProvider(private val context: Context) {

    @SuppressLint("HardwareIds")
    fun getDeviceSN(): String? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            } else {
                Build.SERIAL
            }
        } catch (e: Exception) {
            Log.e("DeviceInfoProvider", "Error getting device serial number", e)
            null
        }
    }

    fun getDeviceName(): String? {
        return try {
            Build.MODEL
        } catch (e: Exception) {
            Log.e("DeviceInfoProvider", "Error getting device name", e)
            null
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getDeviceCurrentLocation(): String? {
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        return try {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                suspendCancellableCoroutine { cont ->
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            cont.resume("lat: ${location.latitude}, long: ${location.longitude}")
                        } else {
                            cont.resumeWithException(Exception("Location not available"))
                        }
                    }.addOnFailureListener { exception ->
                        cont.resumeWithException(exception)
                    }
                }
            } else {
                "Permission not granted"
            }
        } catch (e: Exception) {
            Log.e("DeviceInfoProvider", "Error getting current location", e)
            null
        }
    }
}
