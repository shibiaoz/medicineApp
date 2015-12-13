package com.example.zhangshibiao.medicinetipsapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NotificationTime extends AppCompatActivity {

    private final int DATE_DIALOG = 1;
    private final int TIME_DIALOG = 2;
    private String dateStr;
    private String timeStr;
    private SQLiteDatabase db;  //数据库对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_time);
        getSupportActionBar().hide();
        /**
         * 参考 文章
         * 时间选择
         * http://blog.csdn.net/lganggang131/article/details/7342790
         *
         * 数据库操作
         * http://blog.csdn.net/shulianghan/article/details/19028665
         */
        View.OnClickListener dateBtnListener =
                new BtnOnClickListener(DATE_DIALOG);

        Button btnDate = (Button) findViewById(R.id.date_sel);
        btnDate.setOnClickListener(dateBtnListener);

        View.OnClickListener timeBtnListener =
                new BtnOnClickListener(TIME_DIALOG);
        Button btnTime = (Button) findViewById(R.id.time_sel);
        btnTime.setOnClickListener(timeBtnListener);
    }

    protected Dialog onCreateDialog(int id) {
        //用来获取日期和时间的
        Calendar calendar = Calendar.getInstance();

        Dialog dialog = null;
        switch(id) {
            case DATE_DIALOG:
                DatePickerDialog.OnDateSetListener dateListener =
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker,
                                                  int year, int month, int dayOfMonth) {
                                //Calendar月份是从0开始,所以month要加1
                                int m = month + 1;
                                dateStr = "" + year +"/"+ (m<10 ? "0"+ m : m) +"/"+ (dayOfMonth <10 ? "0"+dayOfMonth : dayOfMonth );
                                String selStr = "你选择了" + year + "年" +
                                        (month+1) + "月" + dayOfMonth + "日";
                                Toast.makeText(getApplicationContext(),selStr+"-"+dateStr, Toast.LENGTH_SHORT).show();
                            }
                        };
                dialog = new DatePickerDialog(this,
                        dateListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                break;
            case TIME_DIALOG:
                TimePickerDialog.OnTimeSetListener timeListener =
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker timerPicker,
                                                  int hourOfDay, int minute) {
                                String selStr = "你选择了" + hourOfDay + "时" +
                                        minute + "分";
                                timeStr ="" + (hourOfDay<10 ? "0"+hourOfDay : hourOfDay) + ":" + (minute<10 ? "0"+minute : minute);
                                Toast.makeText(getApplicationContext(),selStr +"-"+timeStr, Toast.LENGTH_SHORT).show();
                            }
                        };
                dialog = new TimePickerDialog(this, timeListener,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        false);   //是否为二十四制
                break;
            default:
                break;
        }
        return dialog;
    }
    /*
    * 成员内部类,此处为提高可重用性，也可以换成匿名内部类
    */
    private class BtnOnClickListener implements View.OnClickListener {
        private int dialogId = 0;   //默认为0则不显示对话框
        public BtnOnClickListener(int dialogId) {
            this.dialogId = dialogId;
        }
        @Override
        public void onClick(View view) {
            showDialog(dialogId);
        }

    }


    public void save(View view) {
        //打开或者创建数据库, 这里是创建数据库,
        // /data/data/com.example.zhangshibiao.medicinetipsapp/files/tipslist.db
        db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString() + "/tipslist.db", null);
        System.out.println(this.getFilesDir().toString() + "/tipslist.db");
        insertNotification();
        finish();
//        Intent intent = new Intent(this, NotificationList.class);
//        startActivity(intent);
    }
    /*
     * 插入数据到数据库中
     * 如果数据库存在就能正常访问数据库, 如果不存在访问数据库的时候就会出现 SQLiteException 异常
     * 异常访问 : 如果访问出现了SQLiteException异常, 说明数据库不存在, 这时就需要先创建数据库
     */
    public void insertNotification() {
        String dateStr = this.dateStr;
        String timeStr = this.timeStr;
        if (null == dateStr || "" == dateStr || null == timeStr || "" == dateStr) {
            Log.i("error","dateStr or timeStr is null !!!");
            return;
        }
        try{
            insertData(db, dateStr, timeStr);
            Cursor cursor = db.rawQuery("select * from notification_list", null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                String a = cursor.getString(cursor.getColumnIndex("date_str"));
                String b =  cursor.getString(cursor.getColumnIndex("time_str"));
                Log.i("_xt_try",a+"-"+b);
                cursor.moveToNext();
            }
            cursor.close();
        }catch(SQLiteException exception){
            Log.i("__test__","test");
            db.execSQL("create table notification_list (" +
                    "_id integer primary key autoincrement, " +
                    "date_str varchar(50), " +
                    "time_str varchar(50), " +
                    "time_stamp varchar(150))");
            insertData(db, dateStr, timeStr);
            Cursor cursor = db.rawQuery("select * from notification_list", null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                String a = cursor.getString(cursor.getColumnIndex("date_str"));
                String b =  cursor.getString(cursor.getColumnIndex("time_str"));
                String c = cursor.getString(cursor.getColumnIndex("time_stamp"));
                Log.i("_xt_catch",a+"-"+b);
                cursor.moveToNext();
            }
            cursor.close();
        }

    }

    /*
     * 向数据库中插入数据
     * 参数介绍 :
     * -- 参数① : SQL语句, 在这个语句中使用 ? 作为占位符, 占位符中的内容在后面的字符串中按照顺序进行替换
     * -- 参数② : 替换参数①中占位符中的内容
     */
    private void insertData(SQLiteDatabase db, String dateStr, String timeStr) {
        // insert into apple_info (name, age) values ('乔帮主', 54)
        String startDateString = dateStr+" "+timeStr;
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date startDate;
        String newDateString = "";
        String timeStamp = "";
        try {
            startDate = df.parse(startDateString);
            newDateString = df.format(startDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            timeStamp = "" + cal.getTimeInMillis();
            System.out.println(newDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        db.execSQL("insert into notification_list values(null, ?, ?, ?)", new String[]{dateStr, timeStr, timeStamp});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notification_time, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
