package com.example.zhangshibiao.medicinetipsapp;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class UserFormActivity extends AppCompatActivity {

    private EditText userNameEdit, userAgeEdit, userHeightEdit, userWeightEdit;
    private RadioGroup sexGroup;
    private  SharedPreferences userPreferenceObj;
    SharedPreferences.Editor userEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);
        /**
         * edittexts for user info
         */

        userPreferenceObj = getSharedPreferences("userInfo",0);
        userEditor = userPreferenceObj.edit();
        sexGroup = (RadioGroup) findViewById(R.id.sex_group);
        userNameEdit =(EditText)findViewById(R.id.user_name);
        userAgeEdit = (EditText)findViewById(R.id.user_age);
        userHeightEdit = (EditText)findViewById(R.id.user_height);
        userWeightEdit = (EditText)findViewById(R.id.user_weight);
        setUserInfo();
        TextView saveUserView = (TextView) findViewById(R.id.save_user_btn);
        saveUserView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameEdit.getText().toString();
                String userAge = userAgeEdit.getText().toString();
                String userWeight = userWeightEdit.getText().toString();
                String userHeight = userHeightEdit.getText().toString();
                String sex = ((RadioButton)findViewById(sexGroup.getCheckedRadioButtonId())).getText().toString();

                userEditor.putString("userName", userName);
                userEditor.putString("userAge", userAge);
                userEditor.putString("userWeight", userWeight);
                userEditor.putString("userHeight", userHeight);
                userEditor.putString("userSex", sex);
                userEditor.commit();
//                Toast.makeText(UserFormActivity.this, "save success"+sex, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void setUserInfo() {
        /**
         * config for sex
         * key get from shareprefences
         */
        Map mapSex = new HashMap();
        mapSex.put("男", R.id.sex_male);
        mapSex.put("女", R.id.sex_female);

        String userName = userPreferenceObj.getString("userName","user_name");
//        Toast.makeText(UserFormActivity.this, userName, Toast.LENGTH_LONG).show();
        String userAge = userPreferenceObj.getString("userAge","userAge");
        String userWeight = userPreferenceObj.getString("userWeight","userWeight");
        String userHeight = userPreferenceObj.getString("userHeight","userHeight");
        String userSex = userPreferenceObj.getString("userSex","男");

        userNameEdit.setText(userName, TextView.BufferType.EDITABLE);
        userAgeEdit.setText(userAge, TextView.BufferType.EDITABLE);
        userHeightEdit.setText(userHeight, TextView.BufferType.EDITABLE);
        userWeightEdit.setText(userWeight, TextView.BufferType.EDITABLE);
        sexGroup.check(Integer.parseInt(mapSex.get(userSex).toString()));
    }

    // return void
    public void goBack(View view) {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_form, menu);
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
