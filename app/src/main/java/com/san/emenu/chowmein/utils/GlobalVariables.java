package com.san.emenu.chowmein.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.san.emenu.chowmein.LoginScreen;
import com.san.emenu.chowmein.R;
import com.san.emenu.chowmein.bean.ItemsBean;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by SANTECH on 12/10/2015.
 */
public class GlobalVariables {
    Context mContext;

    public static String ipaddress;
    public static String url = "http://192.168.0.102/chowmein/";
    //public static String serverUrl = "http://"+ipaddress+"/chowmein/";
    public static String serverUrl = "http://192.168.0.7/chowmein/";
    public static List<ItemsBean> preordrList = new ArrayList<>();
    public static  String username ;
    public static  String password ;

    public static String tblno;
    public static String order_id;
    public static String dngarea ;

    public static String getcurrentdate() {
       /* Calendar c = Calendar.getInstance();
        String currentDate = c.get(Calendar.YEAR) + "-"
                + (c.get(Calendar.MONTH) + 1) + "-"
                + c.get(Calendar.DAY_OF_MONTH);*/

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String currentDate = String.valueOf(dateFormat.format(date));
        return currentDate;
    }

    public static String getcurrentime() {
        Date dt = new Date();
        int hours = dt.getHours();
        int minutes = dt.getMinutes();
        int seconds = dt.getSeconds();
        String curTime = hours + ":" + minutes + ":" + seconds;
        return curTime;
    }

    public static HttpURLConnection getconnection(String url) throws IOException {

        HttpURLConnection httpcon = (HttpURLConnection) ((new URL(url).openConnection()));
        httpcon.setDoOutput(true);
        httpcon.setRequestProperty("Content-Type", "application/json");
        httpcon.setRequestProperty("Accept", "application/json");
        httpcon.setRequestMethod("POST");
        httpcon.connect();

        return httpcon;
    }

    public static void showAlertDialog(final Context context, String title, String message, final Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);
        //alertDialog.setIcon(R.drawable.);
        // Setting alert dialog icon
        //  alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent loginintetn = new Intent(context, LoginScreen.class);
                context.startActivity(loginintetn);
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

}
