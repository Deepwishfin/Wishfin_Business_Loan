package com.wishfin.wishfinbusinessloan;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
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
import com.android.volley.toolbox.Volley;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PersonalInformationPage extends AppCompatActivity {

    TextView continuebtn, heading, subheading;
    KProgressHUD progressDialog;
    SharedPreferences prefs;
    RequestQueue queue;

    ArrayList<Gettersetterforall> citylist = new ArrayList<>();
    ArrayList<String> stringcitylist = new ArrayList<>();
    String gender = "1", lead_id = "";
    RadioButton yescheck, nocheck, malecheck, femalecheck;
    EditText pincode, pan, fullname, email, dob;
    int mYear, mMonth, mDay;
    RelativeLayout backbutton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personalinfopage);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        progressDialog = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel(getString(R.string.dialogtext)).setCancellable(false).setAnimationSpeed(1).setDimAmount(0.5f);

        queue = Volley.newRequestQueue(PersonalInformationPage.this);
        prefs = PreferenceManager.getDefaultSharedPreferences(PersonalInformationPage.this);

        continuebtn = findViewById(R.id.continuebtn);
        heading = findViewById(R.id.heading);
        subheading = findViewById(R.id.subheading);
        yescheck = findViewById(R.id.yescheck);
        nocheck = findViewById(R.id.nocheck);
        malecheck = findViewById(R.id.malecheck);
        femalecheck = findViewById(R.id.femalecheck);
        pincode = findViewById(R.id.pincodet);
        pan = findViewById(R.id.pan);
        dob = findViewById(R.id.dob);
        backbutton = findViewById(R.id.backbutton);
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        pan.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

        pan.setText(SessionManager.get_pan(prefs));
        dob.setText(SessionManager.get_dob(prefs));
        email.setText(SessionManager.get_emailid(prefs));
        fullname.setText(SessionManager.get_firstname(prefs));

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getaouth();

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicdob("user");
            }
        });

        malecheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "1";
            }
        });

        femalecheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "0";
            }
        });


        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fullname.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter Full Name", Toast.LENGTH_LONG).show();
                } else if (email.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter Email", Toast.LENGTH_LONG).show();
                } else if (dob.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Select DOB", Toast.LENGTH_LONG).show();
                } else if (pincode.getText().toString().length() < 6) {
                    Toast.makeText(getApplicationContext(), "Enter Pincode", Toast.LENGTH_LONG).show();
                } else if (!validatePAN()) {
                    Toast.makeText(getApplicationContext(), "Enter Valid PAN", Toast.LENGTH_LONG).show();
                } else {
                    progressDialog.show();
                    getloanid();
                }


            }
        });

    }

    public void update_lead() {
        final JSONObject json = new JSONObject();
        try {

            json.put("gender", gender);
            json.put("panno", pan.getText().toString());
            json.put("coapplicantname", "");
            json.put("coapplicantdob", "");
            json.put("coapplicantincome", "");
            json.put("coapplicanttotalobiligation", "");
            json.put("prorequestid", lead_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/v1/home-loan-continue", json, response -> {
            try {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                JSONObject jsonObject = new JSONObject(response.toString());
                JSONObject jsonObject1 = jsonObject.getJSONObject("result");

                if (jsonObject.getString("status").equalsIgnoreCase("Success")) {

                    Intent intent = new Intent(PersonalInformationPage.this, Dashboard.class);
                    startActivity(intent);
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                String bearer = "Bearer " + SessionManager.get_access_token(prefs);
                header.put("Content-Type", "application/json; charset=utf-8");
                header.put("Accept", "application/json");
                header.put("Authorization", bearer);

                return header;
            }
        };
        queue.add(jsonObjectRequest);
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

    private boolean validatePAN() {

        String pannum = pan.getText().toString().trim();
        Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
        Matcher matcher = pattern.matcher(pannum);
        if (!matcher.matches() || pan.getText().length() != 10) {
            return false;
        }
        return true;
    }

    private void DatePicdob(String type) {

        Calendar mcurrentDate = Calendar.getInstance();
        mYear = mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        mcurrentDate.add(Calendar.DATE, -1);

        DatePickerDialog mDatePicker = new DatePickerDialog(PersonalInformationPage.this, (datepicker, selectedyear, selectedmonth, selectedday) -> {
            // TODO Auto-generated method stub

            selectedmonth++;
            String month = "";
            if (selectedmonth > 0 && selectedmonth < 10) {
                month = "0" + selectedmonth;
            } else {
                month = "" + selectedmonth;
            }


        }, mYear, mMonth, mDay);
        mDatePicker.setTitle("Select date");
        mDatePicker.getDatePicker().setMaxDate((long) (mcurrentDate.getTimeInMillis() - (1000 * 60 * 60 * 24 * 365.25 * 18)));
        mDatePicker.getDatePicker().setMinDate((long) (mcurrentDate.getTimeInMillis() - (1000 * 60 * 60 * 24 * 365.25 * 65)));
        mDatePicker.show();

    }

    public void getloanid() {
        final JSONObject json = new JSONObject();
        try {
            json.put("legal_response", "Accept");
            json.put("report_trigger", "true");
            json.put("show_report_xml", false);
            json.put("consent_option", "");
            json.put("website_flag", "wishfin");
            json.put("resource_pagename", "Business_Loan_Wishfin_Android");
            json.put("resource_source", "Business_Loan_Wishfin_Android");
            json.put("resource_querystring", "");
            json.put("resource_ip_address", "");
            json.put("source", "Wishfin_Android");
            json.put("utm_source", "");
            json.put("utm_medium", "");
            json.put("referrer_address", "Business_Loan_Wishfin_Android");
            json.put("querystring", "");

            json.put("EmploymentType", SessionManager.get_occupation(prefs));
            json.put("DesiredLoanAmount", SessionManager.get_loanamount(prefs));
            json.put("GrossAnnualTurnover", SessionManager.get_annualturnover(prefs));
            json.put("YearsinCurrentBusiness", SessionManager.get_business_year(prefs));
            json.put("CompanyType", SessionManager.get_company_type(prefs));
            json.put("NatureofBusiness", SessionManager.get_nature_business(prefs));
            json.put("IndustryType", SessionManager.get_industry_type(prefs));
            json.put("SubIndustryType", SessionManager.get_sub_industry_type(prefs));
            json.put("OwnershipofResidenceOrBusinessPlace", SessionManager.get_ownership_residence(prefs));
            json.put("Wishtotakeloanagainstcollateral", SessionManager.get_collatoral_loan(prefs));
            json.put("fname", SessionManager.get_firstname(prefs));
            json.put("lname", SessionManager.get_lastname(prefs));
            json.put("Gender", SessionManager.get_gender(prefs));
            json.put("Email", SessionManager.get_emailid(prefs));
            json.put("Dob", SessionManager.get_dob(prefs));
            json.put("Pincode", pincode.getText().toString());
            json.put("Pan", SessionManager.get_pan(prefs));
            json.put("city_name", SessionManager.get_city(prefs));
            json.put("total_monthly_obligation", "");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/bussiness-loan-create", json, response -> {
            try {

                JSONObject jsonObject = new JSONObject(response.toString());
                JSONObject jsonObject1 = jsonObject.getJSONObject("result");

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                lead_id = jsonObject1.getString("id");
                SessionManager.save_lead_id(prefs, lead_id);
                Intent intent = new Intent(PersonalInformationPage.this, Dashboard.class);
                startActivity(intent);
                finish();

//                progressDialog.show();
//                update_lead();

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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
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
