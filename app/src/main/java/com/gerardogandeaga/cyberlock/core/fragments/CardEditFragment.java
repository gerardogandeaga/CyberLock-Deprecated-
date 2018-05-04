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
import com.gerardogandeaga.cyberlock.database.objects.notes.CardNote;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author gerardogandeaga
 */
public class CardEditFragment extends EditFragment {
    private static final String TAG = "CardEditFragment";

    private Note mNote;
    private CardNote mCardNote;
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

        // get note object
        Bundle bundle = this.getArguments();
        this.mNote = (Note) bundle.get("data");

        assert mNote != null;
        this.mCardNote = mNote.getCardNote();

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
        if (!mNote.isNew()) {
            mTvDate.setText(mCardNote.getDate());
            mEtLabel.setText(mCardNote.getLabel());
            mEtCardHolder.setText(mCardNote.getHolder());
            mEtCardNumber.setText(mCardNote.getNumber());
            mEtCardExpire.setText(mCardNote.getExpiry());
            mEtCardCVV.setText(mCardNote.getCVV());
            mEtNotes.setText(mCardNote.getNotes());

            // spinner
            mSpCardSelect.setSelection(mArrayAdapter.getPosition(mCardNote.getCardType()));
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
    protected void compile() {
        Log.i(TAG, "compile: compiling note object...");

        mCardNote.withHolder(mEtCardHolder.getText().toString())
                .withNumber(mEtCardNumber.getText().toString())
                .withCardType(mCardType)
                .withExpiry(mEtCardExpire.getText().toString())
                .withCVV(mEtCardCVV.getText().toString())
                .withNotes(mEtNotes.getText().toString());
        mCardNote.withLabel(mEtLabel.getText().toString());
        // create note
        this.mNote = mCardNote.compile();

        Log.i(TAG, "compile: done compiling");
    }

    @Override
    public void update() {
        Log.i(TAG, "update: updated object requested");
        compile();
        mRequestResponder.onUpdateObjectResponse(mNote);
        Log.i(TAG, "update: updated object sent");
    }

    public void save() {
        Log.i(TAG, "onSaveRequest: save requested");
        compile();
        mRequestResponder.onSaveResponse(mNote);
    }
}
