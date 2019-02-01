package com.mivas.mycocktailgallery.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.mivas.mycocktailgallery.R
import com.mivas.mycocktailgallery.model.Cocktail
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.grid_item_cocktail.view.*

class CocktailsAdapter(private val context: Context, private val cocktails: List<Cocktail>) : RecyclerView.Adapter<CocktailsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(context).inflate(R.layout.grid_item_cocktail, parent, false))
    override fun getItemCount() = cocktails.size
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val cocktail = cocktails[position]
        with(viewHolder) {
            Picasso.get()
                .load("https://drive.google.com/thumbnail?id=${cocktail.id}")
                .resize(250, 250)
                .centerCrop()
                .into(image)
            title.text = cocktail.title
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val image = view.image!!
        val title = view.title!!
        val parent = view
    }

}