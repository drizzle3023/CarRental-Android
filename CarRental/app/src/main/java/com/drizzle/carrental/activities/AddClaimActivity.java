package com.drizzle.carrental.activities;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drizzle.carrental.R;
import com.drizzle.carrental.models.Company;
import com.drizzle.carrental.models.Coverage;

import java.util.GregorianCalendar;

public class AddClaimActivity extends Activity implements View.OnClickListener {

    /**
     * UI Control Handlers
     */
    private ImageButton buttonBack;
    private Button buttonSave;


    /**
     * get control handlers by id and add listenres
     */
    private void getControlHandlersAndLinkActions() {

        buttonBack = findViewById(R.id.button_back);
        buttonSave = findViewById(R.id.button_save);

    }

    /**
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_claim);

        getControlHandlersAndLinkActions();

    }


    /**
     * OnClick Handlers
     *
     * @param view
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.button_back:

                break;


        }
    }


}
