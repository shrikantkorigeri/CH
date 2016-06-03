package com.san.emenu.chowmein;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.san.emenu.chowmein.adapter.TablesAdapter;
import com.san.emenu.chowmein.app.AppController;
import com.san.emenu.chowmein.bean.TablesBean;
import com.san.emenu.chowmein.utils.GlobalVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TableSelectionACT extends AppCompatActivity implements View.OnClickListener {

    GridView gv_tables;
    String serverUrl;
    ProgressDialog PD;
    Button btnlogout;
    CustomDialog custmdlg;
    WifiManager mWifiManager;
    WifiInfo wifiInfo;
    Handler handler = new Handler();
    Runnable runnable;
    Timer timer;
    TimerTask timerTask;

    String macaddress;
    List<TablesBean> tblsList = new ArrayList<>();
    SharedPreferences pref,pref1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_tableselection_act);
        pref1 = getSharedPreferences("shpreipaddress", MODE_PRIVATE);
        serverUrl = "http://" + pref1.getString("ipaddress", null) + "/chowmein/";
        initializations();

        mWifiManager = (WifiManager) this
                .getSystemService(Context.WIFI_SERVICE);
        wifiInfo = mWifiManager.getConnectionInfo();
        macaddress = wifiInfo.getMacAddress();

        btnlogout.setOnClickListener(this);

        SharedPreferences preferences = getSharedPreferences("tmpvalues", MODE_PRIVATE);
        preferences.edit().remove("order_id").commit();

        gettablesdetails();

    }

    private void gettablesdetails() {

        PD = new ProgressDialog(this);
        PD.setMessage("Loading.....");
        PD.setCancelable(false);
        PD.show();

        gv_tables.setAdapter(null);

        pref  = getSharedPreferences("tmpvalues", MODE_PRIVATE);
        String tblsUrl = serverUrl + "getalltablesdtls.php?area=" + pref.getString("dngarea", null);
        JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.GET, tblsUrl, null,
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
                                    TablesBean tblbean = new TablesBean();
                                    JSONObject jobj = ja.getJSONObject(i);
                                    tblbean.setId(jobj.getInt("id"));
                                    tblbean.setTableno(jobj.getInt("tableno"));
                                    tblbean.setTstatus(jobj.getInt("tstatus"));
                                    tblbean.setSeats(jobj.getInt("seats"));

                                    tblsList.add(tblbean);

                                }
                                PD.dismiss();
                                gv_tables.setAdapter(new TablesAdapter(TableSelectionACT.this, tblsList));
                                double total = 0;

                            } else if (success == 0) {
                                PD.dismiss();
                                Toast.makeText(TableSelectionACT.this, "No Items found...", Toast.LENGTH_SHORT).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
               // Toast.makeText(TableSelectionACT.this, "OOPPPSSSSSSSS.... Table Selection...", Toast.LENGTH_SHORT).show();
                PD.dismiss();
                showAlertDialog("Something went wrong", "Please Try once again...", false);
               // gettablesdetails();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jreq);
    }

    private void initializations() {
        gv_tables = (GridView) findViewById(R.id.gv_tables);
        btnlogout = (Button) findViewById(R.id.btnlogout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnlogout:
                logout();
                break;
        }
    }

    private void logout() {
        custmdlg = new CustomDialog(this);
        custmdlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        custmdlg.setContentView(R.layout.adminlogout);
        WindowManager.LayoutParams a = custmdlg.getWindow().getAttributes();
        custmdlg.getWindow().setAttributes(a);
        custmdlg.setCancelable(false);
        custmdlg.getWindow().setLayout(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);

        custmdlg.show();

        final EditText et_waiterpass = (EditText) custmdlg.findViewById(R.id.et_waiterpass);

        custmdlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        Button btnadminlogout = (Button) custmdlg.findViewById(R.id.btnadminlogout);
        Button btnadmincancel = (Button) custmdlg.findViewById(R.id.btnadmincancel);

        btnadminlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String etpassword = et_waiterpass.getText().toString().trim();
                if (etpassword.equalsIgnoreCase(pref.getString("password", null))) {

                   /* GlobalVariables.tblno = null;
                    GlobalVariables.dngarea = null;
                    GlobalVariables.username = null;
                    GlobalVariables.order_id = null;
                    GlobalVariables.password = null;*/

                    changeloutsts();

                    Intent loginintent = new Intent(TableSelectionACT.this, LoginScreen.class);
                    startActivity(loginintent);

                } else {
                    Toast.makeText(TableSelectionACT.this, "Please enter correct password...", Toast.LENGTH_SHORT).show();
                }

                custmdlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            }
        });

        btnadmincancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                custmdlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                custmdlg.dismiss();
            }
        });
    }

    private void changeloutsts() {
        String url = serverUrl + "updtLogoutsts.php?macaddress=" + macaddress;
        JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        int success = 0;
                        try {
                            success = response.getInt("success");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (success == 1) {

                            PD.dismiss();
                        } else if (success == 0) {
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                PD.dismiss();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jreq);
    }

    @Override
    protected void onPause() {
        super.onPause();
      //  handler.removeCallbacks(runnable);
       /* timer.cancel();
        timer.purge();*/
    }

    @Override
    protected void onStop() {
        super.onStop();
       /* timer.cancel();
        timer.purge();*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // handler.removeCallbacks(runnable);
      /*  timer.cancel();
        timer.purge();*/
    }
    private void showAlertDialog(String title, String message, final Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(TableSelectionACT.this).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);
        //alertDialog.setIcon(R.drawable.);
        // Setting alert dialog icon
        //  alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

        // Setting OK Button
        alertDialog.setButton("CONTINUE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
               /* Intent loginintetn = new Intent(TableSelectionACT.this, LoginScreen.class);
                startActivity(loginintetn);*/
                gettablesdetails();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}
