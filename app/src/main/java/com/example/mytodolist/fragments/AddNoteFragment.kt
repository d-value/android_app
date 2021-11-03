package com.example.mytodolist.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.mytodolist.MyToDoListApplication
import com.example.mytodolist.R
import com.example.mytodolist.databinding.FragmentAddNoteBinding
import com.example.mytodolist.viewmodels.NotesViewModel
import com.example.mytodolist.viewmodels.NotesViewModelFactory
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class AddNoteFragment : Fragment(R.layout.fragment_add_note) {

    private val viewModel: NotesViewModel by activityViewModels {
        NotesViewModelFactory(
            (activity?.application as MyToDoListApplication).database.noteDao()
        )
    }

    private lateinit var binding: FragmentAddNoteBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddNoteBinding.bind(view)

        //Restores Note's data
        setViews()

        viewModel.hasTime.observe(this.viewLifecycleOwner) {
            showTimeViews(it)
        }

        binding.timeSwitch.setOnClickListener {
            viewModel.noteToAdd.hasTime = binding.timeSwitch.isChecked
            viewModel.showTime(binding.timeSwitch.isChecked)
        }

        binding.noteContentTextView.doAfterTextChanged {
            if (viewModel.isEntryValid(it.toString())) {
                viewModel.noteToAdd.content = it.toString()
            }
        }

        binding.timeTextView.doAfterTextChanged {
            viewModel.noteToAdd.time = it.toString()
        }

        binding.notifySwitch.setOnClickListener {
            viewModel.noteToAdd.hasNotification = binding.notifySwitch.isChecked
        }

        binding.timeButton.setOnClickListener {
            openTimePicker()
        }

        binding.addButton.setOnClickListener {
            addNote()
        }

        binding.cancelButton.setOnClickListener {
            cancelNote()
        }
    }

    /**
     * Open TimePicker
     */
    private fun openTimePicker() {
        val hours = viewModel.noteToAdd.time.substring(0, 2).toInt()
        val minutes = viewModel.noteToAdd.time.substring(3).toInt()
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(hours)
            .setMinute(minutes)
            .setTitleText("Set Time")
            .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
            .build()
        picker.show(childFragmentManager, "TAG")
        setupTimePicker(picker)
    }

    /**
     * Add PositiveButtonClickListener to the TimePicker
     */
    private fun setupTimePicker(picker: MaterialTimePicker){
        picker.addOnPositiveButtonClickListener{
            val hours =
                if(picker.hour < 10) "0${picker.hour}"
                else picker.hour.toString()

            val minute =
                if(picker.minute < 10) "0${picker.minute}"
                else picker.minute.toString()

            val time = "$hours:$minute"

            binding.timeTextView.text = time
        }
    }

    /**
     * Setup views with NoteToAdd's data
     */
    private fun setViews() {
        binding.noteContentTextView.setText(viewModel.noteToAdd.content)
        viewModel.showTime(viewModel.noteToAdd.hasTime)
        binding.timeSwitch.isChecked = viewModel.noteToAdd.hasTime
        binding.notifySwitch.isChecked = viewModel.noteToAdd.hasNotification
        binding.timeTextView.text = viewModel.noteToAdd.time
    }

    private fun cancelNote() {
        findNavController().popBackStack()
    }

    private fun addNote() {
        if (viewModel.isEntryValid(binding.noteContentTextView.text.toString())) {
            viewModel.insert(viewModel.noteToAdd)
            viewModel.showTime(false)
            findNavController().popBackStack()
        } else {
            Toast.makeText(context, "Note content is empty!", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Make Time options visible if Note has time
     */
    private fun showTimeViews(isShown: Boolean) {
        val viewVisibility =
            if (isShown) View.VISIBLE
            else View.GONE

        binding.notifySwitch.visibility = viewVisibility
        binding.timeTextView.visibility = viewVisibility
        binding.timeButton.visibility = viewVisibility
    }
}