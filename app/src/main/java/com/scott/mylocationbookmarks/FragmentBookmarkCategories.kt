package com.scott.mylocationbookmarks

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class FragmentBookmarkCategories : Fragment() {

    private lateinit var appNavigator: AppNavigator

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appNavigator = context as AppNavigator
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bookmark_categories, container, false)

        // set toolbar title
        (activity as AppCompatActivity?)?.supportActionBar?.title = "Bookmark Categories"

        val bookmarkCategoriesListView = view.findViewById<ListView>(R.id.bookmarkCategoriesListView)

        val bookmarkCategoryList = mutableListOf<String>()

        DataSingleton.bookmarkCategoryList.forEach { bookmarkCategory ->
            bookmarkCategoryList.add(bookmarkCategory.name)
        }

        val arrayAdapter: ArrayAdapter<*> = ArrayAdapter(requireContext(), R.layout.list_view_item, bookmarkCategoryList)
        bookmarkCategoriesListView.adapter = arrayAdapter

        bookmarkCategoriesListView.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String

            val selectedBookmarkCategory = DataSingleton.lookupBookmarkCategoryByName(selectedItem)
            DataSingleton.currentSelectedBookmarkCategory = selectedBookmarkCategory

            appNavigator.navigateToFragment(FragmentLocationBookmarks(), true)
        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.fragment_bookmark_categories_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return when (item.itemId) {
            R.id.addIcon -> {
                val addBookmarkCategoryAlertDialogView = inflater.inflate(R.layout.dialog_create_bookmark_category, null, false)

                val nameOfCategoryEditText = addBookmarkCategoryAlertDialogView.findViewById<EditText>(R.id.nameOfCategoryEditText)

                val addBookmarkCategoryAlertDialog = AlertDialog.Builder(requireContext())
                    .setTitle("Bookmark Location")
                    .setView(addBookmarkCategoryAlertDialogView)
                    .setNeutralButton("Cancel") { dialog, _ ->
                        dialog.cancel()
                    }
                    .setPositiveButton("Save") { _, _ ->
                        if (DataSingleton.lookupBookmarkCategoryByName(nameOfCategoryEditText.text.toString()).name == "" &&
                            nameOfCategoryEditText.text.isNotEmpty()) {

                            DataSingleton.createBookmarkCategory(nameOfCategoryEditText.text.toString())
                            appNavigator.navigateToFragment(FragmentBookmarkCategories(), false)

                            Toast.makeText(requireContext(), "Category saved!", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            Toast.makeText(requireContext(), "Category " + nameOfCategoryEditText.text.toString() + " already exists!", Toast.LENGTH_SHORT).show()
                        }
                    }
                addBookmarkCategoryAlertDialog.show()

                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

}