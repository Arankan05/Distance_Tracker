package com.example.distancetracker

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private var startLocation: Location? = null
    private lateinit var locationHelper: LocationHelper

    private lateinit var tvStart: TextView
    private lateinit var tvEnd: TextView
    private lateinit var tvDistance: TextView
    private lateinit var btnSetStart: MaterialButton
    private lateinit var btnSetEnd: MaterialButton

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        
        if (fineGranted || coarseGranted) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            toolbar.setPadding(0, systemBars.top, 0, 0)
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
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
                    Toast.makeText(this, "Start point captured", Toast.LENGTH_SHORT).show()
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
                    tvDistance.text = "%.2f meters".format(distance)
                    Toast.makeText(this, "End point captured and distance calculated", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleLocationAction(action: () -> Unit) {
        val finePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarsePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        
        if (finePermission == PackageManager.PERMISSION_GRANTED || coarsePermission == PackageManager.PERMISSION_GRANTED) {
            action()
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}
