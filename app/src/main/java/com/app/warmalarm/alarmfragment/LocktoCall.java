package com.app.warmalarm.alarmfragment;

import com.app.warmalarm.R;
import com.app.warmalarm.R.id;
import com.app.warmalarm.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LocktoCall extends Activity implements OnTouchListener
{
	private TextView locktime;//显示闹钟时间
	private TextView lockcontent;//显示闹钟提醒内容
	private ViewGroup rootview; //
	private ImageView phoneround; //圆环
	private int deltaX, deltaY, positionX, positionY;//拉动距离，和位置参数
	private int originalL, originalR, originalT, originalB;  //初始位置
	private static String phone;  //电话号码
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lockscreen);
		
		locktime = (TextView)findViewById(R.id.locktime);
		lockcontent = (TextView)findViewById(R.id.lockcontent);
		phoneround = (ImageView)findViewById(R.id.unlock);
		rootview = (ViewGroup)findViewById(R.id.rootview);
		
		Intent intent = getIntent();
		String time = intent.getStringExtra("time");
		String name = intent.getStringExtra("name");
		phone = intent.getStringExtra("phone");
		
		locktime.setText(time);
		lockcontent.setText("快给" + " " + name + " " + "打电话");
		
		phoneround.setOnTouchListener(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode==KeyEvent.KEYCODE_BACK)
		{
			return false;
		}
		if(keyCode==KeyEvent.KEYCODE_MENU);
		{
			return false;
		}
	}
	
	public boolean onTouch(View v, MotionEvent mv)
	{
		switch (mv.getAction() & MotionEvent.ACTION_MASK)
		{
		case MotionEvent.ACTION_DOWN:
			//当前所在位置
			positionX = (int)mv.getRawX();
			positionY = (int)mv.getRawY();
			//初始位置坐标
			originalL = v.getLeft();  
			originalR = v.getRight();  
			originalT = v.getTop();  
			originalB = v.getBottom();
	        break; 
	    case MotionEvent.ACTION_MOVE:
	    	//获得手指移动到的地方
	    	int movetoX = (int)mv.getRawX();
	    	int movetoY = (int)mv.getRawY();
	    	
	    	//计算移动距离
	    	deltaX = movetoX - positionX;
	    	deltaY = movetoY - positionY;
	    	
			int l = v.getLeft();  
	        int r = v.getRight();  
	        int t = v.getTop();  
	        int b = v.getBottom();
	    	
            // 更改imageView在窗体的位置  
            v.layout(l + deltaX, t + deltaY, r + deltaX, b + deltaY);
	    	//获取移动后的位置以供下次移动的初始位置
	    	positionX = (int)mv.getRawX();
			positionY = (int)mv.getRawY();
	        break;
	    case MotionEvent.ACTION_UP:
	    	/**
			 * 判断是否拉出了圈外，圈的直径是250
			 */
			//先获得电话图标中心坐标
			int x = (originalL + originalR)/2;
			int y = (originalT + originalB)/2;
			//up的时候view的坐标
			int vx = (v.getLeft() + v.getRight())/2;
			int vy = (v.getTop() + v.getBottom())/2;
			//拉出的距离
			int distance = (vx - x)*(vx -x) + (vy - y)*(vy - y);
			if(distance > (550*550/4))
			{
				//呼出电话
				makeaCall();
				finish();
			}
			v.layout(originalL, originalT, originalR, originalB);
	    	break;
	    }  
	    rootview.invalidate();  
	    return true;
	}
	
	private boolean makeaCall()
	{
		//获取本次通话的时间(单位:秒)
	    //int time = 0;
	    //判断是否正在通话
	   // boolean isCalling = false;
	    //控制循环是否结束
	    //boolean isFinish = true;
	    //ExecutorService service = Executors.newSingleThreadExecutor();
	    
	    //呼出电话
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
		startActivity(intent);
		
		/*
		//获取通话时长，首先获取系统服务TELEPHONY_SERVICE
		 * 
		 */
		return false;
	}
}