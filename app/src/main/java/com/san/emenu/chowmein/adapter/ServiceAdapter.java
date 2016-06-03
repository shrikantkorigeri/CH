package com.san.emenu.chowmein.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.san.emenu.chowmein.R;
import com.san.emenu.chowmein.app.AppController;
import com.san.emenu.chowmein.bean.ServiceBean;
import com.san.emenu.chowmein.utils.GlobalVariables;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SANTECH on 12/17/2015.
 */
public class ServiceAdapter extends BaseAdapter {

    Context mContext;
    List<ServiceBean> serviceList = new ArrayList<>();
    String serverUrl, servicename;

    SharedPreferences pref, pref1;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ServiceAdapter(Context mContext, List<ServiceBean> serviceList) {
        this.mContext = mContext;
        this.serviceList = serviceList;
        pref1 = mContext.getSharedPreferences("shpreipaddress", mContext.MODE_PRIVATE);
        serverUrl = "http://" + pref1.getString("ipaddress", null) + "/chowmein/";
        pref = mContext.getSharedPreferences("tmpvalues", mContext.MODE_PRIVATE);
    }

    @Override
    public int getCount() {
        return serviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return serviceList.get(position);
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
            row = mInflater.inflate(R.layout.gridservices, parent, false);

            holder = new Holder();
            holder.txtsrvc = (TextView) row.findViewById(R.id.txtsrvc);
            holder.ll_service = (LinearLayout) row.findViewById(R.id.ll_service);
            holder.srvcimg = (NetworkImageView) row.findViewById(R.id.thumbnailservice);
            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "font/Calibri.ttf");
            holder.txtsrvc.setTypeface(tf, 1);

            row.setTag(holder);
            row.setTag(R.id.txtsrvc, holder.txtsrvc);
            row.setTag(R.id.ll_service, holder.ll_service);
            row.setTag(R.id.thumbnailservice, holder.srvcimg);

        } else {
            holder = (Holder) row.getTag();
        }
        final ServiceBean srvcbean = serviceList.get(position);
        holder.txtsrvc.setText(srvcbean.getServicename());

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        String srcname = srvcbean.getServicename();
        String newitmname = srcname.replace(" ", "_");
        String url = serverUrl + "image/icons/" + newitmname.toLowerCase() + ".png";
        holder.srvcimg.setImageUrl(url, imageLoader);

        holder.ll_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                servicename = srvcbean.getServicename();
                String msg = "Table No:  " + pref.getString("tblno", null)
                        + " needs "
                        + srvcbean.getServicename() + " @ "
                        + GlobalVariables.getcurrentime() + " @ "
                        + pref.getString("dngarea", null);

                PlaceServiceRqstAsync plcrqst = new PlaceServiceRqstAsync();
                plcrqst.execute(msg);

            }
        });
        return row;
    }

    static class Holder {
        TextView txtsrvc;
        NetworkImageView srvcimg;
        LinearLayout ll_service;
    }

    class PlaceServiceRqstAsync extends AsyncTask<String, Void, String> {

        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(mContext, "Please Wait", "Your Request is Placing...", true, true);
        }

        @Override
        protected String doInBackground(String... params) {

            String msg = params[0];
            String result = "";
            try {

                BufferedReader bufferedReader = null;
                String url = serverUrl + "plc_srvcrqst.php";
                HttpURLConnection conn = GlobalVariables.getconnection(url);
                JSONObject json = new JSONObject();
                json.put("msg", msg);
                json.put("status", "0");
                json.put("username", pref.getString("username", null));
                json.put("tstatus", "0");

                byte[] outputBytes = json.toString().getBytes("UTF-8");
                OutputStream os = conn.getOutputStream();
                os.write(outputBytes);
                os.close();

                bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                result = bufferedReader.readLine();


            } catch (Exception je) {
                je.getStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (loading.isShowing()) {
                loading.dismiss();
            }
            Toast.makeText(mContext, "Service request for " + servicename + " has sent", Toast.LENGTH_SHORT).show();
        }
    }
}
