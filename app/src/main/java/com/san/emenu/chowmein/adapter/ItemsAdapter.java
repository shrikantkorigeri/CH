package com.san.emenu.chowmein.adapter;

import android.app.ActionBar;
import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.san.emenu.chowmein.CustomDialog;
import com.san.emenu.chowmein.R;
import com.san.emenu.chowmein.app.AppController;
import com.san.emenu.chowmein.bean.ItemsBean;
import com.san.emenu.chowmein.bean.SubCatBean;
import com.san.emenu.chowmein.utils.GlobalVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SANTECH on 12/10/2015.
 */
public class ItemsAdapter extends BaseAdapter {

    Context mContext;
    List<ItemsBean> itmList = new ArrayList<>();
    List<ItemsBean> ingrtsList = new ArrayList<>();
    DecimalFormat decimvalue = new DecimalFormat("#.00");
    //ListView lv_cart;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    SharedPreferences pref1;
    CustomDialog customdialog, customdialog1;
    String serverUrl;
    TextView txtcartcnt;
    Animation cartanimation;

    public ItemsAdapter(Context mContext, List<ItemsBean> itmList, TextView txtcartcnt) {
        this.mContext = mContext;
        this.itmList = itmList;
        this.txtcartcnt = txtcartcnt;
        // this.lv_cart = lv_cart;
        pref1 = mContext.getSharedPreferences("shpreipaddress", mContext.MODE_PRIVATE);
        // serverUrl = "http://" + pref1.getString("ipaddress", null) + "/chowmein/";
    }

    @Override
    public int getCount() {
        return itmList.size();
    }

    @Override
    public Object getItem(int position) {
        return itmList.get(position);
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
            row = mInflater.inflate(R.layout.itmstxt, parent, false);

            holder = new Holder();
            holder.itm_name = (TextView) row.findViewById(R.id.itm_name);
            holder.itm_price = (TextView) row.findViewById(R.id.itm_price);
            holder.imgplus = (ImageView) row.findViewById(R.id.imgplus);
            holder.ll_nameprice = (LinearLayout) row.findViewById(R.id.ll_nameprice);
            holder.ll_click = (LinearLayout) row.findViewById(R.id.ll_click);

            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "font/Calibri.ttf");
            holder.itm_name.setTypeface(tf, 1);

            row.setTag(holder);
            row.setTag(R.id.itm_name, holder.itm_name);
            row.setTag(R.id.itm_price, holder.itm_price);
            row.setTag(R.id.ll_nameprice, holder.ll_nameprice);
            row.setTag(R.id.ll_click, holder.ll_click);
        } else {
            holder = (Holder) row.getTag();
        }

        // holder.itm_name.setSelected(true);
        holder.itm_name.setText(itmList.get(position).getItemname());
        holder.itm_price.setText("" + decimvalue.format(Double.parseDouble(itmList.get(position).getItemprice())));

        holder.ll_nameprice.setId(position);
        holder.ll_nameprice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ItemsBean itmseleted = itmList.get(position);

                String itmname = itmseleted.getItemname();
                String newitmname = itmname.replace(" ", "_");
                final String url = serverUrl + "image/item/" + newitmname + ".png";

                customdialog = new CustomDialog(mContext);
                customdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                customdialog.setContentView(R.layout.itmdetails);
                WindowManager.LayoutParams a = customdialog.getWindow().getAttributes();
                a.gravity = Gravity.TOP;
                customdialog.getWindow().setAttributes(a);
                customdialog.setCancelable(false);
                customdialog.getWindow().setLayout(android.support.v7.app.ActionBar.LayoutParams.WRAP_CONTENT,
                        android.support.v7.app.ActionBar.LayoutParams.WRAP_CONTENT);

                customdialog.show();

                TextView txtitmnm = (TextView) customdialog.findViewById(R.id.txtitmnm);
                TextView txtitmprice = (TextView) customdialog.findViewById(R.id.txtitmprice);
                final TextView itm_qty = (TextView) customdialog.findViewById(R.id.itm_qty);
                final ImageView itmimgminus = (ImageView) customdialog.findViewById(R.id.itmimgminus);
                // ImageView img_item =  (ImageView) customdialog.findViewById(R.id.img_item);
                ImageView itmimgplus = (ImageView) customdialog.findViewById(R.id.itmimgplus);
                TextView txtpreptime = (TextView) customdialog.findViewById(R.id.txtpreptime);
                final EditText et_splrqst = (EditText) customdialog.findViewById(R.id.et_splrqst);

                Toast.makeText(mContext, "Preparation Time "+itmseleted.getPretime(), Toast.LENGTH_SHORT ).show();
                txtitmnm.setText(itmseleted.getItemname());
                txtitmprice.setText(itmseleted.getItemprice());
                if (itmseleted.getPretime().equals("") || itmseleted.getPretime() == null) {
                    txtpreptime.setText("NA");
                } else {
                    txtpreptime.setText(itmseleted.getPretime());
                }
               /* if (imageLoader == null)
                    imageLoader = AppController.getInstance().getImageLoader();*/
                // NetworkImageView thumbnail = (NetworkImageView) customdialog.findViewById(R.id.thumbnail);
                TextView ingrtxt = (TextView) customdialog.findViewById(R.id.ingrtxt);
                Button btnadd = (Button) customdialog.findViewById(R.id.btnadd);
                Button btncancel = (Button) customdialog.findViewById(R.id.btncancel);
                // img_item.setImageDrawable(itmseleted.getImgdrwbl());
                //thumbnail.setImageUrl(url, imageLoader);

                btnadd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ItemsBean preordbean = new ItemsBean();
                        preordbean.setItemname(itmList.get(position).getItemname());
                        preordbean.setItemprice(itmList.get(position).getItemprice());
                        preordbean.setItemcode(itmList.get(position).getItemcode());
                        preordbean.setIngrdients(et_splrqst.getText().toString().trim());
                        //preordbean.setSubtot(itmList.get(position).getSubtot());
                        preordbean.setQty(Integer.parseInt(itm_qty.getText().toString().trim()));
                        GlobalVariables.preordrList.add(preordbean);

                        int cartcnt = GlobalVariables.preordrList.size();
                        txtcartcnt.setText("" + cartcnt);
                        cartanimation = AnimationUtils.loadAnimation(mContext, R.anim.blink);
                        txtcartcnt.startAnimation(cartanimation);
                        //   lv_cart.setAdapter(new PreorderAdapter(mContext, GlobalVariables.preordrList, lv_cart));
                        customdialog.dismiss();


                    }
                });

                btncancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customdialog.dismiss();
                    }
                });

                itmimgminus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int qty = Integer.parseInt(itm_qty.getText().toString());
                        int chngdqty = 0;
                        //ItemsBean itmbean = preordrList.get(position);
                        if (qty == 1) {
                            //holder.preimgminus.setVisibility(View.INVISIBLE);
                        } else {

                            //addsubstraction(sign,position,qty);
                            chngdqty = qty - 1;

                            itm_qty.setText("" + chngdqty);
                            //holder.preitm_price.setText(""+decimvalue.format(chngprice));

                        }
                    }
                });

                itmimgplus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int qty = Integer.parseInt(itm_qty.getText().toString());
                        int chngdqty = 0;
                        itmimgminus.setVisibility(View.VISIBLE);
                        //  ItemsBean itmbean = preordrList.get(position);
                        chngdqty = qty + 1;


                       /* GlobalVariables.preordrList.get(position).setSubtot(String.valueOf(chngprice));
                        GlobalVariables.preordrList.get(position).setQty(chngdqty);*/

                        itm_qty.setText("" + chngdqty);

                    }
                });


            }
        });
        return row;
    }

    static class Holder {
        //public TextView textView;
        protected TextView itm_name, itm_price;
        protected ImageView imgplus;
        LinearLayout ll_nameprice, ll_click;

    }

    private void showingridentsindialog(List<ItemsBean> ingrtsList, final int position) {
        ItemsBean itmseleted = itmList.get(position);

        String itmname = itmseleted.getItemname();
        String newitmname = itmname.replace(" ", "_");
        String url = serverUrl + "image/item/" + newitmname + ".png";

        customdialog = new CustomDialog(mContext);
        customdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customdialog.setContentView(R.layout.itmdetails);
        WindowManager.LayoutParams a = customdialog.getWindow().getAttributes();
        a.gravity = Gravity.TOP;
        customdialog.getWindow().setAttributes(a);
        customdialog.setCancelable(false);
        customdialog.getWindow().setLayout(android.support.v7.app.ActionBar.LayoutParams.WRAP_CONTENT,
                android.support.v7.app.ActionBar.LayoutParams.WRAP_CONTENT);

        customdialog.show();

        TextView txtitmnm = (TextView) customdialog.findViewById(R.id.txtitmnm);
        TextView txtitmprice = (TextView) customdialog.findViewById(R.id.txtitmprice);
        final TextView itm_qty = (TextView) customdialog.findViewById(R.id.itm_qty);
        final ImageView itmimgminus = (ImageView) customdialog.findViewById(R.id.itmimgminus);
        ImageView itmimgplus = (ImageView) customdialog.findViewById(R.id.itmimgplus);
       /* ListView lv_ingredients = (ListView) customdialog.findViewById(R.id.lv_ingredients);
        lv_ingredients.setAdapter(new IngredientsAdapter(mContext, ingrtsList));*/

        txtitmnm.setText(itmseleted.getItemname());
        txtitmprice.setText(itmseleted.getItemprice());
       /* if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbnail = (NetworkImageView) customdialog.findViewById(R.id.thumbnail);*/
        TextView ingrtxt = (TextView) customdialog.findViewById(R.id.ingrtxt);
        Button btnadd = (Button) customdialog.findViewById(R.id.btnadd);
        Button btncancel = (Button) customdialog.findViewById(R.id.btncancel);
        //  thumbnail.setImageUrl(url, imageLoader);

        String contains = itmseleted.getIngrdients();
        if (contains != null || !contains.isEmpty()) {
            ingrtxt.setText("Information not Available");
        } else {
            ingrtxt.setText(itmseleted.getIngrdients());
        }


        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemsBean preordbean = new ItemsBean();
                preordbean.setItemname(itmList.get(position).getItemname());
                preordbean.setItemprice(itmList.get(position).getItemprice());
                preordbean.setItemcode(itmList.get(position).getItemcode());
                //preordbean.setSubtot(itmList.get(position).getSubtot());
                preordbean.setQty(Integer.parseInt(itm_qty.getText().toString().trim()));
                GlobalVariables.preordrList.add(preordbean);

                //lv_cart.setAdapter(new PreorderAdapter(mContext, GlobalVariables.preordrList, lv_cart));
                customdialog.dismiss();

            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customdialog.dismiss();
            }
        });

        itmimgminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int qty = Integer.parseInt(itm_qty.getText().toString());
                int chngdqty = 0;
                // ItemsBean itmbean = preordrList.get(position);
                if (qty == 1) {
                    // holder.preimgminus.setVisibility(View.INVISIBLE);
                } else {

                    //addsubstraction(sign,position,qty);
                    chngdqty = qty - 1;
                           /* double price = Double.parseDouble(itmbean.getItemprice());
                            double chngprice = price * chngdqty;
                            GlobalVariables.preordrList.get(position).setSubtot(String.valueOf(chngprice));
                            GlobalVariables.preordrList.get(position).setQty(chngdqty);
*/
                    itm_qty.setText("" + chngdqty);
                    //   holder.preitm_price.setText(""+decimvalue.format(chngprice));

                }
            }
        });

        itmimgplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(itm_qty.getText().toString());
                int chngdqty = 0;
                itmimgminus.setVisibility(View.VISIBLE);
                //  ItemsBean itmbean = preordrList.get(position);
                chngdqty = qty + 1;
                //double price = Double.parseDouble(itmbean.getItemprice());
                // double chngprice = price * chngdqty;

                       /* GlobalVariables.preordrList.get(position).setSubtot(String.valueOf(chngprice));
                        GlobalVariables.preordrList.get(position).setQty(chngdqty);*/

                itm_qty.setText("" + chngdqty);
                // holder.preitm_price.setText("" + decimvalue.format(chngprice));
                //  }
            }
        });
    }
}
