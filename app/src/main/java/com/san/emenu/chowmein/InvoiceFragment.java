package com.san.emenu.chowmein;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.san.emenu.chowmein.adapter.InvoiceAdapter;
import com.san.emenu.chowmein.adapter.OrdersAdapter;
import com.san.emenu.chowmein.app.AppController;
import com.san.emenu.chowmein.bean.ItemsBean;
import com.san.emenu.chowmein.utils.GlobalVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InvoiceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InvoiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InvoiceFragment extends Fragment implements View.OnClickListener, MenuFragment.OnFragmentInteractionListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    SharedPreferences pref,pref1;

    private OnFragmentInteractionListener mListener;

    ListView lv_orderstoinvc;
    TextView txttotlprice, txtsrvctaxvalue, txtgrndtotvalue;

    ProgressDialog PD;
    /* Handler handler ;
     Runnable runnable ;*/


    List<ItemsBean> inceitmList = new ArrayList<>();
    DecimalFormat decimvalue = new DecimalFormat("#.00");
    Button btnbillrqst;
    // ImageView imgextra;

    String serverUrl;
    FragmentTransaction transaction;
    FragmentManager manager;

    public InvoiceFragment() {
        // Required empty public constructor
       /* handler = null;
        runnable = null;*/
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InvoiceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InvoiceFragment newInstance(String param1, String param2) {
        InvoiceFragment fragment = new InvoiceFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_invoice, container, false);
        initializations(view);

        pref = getActivity().getSharedPreferences("tmpvalues", getActivity().MODE_PRIVATE);
        pref1 = getActivity().getSharedPreferences("shpreipaddress", getActivity().MODE_PRIVATE);
        serverUrl = "http://" + pref1.getString("ipaddress", null) + "/chowmein/";

        // Toast.makeText(getActivity(), "Order ID is : " +GlobalVariables.order_id +" Area is : "+GlobalVariables.dngarea,Toast.LENGTH_SHORT).show();
        getinvoicedetails();
        btnbillrqst.setOnClickListener(this);


        /*final int[] imageArray = { R.drawable.newrest, R.drawable.spscn};

          handler = new Handler();
          runnable = new Runnable() {

            int i = 0;

            @Override
            public void run() {
                imgextra.setImageResource(imageArray[i]);
                i++;
                if (i > imageArray.length - 1) {
                    i = 0;
                }
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(runnable, 3000);*/


        return view;
    }

    private void getinvoicedetails() {
        PD = new ProgressDialog(getActivity());
        PD.setMessage("Loading.....");
        PD.setCancelable(false);
        PD.show();

       // Toast.makeText(getActivity(), " Order id is " +pref.getString("order_id", null), Toast.LENGTH_SHORT).show();

        String url = serverUrl + "getorderdetails.php?order_id=" + pref.getString("order_id", null) + "&area=" + pref.getString("dngarea", null);
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
                                    ItemsBean orditmbean = new ItemsBean();
                                    JSONObject jobj = ja.getJSONObject(i);
                                    orditmbean.setItemname(jobj.getString("item_name"));
                                    orditmbean.setQty(jobj.getInt("qty"));
                                    orditmbean.setItemprice(jobj.getString("rate"));

                                    inceitmList.add(orditmbean);

                                }
                                PD.dismiss();
                                lv_orderstoinvc.setAdapter(new InvoiceAdapter(getActivity(), inceitmList));
                                double total = 0;
                                for (int i = 0; i < inceitmList.size(); i++) {
                                    total = total + (inceitmList.get(i).getQty() * Double.parseDouble(inceitmList.get(i).getItemprice()));
                                }
                                txttotlprice.setText("" + decimvalue.format(total));
                                double dblservicecharge = total * 10 / 100;
                                double grndtot = total + dblservicecharge;
                                txtsrvctaxvalue.setText("" + decimvalue.format(dblservicecharge));
                                txtgrndtotvalue.setText("" + decimvalue.format(grndtot));
                            } else if (success == 0) {
                                PD.dismiss();
                                Toast.makeText(getActivity(), "No Items found...", Toast.LENGTH_SHORT).show();
                               /* MenuFragment menuFragment = new MenuFragment();
                                transaction = manager.beginTransaction();
                                transaction.replace(R.id.frgmntgroup, menuFragment, "menufrgmnt");
                                transaction.commit();*/

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

    private void initializations(View view) {
        lv_orderstoinvc = (ListView) view.findViewById(R.id.lv_orderstoinvc);
        txttotlprice = (TextView) view.findViewById(R.id.txttotlprice);
        txtsrvctaxvalue = (TextView) view.findViewById(R.id.txtsrvctaxvalue);
        txtgrndtotvalue = (TextView) view.findViewById(R.id.txtgrndtotvalue);
        btnbillrqst = (Button) view.findViewById(R.id.btnbillrqst);
        //  imgextra = (ImageView) view.findViewById(R.id.imgextra);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnbillrqst:
                sendbil();
                break;
        }
    }

    private void sendbil() {
        if (inceitmList.size() == 0) {
            Toast.makeText(getActivity(), "You don't have any items... ", Toast.LENGTH_SHORT).show();
        } else {
            String orderidUrl = serverUrl + "alrdybillrqstsent.php?order_id=" + pref.getString("order_id", null) + "&order_date=" + GlobalVariables.getcurrentdate();
            JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.GET, orderidUrl, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            int success = 0, orderid = 0;
                            try {
                                success = response.getInt("success");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (success == 1) {
                                Toast.makeText(getActivity(), "Already Bill Request sent", Toast.LENGTH_SHORT).show();
                            } else if (success == 0) {
                                SendBillRqstAsync sendbillrqst = new SendBillRqstAsync();
                                sendbillrqst.execute();
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    class SendBillRqstAsync extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PD = new ProgressDialog(getActivity());
            PD.setMessage("Loading.....");
            PD.setCancelable(false);
            PD.show();
        }

        @Override
        protected String doInBackground(String... params) {

            sendrequest();

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (PD.isShowing())
                PD.dismiss();

            Toast.makeText(getActivity(), "Bill Request Sent Successfully...", Toast.LENGTH_SHORT).show();

           /* MenuFragment menuFragment = new MenuFragment();
            transaction = manager.beginTransaction();
            transaction.add(R.id.frgmntgroup, menuFragment, "menufrgmnt");
            transaction.commit();*/
        }
    }

    public String sendrequest() {
        String result = null, result1 = null;
        BufferedReader bufferedReader = null;
        try {
            String url = serverUrl + "billrequest.php";
            HttpURLConnection conn = GlobalVariables.getconnection(url);
            JSONObject json = new JSONObject();
            json.put("table_no", pref.getString("tblno", null));
            json.put("order_id", pref.getString("order_id", null));
            json.put("order_date", GlobalVariables.getcurrentdate());
            json.put("order_time", GlobalVariables.getcurrentime());

            json.put("status", "0");
            json.put("sname", pref.getString("username", null));
            json.put("area", pref.getString("dngarea", null));

            byte[] outputBytes = json.toString().getBytes("UTF-8");
            OutputStream os = conn.getOutputStream();
            os.write(outputBytes);
            os.close();

            bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            result = bufferedReader.readLine();

            result1 = tabledtlsstatuschng();
        } catch (Exception e) {
            // return null;
        }

        return result1;
    }

    public String tabledtlsstatuschng() {
        String result;
        String url = serverUrl + "updttblstsbillrqst.php?tblno=" + pref.getString("tblno", null) + "&area=" + pref.getString("dngarea", null);
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
                            //result = "1";

                        } else if (success == 0) {
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jreq);

        return null;
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

    @Override
    public void onPause() {
        super.onPause();

        //  handler.removeCallbacks(runnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //  handler.removeCallbacks(runnable);
    }
}
