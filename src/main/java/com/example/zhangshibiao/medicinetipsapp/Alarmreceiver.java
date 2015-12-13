package com.example.zhangshibiao.medicinetipsapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zhangshibiao on 15/12/9.
 *
 * reference
 * http://vincenttung.blog.51cto.com/6249439/1176785
 * http://my.oschina.net/jgy/blog/158513
 */
public  class Alarmreceiver extends BroadcastReceiver {
    // 声明MediaPlayer对象
    private MediaPlayer alarmMusic;
    private SQLiteDatabase db;  //数据库对象
    private String medicineTime;
    @Override
    public void onReceive(final Context context, Intent intent) {
        if ("android.alarm.demo.action".equals(intent.getAction())) {
            //第1步中设置的闹铃时间到，这里可以弹出闹铃提示并播放响铃
            //可以继续设置下一次闹铃时间;

            // 加载指定音乐，并为之创建MediaPlayer对象
//        alarmMusic = MediaPlayer.create(this, R.raw.girl);
            alarmMusic = MediaPlayer.create(context.getApplicationContext(), RingtoneManager.getActualDefaultRingtoneUri(context.getApplicationContext(), RingtoneManager.TYPE_ALARM));
            // 设置为循环播放
            alarmMusic.setLooping(true);
            // 播放音乐
            alarmMusic.start();
            medicineTime = intent.getStringExtra("medicineTime");
            // 创建一个对话框
//            dialog.getWindow()
//                    .setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            AlertDialog.Builder  builder = new AlertDialog.Builder(context);
            builder.setTitle("吃药提醒")
                    .setMessage("亲，该吃药了！！！")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 停止音乐播放
                            alarmMusic.stop();
                            /**
                             * jump to main activity
                             * todo: select db and set next clock time
                             */
                            operaClockLogic(context, false, medicineTime);
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 停止音乐播放
                            alarmMusic.stop();
                            operaClockLogic(context, true, medicineTime);
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            // 结束该Activity
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.getWindow()
                    .setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            dialog.show();
//            Log.i("xy__", "=========="+intent.getStringExtra("medicineTime"));
        }
    }

    private void operaClockLogic(Context context,boolean yesOrNo,String medicineTime) {

        db = SQLiteDatabase.openOrCreateDatabase(context.getFilesDir().toString() + "/tipslist.db", null);
        insertRecord(medicineTime, yesOrNo);
    }

    public String medicineTime(String timePoint) {
        /**
         *
         *   String startDateString = dateStr+" "+timeStr;
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
         */
        DateFormat df = new SimpleDateFormat("HH:mm yyyy/MM/dd");
        Date startDate;
        String newDateString = "";
        try {
            startDate = df.parse(timePoint);
            newDateString = df.format(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.i("format_timeStr",newDateString);
//        calendar.setTime(new Date(timePoint));
//        Calendar calendar = Calendar.getInstance();
//        timeStr = (new SimpleDateFormat("HH:mm yyyy/MM/dd")).format(calendar.getTime());
        return newDateString;
    }

    /*
    * 向数据库中插入数据
    * 参数介绍 :
    * -- 参数① : SQL语句, 在这个语句中使用 ? 作为占位符, 占位符中的内容在后面的字符串中按照顺序进行替换
    * -- 参数② : 替换参数①中占位符中的内容
    */
    private void insertData(SQLiteDatabase db, String timeStr, String isEat) {
        // insert into apple_info (name, age) values ('乔帮主', 54)
        String timeStrFormat = medicineTime(timeStr);
        System.out.println("time_format_alarmreceiver =>" +timeStrFormat);
        db.execSQL("insert into record_list values(null, ?, ?, ?)", new String[]{timeStr, timeStrFormat, isEat});
    }

    /*
     * 插入数据到数据库中
     * 如果数据库存在就能正常访问数据库, 如果不存在访问数据库的时候就会出现 SQLiteException 异常
     * 异常访问 : 如果访问出现了SQLiteException异常, 说明数据库不存在, 这时就需要先创建数据库
     */
    public void insertRecord(String timeStr, boolean yesOrNo) {
        String is_eat = "no";
        if (yesOrNo) {
            is_eat = "yes";
        }
        if (null == timeStr || "" == timeStr ) {
            Log.i("error","dateStr or timeStr is null !!!");
            return;
        }
        try{
            insertData(db, timeStr, is_eat);
        }catch(SQLiteException exception){
            db.execSQL("create table record_list (" +
                    "_id integer primary key autoincrement, " +
                    "time_str varchar(50), " +
                    "time_str_format varchar(150), " +
                    "is_eat varchar(50))");
            insertData(db, timeStr, is_eat);
        }

    }

/**
 * 继承 activity 的实现，但这种方式不利于调整闹钟时间
 */
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // 加载指定音乐，并为之创建MediaPlayer对象
////        alarmMusic = MediaPlayer.create(this, R.raw.girl);
//        alarmMusic = MediaPlayer.create(this, RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM));
//        // 设置为循环播放
//        alarmMusic.setLooping(true);
//        // 播放音乐
//        alarmMusic.start();
//        // 创建一个对话框
//        new AlertDialog.Builder(Alarmreceiver.this).setTitle("吃药提醒")
//                .setMessage("亲，该吃药了！！！")
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // 停止音乐播放
//                        alarmMusic.stop();
//                        // 结束该Activity
//                        new MainActivity();
//                        Alarmreceiver.this.finish();
//                    }
//                })
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // 停止音乐播放
//                        alarmMusic.stop();
//                        // 结束该Activity
//                        Alarmreceiver.this.finish();
//                    }
//                }).show();
//
//        Log.i("xy__","==========");
//        Toast.makeText(getApplicationContext(), "======", Toast.LENGTH_LONG).show();
//    }
}
