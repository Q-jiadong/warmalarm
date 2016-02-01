package com.app.warmalarm.settingfragment;

import com.app.warmalarm.R;
import com.app.warmalarm.R.layout;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingFragment extends Fragment{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View settingrootview = inflater.inflate(R.layout.layout_setting, container, false);
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SettingFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SettingFragment");
    }
}
