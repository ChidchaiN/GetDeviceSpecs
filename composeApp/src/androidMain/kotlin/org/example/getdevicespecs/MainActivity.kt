package org.example.getdevicespecs

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Preview
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import org.example.getdevicespecs.utils.AndriodDeviceInfoProvider
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 100
    private val coordinates = mutableStateOf("Fetching location...")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission granted, fetch location asynchronously
            fetchLocation()
        } else {
            // Request permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        setContent {
            App(contex = this, coordinates = coordinates.value)
        }
    }

    private fun fetchLocation() {
        // Use lifecycleScope to launch the coroutine
        lifecycleScope.launch {
            val deviceInfoProvider = AndriodDeviceInfoProvider(this@MainActivity)
            try {
                // Fetch the location asynchronously
                val location = deviceInfoProvider.getDeviceCurrentLocation()
                coordinates.value = location ?: "Failed to get location."
            } catch (e: Exception) {
                Log.e("MainActivity", "Error fetching location", e)
                coordinates.value = "Error fetching location"
            }
        }
    }

    // Handle permission result if required
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation()
            } else {
                Log.e("MainActivity", "Location permission not granted.")
            }
        }
    }
}

@Composable
fun App(contex: Context, coordinates: String) {
    val deviceInfoProvider = AndriodDeviceInfoProvider(contex)
    val deviceSN = deviceInfoProvider.getDeviceSN()
    val deviceName = deviceInfoProvider.getDeviceName()

    Column {
        deviceSN?.let {
            Text(text = "Device Serial Number: $it")
        }
        deviceName?.let {
            Text(text = "Device Name: $it")
        }
        Text(text = "Current Location: $coordinates")
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(contex = LocalContext.current, coordinates = "lat: 0.0, long: 0.0")
}
