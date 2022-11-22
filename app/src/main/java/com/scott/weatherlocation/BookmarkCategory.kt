package com.scott.weatherlocation

class BookmarkCategory constructor(var name: String) {

    val locationList = mutableListOf<Location>()

    fun lookupLocationByName(name: String) : Location {
        var locationToReturn = Location("", 0.0, 0.0)

        locationList.forEach { location ->
            if (location.name == name) {
                locationToReturn = location
            }
        }

        return locationToReturn
    }

    fun createLocation(name: String, latitude: Double, longitude: Double) {
        val newLocation = Location(name, latitude, longitude)
        locationList.add(newLocation)
    }

    fun updateLocationByName(name: String, updatedName: String) : Boolean {

        locationList.forEach { location ->
            if (location.name == name) {
                location.name = updatedName
                return true
            }
        }

        return false
    }

}