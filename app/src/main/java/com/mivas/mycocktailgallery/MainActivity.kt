package com.mivas.mycocktailgallery

import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.mivas.mycocktailgallery.adapter.CocktailsAdapter
import com.mivas.mycocktailgallery.model.Cocktail
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val cocktails = listOf(Cocktail(123, "", ""),
        Cocktail(123, "", ""),
        Cocktail(123, "", ""),
        Cocktail(123, "", ""),
        Cocktail(123, "", ""),
        Cocktail(123, "", ""))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        grid.layoutManager = GridLayoutManager(this, if (isLandscape) 3 else 2)
        grid.adapter = CocktailsAdapter(this, cocktails)

    }
}
