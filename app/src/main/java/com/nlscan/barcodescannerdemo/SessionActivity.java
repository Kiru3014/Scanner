package com.nlscan.barcodescannerdemo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeReader;
import com.nlscan.barcodescannerdemo.DB.DBController;
import com.nlscan.barcodescannerdemo.adaptor.CustomAdapter;
import com.nlscan.barcodescannerdemo.utils.Constants;
import com.nlscan.barcodescannerdemo.utils.UserSharedPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SessionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    Spinner spin;
    CustomAdapter customAdapter;
    DBController controller;
    ArrayList<String> myListLocation, mlistofSessions;
    Button mbtnstartsession, mbtnresumsession, mbtnEditsession;
    UserSharedPreferences userSharedPreferences;
    String mlocation;
    RadioGroup radioGroup;
    String lookup;

    //scanner
    static BarcodeReader barcodeReader;
    AidcManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        userSharedPreferences = new UserSharedPreferences(getApplicationContext());
        controller = new DBController(this);
        spin = (Spinner) findViewById(R.id.simpleSpinner);
        spin.setOnItemSelectedListener(this);
        lookup="";

        myListLocation = controller.getlocation();
        if (myListLocation != null && myListLocation.size() > 0)
            customAdapter = new CustomAdapter(getApplicationContext(), myListLocation);
        spin.setAdapter(customAdapter);

        //button init
        mbtnstartsession = (Button) findViewById(R.id.start_session);
        mbtnresumsession = (Button) findViewById(R.id.resume_session);
        mbtnEditsession= (Button) findViewById(R.id.edit_session);

        mbtnstartsession.setOnClickListener(this);
        mbtnresumsession.setOnClickListener(this);
        mbtnEditsession.setOnClickListener(this);

        radioGroup = (RadioGroup) findViewById(R.id.radiolookup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                switch (selectedId) {
                    case R.id.radiowithlookup:
                        lookup ="1";
                        break;
                    case R.id.radiowithoutlook:
                        lookup = "0";
                        break;
                }
            }
        });

        // set lock the orientation
        // otherwise, the onDestory will trigger
        // when orientation changes
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // create the AidcManager providing a Context and a
        // CreatedCallback implementation.
        AidcManager.create(this, new AidcManager.CreatedCallback() {

            @Override
            public void onCreated(AidcManager aidcManager) {
                manager = aidcManager;
                barcodeReader = manager.createBarcodeReader();
            }
        });


    }


    static BarcodeReader getBarcodeObject() {
        return barcodeReader;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == 0) {
            mlocation = "";
        } else {
            mlocation = myListLocation.get(i);

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_session:
                createNewsession();
                break;
            case R.id.resume_session:
                ResumeSession();
                break;
            case R.id.edit_session:
                editsession();
        }

    }

    private void editsession() {

       Boolean cnt= controller.getsessioncount(userSharedPreferences.getUserid());

       if(cnt){
           Intent intent = new Intent(getApplicationContext(), Record_Activity.class);
           intent.putExtra(Constants.USR_ID, userSharedPreferences.getUserid());
           startActivity(intent);
       }
       else
           Toast.makeText(getApplicationContext(),"NO Session found ", Toast.LENGTH_LONG).show();
    }

    private void ResumeSession() {
        mlistofSessions = controller.getactivesessionforuser(userSharedPreferences.getUserid());
        if (mlistofSessions.size() > 0) {
            Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
            intent.putExtra(Constants.USR_ID, userSharedPreferences.getUserid());
            intent.putExtra(Constants.SCANTYPE, "R");
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "User dont have any session create new", Toast.LENGTH_SHORT).show();
        }


    }

    private void createNewsession() {
        String session;
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

        if (!mlocation.isEmpty() && !lookup.isEmpty()) {
            session = mlocation + timeStamp + userSharedPreferences.getUserid();
            controller.addSession(session, mlocation, lookup, userSharedPreferences.getUserid());

            Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
            intent.putExtra(Constants.USR_ID, userSharedPreferences.getUserid());
            intent.putExtra(Constants.SESSIONNAME, session);
            intent.putExtra(Constants.LOCATION, mlocation);
            intent.putExtra(Constants.COLUMN_LOOKUP, lookup);
            intent.putExtra(Constants.SCANTYPE, "N");
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "Session Created Sucessfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Please select Location and look up type", Toast.LENGTH_LONG).show();
        }


    }
}
