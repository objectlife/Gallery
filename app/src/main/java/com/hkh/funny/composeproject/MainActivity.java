package com.hkh.funny.composeproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.weshare.ComposeActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.goto_compose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComposeActivity.start(v.getContext());
            }
        });
    }
}
