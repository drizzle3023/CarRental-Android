package com.drizzle.carrental.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.*;
import android.view.View;

import com.drizzle.carrental.adapters.CustomAdapterSubscriptionCarTypeSelect;
import com.drizzle.carrental.R;
import com.drizzle.carrental.enumerators.ServiceArea;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.models.SubscriptionModel;
import com.drizzle.carrental.models.VehicleType;

import java.util.ArrayList;

public class SubscriptionNewActivity extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener, CheckBox.OnCheckedChangeListener {


    //variables to fetch data from database
    ArrayList<VehicleType> vehicleTypes;
    ArrayList<ServiceArea> serviceAreas;
    SubscriptionModel subscriptionInfo;

    //control handlers
    Handler handler;
    private Spinner spinner;
    private Button buttonSubscribe;
    private ImageButton buttonBack;
    private CheckBox checkBoxUs;
    private CheckBox checkBoxEurope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_new);

        getControlHandlersAndLinkActions();


        initVariables();

        updateView();


    }

    private void getControlHandlersAndLinkActions() {

        spinner = (Spinner) findViewById(R.id.subscriptionCarTypeSpinner);
        spinner.setOnItemSelectedListener(this);

        buttonBack = (ImageButton) findViewById(R.id.button_back);
        buttonBack.setOnClickListener(this);

        buttonSubscribe = (Button) findViewById(R.id.button_subscribe);
        buttonSubscribe.setOnClickListener(this);

        checkBoxUs = (CheckBox) findViewById(R.id.checkbox_us);
        checkBoxEurope = (CheckBox) findViewById(R.id.checkbox_europe);

        checkBoxUs.setOnCheckedChangeListener(this);
        checkBoxEurope.setOnCheckedChangeListener(this);

    }

    private void updateView() {

        CustomAdapterSubscriptionCarTypeSelect customAdapter = new CustomAdapterSubscriptionCarTypeSelect(getApplicationContext(), vehicleTypes);
        spinner.setAdapter(customAdapter);


    }

    private void initVariables() {

        vehicleTypes = new ArrayList<>();
        serviceAreas = new ArrayList<>();


        /**
         * Fetch data  from server
         */
        // - fetch vehicle types
        //should be replaced with API
        for (int i = 0; i < 3; i ++) {
            VehicleType item = new VehicleType();
            item.setIconURL("https://image.flaticon.com/icons/png/512/55/55283.png");
            item.setName("Vehicle " + i + 1);
            item.setId(i + 1);

            vehicleTypes.add(item);
        }

        // - fetch service area
        //should be replaced with API
        ServiceArea item1 = new ServiceArea();
        item1.setId(1);
        item1.setAreaName("United States");
        ServiceArea item2 = new ServiceArea();
        item2.setId(1);
        item2.setAreaName("Europe");

        serviceAreas.add(item1);
        serviceAreas.add(item2);


        /**
         * Init local variables
         */
        if (vehicleTypes.isEmpty()) {
            Globals.selectedVehicleType = vehicleTypes.get(0);
        }
        Globals.selectedVehicleType = null;

        if (!serviceAreas.isEmpty()) {
            Globals.selectedServiceArea = serviceAreas.get(0);
        }
        Globals.selectedServiceArea = null;
    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        //Toast.makeText(getApplicationContext(), countryNames[position], Toast.LENGTH_LONG).show();



    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button_subscribe) {
            Intent intent = new Intent(SubscriptionNewActivity.this, SignUpLoginActivity.class);
            startActivity(intent);
        }

        if (view.getId() == R.id.button_back) {
            finish();
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        if (compoundButton.getId() == R.id.checkbox_us) {

            checkBoxEurope.setChecked(false);
            Globals.selectedVehicleType = vehicleTypes.get(1);

        }
        else if (compoundButton.getId() == R.id.checkbox_europe) {

            checkBoxUs.setChecked(false);
            Globals.selectedVehicleType = vehicleTypes.get(0);

        }

    }
}