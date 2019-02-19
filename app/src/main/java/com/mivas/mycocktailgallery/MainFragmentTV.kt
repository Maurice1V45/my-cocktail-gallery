package com.mivas.mycocktailgallery

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v17.leanback.app.BackgroundManager
import android.support.v17.leanback.app.BrowseFragment
import android.support.v17.leanback.widget.ArrayObjectAdapter
import android.support.v17.leanback.widget.HeaderItem
import android.support.v17.leanback.widget.ListRow
import android.support.v17.leanback.widget.ListRowPresenter
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics

import com.mivas.mycocktailgallery.model.Cocktail

class MainFragmentTV(val cocktails: List<Cocktail>) : BrowseFragment() {

    private var defaultBackground: Drawable? = null
    private lateinit var backgroundManager: BackgroundManager
    private lateinit var metrics: DisplayMetrics
    private lateinit var categoryArray: Array<String>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        categoryArray = activity.resources.getStringArray(R.array.category_array)
        prepareBackgroundManager()
        setupUIElements()
        loadRows()
    }

    private fun prepareBackgroundManager() {
        backgroundManager = BackgroundManager.getInstance(activity)
        backgroundManager.attach(activity.window)
        defaultBackground = ContextCompat.getDrawable(activity, R.drawable.tv_background)
        metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
    }

    private fun setupUIElements() {
        title = getString(R.string.app_name)
        // over title
        headersState = BrowseFragment.HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        // set fastLane (or headers) background color
        brandColor = ContextCompat.getColor(activity, R.color.colorPrimary)
        // set search icon color
        searchAffordanceColor = ContextCompat.getColor(activity, R.color.search_opaque)
    }

    private fun loadRows() {
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val cardPresenter = CocktailPresenter()

        for (categoryIndex in 0 until categoryArray.size) {
            val listRowAdapter = ArrayObjectAdapter(cardPresenter)
            val categoryCocktails = cocktails.filter { it.category == categoryArray[categoryIndex] }
            for (cocktailIndex in 0 until categoryCocktails.size) {
                listRowAdapter.add(categoryCocktails[cocktailIndex])
            }
            val header = HeaderItem(categoryIndex.toLong(), categoryArray[categoryIndex])
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }
        adapter = rowsAdapter
    }

}
