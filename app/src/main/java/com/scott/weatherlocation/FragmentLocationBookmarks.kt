package com.scott.weatherlocation

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class FragmentLocationBookmarks : Fragment() {

    private lateinit var appNavigator: AppNavigator

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appNavigator = context as AppNavigator
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_location_bookmarks, container, false)

        // set toolbar title
        (activity as AppCompatActivity?)?.supportActionBar?.title = DataSingleton.currentSelectedBookmarkCategory.name

        val locationListView = view.findViewById<ListView>(R.id.locationListView)

        val currentBookmarkCategory = DataSingleton.currentSelectedBookmarkCategory

        val locationList = mutableListOf<String>()

        currentBookmarkCategory.locationList.forEach { location ->
            locationList.add(location.name)
        }

        val arrayAdapter: ArrayAdapter<*> = ArrayAdapter(requireContext(), R.layout.list_view_item, locationList)
        locationListView.adapter = arrayAdapter

        locationListView.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String

            val selectedLocation = currentBookmarkCategory.lookupLocationByName(selectedItem)
            DataSingleton.currentSelectedLocation = selectedLocation

            val locationBookmarkAlertDialogView = inflater.inflate(R.layout.dialog_location_bookmark, null, false)

            val nameOfLocationEditText = locationBookmarkAlertDialogView.findViewById<EditText>(R.id.nameOfLocationEditText)
            val latitudeTextView = locationBookmarkAlertDialogView.findViewById<TextView>(R.id.latitudeTextView)
            val longitudeTextView = locationBookmarkAlertDialogView.findViewById<TextView>(R.id.longitudeTextView)

            nameOfLocationEditText.setText(selectedLocation.name)
            latitudeTextView.text = selectedLocation.latitude.toString()
            longitudeTextView.text = selectedLocation.longitude.toString()

            val locationBookmarkAlertDialog = AlertDialog.Builder(requireContext())
                .setTitle(selectedLocation.name)
                .setView(locationBookmarkAlertDialogView)
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
                .setNeutralButton("View on Map") { _, _ ->
                    DataSingleton.goToLocationBoolean = true
                    appNavigator.getMapAsync()
                    appNavigator.navigateToFragment(FragmentMaps(), false)
                }
                .setPositiveButton("Save") { _, _ ->
                    currentBookmarkCategory.updateLocationByName(selectedLocation.name, nameOfLocationEditText.text.toString())
                    appNavigator.navigateToFragment(FragmentLocationBookmarks(), false)
                }
            locationBookmarkAlertDialog.show()

        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.fragment_location_bookmarks_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return when (item.itemId) {
            R.id.editCategory -> {
                val editBookmarkCategoryAlertDialogView = inflater.inflate(R.layout.dialog_edit_bookmark_category, null, false)

                val nameOfCategoryEditText = editBookmarkCategoryAlertDialogView.findViewById<EditText>(R.id.nameOfCategoryEditText)

                nameOfCategoryEditText.setText(DataSingleton.currentSelectedBookmarkCategory.name)

                val editBookmarkCategoryAlertDialog = AlertDialog.Builder(requireContext())
                    .setTitle("Edit Category")
                    .setView(editBookmarkCategoryAlertDialogView)
                    .setNeutralButton("Cancel") { dialog, _ ->
                        dialog.cancel()
                    }
                    .setPositiveButton("Save") { _, _ ->
                        DataSingleton.editBookmarkCategoryByName(DataSingleton.currentSelectedBookmarkCategory.name, nameOfCategoryEditText.text.toString())
                        appNavigator.navigateToFragment(FragmentLocationBookmarks(), false)
                    }

                editBookmarkCategoryAlertDialog.show()

                true
            }
            R.id.deleteCategory -> {
                val deleteBookmarkCategoryAlertDialog = AlertDialog.Builder(requireContext())
                    .setTitle("Delete Category")
                    .setMessage("Are you sure you want to delete this category?")
                    .setNeutralButton("Cancel") { dialog, _ ->
                        dialog.cancel()
                    }
                    .setPositiveButton("Delete") { _, _ ->
                        DataSingleton.deleteBookmarkCategoryByName(DataSingleton.currentSelectedBookmarkCategory.name)
                        appNavigator.navigateToFragment(FragmentBookmarkCategories(), false)
                    }

                deleteBookmarkCategoryAlertDialog.show()

                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

}