package com.example.mytodolist

import android.app.Application
import com.example.mytodolist.data.NoteDatabase

class MyToDoListApplication : Application(){
    /**
     * Application class creates database when needed
     */
    val database: NoteDatabase by lazy { NoteDatabase.getInstance(this)}
}