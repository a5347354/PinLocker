package net.funol.photolocker.activity;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.funol.photolocker.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by LuxProtoss on 2015/7/4.
 */
public class Set_passwordActivity extends Activity {
    @Bind(R.id.password)EditText password;
    @Bind(R.id.btn_next)Button next;
    @Bind(R.id.btn_number0)Button number0;
    @Bind(R.id.btn_number1)Button number1;
    @Bind(R.id.btn_number2)Button number2;
    @Bind(R.id.btn_number3)Button number3;
    @Bind(R.id.btn_number4)Button number4;
    @Bind(R.id.btn_number5)Button number5;
    @Bind(R.id.btn_number6)Button number6;
    @Bind(R.id.btn_number7)Button number7;
    @Bind(R.id.btn_number8)Button number8;
    @Bind(R.id.btn_number9)Button number9;
    @Bind(R.id.btn_backspace)Button backspace;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        ButterKnife.bind(this);


        final Vibrator vVi = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE); //震動物件

        View.OnClickListener btnlis = new View.OnClickListener() {
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.btn_next :
                        Intent intent = new Intent();
                        intent.setClass(Set_passwordActivity.this,Check_passwordActivity.class);

                        Bundle bundle = new Bundle(); //new一個Bundle物件，並將要傳遞的資料傳入
                        bundle.putString("password",password.getText().toString());//傳遞String
                        intent.putExtras(bundle);//將Bundle物件傳給intent
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.btn_backspace :
                        if(password.getText().length()==0){   //字串長度0不處理
                            vVi.vibrate( 80 );
                            //nothing
                        }
                        else
                        {
                            vVi.vibrate( 80 );
                            password.setText(password.getText().subSequence(0,password.getText().length()-1));   //刪除最後一個字
                        }
                        break;
                    case R.id.btn_number0 :
                        vVi.vibrate( 80 );
                        password.setText(password.getText()+"0");
                        break;
                    case R.id.btn_number1 :
                        vVi.vibrate( 80 );
                        password.setText(password.getText()+"1");
                        break;
                    case R.id.btn_number2 :
                        vVi.vibrate( 80 );
                        password.setText(password.getText()+"2");
                        break;
                    case R.id.btn_number3 :
                        vVi.vibrate( 80 );
                        password.setText(password.getText()+"3");
                        break;
                    case R.id.btn_number4 :
                        vVi.vibrate( 80 );
                        password.setText(password.getText()+"4");
                        break;
                    case R.id.btn_number5 :
                        vVi.vibrate( 80 );
                        password.setText(password.getText()+"5");
                        break;
                    case R.id.btn_number6 :
                        vVi.vibrate( 80 );
                        password.setText(password.getText()+"6");
                        break;
                    case R.id.btn_number7 :
                        vVi.vibrate( 80 );
                        password.setText(password.getText()+"7");
                        break;
                    case R.id.btn_number8 :
                        vVi.vibrate( 80 );
                        password.setText(password.getText()+"8");
                        break;
                    case R.id.btn_number9 :
                        vVi.vibrate( 80 );
                        password.setText(password.getText()+"9");
                        break;

                }
            }
        };
        next.setOnClickListener(btnlis);
        backspace.setOnClickListener(btnlis);
        number0.setOnClickListener(btnlis);
        number1.setOnClickListener(btnlis);
        number2.setOnClickListener(btnlis);
        number3.setOnClickListener(btnlis);
        number4.setOnClickListener(btnlis);
        number5.setOnClickListener(btnlis);
        number6.setOnClickListener(btnlis);
        number7.setOnClickListener(btnlis);
        number8.setOnClickListener(btnlis);
        number9.setOnClickListener(btnlis);



    }
}
