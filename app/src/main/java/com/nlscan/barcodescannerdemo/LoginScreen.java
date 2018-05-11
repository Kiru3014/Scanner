package com.nlscan.barcodescannerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nlscan.barcodescannerdemo.DB.DBController;
import com.nlscan.barcodescannerdemo.utils.UserSharedPreferences;

import net.gotev.speech.Speech;


public class LoginScreen extends AppCompatActivity implements View.OnClickListener {
    EditText bUserName, bUserPassword;
    Button bLogin;
    String bName, bPassword;
    UserSharedPreferences userSharedPreferences;
    DBController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        userSharedPreferences = new UserSharedPreferences(getApplicationContext());
        initViews();
        controller = new DBController(this);
        controller.createadminlogin();
        bLogin.setOnClickListener(this);

    }


    private void initViews() {
        //initilizise views
        bUserName = (EditText) findViewById(R.id.et_username);
        bUserPassword = (EditText) findViewById(R.id.et_password);
        bLogin = (Button) findViewById(R.id.btn_login);

    }


    //onclik listner
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                userValidate();
                break;
        }
    }

    private void userValidate() {
        bName = bUserName.getText().toString().trim();
        bPassword = bUserPassword.getText().toString().trim();

        if (bName == null || bName.isEmpty()) {
            Toast.makeText(getApplicationContext(),"username cant be empty",Toast.LENGTH_LONG).show();
        } else if (bPassword == null || bPassword.isEmpty()) {
            Toast.makeText(getApplicationContext(),"Password cant be empty",Toast.LENGTH_LONG).show();
        } else {
            logindata(bName, bPassword);

        }
    }

    private void logindata(String bName, String bPassword) {

     Boolean login=controller.checklogin(bName,bPassword);

        if(login) {
            Toast.makeText(getApplicationContext(), "sucess", Toast.LENGTH_LONG).show();
            userSharedPreferences.setUserid(bName);
            if(bName.equals("admin")) {
                startActivity(new Intent(getApplicationContext(), UploadScreen.class));
                finish();
            }
            else
            {
                if(controller.getlocationcount())
                    startActivity(new Intent(getApplicationContext(), SessionActivity.class));
                else
                    Speech.getInstance().say("Please contact admin to upload master files");
            }
        }

        else
            Toast.makeText(getApplicationContext()," Login fail",Toast.LENGTH_LONG).show();
    }

}
