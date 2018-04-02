package com.gerardogandeaga.cyberlock.core;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.core.fragments.CardEditFragment;
import com.gerardogandeaga.cyberlock.core.fragments.LoginEditFragment;
import com.gerardogandeaga.cyberlock.core.fragments.NoteEditFragment;
import com.gerardogandeaga.cyberlock.core.activities.NoteListActivity;
import com.gerardogandeaga.cyberlock.database.DBNoteAccessor;
import com.gerardogandeaga.cyberlock.database.objects.NoteObject;
import com.gerardogandeaga.cyberlock.enums.NoteEditTypes;
import com.gerardogandeaga.cyberlock.interfaces.SaveResponder;
import com.gerardogandeaga.cyberlock.utils.SharedPreferences;
import com.gerardogandeaga.cyberlock.utils.Graphics;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.gerardogandeaga.cyberlock.utils.security.LogoutProtocol.ACTIVITY_INTENT;


// todo java docs the class
// todo add the colour tags selector
/**
 * @author gerardogandeaga
 */
public class TempEdit extends AppCompatActivity implements SaveResponder {
    private static final String TAG = "TempEdit";

    // fragments
    private FragmentManager mFragmentManager;

    private NoteEditFragment mNoteEditFragment;
    private CardEditFragment mCardEditFragment;
    private LoginEditFragment mLoginEditFragment;

    private boolean mSaveFlag;
    private boolean mIsNew;
    private boolean mIsAutoSave;
    private NoteEditTypes enum_type;
    private NoteObject mNoteObject;

    @BindView(R.id.toolbar) Toolbar mToolBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // todo create a parent class that will execute ACTIVITY_INTENT = null and other directives automatically
        ACTIVITY_INTENT = null;
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);

        // fragments
        this.mFragmentManager = getFragmentManager();

        this.mNoteEditFragment = new NoteEditFragment();
        this.mCardEditFragment = new CardEditFragment();
        this.mLoginEditFragment = new LoginEditFragment();

        // edit variables
        this.mSaveFlag = true;
        this.mIsNew = true;
        this.mIsAutoSave = SharedPreferences.getAutoSave(this);

        setupSupportActionBar();
        // launch the fragment
        startEditor();
    }

    private void setupSupportActionBar() {
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(null);
//        getSupportActionBar().setHomeAsUpIndicator(Graphics.BasicFilter.mutateHomeAsUpIndicatorDrawable(
//                this, Resources.getDrawable(this, R.drawable.ic_back)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);

        // change colour filter of icons
        Graphics.BasicFilter.mutateMenuItems(this, menu);

        return true;
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

    /**
     * saves or updates
     * @param noteObject filled note
     */
    @Override
    public void onSaveResponse(NoteObject noteObject) {
        if (mSaveFlag) {
            this.mSaveFlag = false;
            Log.i(TAG, "onSaveResponse: responded to save request");
            // todo save here
            // global not configs
            noteObject.setTag("default");

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
        }
    }

    /**
     * attempts to save the note automatically if auto save is on and then exits activity to the
     * main activity
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (ACTIVITY_INTENT == null) {
            if (mIsAutoSave) {
                requestSave();
            }

            ACTIVITY_INTENT = new Intent(this, NoteListActivity.class);
            this.finish();
            this.startActivity(ACTIVITY_INTENT);
        }
    }
}
