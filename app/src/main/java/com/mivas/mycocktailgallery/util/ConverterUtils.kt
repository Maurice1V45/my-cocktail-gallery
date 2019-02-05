package com.mivas.mycocktailgallery.util

import com.google.gson.Gson
import com.mivas.mycocktailgallery.model.CocktailsJson

object ConverterUtils {

    fun toObject(json: String) = Gson().fromJson(json, CocktailsJson::class.java)
    fun toJson(obj: CocktailsJson) = Gson().toJson(obj)
}