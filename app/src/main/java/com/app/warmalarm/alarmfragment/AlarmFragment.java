package com.app.warmalarm.alarmfragment;

import java.util.ArrayList;
import java.util.HashMap;

import com.app.warmalarm.R;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

public class AlarmFragment extends Fragment {

    private ListView mWenxinView;
    private AlarmFragment mAlarmFragment;
    public static SQLiteDatabase db;
    //��ϵ��������
    private ArrayList<String> mNamelist = new ArrayList<String>();
    //��ϵ��-�绰��
    private HashMap<String, String> mNamePhone = new HashMap<String, String>();
    private ImageView mNullView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        showAlarmList();
    }
    
    //create the alarm fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View alarmRootView = inflater.inflate(R.layout.warmalarm_layout, container, false);
        
        mWenxinView = (ListView)alarmRootView.findViewById(R.id.alarm_Item_list);
        mNullView = (ImageView)alarmRootView.findViewById(R.id.alarm_null_view);
        ImageView mAddBtnImg = (ImageView)alarmRootView.findViewById(R.id.alarm_img_addbtn);
        mAddBtnImg.setOnClickListener(btnclickListener);
        
        /**
         * ��Ӧ�õ����ݿ������洢�����б�
         */
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString() + "/alarmList.db3", null);
        showAlarmList();
        return alarmRootView;
    }
    
    OnClickListener btnclickListener = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent intent = new Intent(getActivity(), AddAlarmActivity.class);
            startActivityForResult(intent, 0);
        }
    };
    
    public void showAlarmList() {
        //ͨ�����ݿ��ѯ������Ŀ
        Cursor cursor = null;
        try
        {
            cursor = db.rawQuery("select * from alarm_info", null);
            //��listװ��adapter����ʾ
            redrawList(cursor);
        }
        catch(SQLiteException se)
        {
            //�������ݱ�
            db.execSQL("create table alarm_info(_id integer primary key autoincrement, alarm_time varchar(50), alarm_name varchar(50))");
            //��������
            cursor = db.rawQuery("select * from alarm_info", null);
            //��listװ��adapter����ʾ
            redrawList(cursor);
        }
    }
    
    private void redrawList(Cursor cursor) {
        if(cursor.getCount() == 0) {
            mNullView.setVisibility(View.VISIBLE);
        } else {
            mNullView.setVisibility(View.GONE);
            /**
             * �绰����list
             * adapter��������ʾ�б�
             */
            
            SimpleCursorAdapter sAdapter = new SimpleCursorAdapter(getActivity(),
                    R.layout.alarm_item_layout,
                    cursor,
                    new String[]{"alarm_time", "alarm_name"},
                    new int[]{R.id.alarm_time_text, R.id.alarm_marks_text},
                    CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
                    );
            
            mWenxinView.setAdapter(sAdapter);//���б���ʾ����
            //���ÿ����Ŀ���¼���������������ɾ������
        }
    }
}