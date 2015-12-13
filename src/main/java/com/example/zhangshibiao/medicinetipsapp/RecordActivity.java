package com.example.zhangshibiao.medicinetipsapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordActivity extends AppCompatActivity {
    private SQLiteDatabase db;  //数据库对象
    private List<Map> tipsList = new ArrayList<Map>();
    private RecordAdapter myAdapter = null;
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        getSupportActionBar().hide();
        OpenDb();
        selectFromDb();
        lv = (ListView) findViewById(R.id.record_list_view);
        myAdapter = new RecordAdapter(this,tipsList);
        lv.setAdapter(myAdapter);
    }

    public void selectFromDb() {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from record_list order by time_str asc", null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                String timeStr = cursor.getString(cursor.getColumnIndex("time_str"));
                String timeStrFormat =  cursor.getString(cursor.getColumnIndex("time_str_format"));
                String isEat =  cursor.getString(cursor.getColumnIndex("is_eat"));

                Map tmpMap = new HashMap<String,String>();
                tmpMap.put("timeStr", timeStr);
                tmpMap.put("timeStrFormat", timeStrFormat);
                tmpMap.put("isEat", isEat);
                Log.i("test_", isEat);
                tipsList.add(tmpMap);
                cursor.moveToNext();
            }
            cursor.close();
        }catch(SQLiteException exception) {
            // 查询没有这个表的化，db.rawQuery会抛出错误，
            // 这个去创建个表吧，下一个activity就不用创建
            db.execSQL("create table record_list (" +
                    "_id integer primary key autoincrement, " +
                    "time_str varchar(50), " +
                    "time_str_format varchar(150), " +
                    "is_eat varchar(50))");
            Toast.makeText(RecordActivity.this, "select db catch", Toast.LENGTH_SHORT).show();
        }
    }

    private void OpenDb() {
        // /data/data/com.example.zhangshibiao.medicinetipsapp/files/tipslist.db
        db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString() + "/tipslist.db", null);
        System.out.println(this.getFilesDir().toString() + "/tipslist.db");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_record, menu);
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
    public void goBack(View view) {
        finish();
    }
}
