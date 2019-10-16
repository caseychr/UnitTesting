package com.codingwithmitch.unittesting.di;

import com.codingwithmitch.unittesting.ui.NoteActivity;
import com.codingwithmitch.unittesting.ui.NotesListActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActvityBuildersModule {

    @ContributesAndroidInjector
    abstract NotesListActivity contributeNotesListActivity();

    @ContributesAndroidInjector
    abstract NoteActivity contributeNoteActivity();

}
