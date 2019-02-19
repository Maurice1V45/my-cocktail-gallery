package com.mivas.mycocktailgallery

import android.app.Activity
import android.os.Bundle
import com.mivas.mycocktailgallery.util.Constants
import com.mivas.mycocktailgallery.util.ConverterUtils


class MainActivityTV : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_tv)

        val cocktailsJsonString = intent.getStringExtra(Constants.EXTRA_COCKTAILS)
        val cocktails = ConverterUtils.toObject(cocktailsJsonString).cocktails
        val fragment = MainFragmentTV(cocktails)
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
