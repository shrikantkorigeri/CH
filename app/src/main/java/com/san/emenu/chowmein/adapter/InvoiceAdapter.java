package com.san.emenu.chowmein.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.san.emenu.chowmein.R;
import com.san.emenu.chowmein.bean.ItemsBean;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SANTECH on 12/15/2015.
 */


public class InvoiceAdapter extends BaseAdapter {

    List<ItemsBean> itminvcList = new ArrayList<>();
    Context mContext;
    DecimalFormat decimvalue = new DecimalFormat("#.00");

    public InvoiceAdapter(Context mContext, List<ItemsBean> itminvcList) {
        this.itminvcList = itminvcList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return itminvcList.size();
    }

    @Override
    public Object getItem(int position) {
        return itminvcList.get(position);
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
            row = mInflater.inflate(R.layout.invoicetxt, parent, false);
            holder = new Holder();

            holder.invcitm_name = (TextView) row.findViewById(R.id.invcitm_name);
            holder.invcitm_price = (TextView) row.findViewById(R.id.invcitm_price);
            holder.invcitm_qty = (TextView) row.findViewById(R.id.invcitm_qty);
            holder.invcitm_subtot = (TextView) row.findViewById(R.id.invcitm_subtot);


            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "font/Calibri.ttf");
            holder.invcitm_name.setTypeface(tf, 1);
            holder.invcitm_price.setTypeface(tf, 1);
            holder.invcitm_qty.setTypeface(tf, 1);
            holder.invcitm_subtot.setTypeface(tf, 1);

            row.setTag(holder);
            row.setTag(R.id.invcitm_name, holder.invcitm_name);
            row.setTag(R.id.ordrditm_price, holder.invcitm_price);
            row.setTag(R.id.ordrditm_qty, holder.invcitm_qty);
            row.setTag(R.id.ordritm_subtot, holder.invcitm_subtot);

        } else {
            holder = (Holder) row.getTag();
        }

        ItemsBean itmbean = itminvcList.get(position);
        holder.invcitm_name.setText(itmbean.getItemname());
        holder.invcitm_price.setText(""+decimvalue.format(Double.parseDouble(itmbean.getItemprice())));
        holder.invcitm_qty.setText(""+itmbean.getQty());
        int qty = itmbean.getQty();
        String price = itmbean.getItemprice();
        double subtot = qty*Double.parseDouble(price);
        holder.invcitm_subtot.setText("" +decimvalue.format(subtot));
        return row;
    }

    static class Holder {
        TextView invcitm_name, invcitm_price, invcitm_qty, invcitm_subtot;
    }
}
