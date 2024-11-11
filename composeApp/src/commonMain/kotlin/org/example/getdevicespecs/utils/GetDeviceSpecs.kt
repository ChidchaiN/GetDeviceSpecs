package org.example.getdevicespecs.utils

interface GetDeviceSpecs {

    fun getDeviceSN(): String?

    fun getDeviceName(): String?

    suspend fun getDeviceCurrentLocation(): String?
}