package com.tiensinoakuma.rikaipic.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tiensinoakuma.rikaipic.R
import kotlinx.android.synthetic.main.view_translation_item.view.*
import java.util.*

class TranslationAdapter(val listener: TranslationListener) : RecyclerView.Adapter<TranslationAdapter.TranslationViewHolder>() {
    private val translations: MutableList<String> = ArrayList()

    override fun onBindViewHolder(holder: TranslationViewHolder, position: Int) {
        holder.setTranslation(translations[position])
    }

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int): TranslationViewHolder {
        return TranslationViewHolder(LayoutInflater
                .from(parent.context).inflate(R.layout.view_translation_item, parent, false))
    }

    override fun getItemCount(): Int {
        return translations.size
    }

    inner class TranslationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                listener.onTranslationClicked(adapterPosition)
            }
        }

        fun setTranslation(translation: String) {
            itemView.text.text = translation
        }
    }

    interface TranslationListener {
        fun onTranslationClicked(position: Int)
    }

    fun setLabels(labels: List<String>) {
        translations.clear()
        translations.addAll(labels)
        notifyDataSetChanged()
    }

    fun clear() {
        val size = translations.size
        translations.clear()
        notifyItemRangeRemoved(0, size)
    }
}
