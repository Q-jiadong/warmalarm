package com.app.warmalarm;

import com.app.warmalarm.alarmfragment.AlarmFragment;
import com.app.warmalarm.colorfragment.ColorsFragment;
import com.app.warmalarm.settingfragment.SettingFragment;
import com.app.warmalarm.wenxinfragment.WenxinFragment;
import com.umeng.analytics.MobclickAgent;

import android.app.ActionBar;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WarmAlarmActivity extends FragmentActivity {
    public static final String[] CONTENT = new String[] {"闹钟", "温馨", "点滴", "设置" };
    private static final int[] TAB_ICONS = new int[] {
        R.drawable.clock_tab_selector,
        R.drawable.wenxin_tab_selector,
        R.drawable.color_tab_selector,
        R.drawable.setting_tab_selector
    };
    public static TextView actionbarTitle;
    private static ImageView mMenu;
    private static boolean isExit = false;
    
    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_indicator_layout);
        initActionBar();
        initViewPager();
        MobclickAgent.openActivityDurationTrack(false);
    }
    
    private void initViewPager() {
        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        FragmentPagerAdapter adapter = new TabViewAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        
        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);
    }
    
    private void initActionBar() {
        ActionBar actionbar = getActionBar();
        if(actionbar != null) {
            actionbar.setDisplayShowCustomEnabled(true);
            actionbar.setCustomView(R.layout.layout_actionbar);
            actionbar.setDisplayShowHomeEnabled(false);
            actionbar.setDisplayShowTitleEnabled(false);
            actionbarTitle = (TextView)actionbar.getCustomView().findViewById(R.id.actionbar_title);
            mMenu = (ImageView)actionbar.getCustomView().findViewById(R.id.actionbar_menu);
            mMenu.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    AlarmFragment.db.execSQL("delete from alarm_info");
                    recreate();
                }
            });
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再次点击退出应用程序！", Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            System.exit(0);
            MobclickAgent.onKillProcess(this);
        }
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
    
    class TabViewAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
        
        public TabViewAdapter(FragmentManager fm) {
            super(fm);
        }
        
        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length];
        }

        @Override
        public int getIconResId(int index) {
            return TAB_ICONS[index];
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch(position % CONTENT.length) {
            case 0:
                fragment = new AlarmFragment();
                break;
            case 1:
                fragment = new WenxinFragment();
                break;
            case 2:
                fragment = new ColorsFragment();
                break;
            case 3:
                fragment = new SettingFragment();
                break;
            }
            
            return fragment;
        }
        @Override
        public int getCount() {
            return CONTENT.length;
        }
        
    }

}