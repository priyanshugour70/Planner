package com.lssgoo.planner.data.repository

import com.lssgoo.planner.features.notes.models.Note
import com.lssgoo.planner.util.Resource
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotes(): Flow<Resource<List<Note>>>
    suspend fun getNote(id: String): Resource<Note>
    suspend fun saveNote(note: Note): Resource<Boolean>
    suspend fun deleteNote(id: String): Resource<Boolean>
}
