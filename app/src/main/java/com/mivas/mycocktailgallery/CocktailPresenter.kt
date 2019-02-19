/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.mivas.mycocktailgallery

import android.graphics.drawable.Drawable
import android.support.v17.leanback.widget.ImageCardView
import android.support.v17.leanback.widget.Presenter
import android.support.v4.content.ContextCompat
import android.view.ViewGroup

import com.bumptech.glide.Glide
import com.mivas.mycocktailgallery.model.Cocktail
import com.squareup.picasso.Picasso
import kotlin.properties.Delegates

class CocktailPresenter : Presenter() {
    private var defaultCardImage: Drawable? = null
    private var selectedBackgroundColor: Int by Delegates.notNull()
    private var defaultBackgroundColor: Int by Delegates.notNull()

    companion object {
        private val CARD_WIDTH = 400
        private val CARD_HEIGHT = 400
    }

    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {

        defaultBackgroundColor = ContextCompat.getColor(parent.context, R.color.black)
        selectedBackgroundColor = ContextCompat.getColor(parent.context, R.color.selected_background)
        defaultCardImage = ContextCompat.getDrawable(parent.context, R.drawable.movie)

        val cardView = object : ImageCardView(parent.context) {
            override fun setSelected(selected: Boolean) {
                updateCardBackgroundColor(this, selected)
                super.setSelected(selected)
            }
        }

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        updateCardBackgroundColor(cardView, false)
        return Presenter.ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
        val cocktail = item as Cocktail
        val cardView = viewHolder.view as ImageCardView

        cardView.titleText = cocktail.title
        cardView.contentText = cocktail.ingredients
        cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT)
        Picasso.get()
            .load("https://drive.google.com/thumbnail?id=${cocktail.id}")
            .resize(1000, 1000)
            .centerCrop()
            .into(cardView.mainImageView)
    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {
        val cardView = viewHolder.view as ImageCardView
        // Remove references to images so that the garbage collector can free up memory
        cardView.badgeImage = null
        cardView.mainImage = null
    }

    private fun updateCardBackgroundColor(view: ImageCardView, selected: Boolean) {
        val color = if (selected) selectedBackgroundColor else defaultBackgroundColor
        // Both background colors should be set because the view's background is temporarily visible
        // during animations.
        view.setBackgroundColor(color)
        view.setInfoAreaBackgroundColor(color)
    }

}