package com.scott.mylocationbookmarks

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class FragmentMaps : Fragment() {

    private lateinit var appNavigator: AppNavigator

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appNavigator = context as AppNavigator
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        // set toolbar title
        (activity as AppCompatActivity?)?.supportActionBar?.title = "Map"

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map)
        if (mapFragment != null) {
            DataSingleton.mapFragment = mapFragment
        }

    }

    override fun onResume() {
        super.onResume()
        if (!DataSingleton.goToLocationBoolean) {
            DataSingleton.getMyLocationBoolean = true
        }
        appNavigator.getMapAsync()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.fragment_maps_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return when (item.itemId) {
            R.id.getMyLocation -> {
                DataSingleton.getMyLocationBoolean = true
                appNavigator.getMapAsync()
                true
            }
            R.id.openInGoogleMaps -> {
                val stringUri = "http://maps.google.com/maps?q=loc:" + DataSingleton.lastLocation.first + "," + DataSingleton.lastLocation.second + " (" + "Select Location" + ")"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(stringUri))
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity")
                startActivity(intent)
                true
            }
            R.id.addIcon -> {
                val bookmarkCategoryAlertDialogView = inflater.inflate(R.layout.dialog_bookmark_location, null, false)

                val nameOfLocationEditText = bookmarkCategoryAlertDialogView.findViewById<EditText>(R.id.nameOfLocationEditText)
                val locationNotesEditText = bookmarkCategoryAlertDialogView.findViewById<EditText>(R.id.locationNotesEditText)
                val bookmarkCategorySpinner = bookmarkCategoryAlertDialogView.findViewById<Spinner>(R.id.bookmarkCategorySpinner)

                val bookmarkCategorySpinnerOptions = mutableListOf<String>()
                DataSingleton.bookmarkCategoryList.forEach { bookmarkCategory ->
                    bookmarkCategorySpinnerOptions.add(bookmarkCategory.name)
                }

                val bookmarkCategorySpinnerArrayAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, bookmarkCategorySpinnerOptions)
                bookmarkCategorySpinner.adapter = bookmarkCategorySpinnerArrayAdapter

                val bookmarkLocationAlertDialog = AlertDialog.Builder(requireContext())
                    .setTitle("Bookmark Location")
                    .setView(bookmarkCategoryAlertDialogView)
                    .setNeutralButton("Cancel") { dialog, _ ->
                        dialog.cancel()
                    }
                    .setPositiveButton("Save") { _, _ ->
                        val selectedBookmarkCategory = DataSingleton.lookupBookmarkCategoryByName(bookmarkCategorySpinner.selectedItem.toString())

                        selectedBookmarkCategory.createLocation(nameOfLocationEditText.text.toString(), locationNotesEditText.text.toString(), DataSingleton.lastLocation.first, DataSingleton.lastLocation.second)

                        if (selectedBookmarkCategory != DataSingleton.allLocationsBookmarkCategory) {
                            DataSingleton.allLocationsBookmarkCategory.createLocation(nameOfLocationEditText.text.toString(), locationNotesEditText.text.toString(), DataSingleton.lastLocation.first, DataSingleton.lastLocation.second)
                        }
                    }
                bookmarkLocationAlertDialog.show()

                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}