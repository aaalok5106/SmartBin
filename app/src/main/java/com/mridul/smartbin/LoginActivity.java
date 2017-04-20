package com.mridul.smartbin;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This class is for opening first activity just after you opens the app.
 * it will open registration activity if you want to register or Forgot Password if you forgot your password.
 */

public class LoginActivity extends AppCompatActivity {

    EditText email, password ;

    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.login_email);
        password = (EditText) findViewById(R.id.login_password);

        if (!isNetworkAvailable(this)) {
            Toast.makeText(this, "No Internet connection", Toast.LENGTH_LONG).show();
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    finish(); //Calling this method to close this activity when internet is not available.
                }
            }, 2000);
        }
    }

    public void activity_afterlogin(View view){
        String s_email = email.getText().toString();
        String s_password = password.getText().toString();
        String type="login";

        BackgroundWorkerLoginActivity backgroundWorker = new BackgroundWorkerLoginActivity(this);
        backgroundWorker.execute(type, s_email, s_password);
    }

    public void registerLayout(View view){
        startActivity(new Intent(this,Register.class));
    }

    public void forgotPasswordLayout(View view){
        startActivity(new Intent(this,ForgotPassword.class));
    }

    @Override
    public void onBackPressed() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);
                finish();
                System.exit(0);
            }
        }, 1);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(conMan.getActiveNetworkInfo() != null && conMan.getActiveNetworkInfo().isConnected())
            return true;
        else
            return false;
    }
}
