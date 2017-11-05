package com.gerardogandeaga.cyberlock;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TestActivity extends AppCompatActivity {
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Button start = findViewById(R.id.btnTest);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = View.inflate(mContext, R.layout.dialog_custom_loadbar, null);
                LinearLayout ln = v.findViewById(R.id.anim);
                TextView tv = v.findViewById(R.id.test);

                TranslateAnimation left2right = new TranslateAnimation(0, 350, 0, 0);
                left2right.setDuration(1000);
                left2right.setFillAfter(true);

                tv.startAnimation(left2right);
                if (left2right.hasEnded());

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setView(v);
                final AlertDialog dialog = builder.show();
            }
        });
    }
}
