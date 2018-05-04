package com.gerardogandeaga.cyberlock.database.objects.notes;

import com.gerardogandeaga.cyberlock.database.objects.Note;

import java.util.Scanner;

/**
 * @author gerardogandeaga
 */
public class LoginNote extends Note {
    private String mUrl;
    private String mEmail;
    private String mUsername;
    private String mPassword;
    private String mNotes;

    public LoginNote(Note note) {
        super(note);

        if (getContent() != null) {
            String url = "";
            String email = "";
            String username = "";
            String password = "";
            StringBuilder notes = new StringBuilder();
            //
            Scanner scanner = new Scanner(getContent());

            try {
                url = scanner.nextLine();
                email = scanner.nextLine();
                username = scanner.nextLine();
                password = scanner.nextLine();
                notes.append(scanner.nextLine());
                while (scanner.hasNextLine()) {
                    notes.append("\n");
                    notes.append(scanner.nextLine());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            scanner.close();

            this.mUrl = url;
            this.mEmail = email;
            this.mUsername = username;
            this.mPassword = password;
            this.mNotes = notes.toString();
        }
    }

    // getters

    public String getUrl() {
        return mUrl;
    }
    public String getEmail() {
        return mEmail;
    }
    public String getUsername() {
        return mUsername;
    }
    public String getPassword() {
        return mPassword;
    }
    public String getNotes() {
        return mNotes;
    }


    // setters

    public LoginNote withUrl(String url) {
        mUrl = url;
        return this;
    }
    public LoginNote withEmail(String email) {
        mEmail = email;
        return this;
    }
    public LoginNote withUsername(String username) {
        mUsername = username;
        return this;
    }
    public LoginNote withPassword(String password) {
        mPassword = password;
        return this;
    }
    public LoginNote withNotes(String notes) {
        mNotes = notes;
        return this;
    }

    public Note compile() {
        // format content
        final String format = "%s\n%s\n%s\n%s\n%s";
        withContent(String.format(format, mUrl, mEmail, mUsername, mPassword, mNotes));
        return this;
    }
}
