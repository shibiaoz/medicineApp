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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationList extends AppCompatActivity {

    private SQLiteDatabase db;  //数据库对象
    private List<Map> tipsList = new ArrayList<Map>();
    private NotificationTipsAdapter myAdapter = null;
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);
        getSupportActionBar().hide();
        OpenDb();
        selectFromDb();
        lv = (ListView) findViewById(R.id.tips_list_view);
        myAdapter = new NotificationTipsAdapter(this,tipsList);
        lv.setAdapter(myAdapter);
//        myAdapter.notifyDataSetChanged();
        ((ImageView)findViewById(R.id.add_time_tips)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationList.this, NotificationTime.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        tipsList.clear();
        selectFromDb();
        lv.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
//        myAdapter = new NotificationTipsAdapter(this,tipsList);
//        lv.setAdapter(myAdapter);
//        myAdapter.notifyDataSetChanged();

        Toast.makeText(this,"sdfds",Toast.LENGTH_SHORT).show();
    }

    /**
     * select from database
     */
    public void selectFromDb() {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from notification_list order by time_stamp asc", null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                String a = cursor.getString(cursor.getColumnIndex("date_str"));
                String b =  cursor.getString(cursor.getColumnIndex("time_str"));

                Map tmpMap = new HashMap<String,String>();
                tmpMap.put("dateStr", a);
                tmpMap.put("timeStr", b);
                tipsList.add(tmpMap);
                String startDateString = a+" "+b;
                DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                Date startDate;
                String newDateString = "";
                try {
                    startDate = df.parse(startDateString);
                    newDateString = df.format(startDate);
                    System.out.println(newDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Log.i("notification_", a + "-" + b+"__"+ newDateString);
                cursor.moveToNext();
            }
            cursor.close();
        }catch(SQLiteException exception) {
            // 查询没有这个表的化，db.rawQuery会抛出错误，
            // 这个去创建个表吧，下一个activity就不用创建
            db.execSQL("create table notification_list (" +
                    "_id integer primary key autoincrement, " +
                    "date_str varchar(50), " +
                    "time_str varchar(50), " +
                    "time_stamp varchar(150))");
            Toast.makeText(NotificationList.this, "select db catch",Toast.LENGTH_SHORT).show();
        }
    }

    public void OpenDb() {
        // /data/data/com.example.zhangshibiao.medicinetipsapp/files/tipslist.db
        db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString() + "/tipslist.db", null);
        System.out.println(this.getFilesDir().toString() + "/tipslist.db");
    }

    public void test(View view) {

    }

    public void goBack(View view) {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notification_list, menu);
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
