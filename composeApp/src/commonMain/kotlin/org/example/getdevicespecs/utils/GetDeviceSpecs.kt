package org.example.getdevicespecs.utils

interface AndriodDeviceInfoProvider {

    fun getDeviceSN(): String?

    fun getDeviceName(): String?

    suspend fun getDeviceCurrentLocation(): String?
}