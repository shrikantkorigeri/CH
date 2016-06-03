package com.san.emenu.chowmein.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.san.emenu.chowmein.R;
import com.san.emenu.chowmein.bean.ItemsBean;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SANTECH on 12/14/2015.
 */
public class OrdersAdapter extends BaseAdapter {

    List<ItemsBean> ordrList = new ArrayList<>();
    Context mContext;
    DecimalFormat decimvalue = new DecimalFormat("#.00");

    public OrdersAdapter(Context mContext, List<ItemsBean> ordrList) {
        this.ordrList = ordrList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return ordrList.size();
    }

    @Override
    public Object getItem(int position) {
        return ordrList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder holder;

        View row = convertView;
        if (row == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            row = mInflater.inflate(R.layout.ordrdtxt, parent, false);
            holder = new Holder();

            holder.orditm_name = (TextView) row.findViewById(R.id.orditm_name);
            holder.ordrditm_price = (TextView) row.findViewById(R.id.ordrditm_price);
            holder.ordrditm_qty = (TextView) row.findViewById(R.id.ordrditm_qty);
            holder.ordritm_subtot = (TextView) row.findViewById(R.id.ordritm_subtot);


            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "font/Calibri.ttf");
            holder.orditm_name.setTypeface(tf, 1);
            holder.ordrditm_price.setTypeface(tf, 1);
            holder.ordrditm_qty.setTypeface(tf, 1);
            holder.ordritm_subtot.setTypeface(tf, 1);

            row.setTag(holder);
            row.setTag(R.id.orditm_name, holder.orditm_name);
            row.setTag(R.id.ordrditm_price, holder.ordrditm_price);
            row.setTag(R.id.ordrditm_qty, holder.ordrditm_qty);
            row.setTag(R.id.ordritm_subtot, holder.ordritm_subtot);

        } else {
            holder = (Holder) row.getTag();
        }
         ItemsBean itmbean = ordrList.get(position);
         holder.orditm_name.setText(itmbean.getItemname());
         holder.ordrditm_price.setText(""+decimvalue.format(Double.parseDouble(itmbean.getItemprice())));
         holder.ordrditm_qty.setText(""+itmbean.getQty());

        int qty = itmbean.getQty();
        String price = itmbean.getItemprice();
        double subtot = qty*Double.parseDouble(price);
         holder.ordritm_subtot.setText(""+decimvalue.format(subtot));

        return row;
    }


    static class Holder {
        TextView orditm_name, ordrditm_price, ordrditm_qty, ordritm_subtot;
    }
}
