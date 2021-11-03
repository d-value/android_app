package com.example.mytodolist.data


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: Note)

    @Delete
    suspend fun delete(vararg note: Note)

    @Update
    suspend fun update(note: Note)

    @Query("SELECT * FROM note_table ORDER BY note_content")
    fun getAllByName(): Flow<List<Note>>

    @Query("SELECT * FROM note_table ORDER BY note_time ASC")
    fun getAllByTime(): Flow<List<Note>>

    @Query("SELECT * FROM note_table WHERE note_done = '0' ORDER BY note_content ASC")
    fun getUndoneByName(): Flow<List<Note>>

    @Query("SELECT * FROM note_table WHERE note_done = '0' ORDER BY note_time ASC")
    fun getUndoneByTime(): Flow<List<Note>>

    @Query("DELETE FROM note_table")
    suspend fun clear()

    @Query("DELETE FROM note_table WHERE note_done = '1'")
    suspend fun deleteDoneNotes()
}