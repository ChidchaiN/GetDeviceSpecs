package org.example.getdevicespecs.utils

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.Foundation.NSBundle
import platform.Foundation.NSObject
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class IOSDeviceInfoProvider : GetDeviceSpecs, NSObject(), CLLocationManagerDelegateProtocol {

    private val locationManager = CLLocationManager()

    override fun getDeviceSN(): String? {
        return NSBundle.mainBundle.bundleIdentifier // Or another identifier suitable as a serial number.
    }

    override fun getDeviceName(): String? {
        return platform.UIKit.UIDevice.currentDevice.name
    }

    override suspend fun getDeviceCurrentLocation(): String? {
        return suspendCancellableCoroutine { cont ->
            locationManager.delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
                override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
                    val location = didUpdateLocations.lastOrNull() as? platform.CoreLocation.CLLocation
                    if (location != null) {
                        cont.resume("lat: ${location.coordinate.latitude}, long: ${location.coordinate.longitude}")
                    } else {
                        cont.resumeWithException(Exception("Location not available"))
                    }
                    locationManager.stopUpdatingLocation()
                }

                override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
                    cont.resumeWithException(Exception("Error getting location: ${didFailWithError.localizedDescription}"))
                    locationManager.stopUpdatingLocation()
                }
            }

            if (locationManager.authorizationStatus == kCLAuthorizationStatusAuthorizedWhenInUse) {
                locationManager.startUpdatingLocation()
            } else {
                cont.resume("Permission not granted")
            }
        }
    }
}
