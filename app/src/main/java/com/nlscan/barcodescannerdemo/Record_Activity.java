package com.nlscan.barcodescannerdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nlscan.barcodescannerdemo.CsvReader.CSVWriter;
import com.nlscan.barcodescannerdemo.DB.DBController;
import com.nlscan.barcodescannerdemo.adapter.RecordAdapter;
import com.nlscan.barcodescannerdemo.model.Record;
import com.nlscan.barcodescannerdemo.utils.Constants;
import com.nlscan.barcodescannerdemo.utils.UserSharedPreferences;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Created by fairoze khazi on 22/02/2017.
 */

public class Record_Activity extends Activity {
    DBController mydb;
    Button delete, export;
    RecyclerView mCustomerReviewRecyclerView;
    RecordAdapter recordAdapter;
    ArrayList<Record> array_list;
    EditText msessionname, mdprouctqty, mlocation, mbracode, mlot, mexprie;
    Button mbtnsave, mbtncncel;
    Spinner filterby;
    String projectfilter;
    ArrayList<String> options = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    UserSharedPreferences userSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_list);
        userSharedPreferences = new UserSharedPreferences(getApplicationContext());
        mydb = new DBController(this);

        delete = (Button) findViewById(R.id.deleteAll);
        export = (Button) findViewById(R.id.export);
        filterby = (Spinner) findViewById(R.id.filterby);

        options.addAll(mydb.getallsessionforuser(userSharedPreferences.getUserid()));
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinneritem, options);
        adapter.setDropDownViewResource(R.layout.spinneritem);
        filterby.setAdapter(adapter);


        array_list = mydb.getAllRecords(options.get(0));
        mCustomerReviewRecyclerView = (RecyclerView) findViewById(R.id.list_records);
        mCustomerReviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        recordAdapter = new RecordAdapter(this, array_list);
        mCustomerReviewRecyclerView.setAdapter(recordAdapter);

        recordAdapter.setOnItemClickListener(new RecordAdapter.OnItemClickListener() {
            @Override
            public void ItemEdit(final String id, final String barcode, final String session_name, final String prod_qty, final String prod_lotno, final String entrydatetime, final String location) {

                edifDialog(id, barcode, session_name, prod_qty, prod_lotno, entrydatetime, location);
            }

            @Override
            public void ItemDelete(String id) {
                delete(id);

            }
        });

        filterby.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) view).setTextColor(Color.BLACK); //Change selected text color
                projectfilter = ((TextView) view).getText().toString();
                array_list = mydb.getAllRecords(projectfilter);
                recordAdapter = new RecordAdapter(Record_Activity.this, array_list);
                mCustomerReviewRecyclerView.setAdapter(recordAdapter);
                recordAdapter.notifyDataSetChanged();

                recordAdapter.setOnItemClickListener(new RecordAdapter.OnItemClickListener() {
                    @Override
                    public void ItemEdit(final String id, final String barcode, final String session_name, final String prod_qty, final String prod_lotno, final String entrydatetime, final String location) {


                        edifDialog(id, barcode, session_name, prod_qty, prod_lotno, entrydatetime, location);
                    }

                    @Override
                    public void ItemDelete(String id) {
                        delete(id);

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog(Record_Activity.this);
            }
        });
        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  if(filterby.getSelectedItem().toString().equalsIgnoreCase("ALL"))
                    exportDB();
                else*/
                exportProjDB(filterby.getSelectedItem().toString());
            }
        });


    }


    private void edifDialog(final String id, final String barcode, final String prod_session, final String prod_qty, final String prod_lot, final String entrydatetime, final String slocation) {
        // custom dialog
        final Dialog dialog = new Dialog(Record_Activity.this);
        dialog.setContentView(R.layout.edit_item);
        dialog.setTitle(slocation);
        dialog.setCancelable(true);

        mbracode = (EditText) dialog.findViewById(R.id.barcode);
        msessionname = (EditText) dialog.findViewById(R.id.sessioname);
        mdprouctqty = (EditText) dialog.findViewById(R.id.quantity);
        mlocation = (EditText) dialog.findViewById(R.id.locationname);
        mlot = (EditText) dialog.findViewById(R.id.tvlot);
        mexprie = (EditText) dialog.findViewById(R.id.exprie);
        mbtnsave = (Button) dialog.findViewById(R.id.btn_save);
        mbtncncel = (Button) dialog.findViewById(R.id.btn_calcel);

        mbracode.setText(barcode);
        msessionname.setText(prod_session);
        mdprouctqty.setText(prod_qty);
        mlocation.setText(slocation);
        mlot.setText(prod_lot);
        mexprie.setText(entrydatetime);


        mbracode.setEnabled(false);
        msessionname.setEnabled(false);
        mlocation.setEnabled(false);

        mbtncncel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        mbtnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    if (prod_qty.isEmpty() || prod_session.isEmpty() || prod_lot.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please Enter all Fields", Toast.LENGTH_LONG).show();
                    } else {
                        mydb.updaterow(id, barcode, msessionname.getText().toString(), mdprouctqty.getText().toString(),
                                mlocation.getText().toString(),  mlot.getText().toString(), mexprie.getText().toString());
                        notifydatachnage(filterby.getSelectedItem().toString());
                        dialog.dismiss();
                    }
                }
            }
        });
        dialog.show();
    }


    public void showConfirmDialog(Context c) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);

        alertDialogBuilder.setTitle("DELETE ALL RECORDS");

        alertDialogBuilder.setPositiveButton("NO",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });

        alertDialogBuilder.setNegativeButton("YES",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mydb.deletealldata(filterby.getSelectedItem().toString());
                        notifydatachnage(filterby.getSelectedItem().toString());
                        Toast.makeText(getApplicationContext(), " DATA DELETED", Toast.LENGTH_LONG).show();

                    }
                });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


    private void delete(String id) {
        mydb.deletesingledata(id);
        notifydatachnage(filterby.getSelectedItem().toString());

    }

    void notifydatachnage(String s) {

        array_list = mydb.getAllRecords(s);
        mydb.close();
        recordAdapter = new RecordAdapter(getApplicationContext(), array_list);
        mCustomerReviewRecyclerView.setAdapter(recordAdapter);
        recordAdapter.notifyDataSetChanged();

        recordAdapter.setOnItemClickListener(new RecordAdapter.OnItemClickListener() {
            @Override
            public void ItemEdit(final String id, final String barcode, final String session_name, final String prod_qty, final String prod_lotno, final String entrydatetime, final String location) {


                edifDialog(id, barcode, session_name, prod_qty, prod_lotno, entrydatetime, location);
            }

            @Override
            public void ItemDelete(String id) {
                delete(id);
            }
        });
    }


    private void exportDB() {
        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "Allrecord.csv");
        if (file.exists())
            file.delete();
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = mydb.getWritableDatabase();
            Cursor curCSV = db.rawQuery("select * FROM " + Constants.TABLE_SCANITEMS, null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                //Which column you want to exprort
                String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2), curCSV.getString(3), curCSV.getString(4), curCSV.getString(5), curCSV.getString(6), curCSV.getString(7)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            db.close();
            sendEmail("Allrecord.csv");
        } catch (Exception sqlEx) {
            Log.e("ScanActivity", sqlEx.getMessage(), sqlEx);
        }
    }


    private void exportProjDB(String projname) {
        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "Projectrecord.csv");
        if (file.exists())
            file.delete();
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = mydb.getWritableDatabase();
            Cursor curCSV = db.rawQuery("select * from tblscanitems where sessioname='" + projname + "'", null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                //Which column you want to exprort
                String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2), curCSV.getString(3), curCSV.getString(4), curCSV.getString(5), curCSV.getString(6), curCSV.getString(7)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            db.close();
            sendEmail("Projectrecord.csv");
        } catch (Exception sqlEx) {
            Log.e("ScanActivity", sqlEx.getMessage(), sqlEx);
        }
    }

    void sendEmail(String filename) {
        File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename);
        Uri path = Uri.fromFile(filelocation);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("vnd.android.cursor.dir/email");
        emailIntent.putExtra(Intent.EXTRA_STREAM, path);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Boutiqaat WareHouse Data");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }


}