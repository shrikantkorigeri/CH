package com.san.emenu.chowmein;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.san.emenu.chowmein.app.AppController;
import com.san.emenu.chowmein.bean.CategoryBean;
import com.san.emenu.chowmein.bean.IngredeintsBean;
import com.san.emenu.chowmein.bean.ItemsBean;
import com.san.emenu.chowmein.bean.SubCatBean;
import com.san.emenu.chowmein.localdb.DBUtils;
import com.san.emenu.chowmein.localdb.SQLiteDBHandler;
import com.san.emenu.chowmein.utils.GlobalVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LoginScreen extends Activity implements View.OnClickListener {

    TextView txtipaddress, txtreset;
    Button btnlogin;
    EditText et_username, et_password;
    CheckBox chkShowPswd;
    String ipaddress, cattblsts, subcattblsts, itemtblsts;
    CustomDialog customDialog;
    String dbdate, macaddress;
    int lcnccnt = 0, usingtabcnt = 0;
    int cntSQLRestCat, cntSQLRestitm,
            cntSqlRstItm, cntSQLRessubtCat;

    WifiManager mWifiManager;
    WifiInfo wifiInfo;
    List<CategoryBean> catList = new ArrayList<>();
    List<SubCatBean> subcatList = new ArrayList<>();
    List<ItemsBean> itmsList = new ArrayList<>();
    List<IngredeintsBean> ingrdntsList = new ArrayList<>();

    List<CategoryBean> tmpcatStsList = new ArrayList<>();
    List<SubCatBean> tmprestsubcatList = new ArrayList<>();
    List<ItemsBean> tmprestitmList = new ArrayList<>();

    List<CategoryBean> sqlRestCatList = new ArrayList<>();
    List<SubCatBean> sqlRstSubCat = new ArrayList<>();
    List<ItemsBean> sqlItemList = new ArrayList<>();

    File sanemenuch, emenu;
    ProgressDialog PD;
    protected PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();

        SharedPreferences pref = getSharedPreferences("shpreipaddress", MODE_PRIVATE);
        ipaddress = pref.getString("ipaddress", null);

        initialization();

        mWifiManager = (WifiManager) this
                .getSystemService(Context.WIFI_SERVICE);
        wifiInfo = mWifiManager.getConnectionInfo();
        macaddress = wifiInfo.getMacAddress();
        txtipaddress.setOnClickListener(this);
        txtreset.setOnClickListener(this);
        btnlogin.setOnClickListener(this);

        if (ipaddress == null || ipaddress.equals("")) {
            ipaddresssetting();
        } else {
            GlobalVariables.ipaddress = ipaddress;
            getmaxdatefromdb();
        }

        sanemenuch = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/sanemenuch");

        if (sanemenuch.exists() == true) {
            emenu = new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/sanemenuch/emenu.db");
            if (emenu.exists()) {
                ;
            } else {
                new SQLiteDBHandler();
                //  DALServerData.deletetab(macaddress);
            }
        } else {
            sanemenuch.mkdir();
            new SQLiteDBHandler();
            //DALServerData.deletetab(macaddress);
        }

       /* catList = DBUtils.getcategory();

        for(int i=0; i<catList.size(); i++){
            Toast.makeText(this, "Item : " +catList.get(i).getRcatname(), Toast.LENGTH_SHORT).show();
        }*/
        chkShowPswd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked) {
                if (ischecked) {
                    et_password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    et_password.setInputType(129);
                }
            }
        });
    }

    private void initialization() {
        txtipaddress = (TextView) findViewById(R.id.txtipaddress);
        btnlogin = (Button) findViewById(R.id.btnlogin);
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        chkShowPswd = (CheckBox) findViewById(R.id.chkShowPswd);
        txtreset = (TextView) findViewById(R.id.txtreset);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtipaddress:
                ipaddresssetting();
                break;
            case R.id.btnlogin:
                login();
                break;
            case R.id.txtreset:
                resetbutton();
                break;
        }
    }

    private void resetbutton() {
        String url = "http://" + ipaddress + "/chowmein/updtLogoutsts.php?macaddress=" + macaddress;
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
                            Toast.makeText(LoginScreen.this, "Reseted Successfully...", Toast.LENGTH_SHORT).show();
                            // PD.dismiss();
                        } else if (success == 0) {
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

    private void login() {
        String username = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        if (username.equals("") || password.equals("")) {
            Toast.makeText(LoginScreen.this, "Please Provide credentials...", Toast.LENGTH_SHORT).show();
        } else {
            doverifyuserNmaxtab();
        }
        /*Intent tblintetnt = new Intent(this, TableSelectionACT.class);
        startActivity(tblintetnt);*/
    }

    private void doverifyuserNmaxtab() {
        getusingtabcount();

    }

    private void getusingtabcount() {
        PD = new ProgressDialog(LoginScreen.this);
        PD.setMessage("Getting.....");
        PD.setCancelable(false);
        PD.show();
        String url = "http://" + GlobalVariables.ipaddress + "/chowmein/tabcount.php";
        //JsonObjectRequest jrq = new JsonObjectRequest(url);
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
                                JSONObject jobj = ja.getJSONObject(0);
                                usingtabcnt = jobj.getInt("tabs");

                                PD.dismiss();

                            } else if (success == 0) {
                                Toast.makeText(LoginScreen.this, "No Items found...", Toast.LENGTH_SHORT).show();
                                PD.dismiss();
                            }

                            gettottablicenses();

                        } catch (JSONException e) {
                            e.printStackTrace();
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

    private void gettottablicenses() {
        final int tottablcns = 0;
        PD = new ProgressDialog(LoginScreen.this);
        PD.setMessage("Getting.....");
        PD.setCancelable(false);
        PD.show();
        String url = "http://" + GlobalVariables.ipaddress + "/chowmein/licensetabs.php";
        //JsonObjectRequest jrq = new JsonObjectRequest(url);
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

                                JSONObject jobj = ja.getJSONObject(0);
                                lcnccnt = jobj.getInt("itemc");
                                PD.dismiss();

                            } else if (success == 0) {
                                Toast.makeText(LoginScreen.this, "No Items found...", Toast.LENGTH_SHORT).show();
                                PD.dismiss();
                            }

                            if (lcnccnt < usingtabcnt || lcnccnt == usingtabcnt) {
                                // Toast.makeText(LoginScreen.this, " License :" +lcnccnt +" Using : "+usingtabcnt, Toast.LENGTH_SHORT).show();
                                alertbox("Max Tab Reached", "You have only " + lcnccnt + " license");
                            } else {
                                docheckuser();
                            }
                            //Toast.makeText(LoginScreen.this, " License :" +lcnccnt +" Using : "+usingtabcnt, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
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

    private void docheckuser() {
        String username = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        String usercheckUrl = "http://" + ipaddress + "/chowmein/usercredentials.php?username=" + username + "&password=" + password;
        JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.GET, usercheckUrl, null,
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

                                JSONObject jobj = ja.getJSONObject(0);
                               /* GlobalVariables.dngarea =  jobj.getString("area");
                                GlobalVariables.username = et_username.getText().toString().trim();
                                GlobalVariables.password = et_password.getText().toString().trim();*/

                                getSharedPreferences("tmpvalues", MODE_PRIVATE)
                                        .edit()
                                        .putString("dngarea", jobj.getString("area"))
                                        .putString("username", et_username.getText().toString().trim())
                                        .putString("password", et_password.getText().toString().trim())
                                        .commit();

                                changeloginstatus();

                                gettabstatus();

                               // LoginScreen.this.finish();
                               // PD.dismiss();

                            } else if (success == 0) {
                                //Toast.makeText(LoginScreen.this, "No user found.", Toast.LENGTH_SHORT).show();
                                alertbox("User not exist", "Please check your Credentials");
                                PD.dismiss();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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

    private void gettabstatus() {

        String usercheckUrl = "http://" + ipaddress + "/chowmein/gettabstatus.php?macaddress=" + macaddress;
        JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.GET, usercheckUrl, null,
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

                                JSONObject jobj = ja.getJSONObject(0);
                               /* GlobalVariables.dngarea =  jobj.getString("area");
                                GlobalVariables.username = et_username.getText().toString().trim();
                                GlobalVariables.password = et_password.getText().toString().trim();*/

                                //  storeall();
                                String tabsts = jobj.getString("sts");
                                if (Integer.parseInt(tabsts) == 2) {
                                    storeall();
                                    changetabsts(1);
                                } else if (Integer.parseInt(tabsts) == 1) {
                                    // Toast.makeText(LoginScreen.this, "Tablet Status 1",Toast.LENGTH_SHORT).show();
                                    //  categorystatuscheck();
                                    allstatuscheck();
                                    changetabsts2();
                                    gettottabcntsts();
                                } else {
                                    checklocalcatdb();
                                    checklocalsubcatdb();
                                    checklocalitemsdb();
                                    Intent menuintent = new Intent(LoginScreen.this, TableSelectionACT.class);
                                    startActivity(menuintent);
                                }
                        //        PD.dismiss();

                            } else if (success == 0) {
                                //Toast.makeText(LoginScreen.this, "No user found.", Toast.LENGTH_SHORT).show();
                                alertbox("User not exist", "Please check your Credentials");
                                PD.dismiss();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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

    private void gettottabcntsts() {
        String url = "http://" + ipaddress + "/chowmein/gettottabsstsone.php";
        //JsonObjectRequest jrq = new JsonObjectRequest(url);
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
                                JSONObject jobj = ja.getJSONObject(0);

                                String strtabcnt = jobj.getString("tabcnt");
                                int inttabcnt = Integer.parseInt(strtabcnt);

                                if(inttabcnt > 0){
                                    mainmenu();
                                }else{
                                    changeallstatus();
                                    mainmenu();
                                }

                            } else if (success == 0) {
                                Toast.makeText(LoginScreen.this, "No Items found...", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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

    private void mainmenu() {
        Intent mainintent = new Intent(LoginScreen.this, TableSelectionACT.class);
        startActivity(mainintent);
    }

    private void changeallstatus() {
        //Toast.makeText(LoginScreen.this, "Inside All status ", Toast.LENGTH_SHORT).show();

        String url = "http://" + ipaddress + "/chowmein/changeallstatus.php";
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

                           // PD.dismiss();
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

    private void changetabsts(int val) {
        if(val == 1){
            mainmenu();
        }else {
            String url = "http://" + GlobalVariables.ipaddress + "/chowmein/updtLoginsts.php?macaddress=" + macaddress + "&param=2";
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

                                // PD.dismiss();
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
    }

    private void changetabsts2() {
            String url = "http://" + GlobalVariables.ipaddress + "/chowmein/updtLoginsts.php?macaddress=" + macaddress + "&param=2";
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

                                // PD.dismiss();
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


    private void storeall() {
        String url = "http://" + ipaddress + "/chowmein/allcategory.php?param=1";
        //JsonObjectRequest jrq = new JsonObjectRequest(url);
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
                                    CategoryBean catbean = new CategoryBean();
                                    JSONObject jobj = ja.getJSONObject(i);
                                    catbean.setRcatid(jobj.getString("RCid"));
                                    catbean.setRcatname(jobj.getString("RCname"));
                                    catbean.setSectionid(jobj.getString("sectionid"));
                                    // catbean.setRcatstatus(jobj.getInt("Ritem_price"));
                                    catList.add(catbean);

                                }

                                DBUtils.storeCategories(catList);

                                getallsubcategories();

                                PD.dismiss();
                            } else if (success == 0) {
                                Toast.makeText(LoginScreen.this, "No Items found...", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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

    private void getallsubcategories() {
        String url = "http://" + ipaddress + "/chowmein/allsubcategory.php?param=1";
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
                                    subcatbean.setRstatus(jobj.getString("rstatus"));
                                    subcatbean.setStat(jobj.getString("stat"));
                                    // catbean.setRcatstatus(jobj.getInt("Ritem_price"));
                                    subcatList.add(subcatbean);

                                }

                                DBUtils.storesubcategories(subcatList);
                                getallitems();

                                PD.dismiss();
                            } else if (success == 0) {
                                Toast.makeText(LoginScreen.this, "No Items found...", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getActivity(), "OOPPPSSSSSSSS....", Toast.LENGTH_SHORT).show();
                PD.dismiss();
                GlobalVariables.showAlertDialog(LoginScreen.this, "oops...", "Please login once again...", false);

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jreq);
    }

    private void getallitems() {
        String url = "http://" + ipaddress + "/chowmein/allitems.php?param=1";
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

                                //  Drawable drawable;
                                for (int i = 0; i < ja.length(); i++) {
                                    ItemsBean itmsbean = new ItemsBean();
                                    JSONObject jobj = ja.getJSONObject(i);
                                    itmsbean.setItemid(jobj.getString("Ritemid"));

                                    String scatname = jobj.getString("Ritem_name");
                                    itmsbean.setItemname(scatname);
                                    itmsbean.setItemprice(jobj.getString("Ritem_price"));
                                    itmsbean.setItemcode(jobj.getString("Ritem_code"));
                                    itmsbean.setIngrdients(jobj.getString("Ritem_des"));
                                    itmsbean.setPretime(jobj.getString("Ritem_ptime"));
                                    itmsbean.setRating(jobj.getString("Rrating"));
                                    itmsbean.setRscatid(jobj.getString("Rscatid"));
                                    itmsbean.setRstatus(jobj.getString("Rstatus"));
                                    itmsbean.setItmsts(jobj.getString("stat"));
                                    /*String newname = "";
                                    newname = scatname.replace(" ", "_");
                                    String imgurl = "http://" + ipaddress + "/chowmein/image/item/" + newname + ".png";
                                    Bitmap bitmap ;
                                    try {
                                        URL url = new URL(imgurl);
                                        URLConnection conn = url.openConnection();
                                        bitmap = BitmapFactory.decodeStream(conn.getInputStream());

                                    } catch (Exception ex) {
                                    }*/
                                    /*Drawable  drawable = LoadImageFromWebOperations(imgurl);
                                    if (drawable == null) {
                                        try {
                                            String deflturl = "http://" + ipaddress + "/chowmein/image/logo.png";
                                            drawable = LoadImageFromWebOperations(deflturl);
                                        } catch (Exception e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                    itmsbean.setImgdrwbl(drawable);*/

                                    itmsList.add(itmsbean);

                                }

                                DBUtils.storeAllItems(itmsList);
                                getingredeints();
                                PD.dismiss();
                            } else if (success == 0) {
                                Toast.makeText(LoginScreen.this, "No Items found...", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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

    private void getingredeints() {
        String url = "http://" + ipaddress + "/chowmein/ingredients.php";
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
                                    IngredeintsBean ingrndbean = new IngredeintsBean();
                                    JSONObject jobj = ja.getJSONObject(i);
                                    ingrndbean.setIng_id((jobj.getInt("id")));
                                    ingrndbean.setIng_name(jobj.getString("ingredient"));

                                    ingrdntsList.add(ingrndbean);

                                }

                                DBUtils.storeingredients(ingrdntsList);

                                changetabsts(2);
                                PD.dismiss();
                            } else if (success == 0) {
                                Toast.makeText(LoginScreen.this, "No Items found...", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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

        if (ipaddress == null || ipaddress.equals("")) {

        } else {
            et_ipaddress.setText(ipaddress);
            GlobalVariables.ipaddress = ipaddress;
            getmaxdatefromdb();
        }

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stripaddress = et_ipaddress.getText().toString();
                GlobalVariables.ipaddress = stripaddress;
                rememberme(stripaddress);
                ipaddress = stripaddress;
                getmaxdatefromdb();
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

    private void getmaxdatefromdb() {

        String url = "http://" + ipaddress + "/chowmein/getmaxdate.php";
        //JsonObjectRequest jrq = new JsonObjectRequest(url);
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
                                    CategoryBean catbean = new CategoryBean();
                                    JSONObject jobj = ja.getJSONObject(i);
                                    dbdate = jobj.getString("date");
                                }

                                dateverification(dbdate);
                            } else if (success == 0) {
                                Toast.makeText(LoginScreen.this, "No Items found...", Toast.LENGTH_SHORT).show();
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
        AppController.getInstance().addToRequestQueue(jreq);
    }

    private void dateverification(final String dbdate) {
        String sysdate = GlobalVariables.getcurrentdate();
        if (sysdate.equals(dbdate)) {
            checktabregistration();
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    LoginScreen.this);

            alertDialog.setTitle("Windows App Closed");
            alertDialog
                    .setMessage("Make sure your System Application is ON");
            alertDialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            getmaxdatefromdb();
                            dialog.cancel();

                        }
                    });

            alertDialog.show();
        }
    }

    private void checktabregistration() {

        String checktabUrl = "http://" + ipaddress + "/chowmein/checktab.php?macaddress=" + macaddress;
        JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.GET, checktabUrl, null,
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
                            ;
                        } else if (success == 0) {
                            Intent tabregintetnt = new Intent(LoginScreen.this, TabletRegistration.class);
                            startActivity(tabregintetnt);
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // PD.dismiss();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jreq);
    }

    private void rememberme(String stripaddress) {
        getSharedPreferences("shpreipaddress", MODE_PRIVATE)
                .edit()
                .putString("ipaddress", stripaddress)
                .commit();
    }

    public void alertbox(String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                LoginScreen.this);

        alertDialog.setTitle(title);
        alertDialog
                .setMessage(message);
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.cancel();

                    }
                });

        alertDialog.show();
    }

    private void changeloginstatus() {
        String url = "http://" + GlobalVariables.ipaddress + "/chowmein/updtLoginsts.php?macaddress=" + macaddress+"param=1";
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
    public void onBackPressed() {

    }

    private static Drawable LoadImageFromWebOperations(String url) {
        Drawable d = null;

        try {
            InputStream is = (InputStream) new URL(url).getContent();

            try {
                d = Drawable.createFromStream(is, "src name");

            } catch (Exception e) {
                System.out.println("Exc=" + e);

            }
        } catch (Exception e) {
            System.out.println("Exc=" + e);
        }
        return d;

    }


    private void allstatuscheck() {
        getcategorystatus();
        getsubcategorystatus();
        getitemstatus();
        //Toast.makeText(LoginScreen.this, "Category Status " + cattblsts + "\n" + "Sub category Status " + subcattblsts + "\n" + "Item Status " + itemtblsts, Toast.LENGTH_SHORT).show();

    }

    private void getcategorystatus() {

        String usercheckUrl = "http://" + ipaddress + "/chowmein/allstatuses.php?param=1";
        JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.GET, usercheckUrl, null,
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

                                JSONObject jobj = ja.getJSONObject(0);
                                cattblsts = jobj.getString("sts");

                                if (cattblsts.equals("1")) {
                                    cntSQLRestCat = DBUtils.getCountRestCat(); // ok
                                    if (cntSQLRestCat == 0) {
                                        getallcategory();
                                    }
                                    getcategoryonCondition();
                                }else{
                                    checklocalcatdb();
                                }

                            } else if (success == 0) {
                                //Toast.makeText(LoginScreen.this, "No user found.", Toast.LENGTH_SHORT).show();
                                alertbox("User not exist", "Please check your Credentials");
                                //PD.dismiss();
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
        AppController.getInstance().addToRequestQueue(jreq);
    }

    private void getcategoryonCondition() {
        String url = "http://" + ipaddress + "/chowmein/allcategory.php?param=2";
      //  Toast.makeText(LoginScreen.this, "URL " + url, Toast.LENGTH_SHORT).show();
        //JsonObjectRequest jrq = new JsonObjectRequest(url);
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
                                    CategoryBean catbean = new CategoryBean();
                                    JSONObject jobj = ja.getJSONObject(i);
                                    catbean.setRcatid(jobj.getString("RCid"));
                                    catbean.setRcatname(jobj.getString("RCname"));
                                    catbean.setCatstatus(jobj.getString("stat"));
                                    catbean.setRcatstatus(jobj.getInt("sectionid"));
                                    catList.add(catbean);
                                }
                                for (int i = 0; i < catList.size(); i++) {

                                   // Toast.makeText(LoginScreen.this,"RCAT ID is" +catList.get(i).getRcatid(), Toast.LENGTH_SHORT).show();
                                    if (catList.get(i).getCatstatus().equals("1")) {
                                        tmpcatStsList.clear();
                                        tmpcatStsList.add(catList.get(i));
                                        DBUtils.addNewRestCategory(tmpcatStsList);
                                    } else if (catList.get(i).getCatstatus().equals("2")) {
                                        tmpcatStsList.clear();
                                        tmpcatStsList.add(catList.get(i));
                                        DBUtils.editRestaurantCategory(tmpcatStsList);
                                    } else if (catList.get(i).getCatstatus().equals("3")) {
                                        tmpcatStsList.clear();
                                        tmpcatStsList.add(catList.get(i));
                                     //   Toast.makeText(LoginScreen.this,"RCAT ID is" +tmpcatStsList.get(i).getRcatid(), Toast.LENGTH_SHORT).show();
                                       DBUtils.deleteRestCat(tmpcatStsList.get(i).getRcatid());
                                    }
                                }
                                checklocalcatdb();

                            } else if (success == 0) {
                                Toast.makeText(LoginScreen.this, "No Items found...", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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

    private void checklocalcatdb() {
        String url = "http://" + ipaddress + "/chowmein/allcategory.php?param=3";
        //JsonObjectRequest jrq = new JsonObjectRequest(url);
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
                                    CategoryBean catbean = new CategoryBean();
                                    JSONObject jobj = ja.getJSONObject(i);
                                    catbean.setRcatname(jobj.getString("RCname"));
                                   // catbean.setRcatname(jobj.getString("RCname"));
                                    // catbean.setRcatstatus(jobj.getInt("Ritem_price"));
                                    catList.add(catbean);
                                }
                                sqlRestCatList = DBUtils.getRestCategory();
                                for (int m = 0; m < sqlRestCatList.size(); m++) {
                                    boolean isFound = false;

                                    for (int n = 0; n < catList.size(); n++) {
                                        if (sqlRestCatList.get(m).getRcatname()
                                                .equals(catList.get(n).getRcatname())) {
                                            isFound = true;
                                        }
                                    }
                                    if (!isFound) {
                                        // delete item which does not matched with server from
                                        // SqLite
                                        DBUtils.deleteRestCat(sqlRestCatList.get(m).getRcatname());
                                    } else {
                                        ;
                                    }
                                }

                            } else if (success == 0) {
                                Toast.makeText(LoginScreen.this, "No Items found...", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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

    private void getallcategory() {
        String url = "http://" + ipaddress + "/chowmein/allcategory.php?param=1";
        //JsonObjectRequest jrq = new JsonObjectRequest(url);
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
                                    CategoryBean catbean = new CategoryBean();
                                    JSONObject jobj = ja.getJSONObject(i);
                                    catbean.setRcatid(jobj.getString("RCid"));
                                    catbean.setRcatname(jobj.getString("RCname"));
                                    catbean.setSectionid(jobj.getString("sectionid"));
                                    // catbean.setRcatstatus(jobj.getInt("Ritem_price"));
                                    catList.add(catbean);
                                }
                                DBUtils.insertRestCategory(catList);

                            } else if (success == 0) {
                                Toast.makeText(LoginScreen.this, "No Items found...", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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

    private void getsubcategorystatus() {
        String usercheckUrl = "http://" + ipaddress + "/chowmein/allstatuses.php?param=2";
        JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.GET, usercheckUrl, null,
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

                                JSONObject jobj = ja.getJSONObject(0);
                                subcattblsts = jobj.getString("sts");
                               // Toast.makeText(LoginScreen.this, "SUb cat Status "+subcattblsts, Toast.LENGTH_SHORT).show();
                                if(subcattblsts.equals("1")){
                                    cntSQLRessubtCat = DBUtils.getSubCatCount();
                                 //   Toast.makeText(LoginScreen.this, "Local sub cat Count "+cntSQLRessubtCat, Toast.LENGTH_SHORT).show();
                                    if(cntSQLRessubtCat == 0){
                                        getallsubcategory();
                                    }
                                    getsubcategoryonCondition();

                                }else{
                                    checklocalsubcatdb();
                                }

                            } else if (success == 0) {
                                //Toast.makeText(LoginScreen.this, "No user found.", Toast.LENGTH_SHORT).show();
                                alertbox("User not exist", "Please check your Credentials");
                                PD.dismiss();
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
        AppController.getInstance().addToRequestQueue(jreq);
    }

    private void getsubcategoryonCondition() {
        String url = "http://" + ipaddress + "/chowmein/allsubcategory.php?param=2";
        //JsonObjectRequest jrq = new JsonObjectRequest(url);
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
                                    subcatbean.setScatid(jobj.getString("rscatid"));
                                    subcatbean.setScname(jobj.getString("rscatname"));
                                    subcatbean.setRcid(jobj.getString("rcid"));
                                    subcatbean.setRstatus(jobj.getString("rstatus"));
                                    subcatbean.setStat(jobj.getString("stat"));
                                    // catbean.setRcatstatus(jobj.getInt("Ritem_price"));
                                    subcatList.add(subcatbean);
                                }
                                for (int i = 0; i < subcatList.size(); i++) {

                                    if (subcatList.get(i).getStat().equals("1")) {

                                        tmprestsubcatList.clear();
                                        tmprestsubcatList.add(subcatList.get(i));
                                      //  Toast.makeText(LoginScreen.this,"Sub cat "+tmprestsubcatList.get(i).getScname(),Toast.LENGTH_SHORT).show();
                                        DBUtils
                                                .insertNewRestSubCat(tmprestsubcatList);
                                    } else if (subcatList.get(i).getStat()
                                            .equals("2")) {
                                        tmprestsubcatList.clear();
                                        tmprestsubcatList.add(subcatList.get(i));
                                        DBUtils.editRestSubCat(tmprestsubcatList);
                                    } else if (subcatList.get(i).getStat()
                                            .equals("3")) {
                                        tmprestsubcatList.clear();
                                        tmprestsubcatList.add(subcatList.get(i));
                                        DBUtils.deleteRstSubCat(subcatList.get(i)
                                                .getScatid());
                                    }
                                }
                                checklocalsubcatdb();

                            } else if (success == 0) {
                                Toast.makeText(LoginScreen.this, "No Items found...", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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

    private void checklocalsubcatdb() {
        String url = "http://" + ipaddress + "/chowmein/allsubcategory.php?param=3";
        //JsonObjectRequest jrq = new JsonObjectRequest(url);
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
                                    subcatbean.setScname(jobj.getString("rscatname"));
                                    // catbean.setRcatname(jobj.getString("RCname"));
                                    // catbean.setRcatstatus(jobj.getInt("Ritem_price"));
                                    subcatList.add(subcatbean);
                                }
                               // sqlRestCatList = DBUtils.getRestCategory();
                                sqlRstSubCat = DBUtils.getRestSubCatNmNId();
                                for (int m = 0; m < sqlRstSubCat.size(); m++) {
                                    boolean isFound = false;
                                    for (int n = 0; n < subcatList.size(); n++) {
                                        if (sqlRstSubCat
                                                .get(m)
                                                .getScname()
                                                .equals(subcatList.get(n)
                                                        .getScname())) {
                                            isFound = true;
                                        }
                                    }
                                    if (!isFound) {
                                        // delete item which does not matched with server
                                        // from SqLite
                                        DBUtils.deleteRstSubCat(sqlRstSubCat.get(m)
                                                .getScname());
                                    } else {
                                        ;
                                    }
                                }

                            } else if (success == 0) {
                                Toast.makeText(LoginScreen.this, "No Items found...", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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

    private void getallsubcategory() {
        String url = "http://" + ipaddress + "/chowmein/allsubcategory.php?param=1";
        //JsonObjectRequest jrq = new JsonObjectRequest(url);
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
                                    subcatbean.setRstatus(jobj.getString("rstatus"));
                                    subcatbean.setStat(jobj.getString("stat"));
                                    // catbean.setRcatstatus(jobj.getInt("Ritem_price"));
                                    subcatList.add(subcatbean);
                                }
                               // DBUtils.insertRestCategory(catList);
                                DBUtils.insertAllRstSubCat(subcatList);

                            } else if (success == 0) {
                                Toast.makeText(LoginScreen.this, "No Items found...", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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

    private void getitemstatus() {
        String usercheckUrl = "http://" + ipaddress + "/chowmein/allstatuses.php?param=3";
        JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.GET, usercheckUrl, null,
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

                                JSONObject jobj = ja.getJSONObject(0);
                                itemtblsts = jobj.getString("sts");

                                if(itemtblsts.equals("1")){
                                    cntSQLRestitm = DBUtils.getRestItemCount();

                                    if(cntSQLRestitm == 0){
                                       // Toast.makeText(LoginScreen.this, "Local Item Count "+cntSQLRestitm, Toast.LENGTH_SHORT).show();
                                        getallitemsQ();
                                    }
                                    getitmsonCondition();

                                }else{
                                    checklocalitemsdb();
                                }
                                // Toast.makeText(LoginScreen.this,"Item Status "+itemtblsts,Toast.LENGTH_SHORT).show();
                            } else if (success == 0) {
                                //Toast.makeText(LoginScreen.this, "No user found.", Toast.LENGTH_SHORT).show();
                                alertbox("User not exist", "Please check your Credentials");
                                PD.dismiss();
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
        AppController.getInstance().addToRequestQueue(jreq);
    }
    private void getitmsonCondition() {

        String url = "http://" + ipaddress + "/chowmein/allitems.php?param=2";
        //JsonObjectRequest jrq = new JsonObjectRequest(url);
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

                                    String scatname = jobj.getString("Ritem_name");
                                    itmsbean.setItemname(scatname);
                                    itmsbean.setItemprice(jobj.getString("Ritem_price"));
                                    itmsbean.setItemcode(jobj.getString("Ritem_code"));
                                    itmsbean.setIngrdients(jobj.getString("Ritem_des"));
                                    itmsbean.setPretime(jobj.getString("Ritem_ptime"));
                                    itmsbean.setRating(jobj.getString("Rrating"));
                                    itmsbean.setRscatid(jobj.getString("Rscatid"));
                                    itmsbean.setRstatus(jobj.getString("Rstatus"));
                                    itmsbean.setItmsts(jobj.getString("stat"));

                                    itmsList.add(itmsbean);
                                }

                               // Toast.makeText(LoginScreen.this, "Inside Items Condition"+cntSQLRestitm, Toast.LENGTH_SHORT).show();
                                for (int i = 0; i < itmsList.size(); i++) {
                                    if (itmsList.get(i).getItmsts().equals("1")) {

                                        tmprestitmList.clear();
                                        tmprestitmList.add(itmsList.get(i));
                                        DBUtils.insertRestItms(tmprestitmList);

                                        // restitmList.remove(i);
                                    } else if (itmsList.get(i).getItmsts().equals("2")) {
                                        //cntSQLRestitm = DBUtils.getRestItemCount();
                                       // Toast.makeText(LoginScreen.this, "Inside Items Condition"+cntSQLRestitm, Toast.LENGTH_SHORT).show();
                                        tmprestitmList.clear();
                                        tmprestitmList.add(itmsList.get(i));
                                        DBUtils.editRestItem(tmprestitmList);

                                    } else if (itmsList.get(i).getItmsts().equals("3")) {
                                        tmprestitmList.clear();
                                        tmprestitmList.add(itmsList.get(i));
                                        DBUtils.deleteRestItem(String.valueOf(itmsList.get(i).getItemid()));
                                    }
                                }
                                checklocalitemsdb();

                            } else if (success == 0) {
                                Toast.makeText(LoginScreen.this, "No Items found...", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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

    private void checklocalitemsdb() {
        String url = "http://" + ipaddress + "/chowmein/allitems.php?param=3";
        //JsonObjectRequest jrq = new JsonObjectRequest(url);
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
                                    ItemsBean itmbean = new ItemsBean();
                                    JSONObject jobj = ja.getJSONObject(i);
                                    itmbean.setItemname(jobj.getString("Ritem_name"));
                                    itmsList.add(itmbean);
                                }
                                // sqlRestCatList = DBUtils.getRestCategory();
                                sqlItemList = DBUtils.getRestItems();
                                for (int m = 0; m < sqlItemList.size(); m++) {
                                    boolean isFound = false;
                                    for (int n = 0; n < itmsList.size(); n++) {
                                        if (String.valueOf(sqlItemList.get(m).getItemname())
                                                .equals(itmsList.get(n)
                                                        .getItemname())) {
                                            isFound = true;
                                        }
                                    }
                                    if (!isFound) {
                                        // delete item which does not matched with server
                                        // from SqLite
                                        DBUtils.deleteRestItem(sqlItemList.get(m).getItemname());
                                    } else {
                                        ;
                                    }
                                }

                            } else if (success == 0) {
                                Toast.makeText(LoginScreen.this, "No Items found...", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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

    private void getallitemsQ() {
        String url = "http://" + ipaddress + "/chowmein/allitems.php?param=1";
        //JsonObjectRequest jrq = new JsonObjectRequest(url);
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

                                    String scatname = jobj.getString("Ritem_name");
                                    itmsbean.setItemname(scatname);
                                    itmsbean.setItemprice(jobj.getString("Ritem_price"));
                                    itmsbean.setItemcode(jobj.getString("Ritem_code"));
                                    itmsbean.setIngrdients(jobj.getString("Ritem_des"));
                                    itmsbean.setPretime(jobj.getString("Ritem_ptime"));
                                    itmsbean.setRating(jobj.getString("Rrating"));
                                    itmsbean.setRscatid(jobj.getString("Rscatid"));
                                    itmsbean.setRstatus(jobj.getString("Rstatus"));
                                    itmsbean.setItmsts(jobj.getString("stat"));

                                    itmsList.add(itmsbean);
                                }
                                for(int i=0; i<itmsList.size(); i++){
                                    Toast.makeText(LoginScreen.this, "Item " +itmsList.get(i).getItemname(), Toast.LENGTH_SHORT).show();
                                }
                                // DBUtils.insertRestCategory(catList);
                                DBUtils.storeAllItems(itmsList);

                            } else if (success == 0) {
                                Toast.makeText(LoginScreen.this, "No Items found...", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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
    public void onDestroy() {
        this.mWakeLock.release();
        super.onDestroy();
    }
}
