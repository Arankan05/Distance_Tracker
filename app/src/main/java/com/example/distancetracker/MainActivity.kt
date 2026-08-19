package com.example.distancetracker

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private var startLocation: Location? = null
    private lateinit var locationHelper: LocationHelper

    private lateinit var tvStart: TextView
    private lateinit var tvEnd: TextView
    private lateinit var tvDistance: TextView
    private lateinit var btnSetStart: Button
    private lateinit var btnSetEnd: Button

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        locationHelper = LocationHelper(this)

        tvStart = findViewById(R.id.tvStart)
        tvEnd = findViewById(R.id.tvEnd)
        tvDistance = findViewById(R.id.tvDistance)
        btnSetStart = findViewById(R.id.btnSetStart)
        btnSetEnd = findViewById(R.id.btnSetEnd)

        btnSetStart.setOnClickListener {
            handleLocationAction {
                locationHelper.fetchCurrentLocation { location ->
                    startLocation = location
                    tvStart.text = "Start Point: ${location.latitude}, ${location.longitude}"
                    Toast.makeText(this, "Start point set", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnSetEnd.setOnClickListener {
            if (startLocation == null) {
                Toast.makeText(this, "Set start point first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            handleLocationAction {
                locationHelper.fetchCurrentLocation { location ->
                    val distance = startLocation!!.distanceTo(location)
                    tvEnd.text = "End Point: ${location.latitude}, ${location.longitude}"
                    tvDistance.text = "Distance: %.2f meters".format(distance)
                }
            }
        }
    }

    private fun handleLocationAction(action: () -> Unit) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                action()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
}
