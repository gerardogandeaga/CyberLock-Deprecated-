package com.gerardogandeaga.cyberlock;

import android.content.Context;

import com.gerardogandeaga.cyberlock.database.DBFolderAccessor;
import com.gerardogandeaga.cyberlock.database.objects.Folder;

/**
 * @author gerardogandeaga
 */
public class Initial {
    private Context mContext;

    public Initial(Context context) {
        this.mContext = context;
    }

    /**
     * creates the main folder for all notes
     */
    public void setupFolder() {
        // setup the main folder for the notes
        Folder folder = new Folder()
                .setColourTag("DEFAULT")
                .setName("MAIN")
                .setSize("");

        // insert folder to db
        DBFolderAccessor folderAccessor = DBFolderAccessor.getInstance(mContext);
        folderAccessor.open();
        folderAccessor.save(folder);
        folderAccessor.close();
    }
}
