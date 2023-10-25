package com.wishfin.wishfinbusinessloan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TataCapitalApplyNow extends AppCompatActivity {

    KProgressHUD progressDialog;
    SharedPreferences prefs;
    RequestQueue queue;
    Spinner select_maritalstatus;
    String str_city_name = "", str_state_name = "",str_pincode_name="", str_branch_code = "", str_webtopNo = "",
            lead_id = "", bank_code = "", bank_name = "", str_first_name = "", str_middlename = "", str_last_name = "",str_loan_amount="";
    TextView continuebtn, heading;
    EditText monthlysalary,official_email,oaddreessline1,oaddreessline2,raddreessline1,raddreessline2;

    AutoCompleteTextView employername,ocity,opincode,rcity,rpincode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tatacapitalapplynow);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        progressDialog = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel(getString(R.string.dialogtext)).setCancellable(false).setAnimationSpeed(1).setDimAmount(0.5f);

        queue = Volley.newRequestQueue(TataCapitalApplyNow.this);
        prefs = PreferenceManager.getDefaultSharedPreferences(TataCapitalApplyNow.this);

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

        continuebtn = findViewById(R.id.continuebtn);
        select_maritalstatus = findViewById(R.id.select_maritalstatus);
        monthlysalary = findViewById(R.id.monthlysalary);
        official_email = findViewById(R.id.official_email);
        employername = findViewById(R.id.employername);
        oaddreessline1 = findViewById(R.id.oaddreessline1);
        oaddreessline2 = findViewById(R.id.oaddreessline2);
        raddreessline1 = findViewById(R.id.raddreessline1);
        raddreessline2 = findViewById(R.id.raddreessline2);
        rcity = findViewById(R.id.rcity);
        rpincode = findViewById(R.id.rpincode);
        ocity = findViewById(R.id.ocity);
        opincode = findViewById(R.id.opincode);
        heading = findViewById(R.id.heading);

        heading.setText(bank_name);

        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                checkduplicatelead();
            }
        });

        getaouth();
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

        String url = BuildConfig.BASE_URL + "/axis-bank-cc-journey-logs?leadId=login&mobile_number=" + SessionManager.get_mobile(prefs) + "&type=tata-bl";
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // response

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                            panverification();
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

    private void panverification() {
        final JSONObject json = new JSONObject();
        try {

            json.put("lead_id", lead_id);
            json.put("panNumber", SessionManager.get_pan(prefs));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/pan-verification", json, response -> {

            try {

                JSONObject jsonObject = new JSONObject(response.toString());

                if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    JSONArray jsonArray = jsonObject1.getJSONArray("PanVerificationResponse");

                    JSONObject jsonObject2 = jsonArray.getJSONObject(0);

                    str_first_name = jsonObject2.getString("FirstName");
                    str_middlename = jsonObject2.getString("MiddleName");
                    str_last_name = jsonObject2.getString("LastName");

                    if (jsonObject2.getString("PanStatus").equalsIgnoreCase("N")) {

                        checkpincode();

                    } else {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
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

    private void checkpincode() {
        final JSONObject json = new JSONObject();
        try {

            json.put("bankCode", "m016_bl");
            json.put("type", "pincode");
            json.put("pincode", rpincode.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/bank-state-city-lists", json, response -> {


            try {

                JSONObject jsonObject = new JSONObject(response.toString());


                if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                    JSONArray jsonArray=jsonObject.getJSONArray("result");
                    JSONObject objectnew2 = jsonArray.getJSONObject(0);
                    str_city_name=objectnew2.getString("city");
                    str_state_name=objectnew2.getString("state");
                    str_branch_code=objectnew2.getString("branchcode");
                    str_pincode_name=objectnew2.getString("pincode");
                    createlead();

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

    public void createlead() {
        final JSONObject json = new JSONObject();
        try {

            json.put("lead_id", lead_id);
            json.put("bank_type", "tata-bl");
            json.put("mobile_number", SessionManager.get_mobile(prefs));
            json.put("request_data", "");
            json.put("status", "");
            json.put("log_data", "");
            json.put("case", "insert");
            json.put("source", "Android_Lead");
            json.put("page_name", "");
            json.put("utm_source", "");
            json.put("utm_campaign", "");
            json.put("utm_medium", "");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/axis-bank-cc-journey-logs", json, response -> {
            try {

                JSONObject jsonObject = new JSONObject(response.toString());


                if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                    createleadtata();

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

    public void createleadtata() {
        final JSONObject json = new JSONObject();
        try {

            json.put("lead_id", lead_id);
            json.put("mobile_number", SessionManager.get_mobile(prefs));
            json.put("last_name", str_last_name);
            json.put("pin_code", str_pincode_name);
            json.put("city_name", str_city_name);
            json.put("subSource", "DSA");
            json.put("Product", "BL");
            json.put("Remarks", "");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/create-lead", json, response -> {
            try {

                JSONObject jsonObject = new JSONObject(response.toString());

                if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                    createwebtop();

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

    public void createwebtop() {
        final JSONObject json = new JSONObject();
        try {

            json.put("lead_id", lead_id);
            json.put("dmsBranchCode", str_branch_code);
            json.put("productName", "Business Loan");
            json.put("applicantName", str_first_name + " " + str_middlename + "" + str_last_name);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/axis-bank-cc-journey-logs", json, response -> {
            try {

                JSONObject jsonObject = new JSONObject(response.toString());


                if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    str_webtopNo = jsonObject1.getString("webtopNo");

                    submitapplication();

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

    public void submitapplication() {
        final JSONObject json = new JSONObject();
        try {

            json.put("webtopNo", str_webtopNo);
            json.put("requestTime", "");
            json.put("productName", "BL");
            json.put("firstName", str_first_name);
            json.put("lastName", str_last_name);
            json.put("namePrefix", "Mr.");
            json.put("gender", SessionManager.get_gender(prefs));
            json.put("marital_status", select_maritalstatus.getSelectedItem().toString());
            json.put("dateOfBirth", SessionManager.get_dob(prefs));
            json.put("age", "30");
            json.put("timeAtAddress", 0);
            json.put("rentAmount", 0);
            json.put("addressLine1", raddreessline1.getText().toString());
            json.put("addressLine2",  raddreessline2.getText().toString());
            json.put("addressCity", rcity.getText().toString());
            json.put("addressPin", rpincode.getText().toString());
            json.put("addressState", "");
            json.put("resiCumOffice", "N");
            json.put("phoneNumber", SessionManager.get_mobile(prefs));
            json.put("emailId", SessionManager.get_emailid(prefs));
            json.put("office_emailId", official_email.getText().toString());
            json.put("panNumber", SessionManager.get_pan(prefs));
            json.put("noOfDependents", 0);
            json.put("noOfEarningMembers", 0);
            json.put("noOfFamilyMembers", 0);
            json.put("employerName", employername.getText().toString());
            json.put("employementType", "Salaried");
            json.put("totalWorkExperience", 96);
            json.put("currentWorkExperience", 96);
            json.put("monthlyIncome", monthlysalary.getText().toString());
            json.put("grossIncome", monthlysalary.getText().toString());
            json.put("itrAmount", 0);
            json.put("totalBusinessStability", 0);
            json.put("currentBusinessStability", 0);
            json.put("reqLoanTenor", 0);
            json.put("reqLoanIrr", 0);
            json.put("apprvEmiAmount", 0);
            json.put("apprvLoanAmount", 0);
            json.put("apprvLoanIrr", 0);
            json.put("emi", 0);
            json.put("reqEmiAmount", 0);
            json.put("advancedEmi", 0);
            json.put("marginAmount", 0);
            json.put("leadId", lead_id);
            json.put("reqLoanAmount", str_loan_amount);
            json.put("Paddressline1", "NOIDA");
            json.put("Paddressline2", "NOIDA");
            json.put("Pcity", str_city_name);
            json.put("Pstate",str_state_name);
            json.put("PpinNumber", str_pincode_name);
            json.put("Ofaddressline1", oaddreessline1.getText().toString());
            json.put("Ofaddressline2", oaddreessline2.getText().toString());
            json.put("Ofcity", ocity.getText().toString());
            json.put("Ofstate", "");
            json.put("OfpinNumber", opincode.getText().toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/axis-bank-cc-journey-logs", json, response -> {
            try {

                JSONObject jsonObject = new JSONObject(response.toString());


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
