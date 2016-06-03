package com.san.emenu.chowmein.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.san.emenu.chowmein.R;
import com.san.emenu.chowmein.app.AppController;
import com.san.emenu.chowmein.bean.CategoryBean;
import com.san.emenu.chowmein.bean.ItemsBean;
import com.san.emenu.chowmein.bean.SubCatBean;
import com.san.emenu.chowmein.localdb.DBUtils;
import com.san.emenu.chowmein.utils.GlobalVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SANTECH on 12/10/2015.
 */
public class SubcatAdapter extends BaseAdapter {

    Context mContext;
    List<SubCatBean> subcatList = new ArrayList<>();
    List<ItemsBean> itmList = new ArrayList<>();
    ListView lv_items,lv_cart ;
    String serverUrl;
    SharedPreferences pref1;
    TextView txtsubmenuname,txtcartcnt;

    public SubcatAdapter(Context mContext, List<SubCatBean> subcatList,ListView lv_items, TextView txtsubmenuname, TextView txtcartcnt) {
        this.mContext = mContext;
        this.subcatList = subcatList;
        //this.lv_cart = lv_cart;
        this.lv_items = lv_items;
        this.txtsubmenuname = txtsubmenuname;
        this.txtcartcnt = txtcartcnt;
        pref1 = mContext.getSharedPreferences("shpreipaddress", mContext.MODE_PRIVATE);
        serverUrl = "http://"+pref1.getString("ipaddress", null) +"/chowmein/";

    }

    @Override
    public int getCount() {
        return subcatList.size();
    }

    @Override
    public Object getItem(int position) {
        return subcatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Holder holder;

        View row = convertView;
        if (row == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            row = mInflater.inflate(R.layout.subcattxt, parent,
                    false);

            holder = new Holder();
            holder.subcat_name = (TextView) row.findViewById(R.id.subcat_name);
            holder.txtcolor = (TextView) row.findViewById(R.id.txtcolor);
            holder.ll_subcatadapter = (LinearLayout) row.findViewById(R.id.ll_subcatadapter);

            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "font/Calibri.ttf");
            holder.subcat_name.setTypeface(tf, 1);

            row.setTag(holder);
            row.setTag(R.id.subcat_name, holder.subcat_name);
            row.setTag(R.id.txtcolor, holder.txtcolor);
            row.setTag(R.id.ll_subcatadapter, holder.ll_subcatadapter);
        } else {
            holder = (Holder) row.getTag();
        }

        if (position % 2 == 1) {
            holder.txtcolor.setBackgroundColor(mContext.getResources().getColor(R.color.pureorange));
        } else {
            holder.txtcolor.setBackgroundColor(mContext.getResources().getColor(R.color.pureylw));
        }
       // holder.subcat_name.setSelected(true);
        holder.subcat_name.setText(subcatList.get(position).getScname());

        holder.ll_subcatadapter.setId(position);
        holder.ll_subcatadapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subcatid = subcatList.get(position).getScatid();

                //Log.d("TAG", " You selected "+subcatid);
                getitems(Integer.parseInt(subcatid));
                txtsubmenuname.setText(subcatList.get(position).getScname());
            }
        });

        return row;
    }
    static class Holder {
        //public TextView textView;
        protected TextView subcat_name,txtcolor;
        protected LinearLayout ll_subcatadapter;

    }

    private void getitems(int subcatid) {
        // PD.show();

        itmList.clear();
        itmList = DBUtils.getitemsfrmdb(String.valueOf(subcatid));
        if(itmList.size() == 0){
            lv_items.setAdapter(null);
            Toast.makeText(mContext, "No Items found...", Toast.LENGTH_SHORT).show();
        }else {
            lv_items.setAdapter(new ItemsAdapter(mContext, itmList, txtcartcnt));
        }
       /* String url = serverUrl +"allitems.php?item="+subcatid;
        JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = 0;
                            try {
                                success = response.getInt("success");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (success == 1) {
                                JSONArray ja = response.getJSONArray("items");
                                for (int i = 0; i < ja.length(); i++) {
                                    ItemsBean itmsbean = new ItemsBean();
                                    JSONObject jobj = ja.getJSONObject(i);
                                    itmsbean.setItemid(Integer.parseInt(jobj.getString("Ritemid")));
                                    itmsbean.setItemname(jobj.getString("Ritem_name"));
                                    itmsbean.setItemprice(jobj.getString("Ritem_price"));
                                    itmsbean.setItemcode(jobj.getString("Ritem_code"));
                                    itmsbean.setIngrdients(jobj.getString("Ritem_des"));
                                    itmList.add(itmsbean);

                                }
                                lv_items.setAdapter(new ItemsAdapter(mContext, itmList,txtcartcnt));
                                // getitems(Integer.parseInt(subcatList.get(0).getSccode()));
                                // PD.dismiss();
                            } else if(success == 0){
                                lv_items.setAdapter(null);
                                Toast.makeText(mContext, "No Items found...", Toast.LENGTH_SHORT).show();
                               // lv_items.setAdapter(null);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                //PD.dismiss();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jreq);*/
    }
}
