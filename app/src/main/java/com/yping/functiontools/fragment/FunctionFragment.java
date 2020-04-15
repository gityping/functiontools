package com.yping.functiontools.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yping.functiontools.R;
import com.yping.functiontools.activity.BluetoothDevelopmentActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class FunctionFragment extends Fragment {
    TextView tvTab1;
    TextView tvTab2;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        } else {
            view = inflater.inflate(R.layout.fragment_function, container, false);
        }
        initView();
        return view;
    }

    private void initView() {
        tvTab1 = view.findViewById(R.id.tv_tab1);
        tvTab1.setOnClickListener(this::onClick);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_tab1:
                startActivity(new Intent(getActivity(), BluetoothDevelopmentActivity.class));
                break;
            case R.id.tv_tab2:
                break;
        }
    }
}
