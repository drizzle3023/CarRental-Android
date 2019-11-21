package com.drizzle.carrental;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

public class MyCoverageEmptyActivity extends Activity {

    private ImageButton buttonBack;
    private ImageButton buttonContact;
    private ImageButton buttonHelp;
    private Button buttonLearnMore;
    private ImageButton buttonHistory;
    private ImageButton buttonCoverage;
    private ImageButton buttonProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coverage_empty);

        buttonBack = findViewById(R.id.coverageBackbutton);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        buttonLearnMore = findViewById(R.id.coverageButtonLearnmore);
        buttonLearnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(MyCoverageEmptyActivity.this,SubscriptionNewActivity.class);
                startActivity(intent);
            }
        });

    }

}