package com.example.lab14

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && requestCode == 0) {
            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (allGranted) {
                loadMap()
            } else {
                finish()
            }
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
        loadMap()
    }

    override fun onMapReady(map: GoogleMap) {
        val isAccessFineLocationGranted = ActivityCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val isAccessCoarseLocationGranted = ActivityCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (isAccessFineLocationGranted && isAccessCoarseLocationGranted) {
            map.isMyLocationEnabled = true

            val taipei101 = LatLng(25.033611, 121.565000)
            val taipeiMainStation = LatLng(25.047924, 121.517081)

            map.addMarker(MarkerOptions().position(taipei101).title("台北101"))
            map.addMarker(MarkerOptions().position(taipeiMainStation).title("台北車站"))

            val apiKey = try {
                packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                    .metaData.getString("com.google.android.geo.API_KEY")
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            if (apiKey.isNullOrEmpty()) {
                Toast.makeText(this, "API 金鑰讀取失敗", Toast.LENGTH_LONG).show()
                return
            }

            val retrofit = Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(DirectionsService::class.java)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = service.getRoute(
                        origin = "${taipei101.latitude},${taipei101.longitude}",
                        destination = "${taipeiMainStation.latitude},${taipeiMainStation.longitude}",
                        mode = "walking",
                        apiKey = apiKey
                    )

                    val points = response.routes.firstOrNull()?.overview_polyline?.points
                    if (!points.isNullOrEmpty()) {
                        val path = PolyUtil.decode(points)
                        CoroutineScope(Dispatchers.Main).launch {
                            map.addPolyline(
                                PolylineOptions().addAll(path).color(Color.RED).width(15f)
                            )
                            val bounds = LatLngBounds.Builder()
                            path.forEach { bounds.include(it) }
                            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100))
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(this@MainActivity, "路線載入失敗", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        }
    }

    private fun loadMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
}