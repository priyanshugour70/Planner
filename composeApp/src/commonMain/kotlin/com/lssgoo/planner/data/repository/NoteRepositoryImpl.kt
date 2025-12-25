package com.lssgoo.planner.data.repository

import com.lssgoo.planner.data.local.AppStorageRepository
import com.lssgoo.planner.features.notes.models.Note
import com.lssgoo.planner.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NoteRepositoryImpl(
    private val localDataSource: AppStorageRepository
) : NoteRepository {

    override fun getNotes(): Flow<Resource<List<Note>>> = flow {
        emit(Resource.Loading)
        try {
            val notes = localDataSource.getNotes().filter { !it.isDeleted }
            emit(Resource.Success(notes))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    override suspend fun getNote(id: String): Resource<Note> {
        return try {
            val note = localDataSource.getNotes().find { it.id == id && !it.isDeleted }
            if (note != null) Resource.Success(note) else Resource.Error(Exception("Note not found"))
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun saveNote(note: Note): Resource<Boolean> {
        return try {
            if (localDataSource.getNotes().any { it.id == note.id }) {
                localDataSource.updateNote(note)
            } else {
                localDataSource.addNote(note)
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun deleteNote(id: String): Resource<Boolean> {
        return try {
            val note = localDataSource.getNotes().find { it.id == id }
            if (note != null) {
                localDataSource.updateNote(note.copy(isDeleted = true))
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}
