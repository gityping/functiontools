package com.yping.functiontools;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.graphics.drawable.Drawable;
import android.os.Bundle;


import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        tabLayout = findViewById(R.id.tab);
        viewpager = findViewById(R.id.viewpager);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new HomeFragment());
        fragments.add(new HomeFragment());
        fragments.add(new HomeFragment());
        fragments.add(new HomeFragment());
        TitleFragmentPagerAdapter adapter = new TitleFragmentPagerAdapter(getSupportFragmentManager(), fragments, new String[]{"环球", "商机", "商城", "尊享", "我的"});
        viewpager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewpager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            Drawable d = null;
            switch (i) {
                case 0:
                    d = getResources().getDrawable(R.drawable.selector_home);
                    break;
                case 1:
                    d = getResources().getDrawable(R.drawable.selector_shop);
                    break;
                case 2:
                    d = getResources().getDrawable(R.drawable.selector_shopping);
                    break;
                case 3:
                    d = getResources().getDrawable(R.drawable.selector_zunxiang);
                    break;
                case 4:
                    d = getResources().getDrawable(R.drawable.selector_mine);
                    break;
            }
            tab.setIcon(d);
        }
//        viewpager.setCurrentItem(2);
    }
}
