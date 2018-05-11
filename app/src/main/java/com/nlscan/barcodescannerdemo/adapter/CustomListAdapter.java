package com.nlscan.barcodescannerdemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nlscan.barcodescannerdemo.R;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflter;
    ArrayList<HashMap<String, String>> hashMaps;

    public CustomListAdapter(Context applicationContext, ArrayList<HashMap<String, String>> hashMaps) {
        this.context = applicationContext;
        this.hashMaps = hashMaps;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return hashMaps.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_list_items, null);
        TextView itemid = (TextView) view.findViewById(R.id.item_code);
        TextView lotno = (TextView) view.findViewById(R.id.item_lot);
        TextView expdate = (TextView) view.findViewById(R.id.item_experidate);

        itemid.setText("ITEMID : "+hashMaps.get(i).get("a"));
        lotno.setText("LOT NO : "+hashMaps.get(i).get("b"));
        expdate.setText("Exprie Date : "+hashMaps.get(i).get("c"));
        return view;
    }
}