package com.gerardogandeaga.cyberlock.core.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.core.dialogs.ColourPaletteFragmentDialog;
import com.gerardogandeaga.cyberlock.core.fragments.CardEditFragment;
import com.gerardogandeaga.cyberlock.core.fragments.LoginEditFragment;
import com.gerardogandeaga.cyberlock.core.fragments.NoteEditFragment;
import com.gerardogandeaga.cyberlock.database.DBNoteAccessor;
import com.gerardogandeaga.cyberlock.database.objects.NoteObject;
import com.gerardogandeaga.cyberlock.enums.NoteEditTypes;
import com.gerardogandeaga.cyberlock.interfaces.RequestResponder;
import com.gerardogandeaga.cyberlock.utils.Graphics;
import com.gerardogandeaga.cyberlock.utils.PreferencesAccessor;

import butterknife.BindView;
import butterknife.ButterKnife;


// todo java docs the class
// todo add the colour tags selector
/**
 * @author gerardogandeaga
 *
 * streamlined edit class that contains 3 fragments for the note, card and login info edit.
 * this class handle the global variables between all types, as well as saving to the db.
 * this class extends the core activity meaning all major security features come with
 * this class
 */
public class NoteEditActivity extends CoreActivity implements RequestResponder, ColourPaletteFragmentDialog.ColourSelector {
    private static final String TAG = "NoteEditActivity";

    private Menu mMenu;

    // fragments
    private FragmentManager mFragmentManager;

    private NoteEditFragment mNoteEditFragment;
    private CardEditFragment mCardEditFragment;
    private LoginEditFragment mLoginEditFragment;

    // edit
    private boolean mSaveFlag;
    private boolean mIsNew;
    private boolean mIsAutoSave;
    private NoteEditTypes enum_type;
    private NoteObject mNoteObject;

    // note object
    private String mColourTag;

    @BindView(R.id.toolbar) Toolbar mToolBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_edit);
        bindView();

        // fragments
        this.mFragmentManager = getFragmentManager();

        this.mNoteEditFragment = new NoteEditFragment();
        this.mCardEditFragment = new CardEditFragment();
        this.mLoginEditFragment = new LoginEditFragment();

        // edit variables
        this.mSaveFlag = true;
        this.mIsNew = true;
        this.mIsAutoSave = PreferencesAccessor.getAutoSave(this);

        // launch the fragment
        startEditor();

        if (mNoteObject != null) {
            this.mColourTag = mNoteObject.getColourTag();
        }

        setupActionBar(null, null, NO_ICON);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindView() {
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        this.mMenu = menu;

        // change colour filter of icons
        Graphics.BasicFilter.mutateMenuItems(this, menu);

        mutateMenuTagIcon();

        return true;
    }

    private void mutateMenuTagIcon() {
        if (mColourTag != null) {
            mMenu.findItem(R.id.menu_colour_tag)
                    .getIcon()
                    .mutate()
                    .setColorFilter(Graphics.ColourTags.colourTagHeader(this, mColourTag), PorterDuff.Mode.SRC_ATOP);
        }
    }

    /**
     * when activating the editor there are 3 possible states in which is will enter:
     * STATE 1 : completely new (when it is called by the ADD function and  is not a data item yet)
     * STATE 2 : floating raw data item (When editor is suspended by the logout protocol but has
     * not been saved in the database master database accessor)
     * STATE 3 : saved note object (when the data item has already been saved and is merely
     * going to get updated).
     * then finally starts the fragment editor
     */
    private void startEditor() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.mNoteObject = (NoteObject) bundle.get("data");
            this.mIsNew = (mNoteObject == null);

            // fragment transaction "manager"
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

            if (!mIsNew) {
                Bundle noteBundle = new Bundle();
                noteBundle.putSerializable("data", mNoteObject);
                switch (mNoteObject.getType()) {
                    case NoteObject.NOTE:
                        this.enum_type = NoteEditTypes.NOTE;
                        fragmentTransaction.add(R.id.fragment_container, newFragment(mNoteEditFragment, noteBundle));
                        break;
                    case NoteObject.CARD:
                        this.enum_type = NoteEditTypes.CARD;
                        fragmentTransaction.add(R.id.fragment_container, newFragment(mCardEditFragment, noteBundle));
                        break;
                    case NoteObject.LOGIN:
                        this.enum_type = NoteEditTypes.LOGIN;
                        fragmentTransaction.add(R.id.fragment_container, newFragment(mLoginEditFragment, noteBundle));
                        break;
                }
                // check if data item already exists
                checkContainsData(); // will alter between STATE 2 & 3 by switching mIsNew
                // new data override!!!
                if (!bundle.getBoolean("isNew?")) {
                    this.mIsNew = false; // will alter between STATE 2 & 3 by switching mIsNew
                }
            } else { // if data is completely new
                String type = (String) bundle.get("type");
                assert type != null;
                switch (type) { // STATE 1
                    case NoteObject.NOTE:
                        this.enum_type = NoteEditTypes.NOTE;
                        fragmentTransaction.add(R.id.fragment_container, mNoteEditFragment);
                        break;
                    case NoteObject.CARD:
                        this.enum_type = NoteEditTypes.CARD;
                        fragmentTransaction.add(R.id.fragment_container, mCardEditFragment);
                        break;
                    case NoteObject.LOGIN:
                        this.enum_type = NoteEditTypes.LOGIN;
                        fragmentTransaction.add(R.id.fragment_container, mLoginEditFragment);
                        break;
                }
            }
            // remove bundles
            bundle.remove("data");
            bundle.remove("type");

            // start fragment
            fragmentTransaction.commit();
        } // else {
            // todo idea - throw command not specified exception
        // }
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

    /**
     * checks if the data if already exists in the database, if it is then
     * is new is true else is new is false
     */
    private void checkContainsData() {
        DBNoteAccessor accessor = DBNoteAccessor.getInstance(this);
        accessor.open();
        System.out.println("Is New ? 1 : " + mIsNew);
        this.mIsNew = accessor.containsData(this.mNoteObject);
        System.out.println("Is New ? 2 : " + mIsNew);
        accessor.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_colour_tag:
                ColourPaletteFragmentDialog.show(this);
                break;
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

    @Override
    public void onColorSelected(String colour) {
        this.mColourTag = colour;
        mutateMenuTagIcon();
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
                // todo idea - throw a possible exception
                Log.e(TAG, "requestSave: edit type was not properly specified");
                break;
        }
    }

    /**
     * makes the instance of the activity un-savable and exits activity
     */
    private void cancelNote() {
        this.mSaveFlag = false;

        // exit
        onBackPressed();
    }

    private void requestUpdatedNoteObject() {
        switch (enum_type) {
            case NOTE:
                mNoteEditFragment.updateObject();
                break;
            case CARD:
                mCardEditFragment.updateObject();
                break;
            case LOGIN:
                mLoginEditFragment.updateObject();
                break;
            default:
                // todo idea - throw a possible exception
                Log.e(TAG, "requestSave: edit type was not properly specified");
                break;
        }
    }

    /**
     * saves or updates
     * @param object filled note
     */
    @Override
    public void onSaveResponse(Object object) {
        if (mSaveFlag) {
            if (object instanceof NoteObject) {
                NoteObject noteObject = (NoteObject) object;
                this.mSaveFlag = false;
                Log.i(TAG, "onSaveResponse: responded to save request");
                // todo save here
                // global not configs
                noteObject.setColourTag(mColourTag);

                DBNoteAccessor accessor = DBNoteAccessor.getInstance(this);
                accessor.open();
                if (mIsNew) {
                    accessor.save(noteObject);
                    Log.i(TAG, "onSaveResponse: note has been saved");
                } else {
                    accessor.update(noteObject);
                    Log.i(TAG, "onSaveResponse: note has been updated");
                }
                accessor.close();

                // exit
                onBackPressed();
            } else {
                Log.e(TAG, "onSaveResponse: object cannot be casted to note object");
            }
        }
    }

    @Override
    public void onUpdateObjectResponse(Object object) {
        if (object instanceof NoteObject) {
            this.mNoteObject = (NoteObject) object;
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
        newIntent(NoteListActivity.class);
        super.onBackPressed();
    }

    @Override protected void onStart() {
        if (!isAppLoggedIn()) {
            if (PreferencesAccessor.getAutoSave(this)) {
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
                getNewIntent().putExtra("isNew?", mIsNew);
                getNewIntent().putExtra("lastDB", mNoteObject);

                /*
                we must call the timer here on our own because our intent is not null
                therefore the super class onPause commands will not execute */
                startLogoutTimer(PreferencesAccessor.getAutoSave(this));
            }
        }
        super.onPause();
    }
}
