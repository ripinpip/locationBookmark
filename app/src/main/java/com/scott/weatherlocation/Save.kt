package com.scott.weatherlocation

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Save {

    fun saveBookmarkCategoryListToSharedPreferences() {
        val sharedPreferences: SharedPreferences = DataSingleton.sharedPreferences
        val preferencesEditor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(DataSingleton.bookmarkCategoryList)
        preferencesEditor.putString("BookmarkCategoryList", json)
        preferencesEditor.apply()
    }

    fun getBookmarkCategoryListFromSharedPreferences(): MutableList<BookmarkCategory> {
        val sharedPreferences: SharedPreferences = DataSingleton.sharedPreferences
        val gson = Gson()
        val json: String? = sharedPreferences.getString("BookmarkCategoryList", "")
        val type = object : TypeToken<MutableList<BookmarkCategory>>() {}.type

        var bookmarkCategoryList = mutableListOf<BookmarkCategory>()
        if (json?.isNotEmpty() == true) {
            bookmarkCategoryList = gson.fromJson(json, type)
        }
        return when (json?.isEmpty()){
            true -> mutableListOf()
            false -> bookmarkCategoryList
            null -> mutableListOf()
        }
    }
}