package com.wishfin.wishfinbusinessloan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class KreditBeeApplyNow extends AppCompatActivity {

    KProgressHUD progressDialog;
    SharedPreferences prefs;
    RequestQueue queue;
    String kb_lead_id = "";
    Spinner spinnercompanytype;
    TextView continuebtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kreditbeeapplynow);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        progressDialog = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel(getString(R.string.dialogtext)).setCancellable(false).setAnimationSpeed(1).setDimAmount(0.5f);

        queue = Volley.newRequestQueue(KreditBeeApplyNow.this);
        prefs = PreferenceManager.getDefaultSharedPreferences(KreditBeeApplyNow.this);

        continuebtn=findViewById(R.id.continuebtn);
        spinnercompanytype=findViewById(R.id.spinnercompanytype);

        getaouth();

        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkduplicatelead();
            }
        });
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

    public void checkduplicatelead() {

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = BuildConfig.BASE_URL + "/lead-bank?mobile_number=" + SessionManager.get_mobile(prefs) + "&bank_code=m105" + SessionManager.get_cibil_id(prefs);
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // response
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                        } else if (jsonObject.getString("status").equalsIgnoreCase("failed")) {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }

                    } catch (Exception e) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        e.printStackTrace();
                    }
                },
                error -> {
                    // TODO Auto-generated method stub
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String bearer = "Bearer " + SessionManager.get_access_token(prefs);
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Accept", "application/json");
                params.put("Authorization", bearer);

                return params;
            }
        };
        queue.add(getRequest);

    }

    public void leasavepersonalloan() {
        final JSONObject json = new JSONObject();
        try {
            json.put("wish_id", "");
            json.put("fullname", SessionManager.get_firstname(prefs) + " " + SessionManager.get_mname(prefs) + " " + SessionManager.get_lastname(prefs));
            json.put("gender", SessionManager.get_gender(prefs));
            json.put("date_of_birth", SessionManager.get_dob(prefs));
            json.put("mobileno", SessionManager.get_mobile(prefs));
            json.put("emailid", SessionManager.get_emailid(prefs));
            json.put("city", SessionManager.get_city(prefs));
            json.put("pancard", SessionManager.get_pan(prefs));
            json.put("monthlyincome", SessionManager.get_monthly_income(prefs));
            json.put("company_name", "");
            json.put("occupation", SessionManager.get_occupation(prefs));
            json.put("loanamount", SessionManager.get_loanamount(prefs));
            json.put("annualturnover", SessionManager.get_annualturnover(prefs));
            json.put("accept", "");
            json.put("source", "Wishfin_Android");
            json.put("querystring", "");
            json.put("utm_source", "");
            json.put("utm_medium", "");
            json.put("utm_campaign", "");
            json.put("referrer_address", "");
            json.put("resource_pagename", "Business_Loan_Wishfin_Android");
            json.put("resource_ip_address", "");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/personal-loan", json, response -> {
            try {

                JSONObject jsonObject = new JSONObject(response.toString());
                JSONObject jsonObject1 = jsonObject.getJSONObject("result");

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                leadbank(jsonObject1.getString("id"));


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

    public void leadbank(String id) {
        final JSONObject json = new JSONObject();
        try {

            json.put("bank_code", "m105");
            json.put("product_type", "BL");
            json.put("member_reference_id", "");
            json.put("partner_application_id", "");
            json.put("first_name", SessionManager.get_firstname(prefs));
            json.put("middle_name", SessionManager.get_mname(prefs));
            json.put("last_name", SessionManager.get_lastname(prefs));
            json.put("email_id", SessionManager.get_emailid(prefs));
            json.put("mobile_number", SessionManager.get_mobile(prefs));
            json.put("pancard", SessionManager.get_pan(prefs));
            json.put("occupation", "1");
            json.put("gender", SessionManager.get_gender(prefs));
            json.put("date_of_birth", SessionManager.get_dob(prefs));
            json.put("loan_purpose", "");
            json.put("monthly_income", "");
            json.put("corr_pincode", "");
            json.put("corr_city", "");
            json.put("corr_state", "");
            json.put("corr_add_type", "");
            json.put("journey_upto", "");
            json.put("source", "Wishfin_Android");
            json.put("utm_source", "");
            json.put("ip_address", "");
            json.put("pagename", "");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/lead-bank", json, response -> {
            try {

                JSONObject jsonObject = new JSONObject(response.toString());
                JSONObject jsonObject1 = jsonObject.getJSONObject("result");

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                createtoken(jsonObject1.getString("id"));

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

    private void createtoken(String id) {
        final JSONObject json = new JSONObject();
        try {

            json.put("lead_id", "m105");
            json.put("email", SessionManager.get_emailid(prefs));
            json.put("phone", SessionManager.get_mobile(prefs));
            json.put("source", "Wishfin_Android");
            json.put("source2", "");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/create-lead-token", json, response -> {
            try {

                JSONObject jsonObject = new JSONObject(response.toString());
                JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                JSONObject jsonObject2 = jsonObject1.getJSONObject("model");

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                getstatus(jsonObject2.getString("idToken"));

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

    private void getstatus(String idToken) {
        final JSONObject json = new JSONObject();
        try {
            json.put("lead_id", "m105");
            json.put("token", idToken);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/get-status", json, response -> {
            try {

                JSONObject jsonObject = new JSONObject(response.toString());
                JSONObject jsonObject1 = jsonObject.getJSONObject("result");

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                createprofile(idToken);


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

    private void createprofile(String idToken) {
        final JSONObject json = new JSONObject();
        try {

            json.put("fname", SessionManager.get_firstname(prefs));
            json.put("lname", SessionManager.get_lastname(prefs));
            json.put("pan", SessionManager.get_pan(prefs));
            json.put("pincode", "110010");
            json.put("monthlySalary", "");
            json.put("employmentType", "salaried");
            json.put("dob", SessionManager.get_dob(prefs));
            json.put("gender", SessionManager.get_gender(prefs));
            json.put("company", "");
            json.put("companyName", "");
            json.put("modeOfSalary", "Bank Transfer");
            json.put("lead_id", kb_lead_id);
            json.put("token", idToken);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/profile-eligible", json, response -> {
            try {

                JSONObject jsonObject = new JSONObject(response.toString());
                JSONObject jsonObject1 = jsonObject.getJSONObject("result");

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(this, ""+jsonObject1.getString("msg"), Toast.LENGTH_SHORT).show();
                finish();

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


}
