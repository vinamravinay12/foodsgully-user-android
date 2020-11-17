package com.foodsgully.foodsgullyuser.utils.listeners

import androidx.appcompat.widget.SearchView

interface  QueryTextChangeListener {
    fun onQueryTextChanged(query : String?)
}

class SearchQueryListener(private  val queryTextChangeListener : QueryTextChangeListener) : SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
       queryTextChangeListener.onQueryTextChanged(newText)
        return false
    }
}