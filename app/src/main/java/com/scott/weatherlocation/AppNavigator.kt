package com.scott.weatherlocation

import androidx.fragment.app.Fragment

interface AppNavigator {
    fun navigateToFragment(inputFragment: Fragment, addToBackStack: Boolean)
    fun getMapAsync()
}