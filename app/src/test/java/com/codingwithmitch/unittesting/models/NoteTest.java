package com.codingwithmitch.unittesting.models;

import com.codingwithmitch.unittesting.Note;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NoteTest {
    public static final String TIMESTAMP_1 = "05-2019";
    public static final String TIMESTAMP_2 = "0-2019";

    /**
     * TODO Compare 2 equal notes
     */
    @Test
    void isNotesEqual_identicalProperties_returnTrue() {
        // Arrange
        Note note1 = new Note("Note #1", "This is note #1", TIMESTAMP_1);
        note1.setId(1);
        Note note2 = new Note("Note #1", "This is note #1", TIMESTAMP_1);
        note2.setId(1);

        // Act

        // Assert
        Assertions.assertEquals(note1, note2);
        System.out.println("The notes are equal!"); // If assertion returns true this will be printed. Otherwise no.

    }
    /**
     * TODO Compare 2 notes with 2 different ids
     */
    @Test
    void isNotesEqual_differentIds_returnTrue() throws Exception {
        // Arrange
        Note note1 = new Note("Note #1", "This is note #1", TIMESTAMP_1);
        note1.setId(1);
        Note note2 = new Note("Note #1", "This is note #1", TIMESTAMP_1);
        note2.setId(2);

        // Act

        // Assert
        Assertions.assertNotEquals(note1, note2);
        System.out.println("The notes are NOT equal!");
    }
    /**
     * TODO Compare 2 notes with different timestamps
     */
    @Test
    void isNotesEqual_differentTimeStamps_returnTrue() throws Exception {
        // Arrange
        Note note1 = new Note("Note #1", "This is note #1", TIMESTAMP_1);
        note1.setId(1);
        Note note2 = new Note("Note #1", "This is note #1", TIMESTAMP_2);
        note2.setId(1);

        // Act

        // Assert
        Assertions.assertEquals(note1, note2);
        System.out.println("The notes are equal!");
    }
    /**
     * TODO Compare 2 notes with different titles
     */
    @Test
    void isNotesEqual_differentTitles_returnFalse() throws Exception {
        // Arrange
        Note note1 = new Note("Note #1", "This is note #1", TIMESTAMP_1);
        note1.setId(1);
        Note note2 = new Note("Note #2", "This is note #1", TIMESTAMP_1);
        note2.setId(1);

        // Act

        // Assert
        Assertions.assertNotEquals(note1, note2);
        System.out.println("The notes are NOT equal!");
    }
    /**
     * TODO Compare 2 notes with different content
     */
    @Test
    void isNotesEqual_differentContent_returnFalse() throws Exception {
        // Arrange
        Note note1 = new Note("Note #1", "This is note #1", TIMESTAMP_1);
        note1.setId(1);
        Note note2 = new Note("Note #1", "This is note #2", TIMESTAMP_1);
        note2.setId(1);

        // Act

        // Assert
        Assertions.assertNotEquals(note1, note2);
        System.out.println("The notes are NOT equal!");
    }
}
