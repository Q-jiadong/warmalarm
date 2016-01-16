package com.app.warmalarm;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;

public class BasePerformanceActivity extends FragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initActionBar();
	}

	private void initActionBar() {
		ActionBar actionbar = getActionBar();
		if(actionbar != null && getActionBar().isShowing()) {
		    
		} else {
		    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}
}
