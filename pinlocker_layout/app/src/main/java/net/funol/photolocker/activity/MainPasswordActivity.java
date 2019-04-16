package net.funol.photolocker.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainPasswordActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Boolean isFirstIn = false;
        SharedPreferences pref = getSharedPreferences("myActivityName", 0);
        isFirstIn = pref.getBoolean("isFirstIn", true);
        if(isFirstIn==true){
            Intent intent = new Intent();
            intent.setClass(MainPasswordActivity.this,Set_passwordActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            Intent intent = new Intent();
            intent.setClass(MainPasswordActivity.this,PasswordActivity.class);
            startActivity(intent);
            finish();
        }

    }

}
