package com.codingwithmitch.unittesting.ui;

import com.codingwithmitch.unittesting.Note;
import com.codingwithmitch.unittesting.repository.NoteRepository;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

public class NotesListViewModel extends ViewModel {

    private static final String TAG = "NotesListViewModel";

    private NoteRepository mNoteRepository;

    private MediatorLiveData<List<Note>> notes = new MediatorLiveData<>();

    @Inject
    public NotesListViewModel(NoteRepository noteRepository) {
        mNoteRepository = noteRepository;
    }

    public LiveData<Resource<Integer>> deleteNote(final Note note) throws Exception{
        return mNoteRepository.deleteNote(note);
    }

    public LiveData<List<Note>> observeNotes(){
        return notes;
    }

    public void getNotes(){
        final LiveData<List<Note>> source = mNoteRepository.getNotes();
        notes.addSource(source, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notesList) {
                if(notesList != null){
                    notes.setValue(notesList);
                }
                notes.removeSource(source);
            }
        });
    }
}
