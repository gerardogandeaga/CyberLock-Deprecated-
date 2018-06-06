package com.gerardogandeaga.cyberlock.core.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.core.dialogs.ColourPaletteDialogFragment;
import com.gerardogandeaga.cyberlock.core.dialogs.FolderSelectDialogFragment;
import com.gerardogandeaga.cyberlock.core.fragments.CardEditFragment;
import com.gerardogandeaga.cyberlock.core.fragments.EditFragment;
import com.gerardogandeaga.cyberlock.core.fragments.LoginEditFragment;
import com.gerardogandeaga.cyberlock.core.fragments.NoteEditFragment;
import com.gerardogandeaga.cyberlock.database.DBNoteAccessor;
import com.gerardogandeaga.cyberlock.database.objects.Folder;
import com.gerardogandeaga.cyberlock.database.objects.Note;
import com.gerardogandeaga.cyberlock.interfaces.RequestResponder;
import com.gerardogandeaga.cyberlock.utils.Graphics;
import com.gerardogandeaga.cyberlock.utils.Pref;

import butterknife.ButterKnife;

/**
 * @author gerardogandeaga
 *
 * streamlined edit class that contains 3 fragments for the note, card and login info edit.
 * this class handle the global variables between all types, as well as saving to the db.
 * this class extends the core activity meaning all major security features come with
 * this class
 */
enum NoteType {
    NOTE, CARD, LOGIN
}
public class NoteEditActivity extends CoreActivity implements RequestResponder, ColourPaletteDialogFragment.ColourSelectionCallBack, FolderSelectDialogFragment.FolderSelectionCallback {
    private static final String TAG = "NoteEditActivity";

    private NoteType enum_type;

    // fragments
    private FragmentManager mFragmentManager;

    private EditFragment mEditFragment;
    private NoteEditFragment mNoteEditFragment;
    private CardEditFragment mCardEditFragment;
    private LoginEditFragment mLoginEditFragment;

    // edit
    private boolean mSaveFlag;
    private boolean mIsAutoSave;
    private Note mNote;

    // note object
    private String mCurrentFolder;
    private String mColourTag;

    // quick and dirty mode switch var
    private Menu mMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_container_static_toolbar);
        bindView();

        // fragments
        this.mFragmentManager = getFragmentManager();

        this.mNoteEditFragment = new NoteEditFragment();
        this.mCardEditFragment = new CardEditFragment();
        this.mLoginEditFragment = new LoginEditFragment();

        // edit variables
        this.mSaveFlag = true;
        this.mIsAutoSave = Pref.getAutoSave(this);

        initializeNoteObject();

        // launch a fragment
        startEditor();

        setupActionBar(null, null, NO_ICON);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindView() {
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_edit, menu);
        mMenu = menu;

        // change colour filter of icons
        Graphics.BasicFilter.mutateMenuItems(menu);

        if (!mNote.getColourTag().equals("default")) {
            mutateMenuTagIcon();
        }

        return true;
    }

    private void mutateMenuTagIcon() {
        setActionBarBackgroundColour(Graphics.ColourTags.colourTagToolbar(this, mColourTag));
        Graphics.BasicFilter.mutateMenuItems(mMenu, R.color.white);
    }

    private void initializeNoteObject() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            // if the the note is already saved the db and being edited
            this.mNote = (Note) bundle.get("data");


            // if the note is new
            if (mNote == null) { // *** because note object bundle is null
                this.mNote = new Note().withType(bundle.getString("type"));
            }

            // general properties
            // folder
            this.mCurrentFolder = (mNote.isNew() ? bundle.getString("folder") : mNote.getFolder());
            this.mColourTag = mNote.getColourTag();

            // remove bundles
            bundle.remove("data");
            bundle.remove("type");
            bundle.remove("folder");

            switch (mNote.getType()) {
                case Note.GENERIC:
                    this.enum_type = NoteType.NOTE;
                    break;

                case Note.CARD:
                    this.enum_type = NoteType.CARD;
                    break;

                case Note.LOGIN:
                    this.enum_type = NoteType.LOGIN;
                    break;
            }
        }
    }

    private void startEditor() {
        // fragment transaction "manager"
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        // bundle of the note to send to the fragment
        Bundle noteBundle = new Bundle();
        noteBundle.putSerializable("data", mNote);

        switch (enum_type) {
            case NOTE:
                fragmentTransaction.add(R.id.fragment_container, newFragment(mNoteEditFragment, noteBundle));
                this.mEditFragment = mNoteEditFragment;
                break;

            case CARD:
                fragmentTransaction.add(R.id.fragment_container, newFragment(mCardEditFragment, noteBundle));
                this.mEditFragment = mCardEditFragment;
                break;

            case LOGIN:
                fragmentTransaction.add(R.id.fragment_container, newFragment(mLoginEditFragment, noteBundle));
                this.mEditFragment = mLoginEditFragment;
                break;
        }

        // start fragment
        fragmentTransaction.commit();
    }

    /**
     * start the new fragment with arguments added to it
     * @param fragment edit fragment that will be activated
     * @param bundle note object in the bundle
     * @return modified fragment
     */
    private Fragment newFragment(Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_trash:
                trashNote();
                break;

            case R.id.menu_folder:
                FolderSelectDialogFragment.show(this, mCurrentFolder);
                break;

            case R.id.menu_colour_tag:
                ColourPaletteDialogFragment.show(this);
                break;

            // toggle view mode between read only and edit modes
//            case R.id.menu_view_mode:
//                switch (enum_type) {
//                    case NOTE:
//                        mNoteEditFragment.toggleViewMode();
//                        break;
//
//                    case CARD:
//                        mCardEditFragment.toggleViewMode();
//                        break;
//
//                    case LOGIN:
//                        mLoginEditFragment.toggleViewMode();
//                        break;
//                }
//                item.setChecked(mEditFragment.isReadOnly());
//                CustomToast.buildAndShowToast(this,
//                        (mEditFragment.isReadOnly() ?
//                        "Read only mode active, Cannot edit"
//                        :
//                        "Edit mode active")
//                );
//                break;

            // send save request to fragment
            case R.id.menu_save:
                requestSave();
                break;

            case R.id.menu_cancel:
                cancelNote();
                break;
        }
        return true;
    }

    /**
     * uses a fragment save() method to invoke a save response from a fragment,
     * effectively requesting for the fragment to send its "compiled" note object to this activity
     * for saving or updating
     */
    public void requestSave() {
        switch (enum_type) {
            case NOTE:
                mNoteEditFragment.save();
                break;
            case CARD:
                mCardEditFragment.save();
                break;
            case LOGIN:
                mLoginEditFragment.save();
                break;
            default:
                Log.e(TAG, "requestSave: edit type was not properly specified");
                break;
        }
    }

    private void requestUpdatedNoteObject() {
        switch (enum_type) {
            case NOTE:
                mNoteEditFragment.update();
                break;
            case CARD:
                mCardEditFragment.update();
                break;
            case LOGIN:
                mLoginEditFragment.update();
                break;
            default:
                Log.e(TAG, "requestSave: edit type was not properly specified");
                break;
        }
    }

    /**
     * makes the instance of the activity un-savable and exits activity
     */
    private void cancelNote() {
        this.mSaveFlag = false;
        this.mNote = null;

        // exit
        onBackPressed();
    }

    private void trashNote() {
        this.mSaveFlag = false;

        // note will not be save dialog
        mNote.withTrashed(true);

        Log.i(TAG, "trashNote: trashing note");
        DBNoteAccessor accessor = DBNoteAccessor.getInstance();
        if (mNote.isNew()) {
            accessor.save(mNote);
        } else {
            accessor.update(mNote);
        }

        onBackPressed();
    }

    @Override
    public void onColorSelected(String colour) {
        this.mColourTag = colour;
        mutateMenuTagIcon();
    }

    @Override
    public void onFolderSelected(Folder folder) {
        this.mCurrentFolder = folder.getName();
    }

    /**
     * saves or updates
     * @param object filled note
     */
    @Override
    public void onSaveResponse(Object object) {
        if (mSaveFlag) {
            if (object instanceof Note) {
                Log.i(TAG, "onSaveResponse: responded to save request");
                this.mSaveFlag = false;

                // global note configs
                this.mNote = ((Note) object)
                        .withFolder(mCurrentFolder)
                        .withColourTag(mColourTag);

                DBNoteAccessor accessor = DBNoteAccessor.getInstance();
                if (mNote.isNew()) {
                    accessor.save(mNote);
                    Log.i(TAG, "onSaveResponse: note has been saved");
                } else {
                    accessor.update(mNote);
                    Log.i(TAG, "onSaveResponse: note has been updated");
                }

                // exit
                onBackPressed();
            } else {
                Log.e(TAG, "onSaveResponse: object cannot be casted to note object");
            }
        }
    }

    @Override
    public void onUpdateObjectResponse(Object object) {
        if (object instanceof Note) {
            this.mNote = (Note) object;
        } else {
            Log.e(TAG, "onSaveResponse: object cannot be casted to note object");
        }
    }

    /**
     * attempts to save the note automatically if auto save is on and then exits activity to the
     * main activity
     */
    @Override
    public void onBackPressed() {
        // if auto save is on then we must save first then leave
        if (mIsAutoSave) {
            requestSave();
        }
        newIntent(NoteActivity.class);
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        if (!isAppLoggedIn()) {
            if (Pref.getAutoSave(this)) {
                requestSave();
            }
        }
        super.onStart();
    }

    @Override
    protected void onPause() {
        if (!isFinishing()) {
            if (secureIntentIsNull()) {
                requestUpdatedNoteObject();
                newIntent(LoginActivity.class);
                getNewIntent().putExtra("edit?", true);
                getNewIntent().putExtra("isNew?", mNote.isNew());
                getNewIntent().putExtra("lastDB", mNote);

                /*
                we must call the timer here on our own because our intent is not null
                therefore the super class onPause commands will not execute */
                startLogoutTimer(Pref.getAutoSave(this));
            }
        }
        super.onPause();
    }
}
