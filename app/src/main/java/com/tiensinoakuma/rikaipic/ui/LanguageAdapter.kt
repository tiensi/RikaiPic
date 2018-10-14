package com.tiensinoakuma.rikaipic.ui

import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tiensinoakuma.rikaipic.R
import kotlinx.android.synthetic.main.view_translation_item.view.*

class LanguageAdapter(val listener: LanguageListener, var selectedLanguage: String, private val languages: List<String>) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    private var selectedLanguagePos = -1
    var isClickable = false

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        holder.setLanguage(languages[position])
    }

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int): LanguageViewHolder {
        return LanguageViewHolder(LayoutInflater
                .from(parent.context).inflate(R.layout.view_language_item, parent, false))
    }

    override fun getItemCount(): Int {
        return languages.size
    }

    inner class LanguageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var selectedBackgroundDrawable: Drawable?

        init {
            itemView.setOnClickListener {
                if (isClickable) {
                    selectedLanguage = itemView.text.text.toString()
                    notifyItemChanged(selectedLanguagePos)
                    selectedLanguagePos = adapterPosition
                    notifyItemChanged(selectedLanguagePos)
                    listener.onLanguageClicked(selectedLanguage)
                }
            }
            selectedBackgroundDrawable = ContextCompat.getDrawable(itemView.context, R.drawable.selected_border)
        }

        fun setLanguage(language: String) {
            itemView.text.text = language
            if (language == selectedLanguage) {
                selectedLanguagePos = adapterPosition
                itemView.background = selectedBackgroundDrawable
            } else {
                itemView.background = null
            }
        }
    }

    interface LanguageListener {
        fun onLanguageClicked(language: String)
    }
}
