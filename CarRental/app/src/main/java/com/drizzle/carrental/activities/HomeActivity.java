package com.drizzle.carrental.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.drizzle.carrental.fragments.CoverageFragmentEmpty;
import com.drizzle.carrental.fragments.CoverageFragmentFull;
import com.drizzle.carrental.fragments.HistoryFragmentEmpty;
import com.drizzle.carrental.fragments.HistoryFragmentFull;
import com.drizzle.carrental.fragments.ProfileFragmentFull;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.fragments.ProfileFragmentEmpty;
import com.drizzle.carrental.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    static final int ADD_COVERAGE_ACTIVITY_REQUEST = 1;

    protected Fragment curFragment;
    protected BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coverage_ready);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_coverage);

        if (Globals.isLoggedIn) {
            showFragment(R.id.frame_coverage, CoverageFragmentFull.class);
        }
        else {
            showFragment(R.id.frame_coverage, CoverageFragmentEmpty.class);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        if (Globals.isLoggedIn) {

            if (menuItem.getItemId() == R.id.navigation_history) {
                showFragment(R.id.frame_history, HistoryFragmentFull.class);
            } else if (menuItem.getItemId() == R.id.navigation_coverage) {
                showFragment(R.id.frame_coverage, CoverageFragmentFull.class);
            } else if (menuItem.getItemId() == R.id.navigation_profile) {
                showFragment(R.id.frame_profile, ProfileFragmentFull.class);
            }

        }
        else {

            if (menuItem.getItemId() == R.id.navigation_history) {
                showFragment(R.id.frame_history, HistoryFragmentEmpty.class);
            } else if (menuItem.getItemId() == R.id.navigation_coverage) {
                showFragment(R.id.frame_coverage, CoverageFragmentEmpty.class);
            } else if (menuItem.getItemId() == R.id.navigation_profile) {
                showFragment(R.id.frame_profile, ProfileFragmentEmpty.class);
            }

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

    @Override
    public void onBackPressed() {

        this.finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ADD_COVERAGE_ACTIVITY_REQUEST) {

            if (resultCode == RESULT_OK) {

                updateView();
            }
        }
    }

    public void updateView() {


    }
}
