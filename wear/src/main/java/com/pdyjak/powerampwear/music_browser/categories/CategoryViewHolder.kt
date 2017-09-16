package com.pdyjak.powerampwear.music_browser.categories

import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.pdyjak.powerampwear.R
import com.pdyjak.powerampwear.music_browser.ItemViewHolder

internal class CategoryViewHolder(itemView: View) : ItemViewHolder(itemView) {

    private val mIcon: ImageView = itemView.findViewById(R.id.icon) as ImageView
    private val mName: TextView = itemView.findViewById(R.id.name) as TextView

    override fun bindWith(obj: Any?) {
        (obj as CategoryItem).let { item ->
            itemView.setOnClickListener { item.onClicked() }
            mIcon.setImageResource(item.iconId)
            mName.text = itemView.context.getText(item.stringId)
        }
    }
}
