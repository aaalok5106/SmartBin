package com.mridul.smartbin;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static com.mridul.smartbin.LoginActivity.isNetworkAvailable;

/**
 * This class will be used to register a user and stores user_data on the server.
 */

public class Register extends AppCompatActivity {

    EditText name, email, mob_no, password;



    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if(!isNetworkAvailable(this)) {
            Toast.makeText(this, "No Internet connection", Toast.LENGTH_LONG).show();
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    finish(); //Calling this method to close this activity when internet is not available.
                }
            }, 2000);
        }
        Toolbar tbar = (Toolbar)findViewById(R.id.tbar_act_register_admin);
        tbar.setTitle("Register");
        tbar.setTitleTextColor(getResources().getColor(R.color.WHITE));
        setSupportActionBar(tbar);


        name = (EditText)findViewById(R.id.et_name);
        email = (EditText)findViewById(R.id.et_email);
        mob_no = (EditText)findViewById(R.id.et_mobno);
        password = (EditText)findViewById(R.id.et_password);


    }

    public void RegisterMe(View view)
    {
        String s_name = name.getText().toString();
        String s_email = email.getText().toString();
        String s_mob_no = mob_no.getText().toString();
        String s_password = password.getText().toString();

        String type = "register";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type,s_email,s_name,s_mob_no,s_password);
    }
    public void gotologin(View v){
        Intent intent = new Intent(this,LoginActivity.class);
        this.startActivity(intent);
        finish();
    }
}
