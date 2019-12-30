package com.drizzle.carrental.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.drizzle.carrental.globals.SharedHelper;
import com.drizzle.carrental.models.MyProfile;
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
        SharedHelper.putKey(this, "access_token", "bstohcty6u56epm09pnplrlcgpv07dj6ur6korqomx2nk0lmcy8w97anye3pxj7xoey46ckmabnp7pht3t92ssgaoy5t007ojy557aaoimc2yw25tg2ke314bdw5w6m4");

        String strAccessToken = SharedHelper.getKey(this, "access_token");
        if (strAccessToken != null) {

            //check validation of API token
            Globals.AccessToken = strAccessToken;

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

            Globals.isLoggedIn = true;
            Globals.profile = profile;

            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 3000);

        } else {

            Globals.isLoggedIn = false;

            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, OnboardingActivity.class);
                    //Intent intent = new Intent(SplashActivity.this, AddClaimActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 3000);

        }


    }




}