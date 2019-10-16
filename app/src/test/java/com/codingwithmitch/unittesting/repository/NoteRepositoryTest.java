package com.codingwithmitch.unittesting.repository;

import static com.codingwithmitch.unittesting.repository.NoteRepository.DELETE_FAILURE;
import static com.codingwithmitch.unittesting.repository.NoteRepository.DELETE_SUCCESS;
import static com.codingwithmitch.unittesting.repository.NoteRepository.INSERT_FAILURE;
import static com.codingwithmitch.unittesting.repository.NoteRepository.INSERT_SUCCESS;
import static com.codingwithmitch.unittesting.repository.NoteRepository.INVALID_NOTE_ID;
import static com.codingwithmitch.unittesting.repository.NoteRepository.UPDATE_FAILURE;
import static com.codingwithmitch.unittesting.repository.NoteRepository.UPDATE_SUCCESS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.codingwithmitch.unittesting.Note;
import com.codingwithmitch.unittesting.persistence.NoteDao;
import com.codingwithmitch.unittesting.ui.Resource;
import com.codingwithmitch.unittesting.util.InstanceExecutorExtension;
import com.codingwithmitch.unittesting.util.LiveDataTestUtil;
import com.codingwithmitch.unittesting.util.TestUtil;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import io.reactivex.Single;
@ExtendWith(InstanceExecutorExtension.class)
public class NoteRepositoryTest {

    public static final Note NOTE1 = new Note(TestUtil.TEST_NOTE_1);

    private NoteRepository mNoteRepository;

    /**
     * We need NoteDao to test NoteRepository BUT we cannot use a real object of NoteDao. We need to use Mockito!!!
     */
    @Mock
    private NoteDao mNoteDao;

    /**
     * If we used @BeforeAll it would get called just once for all tests. We use @BeforeEach to have a fresh set of
     * instantiated objects for each test. Also if you use @BeforeAll you would need to add
     * @TestInstance(TestInstance.Lifecycle.PER_CLASS) to the test class.
     */

    @BeforeEach
    public void initEach() {
        /**
         * Searches the class for everything annotated with @Mock and then builds those mocks. We can also do ourselves
         * like this:
         * mNoteDao = Mockito.mock(NoteDao.class);
         */
        MockitoAnnotations.initMocks(this);
        mNoteRepository = new NoteRepository(mNoteDao);
    }

    @Test
    void insertNote_returnRow() throws Exception {
        // This is all defining the thing that's going to be returned
        final Long insertedRow = 1L;
        final Single<Long> returnedData = Single.just(insertedRow);
        /**
         * we're defining what happens to the mock objects when the note is inserted. We're saying, "When a note is
         * inserted into the database return the data in returnedData.
          */
        when(mNoteDao.insertNote(any(Note.class))).thenReturn(returnedData);

        // Act
        /**
         * We call blockingFirst since we're calling it as a Flowable in the class and we want to convert it into a data type first
          */
        final Resource<Integer> returnedValue = mNoteRepository.insertNote(NOTE1).blockingFirst();

        // Assert
        verify(mNoteDao).insertNote(any(Note.class));
        /**
         * Basically telling Mockito nothing else is happening here. We're done.
         */
        verifyNoMoreInteractions(mNoteDao);
        System.out.println("Returned Data: "+returnedValue.data);
        assertEquals(Resource.success(1, INSERT_SUCCESS), returnedValue);

        /**
         * OR.... test using RxJava. Much more concise but the other is more readable and understanding of what
         * is being tested.
         */
        mNoteRepository.insertNote(NOTE1).test().await().assertValue(Resource.success(1, INSERT_SUCCESS));
    }

    @Test
    void insertNote_returnFailure() throws Exception {
        // This is all defining the thing that's going to be returned
        final Long failedInsertRow = -1L;
        final Single<Long> returnedData = Single.just(failedInsertRow);
        /**
         * we're defining what happens to the mock objects when the note is inserted. We're saying, "When a note is
         * inserted into the database return the data in returnedData.
         */
        when(mNoteDao.insertNote(any(Note.class))).thenReturn(returnedData);

        // Act
        /**
         * We call blockingFirst since we're calling it as a Flowable in the class and we want to convert it into a data type first
         */
        final Resource<Integer> returnedValue = mNoteRepository.insertNote(NOTE1).blockingFirst();

        // Assert
        verify(mNoteDao).insertNote(any(Note.class));
        /**
         * Basically telling Mockito nothing else is happening here. We're done.
         */
        verifyNoMoreInteractions(mNoteDao);
        System.out.println("Returned Data: "+returnedValue.data);
        assertEquals(Resource.error(null, INSERT_FAILURE), returnedValue);
    }

    @Test
    void insertNote_nullTitle_throwException() throws Exception {
        assertThrows(Exception.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                final Note note = new Note(NOTE1);
                note.setTitle(null);
                mNoteRepository.insertNote(note);
            }
        });
    }

    @Test
    void updateNote_returnNumRowsUpdated() throws Exception {
        final int updatedRow = 1;
        Mockito.when(mNoteDao.updateNote(Mockito.any(Note.class))).thenReturn(Single.just(updatedRow));

        final Resource<Integer> returnedValue = mNoteRepository.updatedNote(NOTE1).blockingFirst();

        Mockito.verify(mNoteDao).updateNote(Mockito.any(Note.class));
        verifyNoMoreInteractions(mNoteDao);

        assertEquals(Resource.success(updatedRow, UPDATE_SUCCESS), returnedValue);
    }

    @Test
    void updateNote_returnFailure() throws Exception {
        final int failedInsert = -1;
        final Single<Integer> returnedData = Single.just(failedInsert);
        Mockito.when(mNoteDao.updateNote(Mockito.any(Note.class))).thenReturn(returnedData);

        final Resource<Integer> returnedValue = mNoteRepository.updatedNote(NOTE1).blockingFirst();

        Mockito.verify(mNoteDao).updateNote(Mockito.any(Note.class));
        verifyNoMoreInteractions(mNoteDao);

        assertEquals(Resource.error(null, UPDATE_FAILURE), returnedValue);
    }

    @Test
    void updateNote_nullTitle_throwException() throws Exception {
        assertThrows(Exception.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                final Note note = new Note(NOTE1);
                note.setTitle(null);
                mNoteRepository.updatedNote(note);
            }
        });
    }

    @Test
    void deleteNote_nullId_throwException() throws Exception {
        Exception exception = assertThrows(Exception.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                final Note note = new Note(TestUtil.TEST_NOTE_1);
                note.setId(-1);
                mNoteRepository.deleteNote(note);
            }
        });

        assertEquals(INVALID_NOTE_ID, exception.getMessage());
    }

    /*
        delete note
        delete success
        return Resource.success with deleted row
     */

    @Test
    void deleteNote_deleteSuccess_returnResourceSuccess() throws Exception {
        // Arrange
        final int deletedRow = 1;
        Resource<Integer> successResponse = Resource.success(deletedRow, DELETE_SUCCESS);
        LiveDataTestUtil<Resource<Integer>> liveDataTestUtil = new LiveDataTestUtil<>();
        when(mNoteDao.deleteNote(any(Note.class))).thenReturn(Single.just(deletedRow));

        // Act
        Resource<Integer> observedResponse = liveDataTestUtil.getValue(mNoteRepository.deleteNote(NOTE1));

        // Assert
        assertEquals(successResponse, observedResponse);
    }


    /*
        delete note
        delete failure
        return Resource.error
     */
    @Test
    void deleteNote_deleteFailure_returnResourceError() throws Exception {
        // Arrange
        final int deletedRow = -1;
        Resource<Integer> errorResponse = Resource.error(null, DELETE_FAILURE);
        LiveDataTestUtil<Resource<Integer>> liveDataTestUtil = new LiveDataTestUtil<>();
        when(mNoteDao.deleteNote(any(Note.class))).thenReturn(Single.just(deletedRow));

        // Act
        Resource<Integer> observedResponse = liveDataTestUtil.getValue(mNoteRepository.deleteNote(NOTE1));

        // Assert
        assertEquals(errorResponse, observedResponse);
    }


    /*
        retrieve notes
        return list of notes
     */

    @Test
    void getNotes_returnListWithNotes() throws Exception {
        // Arrange
        List<Note> notes = TestUtil.TEST_NOTES_LIST;
        LiveDataTestUtil<List<Note>> liveDataTestUtil = new LiveDataTestUtil<>();
        MutableLiveData<List<Note>> returnedData = new MutableLiveData<>();
        returnedData.setValue(notes);
        when(mNoteDao.getNotes()).thenReturn(returnedData);

        // Act
        List<Note> observedData = liveDataTestUtil.getValue(mNoteRepository.getNotes());

        // Assert
        assertEquals(notes, observedData);
    }

    /*
        retrieve notes
        return empty list
     */

    @Test
    void getNotes_returnEmptyList() throws Exception {
        // Arrange
        List<Note> notes = new ArrayList<>();
        LiveDataTestUtil<List<Note>> liveDataTestUtil = new LiveDataTestUtil<>();
        MutableLiveData<List<Note>> returnedData = new MutableLiveData<>();
        returnedData.setValue(notes);
        when(mNoteDao.getNotes()).thenReturn(returnedData);

        // Act
        List<Note> observedData = liveDataTestUtil.getValue(mNoteRepository.getNotes());

        // Assert
        assertEquals(notes, observedData);
    }
}
