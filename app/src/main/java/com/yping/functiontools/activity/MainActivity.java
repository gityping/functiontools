package com.yping.functiontools.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.yping.functiontools.R;
import com.yping.functiontools.adapter.TitleFragmentPagerAdapter;
import com.yping.functiontools.fragment.FunctionFragment;
import com.yping.functiontools.fragment.HomeFragment;
import com.yping.functiontools.fragment.MineFragment;
import com.yping.functiontools.fragment.SocialFragment;
import com.yping.functiontools.fragment.ToolsFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.tab)
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new FunctionFragment());
        fragments.add(new ToolsFragment());
        fragments.add(new SocialFragment());
        fragments.add(new MineFragment());
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
        viewpager.setOffscreenPageLimit(5);
//        viewpager.setCurrentItem(2);
    }
}
