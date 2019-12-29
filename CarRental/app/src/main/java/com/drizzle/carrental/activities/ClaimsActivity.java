package com.drizzle.carrental.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.drizzle.carrental.R;
import com.drizzle.carrental.adapters.CustomAdapterForClaimListView;
import com.drizzle.carrental.enumerators.ClaimState;
import com.drizzle.carrental.models.Claim;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class ClaimsActivity extends Activity implements View.OnClickListener {

    /**
     * UI Control Handlers
     */
    private Button buttonFileAClaim;
    private ListView listView;

    private static CustomAdapterForClaimListView adapter;

    ArrayList<Claim> dataModels;

    /**
     * get control handlers by id and add listenres
     */
    private void getControlHandlersAndLinkActions() {

        buttonFileAClaim = findViewById(R.id.button_file_a_claim);
        listView = findViewById(R.id.list_claims);

        dataModels = new ArrayList<>();

        prepareTestData();

        adapter = new CustomAdapterForClaimListView(dataModels, this);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {

            Claim claim = dataModels.get(position);

        });

        buttonFileAClaim.setOnClickListener(this);
    }

    private void prepareTestData() {

        for (int i = 0; i < 10; i++) {

            Claim claim = new Claim();
            claim.setAddressHappened("Independence Distreet, 37 ");
            if (i % 4 == 0) {
                claim.setClaimState(ClaimState.APPROVED);
            } else if (i % 4 == 1) {
                claim.setClaimState(ClaimState.NOT_APPROVED);
            } else if (i % 4 == 2) {
                claim.setClaimState(ClaimState.INCOMPLETE);
            } else {
                claim.setClaimState(ClaimState.EXPERT_UNDERGOING);
            }
            claim.setWhenHappened(new GregorianCalendar());
            claim.setWhatHappened("Glass Damaged");

            dataModels.add(claim);
        }
    }

    /**
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claims);

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

            case R.id.button_file_a_claim:
                navigateToFileAClaimActivity();
                break;
        }
    }

    private void navigateToFileAClaimActivity() {

        Intent intent = new Intent(ClaimsActivity.this, AddClaimActivity.class);
        startActivity(intent);
    }
}
