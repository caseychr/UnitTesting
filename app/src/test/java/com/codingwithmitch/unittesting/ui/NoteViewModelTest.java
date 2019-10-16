package com.codingwithmitch.unittesting.ui;

import com.codingwithmitch.unittesting.Note;
import com.codingwithmitch.unittesting.repository.NoteRepository;
import com.codingwithmitch.unittesting.ui.note.NoteViewModel;
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

import io.reactivex.Flowable;
import io.reactivex.internal.operators.single.SingleToFlowable;

/**
 * Notice here how often the Resource class is used to measure against returned data to see if it worked. Definitely
 * want to consider using it.
 *
 * When we initially run these tests they fail. We are using LiveData here so everything is on a background thread!!!
 * We create InstanceExecutorExtension to deal with this and annotate with @ExtendWith(InstanceExecutorExtension.class)
 */
@ExtendWith(InstanceExecutorExtension.class)
public class NoteViewModelTest {

    private NoteViewModel mNoteViewModel;

    @Mock
    private NoteRepository mNoteRepository;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        mNoteViewModel = new NoteViewModel(mNoteRepository);
    }

    @Test
    public void observeEmptyNoteWhenNoteSet() throws Exception {
        LiveDataTestUtil<Note> liveDataTestUtil = new LiveDataTestUtil<>();

        Note note = liveDataTestUtil.getValue(mNoteViewModel.observeNote());

        Assertions.assertNull(note);
    }

    @Test
    public void observeNote_whenSet() throws Exception {
        Note note = new Note(TestUtil.TEST_NOTE_1);
        LiveDataTestUtil<Note> liveDataTestUtil = new LiveDataTestUtil<>();
        mNoteViewModel.setNote(note);
        Note observedNote = liveDataTestUtil.getValue(mNoteViewModel.observeNote());
        Assertions.assertEquals(note, observedNote);
    }

    @Test
    public void insertNote_returnRow() throws Exception {
        Note note = new Note(TestUtil.TEST_NOTE_1);
        LiveDataTestUtil<Resource<Integer>> liveDataTestUtil = new LiveDataTestUtil<>();
        final int insertedRow = 1;
        Flowable<Resource<Integer>> returnedData = SingleToFlowable.just(Resource.success(insertedRow, NoteRepository.INSERT_SUCCESS));
        Mockito.when(mNoteRepository.insertNote(Mockito.any(Note.class))).thenReturn(returnedData);

        mNoteViewModel.setNote(note);
        Resource<Integer> returnedValue = liveDataTestUtil.getValue(mNoteViewModel.insertNote());

        Assertions.assertEquals(Resource.success(insertedRow, NoteRepository.INSERT_SUCCESS), returnedValue);
    }

    @Test
    public void updateNote_returnRow() throws Exception {
        Note note = new Note(TestUtil.TEST_NOTE_1);
        LiveDataTestUtil<Resource<Integer>> liveDataTestUtil = new LiveDataTestUtil<>();
        final int updatedRow = 1;
        Flowable<Resource<Integer>> returnedData = SingleToFlowable.just(Resource.success(updatedRow, NoteRepository.UPDATE_SUCCESS));
        Mockito.when(mNoteRepository.updatedNote(Mockito.any(Note.class))).thenReturn(returnedData);

        mNoteViewModel.setNote(note);
        mNoteViewModel.setIsNewNote(false);
        Resource<Integer> returnedValue = liveDataTestUtil.getValue(mNoteViewModel.updateNote());

        Assertions.assertEquals(Resource.success(updatedRow, NoteRepository.UPDATE_SUCCESS), returnedValue);
    }

    @Test
    public void dontReturnInsertRowWithoutObserver() throws Exception {
        Note note = new Note(TestUtil.TEST_NOTE_1);
        mNoteViewModel.setNote(note);
        Mockito.verify(mNoteRepository, Mockito.never()).insertNote(Mockito.any(Note.class));
    }

    @Test
    public void dontReturnUpdateRowWithoutObserver() throws Exception {
        Note note = new Note(TestUtil.TEST_NOTE_1);
        mNoteViewModel.setNote(note);
        Mockito.verify(mNoteRepository, Mockito.never()).updatedNote(Mockito.any(Note.class));
    }

    @Test
    public void setNote_nullTitle_throwException()  throws Exception {
        final Note note = new Note(TestUtil.TEST_NOTE_1);
        note.setTitle(null);
        Assertions.assertThrows(Exception.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                mNoteViewModel.setNote(note);
            }
        });
    }

    @Test
    public void saveNote_shouldAllowSave_returnFalse()  throws Exception {
        final Note note = new Note(TestUtil.TEST_NOTE_1);
        note.setContent(null);

        mNoteViewModel.setNote(note);
        mNoteViewModel.setIsNewNote(true);

        Exception exception = Assertions.assertThrows(Exception.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                mNoteViewModel.saveNote();
            }
        });

        Assertions.assertEquals(NoteViewModel.NO_CONTENT_ERROR, exception.getMessage());
    }
}
