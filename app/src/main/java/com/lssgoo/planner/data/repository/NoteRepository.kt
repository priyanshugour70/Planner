package com.lssgoo.planner.data.repository

import com.lssgoo.planner.data.local.LocalStorageManager
import com.lssgoo.planner.data.model.Note

/**
 * Repository for managing Notes - part of the backend layer
 */
class NoteRepository(private val storage: LocalStorageManager) {

    fun getNotes(): List<Note> {
        return storage.getNotes()
    }

    fun saveNote(note: Note) {
        val notes = storage.getNotes().toMutableList()
        val index = notes.indexOfFirst { it.id == note.id }
        if (index != -1) {
            notes[index] = note.copy(updatedAt = System.currentTimeMillis())
        } else {
            notes.add(0, note)
        }
        storage.saveNotes(notes)
    }

    fun deleteNote(noteId: String) {
        val notes = storage.getNotes().filter { it.id != noteId }
        storage.saveNotes(notes)
    }

    fun toggleNotePin(noteId: String) {
        val notes = storage.getNotes().toMutableList()
        val index = notes.indexOfFirst { it.id == noteId }
        if (index != -1) {
            notes[index] = notes[index].copy(isPinned = !notes[index].isPinned)
            storage.saveNotes(notes)
        }
    }
}
