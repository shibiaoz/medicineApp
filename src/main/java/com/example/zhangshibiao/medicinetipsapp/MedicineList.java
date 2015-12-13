package com.example.zhangshibiao.medicinetipsapp;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

public class MedicineList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_list);
//        getSupportActionBar().hide();

        ((CheckBox)findViewById(R.id.selected)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean flag = false;
                if(((CheckBox)v).isChecked()) {
                    /**
                     * save time to shareprefence
                     * 7:00~7:30
                     */
                    flag = true;
                    SharedPreferences timePrefenceObj = getSharedPreferences("medicineInfo",0);
                    SharedPreferences.Editor editor = timePrefenceObj.edit();
                    editor.putString("startTime","7");
                    editor.putString("endTime","7:30");
                    editor.putBoolean("isSetDayTips",flag);
                    editor.commit();
                }
            }
        });
    }

    public void goBack(View view) {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_medicine_list, menu);
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
