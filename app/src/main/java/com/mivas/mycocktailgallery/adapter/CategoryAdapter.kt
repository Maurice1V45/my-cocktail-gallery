package com.mivas.mycocktailgallery.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mivas.mycocktailgallery.R
import com.mivas.mycocktailgallery.listener.MainActivityListener
import kotlinx.android.synthetic.main.list_item_category.view.*

class CategoryAdapter(private val context: Context, private val listener: MainActivityListener) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var selectedPosition = 0
    private val categoryArray = context.resources.getStringArray(R.array.category_array)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_category, parent, false))
    override fun getItemCount() = categoryArray.size + 1
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        with(viewHolder) {
            title.text = if (position == 0) "All" else categoryArray[position - 1]
            parent.isSelected = selectedPosition == position
            parent.setOnClickListener {
                selectedPosition = viewHolder.adapterPosition
                notifyDataSetChanged()
                listener.onCategorySelected(title.text.toString())
            }
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val title = view.title!!
        val parent = view
    }

}