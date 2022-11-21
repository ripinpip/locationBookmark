package com.scott.weatherlocation

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
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

}