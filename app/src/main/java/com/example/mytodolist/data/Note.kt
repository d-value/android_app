package com.example.mytodolist.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
data class Note(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "note_content")
    var content: String,

    @ColumnInfo(name = "note_done")
    var done: Boolean,

    @ColumnInfo(name = "note_has_time")
    var hasTime: Boolean,

    @ColumnInfo(name = "note_time")
    var time: String,

    @ColumnInfo(name = "note_notify")
    var hasNotification: Boolean
)
