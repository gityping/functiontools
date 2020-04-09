package com.yping.functiontools;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.yping.functiontools.fragment.CommonFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends androidx.fragment.app.Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;

    List<Fragment> fragments = new ArrayList<>();
    List<String> titles = new ArrayList<>();
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home, container, false);
        }
        initView(view);
        return view;
    }

    private void initView(View view) {
        tabLayout = view.findViewById(R.id.tl_tabs);
        viewPager = view.findViewById(R.id.vp_content);
        fragments.add(CommonFragment.newInstance("11111", "11111"));
        fragments.add(CommonFragment.newInstance("22222", "22222"));
        fragments.add(CommonFragment.newInstance("33333", "33333"));
        fragments.add(CommonFragment.newInstance("44444", "44444"));
        fragments.add(CommonFragment.newInstance("55555", "55555"));
        titles.add("fragment1");
        titles.add("fragment2");
        titles.add("fragment3");
        titles.add("fragment4");
        titles.add("fragment5");
        viewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                super.destroyItem(container, position, object);
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {

                return titles.get(position);
            }
        });

        tabLayout.setupWithViewPager(viewPager);
    }

}
