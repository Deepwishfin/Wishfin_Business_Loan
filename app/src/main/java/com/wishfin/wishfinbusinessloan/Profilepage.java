package com.wishfin.wishfinbusinessloan;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Profilepage extends AppCompatActivity implements View.OnClickListener {

    String updated_time="";
    LinearLayout line1, line2, line5;

    ImageView back;
    LinearLayout termsandconditionrelative, privacypolicyrelative,
            helpsupportrelative, contactusrelative, logoutrelative;
    Dialog dialog;
    SharedPreferences prefs;
    TextView heading, desc, checkcibil, refreshscore, cibilscore, cibildisplay;
    KProgressHUD progressDialog;
    RequestQueue queue;
    String IPaddress = "", multiplequestion = "false", singlequestion = "false";
    CustomPagerAdapter adapter;
    ExpandableListAdapter radio_question_list_adapter_expandable;
    WishFinAnalytics wishFinAnalytics;
    Dialog dialog2, dialog3, dialog4;
    EditText fname, mname, lname, pan, dob, email, monthly_income;
    RadioButton salariedcheck, selfemployedcheck;
    String str_occupation = "";
    int mYear, mMonth, mDay;
    String selecteddate = "";
    TextInputLayout input_layout_fname, input_layout_lname, input_layout_pan, input_layout_email, input_layout_mname, input_layout_dob, input_layout_monthly_income;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profilepage);

        wishFinAnalytics = new WishFinAnalytics(this);

        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        line5 = findViewById(R.id.line5);

        line1.setOnClickListener(this);
        line2.setOnClickListener(this);
        line5.setOnClickListener(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(Profilepage.this);

        termsandconditionrelative = findViewById(R.id.termsandconditionrelative);
        privacypolicyrelative = findViewById(R.id.privacypolicyrelative);
        helpsupportrelative = findViewById(R.id.helpsupportrelative);
        logoutrelative = findViewById(R.id.logoutrelative);
        checkcibil = findViewById(R.id.checkcibil);
        refreshscore = findViewById(R.id.refreshscore);
        cibilscore = findViewById(R.id.cibilscore);
        cibildisplay = findViewById(R.id.cibildisplay);
        contactusrelative = findViewById(R.id.contactusrelative);

        dialog4 = new Dialog(Profilepage.this);
        dialog4.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog4.setCancelable(true);

        dialog2 = new Dialog(Profilepage.this, androidx.appcompat.R.style.Theme_AppCompat_Light_DarkActionBar);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.setCancelable(false);

        dialog3 = new Dialog(Profilepage.this, androidx.appcompat.R.style.Theme_AppCompat_Light_DarkActionBar);
        dialog3.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog3.setCancelable(false);

        queue = Volley.newRequestQueue(Profilepage.this);
        prefs = PreferenceManager.getDefaultSharedPreferences(Profilepage.this);
        progressDialog = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(getString(R.string.dialogtext))
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                IPaddress = "";
            } else {
                IPaddress = extras.getString("ipaddress");
            }
        } else {
            IPaddress = (String) savedInstanceState.getSerializable("ipaddress");
        }

        getaouth();

        if (SessionManager.get_logintype(prefs).equalsIgnoreCase("Signup")) {
            cibildisplay.setVisibility(View.GONE);
            checkcibil.setVisibility(View.VISIBLE);
            refreshscore.setVisibility(View.GONE);
        } else {
            update_last_login();
            cibildisplay.setVisibility(View.VISIBLE);
            checkcibil.setVisibility(View.GONE);
            refreshscore.setVisibility(View.GONE);
        }

        contactusrelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Objects.requireNonNull(dialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
                dialog.setContentView(R.layout.contactus);
                dialog.show();
                TextView cancle = dialog.findViewById(R.id.call);
                TextView ok = dialog.findViewById(R.id.email);

                cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:+918882935454"));
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        String emailsend = "appsupport@wishfin.com";
                        String emailsubject = "Wishfin Home Loan App Issue";
                        String emailbody = "Hi Support";

                        // define Intent object
                        // with action attribute as ACTION_SEND
                        Intent intent = new Intent(Intent.ACTION_SEND);

                        // add three fiels to intent using putExtra function
                        intent.putExtra(Intent.EXTRA_EMAIL,
                                new String[]{emailsend});
                        intent.putExtra(Intent.EXTRA_SUBJECT, emailsubject);
                        intent.putExtra(Intent.EXTRA_TEXT, emailbody);

                        // set type of intent
                        intent.setType("message/rfc822");

                        // startActivity with intent with chooser
                        // as Email client using createChooser function
                        startActivity(
                                Intent
                                        .createChooser(intent,
                                                "Choose an Email client :"));
                        dialog.dismiss();

                    }
                });

            }
        });

        checkcibil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (progressDialog != null) {
                    progressDialog.show();
                }
                wishFinAnalytics.cibilPage();
                get_cibil_fulfill_order("normal");


            }
        });

        refreshscore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (progressDialog != null) {
                    progressDialog.show();
                }
                customerassetsforrefresh();


            }
        });

        dialog = new Dialog(Profilepage.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        dialog.setContentView(R.layout.logout);
        back = findViewById(R.id.backbutton);
        heading = findViewById(R.id.heading);
        desc = findViewById(R.id.desc);

        String heading_text = SessionManager.get_firstname(prefs);
        heading.setText(heading_text);
        String number = "+91-" + SessionManager.get_mobile(prefs) + " | " + SessionManager.get_emailid(prefs);
        desc.setText(number);

        back.setOnClickListener(v -> {
            if (isThereInternetConnection()) {
                Intent intent5 = new Intent(Profilepage.this, Dashboard.class);
                startActivity(intent5);
                finish();
            } else {
                Objects.requireNonNull(dialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
                dialog.setContentView(R.layout.nointernet);
                dialog.show();
                TextView submit = dialog.findViewById(R.id.btnsubmit);
                submit.setOnClickListener(view -> {
                    if (isThereInternetConnection()) {
                        dialog.dismiss();
                        Intent intent5 = new Intent(Profilepage.this, Dashboard.class);
                        startActivity(intent5);
                        finish();

                    } else {
                        Toast.makeText(Profilepage.this, getString(R.string.checkinternet), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        termsandconditionrelative.setOnClickListener(v -> {
            if (isThereInternetConnection()) {
                Intent intent1 = new Intent(Profilepage.this, Dynamicdisplaypage.class);
                intent1.putExtra("type", "terms-and-conditions");
                startActivity(intent1);
            } else {
                Objects.requireNonNull(dialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
                dialog.setContentView(R.layout.nointernet);
                dialog.show();
                TextView submit = dialog.findViewById(R.id.btnsubmit);
                submit.setOnClickListener(view -> {
                    if (isThereInternetConnection()) {
                        dialog.dismiss();
                        Intent intent1 = new Intent(Profilepage.this, Dynamicdisplaypage.class);
                        intent1.putExtra("type", "terms-and-conditions");
                        startActivity(intent1);

                    } else {
                        Toast.makeText(Profilepage.this, getString(R.string.checkinternet), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        privacypolicyrelative.setOnClickListener(v -> {
            if (isThereInternetConnection()) {
                Intent intent1 = new Intent(Profilepage.this, Dynamicdisplaypage.class);
                intent1.putExtra("type", "privacy-policy");
                startActivity(intent1);
            } else {
                Objects.requireNonNull(dialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
                dialog.setContentView(R.layout.nointernet);
                dialog.show();
                TextView submit = dialog.findViewById(R.id.btnsubmit);
                submit.setOnClickListener(view -> {
                    if (isThereInternetConnection()) {
                        dialog.dismiss();
                        Intent intent1 = new Intent(Profilepage.this, Dynamicdisplaypage.class);
                        intent1.putExtra("type", "privacy-policy");
                        startActivity(intent1);

                    } else {
                        Toast.makeText(Profilepage.this, getString(R.string.checkinternet), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        helpsupportrelative.setOnClickListener(v -> {

            wishFinAnalytics.rateapp();
            showRateAppFallbackDialog();

//            Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
//            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
//            try {
//                startActivity(goToMarket);
//            } catch (ActivityNotFoundException e) {
//                Toast.makeText(getApplicationContext(), "Couldn't launch the market", Toast.LENGTH_LONG).show();
//            }

        });

        logoutrelative.setOnClickListener(v -> {

            Objects.requireNonNull(dialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            dialog.setContentView(R.layout.logout);
            dialog.show();
            TextView cancle = dialog.findViewById(R.id.cancle);
            TextView ok = dialog.findViewById(R.id.ok);

            cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    dialog.dismiss();
                    progressDialog.show();
                    logout_app();
                }
            });

        });

    }


    protected boolean isThereInternetConnection() {
        boolean isConnected;
        ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        isConnected = (networkInfo != null && networkInfo.isConnectedOrConnecting());

        return isConnected;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent5 = new Intent(Profilepage.this, Dashboard.class);
        startActivity(intent5);
        finish();

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.line1:
                Intent intent2 = new Intent(Profilepage.this, Dashboard.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.line2:
                Intent intent3 = new Intent(Profilepage.this, EMICalculator.class);
                startActivity(intent3);
                break;


        }

    }

    public void get_cibil_fulfill_order(String type) {

        final JSONObject json = new JSONObject();
        String appendurl = "";

        try {
            if (type.equalsIgnoreCase("normal")) {
                appendurl = "/v2/cibil-fulfill-order";
                json.put("first_name", "" + SessionManager.get_firstname(prefs));
                json.put("middle_name", "" + SessionManager.get_mname(prefs));
                if (SessionManager.get_lastname(prefs).equalsIgnoreCase("")) {
                    json.put("last_name", "" + SessionManager.get_firstname(prefs));
                } else {
                    json.put("last_name", "" + SessionManager.get_lastname(prefs));
                }
                json.put("email_id", "" + SessionManager.get_emailid(prefs));
                json.put("mobile_number", "" + SessionManager.get_mobile(prefs));
                json.put("date_of_birth", "");
                json.put("annual_income", "");
                json.put("occupation", "");
            } else {
                appendurl = "/v1/cibil-fulfill-order";
                json.put("first_name", "" + fname.getText().toString().trim());
                json.put("middle_name", "" + mname.getText().toString().trim());
                json.put("last_name", "" + lname.getText().toString().trim());
                json.put("date_of_birth", "" + dob.getText().toString().trim());
                json.put("email_id", "" + email.getText().toString().trim());
                json.put("annual_income", "" + Integer.parseInt(monthly_income.getText().toString()) * 12);
                json.put("occupation", "" + str_occupation);
                json.put("mobile_number", "" + SessionManager.get_mobile(prefs));
            }
            json.put("pancard", "" + SessionManager.get_pan(prefs));
            json.put("gender", "");
            json.put("city_name", "Default");
            json.put("state_code", "27");
            json.put("residence_address", "Default");
            json.put("residence_pincode", "400001");
            json.put("legal_response", "Accept");
            json.put("report_trigger", "true");
            json.put("show_report_xml", false);
            json.put("consent_option", "");
            json.put("website_flag", "wishfin");
            json.put("resource_pagename", "Home_Loan_Wishfin_Android");
            json.put("resource_source", "Home_Loan_Wishfin_Android");
            json.put("resource_querystring", "");
            json.put("resource_ip_address", IPaddress);
            json.put("source", "Wishfin_Android");
            json.put("utm_source", "");
            json.put("utm_medium", "");
            json.put("referrer_address", "Home_Loan_Wishfin_Android");
            json.put("querystring", "");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, BuildConfig.BASE_URL + appendurl, json,
                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());

                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                            JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                            String cibil_score = "", lastdate = "";
                            try {
                                String cibil_id = jsonObject1.getString("cibil_id");
                                SessionManager.save_cibil_id(prefs, cibil_id);
                            } catch (Exception e) {

                            }
                            String cibil_status = jsonObject1.getString("cibil_status");
                            try {
                                cibil_score = jsonObject1.getString("cibil_score");
                                cibilscore.setText(cibil_score);

                            } catch (Exception e) {

                            }
                            try {
                                lastdate = jsonObject1.getString("cibil_score_fetch_date");
                            } catch (Exception e) {

                            }
                            String apicall = "";
                            try {
                                apicall = jsonObject1.getString("next_api_call");
                            } catch (Exception e) {

                            }

                            String is_returning_customer = "";
                            try {
                                is_returning_customer = jsonObject1.getString("is_returning_customer");
                            } catch (Exception e) {

                            }
                            if (cibil_status.equalsIgnoreCase("Failure")) {

                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                showerrordialog();
                            }

                            if (cibil_status.equalsIgnoreCase("Inprogress") && apicall.
                                    equalsIgnoreCase("cibil-authentication-questions") && is_returning_customer.equalsIgnoreCase("1")
                                    || (cibil_status.equalsIgnoreCase("Pending") && apicall.
                                    equalsIgnoreCase("cibil-authentication-questions") && is_returning_customer.equalsIgnoreCase("1"))) {
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                authenticateuestionasync();

                            } else if (is_returning_customer.equalsIgnoreCase("1")) {
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
//                                Toast.makeText(Profilepage.this, "User already exist", Toast.LENGTH_SHORT).show();

                            } else if (cibil_status.equalsIgnoreCase("success") && apicall.
                                    equalsIgnoreCase("cibil-customer-assets")) {
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                customerassets();

                            } else if (cibil_status.equalsIgnoreCase("Inprogress") && apicall.
                                    equalsIgnoreCase("cibil-authentication-questions")
                                    || (cibil_status.equalsIgnoreCase("Pending") && apicall.
                                    equalsIgnoreCase("cibil-authentication-questions"))) {
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                authenticateuestionasync();

                            } else if (cibil_status.equalsIgnoreCase("failed")) {
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                String message = "";
                                try {
                                    message = jsonObject1.getString("message");
                                } catch (Exception e) {
                                    JSONObject jsonObject2 = jsonObject1.getJSONObject("RequestError");
                                    message = jsonObject2.getString("NO_HIT");
                                }

                                Toast.makeText(Profilepage.this, message, Toast.LENGTH_SHORT).show();
                            } else {
                                JSONObject jsonObjectresult = new JSONObject(jsonObject.getJSONObject("result").toString());

                                if (jsonObjectresult.getString("cibil_score").equalsIgnoreCase("0") ||
                                        jsonObjectresult.getString("cibil_score").equalsIgnoreCase("1")) {

                                    Intent intent1 = new Intent(Profilepage.this, Dashboard.class);
                                    startActivity(intent1);
                                    finish();


//                                    if (progressDialog != null && progressDialog.isShowing()) {
//                                        progressDialog.dismiss();
//                                    }
//                                    Objects.requireNonNull(dialog2.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//
//                                    dialog2.setContentView(R.layout.wishfinapplycreditcardbox);
//
//                                    TextView getcreditcard = dialog2.findViewById(R.id.getcreditcard);
//                                    TextView logout = dialog2.findViewById(R.id.logout);
//
//                                    logout.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                            Objects.requireNonNull(dialog3.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//                                            dialog3.setContentView(R.layout.logout);
//                                            dialog3.show();
//                                            TextView cancle = dialog3.findViewById(R.id.cancle);
//                                            TextView ok = dialog3.findViewById(R.id.ok);
//
//                                            cancle.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//                                                    dialog3.dismiss();
//                                                }
//                                            });
//
//                                            ok.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//
////                                                    wishFinAnalytics.loggedOut();
//                                                    SessionManager.dataclear(prefs);
//                                                    dialog3.dismiss();
//                                                    dialog2.dismiss();
//                                                    Intent intent1 = new Intent(Profilepage.this, LoginPage.class);
//                                                    startActivity(intent1);
//                                                    finish();
//
//                                                }
//                                            });
//                                        }
//                                    });
//
//                                    getcreditcard.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.wishfin.com/credit-cards"));
//                                            startActivity(browserIntent);
//
//                                        }
//                                    });
//
//                                    dialog2.show();
//
//                                } else if (jsonObjectresult.isNull("cibil_id") || jsonObjectresult.getString("cibil_score").equalsIgnoreCase("-1")) {
//
//                                    if (progressDialog != null && progressDialog.isShowing()) {
//                                        progressDialog.dismiss();
//                                    }
//                                    Objects.requireNonNull(dialog2.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//
//                                    dialog2.setContentView(R.layout.wishfinupdatedetaildbox);
//
//                                    TextView editdetails = dialog2.findViewById(R.id.btnedit);
//                                    TextView logout = dialog2.findViewById(R.id.logout);
//
//                                    logout.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                            Objects.requireNonNull(dialog3.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//                                            dialog3.setContentView(R.layout.logout);
//                                            dialog3.show();
//                                            TextView cancle = dialog3.findViewById(R.id.cancle);
//                                            TextView ok = dialog3.findViewById(R.id.ok);
//
//                                            cancle.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//                                                    dialog3.dismiss();
//                                                }
//                                            });
//
//                                            ok.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//
////                                                    wishFinAnalytics.loggedOut();
//                                                    SessionManager.dataclear(prefs);
//                                                    dialog3.dismiss();
//                                                    dialog2.dismiss();
//                                                    Intent intent1 = new Intent(Dashboardpage.this, LoginPage.class);
//                                                    startActivity(intent1);
//                                                    finish();
//
//                                                }
//                                            });
//                                        }
//                                    });
//
//                                    editdetails.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                            dialog2.dismiss();
//
////                                            dialog2.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//
//                                            dialog2.setContentView(R.layout.wishfinfilldetailtbox);
//                                            fname = dialog2.findViewById(R.id.fname);
//                                            input_layout_fname = dialog2.findViewById(R.id.input_layout_fname);
//                                            monthly_income = dialog2.findViewById(R.id.monthly_income);
//                                            input_layout_monthly_income = dialog2.findViewById(R.id.input_layout_monthly_income);
//                                            selfemployedcheck = dialog2.findViewById(R.id.selfemployedcheck);
//                                            salariedcheck = dialog2.findViewById(R.id.salariedcheck);
//                                            mname = dialog2.findViewById(R.id.mname);
//                                            input_layout_mname = dialog2.findViewById(R.id.input_layout_mname);
//                                            lname = dialog2.findViewById(R.id.lname);
//                                            input_layout_lname = dialog2.findViewById(R.id.input_layout_lname);
//                                            email = dialog2.findViewById(R.id.emailid);
//                                            input_layout_email = dialog2.findViewById(R.id.input_layout_emailid);
//                                            pan = dialog2.findViewById(R.id.pan);
//                                            dob = dialog2.findViewById(R.id.dob);
//                                            input_layout_dob = dialog2.findViewById(R.id.input_layout_dob);
//                                            dob.setOnClickListener(v1 -> DatePicdob());
//
//                                            if (SessionManager.get_occupation(prefs).equalsIgnoreCase("1")) {
//                                                str_occupation = "1";
//                                                salariedcheck.setChecked(true);
//                                            } else if (SessionManager.get_occupation(prefs).equalsIgnoreCase("2")) {
//                                                str_occupation = "2";
//                                                selfemployedcheck.setChecked(true);
//                                            }
//
//                                            selfemployedcheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                                                if (isChecked) {
//                                                    str_occupation = "2";
//
//                                                }
//                                            });
//
//                                            salariedcheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                                                if (isChecked) {
//                                                    str_occupation = "1";
//
//                                                }
//                                            });
//
//                                            fname.addTextChangedListener(new Dashboardpage.MyTextWatcher(fname));
//                                            mname.addTextChangedListener(new Dashboardpage.MyTextWatcher(mname));
//                                            lname.addTextChangedListener(new Dashboardpage.MyTextWatcher(lname));
//                                            email.addTextChangedListener(new Dashboardpage.MyTextWatcher(email));
//                                            pan.addTextChangedListener(new Dashboardpage.MyTextWatcher(pan));
//                                            monthly_income.addTextChangedListener(new Dashboardpage.MyTextWatcher(monthly_income));
//                                            fname.setText(SessionManager.get_firstname(prefs));
//                                            mname.setText(SessionManager.get_mname(prefs));
//                                            lname.setText(SessionManager.get_lastname(prefs));
//                                            email.setText(SessionManager.get_emailid(prefs));
//                                            pan.setText(SessionManager.get_pan(prefs));
//                                            monthly_income.setText(SessionManager.get_monthly_income(prefs));
//                                            if (!SessionManager.get_dob(prefs).equalsIgnoreCase("0000-00-00")) {
//                                                dob.setText(SessionManager.get_dob(prefs));
//                                            }
//
//                                            TextView submit = dialog2.findViewById(R.id.signuptwo);
//
//                                            submit.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View view) {
//
//                                                    if (isThereInternetConnection()) {
//                                                        submitFormtwo();
//                                                    } else {
//                                                        Toast.makeText(Dashboardpage.this, getString(R.string.checkinternet), Toast.LENGTH_LONG).show();
//
//                                                    }
//
//                                                }
//
//                                            });
//
//                                            dialog2.show();
//                                        }
//                                    });
//
//                                    dialog2.show();
                                } else {
                                    cibilscore.setText(jsonObjectresult.getString("cibil_score"));
                                    cibildisplay.setVisibility(View.VISIBLE);
                                    refreshscore.setVisibility(View.GONE);
                                    checkcibil.setVisibility(View.GONE);
//                                    pb.setVisibility(View.VISIBLE);
//                                    startAnimation(Integer.parseInt(jsonObjectresult.getString("cibil_score")));
                                    SessionManager.save_logintype(prefs, "Login");
                                    Constants.cardresponse = "";
                                    Constants.hardinquiryresponse = "";
                                    Constants.ontimepaymentresponse = "";
                                    Constants.ontimepaymentresponse = "";
                                    Constants.loanresponse = "";
                                    SessionManager.save_cibil_id(prefs, String.valueOf(jsonObjectresult.getString("cibil_id")));
                                    SessionManager.save_cibil_score(prefs, String.valueOf(jsonObjectresult.getString("cibil_score")));
                                    SessionManager.save_cibil_fetch_date(prefs, String.valueOf(jsonObjectresult.getString("cibil_score_fetch_date")));
//                                    String cibilfetchdate = "Last updated on " + coverteddate(SessionManager.get_cibil_fetch_date(prefs));
//                                    cibil_fetch_date.setText(cibilfetchdate);
                                    if (progressDialog != null && progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
//                                    if (type.equalsIgnoreCase("normal")) {
                                    if (progressDialog != null) {
                                        progressDialog.show();
                                    }
//                                    get_cibil_credit_factors();
//                                    } else {
//                                        if (progressDialog != null) {
//                                            progressDialog.show();
//                                        }
//                                        updateuserdetails();
//                                    }
                                }
                            }

                        } else if (jsonObject.getString("status").equalsIgnoreCase("failed")) {

                            try {

                                JSONObject jsonObject1 = jsonObject.getJSONObject("message");
                                JSONObject jsonObject2 = jsonObject1.getJSONObject("RequestError");

                                if (jsonObject2.getString("NO_HIT").equalsIgnoreCase("Bureau error:ERROR: Express NO HIT Subject")) {

                                    if (progressDialog != null && progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                    Objects.requireNonNull(dialog2.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

                                    dialog2.setContentView(R.layout.wishfinupdatedetaildbox);

                                    TextView editdetails = dialog2.findViewById(R.id.btnedit);
                                    TextView logout = dialog2.findViewById(R.id.logout);

                                    logout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog2.dismiss();
                                        }
                                    });

                                    editdetails.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            dialog2.dismiss();

//                                            dialog2.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

                                            dialog2.setContentView(R.layout.wishfinfilldetailtbox);
                                            fname = dialog2.findViewById(R.id.fname);
                                            input_layout_fname = dialog2.findViewById(R.id.input_layout_fname);
                                            monthly_income = dialog2.findViewById(R.id.monthly_income);
                                            input_layout_monthly_income = dialog2.findViewById(R.id.input_layout_monthly_income);
                                            selfemployedcheck = dialog2.findViewById(R.id.selfemployedcheck);
                                            salariedcheck = dialog2.findViewById(R.id.salariedcheck);
                                            mname = dialog2.findViewById(R.id.mname);
                                            input_layout_mname = dialog2.findViewById(R.id.input_layout_mname);
                                            lname = dialog2.findViewById(R.id.lname);
                                            input_layout_lname = dialog2.findViewById(R.id.input_layout_lname);
                                            email = dialog2.findViewById(R.id.emailid);
                                            input_layout_email = dialog2.findViewById(R.id.input_layout_emailid);
                                            pan = dialog2.findViewById(R.id.pan);
                                            input_layout_pan = dialog2.findViewById(R.id.input_layout_pan);
                                            dob = dialog2.findViewById(R.id.dob);
                                            input_layout_dob = dialog2.findViewById(R.id.input_layout_dob);
                                            dob.setOnClickListener(v1 -> DatePicdob());

                                            if (SessionManager.get_occupation(prefs).equalsIgnoreCase("1")) {
                                                str_occupation = "1";
                                                salariedcheck.setChecked(true);
                                            } else if (SessionManager.get_occupation(prefs).equalsIgnoreCase("2")) {
                                                str_occupation = "2";
                                                selfemployedcheck.setChecked(true);
                                            }

                                            selfemployedcheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                                if (isChecked) {
                                                    str_occupation = "2";

                                                }
                                            });

                                            salariedcheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                                if (isChecked) {
                                                    str_occupation = "1";

                                                }
                                            });

                                            fname.addTextChangedListener(new MyTextWatcher(fname));
                                            mname.addTextChangedListener(new MyTextWatcher(mname));
                                            lname.addTextChangedListener(new MyTextWatcher(lname));
                                            email.addTextChangedListener(new MyTextWatcher(email));
                                            pan.addTextChangedListener(new MyTextWatcher(pan));
                                            monthly_income.addTextChangedListener(new MyTextWatcher(monthly_income));
                                            fname.setText(SessionManager.get_firstname(prefs));
                                            mname.setText(SessionManager.get_mname(prefs));
                                            lname.setText(SessionManager.get_lastname(prefs));
                                            email.setText(SessionManager.get_emailid(prefs));
                                            pan.setText(SessionManager.get_pan(prefs));
                                            monthly_income.setText(SessionManager.get_monthly_income(prefs));
                                            if (!SessionManager.get_dob(prefs).equalsIgnoreCase("0000-00-00")) {
                                                dob.setText(SessionManager.get_dob(prefs));
                                            }

                                            TextView submit = dialog2.findViewById(R.id.signuptwo);

                                            submit.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    if (isThereInternetConnection()) {
                                                        submitFormtwo();
                                                    } else {
                                                        Toast.makeText(Profilepage.this, getString(R.string.checkinternet), Toast.LENGTH_LONG).show();

                                                    }

                                                }

                                            });

                                            dialog2.show();
                                        }
                                    });

                                    dialog2.show();
                                }


                            } catch (Exception e) {
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                showerrordialog();
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
                if (statusCode == 422) {
                    try {
                        String string = new String(error.networkResponse.data);
                        JSONObject jsonObject = new JSONObject(string);

                        if (jsonObject.getString("detail").equalsIgnoreCase("Failed Validation")) {

                            JSONObject jsonObject1 = jsonObject.getJSONObject("validation_messages");
                            try {
//                                historytxt.setVisibility(View.GONE);
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                /*
                                JSONArray jsonArray = jsonObject1.getJSONArray("email_id");
                                Toast.makeText(this, "Please provide valid email id", Toast.LENGTH_LONG).show();

                                Objects.requireNonNull(dialog2.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

                                dialog2.setContentView(R.layout.wishfinfilldetailtbox);
                                fname = dialog2.findViewById(R.id.fname);
                                input_layout_fname = dialog2.findViewById(R.id.input_layout_fname);
                                mname = dialog2.findViewById(R.id.mname);
                                monthly_income = dialog2.findViewById(R.id.monthly_income);
                                input_layout_monthly_income = dialog2.findViewById(R.id.input_layout_monthly_income);
                                selfemployedcheck = dialog2.findViewById(R.id.selfemployedcheck);
                                salariedcheck = dialog2.findViewById(R.id.salariedcheck);
                                input_layout_mname = dialog2.findViewById(R.id.input_layout_mname);
                                lname = dialog2.findViewById(R.id.lname);
                                input_layout_lname = dialog2.findViewById(R.id.input_layout_lname);
                                email = dialog2.findViewById(R.id.emailid);
                                input_layout_email = dialog2.findViewById(R.id.input_layout_emailid);
                                pan = dialog2.findViewById(R.id.pan);
                                dob = dialog2.findViewById(R.id.dob);
                                input_layout_dob = dialog2.findViewById(R.id.input_layout_dob);

                                dob.setOnClickListener(v1 -> DatePicdob());
                                if (SessionManager.get_occupation(prefs).equalsIgnoreCase("1")) {
                                    str_occupation = "1";
                                    salariedcheck.setChecked(true);
                                } else if (SessionManager.get_occupation(prefs).equalsIgnoreCase("2")) {
                                    str_occupation = "2";
                                    selfemployedcheck.setChecked(true);
                                }
                                selfemployedcheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                    if (isChecked) {
                                        str_occupation = "2";

                                    }
                                });

                                salariedcheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                    if (isChecked) {
                                        str_occupation = "1";

                                    }
                                });

                                fname.addTextChangedListener(new Dashboardpage.MyTextWatcher(fname));
                                mname.addTextChangedListener(new Dashboardpage.MyTextWatcher(mname));
                                lname.addTextChangedListener(new Dashboardpage.MyTextWatcher(lname));
                                email.addTextChangedListener(new Dashboardpage.MyTextWatcher(email));
                                pan.addTextChangedListener(new Dashboardpage.MyTextWatcher(pan));
                                monthly_income.addTextChangedListener(new Dashboardpage.MyTextWatcher(monthly_income));

                                fname.setText(SessionManager.get_firstname(prefs));
                                mname.setText(SessionManager.get_mname(prefs));
                                lname.setText(SessionManager.get_lastname(prefs));
                                email.setText(SessionManager.get_emailid(prefs));
                                pan.setText(SessionManager.get_pan(prefs));
                                monthly_income.setText(SessionManager.get_monthly_income(prefs));
                                if (!SessionManager.get_dob(prefs).equalsIgnoreCase("0000-00-00")) {
                                    dob.setText(SessionManager.get_dob(prefs));
                                }

                                TextView submit = dialog2.findViewById(R.id.signuptwo);

                                submit.setOnClickListener(view -> {

                                    dialog2.dismiss();
                                    if (isThereInternetConnection()) {
                                        submitFormtwo();
                                    } else {
                                        Toast.makeText(Dashboardpage.this, getString(R.string.checkinternet), Toast.LENGTH_LONG).show();

                                    }

                                });

                                dialog2.show();
*/
                            } catch (Exception e) {
//                                historytxt.setVisibility(View.GONE);
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                showerrordialog();
                            }

                        }
                    } catch (Exception ignored) {

                    }
                }
                error.printStackTrace();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ) {
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
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
    }

    private void showerrordialog() {

        dialog.setContentView(R.layout.wishfinalertbox);
        Button submit = dialog.findViewById(R.id.btnsubmit);
        Button logout = dialog.findViewById(R.id.btnlogout);
        TextView head = dialog.findViewById(R.id.heading);
        head.setClickable(true);
        head.setMovementMethod(LinkMovementMethod.getInstance());
        String two = "<a href='https://www.wishfin.com/cibil-score'> wishfin.com </a>";
        String one = "Your cibil can't be generated now due to some technical error.Please try again later or visit ";
        Spanned s = (Html.fromHtml(one + two + " to check your CIBIL score."));
        head.setText(s);
        cibilscore.setText("N/A");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                if (progressDialog != null) {
                    progressDialog.show();
                }
                get_cibil_user_detail();

            }

        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                dialog.dismiss();
            }

        });

        dialog.show();

    }

    private void showerrordialogCC() {

        dialog.setContentView(R.layout.wishfinalertbox);
        Button submit = dialog.findViewById(R.id.btnsubmit);
        Button logout = dialog.findViewById(R.id.btnlogout);
        TextView head = dialog.findViewById(R.id.heading);
        submit.setText("Ok");
        logout.setText("Cancel");
        head.setClickable(true);
        head.setMovementMethod(LinkMovementMethod.getInstance());
        String one = "Your cibil can't be generated since you do not have any credit card.Download our App and Apply for Credit card to get your Cibil score. ";
        head.setText(one);
        cibilscore.setText("N/A");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                if (progressDialog != null) {
                    progressDialog.show();
                }
                final String appPackageName = getPackageName();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));

            }

        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }

        });

        dialog.show();

    }

    public void get_cibil_user_detail() {

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = BuildConfig.BASE_URL + "/v1/get-cibil-user-detail?master_user_id=" + SessionManager.get_masteruserid(prefs) + "&mf_user_id=" + SessionManager.get_mfuserid(prefs);
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // response

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        {
                            if (!jsonObject.getString("status").equalsIgnoreCase("failed")) {

                                JSONObject jsonObject1 = jsonObject.getJSONObject("result");

                                if (jsonObject1.getString("cibil_score").equalsIgnoreCase("0") || jsonObject1.getString("cibil_score").equalsIgnoreCase("1")) {
                                    Intent intent1 = new Intent(Profilepage.this, Dashboard.class);
                                    startActivity(intent1);
                                    finish();

                                }
//
//                                    if (progressDialog != null && progressDialog.isShowing()) {
//                                        progressDialog.dismiss();
//                                    }
//
//                                    Objects.requireNonNull(dialog2.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//
//                                    dialog2.setContentView(R.layout.wishfinapplycreditcardbox);
//
//                                    TextView getcreditcard = dialog2.findViewById(R.id.getcreditcard);
//                                    TextView logout = dialog2.findViewById(R.id.logout);
//
//                                    logout.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                            Objects.requireNonNull(dialog3.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//                                            dialog3.setContentView(R.layout.logout);
//                                            dialog3.show();
//                                            TextView cancle = dialog3.findViewById(R.id.cancle);
//                                            TextView ok = dialog3.findViewById(R.id.ok);
//
//                                            cancle.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//                                                    dialog3.dismiss();
//                                                }
//                                            });
//
//                                            ok.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//
//                                                    wishFinAnalytics.loggedOut();
//                                                    SessionManager.dataclear(prefs);
//                                                    dialog3.dismiss();
//                                                    dialog2.dismiss();
//                                                    Intent intent1 = new Intent(Dashboardpage.this, LoginPage.class);
//                                                    startActivity(intent1);
//                                                    finish();
//
//                                                }
//                                            });
//                                        }
//                                    });
//                                    getcreditcard.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.wishfin.com/credit-cards"));
//                                            startActivity(browserIntent);
//
//                                        }
//                                    });
//
//                                    dialog2.show();
//
//                                }
                                else if (jsonObject1.isNull("cibil_id") || jsonObject1.getString("cibil_score").equalsIgnoreCase("-1")) {

                                    if (progressDialog != null && progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                    try {
                                        int income = Integer.parseInt(jsonObject1.getString("annual_income")) / 12;
                                        SessionManager.save_monthly_income(prefs, "" + income);
                                        SessionManager.save_occupation(prefs, "" + jsonObject1.getString("occupation"));
                                    } catch (Exception e) {
                                        SessionManager.save_monthly_income(prefs, "1");
                                        SessionManager.save_occupation(prefs, "1");
                                    }

                                    Objects.requireNonNull(dialog2.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

                                    dialog2.setContentView(R.layout.wishfinupdatedetaildbox);

                                    TextView editdetails = dialog2.findViewById(R.id.btnedit);
                                    TextView logout = dialog2.findViewById(R.id.logout);
                                    logout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            dialog2.dismiss();
                                        }
                                    });
                                    editdetails.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            dialog2.dismiss();
                                            Objects.requireNonNull(dialog2.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

                                            dialog2.setContentView(R.layout.wishfinfilldetailtbox);
                                            fname = dialog2.findViewById(R.id.fname);
                                            input_layout_fname = dialog2.findViewById(R.id.input_layout_fname);
                                            mname = dialog2.findViewById(R.id.mname);
                                            monthly_income = dialog2.findViewById(R.id.monthly_income);
                                            input_layout_monthly_income = dialog2.findViewById(R.id.input_layout_monthly_income);
                                            selfemployedcheck = dialog2.findViewById(R.id.selfemployedcheck);
                                            salariedcheck = dialog2.findViewById(R.id.salariedcheck);
                                            input_layout_mname = dialog2.findViewById(R.id.input_layout_mname);
                                            lname = dialog2.findViewById(R.id.lname);
                                            input_layout_lname = dialog2.findViewById(R.id.input_layout_lname);
                                            email = dialog2.findViewById(R.id.emailid);
                                            input_layout_email = dialog2.findViewById(R.id.input_layout_emailid);
                                            pan = dialog2.findViewById(R.id.pan);
                                            input_layout_pan = dialog2.findViewById(R.id.input_layout_pan);
                                            dob = dialog2.findViewById(R.id.dob);
                                            input_layout_dob = dialog2.findViewById(R.id.input_layout_dob);

                                            dob.setOnClickListener(v1 -> DatePicdob());
                                            if (SessionManager.get_occupation(prefs).equalsIgnoreCase("1")) {
                                                str_occupation = "1";
                                                salariedcheck.setChecked(true);
                                            } else if (SessionManager.get_occupation(prefs).equalsIgnoreCase("2")) {
                                                str_occupation = "2";
                                                selfemployedcheck.setChecked(true);
                                            }
                                            selfemployedcheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                                if (isChecked) {
                                                    str_occupation = "2";

                                                }
                                            });

                                            salariedcheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                                if (isChecked) {
                                                    str_occupation = "1";

                                                }
                                            });

                                            fname.addTextChangedListener(new MyTextWatcher(fname));
                                            mname.addTextChangedListener(new MyTextWatcher(mname));
                                            lname.addTextChangedListener(new MyTextWatcher(lname));
                                            email.addTextChangedListener(new MyTextWatcher(email));
                                            pan.addTextChangedListener(new MyTextWatcher(pan));
                                            monthly_income.addTextChangedListener(new MyTextWatcher(monthly_income));

                                            fname.setText(SessionManager.get_firstname(prefs));
                                            mname.setText(SessionManager.get_mname(prefs));
                                            lname.setText(SessionManager.get_lastname(prefs));
                                            email.setText(SessionManager.get_emailid(prefs));
                                            pan.setText(SessionManager.get_pan(prefs));
                                            monthly_income.setText(SessionManager.get_monthly_income(prefs));

                                            if (!SessionManager.get_dob(prefs).equalsIgnoreCase("0000-00-00")) {
                                                dob.setText(SessionManager.get_dob(prefs));
                                            }

                                            TextView submit = dialog2.findViewById(R.id.signuptwo);

                                            submit.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    if (isThereInternetConnection()) {
                                                        submitFormtwo();
                                                    } else {
                                                        Toast.makeText(Profilepage.this, getString(R.string.checkinternet), Toast.LENGTH_LONG).show();

                                                    }

                                                }

                                            });

                                            dialog2.show();
                                        }
                                    });

                                    dialog2.show();
                                }
//                                else {
                                else if (jsonObject1.getString("cibil_status").equalsIgnoreCase("Success")) {

                                    String date = jsonObject1.getString("cibil_score_fetch_date");
//                                        pb.setVisibility(View.VISIBLE);
//                                        String cibildate = "Last updated on " + coverteddate(date);
//                                        cibil_fetch_date.setText(cibildate);
                                    SessionManager.save_cibil_fetch_date(prefs, date);
                                    SessionManager.save_cibil_id(prefs, jsonObject1.getString("cibil_id"));
                                    SessionManager.save_cibil_score(prefs, jsonObject1.getString("cibil_score"));
                                    cibilscore.setText(jsonObject1.getString("cibil_score"));
//                                        startAnimation(Integer.parseInt(jsonObject1.getString("cibil_score")));

                                    long differenceDates = 0;
                                    try {
                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat dates = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                        Date date1;
                                        Date date2;
                                        date1 = dates.parse(updated_time);
                                        date2 = dates.parse(jsonObject1.getString("cibil_score_fetch_date"));
                                        long difference = Math.abs(date1.getTime() - date2.getTime());
                                        differenceDates = difference / (24 * 60 * 60 * 1000);
                                    } catch (Exception e) {

                                    }

//                                    get_cibil_credit_factors();

                                    if (progressDialog != null && progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                    if (differenceDates > 30) {

                                        Constants.refreshclick = "true";
                                        Constants.updateavailable = "Click Refresh to see your updated score";
                                        checkcibil.setVisibility(View.GONE);
                                        cibildisplay.setVisibility(View.GONE);
                                        refreshscore.setVisibility(View.VISIBLE);
//                                            noproceed.setText(Constants.updateavailable);
//                                            yesproceed.setBackgroundResource(R.drawable.roundedbuttonrefreshblue);
//                                            refreshlayout.setVisibility(View.VISIBLE);
                                    } else {

                                        int remainingdays = (int) (31 - differenceDates);
                                        Constants.refreshclick = "false";
                                        Toast.makeText(getApplicationContext(), "Next score update will be in " + remainingdays + " Days", Toast.LENGTH_LONG).show();
                                        Constants.updateavailable = "Next score update will be in " + remainingdays + " Days";
                                        checkcibil.setVisibility(View.GONE);
                                        cibildisplay.setVisibility(View.VISIBLE);
                                        refreshscore.setVisibility(View.GONE);
//                                            noproceed.setText(Constants.updateavailable);
//                                            yesproceed.setBackgroundResource(R.drawable.roundedbuttonrefreshgrey);
//                                            refreshlayout.setVisibility(View.VISIBLE);

                                    }
                                } else {
                                    get_cibil_fulfill_order("normal");
                                }

//                                }

                            } else {
                                get_cibil_fulfill_order("normal");
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

    public void get_cibil_credit_factors() {

        RequestQueue queue = Volley.newRequestQueue(this);

//        String url = Constants.BASE_URL + "/get-cibil-credit-factors/3061998";
        String url = BuildConfig.BASE_URL + "/get-cibil-credit-factors/" + SessionManager.get_cibil_id(prefs);
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // response
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                            JSONObject jsonObjectresult1 = new JSONObject(jsonObject.getJSONObject("result").toString());
                            JSONObject jsonObjectresult = jsonObjectresult1.getJSONObject("userInfo");

                            SessionManager.save_cibil_score(prefs, String.valueOf(jsonObjectresult.getString("cibil_score")));
                            cibilscore.setText(jsonObjectresult.getString("cibil_score"));
                            SessionManager.save_cibil_id(prefs, String.valueOf(jsonObjectresult.getString("cibil_id")));
                            SessionManager.save_cibil_score(prefs, String.valueOf(jsonObjectresult.getString("cibil_score")));
                            SessionManager.save_cibil_fetch_date(prefs, String.valueOf(jsonObjectresult.getString("cibil_score_fetch_date")));
                            cibildisplay.setVisibility(View.VISIBLE);
                            refreshscore.setVisibility(View.GONE);
                            checkcibil.setVisibility(View.GONE);
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

    public void authenticateuestionasync() {
        if (progressDialog != null) {
            progressDialog.show();
        }
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = BuildConfig.BASE_URL + "/v1/cibil-authentication-questions/" + SessionManager.get_cibil_id(prefs);
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // response
                    try {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        JSONObject jsonObject = new JSONObject(response);
                        {
                            JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                            String cibil_status = jsonObject1.getString("cibil_status");
                            String apicall = "";
                            try {
                                apicall = jsonObject1.getString("next_api_call");
                            } catch (Exception e) {

                            }
                            if (cibil_status.equalsIgnoreCase("Success") && apicall.
                                    equalsIgnoreCase("cibil-customer-assets")) {
                                customerassets();
                            } else if ((cibil_status.equalsIgnoreCase("InProgress") || cibil_status.equalsIgnoreCase("Pending")) && apicall.
                                    equalsIgnoreCase("cibil-verify-answers")) {

                                try {
                                    JSONObject jsonObject2 = jsonObject1.getJSONObject("questions");
                                    Objects.requireNonNull(dialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

                                    dialog.setContentView(R.layout.singlequestionalert);
                                    TextView text = dialog.findViewById(R.id.txt_dia);
                                    text.setText(jsonObject2.getString("FullQuestionText"));

                                    JSONObject jsonObject3 = jsonObject2.getJSONObject("AnswerChoice");

                                    Button submit = dialog.findViewById(R.id.btnsubmit);
                                    Button skip = dialog.findViewById(R.id.btn_skip);
                                    EditText inputanswer = dialog.findViewById(R.id.inputanswer);

                                    String questionkey = jsonObject2.getString("Key");
                                    String skipeligible = jsonObject2.getString("skipEligible");
                                    String answerkey = jsonObject3.getString("AnswerChoiceId");

                                    if (jsonObject2.getString("FullQuestionText").contains("email")) {
                                        skip.setText("Skip");
                                    } else {
                                        skip.setText("Resend");
                                    }
                                    singlequestion = "true";
                                    multiplequestion = "true";
                                    submit.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            if (!inputanswer.getText().toString().trim().equalsIgnoreCase("")) {
                                                hideSoftKeyboard();
                                                dialog.dismiss();
                                                JSONObject jsonObject = new JSONObject();
                                                try {
                                                    jsonObject.put("cibil_id", "" + SessionManager.get_cibil_id(prefs));

                                                    JSONArray jsonArray = new JSONArray();
                                                    JSONObject object = new JSONObject();

                                                    try {
                                                        object.put("question_key", "" + questionkey);
                                                        object.put("answer_key", "" + answerkey);
                                                        object.put("user_input_answer", "" + inputanswer.getText().toString());
                                                        object.put("skip", "false");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    jsonArray.put(object);
                                                    jsonObject.put("answers", jsonArray);

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                verifyanswerasync(jsonObject);
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Value can't be empty", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                                    skip.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            hideSoftKeyboard();

                                            dialog.dismiss();
                                            JSONObject jsonObject = new JSONObject();
                                            try {
                                                jsonObject.put("cibil_id", "" + SessionManager.get_cibil_id(prefs));
                                                JSONArray jsonArray = new JSONArray();
                                                JSONObject object = new JSONObject();

                                                try {
                                                    object.put("question_key", "" + questionkey);
                                                    object.put("answer_key", "" + answerkey);
                                                    object.put("user_input_answer", "");
                                                    if (skip.getText().toString().equalsIgnoreCase("skip")) {
                                                        object.put("skip", "true");

                                                    } else {
                                                        object.put("resend", "true");
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                jsonArray.put(object);
                                                jsonObject.put("answers", jsonArray);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            verifyanswerasync(jsonObject);
                                        }
                                    });
                                    dialog.show();
                                } catch (Exception ignored) {

                                }
                                if (singlequestion.equalsIgnoreCase("false")) {
                                    try {
                                        JSONArray jsonObject2 = jsonObject1.getJSONArray("questions");
                                        Objects.requireNonNull(dialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

                                        dialog.setContentView(R.layout.multiplequestionadapter);
                                        ListView textone = dialog.findViewById(R.id.list);
                                        ArrayList<CibilQuestionanswergetset> catlist = new ArrayList<>();
                                        for (int k = 0; k < jsonObject2.length(); k++) {
                                            JSONObject objectnew2 = jsonObject2.getJSONObject(k);
                                            CibilQuestionanswergetset pack = new CibilQuestionanswergetset();
                                            pack.setQuestion((objectnew2.getString("FullQuestionText")));
                                            pack.setQuestionkey((objectnew2.getString("Key")));
                                            JSONObject jsonObject3 = objectnew2.getJSONObject("AnswerChoice");
                                            pack.setAnswerid(jsonObject3.getString("AnswerChoiceId"));
                                            pack.setAnswerkey(jsonObject3.getString("AnswerChoiceId"));
                                            pack.setAsnwer("");
                                            catlist.add(pack);
                                        }
                                        adapter = new CustomPagerAdapter(Profilepage.this, catlist);
                                        textone.setAdapter(adapter);
                                        singlequestion = "true";
                                        multiplequestion = "true";
                                        Button submit = (Button) dialog.findViewById(R.id.btnsubmit);
                                        Button skip = (Button) dialog.findViewById(R.id.btn_skip);
                                        submit.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String empty = "false";
                                                hideSoftKeyboard();
                                                dialog.dismiss();
                                                JSONObject jsonObject = new JSONObject();
                                                try {
                                                    jsonObject.put("cibil_id", "" + SessionManager.get_cibil_id(prefs));
                                                    JSONArray jsonArray = new JSONArray();

                                                    for (int i = 0; i < Constants.multipleinput_question_list.size(); i++) {
                                                        JSONObject object = new JSONObject();

                                                        if (Constants.multipleinput_question_list.get(i).getAsnwer().trim().equalsIgnoreCase("")) {
                                                            empty = "true";
                                                        }
                                                        try {
                                                            object.put("question_key", Constants.multipleinput_question_list.get(i).getQuestionkey());
                                                            object.put("answer_key", Constants.multipleinput_question_list.get(i).getAnswerkey());
                                                            object.put("user_input_answer", Constants.multipleinput_question_list.get(i).getAsnwer());
                                                            object.put("skip", "false");
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                        jsonArray.put(object);

                                                    }
                                                    jsonObject.put("answers", jsonArray);

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                if (empty.equalsIgnoreCase("true")) {
                                                    Toast.makeText(getApplicationContext(), "Value can't be empty", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    verifyanswerasync(jsonObject);
                                                }
                                            }
                                        });
                                        skip.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                hideSoftKeyboard();
                                                dialog.dismiss();
                                                JSONObject jsonObject = new JSONObject();
                                                try {
                                                    jsonObject.put("cibil_id", "" + SessionManager.get_cibil_id(prefs));
                                                    JSONArray jsonArray = new JSONArray();

                                                    for (int i = 0; i < Constants.multipleinput_question_list.size(); i++) {
                                                        JSONObject object = new JSONObject();

                                                        try {
                                                            object.put("question_key", Constants.multipleinput_question_list.get(i).getQuestionkey());
                                                            object.put("answer_key", Constants.multipleinput_question_list.get(i).getAnswerkey());
                                                            object.put("user_input_answer", "");
                                                            object.put("skip", "true");
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                        jsonArray.put(object);
                                                    }
                                                    jsonObject.put("answers", jsonArray);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                verifyanswerasync(jsonObject);
                                            }
                                        });
                                        dialog.show();
                                    } catch (Exception ignored) {
                                    }
                                }
                                if (multiplequestion.equalsIgnoreCase("false")) {
                                    try {
                                        JSONArray jsonObject2 = jsonObject1.getJSONArray("questions");

                                        Objects.requireNonNull(dialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

                                        dialog.setContentView(R.layout.threeeditablequestion);

                                        ExpandableListView textone = dialog.findViewById(R.id.list);

                                        ArrayList<CibilQuestionanswergetset> top250;
                                        ArrayList<CibilQuestionanswergetset> headercustom = new ArrayList<>();
                                        Constants.radio_question_list = new ArrayList<>();
                                        Constants.radio_option_list = new HashMap<>();

                                        for (int k = 0; k < jsonObject2.length(); k++) {

                                            JSONObject objectnew2 = jsonObject2.getJSONObject(k);
                                            CibilQuestionanswergetset pack = new CibilQuestionanswergetset();
                                            pack.setQuestion((objectnew2.getString("FullQuestionText")));
                                            pack.setQuestionkey((objectnew2.getString("Key")));
                                            headercustom.add(pack);
                                            JSONArray jsonObject5 = objectnew2.getJSONArray("AnswerChoice");
                                            Constants.radio_question_list.add(objectnew2.getString("FullQuestionText"));

                                            top250 = new ArrayList<>();

                                            for (int j = 0; j < jsonObject5.length(); j++) {

                                                JSONObject objectnew = jsonObject5.getJSONObject(j);
                                                CibilQuestionanswergetset optionpack = new CibilQuestionanswergetset();
                                                optionpack.setOptionone((objectnew.getString("AnswerChoiceText")));
                                                optionpack.setAnswerkey((objectnew.getString("AnswerChoiceId")));
                                                optionpack.setRadiostatus("false");

                                                top250.add(optionpack);

                                            }
                                            Constants.radio_option_list.put(Constants.radio_question_list.get(k), top250);
                                        }
                                        radio_question_list_adapter_expandable = new ExpandableListAdapter(Profilepage.this, Constants.radio_question_list, Constants.radio_option_list);
                                        textone.setAdapter(radio_question_list_adapter_expandable);

                                        Button submit = (Button) dialog.findViewById(R.id.btnsubmit);
                                        Button skip = (Button) dialog.findViewById(R.id.btn_skip);

                                        submit.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String empty = "false";
                                                hideSoftKeyboard();

                                                JSONObject jsonObject = new JSONObject();
                                                try {
                                                    jsonObject.put("cibil_id", "" + SessionManager.get_cibil_id(prefs));
                                                    JSONArray jsonArray = new JSONArray();

                                                    for (int i = 0; i < Constants.radio_question_list.size(); i++) {
                                                        JSONObject object = new JSONObject();
                                                        String selectedis = "false";
                                                        try {
                                                            object.put("question_key", headercustom.get(i).getQuestionkey());

                                                            for (int j = 0; j < Constants.radio_option_list.get(Constants.radio_question_list.get(i)).size(); j++) {

                                                                if (Constants.radio_option_list.get(Constants.radio_question_list.get(i)).get(j).getRadiostatus().equalsIgnoreCase("true")) {
                                                                    object.put("user_input_answer", Constants.radio_option_list.get(Constants.radio_question_list.get(i)).get(j).getOptionone());
                                                                    object.put("answer_key", Constants.radio_option_list.get(Constants.radio_question_list.get(i)).get(j).getAnswerkey());
                                                                    selectedis = "true";
                                                                }
                                                            }
                                                            object.put("skip", "false");

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                        jsonArray.put(object);

                                                        if (selectedis.equalsIgnoreCase("false")) {
                                                            empty = "true";
                                                            break;
                                                        }

                                                    }
                                                    jsonObject.put("answers", jsonArray);

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                if (empty.equalsIgnoreCase("true")) {
                                                    Toast.makeText(getApplicationContext(), "Please give answer of all question.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    dialog.dismiss();
                                                    verifyanswerasync(jsonObject);
                                                }
                                            }
                                        });
                                        skip.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                                hideSoftKeyboard();

                                                JSONObject jsonObject = new JSONObject();
                                                try {
                                                    jsonObject.put("cibil_id", "" + SessionManager.get_cibil_id(prefs));

                                                    JSONArray jsonArray = new JSONArray();

                                                    for (int i = 0; i < Constants.radio_question_list.size(); i++) {
                                                        JSONObject object = new JSONObject();

                                                        try {
                                                            object.put("question_key", headercustom.get(i).getQuestionkey());
                                                            object.put("user_input_answer", "");
                                                            object.put("answer_key", "");
                                                            object.put("skip", "true");

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                        jsonArray.put(object);

                                                    }
                                                    jsonObject.put("answers", jsonArray);

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                verifyanswerasync(jsonObject);
                                            }
                                        });
                                        dialog.show();
                                    } catch (Exception ignored) {
                                    }
                                }
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

    public void customerassets() {
        if (progressDialog != null) {
            progressDialog.show();
        }
        final JSONObject json = new JSONObject();
        try {
            json.put("cibil_id", SessionManager.get_cibil_id(prefs));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, BuildConfig.BASE_URL + "/v1/cibil-customer-assets", json,
                response -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());
                        {
                            String status = jsonObject.getString("status");

                            if (status.equalsIgnoreCase("success")) {

//                                wishFinAnalytics.cibilPage();

                                JSONObject jsonObjectresult = new JSONObject(jsonObject.getJSONObject("result").toString());

                                //code handeling for 0 and 1
                                if (jsonObjectresult.getString("cibil_score").equalsIgnoreCase("0") ||
                                        jsonObjectresult.getString("cibil_score").equalsIgnoreCase("1")) {

                                    showerrordialogCC();
//                                    Intent intent1 = new Intent(Profilepage.this, Dashboard.class);
//                                    startActivity(intent1);
//                                    finish();


//                                    if (progressDialog != null && progressDialog.isShowing()) {
//                                        progressDialog.dismiss();
//                                    }
//                                    Objects.requireNonNull(dialog2.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//
//                                    dialog2.setContentView(R.layout.wishfinapplycreditcardbox);
//
//                                    TextView getcreditcard = dialog2.findViewById(R.id.getcreditcard);
//                                    TextView logout = dialog2.findViewById(R.id.logout);
//
//                                    logout.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                            Objects.requireNonNull(dialog3.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//                                            dialog3.setContentView(R.layout.logout);
//                                            dialog3.show();
//                                            TextView cancle = dialog3.findViewById(R.id.cancle);
//                                            TextView ok = dialog3.findViewById(R.id.ok);
//
//                                            cancle.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//                                                    dialog3.dismiss();
//                                                }
//                                            });
//
//                                            ok.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//
//                                                    wishFinAnalytics.loggedOut();
//                                                    SessionManager.dataclear(prefs);
//                                                    dialog3.dismiss();
//                                                    dialog2.dismiss();
//                                                    Intent intent1 = new Intent(Dashboardpage.this, LoginPage.class);
//                                                    startActivity(intent1);
//                                                    finish();
//
//                                                }
//                                            });
//                                        }
//                                    });
//                                    getcreditcard.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.wishfin.com/credit-cards"));
//                                            startActivity(browserIntent);
//
//                                        }
//                                    });
//
//                                    dialog2.show();

                                } else if (jsonObjectresult.isNull("cibil_id") || jsonObjectresult.getString("cibil_score").equalsIgnoreCase("-1")) {
//
                                    showerrordialog();
//                                    if (progressDialog != null && progressDialog.isShowing()) {
//                                        progressDialog.dismiss();
//                                    }
//                                    Objects.requireNonNull(dialog2.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//
//                                    dialog2.setContentView(R.layout.wishfinupdatedetaildbox);
//
//                                    TextView editdetails = dialog2.findViewById(R.id.btnedit);
//                                    TextView logout = dialog2.findViewById(R.id.logout);
//
//                                    logout.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                            Objects.requireNonNull(dialog3.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//                                            dialog3.setContentView(R.layout.logout);
//                                            dialog3.show();
//                                            TextView cancle = dialog3.findViewById(R.id.cancle);
//                                            TextView ok = dialog3.findViewById(R.id.ok);
//
//                                            cancle.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//                                                    dialog3.dismiss();
//                                                }
//                                            });
//
//                                            ok.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//
//                                                    wishFinAnalytics.loggedOut();
//                                                    SessionManager.dataclear(prefs);
//                                                    dialog3.dismiss();
//                                                    dialog2.dismiss();
//                                                    Intent intent1 = new Intent(Dashboardpage.this, LoginPage.class);
//                                                    startActivity(intent1);
//                                                    finish();
//
//                                                }
//                                            });
//                                        }
//                                    });
//                                    editdetails.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                            dialog2.dismiss();
////                                            dialog2.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//
//                                            dialog2.setContentView(R.layout.wishfinfilldetailtbox);
//                                            fname = dialog2.findViewById(R.id.fname);
//                                            input_layout_fname = dialog2.findViewById(R.id.input_layout_fname);
//                                            mname = dialog2.findViewById(R.id.mname);
//                                            monthly_income = dialog2.findViewById(R.id.monthly_income);
//                                            input_layout_monthly_income = dialog2.findViewById(R.id.input_layout_monthly_income);
//                                            selfemployedcheck = dialog2.findViewById(R.id.selfemployedcheck);
//                                            salariedcheck = dialog2.findViewById(R.id.salariedcheck);
//                                            input_layout_mname = dialog2.findViewById(R.id.input_layout_mname);
//                                            lname = dialog2.findViewById(R.id.lname);
//                                            input_layout_lname = dialog2.findViewById(R.id.input_layout_lname);
//                                            email = dialog2.findViewById(R.id.emailid);
//                                            input_layout_email = dialog2.findViewById(R.id.input_layout_emailid);
//                                            pan = dialog2.findViewById(R.id.pan);
//                                            dob = dialog2.findViewById(R.id.dob);
//                                            input_layout_dob = dialog2.findViewById(R.id.input_layout_dob);
//
//                                            dob.setOnClickListener(v1 -> DatePicdob());
//                                            if (SessionManager.get_occupation(prefs).equalsIgnoreCase("1")) {
//                                                str_occupation = "1";
//                                                salariedcheck.setChecked(true);
//                                            } else if (SessionManager.get_occupation(prefs).equalsIgnoreCase("2")) {
//                                                str_occupation = "2";
//                                                selfemployedcheck.setChecked(true);
//                                            }
//                                            selfemployedcheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                                                if (isChecked) {
//                                                    str_occupation = "2";
//
//                                                }
//                                            });
//
//                                            salariedcheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                                                if (isChecked) {
//                                                    str_occupation = "1";
//
//                                                }
//                                            });
//
//                                            fname.addTextChangedListener(new Dashboardpage.MyTextWatcher(fname));
//                                            mname.addTextChangedListener(new Dashboardpage.MyTextWatcher(mname));
//                                            lname.addTextChangedListener(new Dashboardpage.MyTextWatcher(lname));
//                                            email.addTextChangedListener(new Dashboardpage.MyTextWatcher(email));
//                                            pan.addTextChangedListener(new Dashboardpage.MyTextWatcher(pan));
//                                            monthly_income.addTextChangedListener(new Dashboardpage.MyTextWatcher(monthly_income));
//
//                                            fname.setText(SessionManager.get_firstname(prefs));
//                                            mname.setText(SessionManager.get_mname(prefs));
//                                            lname.setText(SessionManager.get_lastname(prefs));
//                                            email.setText(SessionManager.get_emailid(prefs));
//                                            pan.setText(SessionManager.get_pan(prefs));
//                                            monthly_income.setText(SessionManager.get_monthly_income(prefs));
//
//                                            if (!SessionManager.get_dob(prefs).equalsIgnoreCase("0000-00-00")) {
//                                                dob.setText(SessionManager.get_dob(prefs));
//                                            }
//
//                                            TextView submit = dialog2.findViewById(R.id.signuptwo);
//
//                                            submit.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View view) {
//
//                                                    if (isThereInternetConnection()) {
//                                                        submitFormtwo();
//                                                    } else {
//                                                        Toast.makeText(Dashboardpage.this, getString(R.string.checkinternet), Toast.LENGTH_LONG).show();
//
//                                                    }
//
//                                                }
//
//                                            });
//
//                                            dialog2.show();
//                                        }
//                                    });
//
//                                    dialog2.show();
                                } else {
                                    cibilscore.setText(jsonObjectresult.getString("cibil_score"));
                                    cibildisplay.setVisibility(View.VISIBLE);
                                    refreshscore.setVisibility(View.GONE);
                                    checkcibil.setVisibility(View.GONE);
//                                    pb.setVisibility(View.VISIBLE);
//                                    startAnimation(Integer.parseInt(jsonObjectresult.getString("cibil_score")));
                                    SessionManager.save_logintype(prefs, "Login");
                                    Constants.cardresponse = "";
                                    Constants.hardinquiryresponse = "";
                                    Constants.ontimepaymentresponse = "";
                                    Constants.ontimepaymentresponse = "";
                                    Constants.loanresponse = "";
//                                    logintype = "login";
                                    SessionManager.save_cibil_id(prefs, String.valueOf(jsonObjectresult.getString("cibil_id")));
                                    SessionManager.save_cibil_score(prefs, String.valueOf(jsonObjectresult.getString("cibil_score")));
                                    SessionManager.save_cibil_fetch_date(prefs, String.valueOf(jsonObjectresult.getString("cibil_score_fetch_date")));
//                                    String cibilfetchdate = "Last updated on " + coverteddate(SessionManager.get_cibil_fetch_date(prefs));
//                                    cibil_fetch_date.setText(cibilfetchdate);
                                    if (progressDialog != null && progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                    Constants.refreshclick = "false";
//                                    noproceed.setText("Next score update will be in 30 Days");
//                                    yesproceed.setBackgroundResource(R.drawable.roundedbuttonrefreshgrey);

//                                    if (type.equalsIgnoreCase("updated")) {
//                                        try {
//                                            dialog2.dismiss();
//                                        } catch (Exception ignored) {
//
//                                        }
//                                        updateuserdetails();
//                                    } else {
//                                    get_cibil_credit_factors();
//                                    }
                                }

                            } else {
                                JSONObject jsonObject2 = jsonObject.getJSONObject("message");
                                Toast.makeText(Profilepage.this, jsonObject2.getString("cibil_no_success"), Toast.LENGTH_SHORT).show();
                            }

                        }
                    } catch (Exception e) {

                        showerrordialog();

                        e.printStackTrace();

                    }
                }, Throwable::printStackTrace) {
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
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
    }

    public void customerassetsforrefresh() {
        if (progressDialog != null) {
            progressDialog.show();
        }
        boolean truevalue = true;
        boolean falsevalue = false;
        final JSONObject json = new JSONObject();
        try {
            json.put("cibil_id", SessionManager.get_cibil_id(prefs));
            json.put("show_report_xml", falsevalue);
            json.put("fetch_from_cibil", truevalue);
            json.put("is_autofetch", truevalue);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, BuildConfig.BASE_URL + "/v1/cibil-customer-assets", json,
                response -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());
                        {
                            String status = jsonObject.getString("status");

                            if (status.equalsIgnoreCase("success")) {

//                                wishFinAnalytics.cibilPage();

                                JSONObject jsonObjectresult = new JSONObject(jsonObject.getJSONObject("result").toString());

                                //code handeling for 0 and 1
                                if (jsonObjectresult.getString("cibil_score").equalsIgnoreCase("0") ||
                                        jsonObjectresult.getString("cibil_score").equalsIgnoreCase("1")) {

                                    Intent intent1 = new Intent(Profilepage.this, Dashboard.class);
                                    startActivity(intent1);
                                    finish();

//                                    if (progressDialog != null && progressDialog.isShowing()) {
//                                        progressDialog.dismiss();
//                                    }
//                                    Objects.requireNonNull(dialog2.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//
//                                    dialog2.setContentView(R.layout.wishfinapplycreditcardbox);
//
//                                    TextView getcreditcard = dialog2.findViewById(R.id.getcreditcard);
//                                    TextView logout = dialog2.findViewById(R.id.logout);
//
//                                    logout.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                            Objects.requireNonNull(dialog3.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//                                            dialog3.setContentView(R.layout.logout);
//                                            dialog3.show();
//                                            TextView cancle = dialog3.findViewById(R.id.cancle);
//                                            TextView ok = dialog3.findViewById(R.id.ok);
//
//                                            cancle.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//                                                    dialog3.dismiss();
//                                                }
//                                            });
//
//                                            ok.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//
//                                                    wishFinAnalytics.loggedOut();
//                                                    SessionManager.dataclear(prefs);
//                                                    dialog3.dismiss();
//                                                    dialog2.dismiss();
//                                                    Intent intent1 = new Intent(Dashboardpage.this, LoginPage.class);
//                                                    startActivity(intent1);
//                                                    finish();
//
//                                                }
//                                            });
//                                        }
//                                    });
//                                    getcreditcard.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.wishfin.com/credit-cards"));
//                                            startActivity(browserIntent);
//
//                                        }
//                                    });
//
//                                    dialog2.show();

                                } else if (jsonObjectresult.isNull("cibil_id") || jsonObjectresult.getString("cibil_score").equalsIgnoreCase("-1")) {

//                                    if (progressDialog != null && progressDialog.isShowing()) {
//                                        progressDialog.dismiss();
//                                    }
//                                    Objects.requireNonNull(dialog2.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//
//                                    dialog2.setContentView(R.layout.wishfinupdatedetaildbox);
//
//                                    TextView editdetails = dialog2.findViewById(R.id.btnedit);
//                                    TextView logout = dialog2.findViewById(R.id.logout);
//
//                                    logout.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                            Objects.requireNonNull(dialog3.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//                                            dialog3.setContentView(R.layout.logout);
//                                            dialog3.show();
//                                            TextView cancle = dialog3.findViewById(R.id.cancle);
//                                            TextView ok = dialog3.findViewById(R.id.ok);
//
//                                            cancle.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//                                                    dialog3.dismiss();
//                                                }
//                                            });
//
//                                            ok.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//
//                                                    wishFinAnalytics.loggedOut();
//                                                    SessionManager.dataclear(prefs);
//                                                    dialog3.dismiss();
//                                                    dialog2.dismiss();
//                                                    Intent intent1 = new Intent(Dashboardpage.this, LoginPage.class);
//                                                    startActivity(intent1);
//                                                    finish();
//
//                                                }
//                                            });
//                                        }
//                                    });
//                                    editdetails.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                            dialog2.dismiss();
////                                            dialog2.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//
//                                            dialog2.setContentView(R.layout.wishfinfilldetailtbox);
//                                            fname = dialog2.findViewById(R.id.fname);
//                                            input_layout_fname = dialog2.findViewById(R.id.input_layout_fname);
//                                            mname = dialog2.findViewById(R.id.mname);
//                                            monthly_income = dialog2.findViewById(R.id.monthly_income);
//                                            input_layout_monthly_income = dialog2.findViewById(R.id.input_layout_monthly_income);
//                                            selfemployedcheck = dialog2.findViewById(R.id.selfemployedcheck);
//                                            salariedcheck = dialog2.findViewById(R.id.salariedcheck);
//                                            input_layout_mname = dialog2.findViewById(R.id.input_layout_mname);
//                                            lname = dialog2.findViewById(R.id.lname);
//                                            input_layout_lname = dialog2.findViewById(R.id.input_layout_lname);
//                                            email = dialog2.findViewById(R.id.emailid);
//                                            input_layout_email = dialog2.findViewById(R.id.input_layout_emailid);
//                                            pan = dialog2.findViewById(R.id.pan);
//                                            dob = dialog2.findViewById(R.id.dob);
//                                            input_layout_dob = dialog2.findViewById(R.id.input_layout_dob);
//
//                                            dob.setOnClickListener(v1 -> DatePicdob());
//                                            if (SessionManager.get_occupation(prefs).equalsIgnoreCase("1")) {
//                                                str_occupation = "1";
//                                                salariedcheck.setChecked(true);
//                                            } else if (SessionManager.get_occupation(prefs).equalsIgnoreCase("2")) {
//                                                str_occupation = "2";
//                                                selfemployedcheck.setChecked(true);
//                                            }
//                                            selfemployedcheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                                                if (isChecked) {
//                                                    str_occupation = "2";
//
//                                                }
//                                            });
//
//                                            salariedcheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                                                if (isChecked) {
//                                                    str_occupation = "1";
//
//                                                }
//                                            });
//
//                                            fname.addTextChangedListener(new Dashboardpage.MyTextWatcher(fname));
//                                            mname.addTextChangedListener(new Dashboardpage.MyTextWatcher(mname));
//                                            lname.addTextChangedListener(new Dashboardpage.MyTextWatcher(lname));
//                                            email.addTextChangedListener(new Dashboardpage.MyTextWatcher(email));
//                                            pan.addTextChangedListener(new Dashboardpage.MyTextWatcher(pan));
//                                            monthly_income.addTextChangedListener(new Dashboardpage.MyTextWatcher(monthly_income));
//
//                                            fname.setText(SessionManager.get_firstname(prefs));
//                                            mname.setText(SessionManager.get_mname(prefs));
//                                            lname.setText(SessionManager.get_lastname(prefs));
//                                            email.setText(SessionManager.get_emailid(prefs));
//                                            pan.setText(SessionManager.get_pan(prefs));
//                                            monthly_income.setText(SessionManager.get_monthly_income(prefs));
//
//                                            if (!SessionManager.get_dob(prefs).equalsIgnoreCase("0000-00-00")) {
//                                                dob.setText(SessionManager.get_dob(prefs));
//                                            }
//
//                                            TextView submit = dialog2.findViewById(R.id.signuptwo);
//
//                                            submit.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View view) {
//
//                                                    if (isThereInternetConnection()) {
//                                                        submitFormtwo();
//                                                    } else {
//                                                        Toast.makeText(Profilepage.this, getString(R.string.checkinternet), Toast.LENGTH_LONG).show();
//
//                                                    }
//
//                                                }
//
//                                            });
//
//                                            dialog2.show();
//                                        }
//                                    });
//
//                                    dialog2.show();
                                } else {
                                    cibilscore.setText(jsonObjectresult.getString("cibil_score"));
                                    cibildisplay.setVisibility(View.VISIBLE);
                                    refreshscore.setVisibility(View.GONE);
                                    checkcibil.setVisibility(View.GONE);
//                                    pb.setVisibility(View.VISIBLE);
//                                    startAnimation(Integer.parseInt(jsonObjectresult.getString("cibil_score")));
                                    SessionManager.save_logintype(prefs, "Login");
                                    Constants.cardresponse = "";
                                    Constants.hardinquiryresponse = "";
                                    Constants.ontimepaymentresponse = "";
                                    Constants.ontimepaymentresponse = "";
                                    Constants.loanresponse = "";
//                                    logintype = "login";
                                    SessionManager.save_cibil_id(prefs, String.valueOf(jsonObjectresult.getString("cibil_id")));
                                    SessionManager.save_cibil_score(prefs, String.valueOf(jsonObjectresult.getString("cibil_score")));
                                    SessionManager.save_cibil_fetch_date(prefs, String.valueOf(jsonObjectresult.getString("cibil_score_fetch_date")));
//                                    String cibilfetchdate = "Last updated on " + coverteddate(SessionManager.get_cibil_fetch_date(prefs));
//                                    cibil_fetch_date.setText(cibilfetchdate);
                                    if (progressDialog != null) {
                                        progressDialog.show();
                                    }
                                    Constants.refreshclick = "false";
//                                    noproceed.setText("Next score update will be in 30 Days");
//                                    yesproceed.setBackgroundResource(R.drawable.roundedbuttonrefreshgrey);

//                                    if (type.equalsIgnoreCase("updated")) {
//                                        try {
//                                            dialog2.dismiss();
//                                        } catch (Exception ignored) {
//
//                                        }
//                                        updateuserdetails();
//                                    } else {
//                                    get_cibil_credit_factors();
//                                    }
                                }

                            } else {
                                JSONObject jsonObject2 = jsonObject.getJSONObject("message");
                                Toast.makeText(Profilepage.this, jsonObject2.getString("cibil_no_success"), Toast.LENGTH_SHORT).show();
                            }

                        }
                    } catch (Exception e) {

                        showerrordialog();

                        e.printStackTrace();

                    }
                }, Throwable::printStackTrace) {
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
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {

            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }

        }
    }

    public void verifyanswerasync(JSONObject jsonstring) {

        if (progressDialog != null) {
            progressDialog.show();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, BuildConfig.BASE_URL + "/v1/cibil-verify-answers", jsonstring,
                response -> {
                    singlequestion = "false";
                    multiplequestion = "false";
                    try {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        JSONObject jsonObject = new JSONObject(response.toString());
                        {
                            JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                            String cibil_status = jsonObject1.getString("cibil_status");

                            String apicall = "";
                            try {
                                apicall = jsonObject1.getString("next_api_call");
                            } catch (Exception e) {

                            }
                            if (cibil_status.equalsIgnoreCase("Success") && apicall.
                                    equalsIgnoreCase("cibil-customer-assets")) {

                                customerassets();

                            } else if ((cibil_status.equalsIgnoreCase("InProgress") || cibil_status.equalsIgnoreCase("Pending")) && apicall.
                                    equalsIgnoreCase("cibil-authentication-questions")) {

                                authenticateuestionasync();

                            }
                        }
                    } catch (Exception e) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        showerrordialog();

                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace) {
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
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
    }


//    @Override
//    public void onBackPressed() {
//        if (doubleBackToExitPressedOnce) {
//            super.onBackPressed();
//            return;
//        }
//
//        this.doubleBackToExitPressedOnce = true;
//        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
//
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                doubleBackToExitPressedOnce = false;
//            }
//        }, 2000);
//    }

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
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, BuildConfig.BASE_URL + "/oauth", json,
                response -> {
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

    public void update_last_login() {

        final JSONObject json = new JSONObject();

        try {
            json.put("master_user_id", "" + SessionManager.get_masteruserid(prefs));
            json.put("mobile_number", "" + SessionManager.get_mobile(prefs));
            json.put("device_token", "" + SessionManager.get_device_token(prefs));
            json.put("device_type", "Android");
//            json.put("app_version", "" + SessionManager.get_appversion(prefs));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, BuildConfig.BASE_URL + "/update-last-login", json,
                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response));
                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                            JSONObject jsonObjectresult1 = new JSONObject(jsonObject.getJSONObject("result").toString());

                            updated_time = jsonObjectresult1.getString("last_login");

                        }
                        get_cibil_user_detail();
                    } catch (Exception e) {
                        get_cibil_user_detail();
                    }

                }, error -> {
            error.printStackTrace();
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

        }

        ) {
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
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
    }

    private void showRateAppFallbackDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.hello) + " " + SessionManager.get_firstname(prefs))
                .setMessage(getString(R.string.rateuspopup))
                .setPositiveButton(getString(R.string.rateus), (dialog, which) -> redirectToPlayStore())
                .setNegativeButton(getString(R.string.notnow),
                        (dialog, which) -> {
                        })
                .show();
    }

    public void redirectToPlayStore() {
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (ActivityNotFoundException exception) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }


    private void submitFormtwo() {
        if (!validateName()) {
            return;
        }

        if (!validatemiddleName()) {
            return;
        }

        if (!validateLastName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validateDOB()) {
            return;
        }
        if (!validateIncome()) {
            return;
        }

        if (!validateoccupation()) {
            return;
        }
        if (progressDialog != null) {
            progressDialog.show();
        }
        get_cibil_fulfill_order("updated");

        if (dialog2.isShowing()) {
            dialog2.dismiss();
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

    }

    private boolean validateName() {
        Pattern pattern = Pattern.compile("^[a-zA-Z]{2,15}$");
        Matcher matcher = pattern.matcher(fname.getText().toString().trim());
        if (fname.getText().toString().trim().isEmpty() || !matcher.matches()) {
            input_layout_fname.setError("Enter Valid First Name");
            requestFocus(fname);
            return false;
        } else {
            input_layout_fname.setErrorEnabled(false);
            input_layout_fname.setDefaultHintTextColor(ColorStateList.valueOf(Color.parseColor("#788691")));
        }

        return true;
    }

    private boolean validateEmail() {
        String emailid = email.getText().toString().trim();

        if (emailid.isEmpty() || !isValidEmail(emailid)) {
            input_layout_email.setError("Enter Valid Email Id");
            requestFocus(email);
            return false;
        } else {
            input_layout_email.setErrorEnabled(false);
            input_layout_email.setDefaultHintTextColor(ColorStateList.valueOf(Color.parseColor("#788691")));
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validatemiddleName() {
        Pattern pattern = Pattern.compile("^[a-zA-Z]{0,10}$");
        Matcher matcher = pattern.matcher(mname.getText().toString().trim());
        if (!matcher.matches()) {
            input_layout_mname.setError("Enter Valid Middle Name");
            requestFocus(mname);
            return false;
        } else {
            input_layout_mname.setErrorEnabled(false);
            input_layout_mname.setDefaultHintTextColor(ColorStateList.valueOf(Color.parseColor("#788691")));
        }

        return true;
    }

    private boolean validateLastName() {
        Pattern pattern = Pattern.compile("^[a-zA-Z]{2,15}$");
        Matcher matcher = pattern.matcher(lname.getText().toString().trim());
        if (lname.getText().toString().trim().isEmpty() || !matcher.matches()) {
            input_layout_lname.setError("Enter Valid Last Name");
            requestFocus(lname);
            return false;
        } else {
            input_layout_lname.setErrorEnabled(false);
            input_layout_lname.setDefaultHintTextColor(ColorStateList.valueOf(Color.parseColor("#788691")));
        }

        return true;
    }

    private boolean validateDOB() {

        if (dob.getText().toString().equalsIgnoreCase("")) {
            input_layout_dob.setError("Select DOB");
            return false;
        }

        return true;
    }

    private boolean validatePAN() {

        String pannum = pan.getText().toString().trim();
        Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
        Matcher matcher = pattern.matcher(pannum);
        if (!matcher.matches() || pan.getText().length() != 10) {
            input_layout_pan.setError("Enter Valid PAN");
            requestFocus(pan);
            return false;
        } else {
            input_layout_pan.setErrorEnabled(false);
            input_layout_pan.setDefaultHintTextColor(ColorStateList.valueOf(Color.parseColor("#788691")));
        }

        return true;
    }

    private boolean validateIncome() {

        try {
            if (monthly_income.getText().toString().equalsIgnoreCase("") || Integer.parseInt(monthly_income.getText().toString()) < 0) {
                input_layout_monthly_income.setError("Provide Monthly Income");
                return false;
            } else {
                input_layout_monthly_income.setErrorEnabled(false);
                input_layout_monthly_income.setDefaultHintTextColor(ColorStateList.valueOf(Color.parseColor("#788691")));
            }
        } catch (Exception e) {

        }

        return true;
    }

    private boolean validateoccupation() {

        if (str_occupation.equalsIgnoreCase("")) {
            Toast.makeText(Profilepage.this, "Select Occupation", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

//    @Override
//    public void onChatCallBack() {
//
//    }
//
//    @Override
//    public void onEventsCallBack(@NonNull String s, @NonNull JSONObject jsonObject) {
//
//    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {

                case R.id.fname:
                    validateName();
                    break;
                case R.id.mname:
                    validatemiddleName();
                    break;
                case R.id.lname:
                    validateLastName();
                    break;
                case R.id.emailid:
                    validateEmail();
                    break;
                case R.id.pan:
                    validatePAN();
                    break;
                case R.id.monthly_income:
                    validateIncome();
                    break;

            }
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private void DatePicdob() {

        try {
            Calendar mcurrentDate = Calendar.getInstance();
            mYear = mcurrentDate.get(Calendar.YEAR);
            mMonth = mcurrentDate.get(Calendar.MONTH);
            mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

            mcurrentDate.add(Calendar.DATE, -1);

            DatePickerDialog mDatePicker = new DatePickerDialog(Profilepage.this, (datepicker, selectedyear, selectedmonth, selectedday) -> {
                // TODO Auto-generated method stub

                selectedmonth++;
                String month = "";
                if (selectedmonth > 0 && selectedmonth < 10) {
                    month = "0" + selectedmonth;
                } else {
                    month = "" + selectedmonth;
                }

                selecteddate = selectedyear + "-" + month + "-" + selectedday;
                dob.setText(selecteddate);
                input_layout_dob.setErrorEnabled(false);
                input_layout_dob.setDefaultHintTextColor(ColorStateList.valueOf(Color.parseColor("#788691")));

            }, mYear, mMonth, mDay);
            mDatePicker.setTitle("Select Date");
            mDatePicker.getDatePicker().setMaxDate((long) (mcurrentDate.getTimeInMillis() - (1000 * 60 * 60 * 24 * 365.25 * 18)));
            mDatePicker.getDatePicker().setMinDate((long) (mcurrentDate.getTimeInMillis() - (1000 * 60 * 60 * 24 * 365.25 * 65)));
            mDatePicker.show();
        } catch (Exception e) {


        }

    }

    public void logout_app() {

        final JSONObject json = new JSONObject();
        try {
            json.put("master_user_id", "" + SessionManager.get_masteruserid(prefs));
            json.put("app_type", "android-home-loan");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, BuildConfig.BASE_URL + "/v1/update-login-status", json,
                response -> {

                    try {
                        SessionManager.dataclear(prefs);
                        SessionManager.save_app_lang(prefs, "en");
                        Locale myLocale;
                        myLocale = new Locale("" + SessionManager.get_app_lang(prefs));
                        Resources res = getResources();
                        DisplayMetrics dm = res.getDisplayMetrics();
                        Configuration conf = res.getConfiguration();
                        conf.locale = myLocale;
                        res.updateConfiguration(conf, dm);

                        Intent intent1 = new Intent(Profilepage.this, Loginpage.class);
                        startActivity(intent1);
                        finish();

                    } catch (Exception e) {

                        e.printStackTrace();
                    }

                }, error -> {
            error.printStackTrace();

        }

        ) {
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
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
    }


}
