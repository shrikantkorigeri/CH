package com.san.emenu.chowmein.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.san.emenu.chowmein.CustomDialog;
import com.san.emenu.chowmein.R;
import com.san.emenu.chowmein.app.AppController;
import com.san.emenu.chowmein.bean.ItemsBean;
import com.san.emenu.chowmein.utils.GlobalVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SANTECH on 12/10/2015.
 */
public class PreorderAdapter extends BaseAdapter {

    Context mContext;
    List<ItemsBean> preordrList = new ArrayList<>();
    DecimalFormat decimvalue = new DecimalFormat("#.00");
    ListView lv_cart ;
    CustomDialog customdialog;
    List<ItemsBean> ingrtsList = new ArrayList<>();
    String serverUrl ;
    SharedPreferences pref1;

    TextView txtcartcnt;

    public PreorderAdapter(Context mContext, List<ItemsBean> preordrList, ListView lv_cart,TextView txtcartcnt) {
        this.mContext = mContext;
        this.preordrList = preordrList;
        this.lv_cart = lv_cart;
        this.txtcartcnt = txtcartcnt;

        pref1 = mContext.getSharedPreferences("shpreipaddress", mContext.MODE_PRIVATE);

        serverUrl = "http://"+pref1.getString("ipaddress", null) +"/chowmein/";
    }

    @Override
    public int getCount() {
        return preordrList.size();
    }

    @Override
    public Object getItem(int position) {
        return preordrList.get(position);
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
            row = mInflater.inflate(R.layout.preordrtxt, parent,
                    false);

            holder = new Holder();
            holder.preitm_name = (TextView) row.findViewById(R.id.preitm_name);
            holder.txtremark = (TextView) row.findViewById(R.id.txtremark);
            holder.preitm_qty = (TextView) row.findViewById(R.id.preitm_qty);
            holder.preimgminus = (ImageView) row.findViewById(R.id.preimgminus);
            holder.preimgplus = (ImageView) row.findViewById(R.id.preimgplus);
            holder.preimgdelete = (ImageView) row.findViewById(R.id.preimgdelete);
            holder.ll_extra = (LinearLayout) row.findViewById(R.id.ll_extra);
            holder.rl_preorddecrement = (RelativeLayout) row.findViewById(R.id.rl_preorddecrement);
            holder.rl_preordincrement = (RelativeLayout) row.findViewById(R.id.rl_preordincrement);

            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "font/Calibri.ttf");
            holder.preitm_name.setTypeface(tf, 1);
            holder.txtremark.setTypeface(tf, 1);
            holder.preitm_qty.setTypeface(tf, 1);

            row.setTag(holder);
            row.setTag(R.id.preitm_name, holder.preitm_name);
            row.setTag(R.id.txtremark, holder.txtremark);
            row.setTag(R.id.preitm_qty, holder.preitm_qty);
            row.setTag(R.id.preimgplus, holder.preimgplus);
            row.setTag(R.id.preimgminus, holder.preimgminus);
            row.setTag(R.id.preimgdelete, holder.preimgdelete);
            row.setTag(R.id.ll_extra, holder.ll_extra);
            row.setTag(R.id.rl_preorddecrement, holder.rl_preorddecrement);
            row.setTag(R.id.rl_preordincrement, holder.rl_preordincrement);
        } else {
            holder = (Holder) row.getTag();
        }

        final ItemsBean preordrbean = preordrList.get(position);
        holder.preitm_name.setText(preordrbean.getItemname());
        holder.txtremark.setText(preordrbean.getIngrdients());
      //  holder.preitm_price.setText(""+decimvalue.format(Double.parseDouble(preordrbean.getItemprice())));
        holder.preitm_qty.setText("" + preordrbean.getQty());

        holder.preitm_name.setSelected(true);
        holder.rl_preorddecrement.setId(position);
        holder.rl_preorddecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int qty = Integer.parseInt(holder.preitm_qty.getText().toString());
                int chngdqty = 0;
                ItemsBean itmbean = preordrList.get(position);
                if (qty == 1) {
                    holder.preimgminus.setVisibility(View.INVISIBLE);
                } else {

                    //addsubstraction(sign,position,qty);
                    chngdqty = qty - 1;
                    double price = Double.parseDouble(itmbean.getItemprice());
                    double chngprice = price * chngdqty;
                    GlobalVariables.preordrList.get(position).setSubtot(String.valueOf(chngprice));
                    GlobalVariables.preordrList.get(position).setQty(chngdqty);


                    holder.preitm_qty.setText(""+chngdqty);

                }
            }
        });

        holder.rl_preordincrement.setId(position);
        holder.rl_preordincrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(holder.preitm_qty.getText().toString());
                int chngdqty = 0;
               /* if (qty == 1) {
                    holder.preimgminus.setVisibility(View.VISIBLE);
                } else {*/
                    holder.preimgminus.setVisibility(View.VISIBLE);
                    ItemsBean itmbean = preordrList.get(position);
                    chngdqty = qty + 1;
                    double price = Double.parseDouble(itmbean.getItemprice());
                    double chngprice = price * chngdqty;

                    GlobalVariables.preordrList.get(position).setSubtot(String.valueOf(chngprice));
                    GlobalVariables.preordrList.get(position).setQty(chngdqty);

                    holder.preitm_qty.setText("" + chngdqty);
                   // holder.preitm_price.setText("" + decimvalue.format(chngprice));
              //  }
            }
        });


        holder.preimgdelete.setId(position);
        holder.preimgdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                removeitem(position);

            }
        });

        return row;
    }

    private void showingridentsindialog(List<ItemsBean> ingrtsList) {
        customdialog = new CustomDialog(mContext);
        customdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customdialog.setContentView(R.layout.specialrqssts);
        WindowManager.LayoutParams a = customdialog.getWindow().getAttributes();
        customdialog.getWindow().setAttributes(a);
        customdialog.setCancelable(false);
        customdialog.getWindow().setLayout(android.support.v7.app.ActionBar.LayoutParams.WRAP_CONTENT,
                android.support.v7.app.ActionBar.LayoutParams.WRAP_CONTENT);

        customdialog.show();

        ListView lv_ingredients = (ListView) customdialog.findViewById(R.id.lv_ingredients);
        lv_ingredients.setAdapter(new IngredientsAdapter(mContext, ingrtsList));

        Button btcancel = (Button) customdialog.findViewById(R.id.btcancel);
        Button btnok = (Button) customdialog.findViewById(R.id.btnok);

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customdialog.dismiss();
            }
        });
        btcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            customdialog.dismiss();
            }
        });
    }

    private void removeitem(final int position) {
        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("Remove Item");
        alertDialog.setMessage("Are You Sure Want to Delete Item?");
        alertDialog.setIcon(R.drawable.warning);

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GlobalVariables.preordrList.remove(position);
                if(GlobalVariables.preordrList.size() == 0){
                    Toast.makeText(mContext,"Cart is Empty",Toast.LENGTH_SHORT).show();
                    txtcartcnt.setText("" + GlobalVariables.preordrList.size());
                    lv_cart.setAdapter(null);
                }else {
                    lv_cart.setAdapter(new PreorderAdapter(mContext, GlobalVariables.preordrList,lv_cart,txtcartcnt));
                    txtcartcnt.setText("" + GlobalVariables.preordrList.size());
                }

            }
        });

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //GlobalVariables.preordrList.remove(position);
                alertDialog.dismiss();

            }
        });
        alertDialog.show();
    }

    static class Holder {
        //public TextView textView;
        protected TextView preitm_name,txtremark,preitm_qty;
        protected ImageView preimgminus,preimgplus,preimgdelete;
        protected LinearLayout ll_extra;
        protected RelativeLayout rl_preorddecrement, rl_preordincrement;
    }

    private void addsubstraction(String sign, int pos,int qty ){
       /* int prsrqty = qty;
        double price = Double.parseDouble(preordrList.get(pos).);
        int chngqty = 0;

        if(sign.equals("+")){
            chngqty = prsrqty + 1;

        }else if(sign.equals("-")){
            chngqty = prsrqty - 1;
        }*/
        /*double chngprice = price * chngqty ;
        txtordrcntdtl.setText("" + chngqty);
        prdrditem_subtot.setText("" + decimvalue.format(chngprice));
        preordrList.get(pos).setQty(String.valueOf(chngqty));
        preordrList.get(pos).setSubtot(""+decimvalue.format(chngprice));

       *//* for(int i=0;i< GlobalVariables.itmpordrList.size(); i++){
            totamount = totamount + Integer.parseInt(preordrList.get(i).getQty()) * Double.parseDouble(preordrList.get(i).getPrice());
        }*//*
        txttotamnt.setText(""+decimvalue.format(totamount));
        totamount = 0;*/
    }
}
