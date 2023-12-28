package com.wishfin.wishfinbusinessloan;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MoneyViewApplyNow extends AppCompatActivity {

    KProgressHUD progressDialog;
    SharedPreferences prefs;
    RequestQueue queue;
    String str_loan_purpose="",str_family_income = "", str_education = "", strpartnetrefid = "", lead_id = "", bank_code = "", bank_name = "";
    Spinner select_family_income, select_education,select_loan_purpose;
    TextView continuebtn, heading;
    EditText monthlysalary;
    RelativeLayout backbutton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moneyviewapplynow);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        progressDialog = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel(getString(R.string.dialogtext)).setCancellable(false).setAnimationSpeed(1).setDimAmount(0.5f);

        queue = Volley.newRequestQueue(MoneyViewApplyNow.this);
        prefs = PreferenceManager.getDefaultSharedPreferences(MoneyViewApplyNow.this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                lead_id = null;
                bank_code = null;
                bank_name = null;
            } else {
                lead_id = extras.getString("lead_id");
                bank_code = extras.getString("bank_code");
                bank_name = extras.getString("bank_name");
            }
        } else {
            lead_id = (String) savedInstanceState.getSerializable("lead_id");
            bank_code = (String) savedInstanceState.getSerializable("bank_code");
            bank_name = (String) savedInstanceState.getSerializable("bank_name");
        }

        continuebtn = findViewById(R.id.continuebtn);
        select_family_income = findViewById(R.id.select_family_income);
        select_loan_purpose = findViewById(R.id.select_loan_purpose);
        select_education = findViewById(R.id.select_education);
        monthlysalary = findViewById(R.id.monthlysalary);
        heading = findViewById(R.id.heading);

        backbutton = findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        heading.setText(bank_name);

        getaouth();

        select_family_income.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    str_family_income = select_family_income.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        select_loan_purpose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String[] array_accomtype = getResources().getStringArray(R.array.array_loan_purpose_value);

                if (position != 0) {
                    str_loan_purpose = array_accomtype[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        select_education.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    str_education = select_education.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (str_family_income.equalsIgnoreCase("")) {
                    Toast.makeText(MoneyViewApplyNow.this, "Please select family income", Toast.LENGTH_SHORT).show();
                } else if (str_education.equalsIgnoreCase("")) {
                    Toast.makeText(MoneyViewApplyNow.this, "Please select education", Toast.LENGTH_SHORT).show();
                } else if (str_loan_purpose.equalsIgnoreCase("")) {
                    Toast.makeText(MoneyViewApplyNow.this, "Please select loan purpose", Toast.LENGTH_SHORT).show();
                } else if (monthlysalary.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(MoneyViewApplyNow.this, "Please enter salary", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    checkduplicatelead();

                }

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
            strpartnetrefid = "WF-AND" + ts;

            json.put("bank_code", bank_code);
            json.put("product_type", "BL");
            json.put("member_reference_id", "0");
            json.put("partner_application_id", strpartnetrefid);
            json.put("first_name", SessionManager.get_firstname(prefs));
            json.put("middle_name", SessionManager.get_mname(prefs));
            json.put("last_name", SessionManager.get_lastname(prefs));
            json.put("email_id", SessionManager.get_emailid(prefs));
            json.put("mobile_number", SessionManager.get_mobile(prefs));
            json.put("pancard", SessionManager.get_pan(prefs));
            json.put("occupation", "1");
            json.put("gender", SessionManager.get_gender(prefs));
            json.put("date_of_birth", SessionManager.get_dob(prefs));
            json.put("loan_purpose", str_loan_purpose);
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

                createlead(jsonObject1.getString("id"));


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
    public void createlead(String id) {
        final JSONObject json = new JSONObject();
        try {

            Long tsLong = System.currentTimeMillis() / 1000;
            String ts = tsLong.toString();

            json.put("partnerCode", "29");
            json.put("partnerRef", strpartnetrefid);
            json.put("phone", SessionManager.get_mobile(prefs));
            json.put("pan", SessionManager.get_pan(prefs));
            json.put("name", SessionManager.get_firstname(prefs) + " " + SessionManager.get_mname(prefs) + " " + SessionManager.get_lastname(prefs));

            String gendervalue = "male";
            if (SessionManager.get_gender(prefs).equalsIgnoreCase("1")) {
                gendervalue = "male";
            } else if (SessionManager.get_gender(prefs).equalsIgnoreCase("2")) {
                gendervalue = "female";
            }
            json.put("gender", gendervalue);
            json.put("dateOfBirth", SessionManager.get_dob(prefs));
            json.put("bureauPermission", 1);
            json.put("employmentType", "Salaried");
            json.put("incomeMode", "online");
            json.put("declaredIncome", monthlysalary.getText().toString());
            json.put("educationLevel", str_education);
            json.put("loanPurpose", "");
            json.put("annualFamilyIncome", str_family_income);

            JSONObject jsonObject1=new JSONObject();
            jsonObject1.put("consentDecision",true);
            jsonObject1.put("deviceTimeStamp",ts);
            JSONObject jsonObject2=new JSONObject();
            jsonObject2.put("deviceIpAddress","127.0.1");
            jsonObject1.put("metaData",jsonObject2);

            json.put("consent",jsonObject1);

            JSONObject jsonObjectaddress=new JSONObject();
            jsonObjectaddress.put("pincode",SessionManager.get_pincode(prefs));
            jsonObjectaddress.put("addressType","current");
            JSONArray jsonArrayaddress=new JSONArray();
            jsonArrayaddress.put(jsonObjectaddress);

            json.put("addressList",jsonArrayaddress);

            JSONObject jsonObjectemail=new JSONObject();
            jsonObjectemail.put("email",SessionManager.get_emailid(prefs));
            jsonObjectemail.put("type","primary_device");
            JSONArray jsonArrayemail=new JSONArray();
            jsonArrayemail.put(jsonObjectemail);

            json.put("emailList",jsonArrayemail);




        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/money-view/create-lead", json, response -> {
            try {

                JSONObject jsonObject = new JSONObject(response.toString());

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                    JSONObject jsonObject1=jsonObject.getJSONObject("result");
                    if(jsonObject1.getString("status").equalsIgnoreCase("success")) {
                        Intent intent2 = new Intent(MoneyViewApplyNow.this, MoneyViewOfferPage.class);
                        intent2.putExtra("lead_id", jsonObject1.getString("leadId"));
                        intent2.putExtra("strpartnetrefid", strpartnetrefid);
                        intent2.putExtra("bank_name", bank_name);
                        startActivity(intent2);
                    }else {
                        Toast.makeText(this, jsonObject1.getString("message"), Toast.LENGTH_SHORT).show();
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

