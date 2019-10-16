package com.codingwithmitch.unittesting.ui.note;

import com.codingwithmitch.unittesting.Note;
import com.codingwithmitch.unittesting.repository.NoteRepository;
import com.codingwithmitch.unittesting.ui.Resource;
import com.codingwithmitch.unittesting.util.DateUtil;

import org.reactivestreams.Subscription;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.functions.Consumer;

public class NoteViewModel extends ViewModel {
    private static final String TAG = "NoteViewModel";
    public static final String NO_CONTENT_ERROR = "Can't save not with no content";

    public enum ViewState {VIEW, EDIT}

    private final NoteRepository mNoteRepository;

    private final MutableLiveData<Note> note = new MutableLiveData<>();
    private final MutableLiveData<ViewState> mViewState = new MutableLiveData<>();
    private boolean isNewNote;
    private Subscription updateSubscription, insertSubscription;

    @Inject
    public NoteViewModel(NoteRepository noteRepository) {
        mNoteRepository = noteRepository;
    }

    /**
     * We don't need to pass Note in the Resource here since we already have that in the MutableLiveData<Note> object
     * above
     *
     * By setting the subscription to class variables we can control what happens with them i.e. cancel, set null, etc
     * @return
     */
    public LiveData<Resource<Integer>> insertNote() throws Exception{
        //RxJava -> It converts an RxObservable object to a LiveData object
        return LiveDataReactiveStreams.fromPublisher(mNoteRepository.insertNote(note.getValue())
        .doOnSubscribe(new Consumer<Subscription>() {
            @Override
            public void accept(Subscription subscription) throws Exception {
                insertSubscription = subscription;
            }
        }));
    }

    public LiveData<Resource<Integer>> updateNote() throws Exception{
        //RxJava -> It converts an RxObservable object to a LiveData object
        return LiveDataReactiveStreams.fromPublisher(mNoteRepository.updatedNote(note.getValue())
                .doOnSubscribe(new Consumer<Subscription>() {
            @Override
            public void accept(Subscription subscription) throws Exception {
                updateSubscription = subscription;
            }
        }));
    }

    public LiveData<Note> observeNote() {
        return note;
    }

    public LiveData<ViewState> observeViewState() {
        return mViewState;
    }

    public void setViewState(ViewState viewState) {
        this.mViewState.setValue(viewState);
    }

    public void setIsNewNote(boolean newNote) {
        isNewNote = newNote;
    }

    public LiveData<Resource<Integer>> saveNote() throws Exception {
        if(!shouldAllowSave()) {
            throw new Exception(NO_CONTENT_ERROR);
        }
        cancelPendingTransactions();
        return new NoteInsertUpdateHelper<Integer>() {
            @Override
            public void setNoteId(int noteId) {
                isNewNote = false;
                Note currentNote = note.getValue();
                currentNote.setId(noteId);
                note.setValue(currentNote);
            }

            @Override
            public LiveData<Resource<Integer>> getAction() throws Exception {
                if(isNewNote) {
                    return insertNote();
                } else {
                    return updateNote();
                }
            }

            @Override
            public String defineAction() {
                if(isNewNote) {
                    return ACTION_INSERT;
                } else {
                    return ACTION_UPDATE;
                }
            }

            @Override
            public void onTransactionComplete() {
                updateSubscription = null;
                insertSubscription = null;
            }
        }.getAsLiveData();
    }

    public void cancelPendingTransactions() {
        if(insertSubscription != null) {
            cancelInsertTransaction();
        }
        if(updateSubscription != null) {
            cancelUpdateTransaction();
        }
    }

    public void cancelUpdateTransaction() {
        updateSubscription.cancel();
        updateSubscription = null;
    }

    public void cancelInsertTransaction() {
        insertSubscription.cancel();
        insertSubscription = null;
    }

    private boolean shouldAllowSave() throws Exception {
        try {
            return removeWhiteSpace(note.getValue().getContent()).length() > 0;
        } catch (NullPointerException e) {
            throw new Exception(NO_CONTENT_ERROR);
        }
    }

    public void updateNote(String title, String content) throws Exception{
        if(title == null || title.equals("")){
            throw new NullPointerException("Title can't be null");
        }
        String temp = removeWhiteSpace(content);
        if(temp.length() > 0){
            Note updatedNote = new Note(note.getValue());
            updatedNote.setTitle(title);
            updatedNote.setContent(content);
            updatedNote.setTimestamp(DateUtil.getCurrentTimeStamp());

            note.setValue(updatedNote);
        }
    }

    private String removeWhiteSpace(String string){
        string = string.replace("\n", "");
        string = string.replace(" ", "");
        return string;
    }

    public void setNote(Note note) throws Exception {
        if(note.getTitle() == null || note.getTitle().equals("")) {
            throw new Exception(NoteRepository.NOTE_TITLE_NULL);
        }
        this.note.setValue(note);
    }

    public boolean shouldNavigateBack(){
        if(mViewState.getValue() == ViewState.VIEW){
            return true;
        }
        else{
            return false;
        }
    }
}
