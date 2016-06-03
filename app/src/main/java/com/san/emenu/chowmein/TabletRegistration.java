package com.san.emenu.chowmein;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.san.emenu.chowmein.localdb.SQLiteDBHandler;
import com.san.emenu.chowmein.utils.GlobalVariables;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class TabletRegistration extends Activity implements View.OnClickListener {

    TextView txtmodel, txtbrand, txtserialno, txtmacadd;
    Button btnregister;

    WifiManager mWifiManager;
    WifiInfo wifiInfo;
    String serverUrl;
    File  emenu;

    String model, brand, serialno, macaddress;
    protected PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabletregistration);
        initialization();


        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();
        serverUrl = "http://"+GlobalVariables.ipaddress+"/chowmein/";
        mWifiManager = (WifiManager) this
                .getSystemService(Context.WIFI_SERVICE);
        wifiInfo = mWifiManager.getConnectionInfo();

        System.getProperty("os.version");
        model = android.os.Build.MODEL;
        brand = android.os.Build.BRAND;
        serialno = android.os.Build.SERIAL;
        macaddress = wifiInfo.getMacAddress();

        txtmodel.setText(model);
        txtbrand.setText(brand);
        txtserialno.setText(serialno);
        txtmacadd.setText(macaddress);

        btnregister.setOnClickListener(this);
    }

    private void initialization() {
        txtmodel = (TextView) findViewById(R.id.txtmodel);
        txtbrand = (TextView) findViewById(R.id.txtbrand);
        txtserialno = (TextView) findViewById(R.id.txtserialno);
        txtmacadd = (TextView) findViewById(R.id.txtmacadd);
        btnregister = (Button) findViewById(R.id.btnregister);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnregister:
                registertab();
                break;
        }
    }
    private void registertab() {

        emenu = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/sanemenuch/emenu.db");
        if (emenu.exists()) {
          //  Toast.makeText(this,"Emenu existed", Toast.LENGTH_SHORT).show();
           this.deleteDatabase(Environment.getExternalStorageDirectory()
                   .getAbsolutePath() + "/sanemenuch/emenu.db");
        }
        RegistrationAsync regtab = new RegistrationAsync();
        regtab.execute();
    }

    class RegistrationAsync extends AsyncTask<String, Void, String> {
        String result;
        ProgressDialog loading;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(TabletRegistration.this, "Please Wait", "Tab is Registering...", true, true);
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                BufferedReader bufferedReader = null;
                String url = serverUrl + "tabletregister.php";
                HttpURLConnection conn = GlobalVariables.getconnection(url);
                JSONObject json = new JSONObject();
                json.put("tno", "0");
                json.put("type", "Customer Type");
                json.put("dbuser", "dbuser");
                json.put("dbpass", "dbpass");
                json.put("device_sn", serialno);
                json.put("device_model", model);
                json.put("device_brand", brand);
                json.put("device_status", "0");
                json.put("sts", "2");
                json.put("macaddress", macaddress);
                json.put("login_status", "0");

                byte[] outputBytes = json.toString().getBytes("UTF-8");
                OutputStream os = conn.getOutputStream();
                os.write(outputBytes);
                os.close();

                bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                result = bufferedReader.readLine();

            } catch (IOException ie) {
                ie.getStackTrace();
            } catch (Exception je) {
                je.getStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(TabletRegistration.this, " Registered Successfully", Toast.LENGTH_SHORT).show();
            Intent loginintent = new Intent(TabletRegistration.this, LoginScreen.class);
            startActivity(loginintent);

        }
    }
    @Override
    public void onDestroy() {
        this.mWakeLock.release();
        super.onDestroy();
    }
}
