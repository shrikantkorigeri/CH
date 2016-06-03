package com.san.emenu.chowmein.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.san.emenu.chowmein.EmenuMain;
import com.san.emenu.chowmein.R;
import com.san.emenu.chowmein.app.AppController;
import com.san.emenu.chowmein.bean.TablesBean;
import com.san.emenu.chowmein.utils.GlobalVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SANTECH on 12/15/2015.
 */
public class TablesAdapter extends BaseAdapter {

    Context mContext;
    List<TablesBean> tblsList = new ArrayList<>();
    String serverUrl;
    SharedPreferences pref1;

    public TablesAdapter(Context mContext, List<TablesBean> tblsList) {
        this.mContext = mContext;
        this.tblsList = tblsList;
    }

    @Override
    public int getCount() {
        return tblsList.size();
    }

    @Override
    public Object getItem(int position) {
        return tblsList.get(position);
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
            row = mInflater.inflate(R.layout.gridtablestxt, parent, false);

            holder = new Holder();
            holder.txt_tblno = (TextView) row.findViewById(R.id.txt_tblno);
            holder.txtFrEng = (TextView) row.findViewById(R.id.txtFrEng);
            holder.ll_gridlayout = (LinearLayout) row.findViewById(R.id.ll_gridlayout);
            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "font/Calibri.ttf");
            holder.txt_tblno.setTypeface(tf, 1);
            holder.txtFrEng.setTypeface(tf, 1);

            row.setTag(holder);
            row.setTag(R.id.txt_tblno, holder.txt_tblno);
            row.setTag(R.id.txtFrEng, holder.txtFrEng);
            row.setTag(R.id.ll_gridlayout, holder.ll_gridlayout);

        } else {
            holder = (Holder) row.getTag();
        }

        final TablesBean tblbean = tblsList.get(position);
        holder.txt_tblno.setText("" + tblbean.getTableno());
        if (tblbean.getTstatus() == 0) {
            holder.txtFrEng.setText("Free");
        } else if (tblbean.getTstatus() == 1) {
            holder.txtFrEng.setText("Occupied");
            holder.ll_gridlayout.setBackgroundResource(R.drawable.gridtbl_selectorred);
            holder.txtFrEng.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.txt_tblno.setTextColor(mContext.getResources().getColor(R.color.white));

        } else if (tblbean.getTstatus() == 2) {
            holder.txtFrEng.setText("Bill");
            holder.ll_gridlayout.setBackgroundResource(R.drawable.gridtbl_selectorylw);
            holder.txtFrEng.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.txt_tblno.setTextColor(mContext.getResources().getColor(R.color.white));

        }

        holder.ll_gridlayout.setId(position);
        holder.ll_gridlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("tmpvalues", mContext.MODE_PRIVATE)
                        .edit().putString("tblno", String.valueOf(tblbean.getTableno())).commit();
                //  GlobalVariables.tblno = String.valueOf(tblbean.getTableno());
                if (holder.txtFrEng.getText().equals("Occupied")) {
                    // getOrderID(GlobalVariables.tblno);
                    getOrderID(String.valueOf(tblbean.getTableno()));
                } else if (holder.txtFrEng.getText().equals("Bill")) {
                    //getOrderID(GlobalVariables.tblno);
                    getOrderID(String.valueOf(tblbean.getTableno()));
                }

                ((Activity) mContext).finish();
                Intent emenuintent = new Intent(mContext, EmenuMain.class);
                mContext.startActivity(emenuintent);

            }
        });
        return row;
    }

    static class Holder {
        TextView txt_tblno, txtFrEng;
        LinearLayout ll_gridlayout;
    }

    private void getOrderID(String tblno) {
        pref1 = mContext.getSharedPreferences("shpreipaddress", mContext.MODE_PRIVATE);
        serverUrl = "http://" + pref1.getString("ipaddress", null) + "/chowmein/";
        String orderidUrl = serverUrl + "getlrgstorderId.php?tblno=" + tblno;
        JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.GET, orderidUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = 0, orderid = 0;
                            try {
                                success = response.getInt("success");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (success == 1) {
                                JSONArray ja = response.getJSONArray("items");
                                for (int i = 0; i < ja.length(); i++) {
                                    JSONObject jobj = ja.getJSONObject(i);
                                    orderid = jobj.getInt("order_id");
                                }
                                // GlobalVariables.order_id = String.valueOf(orderid);
                                mContext.getSharedPreferences("tmpvalues", mContext.MODE_PRIVATE)
                                        .edit().putString("order_id", String.valueOf(orderid)).commit();

                            } else if (success == 0) {
                                mContext.getSharedPreferences("tmpvalues", mContext.MODE_PRIVATE)
                                        .edit().putString("order_id", String.valueOf(0)).commit();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jreq);
    }

}
