package com.yping.functiontools.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yping.functiontools.R;

import butterknife.BindView;

public class CommonFragment extends Fragment {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    private String value1, value2;
    private View view;
    private static final String KEY = "title";

    public static CommonFragment newInstance(String s, String s1) {
        Bundle args = new Bundle();
        args.putString(KEY, s);
        args.putString("value2", s1);
        CommonFragment fragment = new CommonFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_common, container, false);
        initView(view);
        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            value1 = args.getString(KEY);
            value2 = args.getString("value2");
        }

    }

    private void initView(View view) {
        tvTitle = view.findViewById(R.id.tv_title);
        initData();
    }

    private void initData() {
        tvTitle.setText(value1);
    }
}
