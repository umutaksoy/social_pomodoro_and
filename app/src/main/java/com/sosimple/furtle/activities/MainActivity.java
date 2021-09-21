package com.sosimple.furtle.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.sosimple.furtle.R;
import com.sosimple.furtle.fragments.FragmentAccount;
import com.sosimple.furtle.fragments.FragmentRecent;
import com.sosimple.furtle.utils.AppBarLayoutBehavior;
import com.sosimple.furtle.utils.SharedPref;
import com.sosimple.furtle.utils.Tools;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class MainActivity extends AppCompatActivity {

    View view;
    SharedPref sharedPref;
    SharedPreferences preferences;
    String androidId;
    MyApplication myApplication;
    private TextView title_toolbar;
    CoordinatorLayout coordinatorLayout;
    private BottomNavigationView navigation;
    private ViewPager viewPager;
    int pager_number = 2;
    MenuItem prevMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        setContentView(R.layout.activity_main);

        view = findViewById(android.R.id.content);

        sharedPref = new SharedPref(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        AppBarLayout appBarLayout = findViewById(R.id.appbarLayout);
        ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).setBehavior(new AppBarLayoutBehavior());

        myApplication = MyApplication.getInstance();

        title_toolbar = findViewById(R.id.title_toolbar);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        navigation = findViewById(R.id.navigation);
        navigation.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        initViewPager();

        initToolbarIcon();

    }

    public void initViewPager() {
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(pager_number);
        navigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_settings:
                    viewPager.setCurrentItem(1);
                    return true;
            }
            return false;
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);

                if (viewPager.getCurrentItem() == 0) {
                    title_toolbar.setText(getResources().getString(R.string.app_name));
                } else if (viewPager.getCurrentItem() == 1) {
                    title_toolbar.setText(getResources().getString(R.string.title_nav_profile));
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void initToolbarIcon() {
        findViewById(R.id.toolbar).setBackgroundColor(getResources().getColor(R.color.colorToolbarDark));
        navigation.setBackgroundColor(getResources().getColor(R.color.colorToolbarDark));
    }

    public class MyAdapter extends FragmentPagerAdapter {

        MyAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new FragmentRecent();
                case 1:
                    return new FragmentAccount();
            }
            return null;
        }

        @Override
        public int getCount() {
            return pager_number;
        }

    }

}
