package com.nlscan.barcodescannerdemo;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.ScannerNotClaimedException;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.TriggerStateChangeEvent;
import com.honeywell.aidc.UnsupportedPropertyException;
import com.nlscan.barcodescannerdemo.DB.DBController;
import com.nlscan.barcodescannerdemo.adapter.CustomListAdapter;
import com.nlscan.barcodescannerdemo.adaptor.CustomAdapter;
import com.nlscan.barcodescannerdemo.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScanActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener
{
    Bundle bundle;
    String usrId, mScanType, msessioname, mlocation, msession, itemcode="", lookup="0";
    DBController controller;
    ArrayList<String> mlistofSessions, mlistoflocation;
    Spinner spin;
    CustomAdapter customAdapter;
    TextView tvsessioname, tvlocationname, product_price,mproduname;
    RadioGroup radioGroup;
    RadioButton radioButton;
    EditText mbarcode, mqty, mlotno, mexpdate;
    Button msave, mrest, mclosession, mresumesession,startscan;
    PopupWindow mPopupWindow;
    LinearLayout linearLayout, locatinview, scanscection,withlookupsec;
    //scanner
    private BarcodeReader barcodeReader;
    ArrayList<String> proddata;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        linearLayout = (LinearLayout) findViewById(R.id.rl);
        controller = new DBController(this);

        //scannersetup();

        proddata=new ArrayList<>();
        bundle = getIntent().getExtras();
        assert bundle != null;
        mScanType = bundle.getString(Constants.SCANTYPE);
        usrId = bundle.getString(Constants.USR_ID);
        //initviews
        spin = (Spinner) findViewById(R.id.simplesessionSpinner);
        spin.setOnItemSelectedListener(this);
        startscan =(Button)findViewById(R.id.btn_scan_for);
        tvsessioname = (TextView) findViewById(R.id.tv_sessionname);
        tvlocationname = (TextView) findViewById(R.id.tv_locationname);
        mbarcode = (EditText) findViewById(R.id.barcode);
        mqty = (EditText) findViewById(R.id.barcode_qty);
        mproduname = (TextView) findViewById(R.id.barcode_name);
        mlotno = (EditText) findViewById(R.id.barcode_lot);
        mexpdate = (EditText) findViewById(R.id.barcode_date);
        mbarcode.requestFocus();
        msave = (Button) findViewById(R.id.btn_save);
        mrest = (Button) findViewById(R.id.btn_reset);
        mclosession = (Button) findViewById(R.id.btn_finish);
        mresumesession = (Button) findViewById(R.id.btn_later);
        product_price=(TextView) findViewById(R.id.prod_price);
        withlookupsec=(LinearLayout) findViewById(R.id.withlookupsec);

        msave.setOnClickListener(this);
        mrest.setOnClickListener(this);
        mclosession.setOnClickListener(this);
        mresumesession.setOnClickListener(this);

        //getting views to setvisiale
        locatinview = (LinearLayout) findViewById(R.id.ll_loc);
        scanscection = (LinearLayout) findViewById(R.id.scan_sec);


        radioGroup = (RadioGroup) findViewById(R.id.radiolookup);
        radioGroup.setEnabled(false);
        RadioButton radiowithlokup=(RadioButton) findViewById(R.id.radiowithlookup);
        RadioButton radiowithoutlokup=(RadioButton) findViewById(R.id.radiowithoutlook);

        radiowithlokup.setEnabled(false);
        radiowithoutlokup.setEnabled(false);

        //checking which starte
        if (mScanType.equals("R")) {
            mlistofSessions = controller.getactivesessionforuser(usrId);
            if (mlistofSessions != null && mlistofSessions.size() > 0)
                customAdapter = new CustomAdapter(getApplicationContext(), mlistofSessions);
            spin.setAdapter(customAdapter);
            spin.setVisibility(View.VISIBLE);
            tvsessioname.setVisibility(View.INVISIBLE);
            msession = mlistofSessions.get(0);

            mlistoflocation = controller.getlocationforsession(msession);
            if (mlistoflocation != null && mlistoflocation.size() > 0) {
                tvlocationname.setVisibility(View.VISIBLE);
                mlocation = mlistoflocation.get(0);
                tvlocationname.setText(mlocation);
            }

            mlocation = tvlocationname.getText().toString();
            lookup= controller.getlookforsesion(msession);
            if(lookup.equals("1")) {
                radioGroup.check(R.id.radiowithlookup);
                withlookupsec.setVisibility(View.VISIBLE);
            }
            else if(lookup.equals("0")) {
                radioGroup.check(R.id.radiowithoutlook);
                withlookupsec.setVisibility(View.GONE);
            }
            radioGroup.setEnabled(false);

        } else {
            msessioname = bundle.getString(Constants.SESSIONNAME);
            mlocation = bundle.getString(Constants.LOCATION);
            lookup= bundle.getString(Constants.COLUMN_LOOKUP);
            if(lookup.equals("1")) {
                radioGroup.check(R.id.radiowithlookup);
                withlookupsec.setVisibility(View.VISIBLE);
            }
            else if(lookup.equals("0")) {
                radioGroup.check(R.id.radiowithoutlook);
                withlookupsec.setVisibility(View.GONE);
            }
            radioGroup.setEnabled(false);
            tvsessioname.setVisibility(View.VISIBLE);
            spin.setVisibility(View.GONE);
            tvlocationname.setVisibility(View.VISIBLE);
            tvsessioname.setText(msessioname);
            tvlocationname.setText(mlocation);
            locatinview.setVisibility(View.VISIBLE);
            radioGroup.setVisibility(View.VISIBLE);
            scanscection.setVisibility(View.VISIBLE);
            msession = msessioname;
            mlocation = tvlocationname.getText().toString();
        }


        if(lookup.equals("1"))
        {
            startscan.setVisibility(View.VISIBLE);
        }

        startscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkbarcodedata();
            }
        });

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        msession = mlistofSessions.get(i);
        mlistoflocation = controller.getlocationforsession(msession);
        if (mlistoflocation != null && mlistoflocation.size() > 0) {
            tvlocationname.setVisibility(View.VISIBLE);
            mlocation = mlistoflocation.get(0);
            tvlocationname.setText(mlocation);
        }

        radioGroup.setVisibility(View.VISIBLE);
        locatinview.setVisibility(View.VISIBLE);
        scanscection.setVisibility(View.VISIBLE);
        resetall();
        lookup=controller.getlookforsesion(msession);
        if(lookup.equals("1")) {
            radioGroup.check(R.id.radiowithlookup);
            withlookupsec.setVisibility(View.VISIBLE);
        }
        else if(lookup.equals("0")) {
            radioGroup.check(R.id.radiowithoutlook);
            withlookupsec.setVisibility(View.GONE);
        }
        radioGroup.setEnabled(false);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {


        switch (view.getId()) {
            case R.id.btn_save:
                savedata();
                break;
            case R.id.btn_reset:
                resetall();
                break;
            case R.id.btn_finish:
                finishsession();
                break;
            case R.id.btn_later:
                finish();
                //checkbarcodedata();
                break;
        }

    }

    private void finishsession() {
        controller.finishsession(msession);
        finish();

    }

    private void checkbarcodedata() {
        if (mbarcode.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "enter barcode", Toast.LENGTH_SHORT).show();
        } else {
            proddata.clear();
            mproduname.setText("");
            product_price.setText("");

            proddata=controller.getproddata(mbarcode.getText().toString());
            if(proddata.size()>0)
                mproduname.setText(proddata.get(0));
            if(proddata.size()>1)
                product_price.setText(proddata.get(1));

            if(lookup.equals("1")) {
                ArrayList<HashMap<String, String>> hashMaps;
                hashMaps = controller.getlookupdata(mbarcode.getText().toString());
                if (hashMaps != null) {
                    createalert(hashMaps, proddata.get(0));
                } else {
                    Toast.makeText(getApplicationContext(), "No Recored found enter data", Toast.LENGTH_LONG).show();
                    mqty.requestFocus();
                }
            }
        }
    }

    private void createalert(final ArrayList<HashMap<String, String>> hashMaps, String prodname) {
        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.custom, null);

        // Initialize a new instance of popup window
        mPopupWindow = new PopupWindow(
                customView,
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        );

        Button poup = (Button) customView.findViewById(R.id.close_popup);

        Button newlot=(Button) customView.findViewById(R.id.newlot);

        TextView prodnametxt=(TextView) customView.findViewById(R.id.prodname);

        prodnametxt.setText(prodname);
        poup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });

        newlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mqty.setText("1");
                mlotno.setEnabled(true);
                mexpdate.setEnabled(true);
                mPopupWindow.dismiss();
                if(hashMaps.size()>0)
                    itemcode= hashMaps.get(0).get("a");
            }
        });
// set the custom dialog components - text, image and button
        ListView listView = (ListView) customView.findViewById(R.id.list_view);
        CustomListAdapter customAdapter = new CustomListAdapter(getApplicationContext(), hashMaps);
        listView.setAdapter(customAdapter);
        mPopupWindow.showAtLocation(linearLayout, Gravity.CENTER, 0, 0);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mlotno.setText(hashMaps.get(i).get("b"));
                mexpdate.setText(hashMaps.get(i).get("c"));
                mproduname.requestFocus();
                mlotno.setEnabled(false);
                mexpdate.setEnabled(false);
                mPopupWindow.dismiss();
            }
        });

    }

    private void savedata()
    {
            if(lookup.equals("1"))
            {
                if (mbarcode.getText().toString().isEmpty() ||
                        mqty.getText().toString().isEmpty() ||
                        mlotno.getText().toString().isEmpty() ||
                        mexpdate.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(),"please enter all data", Toast.LENGTH_LONG).show();
                }
                else
                {
                    controller.addbarcodeitem(msession,mlocation,mbarcode.getText().toString(),mqty.getText().toString(),mlotno.getText().toString(),
                            mexpdate.getText().toString(),mproduname.getText().toString(),mproduname.getText().toString());
                    controller.addnewlot(itemcode,mlotno.getText().toString(),mexpdate.getText().toString());

                    Toast.makeText(getApplicationContext(),"item added successfully", Toast.LENGTH_LONG).show();
                    resetall();
                }

            }else
            {
                if (mbarcode.getText().toString().isEmpty() || mqty.getText().toString().isEmpty() )
                {

                    Toast.makeText(getApplicationContext(),"please enter all data", Toast.LENGTH_LONG).show();
                }
                else
                {
                    controller.addbarcodeitem(msession,mlocation,mbarcode.getText().toString(),mqty.getText().toString(),"NA","NA",mproduname.getText().toString(),mproduname.getText().toString());
                    Toast.makeText(getApplicationContext(),"item added successfully", Toast.LENGTH_LONG).show();
                    resetall();
                }
            }


//        Log.d("TEST LOOK", lookup);
//        Log.d("TEST session", msession);
        Log.d("TEST Location", mlocation);

    }

    private void resetall() {
        mbarcode.setText("");
        mqty.setText("");
        mproduname.setText("");
        mlotno.setText("");
        mexpdate.setText("");
        mbarcode.requestFocus();
        product_price.setText("");
    }

}
