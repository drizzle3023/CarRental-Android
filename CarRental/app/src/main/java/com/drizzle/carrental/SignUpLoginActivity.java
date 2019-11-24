package com.drizzle.carrental;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class SignUpLoginActivity extends AppCompatActivity {

    protected Fragment curFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_login);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.signup_login_radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if (checkedId == R.id.radio_signup) {
                    showFragment(R.id.frame_signup_frag_container, SignupFragment.class);
                }
                else {
                    showFragment(R.id.frame_login_frag_container, LoginFragment.class);
                }
            }
        });

    }

    public void showFragment(int layoutId, Class fragClass) {
        showFragment(layoutId, fragClass, null);
    }

    public void showFragment(int layoutId, Class fragClass, Bundle bundle) {

        if (fragClass == null)
            return;

        if (curFragment != null && fragClass.isInstance(curFragment))
            return;

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);

        if (curFragment != null) {
            fragmentTransaction.hide(curFragment);
        }

        Fragment fragment = fragmentManager.findFragmentByTag(fragClass.toString());

        try {
            if (fragment == null) {
                fragment = (Fragment) fragClass.newInstance();
                fragment.setArguments(bundle);
                fragmentTransaction.add(layoutId, fragment, fragClass.toString());
            } else {
                fragment.setArguments(bundle);
                fragmentTransaction.show(fragment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        curFragment = fragment;
        fragmentTransaction.commit();
    }

}
