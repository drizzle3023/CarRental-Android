package com.drizzle.carrental;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    protected Fragment curFragment;
    protected BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coverage_ready);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_coverage);

        if (((MyApplication) this.getApplication()).isLoggedIn()) {
            showFragment(R.id.frame_coverage, CoverageFragment.class);
        }
        else {
            showFragment(R.id.frame_coverage, CoverageEmptyFragment.class);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        if (((MyApplication) this.getApplication()).isLoggedIn()) {

            if (menuItem.getItemId() == R.id.navigation_history) {
                showFragment(R.id.frame_history, HistoryFragment.class);
            } else if (menuItem.getItemId() == R.id.navigation_coverage) {
                showFragment(R.id.frame_coverage, CoverageFragment.class);
            } else if (menuItem.getItemId() == R.id.navigation_profile) {
                showFragment(R.id.frame_profile, ProfileFragment.class);
            }

        }
        else {

            if (menuItem.getItemId() == R.id.navigation_history) {
                showFragment(R.id.frame_history, HistoryFragment.class);
            } else if (menuItem.getItemId() == R.id.navigation_coverage) {
                showFragment(R.id.frame_coverage, CoverageEmptyFragment.class);
            } else if (menuItem.getItemId() == R.id.navigation_profile) {
                showFragment(R.id.frame_profile, ProfileFragment.class);
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

}
