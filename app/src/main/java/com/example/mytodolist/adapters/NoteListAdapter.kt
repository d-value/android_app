package com.example.mytodolist.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.OnReceiveContentListener
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodolist.R
import com.example.mytodolist.data.Note
import com.example.mytodolist.databinding.NoteItemViewBinding

class NoteListAdapter(private val onNoteListener: OnNoteListener): ListAdapter<Note, NoteListAdapter.NoteHolder>(NotesDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item_view, parent, false)
        return NoteHolder(view, onNoteListener)
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        holder.bind(this.getItem(position))
    }

    class NoteHolder(view: View, private val onNoteListener: OnNoteListener): RecyclerView.ViewHolder(view){

        private val binding = NoteItemViewBinding.bind(view)
        private lateinit var item: Note

        fun bind(note: Note){
            item = note
            binding.noteContentTextView.text = note.content
            binding.noteDoneCheckBox.isChecked = note.done

            if (item.hasTime) {
                binding.noteTimeTextView.text = note.time
                if (note.hasNotification) {
                    with(binding.notifyImageView) {
                        setImageResource(R.drawable.ic_notifications_on_24)
                        isVisible = true
                    }
                } else if (!note.hasNotification) {
                    with(binding.notifyImageView) {
                        setImageResource(R.drawable.ic_notifications_off_24)
                        isVisible = true
                    }
                }
            } else {
                binding.notifyImageView.isGone = true
                binding.noteTimeTextView.isGone = true
            }

            setListeners()
        }

        private fun setListeners(){
            binding.noteItemLayout.setOnClickListener{
                onNoteListener.onNoteClick(item, binding.noteItemLayout)
            }
            binding.notifyImageView.setOnClickListener{
                onNoteListener.onNoteClick(item, binding.notifyImageView)
            }
            binding.noteDoneCheckBox.setOnClickListener {
                onNoteListener.onNoteClick(item, binding.noteDoneCheckBox)
            }
        }
    }
}

interface OnNoteListener{
    fun onNoteClick(note: Note, view: View)
}

class NotesDiffCallback: DiffUtil.ItemCallback<Note>(){
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem == newItem
    }
}