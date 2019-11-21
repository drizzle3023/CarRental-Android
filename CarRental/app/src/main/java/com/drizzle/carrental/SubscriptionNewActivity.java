package com.drizzle.carrental;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.*;
import android.view.View;

public class SubscriptionNewActivity extends Activity implements AdapterView.OnItemSelectedListener {

    String[] countryNames={"Car1","Car2","Car3"};
    int flags[] = {R.drawable.icon_history, R.drawable.icon_profile, R.drawable.icon_my_coverage};

    private ImageButton buttonBack;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_new);

        Spinner spinner = (Spinner) findViewById(R.id.subscriptionCarTypeSpinner);
        spinner.setOnItemSelectedListener(this);

        CustomAdapter customAdapter=new CustomAdapter(getApplicationContext(),flags,countryNames);
        spinner.setAdapter(customAdapter);

        buttonBack = findViewById(R.id.subscriptionNewBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        Toast.makeText(getApplicationContext(), countryNames[position], Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}