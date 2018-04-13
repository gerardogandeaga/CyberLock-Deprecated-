package com.gerardogandeaga.cyberlock.core.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.database.objects.Note;
import com.gerardogandeaga.cyberlock.helpers.content.NoteContentHandler;
import com.gerardogandeaga.cyberlock.interfaces.RequestResponder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author gerardogandeaga
 */
public class CardEditFragment extends EditFragment {
    private static final String TAG = "CardEditFragment";

    // response interface
    private RequestResponder mRequestResponder;

    private Note mNote;
    private NoteContentHandler mNoteContentHandler;
    private ArrayAdapter<CharSequence> mArrayAdapter;
    private String mCardType;

    // view
    @BindView(R.id.tvDate)       TextView mTvDate;
    @BindView(R.id.etLabel)      EditText mEtLabel;
    @BindView(R.id.etCardHolder) EditText mEtCardHolder;
    @BindView(R.id.etCardNumber) EditText mEtCardNumber;
    @BindView(R.id.etCardExpire) EditText mEtCardExpire;
    @BindView(R.id.etCardCVV)    EditText mEtCardCVV;
    @BindView(R.id.etNotes)      EditText mEtNotes;
    @BindView(R.id.spCardSelect) Spinner mSpCardSelect;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // instantiate interface
        try {
            this.mRequestResponder = (RequestResponder) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onCreate: could not cast " + TAG + " to RequestResponder class");
        }

        // get note object
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            this.mNote = (Note) bundle.get("data");
            this.mNoteContentHandler = new NoteContentHandler(getActivity(), mNote);
        }

        // spinner array adapter
        this.mArrayAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.str_array_card_type, R.layout.spinner_setting_text);
        this.mArrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
    }

    /**
     * set view content
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_card, container, false);
        ButterKnife.bind(this, view);

        // configure views
        mEtCardNumber.addTextChangedListener(new TextWatcher() {
            int prevL = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                prevL = mEtCardNumber.getText().toString().length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                if ((prevL < length) && (length == 4 || length == 9 || length == 14)) {

                    String data = mEtCardNumber.getText().toString();
                    mEtCardNumber.setText(data + " ");
                    mEtCardNumber.setSelection(length + 1);
                }
            }
        });
        mEtCardExpire.addTextChangedListener(new TextWatcher() {
            int prevL = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                prevL = mEtCardExpire.getText().toString().length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                if ((prevL < length) && (length == 2)) {

                    String data = mEtCardExpire.getText().toString();
                    mEtCardExpire.setText(data + "/");
                    mEtCardExpire.setSelection(length + 1);
                }
            }
        });
        mSpCardSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object object = parent.getItemAtPosition(position);
                if (object != null) {
                    mCardType = object.toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mSpCardSelect.setAdapter(mArrayAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // if data is not null then we set our stored data onto
        if (mNote != null) {
            mTvDate.setText(mNoteContentHandler.mDate);
            mEtLabel.setText(mNoteContentHandler.mLabel);
            mEtCardHolder.setText(mNoteContentHandler.mHolder);
            mEtCardNumber.setText(mNoteContentHandler.mNumber);
            mEtCardExpire.setText(mNoteContentHandler.mExpiry);
            mEtCardCVV.setText(mNoteContentHandler.mCVV);
            mEtNotes.setText(mNoteContentHandler.mNotes);

            // spinner
            mSpCardSelect.setSelection(mArrayAdapter.getPosition(mNoteContentHandler.mCardType));
        } else {
            mTvDate.setText(null);
            mEtLabel.setText(null);
            mEtCardHolder.setText(null);
            mEtCardNumber.setText(null);
            mEtCardExpire.setText(null);
            mEtCardCVV.setText(null);
            mEtNotes.setText(null);
        }
    }

    @Override
    protected void compileObject() {
        Log.i(TAG, "compileObject: compiling note object...");
        final String label = mEtLabel.getText().toString();
        final String holder = mEtCardHolder.getText().toString();
        final String number = mEtCardNumber.getText().toString();
        final String type = mCardType;
        final String expiry = mEtCardExpire.getText().toString();
        final String cvv = mEtCardCVV.getText().toString();
        final String notes = mEtNotes.getText().toString();

        // format content
        final String format = "%s\n%s\n%s\n%s\n%s\n%s";
        final String content = String.format(format, holder, number, type, expiry, cvv, notes);

        if (mNote == null) {
            this.mNote = new Note();

            mNote.setType(Note.CARD);
        }

        mNote.setLabel(label);
        mNote.setContent(content);
        Log.i(TAG, "compileObject: done compiling");
    }

    @Override
    public void updateObject() {
        Log.i(TAG, "updateObject: updated object requested");
        compileObject();
        mRequestResponder.onUpdateObjectResponse(mNote);
        Log.i(TAG, "updateObject: updated object sent");
    }

    public void save() {
        Log.i(TAG, "onSaveRequest: save requested");
        compileObject();
        mRequestResponder.onSaveResponse(mNote);
    }
}