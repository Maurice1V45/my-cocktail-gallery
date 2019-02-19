package com.mivas.mycocktailgallery

import android.content.Intent
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import com.mivas.mycocktailgallery.adapter.CategoryAdapter
import com.mivas.mycocktailgallery.adapter.CocktailsAdapter
import com.mivas.mycocktailgallery.listener.MainActivityListener
import com.mivas.mycocktailgallery.model.Cocktail
import com.mivas.mycocktailgallery.util.Constants
import com.mivas.mycocktailgallery.util.ConverterUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainActivityListener {

    private lateinit var cocktailsJsonString: String
    private lateinit var cocktails: List<Cocktail>
    private lateinit var cocktailsAdapter: CocktailsAdapter
    private var selectedCategory = "All"

    companion object {
        private const val REQUEST_CODE_ADD_EDIT_COCKTAIL = 1
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START)
        else super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.action_add -> {
                startActivityForResult(Intent(this, AddEditCocktailActivity::class.java).apply {
                    putExtra(Constants.EXTRA_COCKTAILS, cocktailsJsonString)
                }, REQUEST_CODE_ADD_EDIT_COCKTAIL)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cocktailsJsonString = intent.getStringExtra(Constants.EXTRA_COCKTAILS)
        cocktails = ConverterUtils.toObject(cocktailsJsonString).cocktails
        initViews()
        initListeners()
    }

    private fun initViews() {
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.main_activity_navigation_drawer_open, R.string.main_activity_navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        val categoryList = navigationView.getHeaderView(0).findViewById(R.id.categoryList) as RecyclerView
        categoryList.layoutManager = LinearLayoutManager(this)
        categoryList.adapter = CategoryAdapter(this, this)
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        grid.layoutManager = GridLayoutManager(this, if (isLandscape) 3 else 2)
        cocktailsAdapter = CocktailsAdapter(this, this, cocktails)
        grid.adapter = cocktailsAdapter
    }

    private fun initListeners() {
    }

    private fun refreshCocktails() {
        cocktailsAdapter.cocktails = if (selectedCategory == "All") cocktails else cocktails.filter { it.category == selectedCategory }
        cocktailsAdapter.notifyDataSetChanged()
    }

    override fun onCocktailSelected(id: String) {
        startActivityForResult(Intent(this@MainActivity, AddEditCocktailActivity::class.java).apply {
            putExtra(Constants.EXTRA_COCKTAILS, cocktailsJsonString)
            putExtra(Constants.EXTRA_SELECTED_COCKTAIL, id)
        }, REQUEST_CODE_ADD_EDIT_COCKTAIL)
    }

    override fun onCategorySelected(category: String) {
        selectedCategory = category
        refreshCocktails()
        drawerLayout.closeDrawer(Gravity.START)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_EDIT_COCKTAIL && data != null) {
            cocktailsJsonString = data.getStringExtra(Constants.EXTRA_COCKTAILS)
            cocktails = ConverterUtils.toObject(cocktailsJsonString).cocktails
            refreshCocktails()
        }
    }
}
