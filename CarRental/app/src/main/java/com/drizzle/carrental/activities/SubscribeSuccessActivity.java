package com.drizzle.carrental.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.drizzle.carrental.R;
import com.drizzle.carrental.globals.Globals;

public class SubscribeSuccessActivity extends Activity {

    private Button buttonGotIt;

    private ImageButton imageButtonBack;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribesuccess);

        buttonGotIt = findViewById(R.id.button_subscribe);
        buttonGotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Globals.isLoggedIn = true;
                Intent intent = new Intent(SubscribeSuccessActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();


            }
        });

        imageButtonBack = findViewById(R.id.button_back_to_onboarding);
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (doubleBackToExitPressedOnce) {

                    return;
                }

                doubleBackToExitPressedOnce = true;
                Toast.makeText(SubscribeSuccessActivity.this, getResources().getString(R.string.press_back_again), Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce=false;
                    }
                }, 2000);
            }
        });

    }

    @Override
    public void onBackPressed() {

        Globals.isLoggedIn = true;
        Intent intent = new Intent(SubscribeSuccessActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }
}