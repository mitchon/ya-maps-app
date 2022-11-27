package com.komarov.ya_maps_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SearchResultsRecyclerAdapter(
    private val results: ArrayList<String>,
    private val onItemClick: (position: Int) -> Unit
): RecyclerView.Adapter<SearchResultsRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val result: TextView = itemView.findViewById(R.id.searchResultName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_results_list_item_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.result.text = results[position]
        holder.itemView.setOnClickListener{
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int = results.size
}

