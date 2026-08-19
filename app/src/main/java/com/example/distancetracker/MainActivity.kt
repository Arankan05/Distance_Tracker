package com.example.distancetracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    // 1. Register the permissions launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted. Tell the user, or trigger the location fetch directly.
            Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show()
        } else {
            // Permission denied. Show a message explaining why it is needed.
            Toast.makeText(this, "Location permission is required to calculate distance.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find the button (using the ID you all agreed on in Step 0)
        val btnSetStart = findViewById<Button>(R.id.btnSetStart)

        // Set the onClick listener for the button
        btnSetStart.setOnClickListener {
            // Call your custom permission function
            checkLocationPermission {
                // This block ONLY runs if permission is already granted.
                // Person 3 will put their fetchCurrentLocation() code right here later.
                Toast.makeText(this, "Permission is good. Ready to fetch location!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 2. The core permission check function
    private fun checkLocationPermission(onGranted: () -> Unit) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // We already have permission, run the code block passed in
                onGranted()
            }
            else -> {
                // We don't have permission, launch the Android system prompt
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
}