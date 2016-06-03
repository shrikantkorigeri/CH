package com.san.emenu.chowmein.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
public class CategoryAdapter extends BaseAdapter {

    Context mContext;
    List<CategoryBean> catList = new ArrayList<>();
    ListView lv_subcat,lv_items,lv_cart ;
    List<SubCatBean> subcatList = new ArrayList<>();
    List<ItemsBean> itmList = new ArrayList<>();
    SharedPreferences pref1;

    String serverUrl;
    TextView txtmainmenuname, txtsubmenuname,txtcartcnt;

    public CategoryAdapter(Context mContext, List<CategoryBean> catList,ListView lv_subcat,ListView lv_items,TextView txtmainmenuname,TextView txtsubmenuname, TextView txtcartcnt) {
        this.mContext = mContext;
        this.catList = catList;
        this.lv_subcat = lv_subcat;
        this.lv_items = lv_items;
        this.txtcartcnt = txtcartcnt;
      //  this.lv_cart = lv_cart;
        this.txtmainmenuname = txtmainmenuname;
        this.txtsubmenuname = txtsubmenuname;
        pref1 = mContext.getSharedPreferences("shpreipaddress", mContext.MODE_PRIVATE);
        serverUrl = "http://"+pref1.getString("ipaddress", null) +"/chowmein/";
    }

    @Override
    public int getCount() {
        return catList.size();
    }

    @Override
    public Object getItem(int position) {
        return catList.get(position);
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
            row = mInflater.inflate(R.layout.cattxt, parent,
                    false);

            holder = new Holder();
            holder.ll_categoryadapter = (LinearLayout) row.findViewById(R.id.ll_categoryadapter);
            holder.cat_name = (TextView) row.findViewById(R.id.cat_name);

            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "font/Calibri.ttf");
            holder.cat_name.setTypeface(tf, 1);

            row.setTag(holder);
            row.setTag(R.id.cat_name, holder.cat_name);
            row.setTag(R.id.ll_categoryadapter, holder.ll_categoryadapter);

        } else {
            holder = (Holder) row.getTag();
        }

        holder.cat_name.setSelected(true);
        holder.cat_name.setText(catList.get(position).getRcatname());
        holder.ll_categoryadapter.setId(position);
        holder.ll_categoryadapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // holder.cat_name.setId(position);
               // holder.cat_name.setTextColor(mContext.getResources().getColor(R.color.lightgreen));
               // holder.cat_name.setTextColor(mContext.getResources(R.drawable.changetxt));
                String catid = catList.get(position).getRcatid();
                getsubcategories(Integer.parseInt(catid));
                txtmainmenuname.setText(catList.get(position).getRcatname());
                v.setSelected(true);

            }
        });
        return row;
    }
    static class Holder {
        //public TextView textView;
        protected TextView cat_name ;
        protected LinearLayout ll_categoryadapter;

    }

    private void getsubcategories(int catid) {

        subcatList.clear();
        String subcatid =null;
        subcatList = DBUtils.getsubcatfrmdb(String.valueOf(catid));
        if(subcatList.size() == 0){
            lv_subcat.setAdapter(null);

            Toast.makeText(mContext, "No Sub Menu found...", Toast.LENGTH_SHORT).show();
        }else {
            subcatid = subcatList.get(0).getScatid();
            lv_subcat.setAdapter(new SubcatAdapter(mContext, subcatList, lv_items, txtsubmenuname, txtcartcnt));
        }

        itmList.clear();
        itmList = DBUtils.getitemsfrmdb(subcatid);
        txtsubmenuname.setText(subcatList.get(0).getScname());
        if (itmList.size() == 0) {
            lv_items.setAdapter(null);
            Toast.makeText(mContext, "No Items found...", Toast.LENGTH_SHORT).show();
        } else {
            lv_items.setAdapter(new ItemsAdapter(mContext, itmList, txtcartcnt));
        }
       // PD.show();
       /* String url = serverUrl +"allsubcategory.php?item="+catid;
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
                                    SubCatBean subcatbean = new SubCatBean();
                                    JSONObject jobj = ja.getJSONObject(i);
                                    subcatbean.setSccode(jobj.getString("rscatid"));
                                    subcatbean.setScname(jobj.getString("rscatname"));
                                    subcatbean.setRcid(jobj.getString("rcid"));
                                    // catbean.setRcatstatus(jobj.getInt("Ritem_price"));
                                    subcatList.add(subcatbean);

                                }
                                lv_subcat.setAdapter(new SubcatAdapter(mContext, subcatList, lv_items,txtsubmenuname,txtcartcnt));
                                getitems(Integer.parseInt(subcatList.get(0).getSccode()));
                                txtsubmenuname.setText(subcatList.get(0).getScname());
                                //PD.dismiss();
                            } else if(success == 0){
                                Toast.makeText(mContext, "No Items found...", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
               // PD.dismiss();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jreq);*/
    }

    private void getitems(int subcatid) {
       // PD.show();

        itmList.clear();
        String url = serverUrl +"allitems.php?item="+subcatid;
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
                                    itmsbean.setItemid(jobj.getString("Ritemid"));
                                    itmsbean.setItemname(jobj.getString("Ritem_name"));
                                    itmsbean.setItemprice(jobj.getString("Ritem_price"));
                                    itmsbean.setItemcode(jobj.getString("Ritem_code"));
                                    // catbean.setRcatstatus(jobj.getInt("Ritem_price"));
                                    itmList.add(itmsbean);

                                }
                                lv_items.setAdapter(new ItemsAdapter(mContext, itmList,txtcartcnt));
                                // getitems(Integer.parseInt(subcatList.get(0).getSccode()));
                               // PD.dismiss();
                            } else if(success == 0){
                                lv_items.setAdapter(null);
                                Toast.makeText(mContext, "No Items found...", Toast.LENGTH_SHORT).show();
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
        AppController.getInstance().addToRequestQueue(jreq);
    }

}
