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
    /*�����µ�ͨ����������*/
    private Button confirmBtn;
    /*ȷ������View*/
    private View layout1, layout2;
    /*�ı���������ͨѶ¼�����֣�������������ֲ���绰���������ֻ�������ô�죿*/
    private AutoCompleteTextView fName;
    private ImageView addnewcontacts;
    /*�������ν������ʱ��*/
    private static String aName="";
    private static String aTime="";
    private static String aPhone="";
    /*�ı������趨��绰��ʱ��*/
    private EditText dtPicker;
    /*����Calendar*/
    static int id = 0;
    final Calendar calendar = Calendar.getInstance();
    /*����ִ�����ӵ�BroadCastReceiver��calendar*/
    static Calendar cldr = Calendar.getInstance();
    //��õ�ǰʱ��
    private int mHour = calendar.get(Calendar.HOUR_OF_DAY);
    private int mMinute = calendar.get(Calendar.MINUTE);
    private String timebefore="";
    //��ϵ��������
    private ArrayList<String> mNamelist = new ArrayList<String>();
    //��ϵ��-�绰��
    private HashMap<String, String> mNamePhone = new HashMap<String, String>();
    //�洢��ϵ�˵�����
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
            actionbarTitle.setText("����������");
        }
    }
    
    private void getContacts()
    {
        /*�ֻ�ͨѶ¼��URI*/
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        /*ͨѶ¼��ϵ�˵ĳ�ν*/
        String cName;
        /*ͨѶ¼��ϵ�˵ĵ绰*/
        String phoneNumber;
        int i;
         //�õ�ContentResolver����
        ContentResolver resolver = getContentResolver();
        //ȡ�õ绰���п�ʼһ��Ĺ��
        Cursor cursor = resolver.query(uri, null, null, null, null);
        i=0;
        while(cursor.moveToNext())
        {
            //ȡ����ϵ��
            int columnIndex = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
            cName = cursor.getString(columnIndex);
            mNamelist.add(cName);
            i++;
            //ȡ�õ绰����
            String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            //����ͨ��������ѯ�绰��ֱ����ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            Cursor phone = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, 
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
            
            while(phone.moveToNext())
            {
                //ȡ�õ绰����
                phoneNumber = phone.getString(
                        phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //����Map
                HashMap<String, String> contactMap = new HashMap<String, String>();
                //���ռ�ֵ"name"��"number"��ֵ����Map��
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
        //����һ������������ʾListView
        mAdapter = new SimpleAdapter(this, 
                contactsList,
                R.layout.contactsinfo,                                 //ÿһ����ϵ����Ϣ�Ĳ���
                new String[]{"name", "number"},                        //Map����ÿһ����Ϣ�����֣��������ݱ������
                new int[]{R.id.contactname, R.id.phonenumber});          //�Զ��岼���и����ؼ���id
        /*ͨѶ¼view*/
        ListView listview;
        //��ʾ��ϵ���б�
        setContentView(R.layout.contactslist);
        listview = (ListView)findViewById(R.id.lview);
        //ΪListView��������
        listview.setAdapter(mAdapter);
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long itemId) {
                //�ڲ�������final
                final TextView tvName, tvPhone;
                //��ǰ��view���沼�ֵĸ����ؼ�����id�ģ������android:id�ĺô�
                tvName = (TextView)view.findViewById(R.id.contactname);
                tvPhone = (TextView)view.findViewById(R.id.phonenumber);
                String gotname = (String) tvName.getText();
                //�����ϵ�˵绰
                aPhone = tvPhone.getText().toString().trim();
                //�ص�ǰһ������
                showInputView(gotname);
            }
        });      
    }
    
    private void showInputView(String name) {
        setContentView(layout2);
        //���Ұ�
        fName = (AutoCompleteTextView)findViewById(R.id.textname);
        if(name != null) {
            fName.setText(name);
        }
        addnewcontacts = (ImageView)findViewById(R.id.newcontacts);
        dtPicker = (EditText)findViewById(R.id.dtpicker);
        confirmBtn =(Button)findViewById(R.id.confirm);
        //����־����
        mInputViewLoad = true;
        mContactsViewOn = false;
        //�Զ�����
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mNamelist);
        fName.setAdapter(arrayAdapter);
        addnewcontacts.setOnTouchListener(addbtnListener);
        dtPicker.setKeyListener(null);
        dtPicker.setOnFocusChangeListener(dtpickerListener);
        confirmBtn.setOnClickListener(confirmbtnListener);
        //��ʾ��ǰʱ��
        timeDisplay();
    }
    
    private void getDtime() {
        /**
         * ʵ����һ��DatePickerDialog�Ķ���
         * �ڶ���������һ��DatePickerDialog.OnDateSetListener�����ڲ���
         * ���û�ѡ������ڵ��done����������onDateSet����
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
            //��ȡͨѶ¼��ϵ��
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
            //�ó������ж�fName�Ƿ������ݣ�����û������������
            if(fName.getText().length()!=0) {
                //�������ƺ����ݸ�ȫ�ֱ�������Ϊ����������ϵ��
                aName = fName.getText().toString();
                ////��ʱ�䴫�ݸ�ȫ�ֱ�������Ϊ��������ʱ��
                aTime = dtPicker.getText().toString().trim();
                if(aPhone.length() == 0) {
                    //������Զ�������ɵ����֣��ͻ�û�в�ѯ�绰
                    //����ǵ�������ϵ�˻�ȡ��ϵ���б����Ѿ���ѯ���绰
                    aPhone = mNamePhone.get(aName);
                } else {
                    //ͬһʱ�䲻�������������,�������Ҫ����id,���ж��������ʱ���ڲ���
                    if(isNotExist(aTime)) {
                        /**
                         * �������õ�ʱ�䷢��BroadcastReceiver
                         */
                        id = mHour*60+mMinute;
                        cldr.setTimeInMillis(System.currentTimeMillis());
                        cldr.set(Calendar.HOUR_OF_DAY, mHour);
                        cldr.set(Calendar.MINUTE, mMinute);
                        cldr.set(Calendar.SECOND, 0);
                        cldr.set(Calendar.MILLISECOND, 0);
                        if(timebefore.length()>2) {
                            //�ж��Ǹ��»����½�
                            //updateData(mContactDb, aTime, aName, timebefore);
                            //setonAlarmMngr(cldr, id, aTime, aName, aPhone);
                            //aPhone = null;//���绰��գ���������aPhone.length()�޷������Ƿ����Զ������
                            //finish();
                        } else {
                            //�����趨ʱ���ǲ����Ѿ�����
                            insertData(mContactDb, aTime, aName);
                            setonAlarmMngr(cldr, id, aTime, aName, aPhone);
                            aPhone = null;//���绰��գ���������aPhone.length()�޷������Ƿ����Զ������
                            finish();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "����ʱ���Ѵ��ڣ�", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "���ﲻ��Ϊ�գ�", Toast.LENGTH_LONG).show();
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
        //���޸ĸ��µ����ݿ�
        db.execSQL("update alarm_info set alarm_time = ?, alarm_name = ? where alarm_time = ?", new String[]{time, name, timebefore});
    }
    
    private void deleteData(SQLiteDatabase db, String time) {
        //SQLite���ݿ���÷���Ȼ��Java��MySQL��̫һ��������˼���࣬�����á��������������λ��
        db.execSQL("delete from alarm_info where alarm_time = ?", new String[]{time});
    }
    
    private void setonAlarmMngr(Calendar c, int id, String time, String name, String phonenumber) {
        //��������  
        //id�������ڶ����������ֿ���
        /**
         * ע�����ӹ�����alarmManager
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
        //�ر�����
        //id�������ڶ����������ֿ���
        //Intent mAlarmIntent = new Intent(this, AlarmReceiver.class);
        //malarmPendingintent = PendingIntent.getBroadcast(getApplicationContext(), id, mAlarmIntent, 0);
        AlarmManager mAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.cancel(malarmPendingintent);
        Toast.makeText(getApplicationContext(), "������ɾ����", Toast.LENGTH_SHORT).show();
    }
    
    private void insertData(SQLiteDatabase db, String time, String name) {
        db.execSQL("insert into alarm_info values(null, ?, ?)", new String[]{time, name});
    }
}
