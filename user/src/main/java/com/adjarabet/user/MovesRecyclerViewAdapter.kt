package com.adjarabet.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MovesRecyclerViewAdapter(dataSet: Array<String>) :
    RecyclerView.Adapter<MovesRecyclerViewAdapter.ViewHolder>() {
    private val localData: MutableList<String> = dataSet.toMutableList()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recycler_view_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(localData[position])
    }

    override fun getItemCount() = localData.size

    fun addItem(stringElem: String) {
        localData.add(stringElem)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.findViewById(R.id.recycler_view_item_text_view)

        fun bind(elem: String) {
            val playerIndex = layoutPosition % 2 + 1
            textView.text = "${layoutPosition + 1}) PL-$playerIndex: $elem"
        }
    }
}
