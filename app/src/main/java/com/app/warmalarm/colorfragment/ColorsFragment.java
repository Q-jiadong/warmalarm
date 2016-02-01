package com.app.warmalarm.colorfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.warmalarm.R;
import com.umeng.analytics.MobclickAgent;

public class ColorsFragment extends Fragment {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.colors_fragment_layout, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ColorsFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ColorsFragment");
    }
}
