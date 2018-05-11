package com.nlscan.barcodescannerdemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nlscan.barcodescannerdemo.DB.DBController;
import com.nlscan.barcodescannerdemo.utils.Constants;

import net.gotev.speech.Speech;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class UploadScreen extends AppCompatActivity implements View.OnClickListener {

    Button btnaddsuer,btnUploadLoc,btnUploaditemmaster, mbtnUploaditembatches, mbtnStartScan;

    //fileUpload
    DBController controller;
    ArrayList<HashMap<String, String>> myList;
    public static final int requestlocation=5,requestbacthes=10, requestitemmaster = 1;

    SimpleDateFormat dates;
    Date date1;
    Date date2;
    private final int PERMISSION_ALL = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploadscreen);
        Speech.init(getApplicationContext());

        btnaddsuer = (Button) findViewById(R.id.add_user);
        btnUploadLoc = (Button) findViewById(R.id.btn_upload_loc);
        btnUploaditemmaster=(Button) findViewById(R.id.btn_upload_itemmaster);
        mbtnUploaditembatches = (Button) findViewById(R.id.btn_upload_itembatches);
        mbtnStartScan = (Button) findViewById(R.id.btn_start_scan);

        btnaddsuer.setOnClickListener(this);
        btnUploadLoc.setOnClickListener(this);
        btnUploaditemmaster.setOnClickListener(this);

        mbtnUploaditembatches.setOnClickListener(this);
        mbtnStartScan.setOnClickListener(this);
        //upload
        controller = new DBController(this);
        myList = controller.getAllProducts();
        checkAllPermission();
    }


    public boolean hasPermissions( String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&  permissions != null)
        {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void checkAllPermission(){

        String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};

        if(!hasPermissions( PERMISSIONS))
        {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_user:
                adduser();
                break;
            case R.id.btn_upload_loc:
                UploadCSV(requestlocation);
                break;

            case R.id.btn_upload_itembatches:
                UploadCSV(requestbacthes);
                break;

            case R.id.btn_upload_itemmaster:
                UploadCSV(requestitemmaster);
                break;
            case R.id.btn_start_scan:
                if(controller.getlocationcount())
                    startActivity(new Intent(getApplicationContext(), SessionActivity.class));
                else
                    Speech.getInstance().say("Please Upload File for Scanning");

                break;
        }

    }


    public void adduser() {
        final Dialog mNotifyAlert = new Dialog(this);
        mNotifyAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mNotifyAlert.setContentView(R.layout.add_user);
        mNotifyAlert.setCancelable(true);
        final EditText username =(EditText) mNotifyAlert.findViewById(R.id.userid);
        final EditText password =(EditText)  mNotifyAlert.findViewById(R.id.password);
        Button save =(Button) mNotifyAlert.findViewById(R.id.saveuser);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String errorMsg = null;
                if (username == null || username.getText().toString().trim().length() == 0) {
                    errorMsg = "username cant be empty";
                } else if (password == null ||password.getText().toString().length() < 3) {
                    errorMsg ="Password should be of minimum 3 character";
                }
                if (errorMsg == null) {
                  addusertobd(username.getText().toString().trim(),password.getText().toString().trim());
                  mNotifyAlert.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            }
        });

        mNotifyAlert.show();
    }

    private void addusertobd(String userid, String password) {
        controller.adduser(userid,password);
        Toast.makeText(getApplicationContext(),"User added Sucessfully", Toast.LENGTH_LONG ).show();

    }

    private void UploadCSV( int req) {
        Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
        fileintent.addCategory(Intent.CATEGORY_OPENABLE);
        // Set your required file type
        fileintent.setType("*/*");
        try {
            startActivityForResult(fileintent, req);
        } catch (ActivityNotFoundException e)
        {
            Speech.getInstance().say("Error Allowed CSV Files Only");
        }
    }


    //upload
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data == null)
            return;

        Uri selectedMediaUri = data.getData();
        controller = new DBController(getApplicationContext());
        SQLiteDatabase db = controller.getWritableDatabase();
        switch (requestCode) {
            case requestitemmaster:
                try {

                    if (resultCode == RESULT_OK) {

                        try {

                            FileReader file = new FileReader(getPath(getApplicationContext(), selectedMediaUri));
                            BufferedReader buffer = new BufferedReader(file);
                            db.execSQL("delete from " + Constants.TABLE_NAME_ITEMMASTER);
                            ContentValues contentValues = new ContentValues();
                            String line = "";
                            db.beginTransaction();

                            while ((line = buffer.readLine()) != null) {
                                String[] str = line.split(",", 5);
                                String itemcode = str[0].toString();
                                String itemname = str[1].toString();
                                String barcode = str[2].toString();
                                String itemprice =str[3].toString();
                                String expire = str[4].toString();

                                contentValues.put(Constants.COLUMN_ITEMCODE, itemcode);
                                contentValues.put(Constants.COLUMN_ITEMNAME, itemname);
                                contentValues.put(Constants.COLUMN_BARCODE, barcode);
                                contentValues.put(Constants.COLUMN_ITEMPRICE, itemprice);
                                contentValues.put(Constants.COLUMN_EXPIRYBOLEAN, expire);
                                db.insert(Constants.TABLE_NAME_ITEMMASTER, null, contentValues);
                            }
                            db.setTransactionSuccessful();
                            db.endTransaction();
                            db.close();
                            Speech.getInstance().say("Data Updated Successfully");


                        } catch (SQLException e) {
                            Log.e("Error", e.getMessage().toString());
                            if (db.inTransaction())
                                db.endTransaction();
                            db.close();
                        } catch (IOException e) {

                            if (db.inTransaction())
                                db.endTransaction();
                            db.close();
                            Speech.getInstance().say("NO Able to Upload Data");
                        }
                    } else {

                        if (db.inTransaction())
                            db.endTransaction();
                        db.close();
                        Speech.getInstance().say("Only CSV Files allowed");

                    }
                } catch (Exception ex) {

                    if (db.inTransaction())
                        db.endTransaction();
                    db.close();
                    Speech.getInstance().say("Please Upload  CSV Files allowed");

                }
            break;

            case requestbacthes:
                try {

                    if (resultCode == RESULT_OK) {

                        try {

                            FileReader file = new FileReader(getPath(getApplicationContext(), selectedMediaUri));
                            BufferedReader buffer = new BufferedReader(file);
                            db.execSQL("delete from " + Constants.TABLE_NAME_ITEMBATCH);
                            ContentValues contentValues = new ContentValues();
                            String line = "";
                            db.beginTransaction();

                            while ((line = buffer.readLine()) != null) {
                                String[] str = line.split(",", 3);
                                String itemcode = str[0].toString();
                                String lotno = str[1].toString();
                                String expiredate = str[2].toString();

                                contentValues.put(Constants.COLUMN_ITEMCODE, itemcode);
                                contentValues.put(Constants.COLUMN_LOTNO, lotno);
                                contentValues.put(Constants.COLUMN_EXPIRYDATE, expiredate);
                                db.insert(Constants.TABLE_NAME_ITEMBATCH, null, contentValues);
                            }
                            db.setTransactionSuccessful();
                            db.endTransaction();
                            db.close();
                            Speech.getInstance().say("Data Updated Successfully");


                        } catch (SQLException e) {
                            Log.e("Error", e.getMessage().toString());
                            if (db.inTransaction())
                                db.endTransaction();
                            db.close();
                        } catch (IOException e) {

                            if (db.inTransaction())
                                db.endTransaction();
                            db.close();
                            Speech.getInstance().say("NO Able to Upload Data");
                        }
                    } else {

                        if (db.inTransaction())
                            db.endTransaction();
                        db.close();
                        Speech.getInstance().say("Only CSV Files allowed");

                    }
                } catch (Exception ex) {

                    if (db.inTransaction())
                        db.endTransaction();
                    db.close();
                    Speech.getInstance().say("Please Upload  CSV Files allowed");

                }
                break;



        case requestlocation:
        try {

            if (resultCode == RESULT_OK) {

                try {

                    FileReader file = new FileReader(getPath(getApplicationContext(), selectedMediaUri));
                    BufferedReader buffer = new BufferedReader(file);
                    db.execSQL("delete from " + Constants.TABLE_NAME_LOCATION);
                    ContentValues contentValues = new ContentValues();
                    String line = "";
                    db.beginTransaction();

                    while ((line = buffer.readLine()) != null) {
                        String[] str = line.split(",", 2);
                        String loccode = str[0].toString();
                        String locname = str[1].toString();

                        contentValues.put(Constants.COLUMN_LOCATIONCODE, loccode);
                        contentValues.put(Constants.COLUMN_LOCATIONNAME, locname);
                        db.insert(Constants.TABLE_NAME_LOCATION, null, contentValues);
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    db.close();
                    Speech.getInstance().say("Data Updated Successfully");


                } catch (SQLException e) {
                    Log.e("Error", e.getMessage().toString());
                    if (db.inTransaction())
                        db.endTransaction();
                    db.close();
                } catch (IOException e) {

                    if (db.inTransaction())
                        db.endTransaction();
                    db.close();
                    Speech.getInstance().say("NO Able to Upload Data");
                }
            } else {

                if (db.inTransaction())
                    db.endTransaction();
                db.close();
                Speech.getInstance().say("Only CSV Files allowed");

            }
        } catch (Exception ex) {

            if (db.inTransaction())
                db.endTransaction();
            db.close();
            Speech.getInstance().say("Please Upload  CSV Files allowed");

        }
        break;
        }

    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(Context context, Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
