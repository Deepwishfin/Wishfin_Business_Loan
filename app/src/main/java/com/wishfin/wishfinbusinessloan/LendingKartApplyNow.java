package com.wishfin.wishfinbusinessloan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LendingKartApplyNow extends AppCompatActivity {

    KProgressHUD progressDialog;
    SharedPreferences prefs;
    RequestQueue queue;
    String lead_id = "", bank_code = "", bank_name = "", str_loan_amount = "", str_registered_as = "";
    TextView applybtn, heading;
    RelativeLayout backbutton;
    EditText businessage, businessrevenue, monthlysalary;
    Spinner select_registeredas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lendingkartlayout);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        progressDialog = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel(getString(R.string.dialogtext)).setCancellable(false).setAnimationSpeed(1).setDimAmount(0.5f);

        queue = Volley.newRequestQueue(LendingKartApplyNow.this);
        prefs = PreferenceManager.getDefaultSharedPreferences(LendingKartApplyNow.this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                lead_id = null;
                bank_code = null;
                bank_name = null;
                str_loan_amount = null;
            } else {
                lead_id = extras.getString("lead_id");
                bank_code = extras.getString("bank_code");
                bank_name = extras.getString("bank_name");
                str_loan_amount = extras.getString("loan_amount");
            }
        } else {
            lead_id = (String) savedInstanceState.getSerializable("lead_id");
            bank_code = (String) savedInstanceState.getSerializable("bank_code");
            bank_name = (String) savedInstanceState.getSerializable("bank_name");
            str_loan_amount = (String) savedInstanceState.getSerializable("loan_amount");
        }

        applybtn = findViewById(R.id.applybtn);
        heading = findViewById(R.id.heading);
        backbutton = findViewById(R.id.backbutton);
        businessage = findViewById(R.id.businessage);
        businessrevenue = findViewById(R.id.businessrevenue);
        monthlysalary = findViewById(R.id.monthlysalary);
        select_registeredas = findViewById(R.id.select_registeredas);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        heading.setText(bank_name);


        select_registeredas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    str_registered_as = select_registeredas.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        applybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (monthlysalary.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(LendingKartApplyNow.this, "Please enter monthly salary", Toast.LENGTH_SHORT).show();
                } else if (businessage.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(LendingKartApplyNow.this, "Please enter business age", Toast.LENGTH_SHORT).show();
                } else if (businessrevenue.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(LendingKartApplyNow.this, "Please enter business revenue", Toast.LENGTH_SHORT).show();
                } else if (str_registered_as.equalsIgnoreCase("")) {
                    Toast.makeText(LendingKartApplyNow.this, "Please select business registered as", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    checkduplicatelead();
                }

            }
        });

    }

    public void checkduplicatelead() {

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = BuildConfig.BASE_URL + "/lead-bank?mobile_number=" + SessionManager.get_mobile(prefs) + "&bank_code=m036" + SessionManager.get_cibil_id(prefs);
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // response

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(this, "Application Request Already Exist", Toast.LENGTH_SHORT).show();
                            finish();
                        } else if (jsonObject.getString("status").equalsIgnoreCase("failed")) {
                            if (jsonObject.getString("message").equalsIgnoreCase("no data found")) {
                                leadbank();
                            }
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

    public void leadbank() {
        final JSONObject json = new JSONObject();
        try {
            Long tsLong = System.currentTimeMillis() / 1000;
            String ts = tsLong.toString();

            json.put("bank_code", bank_code);
            json.put("product_type", "BL");
            json.put("member_reference_id", "0");
            json.put("partner_application_id", "WF-AND" + ts);
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
            json.put("monthly_income", monthlysalary.getText().toString());
            json.put("corr_pincode", SessionManager.get_pincode(prefs));
            json.put("corr_city", SessionManager.get_city(prefs));
            json.put("corr_state", "");
            json.put("corr_add_type", "");
            json.put("journey_upto", "");
            json.put("source", "Wishfin_Android");
            json.put("utm_source", "");
            json.put("ip_address", "");
            json.put("pagename", "");
            json.put("cc_pl_id", lead_id);


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
                checklendingcartlead(jsonObject1.getString("id"));


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

    private void checklendingcartlead(String id) {
        final JSONObject json = new JSONObject();
        try {

            json.put("email", SessionManager.get_emailid(prefs));
            json.put("mobile", SessionManager.get_mobile(prefs));
            json.put("lead_id", id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/lead-status", json, response -> {


            try {

                JSONObject jsonObject = new JSONObject(response.toString());


                if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    String redirectUrl = jsonObject1.getString("leadExists");

                    if (redirectUrl.equalsIgnoreCase("false")) {
                        updatelead(id);
                    } else {
                        Toast.makeText(LendingKartApplyNow.this, "Loan Request Already Exists", Toast.LENGTH_SHORT).show();
                    }


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


        }, error -> {

            try {
                int statusCode = error.networkResponse.statusCode;
                if (statusCode == 421) {

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
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        queue.add(jsonObjectRequest);
    }

    private void updatelead(String id) {
        final JSONObject json = new JSONObject();
        try {
            json.put("firstName", SessionManager.get_firstname(prefs));
            json.put("lastName", SessionManager.get_lastname(prefs));
            json.put("email", SessionManager.get_emailid(prefs));
            json.put("mobile", SessionManager.get_mobile(prefs));
            json.put("businessAge", businessage.getText().toString());
            json.put("businessRevenue", businessrevenue.getText().toString());
            json.put("registeredAs", str_registered_as);
            json.put("haveBusiness", "true");
            json.put("loanAmount", str_loan_amount);
            json.put("pincode", SessionManager.get_pincode(prefs));
            json.put("personalDob", SessionManager.get_dob(prefs));
            json.put("personalPAN", SessionManager.get_pan(prefs));
            json.put("lead_id", id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/create-application", json, response -> {


            try {

                JSONObject jsonObject = new JSONObject(response.toString());


                if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }


                    if (jsonObject1.getString("message").equalsIgnoreCase("ELIGIBLE")) {
                        String redirectUrl = jsonObject1.getString("redirectUrl");
                        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirectUrl));
                        startActivity(urlIntent);

                    } else {
                        Toast.makeText(LendingKartApplyNow.this, jsonObject1.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    finish();

                } else if (jsonObject.getString("status").equalsIgnoreCase("failed")) {
                    Toast.makeText(LendingKartApplyNow.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

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


        }, error -> {

            try {
                int statusCode = error.networkResponse.statusCode;
                if (statusCode == 421) {
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
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        queue.add(jsonObjectRequest);
    }
}
