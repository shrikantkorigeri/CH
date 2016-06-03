package com.san.emenu.chowmein;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.san.emenu.chowmein.adapter.CategoryAdapter;
import com.san.emenu.chowmein.adapter.ItemsAdapter;
import com.san.emenu.chowmein.adapter.PreorderAdapter;
import com.san.emenu.chowmein.adapter.SubcatAdapter;
import com.san.emenu.chowmein.app.AppController;
import com.san.emenu.chowmein.bean.CategoryBean;
import com.san.emenu.chowmein.bean.ItemsBean;
import com.san.emenu.chowmein.bean.SubCatBean;
import com.san.emenu.chowmein.localdb.DBUtils;
import com.san.emenu.chowmein.utils.GlobalVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MenuFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    ListView lv_category, lv_subcat, lv_items;
    // Button btnplaceorder;
    TextView txtmainmenuname, txtsubmenuname, txtcartcnt;
    RelativeLayout rl_cart;

    CustomDialog custmdlg;
    Animation cartanimation;

    SharedPreferences pref, pref1;
    //  EditText et_search;
    FragmentManager manager;
    FragmentTransaction transaction;

    List<ItemsBean> itmList = new ArrayList<>();
    List<SubCatBean> subcatList = new ArrayList<>();
    List<CategoryBean> catList = new ArrayList<>();

    ProgressDialog PD;
    WifiManager mWifiManager;
    WifiInfo wifiInfo;
    String macaddress, serverUrl;
    int ordercount = 0;
    private OnFragmentInteractionListener mListener;

    public MenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MenuFragment newInstance(String param1, String param2) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        pref1 = getActivity().getSharedPreferences("shpreipaddress", getActivity().MODE_PRIVATE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        lv_category = (ListView) view.findViewById(R.id.lv_category);
        lv_subcat = (ListView) view.findViewById(R.id.lv_subcat);
        lv_items = (ListView) view.findViewById(R.id.lv_items);
        // lv_cart = (ListView) view.findViewById(R.id.lv_cart);
        txtsubmenuname = (TextView) view.findViewById(R.id.txtsubmenuname);
        txtmainmenuname = (TextView) view.findViewById(R.id.txtmainmenuname);
        txtcartcnt = (TextView) view.findViewById(R.id.txtcartcnt);
        //  et_search = (EditText) view.findViewById(R.id.et_search);
        rl_cart = (RelativeLayout) view.findViewById(R.id.rl_cart);

        rl_cart.setOnClickListener(this);

        mWifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        wifiInfo = mWifiManager.getConnectionInfo();
        macaddress = wifiInfo.getMacAddress();

        serverUrl = "http://" + pref1.getString("ipaddress", null) + "/chowmein/";
        // btnplaceorder = (Button) view.findViewById(R.id.btnplaceorder);

        pref = getActivity().getSharedPreferences("tmpvalues", getActivity().MODE_PRIVATE);


       /* et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchitm = s.toString();
                String url_auto = serverUrl+"readauto.php?item=" + searchitm;

                JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.GET, url_auto, null,
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
                                    itmList.clear();
                                    if (success == 1) {
                                        JSONArray ja = response.getJSONArray("items");
                                        String[] itms = new String[ja.length()];
                                        for (int i = 0; i < ja.length(); i++) {
                                            ItemsBean itmbean = new ItemsBean();
                                            JSONObject jobj = ja.getJSONObject(i);
                                            itmbean.setItemcode(jobj.getString("Ritem_code"));
                                            itmbean.setItemname(jobj.getString("Ritem_name"));
                                            itmbean.setItemprice(jobj.getString("Ritem_price"));
                                            itmList.add(itmbean);

                                        } // for loop ends

                                        lv_items.setAdapter(new ItemsAdapter(getActivity(), itmList));

                                        PD.dismiss();

                                    } else if(success == 0){
                                        Toast.makeText(getActivity(),"No Items found...", Toast.LENGTH_SHORT).show();
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
            public void afterTextChanged(Editable s) {

            }
        });*/

        txtcartcnt.setText("" + GlobalVariables.preordrList.size());

        catList = DBUtils.getcategoryfromdb();
        String subcatid = null;
        // txtmainmenuname.setText(catList.get(0).getRcatname());
        lv_category.setAdapter(new CategoryAdapter(getActivity(), catList, lv_subcat, lv_items, txtmainmenuname, txtsubmenuname, txtcartcnt));
        subcatList.clear();
        subcatList = DBUtils.getsubcatfrmdb(catList.get(0).getRcatid());
        if (subcatList.size() == 0) {
            lv_subcat.setAdapter(null);
            Toast.makeText(getActivity(), "No Sub Menu found...", Toast.LENGTH_SHORT).show();
        } else {
            subcatid = subcatList.get(0).getScatid();
            lv_subcat.setAdapter(new SubcatAdapter(getActivity(), subcatList, lv_items, txtsubmenuname, txtcartcnt));
            // Toast.makeText(getActivity(), "Sub cat ID " +subcatid, Toast.LENGTH_SHORT).show();
        }

        itmList.clear();
        itmList = DBUtils.getitemsfrmdb(subcatid);
        txtsubmenuname.setText(subcatList.get(0).getScname());
        if (itmList.size() == 0) {
            lv_items.setAdapter(null);
            Toast.makeText(getActivity(), "No Items found...", Toast.LENGTH_SHORT).show();
        } else {
            lv_items.setAdapter(new ItemsAdapter(getActivity(), itmList, txtcartcnt));
        }

        return view;
    }

    private void updatOrdrConnectionst(int orderid) {
        String url = serverUrl + "updtOrdrConnectionst.php?order_id=" + orderid;
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

                            //  PD.dismiss();
                        } else if (success == 0) {
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //  PD.dismiss();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jreq);
    }

    private void addorder() {
        InsertOrder insrtordr = new InsertOrder();
        insrtordr.execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_cart:
                preordercart();
                break;
        }
    }

    private void preordercart() {
        if (GlobalVariables.preordrList.size() <= 0) {
            Toast.makeText(getActivity(), "You do not have any items to place order", Toast.LENGTH_SHORT).show();
        } else {
            custmdlg = new CustomDialog(getActivity());
            custmdlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
            custmdlg.setContentView(R.layout.preordlayout);
            WindowManager.LayoutParams a = custmdlg.getWindow().getAttributes();
            custmdlg.getWindow().setAttributes(a);
            custmdlg.setCancelable(false);
            custmdlg.getWindow().setLayout(ActionBar.LayoutParams.WRAP_CONTENT,
                    ActionBar.LayoutParams.WRAP_CONTENT);

            custmdlg.show();

            ListView lv_cart = (ListView) custmdlg.findViewById(R.id.lv_cart);
            Button btnplaceorder = (Button) custmdlg.findViewById(R.id.btnplaceorder);
            Button btnordermore = (Button) custmdlg.findViewById(R.id.btnordermore);

            lv_cart.setAdapter(new PreorderAdapter(getActivity(), GlobalVariables.preordrList, lv_cart, txtcartcnt));

            btnordermore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    custmdlg.dismiss();
                }
            });

            btnplaceorder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int cartsize = GlobalVariables.preordrList.size();

                    if (cartsize == 0) {
                        Toast.makeText(getActivity(), "Your Cart is empty...", Toast.LENGTH_SHORT).show();
                    } else {
                        custmdlg.dismiss();
                        String orderidUrl = serverUrl + "getorderid.php?tblno=" + pref.getString("tblno", null) + "&area=" + pref.getString("dngarea", null) + "&odate=" + GlobalVariables.getcurrentdate();
                        JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.GET, orderidUrl, null,
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            int success = 0, orderid = 0;
                                            try {
                                                success = response.getInt("success");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            if (success == 1) {
                                                JSONArray ja = response.getJSONArray("items");
                                                for (int i = 0; i < ja.length(); i++) {
                                                    JSONObject jobj = ja.getJSONObject(i);
                                                    orderid = jobj.getInt("order_id");
                                                }
                                                // GlobalVariables.order_id = String.valueOf(orderid);
                                                getActivity().getSharedPreferences("tmpvalues", getActivity().MODE_PRIVATE)
                                                        .edit().putString("order_id", String.valueOf(orderid)).commit();

                                                increaseorderCount(orderid);

                                              /*  addordereditem(orderid);
                                                updatOrdrConnectionst(orderid);*/

                                            } else if (success == 0) {
                                                // custmdlg.dismiss();
                                                addorder();

                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });

                        // Adding request to request queue
                        AppController.getInstance().addToRequestQueue(jreq);
                    }
                }
            });
        }

    }

    private void increaseorderCount(final int orderidd) {
        String orderidUrl = serverUrl + "getmaxordercount.php?order_id=" + orderidd;
        JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.GET, orderidUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = 0, orderid = 0;
                            try {
                                success = response.getInt("success");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (success == 1) {
                                JSONArray ja = response.getJSONArray("items");
                                // for (int i = 0; i < ja.length(); i++) {
                                JSONObject jobj = ja.getJSONObject(0);
                                ordercount = Integer.valueOf(jobj.getString("orderCount"));
                                //}
                                addordereditem(orderidd);
                                updatOrdrConnectionst(orderidd);

                            } else if (success == 0) {
                                // custmdlg.dismiss();
                                // addorder();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jreq);
    }

    class InsertOrder extends AsyncTask<String, Void, String> {

        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(getActivity(), "Please Wait", "Your Order is Placing...", true, true);
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String result;
                BufferedReader bufferedReader = null;
                String url = serverUrl + "plc_order.php";
                HttpURLConnection conn = GlobalVariables.getconnection(url);
                JSONObject json = new JSONObject();
                json.put("table_no", pref.getString("tblno", null));
                json.put("staff_id", "test");
                json.put("order_date", GlobalVariables.getcurrentdate());
                json.put("order_time", GlobalVariables.getcurrentime());
                json.put("fotype", "Tab");
                json.put("ostatus", 0);
                json.put("macaddress", macaddress);
                json.put("darea", pref.getString("dngarea", null));
                json.put("seats", "1");
                json.put("order_price", "150.00");

                byte[] outputBytes = json.toString().getBytes("UTF-8");
                OutputStream os = conn.getOutputStream();
                os.write(outputBytes);
                os.close();

                bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                result = bufferedReader.readLine();

            } catch (Exception je) {
                je.getStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (loading.isShowing()) {
                loading.dismiss();
            }
            //Replaced it from doInBackground method
            getOrderID(pref.getString("tblno", null));
        }
    }

    private void getOrderID(String tblno) {
        String orderidUrl = serverUrl + "getlrgstorderId.php?tblno=" + tblno;
        JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.GET, orderidUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = 0, orderid = 0;
                            try {
                                success = response.getInt("success");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (success == 1) {
                                JSONArray ja = response.getJSONArray("items");
                                for (int i = 0; i < ja.length(); i++) {
                                    JSONObject jobj = ja.getJSONObject(i);
                                    orderid = jobj.getInt("order_id");
                                }
                                // GlobalVariables.order_id = String.valueOf(orderid);
                                getActivity().getSharedPreferences("tmpvalues", getActivity().MODE_PRIVATE)
                                        .edit().putString("order_id", String.valueOf(orderid)).commit();
                                updatOrdrConnectionst(orderid);
                                addordereditem(orderid);

                            } else if (success == 0) {
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


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private void addordereditem(int orderid) {
        InsertPlacedOrders insertorders = new InsertPlacedOrders();
        insertorders.execute(String.valueOf(orderid));
    }


    class InsertPlacedOrders extends AsyncTask<String, Void, String> {

        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(getActivity(), "Please Wait", "Your Order is Placing...", true, true);
        }

        @Override
        protected void onPostExecute(String s) {
            loading.dismiss();
            changetablestatys();

            Toast.makeText(getActivity(), "Placed successfully...", Toast.LENGTH_LONG).show();
            //custmdlg.dismiss();
            txtcartcnt.setText("0");
            cartanimation = AnimationUtils.loadAnimation(getActivity(), R.anim.blink);
            txtcartcnt.startAnimation(cartanimation);
            txtcartcnt.clearAnimation();
            //   lv_cart.setAdapter(null);
            GlobalVariables.preordrList.clear();

        }

        @Override
        protected String doInBackground(String... params) {

            updtitmscnt();

            String ordid = params[0];
            // String ordercount = params[1];
            String result = null;
            BufferedReader bufferedReader = null;
            JSONObject response = null;
            int addmore;
            if (ordercount == 0) {
                addmore = 1;
            } else {
                int intordrcnt = ordercount;
                addmore = intordrcnt + 1;
            }

            try {
                String url = serverUrl + "plcd_orders.php";
                HttpURLConnection conn;
                JSONObject json;
                for (int i = 0; i < GlobalVariables.preordrList.size(); i++) {
                    conn = GlobalVariables.getconnection(url);
                    ItemsBean itmbean = GlobalVariables.preordrList.get(i);
                    json = new JSONObject();
                    json.put("item_name", itmbean.getItemname());
                    json.put("rate", itmbean.getItemprice());
                    json.put("order_id", ordid);
                    json.put("item_id", itmbean.getItemcode());

                    json.put("qty", itmbean.getQty());
                    json.put("free", "0");
                    json.put("sqty", itmbean.getOrdertype());
                    double price = Double.parseDouble(itmbean.getItemprice());
                    int qty = itmbean.getQty();

                    double subtot = price * qty;
                    json.put("tprice", String.valueOf(subtot));
                    json.put("ostatus", "0");
                    json.put("ing_choice", itmbean.getIngrdients());
                    json.put("cdate", GlobalVariables.getcurrentdate());
                    json.put("ptype", "T");
                    json.put("otype", "Restaurant");

                    json.put("orderCount", String.valueOf(addmore));
                    json.put("SLNO", String.valueOf(i + 1));

                    // response = json.getJSONObject("success");

                    byte[] outputBytes = json.toString().getBytes("UTF-8");
                    OutputStream os = conn.getOutputStream();
                    os.write(outputBytes);
                    os.close();

                    bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    result = bufferedReader.readLine();

                }

                return result;
            } catch (Exception e) {
                return null;
            }
        }
    }

    private void updtitmscnt() {

        String url = serverUrl + "updttblitmcnt.php?tblno=" + pref.getString("tblno", null) + "&area=" + pref.getString("dngarea", null) + "&itmcnt=" + GlobalVariables.preordrList.size();
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

                            //  PD.dismiss();
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

    private void changetablestatys() {
        String url = serverUrl + "updttblsts.php?tblno=" + pref.getString("tblno", null) + "&area=" + pref.getString("dngarea", null);
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

                            //  PD.dismiss();
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void showAlertDialog(String title, String message, final Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

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
                //getcategories();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}
