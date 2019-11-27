package com.drizzle.carrental;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CoverageActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    protected Fragment curFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coverage_ready);

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        if (menuItem.getItemId() == R.id.frame_history) {
            //showFragment(menuItem.getItemId(), HistoryFragment.class);
        }
        else if (menuItem.getItemId() == R.id.frame_coverage) {
            //showFragment(menuItem.getItemId(), CoverageFragment.class);
        }
        else if (menuItem.getItemId() == R.id.frame_profile) {
            //showFragment(menuItem.getItemId(), CoverageFragment.class);
        }

        return true;

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
