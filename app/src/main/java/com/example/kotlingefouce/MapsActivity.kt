package com.example.kotlingefouce

import android.Manifest
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.BaseExpandableListAdapter
import android.widget.Toast
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQuery
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import org.xml.sax.Locator

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, IOnloadLocationListener {

    private lateinit var mMap: GoogleMap
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentMarker: Marker
    private lateinit var myLocationRef: DatabaseReference
    private lateinit var dangerousArea: MutableList<LatLng>
    private lateinit var listener: IOnloadLocationListener

    private lateinit var myCity: DatabaseReference
    private lateinit var lastLocation: Location
    private lateinit var geoQuery: GeoQuery
    private lateinit var geoFire: GeoFire


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {

                        buidLocationRequest()
                        buidLocationCallBack()
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this@MapsActivity)
                        initArea()
                        settingGeoFire()

                    }

                    override fun onPermissionRationaleShouldBeShown(
                            permission: PermissionRequest?,
                            token: PermissionToken?
                    ) {

                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        Toast.makeText(MapsActivity.this, "you must enable this  permission ", Toast.LENGTH_SHORT).show()
                    }

                })


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun settingGeoFire() {
        TODO("Not yet implemented")
    }

    private fun initArea() {
        myCity = FirebaseDatabase.getInstance()
                .getReference("Dangerous area")
                .child("My city")

        listener = this

        //add  realtime chnge update
        myCity!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(dataSnapShot: DataSnapshot) {
                val latLngList = ArrayList<MyLatLng>()
                for (locationSnapShot in dataSnapShot.children) {
                    val latLng = locationSnapShot.getValue(MyLatLng::class.java)
                    latLngList.add(latLng!!)
                }
                listener!!.onLocationLoadSuccess(latLngList)

            }

        })

    }

    private fun buidLocationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                if (mMap != null) {
                    lastLocation = locationResult!!.lastLocation
                    addUserMarker()
aa
                }
            }

        }
    }

    private fun addUserMarker() {
        geoFire!!.setLocation("you", GeoLocation(lastLocation!!.latitude,
                lastLocation!!.longitude)) { _, _ ->
            if (currentMarker != null) currentMarker.remove()
            currentMarker = mMap!!.addMarker(MarkerOptions().position(LatLng(lastLocation!!.latitude, lastLocation.longitude))
                    .title("tittle"))         //after add marker

            mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(currentMarker!!.position, 12.0f))


        }
    }

    private fun buidLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest!!.interval = 5000
        locationRequest!!.fastestInterval = 3000
        locationRequest!!.smallestDisplacement = 10f
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onLocationLoadSuccess(latLng: List<MyLatLng>) {
        TODO("Not yet implemented")
    }

    override fun onLocationLoadFailed(message: String) {
        TODO("Not yet implemented")
    }
}