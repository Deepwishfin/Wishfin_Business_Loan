package com.wishfin.wishfinbusinessloan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MoneyViewOfferPage extends AppCompatActivity {

    KProgressHUD progressDialog;
    SharedPreferences prefs;
    RequestQueue queue;
    String lead_id = "", strpartnetrefid = "", bank_name = "";
    TextView continuebtn, heading;
    RecyclerView article_list;
    ArrayList<Gettersetterforall> list1 = new ArrayList<>();
    Share_Adapter radio_question_list_adapter;
    RelativeLayout backbutton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moneyviewofferpage);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        progressDialog = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel(getString(R.string.dialogtext)).setCancellable(false).setAnimationSpeed(1).setDimAmount(0.5f);

        queue = Volley.newRequestQueue(MoneyViewOfferPage.this);
        prefs = PreferenceManager.getDefaultSharedPreferences(MoneyViewOfferPage.this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                lead_id = null;
                strpartnetrefid = null;
                bank_name = null;
            } else {
                lead_id = extras.getString("lead_id");
                strpartnetrefid = extras.getString("strpartnetrefid");
                bank_name = extras.getString("bank_name");
            }
        } else {
            lead_id = (String) savedInstanceState.getSerializable("lead_id");
            strpartnetrefid = (String) savedInstanceState.getSerializable("strpartnetrefid");
            bank_name = (String) savedInstanceState.getSerializable("bank_name");
        }

        continuebtn = findViewById(R.id.continuebtn);
        heading = findViewById(R.id.heading);
        article_list = findViewById(R.id.article_list);

        backbutton = findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        heading.setText(bank_name);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(MoneyViewOfferPage.this) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(MoneyViewOfferPage.this) {
                    private static final float SPEED = 4000f;

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return SPEED / displayMetrics.densityDpi;
                    }
                };
                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }

        };

        layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
//        card_list.addItemDecoration(new DividerItemDecoration(Dashboard.this, DividerItemDecoration.HORIZONTAL));
        article_list.setLayoutManager(layoutManager1);

        getaouth();
        progressDialog.show();
        getoffers();
    }

    public void getaouth() {
        final JSONObject json = new JSONObject();
        try {
            json.put("username", BuildConfig.oAuthUserName);
            json.put("password", BuildConfig.oAuthPassword);
            json.put("client_id", BuildConfig.oAuthClientId);
            json.put("grant_type", BuildConfig.oAuthGrantType);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/oauth", json, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response.toString());

                SessionManager.save_access_token(prefs, jsonObject.getString("access_token"));
                SessionManager.save_expirein(prefs, jsonObject.getString("expires_in"));
                SessionManager.save_token_type(prefs, jsonObject.getString("token_type"));
                SessionManager.save_refresh_token(prefs, jsonObject.getString("refresh_token"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
    }

    private void getoffers() {
        final JSONObject json = new JSONObject();
        try {

            json.put("lead_id", lead_id);
            json.put("partner_reference_id", strpartnetrefid);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/money-view/get-offers", json, response -> {
            try {

                JSONObject jsonObject = new JSONObject(response.toString());

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                    JSONObject jsonObject1=jsonObject.getJSONObject("result");
                    JSONArray jsonArray = (jsonObject1.getJSONArray("offerObjects"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject objectnew2 = jsonArray.getJSONObject(i);
                        Gettersetterforall pack = new Gettersetterforall();
                        try {
                            pack.setInterest_rate(objectnew2.getString("rateOfInterest"));
                            pack.setEmi(objectnew2.getString("loanEmi"));
                            pack.setTenure(objectnew2.getString("loanTenure"));
                            pack.setLoan_amount(objectnew2.getString("loanAmount"));
                            pack.setProcessing_fee(objectnew2.getString("processingFeeAmount"));
                        } catch (Exception e) {
                            pack.setInterest_rate("NA");
                            pack.setEmi("NA");
                            pack.setTenure("NA");
                            pack.setLoan_amount("NA");
                            pack.setProcessing_fee("NA");
                        }

                        list1.add(pack);
                    }

                    if (list1.size() > 0) {
                        radio_question_list_adapter = new Share_Adapter(MoneyViewOfferPage.this, list1);
                        article_list.setAdapter(radio_question_list_adapter);
                        article_list.setVisibility(View.GONE);

                    } else {
                        Toast.makeText(MoneyViewOfferPage.this, "No Offers Found", Toast.LENGTH_LONG).show();
                        article_list.setVisibility(View.GONE);

                    }

                    progressDialog.show();
                    getactivitystatus();


                } else if (jsonObject.getString("status").equalsIgnoreCase("failed")) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(MoneyViewOfferPage.this, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                }

            } catch (Exception e) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                e.printStackTrace();
            }


        }, error -> {

            try {
                int statusCode = error.networkResponse.statusCode;
                if (statusCode == 421) {
                    getaouth();
                }
                error.printStackTrace();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<>();
                String bearer = "Bearer " + SessionManager.get_access_token(prefs);
                header.put("Content-Type", "application/json; charset=utf-8");
                header.put("Accept", "application/json");
                header.put("Authorization", bearer);

                return header;
            }
        };
        queue.add(jsonObjectRequest);
    }

//    private void getleadstatus() {
//        final JSONObject json = new JSONObject();
//        try {
//            json.put("lead_id", lead_id);
//            json.put("partner_reference_id", strpartnetrefid);
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/lead-activity-status", json, response -> {
//            try {
//
//                JSONObject jsonObject = new JSONObject(response.toString());
//
//                if (progressDialog != null && progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
//                if (jsonObject.getString("status").equalsIgnoreCase("success") && jsonObject.getString("leadStatus").equalsIgnoreCase("NOT_REQUIRED")) {///created logic
//                    article_list.setVisibility(View.VISIBLE);
//                }else {
//                    journeyurl(lead_id);
//                }
//
//
//            } catch (Exception e) {
//                if (progressDialog != null && progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
//                e.printStackTrace();
//            }
//
//
//        }, error -> {
//
//            try {
//                int statusCode = error.networkResponse.statusCode;
//                if (statusCode == 421) {
//                    getaouth();
//                }
//                error.printStackTrace();
//                if (progressDialog != null && progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> header = new HashMap<>();
//                String bearer = "Bearer " + SessionManager.get_access_token(prefs);
//                header.put("Content-Type", "application/json; charset=utf-8");
//                header.put("Accept", "application/json");
//                header.put("Authorization", bearer);
//
//                return header;
//            }
//        };
//        queue.add(jsonObjectRequest);
//    }

    private void getactivitystatus() {
        final JSONObject json = new JSONObject();
        try {
            json.put("type", "bankstatement");
            json.put("leadId", lead_id);
            json.put("partner_reference_id", strpartnetrefid);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/money-view/lead-activity-status", json, response -> {
            try {

                JSONObject jsonObject = new JSONObject(response.toString());

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                if (jsonObject.getString("status").equalsIgnoreCase("success") && jsonObject1.getString("activityStatus").equalsIgnoreCase("NOT_REQUIRED")) {
                   article_list.setVisibility(View.VISIBLE);
                }else {
                    journeyurl(lead_id);
                }


            } catch (Exception e) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                e.printStackTrace();
            }


        }, error -> {

            try {
                int statusCode = error.networkResponse.statusCode;
                if (statusCode == 421) {
                    getaouth();
                }
                error.printStackTrace();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<>();
                String bearer = "Bearer " + SessionManager.get_access_token(prefs);
                header.put("Content-Type", "application/json; charset=utf-8");
                header.put("Accept", "application/json");
                header.put("Authorization", bearer);

                return header;
            }
        };
        queue.add(jsonObjectRequest);
    }

    private void getupdatelead(String loanamt,String emi,String processfee,String tenure,String roi) {
        final JSONObject json = new JSONObject();
        try {
            json.put("partnerCode", "29");
            json.put("leadId", lead_id);
            json.put("partner_reference_id", strpartnetrefid);

            JSONObject jsonObject1=new JSONObject();
            jsonObject1.put("loanAmount",loanamt);
            jsonObject1.put("loanEmi",emi);
            jsonObject1.put("processingFeeAmount",processfee);
            jsonObject1.put("loanTenure",tenure);
            jsonObject1.put("rateOfInterest",roi);

            json.put("selectedOffer",jsonObject1);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, BuildConfig.BASE_URL + "/money-view/create-lead/1", json, response -> {
            try {

                JSONObject jsonObject = new JSONObject(response.toString());

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (jsonObject.getString("status").equalsIgnoreCase("success") && jsonObject.getString("activityStatus").equalsIgnoreCase("NOT_REQUIRED")) {
                    article_list.setVisibility(View.VISIBLE);
                }else {
                    journeyurl(lead_id);
                }


            } catch (Exception e) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                e.printStackTrace();
            }


        }, error -> {

            try {
                int statusCode = error.networkResponse.statusCode;
                if (statusCode == 421) {
                    getaouth();
                }
                error.printStackTrace();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<>();
                String bearer = "Bearer " + SessionManager.get_access_token(prefs);
                header.put("Content-Type", "application/json; charset=utf-8");
                header.put("Accept", "application/json");
                header.put("Authorization", bearer);

                return header;
            }
        };
        queue.add(jsonObjectRequest);
    }

    private void journeyurl(String id) {
        final JSONObject json = new JSONObject();
        try {

            json.put("lead_id", lead_id);
            json.put("partner_reference_id", strpartnetrefid);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/money-view/get-journey-url", json, response -> {
            try {

                JSONObject jsonObject = new JSONObject(response.toString());

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                    JSONObject jsonObject1=jsonObject.getJSONObject("result");
                    String url = jsonObject1.getString("pwa");
                    Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(urlIntent);
                    finish();
                }


            } catch (Exception e) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                e.printStackTrace();
            }


        }, error -> {

            try {
                int statusCode = error.networkResponse.statusCode;
                if (statusCode == 421) {
                    getaouth();
                }
                error.printStackTrace();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<>();
                String bearer = "Bearer " + SessionManager.get_access_token(prefs);
                header.put("Content-Type", "application/json; charset=utf-8");
                header.put("Accept", "application/json");
                header.put("Authorization", bearer);

                return header;
            }
        };
        queue.add(jsonObjectRequest);
    }

    public class Share_Adapter extends RecyclerView.Adapter<Share_Adapter.MyViewHolder> {

        private ArrayList<Gettersetterforall> list_car;
        Activity context;

        Share_Adapter(Activity mcontext, ArrayList<Gettersetterforall> list) {
            this.list_car = list;
            this.context = mcontext;
        }

        // method for filtering our recyclerview items.
        @SuppressLint("NotifyDataSetChanged")
        public void filterList(ArrayList<Gettersetterforall> filterllist) {
            // below line is to add our filtered
            // list in our course array list.
            list_car = filterllist;
            // below line is to notify our adapter
            // as change in recycler view data.
            notifyDataSetChanged();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView joiningfees, annualfees, viewdetails,loan_amount_text,emi_text;
            RelativeLayout relit;

            MyViewHolder(View view) {
                super(view);

                annualfees = view.findViewById(R.id.annualfees);
                joiningfees = view.findViewById(R.id.joiningfees);
                viewdetails = view.findViewById(R.id.viewdetails);
                loan_amount_text = view.findViewById(R.id.loan_amount_text);
                emi_text = view.findViewById(R.id.emi_text);
                relit = view.findViewById(R.id.relit);

            }
        }

        @Override
        public Share_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.moneyview_offer_adapter, parent, false);

            return new Share_Adapter.MyViewHolder(itemView);
        }

        @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
        @Override
        public void onBindViewHolder(Share_Adapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            holder.joiningfees.setText(list_car.get(position).getInterest_rate() + "%");
            holder.annualfees.setText(list_car.get(position).getProcessing_fee());
            holder.loan_amount_text.setText(list_car.get(position).getLoan_amount());
            holder.emi_text.setText(list_car.get(position).getEmi());

            holder.viewdetails.setTag(position);

            holder.viewdetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos = (int) v.getTag();

                    holder.relit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressDialog.show();
                            getupdatelead(list_car.get(pos).getLoan_amount(),list_car.get(pos).getEmi(),
                                    list_car.get(pos).getProcessing_fee(),list_car.get(pos).getTenure(),list_car.get(pos).getInterest_rate());
                        }
                    });

                }
            });


        }

        @Override
        public int getItemCount() {
            return list_car.size();
        }

    }


}
