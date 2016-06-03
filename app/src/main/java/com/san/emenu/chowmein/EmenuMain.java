package com.san.emenu.chowmein;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.san.emenu.chowmein.utils.GlobalVariables;

import java.util.Date;

public class EmenuMain extends FragmentActivity implements MenuFragment.OnFragmentInteractionListener, View.OnClickListener,
        OrdersFragment.OnFragmentInteractionListener, InvoiceFragment.OnFragmentInteractionListener, ServicesFragment.OnFragmentInteractionListener {

    FragmentManager manager;
    Button btnmenu, btnservice, btnordrs, btninvoice, btnadmin;
    FragmentTransaction transaction;
    TextView txtcurrenttime, txtwiatername, txttblno;
    protected PowerManager.WakeLock mWakeLock;

    CustomDialog custmdlg;

    SharedPreferences pref;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.emenu_main);

        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        btnmenu = (Button) findViewById(R.id.btnmenu);
        btnservice = (Button) findViewById(R.id.btnservice);
        btnordrs = (Button) findViewById(R.id.btnordrs);
        btninvoice = (Button) findViewById(R.id.btninvoice);
        btnadmin = (Button) findViewById(R.id.btnadmin);
        txtcurrenttime = (TextView) findViewById(R.id.txtcurrenttime);
        txtwiatername = (TextView) findViewById(R.id.txtwiatername);
        txttblno = (TextView) findViewById(R.id.txttblno);


        btnmenu.setOnClickListener(this);
        btnservice.setOnClickListener(this);
        btnordrs.setOnClickListener(this);
        btninvoice.setOnClickListener(this);
        btnadmin.setOnClickListener(this);

        pref = getSharedPreferences("tmpvalues", MODE_PRIVATE);

        manager = getSupportFragmentManager();

        btnmenu.setBackgroundResource(R.drawable.menu);

        MenuFragment menuFragment = new MenuFragment();
        transaction = manager.beginTransaction();
        transaction.add(R.id.frgmntgroup, menuFragment, "menufrgmnt");
        transaction.commit();

        Thread myThread = null;

        Runnable myRunnableThread = new CountDownRunner();
        myThread = new Thread(myRunnableThread);
        myThread.start();

        txttblno.setText(pref.getString("tblno", null));
        txtwiatername.setText(pref.getString("username", null));

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnmenu:

                btnmenu.setBackgroundResource(R.drawable.menu);
                btnservice.setBackgroundResource(R.drawable.servicered);
                 btnordrs.setBackgroundResource(R.drawable.videofade);
                btninvoice.setBackgroundResource(R.drawable.invoicered);

                MenuFragment menuFragment = new MenuFragment();
                transaction = manager.beginTransaction();
                transaction.replace(R.id.frgmntgroup, menuFragment, "menufrgmnt");
                transaction.commit();
                break;
            case R.id.btnservice:

                btnmenu.setBackgroundResource(R.drawable.menufade);
                btnservice.setBackgroundResource(R.drawable.servicefade);
                 btnordrs.setBackgroundResource(R.drawable.videofade);
                btninvoice.setBackgroundResource(R.drawable.invoicered);

                ServicesFragment srvcFragment = new ServicesFragment();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.frgmntgroup, srvcFragment, "srvcfrgmnt");
                transaction.commit();

                break;
            case R.id.btnordrs:

                btnmenu.setBackgroundResource(R.drawable.menufade);
                btnservice.setBackgroundResource(R.drawable.servicered);
                btnordrs.setBackgroundResource(R.drawable.video);
                btninvoice.setBackgroundResource(R.drawable.invoicered);

                OrdersFragment ordersFragment = new OrdersFragment();
                transaction = manager.beginTransaction();
                transaction.replace(R.id.frgmntgroup, ordersFragment, "ordrsfrgmnt");
                transaction.commit();
                break;
            case R.id.btninvoice:

                btnmenu.setBackgroundResource(R.drawable.menufade);
                btnservice.setBackgroundResource(R.drawable.servicered);
                 btnordrs.setBackgroundResource(R.drawable.videofade);
                btninvoice.setBackgroundResource(R.drawable.invoicefade);

                InvoiceFragment invcFragment = new InvoiceFragment();
                transaction = manager.beginTransaction();
                transaction.replace(R.id.frgmntgroup, invcFragment, "invcfrgmnt");
                transaction.commit();
                break;

            case R.id.btnadmin:
                tblselection();
                break;

        }
    }

    private void tblselection() {
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
                    GlobalVariables.tblno = null;
                    GlobalVariables.order_id = null;
                    SharedPreferences preferences = getSharedPreferences("tmpvalues", MODE_PRIVATE);
                    preferences.edit().remove("order_id").commit();
                    Intent tblintent = new Intent(EmenuMain.this, TableSelectionACT.class);
                    startActivity(tblintent);
                } else {
                    Toast.makeText(EmenuMain.this, "Please enter correct password...", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "EmenuMain Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.san.emenu.chowmein/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "EmenuMain Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.san.emenu.chowmein/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    class CountDownRunner implements Runnable {
        // @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    doWork();
                    Thread.sleep(1000); // Pause of 1 Second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    public void doWork() {
        runOnUiThread(new Runnable() {
            @SuppressWarnings("deprecation")
            public void run() {
                try {
                    Date dt = new Date();
                    int hours = dt.getHours();
                    int minutes = dt.getMinutes();
                    int seconds = dt.getSeconds();
                    String curTime = hours + ":" + minutes + ":" + seconds;
                    txtcurrenttime.setText(curTime);
                } catch (Exception e) {
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onDestroy() {
        this.mWakeLock.release();
        super.onDestroy();
    }
}
