package com.codingwithmitch.unittesting;

import android.database.sqlite.SQLiteConstraintException;

import com.codingwithmitch.unittesting.util.LiveDataTestUtil;
import com.codingwithmitch.unittesting.util.TestUtil;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

public class NoteDaoTest extends NoteDatabaseTest {
    public static final String TEST_TITLE = "TEST_TITLE";
    public static final String TEST_CONTENT = "TEST_CONTENT";
    public static final String TEST_TIMESTAMP = "08-2018";

    /**
     * 1) Insert, Read, Delete
     *
     * 2) Insert, Read, Update, Delete
     *
     * 3) Insert null title Note, throw Exception
     *
     * 4) Insert, Update with null title, throw Exception
     *
     * Need to define a special rule when using JUnit4 when you're trying to test things that run on a background thread
     */
    @Rule
    public InstantTaskExecutorRule mRule = new InstantTaskExecutorRule();



    @Test
    public void insertReadDelete() throws Exception {
        Note note = new Note(TestUtil.TEST_NOTE_1);

        /**
         * Since we're using RxJava we need to call blockingGet(); to wait until that note is inserted
         */
        //insert
        getNoteDao().insertNote(note).blockingGet();

        //read
        LiveDataTestUtil<List<Note>> listLiveDataTestUtil = new LiveDataTestUtil<>();
        List<Note> insertedNotes = listLiveDataTestUtil.getValue(getNoteDao().getNotes());

        Assert.assertNotNull(insertedNotes);
        Assert.assertEquals(note.getTitle(), insertedNotes.get(0).getTitle());
        Assert.assertEquals(note.getContent(), insertedNotes.get(0).getContent());
        Assert.assertEquals(note.getTimestamp(), insertedNotes.get(0).getTimestamp());

        note.setId(insertedNotes.get(0).getId());
        Assert.assertEquals(note, insertedNotes.get(0));

        //delete
        getNoteDao().deleteNote(note).blockingGet();

        insertedNotes = listLiveDataTestUtil.getValue(getNoteDao().getNotes());
        Assert.assertEquals(0, insertedNotes.size());
    }

    @Test
    public void insertReadUpdateDelete() throws Exception {
        Note note = new Note(TestUtil.TEST_NOTE_1);

        /**
         * Since we're using RxJava we need to call blockingGet(); to wait until that note is inserted
         */
        //insert
        getNoteDao().insertNote(note).blockingGet();

        //read
        LiveDataTestUtil<List<Note>> listLiveDataTestUtil = new LiveDataTestUtil<>();
        List<Note> insertedNotes = listLiveDataTestUtil.getValue(getNoteDao().getNotes());

        Assert.assertNotNull(insertedNotes);
        Assert.assertEquals(note.getTitle(), insertedNotes.get(0).getTitle());
        Assert.assertEquals(note.getContent(), insertedNotes.get(0).getContent());
        Assert.assertEquals(note.getTimestamp(), insertedNotes.get(0).getTimestamp());

        note.setId(insertedNotes.get(0).getId());
        Assert.assertEquals(note, insertedNotes.get(0));

        //update
        note.setTitle(TEST_TITLE);
        note.setContent(TEST_CONTENT);
        note.setTimestamp(TEST_TIMESTAMP);
        getNoteDao().updateNote(note).blockingGet();

        insertedNotes = listLiveDataTestUtil.getValue(getNoteDao().getNotes());
        Assert.assertEquals(note.getTitle(), insertedNotes.get(0).getTitle());
        Assert.assertEquals(note.getContent(), insertedNotes.get(0).getContent());
        Assert.assertEquals(note.getTimestamp(), insertedNotes.get(0).getTimestamp());

        note.setId(insertedNotes.get(0).getId());
        Assert.assertEquals(note, insertedNotes.get(0));

        //delete
        getNoteDao().deleteNote(note).blockingGet();

        insertedNotes = listLiveDataTestUtil.getValue(getNoteDao().getNotes());
        Assert.assertEquals(0, insertedNotes.size());
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insert_nullTitle_throwSQLiteConstraintExecption() throws Exception {
        final Note note = new Note(TestUtil.TEST_NOTE_1);
        note.setTitle(null);

        getNoteDao().insertNote(note).blockingGet();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void update_nullTitle_throwSQLiteConstraintExecption() throws Exception {
        Note note = new Note(TestUtil.TEST_NOTE_1);

        getNoteDao().insertNote(note).blockingGet();

        LiveDataTestUtil<List<Note>> listLiveDataTestUtil = new LiveDataTestUtil<>();
        List<Note> insertedNotes = listLiveDataTestUtil.getValue(getNoteDao().getNotes());
        Assert.assertNotNull(insertedNotes);

        note = new Note(insertedNotes.get(0));
        note.setTitle(null);
        getNoteDao().updateNote(note).blockingGet();
    }
}
