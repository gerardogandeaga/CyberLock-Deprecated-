package com.gerardogandeaga.cyberlock.Activitys.Activities.Menus;

import android.content.Context;
import android.content.SharedPreferences;
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

public class Settings_ScrambleKey
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

    public Settings_ScrambleKey(Context context)
    {
        mContext = context;
    }

    public void ScrambleKey()
    {
        mSharedPreferences = mContext.getSharedPreferences("com.gerardogandeaga.cyberlock", Context.MODE_PRIVATE);

        this.mMemoDatabaseAccess = MemoDatabaseAccess.getInstance(mContext);
        this.mPaymentInfoDatabaseAccess = PaymentInfoDatabaseAccess.getInstance(mContext);
        this.mLoginInfoDatabaseAccess = LoginInfoDatabaseAccess.getInstance(mContext);

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
                String text;

                text = AESContent.decryptContent((memo.getText()), current_ENC_DEC_KEY);

                memo.setText(AESContent.encryptContent(text, new_ENC_DEC_KEY));

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
                String cardName, cardNumber, expiryDate, secCode, question1, question2, answer1, answer2, notes;

                cardName = AESContent.decryptContent((paymentInfo.getCardName()), current_ENC_DEC_KEY);
                cardNumber = AESContent.decryptContent((paymentInfo.getCardNumber()), current_ENC_DEC_KEY);
                expiryDate = AESContent.decryptContent((paymentInfo.getCardExpire()), current_ENC_DEC_KEY);
                secCode = AESContent.decryptContent((paymentInfo.getCardSecCode()), current_ENC_DEC_KEY);
                question1 = AESContent.decryptContent((paymentInfo.getQuestion1()), current_ENC_DEC_KEY);
                question2 = AESContent.decryptContent((paymentInfo.getQuestion2()), current_ENC_DEC_KEY);
                answer1 = AESContent.decryptContent((paymentInfo.getAnswer1()), current_ENC_DEC_KEY);
                answer2 = AESContent.decryptContent((paymentInfo.getAnswer2()), current_ENC_DEC_KEY);
                notes = AESContent.decryptContent((paymentInfo.getNotes()), current_ENC_DEC_KEY);

                paymentInfo.setCardName(AESContent.encryptContent(cardName, new_ENC_DEC_KEY));
                paymentInfo.setCardNumber(AESContent.encryptContent(cardNumber, new_ENC_DEC_KEY));
                paymentInfo.setCardExpire(AESContent.encryptContent(expiryDate, new_ENC_DEC_KEY));
                paymentInfo.setCardSecCode(AESContent.encryptContent(secCode, new_ENC_DEC_KEY));
                paymentInfo.setQuestion1(AESContent.encryptContent(question1, new_ENC_DEC_KEY));
                paymentInfo.setQuestion2(AESContent.encryptContent(question2, new_ENC_DEC_KEY));
                paymentInfo.setAnswer1(AESContent.encryptContent(answer1, new_ENC_DEC_KEY));
                paymentInfo.setAnswer2(AESContent.encryptContent(answer2, new_ENC_DEC_KEY));
                paymentInfo.setNotes(AESContent.encryptContent(notes, new_ENC_DEC_KEY));

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
                String url, username, email, password, question1, question2, answer1, answer2, notes;

                url = AESContent.decryptContent((loginInfo.getUrl()), current_ENC_DEC_KEY);
                username = AESContent.decryptContent((loginInfo.getUsername()), current_ENC_DEC_KEY);
                email = AESContent.decryptContent((loginInfo.getEmail()), current_ENC_DEC_KEY);
                password = AESContent.decryptContent((loginInfo.getPassword()), current_ENC_DEC_KEY);
                question1 = AESContent.decryptContent((loginInfo.getQuestion1()), current_ENC_DEC_KEY);
                question2 = AESContent.decryptContent((loginInfo.getQuestion2()), current_ENC_DEC_KEY);
                answer1 = AESContent.decryptContent((loginInfo.getAnswer1()), current_ENC_DEC_KEY);
                answer2 = AESContent.decryptContent((loginInfo.getAnswer2()), current_ENC_DEC_KEY);
                notes = AESContent.decryptContent((loginInfo.getNotes()), current_ENC_DEC_KEY);

                loginInfo.setUrl(AESContent.encryptContent(url, new_ENC_DEC_KEY));
                loginInfo.setUsername(AESContent.encryptContent(username, new_ENC_DEC_KEY));
                loginInfo.setEmail(AESContent.encryptContent(email, new_ENC_DEC_KEY));
                loginInfo.setPassword(AESContent.encryptContent(password, new_ENC_DEC_KEY));
                loginInfo.setQuestion1(AESContent.encryptContent(question1, new_ENC_DEC_KEY));
                loginInfo.setQuestion2(AESContent.encryptContent(question2, new_ENC_DEC_KEY));
                loginInfo.setAnswer1(AESContent.encryptContent(answer1, new_ENC_DEC_KEY));
                loginInfo.setAnswer2(AESContent.encryptContent(answer2, new_ENC_DEC_KEY));
                loginInfo.setNotes(AESContent.encryptContent(notes, new_ENC_DEC_KEY));

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
            Toast.makeText(mContext, "Key Scramble Successful!", Toast.LENGTH_SHORT).show();

        } catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(mContext, "Something Went Wrong...", Toast.LENGTH_SHORT).show();
        }
    }
}