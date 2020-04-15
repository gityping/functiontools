package com.yping.functiontools.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.yping.functiontools.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class HomeFragment extends Fragment {

    List<Fragment> fragments = new ArrayList<>();
    List<String> titles = new ArrayList<>();
    @BindView(R.id.tl_tabs)
    TabLayout tabLayout;
    @BindView(R.id.vp_content)
    ViewPager viewPager;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home, container, false);
        }
        initView(view);
        return view;
    }

    private void initView(View view) {
        tabLayout = view.findViewById(R.id.tl_tabs);
        viewPager = view.findViewById(R.id.vp_content);
        titles = new ArrayList<>();
        fragments = new ArrayList<>();

        titles.add("推荐");
        titles.add("热点");
        titles.add("视频");
        titles.add("北京");
        titles.add("社会");
        titles.add("娱乐");
        titles.add("问答");
        titles.add("图片");
        titles.add("科技");
        titles.add("汽车");
        titles.add("体育");
        titles.add("财经");
        titles.add("军事");
        titles.add("国际");
        for (int i = 0; i < titles.size(); i++) {
            CommonFragment fragment = CommonFragment.newInstance(titles.get(i), i + "");
            fragments.add(fragment);

        }
        //mTabLayout.setTabMode(TabLayout.SCROLL_AXIS_HORIZONTAL);//设置tab模式，当前为系统默认模式
        for (int i = 0; i < titles.size(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(titles.get(i)));//添加tab选项
        }

        FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            //ViewPager与TabLayout绑定后，这里获取到PageTitle就是Tab的Text
            @Override
            public CharSequence getPageTitle(int position) {
                return titles.get(position);
            }
        };
        viewPager.setAdapter(mAdapter);
        viewPager.setOffscreenPageLimit(5);
        tabLayout.setupWithViewPager(viewPager);//将TabLayout和ViewPager关联起来。
        tabLayout.setTabsFromPagerAdapter(mAdapter);//给Tabs设置适配器

    }
}
