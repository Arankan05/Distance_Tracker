package com.example.distancetracker

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class LocationHelper(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun fetchCurrentLocation(onResult: (Location) -> Unit) {
        val cts = CancellationTokenSource()
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    onResult(location)
                } else {
                    Toast.makeText(context, "Location is null. Ensure GPS is ON and you are not indoors.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e: Exception ->
                Toast.makeText(context, "Location Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
