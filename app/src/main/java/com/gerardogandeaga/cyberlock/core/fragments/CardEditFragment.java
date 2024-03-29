package com.gerardogandeaga.cyberlock.core.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.database.objects.Note;
import com.gerardogandeaga.cyberlock.database.objects.notes.CardNote;
import com.gerardogandeaga.cyberlock.custom.CustomToast;
import com.gerardogandeaga.cyberlock.utils.Views;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author gerardogandeaga
 */
public class CardEditFragment extends EditFragment implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "CardEditFragment";

    private Note mNote;
    private CardNote mCardNote;
    private String mCardType;

    // view
    @BindView(R.id.tvDate)       TextView mTvDate;
    @BindView(R.id.etLabel)      EditText mEtLabel;
    @BindView(R.id.etCardHolder) EditText mEtCardHolder;
    @BindView(R.id.etCardNumber) EditText mEtCardNumber;
    @BindView(R.id.etCardExpire) EditText mEtCardExpire;
    @BindView(R.id.etCardCVV)    EditText mEtCardCVV;
    @BindView(R.id.etNotes)      EditText mEtNotes;

    // containers
    @BindView(R.id.Default)    RelativeLayout mDefault;
    @BindView(R.id.Visa)       RelativeLayout mVisa;
    @BindView(R.id.MasterCard) RelativeLayout mMastercard;
    @BindView(R.id.Amex)       RelativeLayout mAmex;
    @BindView(R.id.Discover)   RelativeLayout mDiscover;
    // radio buttons
    @BindView(R.id.rbDefault)    RadioButton mRbDefault;
    @BindView(R.id.rbVisa)       RadioButton mRbVisa;
    @BindView(R.id.rbMasterCard) RadioButton mRbMastercard;
    @BindView(R.id.rbAmex)       RadioButton mRbAmex;
    @BindView(R.id.rbDiscover)   RadioButton mRbDiscover;
    // custom radio button grouper
    private RadioGroupSelector mRadioSelector;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get note object
        Bundle bundle = this.getArguments();
        this.mNote = (Note) bundle.get("data");

        assert mNote != null;
        this.mCardNote = mNote.getCardNote();

        this.mCardType = mCardNote.getCardType();
    }

    /**
     * set view content
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_card, container, false);
        ButterKnife.bind(this, view);

        // set click listeners
        mDefault.setOnClickListener(this);
        mVisa.setOnClickListener(this);
        mMastercard.setOnClickListener(this);
        mAmex.setOnClickListener(this);
        mDiscover.setOnClickListener(this);
        // long clicks
        mDefault.setOnLongClickListener(this);
        mVisa.setOnLongClickListener(this);
        mMastercard.setOnLongClickListener(this);
        mAmex.setOnLongClickListener(this);
        mDiscover.setOnLongClickListener(this);


        // bundle radio buttons into a group for easy selection
        this.mRadioSelector = new RadioGroupSelector();
        mRadioSelector.add(mRbDefault);
        mRadioSelector.add(mRbVisa);
        mRadioSelector.add(mRbMastercard);
        mRadioSelector.add(mRbAmex);
        mRadioSelector.add(mRbDiscover);

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

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // if data is not null then we set our stored data onto
        if (!mNote.isNew()) {
            Views.TextViews.setOrHideText(mTvDate, mCardNote.getDate());
            mEtLabel.setText(mCardNote.getLabel());
            mEtCardHolder.setText(mCardNote.getHolder());
            mEtCardNumber.setText(mCardNote.getNumber());
            mEtCardExpire.setText(mCardNote.getExpiry());
            mEtCardCVV.setText(mCardNote.getCVV());
            mEtNotes.setText(mCardNote.getNotes());
            mRadioSelector.clicked(mRadioSelector.getTagged(mCardNote.getCardType()));
        } else {
            Views.TextViews.setOrHideText(mTvDate, null);
            mEtLabel.setText(null);
            mEtCardHolder.setText(null);
            mEtCardNumber.setText(null);
            mEtCardExpire.setText(null);
            mEtCardCVV.setText(null);
            mEtNotes.setText(null);
            mRadioSelector.clicked(mRbDefault);
        }
    }

    @Override
    public void toggleViewMode() {
        setReadOnly(!isReadOnly());

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

    @Override
    public void onClick(View view) {
        // get card type to string
        this.mCardType = (String) view.getTag();
        CustomToast.buildAndShowToast(getActivity(), mCardType + " Selected", CustomToast.INFORMATION, CustomToast.LENGTH_SHORT);

        // radio button toggle
        switch (view.getId()) {
            case R.id.Default:
                mRadioSelector.clicked(mRbDefault);
                break;
            case R.id.Visa:
                mRadioSelector.clicked(mRbVisa);
                break;
            case R.id.MasterCard:
                mRadioSelector.clicked(mRbMastercard);
                break;
            case R.id.Amex:
                mRadioSelector.clicked(mRbAmex);
                break;
            case R.id.Discover:
                mRadioSelector.clicked(mRbDiscover);
                break;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (view.getTag() != null) {
            CustomToast.buildAndShowToast(getActivity(), view.getTag().toString());
            return true;
        }
        return false;
    }

    private static class RadioGroupSelector {
        private ArrayList<RadioButton> mRadioButtons;

        RadioGroupSelector() {
            this.mRadioButtons = new ArrayList<>();
        }

        void add(RadioButton radioButton) {
            mRadioButtons.add(radioButton);
        }

        /**
         * get button with specific tag
         * @param tag card type
         * @return radio button with that card type
         */
        RadioButton getTagged(String tag) {
            for (RadioButton button : mRadioButtons) {
                if (button.getTag().equals(tag)) {
                    return button;
                }
            }
            return null;
        }

        /**
         * deselects all other radio buttons other the one selected
         * @param selectedButton clicked button
         */
        void clicked(RadioButton selectedButton) {
            for (RadioButton button : mRadioButtons) {
                // deselect all other buttons
                button.setChecked(button == selectedButton);
            }
        }
    }
}
