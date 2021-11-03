package com.example.mytodolist.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.mytodolist.MyToDoListApplication
import com.example.mytodolist.R
import com.example.mytodolist.adapters.NoteListAdapter
import com.example.mytodolist.adapters.OnNoteListener
import com.example.mytodolist.data.Note
import com.example.mytodolist.databinding.FragmentNotesBinding
import com.example.mytodolist.viewmodels.DisplayType
import com.example.mytodolist.viewmodels.NotesViewModel
import com.example.mytodolist.viewmodels.NotesViewModelFactory
import com.example.mytodolist.viewmodels.SortType

class NotesFragment : Fragment(R.layout.fragment_notes), OnNoteListener {

    private val viewModel : NotesViewModel by activityViewModels {
        NotesViewModelFactory(
            (activity?.application as MyToDoListApplication).database.noteDao()
        )
    }

    private lateinit var binding: FragmentNotesBinding
    private var adapter = NoteListAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNotesBinding.bind(view)

        binding.noteRecyclerView.adapter = adapter

        // Observe notes changes
        viewModel.allNotes.observe(this.viewLifecycleOwner){
            adapter.submitList(it)
        }

        viewModel.sortType.observe(this.viewLifecycleOwner){
            updateList()
        }

        viewModel.displayType.observe(this.viewLifecycleOwner){
            updateList()
        }

        binding.floatingActionButton.setOnClickListener {
            startAddFragment()
        }

        setHasOptionsMenu(true)
    }

    /**
     * Create UpBar menus
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.app_bar_menu, menu)
    }

    /**
     * Remove Notes observers and Update List Adapter with new data
     */
    private fun updateList(){
        viewModel.allNotes.removeObservers(this.viewLifecycleOwner)
        viewModel.getNotes()
        viewModel.allNotes.observe(this.viewLifecycleOwner){
            adapter.submitList(it)
        }
    }

    /**
     * Catch clicks on menu items
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.clearSubItem -> viewModel.clear()
            R.id.deleteDoneItem -> viewModel.deleteDoneNote()
            R.id.hideDoneItem -> {
                if(item.isChecked){
                    viewModel.displayType.value = DisplayType.ALL
                }else{
                    viewModel.displayType.value = DisplayType.UNDONE
                }
                item.isChecked = !item.isChecked
            }
            R.id.sortByNameItem -> viewModel.sortType.value = SortType.BY_NAME
            R.id.sortByTimeItem -> viewModel.sortType.value = SortType.BY_TIME
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Catch clicks on NoteItem's views
     */
    override fun onNoteClick(note: Note, view: View) {
        when(view.id){
            R.id.notifyImageView -> {
                note.hasNotification = !note.hasNotification
                if(view is ImageView){
                    if(note.hasNotification) {
                        view.setImageResource(R.drawable.ic_notifications_on_24)
                    }else view.setImageResource(R.drawable.ic_notifications_off_24)
                }
                viewModel.update(note)
            }
            R.id.noteDoneCheckBox -> {
                note.done = !note.done
                viewModel.update(note)
            }
            R.id.noteItemLayout -> {
                startEditFragment(note)
            }
        }
    }

    private fun startAddFragment() {
        viewModel.clearNoteToAdd()
        viewModel.showTime(false)
        findNavController().navigate(R.id.action_notesFragment_to_addNoteFragment)
    }

    private fun startEditFragment(note: Note) {
        viewModel.noteToEdit = note.copy()
        viewModel.showTime(false)
        findNavController().navigate(R.id.action_notesFragment_to_editNoteFragment)
    }
}