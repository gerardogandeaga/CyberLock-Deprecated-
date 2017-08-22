package com.gerardogandeaga.cyberlock.Activitys.Activities.Menus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Base64;
import android.widget.Toast;

import com.gerardogandeaga.cyberlock.Encryption.AESContent;
import com.gerardogandeaga.cyberlock.Encryption.AESKeyHandler;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.LoginInfo.LoginInfo;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.LoginInfo.LoginInfoDatabaseAccess;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.PaymentInfo.PaymentInfo;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.PaymentInfo.PaymentInfoDatabaseAccess;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.PrivateMemo.Memo;
import com.gerardogandeaga.cyberlock.EncryptionFeatures.PrivateMemo.MemoDatabaseAccess;

import java.util.List;

public class Settings_ScrambleKey extends AsyncTask<Void, Void, Void>
{
    // DATA
    private SharedPreferences mSharedPreferences;
    private static final String KEY = "KEY", TEMP_PIN = "TEMP_PIN";
    private static final int flags = Base64.DEFAULT;

    private MemoDatabaseAccess mMemoDatabaseAccess;
    private PaymentInfoDatabaseAccess mPaymentInfoDatabaseAccess;
    private LoginInfoDatabaseAccess mLoginInfoDatabaseAccess;
    private List<Memo> mMemos;
    private List<PaymentInfo> mPaymentInfos;
    private List<LoginInfo> mLoginInfos;
    
    private Context mContext;

    // WIDGETS
    private ProgressDialog mProgressDialog;

    public Settings_ScrambleKey(Context context)
    {
        mContext = context;

        mSharedPreferences = mContext.getSharedPreferences("com.gerardogandeaga.cyberlock", Context.MODE_PRIVATE);

        this.mMemoDatabaseAccess = MemoDatabaseAccess.getInstance(mContext);
        this.mPaymentInfoDatabaseAccess = PaymentInfoDatabaseAccess.getInstance(mContext);
        this.mLoginInfoDatabaseAccess = LoginInfoDatabaseAccess.getInstance(mContext);
    }

    private void progressBar()
    {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle("Scrambling Key...");
        mProgressDialog.setMessage("Loading Data...");
        mProgressDialog.setProgressStyle(mProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    // ASYNC TASKS
    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        progressBar();

        System.out.println("Scramble Key: onPreExecute");
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        try
        {
            String current_ENC_DEC_KEY = AESKeyHandler.DECRYPTKEY(mSharedPreferences.getString(KEY, null), mSharedPreferences.getString(TEMP_PIN, null));
            System.out.println("Current Key = " + current_ENC_DEC_KEY);


            byte[] KEY_Byte = AESKeyHandler.AES_MEMO_BYTE_KEY_GENERATE(); // GENERATE A NEW BYTE ARRAY AS A SYMMETRIC KEY
            byte[] ENC_DEC_KEY_ByteVal = AESKeyHandler.AES_MEMO_KEY_GENERATE(KEY_Byte);

            System.out.println("REGISTED KEY VAL :" + Base64.encodeToString(ENC_DEC_KEY_ByteVal, flags));
            System.out.println("REGISTED KEY SIZE :" + ENC_DEC_KEY_ByteVal.length);

            String new_ENC_DEC_KEY = Base64.encodeToString(ENC_DEC_KEY_ByteVal, flags);

            mSharedPreferences.edit().remove(KEY).apply();

            // GO THROUGH ALL DATABASES
            this.mMemoDatabaseAccess.open();
            this.mMemos = mMemoDatabaseAccess.getAllMemos();
            for (int i = 0; i < mMemos.size(); i++)
            {
                final Memo memo = mMemos.get(i);
                String text = null;

                if (memo.getText() != null) text = AESContent.decryptContent((memo.getText()), current_ENC_DEC_KEY);

                if (text != null) memo.setText(AESContent.encryptContent(text, new_ENC_DEC_KEY));

                mMemoDatabaseAccess.update(memo);

                text = null;

                System.out.println("done memo");
            }
            this.mMemoDatabaseAccess.close();

            this.mPaymentInfoDatabaseAccess.open();
            this.mPaymentInfos = mPaymentInfoDatabaseAccess.getAllPaymentInfos();
            for (int i = 0; i < mPaymentInfos.size(); i++)
            {
                final PaymentInfo paymentInfo = mPaymentInfos.get(i);
                String cardName = null, cardNumber = null, expiryDate = null, secCode = null, question1 = null, question2 = null, answer1 = null, answer2 = null, notes = null;

                if (paymentInfo.getCardName() != null) cardName = AESContent.decryptContent((paymentInfo.getCardName()), current_ENC_DEC_KEY);
                if (paymentInfo.getCardNumber() != null) cardNumber = AESContent.decryptContent((paymentInfo.getCardNumber()), current_ENC_DEC_KEY);
                if (paymentInfo.getCardExpire() != null) expiryDate = AESContent.decryptContent((paymentInfo.getCardExpire()), current_ENC_DEC_KEY);
                if (paymentInfo.getCardSecCode() != null) secCode = AESContent.decryptContent((paymentInfo.getCardSecCode()), current_ENC_DEC_KEY);
                if (paymentInfo.getQuestion1() != null) question1 = AESContent.decryptContent((paymentInfo.getQuestion1()), current_ENC_DEC_KEY);
                if (paymentInfo.getQuestion2() != null) question2 = AESContent.decryptContent((paymentInfo.getQuestion2()), current_ENC_DEC_KEY);
                if (paymentInfo.getAnswer1() != null) answer1 = AESContent.decryptContent((paymentInfo.getAnswer1()), current_ENC_DEC_KEY);
                if (paymentInfo.getAnswer2() != null) answer2 = AESContent.decryptContent((paymentInfo.getAnswer2()), current_ENC_DEC_KEY);
                if (paymentInfo.getNotes() != null) notes = AESContent.decryptContent((paymentInfo.getNotes()), current_ENC_DEC_KEY);

                if (cardName != null) paymentInfo.setCardName(AESContent.encryptContent(cardName, new_ENC_DEC_KEY));
                if (cardNumber != null) paymentInfo.setCardNumber(AESContent.encryptContent(cardNumber, new_ENC_DEC_KEY));
                if (expiryDate != null) paymentInfo.setCardExpire(AESContent.encryptContent(expiryDate, new_ENC_DEC_KEY));
                if (secCode != null) paymentInfo.setCardSecCode(AESContent.encryptContent(secCode, new_ENC_DEC_KEY));
                if (question1 != null) paymentInfo.setQuestion1(AESContent.encryptContent(question1, new_ENC_DEC_KEY));
                if (question2 != null) paymentInfo.setQuestion2(AESContent.encryptContent(question2, new_ENC_DEC_KEY));
                if (answer1 != null) paymentInfo.setAnswer1(AESContent.encryptContent(answer1, new_ENC_DEC_KEY));
                if (answer2 != null) paymentInfo.setAnswer2(AESContent.encryptContent(answer2, new_ENC_DEC_KEY));
                if (notes != null) paymentInfo.setNotes(AESContent.encryptContent(notes, new_ENC_DEC_KEY));

                cardName = null;
                cardNumber = null;
                expiryDate = null;
                secCode = null;
                question1 = null;
                question2 = null;
                answer1 = null;
                answer2 = null;

                mPaymentInfoDatabaseAccess.update(paymentInfo);

                System.out.println("done payment info");
            }
            this.mPaymentInfoDatabaseAccess.close();

            this.mLoginInfoDatabaseAccess.open();
            this.mLoginInfos = mLoginInfoDatabaseAccess.getAllLoginInfos();
            for (int i = 0; i < mLoginInfos.size(); i++)
            {
                final LoginInfo loginInfo = mLoginInfos.get(i);
                String url = null, username = null, email = null, password = null, question1 = null, question2 = null, answer1 = null, answer2 = null, notes = null;

                if (loginInfo.getUrl() != null) url = AESContent.decryptContent((loginInfo.getUrl()), current_ENC_DEC_KEY);
                if (loginInfo.getUsername() != null) username = AESContent.decryptContent((loginInfo.getUsername()), current_ENC_DEC_KEY);
                if (loginInfo.getEmail() != null) email = AESContent.decryptContent((loginInfo.getEmail()), current_ENC_DEC_KEY);
                if (loginInfo.getPassword() != null) password = AESContent.decryptContent((loginInfo.getPassword()), current_ENC_DEC_KEY);
                if (loginInfo.getQuestion1() != null) question1 = AESContent.decryptContent((loginInfo.getQuestion1()), current_ENC_DEC_KEY);
                if (loginInfo.getQuestion2() != null) question2 = AESContent.decryptContent((loginInfo.getQuestion2()), current_ENC_DEC_KEY);
                if (loginInfo.getAnswer1() != null) answer1 = AESContent.decryptContent((loginInfo.getAnswer1()), current_ENC_DEC_KEY);
                if (loginInfo.getAnswer2() != null) answer2 = AESContent.decryptContent((loginInfo.getAnswer2()), current_ENC_DEC_KEY);
                if (loginInfo.getNotes() != null) notes = AESContent.decryptContent((loginInfo.getNotes()), current_ENC_DEC_KEY);

                if (url != null) loginInfo.setUrl(AESContent.encryptContent(url, new_ENC_DEC_KEY));
                if (username != null) loginInfo.setUsername(AESContent.encryptContent(username, new_ENC_DEC_KEY));
                if (email != null) loginInfo.setEmail(AESContent.encryptContent(email, new_ENC_DEC_KEY));
                if (password != null) loginInfo.setPassword(AESContent.encryptContent(password, new_ENC_DEC_KEY));
                if (question1 != null) loginInfo.setQuestion1(AESContent.encryptContent(question1, new_ENC_DEC_KEY));
                if (question2 != null) loginInfo.setQuestion2(AESContent.encryptContent(question2, new_ENC_DEC_KEY));
                if (answer1 != null) loginInfo.setAnswer1(AESContent.encryptContent(answer1, new_ENC_DEC_KEY));
                if (answer2 != null) loginInfo.setAnswer2(AESContent.encryptContent(answer2, new_ENC_DEC_KEY));
                if (notes != null) loginInfo.setNotes(AESContent.encryptContent(notes, new_ENC_DEC_KEY));

                url = null;
                username = null;
                email = null;
                password = null;
                question1 = null;
                question2 = null;
                answer1 = null;
                answer2 = null;

                mLoginInfoDatabaseAccess.update(loginInfo);

                System.out.println("done login info");
            }
            this.mLoginInfoDatabaseAccess.close();

            mSharedPreferences.edit().putString(KEY, AESKeyHandler.ENCRYPTKEY(new_ENC_DEC_KEY, mSharedPreferences.getString(TEMP_PIN, null))).apply();

            new Handler(mContext.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(mContext, "Key Scrambled Successfully", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e)
        {
            e.printStackTrace();

            System.out.println("Something Went Wrong...");

            new Handler(mContext.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(mContext, "Something Went Wrong...", Toast.LENGTH_SHORT).show();
                }
            });
        }

        System.out.println("Scramble Key: doInBackground");
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        super.onPostExecute(aVoid);

        mProgressDialog.dismiss();

        System.out.println("Scramble Key: onPostExecute");
    }
    // -----------
}