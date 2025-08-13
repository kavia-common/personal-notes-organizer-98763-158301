package org.example.app.ui

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import org.example.app.R
import org.example.app.model.Note

/**
 * Adapter that renders notes in a simple card list with title, preview and tags.
 */
class NoteListAdapter(
    private val onClick: (Note) -> Unit
) : ListAdapter<Note, NoteListAdapter.NoteVH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteVH(v, onClick)
    }

    override fun onBindViewHolder(holder: NoteVH, position: Int) {
        holder.bind(getItem(position))
    }

    class NoteVH(itemView: View, private val onClick: (Note) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val card: MaterialCardView = itemView.findViewById(R.id.card)
        private val title: TextView = itemView.findViewById(R.id.title)
        private val preview: TextView = itemView.findViewById(R.id.preview)
        private val chipGroup: ChipGroup = itemView.findViewById(R.id.chips)

        fun bind(note: Note) {
            title.text = note.title
            val plain = Html.fromHtml(note.contentHtml, Html.FROM_HTML_MODE_COMPACT).toString()
            preview.text = if (plain.length > 140) plain.substring(0, 140) + "…" else plain

            chipGroup.removeAllViews()
            note.tags.take(3).forEach {
                val chip = Chip(chipGroup.context).apply {
                    text = it
                    isClickable = false
                    isCheckable = false
                }
                chipGroup.addView(chip)
            }

            card.setOnClickListener { onClick(note) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean =
                oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean =
                oldItem == newItem
        }
    }
}
