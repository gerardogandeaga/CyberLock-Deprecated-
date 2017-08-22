package com.gerardogandeaga.cyberlock.EncryptionFeatures.PaymentInfo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LoginActivity;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol;
import com.gerardogandeaga.cyberlock.Activitys.Activities.Main.MainActivity;
import com.gerardogandeaga.cyberlock.R;

import java.util.List;

import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.ACTIVITY_INTENT;
import static com.gerardogandeaga.cyberlock.Activitys.Activities.Login.LogoutProtocol.APP_LOGGED_IN;

public class MainPaymentInfoActivity extends AppCompatActivity
{
    // DATA
    private PaymentInfoDatabaseAccess mPaymentInfoDatabaseAccess;
    private List<PaymentInfo> mPaymentInfos;
    // WIDGETS
    private ListView mListView;
    private FloatingActionButton mFabAdd;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_paymentinfo);
        ACTIVITY_INTENT = null; // START ACTIVITY WITH EMPTY INTENT

        this.mPaymentInfoDatabaseAccess = PaymentInfoDatabaseAccess.getInstance(this);

        // ACTION BAR TITLE AND BACK BUTTON
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Card Lock");

        this.mListView = (ListView) findViewById(R.id.listView);
        this.mFabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);

        this.mFabAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onAddClicked();
            }
        });
        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}
        });
    }

    @Override
    public void onResume() // FIRE UP THE DATABASE
    {
        super.onResume();

        this.mPaymentInfoDatabaseAccess.open();
        this.mPaymentInfos = mPaymentInfoDatabaseAccess.getAllPaymentInfos();
        this.mPaymentInfoDatabaseAccess.close();
        PaymentInfoAdapter adapter = new PaymentInfoAdapter(this, mPaymentInfos);
        this.mListView.setAdapter(adapter);
    }

    public void onAddClicked() // START NEW PAYMENT INFO
    {
        ACTIVITY_INTENT = new Intent(this, PaymentInfoEditActivity.class);
        startActivity(ACTIVITY_INTENT);
    }

    public void onDeleteClicked(PaymentInfo paymentInfo) // DELETE DATABASE "COMPONENT"
    {
        this.mPaymentInfoDatabaseAccess.open();
        this.mPaymentInfoDatabaseAccess.delete(paymentInfo);
        this.mPaymentInfoDatabaseAccess.close();

        ArrayAdapter<PaymentInfo> adapter = (ArrayAdapter<PaymentInfo>) mListView.getAdapter();
        adapter.remove(paymentInfo);
        adapter.notifyDataSetChanged();
    }

    public void onEditClicked(PaymentInfo paymentInfo) // EDIT PAYMENT INFO
    {
        ACTIVITY_INTENT = new Intent(this, PaymentInfoEditActivity.class);
        ACTIVITY_INTENT.putExtra("PAYMENTINFO", paymentInfo);
        startActivity(ACTIVITY_INTENT);
    }

    private class PaymentInfoAdapter extends ArrayAdapter<PaymentInfo>
    {

        private PaymentInfoAdapter(Context context, List<PaymentInfo> objects) { super(context, 0, objects); }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = getLayoutInflater().inflate(R.layout.layout_list_item_paymentinfo, parent, false);
            }

            TextView tvLabel = (TextView) convertView.findViewById(R.id.tvTitle);
            TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            ImageView imgDelete = (ImageView) convertView.findViewById(R.id.imgDelete);
            ImageView imgCard = (ImageView) convertView.findViewById(R.id.imgCard);
            RelativeLayout reContent = (RelativeLayout) convertView.findViewById(R.id.Content);

            final PaymentInfo paymentInfo = mPaymentInfos.get(position);
            paymentInfo.setFullDisplayed(false);

            tvDate.setText("Updated:" + paymentInfo.getDate());
            tvLabel.setText(paymentInfo.getLabel());

            String cardType = paymentInfo.getCardType();
            switch (cardType)
            {
                case ("None"): imgCard.setImageResource(R.drawable.creditcard); break;
                case ("Visa"): imgCard.setImageResource(R.drawable.visa); break;
                case ("Master Card"): imgCard.setImageResource(R.drawable.mastercard); break;
                case ("American Express"): imgCard.setImageResource(R.drawable.americanexpress); break;
                case ("Discover"): imgCard.setImageResource(R.drawable.discover); break;
                case ("Other"): imgCard.setImageResource(R.drawable.creditcard); break;
                default: imgCard.setImageResource(R.drawable.creditcard); break;
            }

            reContent.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onEditClicked(paymentInfo);
                }
            });

            imgDelete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onDeleteClicked(paymentInfo);
                }
            });

            return convertView;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) // ACTION BAR BACK BUTTON RESPONSE
    {
        switch (item.getItemId())
        {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // THIS IS THE START OF THE SCRIPT FOR *** THE "TO LOGIN FUNCTION" THIS DETECTS THE ON PRESSED, START, TABS AND HOME BUTTONS IN ORDER TO INITIALIZE SECURITY "FAIL-SAFE"
    @Override
    public void onStart()
    {
        super.onStart();

        if (!APP_LOGGED_IN)
        {
            ACTIVITY_INTENT = new Intent(this, LoginActivity.class);
            this.finish(); // CLEAN UP AND END
            this.startActivity(ACTIVITY_INTENT); // GO TO LOGIN ACTIVITY
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
        {
            ACTIVITY_INTENT = new Intent(this, MainActivity.class);
            finish();
            this.startActivity(ACTIVITY_INTENT);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (!this.isFinishing()) { // HOME AND TABS AND SCREEN OFF
            if (ACTIVITY_INTENT == null) // NO PENDING ACTIVITIES ???(MAIN)--->(EDIT)???
            {
                new LogoutProtocol().logoutExecuteAutosaveOff(this);
            }
        }
        // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    }
    // -----------------------------------------------------------------------------------------------------------------------------------------------------------------------
}