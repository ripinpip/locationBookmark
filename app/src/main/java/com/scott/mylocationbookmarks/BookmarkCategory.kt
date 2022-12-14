package com.scott.mylocationbookmarks

class BookmarkCategory constructor(var name: String) {

    val locationList = mutableListOf<Location>()

    fun lookupLocationByName(name: String) : Location {
        var locationToReturn = Location("", "", 0.0, 0.0)

        locationList.forEach { location ->
            if (location.name == name) {
                locationToReturn = location
            }
        }

        return locationToReturn
    }

    fun createLocation(name: String, notes: String, latitude: Double, longitude: Double) {
        val newLocation = Location(name, notes, latitude, longitude)
        locationList.add(newLocation)

        Save.saveBookmarkCategoryListToSharedPreferences()
    }

    fun updateLocationByName(name: String, updatedName: String, updatedNotes: String) : Boolean {

        locationList.forEach { location ->
            if (location.name == name) {
                location.name = updatedName
                location.notes = updatedNotes
                Save.saveBookmarkCategoryListToSharedPreferences()

                return true
            }
        }

        return false
    }

    fun deleteLocationByName(name: String) : Boolean {

        if (DataSingleton.currentSelectedBookmarkCategory == DataSingleton.allLocationsBookmarkCategory) {

            val location = DataSingleton.allLocationsBookmarkCategory.lookupLocationByName(name)

            DataSingleton.allLocationsBookmarkCategory.locationList.remove(location)

            DataSingleton.bookmarkCategoryList.forEach { bookmarkCategory ->
                bookmarkCategory.locationList.forEach { location ->
                    if (location.name == name) {
                        bookmarkCategory.locationList.remove(location)

                        Save.saveBookmarkCategoryListToSharedPreferences()

                        return true
                    }
                }
            }

            return true
        }

        else if (DataSingleton.currentSelectedBookmarkCategory != DataSingleton.allLocationsBookmarkCategory) {

            var location = lookupLocationByName(name)
            locationList.remove(location)

            location = DataSingleton.allLocationsBookmarkCategory.lookupLocationByName(name)
            DataSingleton.allLocationsBookmarkCategory.locationList.remove(location)

            Save.saveBookmarkCategoryListToSharedPreferences()

            return true
        }

        return false
    }

}