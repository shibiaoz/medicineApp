package com.example.zhangshibiao.medicinetipsapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    // 声明闹钟管理器AlarmManager对象
//    private AlarmManager alarmManager;
    private  SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 获取AlarmManager服务对象
//        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        ImageView aboutUsBtn = (ImageView) findViewById(R.id.aboutus);
        aboutUsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
                startActivity(intent);
            }
        });

        ((Button)findViewById(R.id.add_medicine)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MedicineList.class);
                startActivity(intent);
            }
        });

        ((Button)findViewById(R.id.eat_medicine_tip)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotificationList.class);
                startActivity(intent);
            }
        });

        ((Button)findViewById(R.id.medicine_record)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(MainActivity.this, RecordActivity.class));
            }
        });

        ImageView personalCenterBtn = (ImageView) findViewById(R.id.personal_center);
        personalCenterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserFormActivity.class);
                startActivity(intent);
            }
        });
        calcuTime();
//        setTimeClockInstance();
//        setTimeClock();

    }


    public void setTimeClockInstance (long timePoint) {
        final AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

        // 实例化Intent
        Intent intent = new Intent("android.alarm.demo.action");
        intent.putExtra("medicineTime",""+timePoint);
        // 设置Intent action属性
//        intent.setAction("com.test.BC_ACTION");
        // 实例化PendingIntent
        final PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this, 0,
                intent, 0);
        // 获得系统时间
        final long time = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timePoint));
        String timeStr = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(calendar.getTime());
        Toast.makeText(MainActivity.this, timeStr+" alarm开启", Toast.LENGTH_LONG).show();
        am.set(AlarmManager.RTC_WAKEUP, timePoint, pi);//5秒后闹铃
    }

    private void  calcuTime() {
        SharedPreferences timePrefenceObj = getSharedPreferences("medicineInfo",0);
        Boolean isSetDayTips =  timePrefenceObj.getBoolean("isSetDayTips", false);
        long customTimeStamp = selectDB();
        if (isSetDayTips) {
            // 每天早上七点半
        }
        if (customTimeStamp > 0) {
            setTimeClockInstance(customTimeStamp);
        }
    }

    private long  selectDB() {
        db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString() + "/tipslist.db", null);
        Cursor cursor = null;
        long c = 0;
        try {
            cursor = db.rawQuery("select * from notification_list order by time_stamp asc", null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                String a = cursor.getString(cursor.getColumnIndex("date_str"));
                String b =  cursor.getString(cursor.getColumnIndex("time_str"));
                Map tmpMap = new HashMap<String,String>();
                tmpMap.put("dateStr", a);
                tmpMap.put("timeStr", b);
                if ( Long.parseLong(cursor.getString(cursor.getColumnIndex("time_stamp"))) > System.currentTimeMillis() ) {
                    c = Long.parseLong(cursor.getString(cursor.getColumnIndex("time_stamp")));
                    Log.i("_test__",c+"__"+System.currentTimeMillis());
                    break;
                }
                Log.i("notification_", a + "-" + b);
                cursor.moveToNext();
            }
            cursor.close();
        }catch(SQLiteException exception){
            // 查询没有这个表的化，db.rawQuery会抛出错误，
            // 这个去创建个表吧，下一个activity就不用创建
            db.execSQL("create table notification_list (" +
                    "_id integer primary key autoincrement, " +
                    "date_str varchar(50), " +
                    "time_str varchar(50))");
        }
        return c;
    }
    private void setTimeClock() {

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // 启动指定组件
        Intent intent = new Intent(MainActivity.this,
                Alarmreceiver.class);
        // 创建PendingIntent对象，封装Intent
        PendingIntent pi = PendingIntent.getActivity(
                MainActivity.this, 0, intent, 0);
        //设定一个五秒后的时间
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 5);

        //或者以下面方式简化
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+5*1000, pi);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (5 * 1000), (3 * 1000), pi);

        Toast.makeText(MainActivity.this, "五秒后alarm开启", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
