package com.nlscan.barcodescannerdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nlscan.barcodescannerdemo.R;
import com.nlscan.barcodescannerdemo.model.Record;

import java.util.List;

/**
 * Created by fairoze khazi on 22/02/2017.
 */


public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.CustomerReviewViewHolder> {
    public List<Record> mReviews;
    private Context mContext;
    OnItemClickListener mOnItemClickListener;


    public RecordAdapter(Context context, List<Record> reviews) {
        this.mContext = context;
        this.mReviews = reviews;
    }

    @Override
    public CustomerReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(mContext).
                inflate(R.layout.record_item, viewGroup, false);
        return new CustomerReviewViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(CustomerReviewViewHolder viewHolder, final int position) {
        final Record rec = mReviews.get(position);
        if (rec.getBarcode() != null)
            viewHolder.mBarcode.setText(rec.getBarcode());
        if (rec.getStockSessionName() != null)
            viewHolder.mName.setText(rec.getStockSessionName());
        if (rec.getProd_qty() != null)
            viewHolder.mQty.setText(rec.getProd_qty());
        if (rec.getLocation() != null)
            viewHolder.mlocation.setText(rec.getLocation());
        if (rec.getLotNo() != null)
            viewHolder.mlot.setText(rec.getLotNo());
        if (rec.getExpirydate() != null) {
            viewHolder.mEntrydate.setText(rec.getExpirydate());
        }

        viewHolder.medit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mOnItemClickListener.ItemEdit(rec.getId(),rec.getBarcode(),rec.getStockSessionName(),rec.getProd_qty(),rec.getLotNo(),rec.getExpirydate(),rec.getLocation());
            }
        });

        viewHolder.mdelet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mOnItemClickListener.ItemDelete(rec.getId());
            }
        });

    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        mOnItemClickListener = onItemClickListener;
    }



    public interface OnItemClickListener
    {
       void ItemEdit(String id, String barcode, String session_name, String prod_qty, String prod_lotno, String entrydatetime, String location);

        void ItemDelete(String id);
    }

        @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public class CustomerReviewViewHolder extends RecyclerView.ViewHolder {
        TextView mBarcode, mName, mQty, mlot, mEntrydate, mlocation;
        Button medit,mdelet;

        public CustomerReviewViewHolder(View itemView) {
            super(itemView);
            this.mBarcode = (TextView) itemView.findViewById(R.id.barcodeitem);
            this.mName = (TextView) itemView.findViewById(R.id.sessionname);
            this.mlocation = (TextView) itemView.findViewById(R.id.location);
            this.mQty = (TextView) itemView.findViewById(R.id.prod_qty);
            this.mlot = (TextView) itemView.findViewById(R.id.prod_lot);
            this.mEntrydate = (TextView) itemView.findViewById(R.id.datetime);

            medit =(Button) itemView.findViewById(R.id.editrec);
            mdelet =(Button) itemView.findViewById(R.id.deletrec);

        }
    }
}
