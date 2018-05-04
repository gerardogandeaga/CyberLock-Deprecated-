package com.gerardogandeaga.cyberlock.database.objects.notes;

import com.gerardogandeaga.cyberlock.database.objects.Note;

import java.util.Scanner;

/**
 * @author gerardogandeaga
 */
public class GenericNote extends Note {
    private String mNotes;

    public GenericNote(Note note) {
        super(note);

        if (getContent() != null) {
            StringBuilder notes = new StringBuilder();
            Scanner scanner = new Scanner(getContent());

            notes.append(scanner.nextLine());
            while (scanner.hasNextLine()) {
                notes.append("\n");
                notes.append(scanner.nextLine());
            }
            scanner.close();

            this.mNotes = notes.toString();
        }
    }

    // getter

    public GenericNote withNotes(String notes) {
        this.mNotes = notes;
        return this;
    }

    // setter

    public String getNotes() {
        return mNotes;
    }

    public Note compile() {
        // format content
        final String format = "%s";
        withContent(String.format(format, mNotes));

        return this;
    }
}
