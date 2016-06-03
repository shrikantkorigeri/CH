package com.san.emenu.chowmein;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.san.emenu.chowmein.utils.ConnectionDetector;
import com.san.emenu.chowmein.utils.GlobalVariables;

public class LoadingScreen extends AppCompatActivity {

    Boolean isConnected = false;
    ConnectionDetector cd;
    String stripaddress ;

    CustomDialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadingscreen);

        cd = new ConnectionDetector(LoadingScreen.this);
        isConnected = cd.isConnectingToInternet();
        if (!isConnected) {
           // GlobalVariables.showAlertDialog(LoadingScreen.this, "No Wi-Fi conncetion", "You don't have Wi-Fi connection", false);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    LoadingScreen.this);

            alertDialog.setTitle("No Wi-Fi conncetion");
            alertDialog
                    .setMessage("You don't have Wi-Fi connection");
            alertDialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                           // isConnected = cd.isConnectingToInternet();
                            dialog.cancel();
                            Intent reLdng = new Intent(LoadingScreen.this, LoadingScreen.class);
                            startActivity(reLdng);

                        }
                    });

            alertDialog.show();
        } else {
            SharedPreferences pref = getSharedPreferences("shpreipaddress", MODE_PRIVATE);
            stripaddress = pref.getString("ipaddress", null);
            if(stripaddress == null || stripaddress.equals("")){
                ipaddresssetting();
            }else {
                GlobalVariables.ipaddress = stripaddress;
                Intent loginintent = new Intent(LoadingScreen.this, LoginScreen.class);
                startActivity(loginintent);
            }

        }
    }

    private void ipaddresssetting() {
        customDialog = new CustomDialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(R.layout.ipsetting);
        WindowManager.LayoutParams a = customDialog.getWindow().getAttributes();
        customDialog.getWindow().setAttributes(a);
        customDialog.setCancelable(true);
        customDialog.getWindow().setLayout(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        customDialog.show();

        final EditText et_ipaddress = (EditText) customDialog.findViewById(R.id.et_ipaddress);
        Button btnok = (Button) customDialog.findViewById(R.id.btnok);
        Button btncancel = (Button) customDialog.findViewById(R.id.btncancel);

        if(stripaddress == null || stripaddress.equals("")){

        }else {
            et_ipaddress.setText(stripaddress);
            GlobalVariables.ipaddress = stripaddress ;
        }

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stripaddress = et_ipaddress.getText().toString();
                GlobalVariables.ipaddress = stripaddress;
                rememberme(stripaddress);
                Intent loginintent = new Intent(LoadingScreen.this, LoginScreen.class);
                startActivity(loginintent);
                customDialog.dismiss();
            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });
    }

    private void rememberme(String stripaddress) {
        getSharedPreferences("shpreipaddress", MODE_PRIVATE)
                .edit()
                .putString("ipaddress",stripaddress)
                .commit();
    }

}
