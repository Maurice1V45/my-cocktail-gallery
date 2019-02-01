package com.mivas.mycocktailgallery

import android.content.Intent
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.google.gson.Gson
import com.mivas.mycocktailgallery.adapter.CocktailsAdapter
import com.mivas.mycocktailgallery.model.Cocktail
import com.mivas.mycocktailgallery.model.Cocktails
import com.mivas.mycocktailgallery.util.Constants
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var cocktailsJson: String
    private lateinit var cocktails: List<Cocktail>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cocktailsJson = intent.getStringExtra(Constants.EXTRA_COCKTAILS)
        cocktails = Gson().fromJson(cocktailsJson, Cocktails::class.java).cocktails
        initViews()
        initListeners()
    }

    private fun initViews() {
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        grid.layoutManager = GridLayoutManager(this, if (isLandscape) 3 else 2)
        grid.adapter = CocktailsAdapter(this, cocktails)
    }

    private fun initListeners() {
        addButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddCocktailActivity::class.java).apply {
                putExtra(Constants.EXTRA_COCKTAILS, cocktailsJson)
            })
        }
    }
}
