package com.drizzle.carrental.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.drizzle.carrental.R;
import com.drizzle.carrental.fragments.OnboardingSlideFragment1;
import com.drizzle.carrental.fragments.OnboardingSlideFragment2;

public class OnboardingActivity extends FragmentActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 2;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager2 viewPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private FragmentStateAdapter pagerAdapter;

    private TextView buttonGotit;
    private ImageButton buttonDot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding1);

        buttonGotit = findViewById(R.id.buttonGotit);
        buttonGotit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d("CarRental", "Gotit pressed");
                Intent intent=new Intent(OnboardingActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonDot = findViewById(R.id.buttonDot);
        buttonDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (viewPager.getCurrentItem() == 0) {

                    viewPager.setCurrentItem(1);
                    buttonDot.setImageResource(R.drawable.dot_2);
                }
                else {

                    viewPager.setCurrentItem(0);
                    buttonDot.setImageResource(R.drawable.dot_1);
                }
            }
        });

        // Instantiate a ViewPager2 and a PagerAdapter.
        viewPager = findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (viewPager.getCurrentItem() == 0) {

                    buttonDot.setImageResource(R.drawable.dot_1);
                }
                else {

                    buttonDot.setImageResource(R.drawable.dot_2);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {

            if (position == 0) {
                return new OnboardingSlideFragment1();
            }
            else {
                return new OnboardingSlideFragment2();
            }
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}
