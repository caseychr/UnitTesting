package com.codingwithmitch.unittesting;

import com.codingwithmitch.unittesting.persistence.NoteDao;
import com.codingwithmitch.unittesting.persistence.NoteDatabase;

import org.junit.After;
import org.junit.Before;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

/**
 * Since NoteDatabase is abstract this test class is abstract
 *
 * Why are we using JUnit4 instead of JUnit5? JUnit5 requires Java 8 which means Android 8.0 (Oreo). So
 * if the device is below Oreo JUnit5 will not work.
 */
public abstract class NoteDatabaseTest {

    // system under test
    private NoteDatabase mNoteDatabase;

    public NoteDao getNoteDao() {
        return mNoteDatabase.getNoteDao();
    }

    @Before
    public void init() {
        mNoteDatabase = Room.inMemoryDatabaseBuilder(ApplicationProvider
                .getApplicationContext(), NoteDatabase.class).build();
    }

    @After
    public void finish() {
        mNoteDatabase.close();
    }
}
