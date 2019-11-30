package com.drizzle.carrental;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

public class OnboardingActivity extends Activity {

    private TextView buttonGotit;
    private ImageButton buttonIllustrator;
    private ImageButton buttonDot;
    private TextView textIllustrationTitle;
    private TextView textIllustrationSubTitle;
    private TextView textIllustrationContent;

    private int dotPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        textIllustrationTitle = findViewById(R.id.textillustrationTitle);
        textIllustrationSubTitle = findViewById(R.id.textillustrationSubtitle);
        textIllustrationContent = findViewById(R.id.textillustrationContent);

        buttonGotit = findViewById(R.id.buttonGotit);
        buttonGotit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d("CarRental", "Gotit pressed");
                Intent intent=new Intent(OnboardingActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        buttonIllustrator = findViewById(R.id.imagebuttonillustrator);
        buttonIllustrator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d("CarRental", "Illustrator pressed");
                updateContent();
            }
        });

        buttonDot = findViewById(R.id.buttonDot);
        buttonDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d("CarRental", "DotButton pressed");
                updateContent();
            }
        });
    }

    private void updateContent() {
        if (dotPage == 1) {
            buttonDot.setImageResource(R.drawable.dot_2);
            buttonIllustrator.setImageResource(R.drawable.illustration_pig);
            textIllustrationTitle.setText(R.string.illustration_title1);
            textIllustrationSubTitle.setText(R.string.illustration_subtitle1);
            textIllustrationContent.setText(R.string.illustration_content1);
            dotPage = 2;
        }
        else {
            buttonDot.setImageResource(R.drawable.dot_1);
            buttonIllustrator.setImageResource(R.drawable.illustration_car);
            textIllustrationTitle.setText(R.string.illustration_title2);
            textIllustrationSubTitle.setText(R.string.illustration_subtitle2);
            textIllustrationContent.setText(R.string.illustration_content2);
            dotPage = 1;
        }
    }
}