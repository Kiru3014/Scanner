package com.nlscan.barcodescannerdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nlscan.barcodescannerdemo.R;
import com.nlscan.barcodescannerdemo.model.ColunmDataitem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by K.Gowda on 2/27/2017.
 */

public class ProjectAdaptor extends BaseAdapter
{
    ViewHolder viewHolder;
    Context context;
    List<ColunmDataitem> employeeList;
    OnItemClickListener mOnItemClickListener;

    public ProjectAdaptor(Context context, ArrayList<ColunmDataitem> employeeList) {
        this.context=context;
        this.employeeList=employeeList;
    }

    @Override
    public int getCount() {
        return employeeList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        mOnItemClickListener = onItemClickListener;
    }



    public interface OnItemClickListener
    {
        void selectPrpject(String projectname);

    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup)
    {
        if(view==null)
        {
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view =layoutInflater.inflate(R.layout.project_item,null);
            viewHolder.projectname = (TextView)view.findViewById(R.id.tv_name);
            view.setTag(viewHolder);


        }
        else
        {
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.projectname.setText(employeeList.get(i).getProjectname());

        viewHolder.projectname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.selectPrpject(employeeList.get(i).getProjectname());
            }
        });
        return view;
    }


    public class ViewHolder
    {
        TextView    projectname;
    }
}
