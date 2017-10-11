package com.gerardogandeaga.cyberlock.Activitys.Activities.Edits;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Main.MainActivity;
import com.gerardogandeaga.cyberlock.Crypto.CryptContent;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.ContentDatabase.Data;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.ContentDatabase.MasterDatabaseAccess;
import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.Supports.Globals;
import com.gerardogandeaga.cyberlock.Supports.LogoutProtocol;

import org.jetbrains.annotations.Contract;

import java.util.Scanner;

import static com.gerardogandeaga.cyberlock.Supports.Globals.AUTOSAVE;
import static com.gerardogandeaga.cyberlock.Supports.Globals.DIRECTORY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.MASTER_KEY;
import static com.gerardogandeaga.cyberlock.Supports.Globals.TEMP_PIN;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.APP_LOGGED_IN;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.mCountDownIsFinished;
import static com.gerardogandeaga.cyberlock.Supports.LogoutProtocol.mCountDownTimer;

public class MemoEditActivity extends AppCompatActivity {
    // DATA VARIABLES
    private boolean mIsNew = true;
    private boolean mIsAutoSave = false;
    private CryptContent mCRYPTCONTENT;
    private Data mData;

    // WIDGETS
    private Toolbar mToolbar;
    private EditText
            mEtMemo,
            mEtLabel;
    private TextView mTvDate;

    private String mColourTag;
    private CustomDialogs mCustomDialogs;

    // INITIAL ON CREATE METHODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Globals.COLORSCHEME(this);
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setupLayout();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }
    private void setupLayout() {
        setContentView(R.layout.activity_edit_memo);
        Bundle bundle = getIntent().getExtras();
        ACTIVITY_INTENT = null;
        mCustomDialogs = new CustomDialogs(this);
        mIsAutoSave = getSharedPreferences(DIRECTORY, MODE_PRIVATE).getBoolean(AUTOSAVE, false);
        // WIDGETS
        mEtMemo = (EditText) findViewById(R.id.etText);
        mEtLabel = (EditText) findViewById(R.id.etMemoTitle);
        mTvDate = (TextView) findViewById(R.id.tvLastUpdated);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);

        setupActivity(bundle);
    }
    private void setupActivity(Bundle bundle) {
        mCRYPTCONTENT = new CryptContent(this);
        if (bundle != null) {
            mIsNew = false;
            mData = (Data) bundle.get("DATA");
            if (mData != null) {
                try {
                    String label = mCRYPTCONTENT.DECRYPT_CONTENT(mData.getLabel(), MASTER_KEY);
                    setActionBarTitle(label);
                    mColourTag = mData.getColourTag();

                    final String content = mCRYPTCONTENT.DECRYPT_CONTENT(mData.getContent(), MASTER_KEY);
                    String memo;
                    if (content != null) {
                        Scanner scanner = new Scanner(content);

                        memo = scanner.nextLine();
                        while (scanner.hasNextLine()) {
                            memo += "\n";
                            memo += scanner.nextLine();
                        }
                        scanner.close();
                        mEtMemo.setText(memo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    // -------------------------

    // ON CLICK
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case (R.id.action_save):
                if (!mIsAutoSave) {
                    onSave();
                    onBackPressed();
                } else {
                    onBackPressed();
                }
                return true;
            case (R.id.action_cancel):
                onCancel();
                return true;
            case (R.id.action_colortag):
                mCustomDialogs.createColourPickDialog();
                return true;
            case (R.id.action_label):
                mCustomDialogs.createLabelDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    // --------

    // ACTIVITY ACTIONS
    private void onSave() {
        MasterDatabaseAccess masterDatabaseAccess = MasterDatabaseAccess.getInstance(this);
        masterDatabaseAccess.open();

        if ((!mEtLabel.getText().toString().matches("")) || (!mEtMemo.getText().toString().matches(""))) { /// TODO REFACTOR CONDITIONAL SAVE TO THE SINGLE CONTENT STRING
            final String memo = mEtMemo.getText().toString();

            final String format = "%s";
            final String tmpString = String.format(format,
                    memo);
            if (mData == null) {
                Data tmp = new Data();

                tmp.setType("TYPE_MEMO"); // TODO ENCRYPT TYPE!
//                tmp.setLabel(mCRYPTCONTENT.ENCRYPT_KEY(mEtLabel.getText().toString(), MASTER_KEY));
                tmp.setLabel(mCRYPTCONTENT.ENCRYPT_KEY(mCustomDialogs.getTmpLabel(), MASTER_KEY));
                tmp.setContent(mCRYPTCONTENT.ENCRYPT_KEY(tmpString, MASTER_KEY));
                // SET COLOUR CONDITIONALLY
                if (!mIsNew) {
                    if (mCustomDialogs.getTmpColour() == null) {
                        tmp.setColourTag(mColourTag);
                    } else {
                        tmp.setColourTag(mCustomDialogs.getTmpColour());
                    }
                } else {
                    tmp.setColourTag(mCustomDialogs.getTmpColour());
                }
                // ------------------------
                masterDatabaseAccess.save(tmp);
            } else {
                mData.setLabel(mCRYPTCONTENT.ENCRYPT_KEY(mEtLabel.getText().toString(), MASTER_KEY));
                mData.setContent(mCRYPTCONTENT.ENCRYPT_KEY(tmpString, MASTER_KEY));
                // SET COLOUR CONDITIONALLY
                if (!mIsNew) {
                    if (mCustomDialogs.getTmpColour() == null) {
                        mData.setColourTag(mColourTag);
                    } else {
                        mData.setColourTag(mCustomDialogs.getTmpColour());
                    }
                } else {
                    mData.setColourTag(mCustomDialogs.getTmpColour());
                }
                // ------------------------
                masterDatabaseAccess.update(mData);
            }

            masterDatabaseAccess.close();
        } else {
            Toast.makeText(this, "Nothing to save", Toast.LENGTH_SHORT).show();

            masterDatabaseAccess.close();
        }
    }
    private void onCancel() {
        ACTIVITY_INTENT = new Intent(this, MainActivity.class);
        this.finish();
        this.startActivity(ACTIVITY_INTENT);
    }
    // ----------------

    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE"
    @Override
    protected void onStart() {
        super.onStart();

        if (mCountDownIsFinished) {
            if (!APP_LOGGED_IN) {
                if (this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTOSAVE, false)) {
                    onSave();
                    MASTER_KEY = null;
                    TEMP_PIN = null;
                }

                ACTIVITY_INTENT = new Intent(this, LoginActivity.class);
                ACTIVITY_INTENT.putExtra("lastActivity", "MEMO_EDIT");
                ACTIVITY_INTENT.putExtra("lastDatabase", mData);

                this.finish(); // CLEAN UP AND END
                this.startActivity(ACTIVITY_INTENT); // GO TO LOGIN ACTIVITY

                System.gc();
            }
        } else {
            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
        {
            if (this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTOSAVE, false)) {
                onSave();
            }

            ACTIVITY_INTENT = new Intent(this, MainActivity.class);
            this.finish();
            this.startActivity(ACTIVITY_INTENT);
        }
    }
    @Override
    public void onPause() {
        super.onPause();

        if (!this.isFinishing()) // HOME AND TABS AND SCREEN OFF
        {
            if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
            {
                if (!this.getSharedPreferences(DIRECTORY, Context.MODE_PRIVATE).getBoolean(AUTOSAVE, false)) {
                    new LogoutProtocol().logoutExecuteAutosaveOff(this);
                } else {
                    new LogoutProtocol().logoutExecuteAutosaveOn(this);
                }
            }

        }
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // QUARANTINED FUNCTIONS
    public void setActionBarTitle(String label) {
        getSupportActionBar().setTitle(label);
        getSupportActionBar().setSubtitle(mData.getDate());
    }

    // CUSTOM DIALOG INNER CLASS
    private class CustomDialogs implements View.OnClickListener {
        private Context mContext;
        private AlertDialog.Builder mAlertDialog;
        private Dialog mDialog;

        private String mTmpLabel;
        private String mTmpColour;

        private CustomDialogs(Context context) {
            mContext = context;
        }

        // SET LABEL
        private void createLabelDialog() {
            mAlertDialog = new AlertDialog.Builder(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 10, 10, 10);

            // BUILD VIEW
            final EditText etLabel = new EditText(mContext);

            etLabel.setLayoutParams(params);

            // BUILD DIALOG
            mAlertDialog.setCancelable(false);
            mAlertDialog.setTitle("Input A New Label");
            mAlertDialog.setView(etLabel);

            mAlertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            mAlertDialog.setPositiveButton("SET", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setTmpLabel(etLabel.getText().toString());
                    dialog.dismiss();
                }
            });
            mAlertDialog.show();
        }
        @Contract(pure = true)
        private String getTmpLabel() {
            if (mTmpLabel != null) {
                return mTmpLabel;
            } else {
                return "New Document";
            }
        }
        private void setTmpLabel(String tmpLabel) {
            mTmpLabel = tmpLabel;
            getSupportActionBar().setTitle(mTmpColour);
        }

        // COLOUR TAG CUSTOMIZATION
        private void createColourPickDialog() {
            mDialog = new Dialog(mContext);

            // BUILD DIALOG
            mDialog.setContentView(R.layout.dialog_colourtag);
            mDialog.setCanceledOnTouchOutside(true);
            // BUTTONS
            ImageView blue = (ImageView) mDialog.findViewById(R.id.imgBlue);
            ImageView red = (ImageView) mDialog.findViewById(R.id.imgRed);
            ImageView green = (ImageView) mDialog.findViewById(R.id.imgGreen);
            ImageView yellow = (ImageView) mDialog.findViewById(R.id.imgYellow);
            ImageView purple = (ImageView) mDialog.findViewById(R.id.imgPurple);
            ImageView orange = (ImageView) mDialog.findViewById(R.id.imgOrange);

            blue.setOnClickListener(this);
            red.setOnClickListener(this);
            green.setOnClickListener(this);
            yellow.setOnClickListener(this);
            purple.setOnClickListener(this);
            orange.setOnClickListener(this);

            mDialog.show();
        }
        private String getTmpColour() {
            return mTmpColour;
        }
        private void setTmpColour(String tmpColour) {
            mTmpColour = tmpColour;
            mDialog.dismiss();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgBlue:
                    setTmpColour("COL_BLUE");
                    System.out.println("HI!");
                    break;
                case R.id.imgRed:
                    setTmpColour("COL_RED");
                    break;
                case R.id.imgGreen:
                    setTmpColour("COL_GREEN");
                    break;
                case R.id.imgYellow:
                    setTmpColour("COL_YELLOW");
                    break;
                case R.id.imgPurple:
                    setTmpColour("COL_PURPLE");
                    break;
                case R.id.imgOrange:
                    setTmpColour("COL_ORANGE");
                    break;
                default:
                    setTmpColour("DEFAULT");
                    break;
            }
        }
    }
}