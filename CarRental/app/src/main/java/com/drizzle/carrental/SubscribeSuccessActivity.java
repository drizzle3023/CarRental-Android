package com.drizzle.carrental;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SubscribeSuccessActivity extends Activity {

    private Button buttonGotIt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribesuccess);

        buttonGotIt = findViewById(R.id.subscribe_button);
        buttonGotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(SubscribeSuccessActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

    }

}