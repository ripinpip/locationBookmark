package com.scott.weatherlocation

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.SharedPreferences
import androidx.fragment.app.Fragment

object DataSingleton {

    var bookmarkCategoryList = mutableListOf<BookmarkCategory>()

    lateinit var allLocationsBookmarkCategory: BookmarkCategory

    lateinit var lastLocation: Pair<Double, Double>

    lateinit var mapFragment: Fragment

    lateinit var currentSelectedBookmarkCategory: BookmarkCategory

    lateinit var currentSelectedLocation: Location

    lateinit var sharedPreferences: SharedPreferences

    var getMyLocationBoolean = true

    var goToLocationBoolean = false

    fun lookupBookmarkCategoryByName(name: String) : BookmarkCategory{
        var bookmarkCategoryToReturn = BookmarkCategory("")

        bookmarkCategoryList.forEach { bookmarkCategory ->
            if (bookmarkCategory.name == name) {
                bookmarkCategoryToReturn = bookmarkCategory
            }
        }

        return bookmarkCategoryToReturn
    }

    fun createBookmarkCategory(name: String) {
        val newBookmarkCategory = BookmarkCategory(name)
        bookmarkCategoryList.add(newBookmarkCategory)

        Save.saveBookmarkCategoryListToSharedPreferences()
    }

    fun editBookmarkCategoryByName(name: String, updatedName: String) : Boolean {

        bookmarkCategoryList.forEach { bookmarkCategory ->
            if (bookmarkCategory.name == name) {
                bookmarkCategory.name = updatedName
                Save.saveBookmarkCategoryListToSharedPreferences()
                return true
            }
        }

        return false
    }

    fun deleteBookmarkCategoryByName(name: String) : Boolean {

        bookmarkCategoryList.forEach { bookmarkCategory ->
            if (bookmarkCategory.name == name) {
                bookmarkCategoryList.remove(bookmarkCategory)
                Save.saveBookmarkCategoryListToSharedPreferences()
                return true
            }
        }

        return false
    }

    fun checkAndRequestPermissions(context: Context): Boolean {
        val internet = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.INTERNET
        )
        val loc = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val loc2 = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (internet != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET)
        }
        if (loc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (loc2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                (context as Activity?)!!,
                listPermissionsNeeded.toTypedArray(),
                1
            )
            return false
        }
        return true
    }

}