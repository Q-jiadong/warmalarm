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
	private TextView locktime;//��ʾ����ʱ��
	private TextView lockcontent;//��ʾ������������
	private ViewGroup rootview; //
	private ImageView phoneround; //Բ��
	private int deltaX, deltaY, positionX, positionY;//�������룬��λ�ò���
	private int originalL, originalR, originalT, originalB;  //��ʼλ��
	private static String phone;  //�绰����
	
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
		lockcontent.setText("���" + " " + name + " " + "��绰");
		
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
			//��ǰ����λ��
			positionX = (int)mv.getRawX();
			positionY = (int)mv.getRawY();
			//��ʼλ������
			originalL = v.getLeft();  
			originalR = v.getRight();  
			originalT = v.getTop();  
			originalB = v.getBottom();
	        break; 
	    case MotionEvent.ACTION_MOVE:
	    	//�����ָ�ƶ����ĵط�
	    	int movetoX = (int)mv.getRawX();
	    	int movetoY = (int)mv.getRawY();
	    	
	    	//�����ƶ�����
	    	deltaX = movetoX - positionX;
	    	deltaY = movetoY - positionY;
	    	
			int l = v.getLeft();  
	        int r = v.getRight();  
	        int t = v.getTop();  
	        int b = v.getBottom();
	    	
            // ����imageView�ڴ����λ��  
            v.layout(l + deltaX, t + deltaY, r + deltaX, b + deltaY);
	    	//��ȡ�ƶ����λ���Թ��´��ƶ��ĳ�ʼλ��
	    	positionX = (int)mv.getRawX();
			positionY = (int)mv.getRawY();
	        break;
	    case MotionEvent.ACTION_UP:
	    	/**
			 * �ж��Ƿ�������Ȧ�⣬Ȧ��ֱ����250
			 */
			//�Ȼ�õ绰ͼ����������
			int x = (originalL + originalR)/2;
			int y = (originalT + originalB)/2;
			//up��ʱ��view������
			int vx = (v.getLeft() + v.getRight())/2;
			int vy = (v.getTop() + v.getBottom())/2;
			//�����ľ���
			int distance = (vx - x)*(vx -x) + (vy - y)*(vy - y);
			if(distance > (550*550/4))
			{
				//�����绰
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
		//��ȡ����ͨ����ʱ��(��λ:��)
	    //int time = 0;
	    //�ж��Ƿ�����ͨ��
	   // boolean isCalling = false;
	    //����ѭ���Ƿ����
	    //boolean isFinish = true;
	    //ExecutorService service = Executors.newSingleThreadExecutor();
	    
	    //�����绰
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
		startActivity(intent);
		
		/*
		//��ȡͨ��ʱ�������Ȼ�ȡϵͳ����TELEPHONY_SERVICE
		 * 
		 */
		return false;
	}
}