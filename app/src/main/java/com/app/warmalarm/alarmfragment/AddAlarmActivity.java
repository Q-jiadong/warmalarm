package com.app.warmalarm.alarmfragment;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

import com.app.warmalarm.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

public class AddAlarmActivity extends Activity{
    
    private ActionBar actionbar;
    private SimpleAdapter mAdapter;
    private ArrayAdapter<String> arrayAdapter;
    /*创建新的通话对象的组件*/
    private Button confirmBtn;
    /*确定几个View*/
    private View layout1, layout2;
    /*文本框中输入通讯录的名字，并依据这个名字拨打电话，换了名字或重名怎么办？*/
    private AutoCompleteTextView fName;
    private ImageView addnewcontacts;
    /*姓名或称谓、提醒时间*/
    private static String aName="";
    private static String aTime="";
    private static String aPhone="";
    /*文本框中设定打电话的时间*/
    private EditText dtPicker;
    /*创建Calendar*/
    static int id = 0;
    final Calendar calendar = Calendar.getInstance();
    /*发给执行闹钟的BroadCastReceiver的calendar*/
    static Calendar cldr = Calendar.getInstance();
    //获得当前时间
    private int mHour = calendar.get(Calendar.HOUR_OF_DAY);
    private int mMinute = calendar.get(Calendar.MINUTE);
    private String timebefore="";
    //联系人名字组
    private ArrayList<String> mNamelist = new ArrayList<String>();
    //联系人-电话对
    private HashMap<String, String> mNamePhone = new HashMap<String, String>();
    //存储联系人的数组
    ArrayList<HashMap<String, String>> contactsList = new ArrayList<HashMap<String, String>>();
    private SQLiteDatabase mContactDb;
    private PendingIntent malarmPendingintent;
    private boolean mInputViewLoad = false;
    private boolean mContactsViewOn = false;
    private static final int TYPE_NUMBER = 1;
    private static final int TYPE_LETTER = 2;
    private static final int TYPE_CHINESE = 3;
    private static final int TYPE_OTHER = 4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getLayoutInflater();
        layout1 = inflater.inflate(R.layout.contactslist, null);
        layout2 = inflater.inflate(R.layout.add_member, null);
        mContactDb = AlarmFragment.db;
        initActionBar();
        getContacts();
        showContactsList();
    }
    
    private void initActionBar() {
        actionbar = getActionBar();
        if(actionbar != null) {
            actionbar.setDisplayShowCustomEnabled(true);
            actionbar.setCustomView(R.layout.layout_actionbar);
            actionbar.setDisplayShowHomeEnabled(false);
            actionbar.setDisplayShowTitleEnabled(false);
            TextView actionbarTitle = (TextView)actionbar.getCustomView().findViewById(R.id.actionbar_title);
            ImageView menu = (ImageView)findViewById(R.id.actionbar_menu);
            menu.setVisibility(View.GONE);
            actionbarTitle.setText("创建新闹钟");
        }
    }
    
    private void getContacts()
    {
        /*手机通讯录的URI*/
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        /*通讯录联系人的称谓*/
        String cName;
        /*通讯录联系人的电话*/
        String phoneNumber;
        int i;
         //得到ContentResolver对象
        ContentResolver resolver = getContentResolver();
        //取得电话本中开始一项的光标
        Cursor cursor = resolver.query(uri, null, null, null, null);
        i=0;
        while(cursor.moveToNext())
        {
            //取得联系人
            int columnIndex = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
            cName = cursor.getString(columnIndex);
            mNamelist.add(cName);
            i++;
            //取得电话号码
            String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            //避免通过人名查询电话，直接用ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            Cursor phone = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, 
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
            
            while(phone.moveToNext())
            {
                //取得电话号码
                phoneNumber = phone.getString(
                        phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //构造Map
                HashMap<String, String> contactMap = new HashMap<String, String>();
                //按照键值"name"、"number"将值存入Map中
                contactMap.put("name", cName);
                contactMap.put("number", phoneNumber);
                contactsList.add(contactMap);
                mNamePhone.put(cName, phoneNumber);
            }
        }
        cursor.close();

        //sort the contactList by pinyin4j
        Collections.sort(contactsList, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> contactMap1, HashMap<String, String> contactMap2) {
                String name1 = contactMap1.get("name");
                String name2 = contactMap2.get("name");
                char c1 = name1.charAt(0);
                char c2 = name2.charAt(0);
                int type1 = getLetterType(c1);
                int type2 = getLetterType(c2);
                int result = 0;
                if (type1 != type2) {
                    return c1 - c2;
                } else if ((type1 == type2) && type1 == TYPE_LETTER) {
                    char c11 = Character.toLowerCase(c1);
                    char c22 = Character.toLowerCase(c2);
                    if (c11 == c22 && c1 != c2) {
                        result = c1 - c2;
                    } else {
                        result = c11 - c22;
                    }
                } else if((type1 == type2) && type1 == TYPE_CHINESE) {
                    Collator collator = Collator.getInstance(Locale.CHINA);
                    result = collator.compare(name1, name2);
                }
                return result;
            }
        });
    }

    private int getLetterType(char c) {
        if (c >= 0x4e00 && c <= 0x9fa5) {
            return TYPE_CHINESE;
        } else if (isNum(c)) {
            return TYPE_NUMBER;
        } else if (isLetter(c)) {
            return TYPE_LETTER;
        } else {
            return TYPE_OTHER;
        }
    }

    private boolean isNum(char c) {
        return c >= 0x0030 && c <= 0x0039;
    }

    private boolean isLetter(char c) {
        return (c >= 0x0041 && c <= 0x005a) || (c >= 0x0061 && c <= 0x007a);
    }
    
    private void showContactsList() {
        setContentView(layout1);
        mContactsViewOn = true;
        //定义一个适配器来显示ListView
        mAdapter = new SimpleAdapter(this, 
                contactsList,
                R.layout.contactsinfo,                                 //每一条联系人信息的布局
                new String[]{"name", "number"},                        //Map里面每一项信息的名字，就像数据表的列名
                new int[]{R.id.contactname, R.id.phonenumber});          //自定义布局中各个控件的id
        /*通讯录view*/
        ListView listview;
        //显示联系人列表
        setContentView(R.layout.contactslist);
        listview = (ListView)findViewById(R.id.lview);
        //为ListView绑定适配器
        listview.setAdapter(mAdapter);
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long itemId) {
                //内部类里面final
                final TextView tvName, tvPhone;
                //以前的view里面布局的各个控件是有id的，这就是android:id的好处
                tvName = (TextView)view.findViewById(R.id.contactname);
                tvPhone = (TextView)view.findViewById(R.id.phonenumber);
                String gotname = (String) tvName.getText();
                //获得联系人电话
                aPhone = tvPhone.getText().toString().trim();
                //回到前一个布局
                showInputView(gotname);
            }
        });      
    }
    
    private void showInputView(String name) {
        setContentView(layout2);
        //查找绑定
        fName = (AutoCompleteTextView)findViewById(R.id.textname);
        if(name != null) {
            fName.setText(name);
        }
        addnewcontacts = (ImageView)findViewById(R.id.newcontacts);
        dtPicker = (EditText)findViewById(R.id.dtpicker);
        confirmBtn =(Button)findViewById(R.id.confirm);
        //将标志更改
        mInputViewLoad = true;
        mContactsViewOn = false;
        //自动输入
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mNamelist);
        fName.setAdapter(arrayAdapter);
        addnewcontacts.setOnTouchListener(addbtnListener);
        dtPicker.setKeyListener(null);
        dtPicker.setOnFocusChangeListener(dtpickerListener);
        confirmBtn.setOnClickListener(confirmbtnListener);
        //显示当前时间
        timeDisplay();
    }
    
    private void getDtime() {
        /**
         * 实例化一个DatePickerDialog的对象
         * 第二个参数是一个DatePickerDialog.OnDateSetListener匿名内部类
         * 当用户选择好日期点击done会调用里面的onDateSet方法
         */
        View dtpickerDialog = View.inflate(this, R.layout.date_time_picker_layout, null);
        //final DatePicker dpicker = (DatePicker)dtpickerDialog.findViewById(R.id.date_picker);
        final TimePicker tpicker = (TimePicker)dtpickerDialog.findViewById(R.id.time_picker);
        tpicker.setOnKeyListener(null);
        tpicker.setIs24HourView(true);
        
        // Build DateTimeDialog  
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  
        builder.setView(dtpickerDialog);
        builder.setPositiveButton(R.string.datetime_picker_confirm, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mHour = tpicker.getCurrentHour();
                mMinute = tpicker.getCurrentMinute();
                
                String h = mHour<10?("0"+mHour):(""+mHour);
                String m = mMinute<10?("0"+mMinute):(""+mMinute);
                dtPicker.setText(new StringBuilder().append(h).append(":").append(m).append(" "));
            }
        });
        builder.setNegativeButton(R.string.datetime_picker_cancel, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //quit the dialog
            }
        });
        builder.show();
        fName.requestFocus();
    }
    
    private void timeDisplay() {
        String h = mHour<10?("0"+mHour):(""+mHour);
        String m = mMinute<10?("0"+mMinute):(""+mMinute);
        dtPicker.setText(new StringBuilder().append(h).append(":").append(m).append(" "));
    }
    
    private boolean isNotExist(String ATime) {
        Cursor cursor = mContactDb.rawQuery("select * from alarm_info where alarm_time = ?", new String[]{ATime});
        return !cursor.moveToNext();
    }
    
    OnTouchListener addbtnListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //获取通讯录联系人
            showContactsList();
            return true;
        }
    };
    
    OnFocusChangeListener dtpickerListener = new View.OnFocusChangeListener() {
        
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus) {
                getDtime();
            }
        }
    };
    
    OnClickListener confirmbtnListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            //用长度来判断fName是否有内容，还有没有其他方法？
            if(fName.getText().length()!=0) {
                //将姓名称呼传递给全局变量，此为最终闹钟联系人
                aName = fName.getText().toString();
                ////将时间传递给全局变量，此为最终闹钟时间
                aTime = dtPicker.getText().toString().trim();
                if(aPhone.length() == 0) {
                    //如果是自动输入完成的名字，就还没有查询电话
                    //如果是点击添加联系人获取联系人列表则已经查询过电话
                    aPhone = mNamePhone.get(aName);
                } else {
                    //同一时间不能添加两个闹钟,多个闹钟要区分id,先判断这个闹钟时刻在不在
                    if(isNotExist(aTime)) {
                        /**
                         * 将所设置的时间发给BroadcastReceiver
                         */
                        id = mHour*60+mMinute;
                        cldr.setTimeInMillis(System.currentTimeMillis());
                        cldr.set(Calendar.HOUR_OF_DAY, mHour);
                        cldr.set(Calendar.MINUTE, mMinute);
                        cldr.set(Calendar.SECOND, 0);
                        cldr.set(Calendar.MILLISECOND, 0);
                        if(timebefore.length()>2) {
                            //判断是更新还是新建
                            //updateData(mContactDb, aTime, aName, timebefore);
                            //setonAlarmMngr(cldr, id, aTime, aName, aPhone);
                            //aPhone = null;//将电话清空，否则上面aPhone.length()无法区分是否是自动输入的
                            //finish();
                        } else {
                            //看所设定时间是不是已经存在
                            insertData(mContactDb, aTime, aName);
                            setonAlarmMngr(cldr, id, aTime, aName, aPhone);
                            aPhone = null;//将电话清空，否则上面aPhone.length()无法区分是否是自动输入的
                            finish();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "闹钟时刻已存在！", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "人物不能为空！", Toast.LENGTH_LONG).show();
            }
        }
    };
    
    @Override
    public boolean onKeyDown(int KeyCode, KeyEvent event) {
        if(KeyCode == KeyEvent.KEYCODE_BACK) {
            if(mInputViewLoad ) {
                if(mContactsViewOn) {
                    showInputView(null);
                } else {
                    mInputViewLoad = false;
                    finish();
                }
            } else {
                finish();
            }
        }
        return false;
    }
    
    private void updateData(SQLiteDatabase db, String time, String name, String timebefore) {
        //将修改更新到数据库
        db.execSQL("update alarm_info set alarm_time = ?, alarm_name = ? where alarm_time = ?", new String[]{time, name, timebefore});
    }
    
    private void deleteData(SQLiteDatabase db, String time) {
        //SQLite数据库的用法虽然跟Java用MySQL不太一样，但是思想差不多，都是用“？”代替变量的位置
        db.execSQL("delete from alarm_info where alarm_time = ?", new String[]{time});
    }
    
    private void setonAlarmMngr(Calendar c, int id, String time, String name, String phonenumber) {
        //开启闹钟  
        //id用来在众多闹钟中区分开来
        /**
         * 注册闹钟管理器alarmManager
         */
        Intent mAlarmIntent = new Intent(this, AlarmReceiver.class);
        mAlarmIntent.putExtra("time", time);
        mAlarmIntent.putExtra("name", name);
        mAlarmIntent.putExtra("phone", phonenumber);
        malarmPendingintent = PendingIntent.getBroadcast(getApplicationContext(), id, mAlarmIntent, 0);
        AlarmManager mAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), malarmPendingintent);
    }
    private void setoffAlarmMngr(int id) {
        //关闭闹钟
        //id用来在众多闹钟中区分开来
        //Intent mAlarmIntent = new Intent(this, AlarmReceiver.class);
        //malarmPendingintent = PendingIntent.getBroadcast(getApplicationContext(), id, mAlarmIntent, 0);
        AlarmManager mAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.cancel(malarmPendingintent);
        Toast.makeText(getApplicationContext(), "闹钟已删除！", Toast.LENGTH_SHORT).show();
    }
    
    private void insertData(SQLiteDatabase db, String time, String name) {
        db.execSQL("insert into alarm_info values(null, ?, ?)", new String[]{time, name});
    }
}
