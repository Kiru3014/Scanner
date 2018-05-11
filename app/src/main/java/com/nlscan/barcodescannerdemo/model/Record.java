package com.nlscan.barcodescannerdemo.model;

/**
 * Created by fairoze khazi on 22/02/2017.
 */

public class Record {

    private String Id;
    private String barcode;
    private String StockSessionName;
    private String Prod_qty;
    private String LotNo;
    private String entrydatetime;
    private String Expirydate;
    private String location;

//    public Record(String bc, String sessionname, String qty, String LotNo, String Expirydate, String entrydatetime) {
//        this.barcode = bc;
//        this.StockSessionName = sessionname;
//        this.Prod_qty = qty;
//        this.LotNo = LotNo;
//        this.entrydatetime = entrydatetime;
//        this.Expirydate =Expirydate;
//    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Record(String Id,String bc, String sessionname, String qty, String LotNo, String Expirydate, String location) {
        this.Id=Id;
        this.barcode = bc;
        this.StockSessionName = sessionname;
        this.Prod_qty = qty;
        this.LotNo = LotNo;
 //       this.entrydatetime = entrydatetime;
        this.Expirydate =Expirydate;
        this.location=location;
    }


    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getStockSessionName() {
        return StockSessionName;
    }

    public void setStockSessionName(String stockSessionName) {
        StockSessionName = stockSessionName;
    }

    public String getProd_qty() {
        return Prod_qty;
    }

    public void setProd_qty(String prod_qty) {
        Prod_qty = prod_qty;
    }

    public String getLotNo() {
        return LotNo;
    }

    public void setLotNo(String lotNo) {
        LotNo = lotNo;
    }

    public String getEntrydatetime() {
        return entrydatetime;
    }

    public void setEntrydatetime(String entrydatetime) {
        this.entrydatetime = entrydatetime;
    }

    public String getExpirydate() {
        return Expirydate;
    }

    public void setExpirydate(String expirydate) {
        Expirydate = expirydate;
    }
}
