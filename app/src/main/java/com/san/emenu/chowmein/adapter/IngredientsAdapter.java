package com.san.emenu.chowmein.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.san.emenu.chowmein.R;
import com.san.emenu.chowmein.bean.ItemsBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SANTECH on 12/14/2015.
 */
public class IngredientsAdapter extends BaseAdapter {

    List<ItemsBean> ingrList = new ArrayList<>();
    Context mContext;

    public IngredientsAdapter(Context mContext,List<ItemsBean> ingrList) {
        this.ingrList = ingrList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return ingrList.size();
    }

    @Override
    public Object getItem(int position) {
        return ingrList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder holder;

        View row = convertView;
       // if (row == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            row = mInflater.inflate(R.layout.ingrtxt, parent,
                    false);

            holder = new Holder();
            holder.cb_ingr = (CheckBox) row.findViewById(R.id.cb_ingr);

            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "font/Calibri.ttf");
            holder.cb_ingr.setTypeface(tf, 1);

            row.setTag(holder);
            row.setTag(R.id.cb_ingr, holder.cb_ingr);
       /* } else {
            holder = (Holder) row.getTag();
        }*/

        holder.cb_ingr.setText(ingrList.get(position).getIngrdients());

        return row;
    }
    static class Holder {
        //public TextView textView;
        CheckBox cb_ingr;
    }
}
