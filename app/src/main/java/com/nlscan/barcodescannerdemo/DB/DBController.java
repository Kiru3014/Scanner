package com.nlscan.barcodescannerdemo.DB;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nlscan.barcodescannerdemo.model.Record;
import com.nlscan.barcodescannerdemo.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.nlscan.barcodescannerdemo.utils.Constants.COLUMN_BARCODE;
import static com.nlscan.barcodescannerdemo.utils.Constants.COLUMN_EXPIRYDATE;
import static com.nlscan.barcodescannerdemo.utils.Constants.COLUMN_ID;
import static com.nlscan.barcodescannerdemo.utils.Constants.COLUMN_LOCATIONNAME;
import static com.nlscan.barcodescannerdemo.utils.Constants.COLUMN_LOTNO;
import static com.nlscan.barcodescannerdemo.utils.Constants.COLUMN_QTY;
import static com.nlscan.barcodescannerdemo.utils.Constants.COLUMN_SESSIONCOMPLETE;
import static com.nlscan.barcodescannerdemo.utils.Constants.COLUMN_SESSIONCOMPLETEDATE;
import static com.nlscan.barcodescannerdemo.utils.Constants.COLUMN_SESSIONNAME;
import static com.nlscan.barcodescannerdemo.utils.Constants.COLUMN_USERID;

public class DBController extends SQLiteOpenHelper {
    private static final String LOGCAT = null;

    public DBController(Context applicationcontext) {

        super(applicationcontext, "stocktakemohan.db", null, 2);  // creating DATABASE

        Log.d(LOGCAT, "Created");

    }


    @Override
    public void onCreate(SQLiteDatabase database) {

        String query;

        query = "CREATE TABLE IF NOT EXISTS " + Constants.TABLE_USERS + " ( " + Constants.COLUMN_USERID + " TEXT, " + Constants.COLUMN_PASSWORD + " TEXT ," + Constants.COLUMN_TYPE + " TEXT )";
        database.execSQL(query);

        query = "CREATE TABLE IF NOT EXISTS " + Constants.TABLE_NAME_LOCATION + " ( " + Constants.COLUMN_LOCATIONCODE+ " TEXT, " + Constants.COLUMN_LOCATIONNAME + " TEXT )";
        database.execSQL(query);

        query = "CREATE TABLE IF NOT EXISTS " + Constants.TABLE_NAME_ITEMMASTER + " ( " + Constants.COLUMN_ITEMCODE+ " TEXT, " + Constants.COLUMN_ITEMNAME +" TEXT, " + Constants.COLUMN_BARCODE + " TEXT, " + Constants.COLUMN_ITEMPRICE + " TEXT, " + Constants.COLUMN_EXPIRYBOLEAN +" TEXT )";
        database.execSQL(query);

        query = "CREATE TABLE IF NOT EXISTS " + Constants.TABLE_NAME_ITEMBATCH+ " ( " + Constants.COLUMN_ITEMCODE+ " TEXT, " + Constants.COLUMN_LOTNO +" TEXT, "  + Constants.COLUMN_EXPIRYDATE +" TEXT )";
        database.execSQL(query);


        query = "CREATE TABLE IF NOT EXISTS " + Constants.TABLE_NAME_SESSION+ " ( " +"ID INTEGER PRIMARY KEY AUTOINCREMENT , "+  Constants.COLUMN_SESSIONNAME +" TEXT, "  + Constants.COLUMN_LOCATIONNAME +" TEXT, " +Constants.COLUMN_USERID +" TEXT, " +Constants.COLUMN_SESSIONSTARTDATE+" TEXT, "  +Constants.COLUMN_SESSIONCOMPLETEDATE+" TEXT, "+ Constants.COLUMN_SESSIONCOMPLETE +" TEXT ,"+  Constants.COLUMN_LOOKUP +" TEXT )";
        database.execSQL(query);


        query = "CREATE TABLE IF NOT EXISTS " + Constants.TABLE_SCANITEMS + " ( " +"ID INTEGER PRIMARY KEY AUTOINCREMENT , "+ Constants.COLUMN_SESSIONNAME+ " TEXT, " + Constants.COLUMN_LOCATIONNAME + " TEXT ," + Constants.COLUMN_BARCODE+ " TEXT," + Constants.COLUMN_QTY+ " TEXT ," + Constants.COLUMN_LOTNO+ " TEXT, "+Constants.COLUMN_EXPIRYDATE+ " TEXT, "+ Constants.COLUMN_ITEMNAME+ " TEXT, " + Constants.COLUMN_ITEMPRICE+ " TEXT, "+Constants.COLUMN_ENTRYDATE + " TEXT )";

        database.execSQL(query);

    }


    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS" + Constants.TABLE_SCANITEMS;
        database.execSQL(query);
        query = "DROP TABLE IF EXISTS" + Constants.TABLE_NAME_LOCATION;
        database.execSQL(query);
        query = "DROP TABLE IF EXISTS" + Constants.TABLE_NAME_ITEMMASTER;
        database.execSQL(query);
        query = "DROP TABLE IF EXISTS" + Constants.TABLE_NAME_ITEMBATCH;
        database.execSQL(query);
        query = "DROP TABLE IF EXISTS" + Constants.TABLE_USERS;
        database.execSQL(query);
        query = "DROP TABLE IF EXISTS" + Constants.TABLE_NAME_SESSION;
        database.execSQL(query);
        onCreate(database);
    }

    public int getcount() {
        String countQuery = "SELECT COUNT(*) FROM " + Constants.TABLE_SCANITEMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int cnt = cursor.getInt(0);
        cursor.close();
        db.close();
        return cnt;

    }

    public ArrayList<String> getItemData(String barcode) {
        ArrayList<String> journalList = new ArrayList<>();
            String query = "SELECT * FROM " + Constants.TABLE_SCANITEMS + " WHERE " + Constants.COLUMN_BARCODE + " = " + barcode;
            SQLiteDatabase database = this.getWritableDatabase();
            Cursor cursor = database.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    journalList.add(cursor.getString(1));
                    journalList.add(cursor.getString(2));
                    journalList.add(cursor.getString(3));
                } while (cursor.moveToNext());
            }

        cursor.close();
        database.close();
        return journalList;
    }




    public ArrayList<HashMap<String, String>> getAllProducts() {

        ArrayList<HashMap<String, String>> journalList;
        journalList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + Constants.TABLE_SCANITEMS;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

            do {
                //Id, Company,Name,Price
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("a", cursor.getString(0));
                map.put("b", cursor.getString(1));
                map.put("c", cursor.getString(2));
                map.put("d", cursor.getString(3));
                journalList.add(map);
                Log.e("dataofList", cursor.getString(0) + "," + cursor.getString(1) + "," + cursor.getString(2) + "," + cursor.getString(3));
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return journalList;

    }


    public ArrayList<String> getlocation() {
        ArrayList<String> loc = new ArrayList<>();
        String query = "SELECT  * FROM " + Constants.TABLE_NAME_LOCATION;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor curs = database.rawQuery(query, null);

        if (curs.moveToFirst()) {
            do {
                loc.add(curs.getString(1));

            } while (curs.moveToNext());
        }

        curs.close();
        database.close();
        return loc;
    }
    public Boolean checklogin(String bName, String bPassword) {
        String query = "SELECT * FROM " + Constants.TABLE_USERS ;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor c = database.rawQuery(query, null);
        int unameIndex = c.getColumnIndex(Constants.COLUMN_USERID );
        int pwordIndex = c.getColumnIndex(Constants.COLUMN_PASSWORD );

        if (c.moveToFirst()) {

            do {
                String savedUname = c.getString(unameIndex);
                String savedPword = c.getString(pwordIndex);
                if (savedUname.equals(bName)) {

                    if (savedPword.equals(bPassword)) {
                        c.close();
                        database.close();
                        return true;

                    } else {
                        c.close();
                        database.close();
                        return false;
                    }
                }
            } while (c.moveToNext());
        }

        c.close();
        database.close();
        return false;

    }

    public void createadminlogin() {
        String countQuery = "SELECT COUNT(*) FROM " + Constants.TABLE_USERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int cnt = cursor.getInt(0);
        cursor.close();
        if(cnt==0){
            ContentValues values = new ContentValues();
            values.put(Constants.COLUMN_USERID, "admin");
            values.put(Constants.COLUMN_PASSWORD, "start123");
            values.put(Constants.COLUMN_TYPE, "1");
            SQLiteDatabase db1 = this.getWritableDatabase();
            db1.insert(Constants.TABLE_USERS,null,values);
        }
    }

    public void adduser(String userid, String password) {
            ContentValues values = new ContentValues();
            values.put(Constants.COLUMN_USERID, userid);
            values.put(Constants.COLUMN_PASSWORD,password);
            values.put(Constants.COLUMN_TYPE, "0");
            SQLiteDatabase db1 = this.getWritableDatabase();
            db1.insert(Constants.TABLE_USERS,null,values);
        db1.close();
    }


    public void addSession(String sessionname, String Location , String lookup,String Userid ) {

        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_SESSIONNAME, sessionname);
        values.put(Constants.COLUMN_LOCATIONNAME,Location);
        values.put(Constants.COLUMN_USERID, Userid);
        values.put(Constants.COLUMN_SESSIONSTARTDATE, timeStamp);
        values.put(Constants.COLUMN_SESSIONCOMPLETE, "0");
        values.put(Constants.COLUMN_LOOKUP, lookup);

        SQLiteDatabase db1 = this.getWritableDatabase();
        db1.insert(Constants.TABLE_NAME_SESSION,null,values);
        db1.close();

    }


    public ArrayList<String> getactivesessionforuser(String Userid) {
        ArrayList<String> sessionname = new ArrayList<>();
        String query = "SELECT "+ Constants.COLUMN_SESSIONNAME+" FROM " + Constants.TABLE_NAME_SESSION + " WHERE " + Constants.COLUMN_USERID + "=?" + " AND "+ Constants.COLUMN_SESSIONCOMPLETE+"=0" ;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor =  database.rawQuery(query ,
                new String [] {Userid});



        int itemcodeindex = cursor.getColumnIndex(Constants.COLUMN_SESSIONNAME );

        if (cursor.moveToFirst()) {
            do {
                sessionname.add(cursor.getString(itemcodeindex));
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();


        return sessionname;
    }


    public ArrayList<String> getallsessionforuser(String Userid) {
        ArrayList<String> sessionname = new ArrayList<>();
        String query;
        if(Userid.equals("admin"))
            query = "SELECT "+ Constants.COLUMN_SESSIONNAME+" FROM " + Constants.TABLE_NAME_SESSION ;
        else
         query = "SELECT "+ Constants.COLUMN_SESSIONNAME+" FROM " + Constants.TABLE_NAME_SESSION + " WHERE " + Constants.COLUMN_USERID + "='"+Userid+"'" ;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor =  database.rawQuery(query ,null);
        int itemcodeindex = cursor.getColumnIndex(Constants.COLUMN_SESSIONNAME );

        if (cursor.moveToFirst()) {
            do {
                sessionname.add(cursor.getString(itemcodeindex));
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();


        return sessionname;
    }
    public ArrayList<String> getlocationforsession(String sessionname) {
        ArrayList<String> sessionloc = new ArrayList<>();
        String query = "SELECT "+ Constants.COLUMN_LOCATIONNAME +" FROM " + Constants.TABLE_NAME_SESSION + " WHERE " + Constants.COLUMN_SESSIONNAME + "=?" ;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query,new String [] {sessionname});


        if (cursor.moveToFirst()) {
            do {
                sessionloc.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return sessionloc;
    }


    public ArrayList<HashMap<String, String>> getlookupdata( String barcode ) {
        String itemcode="";
        String itemcodequery = "SELECT " +Constants.COLUMN_ITEMCODE+ " FROM " + Constants.TABLE_NAME_ITEMMASTER + " WHERE " + Constants.COLUMN_BARCODE + "='"+barcode+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery(itemcodequery, null);

        int itemcodeindex = cur.getColumnIndex(Constants.COLUMN_ITEMCODE );

        if(cur.moveToFirst()){
            itemcode= cur.getString(itemcodeindex);
        }
        cur.close();
        db.close();

        if(!itemcode.isEmpty()) {
            ArrayList<HashMap<String, String>> journalList;
            journalList = new ArrayList<HashMap<String, String>>();
            String selectQuery = "SELECT  * FROM " + Constants.TABLE_NAME_ITEMBATCH  + " WHERE " + Constants.COLUMN_ITEMCODE + "='"+itemcode+"'";
            SQLiteDatabase database = this.getReadableDatabase();
            Cursor cursor = database.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {

                do {
                    //Id, Company,Name,Price
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("a", cursor.getString(0));
                    map.put("b", cursor.getString(1));
                    map.put("c", cursor.getString(2));
                    journalList.add(map);
                    Log.e("dataofList", cursor.getString(0) + "," + cursor.getString(1) + "," + cursor.getString(2) );
                } while (cursor.moveToNext());
            }

            cursor.close();
            database.close();
            return journalList;
        }

        return null;

    }

    public boolean getlocationcount() {
        String countQuery = "SELECT COUNT(*) FROM " + Constants.TABLE_NAME_LOCATION;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int cnt = cursor.getInt(0);
        cursor.close();
        db.close();
        if(cnt==0){
            return false;
        }
        return true;
    }



    public boolean updaterow(String id, String barecode, String sessionname, String qty, String location, String lot, String entrytime)
    {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_BARCODE, barecode);
        values.put(Constants.COLUMN_SESSIONNAME, sessionname);
        values.put(Constants.COLUMN_QTY, qty);
        values.put(Constants.COLUMN_LOCATIONNAME,location);
        values.put(Constants.COLUMN_LOTNO, lot);
        values.put(Constants.COLUMN_EXPIRYDATE, entrytime);
        values.put(Constants.COLUMN_ENTRYDATE,timeStamp);
        db.update(Constants.TABLE_SCANITEMS, values, "id = ? ", new String[]{id});
        db.close();
        return true;
    }

    public Integer deleterow(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(Constants.TABLE_SCANITEMS,
                "ID = ? ",
                new String[]{Integer.toString(id)});
    }

    public ArrayList<Record> getAllRecords() {
        ArrayList<Record> arrayList = new ArrayList<Record>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        res=db.rawQuery("select * from " + Constants.TABLE_SCANITEMS, null);

        res.moveToFirst();

 //       Record(String Id,String bc, String sessionname, String qty, String LotNo, String Expirydate, String entrydatetime, String location)

        while (res.isAfterLast() == false) {
            Record rc = new Record(res.getString(res.getColumnIndex(COLUMN_ID)), res.getString(res.getColumnIndex(COLUMN_BARCODE)), res.getString(res.getColumnIndex(COLUMN_SESSIONNAME)),
                    res.getString(res.getColumnIndex(COLUMN_QTY)), res.getString(res.getColumnIndex(COLUMN_LOTNO)),
                    res.getString(res.getColumnIndex(COLUMN_EXPIRYDATE)), res.getString(res.getColumnIndex(COLUMN_LOCATIONNAME)));
            arrayList.add(rc);
            res.moveToNext();
        }
        res.close();
        db.close();
        return arrayList;
    }


    public ArrayList<Record> getAllRecords(String Sesionname) {
        ArrayList<Record> arrayList = new ArrayList<Record>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        res=db.rawQuery("select * from " + Constants.TABLE_SCANITEMS+" where "+ Constants.COLUMN_SESSIONNAME+ "='"+Sesionname+"'", null);

        res.moveToFirst();

        if (res.moveToFirst()) {
            do {
                Record rc = new Record(res.getString(res.getColumnIndex(COLUMN_ID)), res.getString(res.getColumnIndex(COLUMN_BARCODE)), res.getString(res.getColumnIndex(COLUMN_SESSIONNAME)),
                        res.getString(res.getColumnIndex(COLUMN_QTY)), res.getString(res.getColumnIndex(COLUMN_LOTNO)),
                        res.getString(res.getColumnIndex(COLUMN_EXPIRYDATE)), res.getString(res.getColumnIndex(COLUMN_LOCATIONNAME)));
                arrayList.add(rc);
                res.moveToNext();
            } while (res.moveToNext());
        }

        res.close();
        db.close();
        return arrayList;
    }

    public void addbarcodeitem(String msession, String mlocation, String barcode, String qty, String lot, String expirydate, String prodname, String prodprice) {

        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_SESSIONNAME, msession);
        values.put(Constants.COLUMN_LOCATIONNAME,mlocation);
        values.put(Constants.COLUMN_BARCODE, barcode);
        values.put(Constants.COLUMN_QTY, qty);
        values.put(Constants.COLUMN_LOTNO, lot);
        values.put(Constants.COLUMN_EXPIRYDATE, expirydate);
        values.put(Constants.COLUMN_ENTRYDATE, timeStamp);
        values.put(Constants.COLUMN_ITEMNAME, prodname);
        values.put(Constants.COLUMN_ITEMPRICE, prodprice);

        try {
            SQLiteDatabase db1 = this.getWritableDatabase();
            db1.insert(Constants.TABLE_SCANITEMS, null, values);
            db1.close();
        }
        catch (Exception e){

        }

    }

    public Boolean finishsession(String msession) {

        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        SQLiteDatabase db = this.getWritableDatabase();



        String strquery="UPDATE "+ Constants.TABLE_NAME_SESSION+" SET "+ COLUMN_SESSIONCOMPLETE+"=1, " +COLUMN_SESSIONCOMPLETEDATE+"='"+timeStamp+"'"+ " WHERE " +COLUMN_SESSIONNAME+ "='"+msession+"'";
        db.execSQL(strquery);
        db.close();
        return true;
    }

    public Boolean getsessioncount(String userid) {

        String countQuery;
        if(userid.equals("admin"))
            countQuery= "SELECT COUNT(*) FROM " + Constants.TABLE_NAME_SESSION;
        else
            countQuery= "SELECT COUNT(*) FROM " + Constants.TABLE_NAME_SESSION+ " WHERE "+ COLUMN_USERID+ "='"+userid+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int cnt = cursor.getInt(0);
        cursor.close();
        db.close();
        if(cnt>0)
            return true;
        else
            return false;
    }

    public void deletealldata(String session)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete  from " + Constants.TABLE_SCANITEMS+ " WHERE " +COLUMN_SESSIONNAME + "='"+session+"'");
            db.close();
        } catch (Exception e)
        {

        }
    }

    public void deletesingledata(String sessionid)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete  from " + Constants.TABLE_SCANITEMS+ " WHERE " +"id='"+sessionid+"'");
            db.close();
        } catch (Exception e)
        {

        }

    }


    public void addnewlot(String itemcode, String lotno, String expiry) {

        if(!itemcode.isEmpty()) {
            String selectQuery = "SELECT  * FROM " + Constants.TABLE_NAME_ITEMBATCH  + " WHERE " + Constants.COLUMN_ITEMCODE + "='"+itemcode+"'" +" AND " +  Constants.COLUMN_LOTNO + "='"+lotno+"'" + " AND " +  Constants.COLUMN_EXPIRYDATE+ "='"+expiry+"'" ;
            SQLiteDatabase database = this.getReadableDatabase();
            Cursor cursor = database.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                cursor.close();
                database.close();
                return;
            }
            else {
                 ContentValues values = new ContentValues();
                values.put(Constants.COLUMN_ITEMCODE, itemcode);
                values.put(Constants.COLUMN_LOTNO, lotno);
                values.put(Constants.COLUMN_EXPIRYDATE, expiry);
                SQLiteDatabase db1 = this.getWritableDatabase();
                db1.insert(Constants.TABLE_NAME_ITEMBATCH,null,values);
                db1.close();

            }

            return ;
        }
    }

    public ArrayList<String> getproddata(String barcode) {

        ArrayList<String> proddata=new ArrayList<>();
        String itemcodequery = "SELECT " +Constants.COLUMN_ITEMNAME+","+Constants.COLUMN_ITEMPRICE+ " FROM " + Constants.TABLE_NAME_ITEMMASTER + " WHERE " + Constants.COLUMN_BARCODE + "='"+barcode+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery(itemcodequery, null);

        int itemname = cur.getColumnIndex(Constants.COLUMN_ITEMNAME );
        int itemprice = cur.getColumnIndex(Constants.COLUMN_ITEMPRICE );

        if(cur.moveToFirst()){
            proddata.add(cur.getString(itemname));
            proddata.add(cur.getString(itemprice));
        }
        cur.close();
        db.close();
        return proddata;
    }

    public String getlookforsesion(String msession) {

        String lookup="";
        String itemcodequery = "SELECT " +Constants.COLUMN_LOOKUP+ " FROM " + Constants.TABLE_NAME_SESSION + " WHERE " + Constants.COLUMN_SESSIONNAME + "='"+msession+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery(itemcodequery, null);

        int itemcodeindex = cur.getColumnIndex(Constants.COLUMN_LOOKUP );

        if(cur.moveToFirst()){
            lookup= cur.getString(itemcodeindex);
        }
        cur.close();
        db.close();
        return  lookup;
    }
}
