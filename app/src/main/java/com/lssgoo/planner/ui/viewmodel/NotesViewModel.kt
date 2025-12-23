package com.lssgoo.planner.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.lssgoo.planner.data.local.LocalStorageManager
import com.lssgoo.planner.data.model.Note
import com.lssgoo.planner.data.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Notes feature
 */
class NotesViewModel(application: Application) : BaseViewModel(application) {
    
    private val storageManager = LocalStorageManager(application)
    private val noteRepository = NoteRepository(storageManager)
    
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()
    
    init {
        loadNotes()
    }
    
    fun loadNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            _notes.value = noteRepository.getNotes()
        }
    }
    
    fun addNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.saveNote(note)
            loadNotes()
        }
    }
    
    fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.saveNote(note)
            loadNotes()
        }
    }
    
    fun deleteNote(noteId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.deleteNote(noteId)
            loadNotes()
        }
    }
    
    fun toggleNotePin(noteId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.toggleNotePin(noteId)
            loadNotes()
        }
    }
}
