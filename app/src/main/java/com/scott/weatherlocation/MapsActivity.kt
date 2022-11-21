package com.scott.weatherlocation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import com.scott.weatherlocation.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, AppNavigator, NavigationView.OnNavigationItemSelectedListener{

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_maps)

        // set title of location
        this.title = "Location Bookmarks"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // set up nav drawer
        navigationView = findViewById(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)
        drawerLayout = findViewById(R.id.drawerLayout)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigateToFragment(FragmentMaps(), false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // add All Locations Bookmark Category to DataSingleton List
        var allLocationsCategoryExists = false
        DataSingleton.bookmarkCategoryList.forEach { bookmarkCategory ->
            if (bookmarkCategory.name == "All Locations") {
                allLocationsCategoryExists = true
            }
        }

        if (!allLocationsCategoryExists) {
            val allLocationsCategory = BookmarkCategory("All Locations")
            DataSingleton.bookmarkCategoryList.add(allLocationsCategory)
            DataSingleton.allLocationsBookmarkCategory = allLocationsCategory
        }

    }

    override fun onResume() {
        super.onResume()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = DataSingleton.mapFragment as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Check the permissions and request them if they are not already accepted.
        DataSingleton.checkAndRequestPermissions(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        fusedLocationClient.lastLocation.addOnSuccessListener { location->
            if (DataSingleton.getMyLocationBoolean) {
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    DataSingleton.lastLocation = Pair(latitude, longitude)

                    val myLocation = LatLng(latitude, longitude)
                    mMap.clear()
                    mMap.addMarker(MarkerOptions().position(myLocation).title("My Location"))
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16F))
                }
                DataSingleton.getMyLocationBoolean = false
            }
        }

        if (DataSingleton.goToLocationBoolean) {
            val location = DataSingleton.currentSelectedLocation

            val latLng = LatLng(location.latitude, location.longitude)

            val markerOptions = MarkerOptions()

            markerOptions.position(latLng)
            markerOptions.title(location.name)

            DataSingleton.lastLocation = Pair(latLng.latitude, latLng.longitude)

            mMap.clear()
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18F))
            mMap.addMarker(markerOptions)

            DataSingleton.goToLocationBoolean = false
        }

        // click to create a marker
        mMap.setOnMapClickListener { latLng ->
            val markerOptions = MarkerOptions()

            markerOptions.position(latLng)
            markerOptions.title("Selected Location")

            DataSingleton.lastLocation = Pair(latLng.latitude, latLng.longitude)

            mMap.clear()
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18F))
            mMap.addMarker(markerOptions)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentFrameLayout)
        return when (item.itemId) {
            R.id.map -> {
                if (currentFragment !is FragmentMaps) {
                    navigateToFragment(FragmentMaps(), false)
                }
                drawerLayout.close()
                true
            }
            R.id.bookmarkedLocations -> {
                if (currentFragment !is FragmentBookmarkCategories) {
                    navigateToFragment(FragmentBookmarkCategories(), false)
                }
                drawerLayout.close()
                true
            }
            else -> {
                true
            }
        }
    }

    override fun navigateToFragment(inputFragment: Fragment, addToBackStack: Boolean) {
        if (addToBackStack) {
            supportFragmentManager.beginTransaction().replace(R.id.fragmentFrameLayout, inputFragment).addToBackStack(null).commit()
        }
        else {
            supportFragmentManager.beginTransaction().replace(R.id.fragmentFrameLayout, inputFragment).commit()
        }
    }

    override fun getMapAsync() {
        val mapFragment = DataSingleton.mapFragment as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

}

