package com.drizzle.carrental.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.drizzle.carrental.models.MyProfile;
import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.R;

import java.util.GregorianCalendar;

public class SplashActivity extends Activity {


    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);


        //load saved api token
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        String strApiToken = prefs.getString(Constants.SHARED_PREFERENCE_KEY_API_TOKEN, null);

        strApiToken = "apitoken";
        if (strApiToken != null) {

            //check validation of API token
            ((Globals) this.getApplication()).setAPIToken(strApiToken);


            // Todo list
            // Call API to fetch profile from server
            MyProfile profile = new MyProfile();

            profile.setFirstName("Tiny");
            profile.setLastName("Blonde");
            profile.setEmailAddress("drizzle3023@hotmail.com");
            profile.setAddress("Beijing, China");
            profile.setPhoneNumber("+8613522171058");
            profile.setBirthday(new GregorianCalendar(1996, 1, 1));
            profile.setCreditCardNo("4242 4242 4242");

            ((Globals) this.getApplication()).setLoggedIn(true);
            ((Globals) this.getApplication()).setProfile(profile);

            handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent=new Intent(SplashActivity.this,HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            },3000);

        }
        else {

            ((Globals) this.getApplication()).setLoggedIn(false);

            handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent=new Intent(SplashActivity.this,OnboardingActivity.class);
                    startActivity(intent);
                    finish();
                }
            },3000);

        }



    }
}