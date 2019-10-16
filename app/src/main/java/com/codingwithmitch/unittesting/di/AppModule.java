package com.codingwithmitch.unittesting.di;

import static com.codingwithmitch.unittesting.persistence.NoteDatabase.DATABASE_NAME;

import android.app.Application;

import com.codingwithmitch.unittesting.persistence.NoteDao;
import com.codingwithmitch.unittesting.persistence.NoteDatabase;
import com.codingwithmitch.unittesting.repository.NoteRepository;

import javax.inject.Singleton;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @Singleton
    @Provides
    static NoteDatabase providesNoteDatabase(Application application) {
        return Room.databaseBuilder(application.getApplicationContext(), NoteDatabase.class, DATABASE_NAME).build();
    }

    @Singleton
    @Provides
    static NoteDao providesNoteDao(NoteDatabase noteDatabase) {
        return noteDatabase.getNoteDao();
    }

    @Singleton
    @Provides
    static NoteRepository providesNoteRepository(NoteDao noteDao) {
        return new NoteRepository(noteDao);
    }
}
