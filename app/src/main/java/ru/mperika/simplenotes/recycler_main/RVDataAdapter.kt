package ru.mperika.simplenotes.recycler_main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.note_card.view.*
import ru.mperika.simplenotes.R
import ru.mperika.simplenotes.data_source.Note

class RVDataAdapter : RecyclerView.Adapter<RVDataAdapter.ViewHolder> {

    private var inflater: LayoutInflater
    private var notesList: List<Note>
    private var context: Context

    constructor(context: Context, notes: List<Note>) {
        this.notesList = notes
        this.inflater = LayoutInflater.from(context)
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View = inflater.inflate(R.layout.note_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note: Note = notesList[position]
        holder.imageView.setImageURI(note.imageURI)
        holder.headerTV.text = note.noteHeader
        holder.bodyTV.text = note.noteText
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.noteIV
        val headerTV: TextView = itemView.headerTextView
        val bodyTV: TextView = itemView.bodyTextView
    }
}