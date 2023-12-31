package com.wishfin.wishfinbusinessloan;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Signuppage extends Activity implements SMSReceiver.OTPReceiveListener {

    TextView signupone, signuptwo, signupthree, resentotp, lastmobiletext, checkbox_text, checkbox_text1,
            mobilenumberhead, emailidhead, fnamehead, lnamehead, dobhead, panhead;
    LinearLayout linearone, lineartwo, linearthree;
    int page = 1;
    ImageView backbutton;
    RequestQueue queue;
    SharedPreferences prefs;
    EditText mobilenumber, emailid, otpone, otptwo, otpthree, otpfour, fname, mname, lname, pan, dob;
    CheckBox checkbox, checkbox1;
    KProgressHUD progressDialog;
    int mYear, mMonth, mDay;
    private SMSReceiver smsReceiver;
    String selecteddate = "",mobilenumberstr = "", secret_key = "";
    private boolean broadcast = true;
    String IPaddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.signuppage);

        progressDialog = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(getString(R.string.dialogtext))
                .setCancellable(false)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f);

//        progressDialog = new ProgressDialog(Signuppage.this);
//        progressDialog.setMessage("Loading...");
//        progressDialog.setTitle("Please wait");
//        progressDialog.setCancelable(false);

        backbutton = findViewById(R.id.backbutton);
        signupone = findViewById(R.id.signupone);
        signuptwo = findViewById(R.id.signuptwo);
        signupthree = findViewById(R.id.signupthree);

        linearone = findViewById(R.id.linearone);
        lineartwo = findViewById(R.id.lineartwo);
        linearthree = findViewById(R.id.linearthree);

        mobilenumber = findViewById(R.id.mobilenumber);
        mobilenumberhead = findViewById(R.id.mobilenumberhead);
        emailid = findViewById(R.id.emailid);
        emailidhead = findViewById(R.id.emailidhead);
        fnamehead = findViewById(R.id.fnamehead);
        lnamehead = findViewById(R.id.lnamehead);
        dobhead = findViewById(R.id.dobhead);
        panhead = findViewById(R.id.panhead);

        otpone = findViewById(R.id.otpone);
        otptwo = findViewById(R.id.otptwo);
        otpthree = findViewById(R.id.otpthree);
        otpfour = findViewById(R.id.otpfour);
        resentotp = findViewById(R.id.resentotp);
        lastmobiletext = findViewById(R.id.lastmobiletext);
        fname = findViewById(R.id.fname);
        mname = findViewById(R.id.mname);
        lname = findViewById(R.id.lname);
        dob = findViewById(R.id.dob);
        pan = findViewById(R.id.pan);
        checkbox = findViewById(R.id.checkbox);
        checkbox_text = findViewById(R.id.checkbox_text);
        checkbox1 = findViewById(R.id.checkbox1);
        checkbox_text1 = findViewById(R.id.checkbox_text1);
        pan.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

        mobilenumber.addTextChangedListener(new MyTextWatcher(mobilenumber));
        emailid.addTextChangedListener(new MyTextWatcher(emailid));
        fname.addTextChangedListener(new MyTextWatcher(fname));
        mname.addTextChangedListener(new MyTextWatcher(mname));
        lname.addTextChangedListener(new MyTextWatcher(lname));
        pan.addTextChangedListener(new MyTextWatcher(pan));
        otpone.addTextChangedListener(new MyTextWatcher(otpone));
        otptwo.addTextChangedListener(new MyTextWatcher(otptwo));
        otpthree.addTextChangedListener(new MyTextWatcher(otpthree));
        otpfour.addTextChangedListener(new MyTextWatcher(otpfour));


        otpone.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (otpone.getText().length() == 0) {
                        otpone.requestFocus();
                    }
                }
                return false;
            }
        });

        otptwo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (otptwo.getText().length() == 0) {
                        otpone.requestFocus();
                    }
                }
                return false;
            }
        });

        otpthree.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (otpthree.getText().length() == 0) {
                        otptwo.requestFocus();
                    }
                }
                return false;
            }
        });

        otpfour.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (otpfour.getText().length() == 0) {
                        otpthree.requestFocus();
                    }
                }
                return false;
            }
        });

        AppSignatureHashHelper appSignatureHashHelper = new AppSignatureHashHelper(Signuppage.this);
        Constants.hashkey = appSignatureHashHelper.getAppSignatures().get(0);

        NetwordDetect();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                mobilenumberstr = "";
            } else {
                mobilenumberstr = extras.getString("mobile");
                mobilenumber.setText(mobilenumberstr);
                mobilenumber.setFocusable(false);
                mobilenumber.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
                mobilenumber.setClickable(false);
                emailid.requestFocus();
            }
        } else {
            mobilenumberstr = (String) savedInstanceState.getSerializable("mobile");
        }

        queue = Volley.newRequestQueue(Signuppage.this);
        prefs = PreferenceManager.getDefaultSharedPreferences(Signuppage.this);
        Constants.apihittime = 0;

        getaouth();

        checkbox_text.setText(Html.fromHtml("By Tapping continue, I am agree to " +
                "<a href='com.wishfin.wishfinbusinessloan.Dynamicdisplaypage://Kode'>TERMS AND CONDITIONS</a>" + " and I give my Consent to check my <b> Cibil Score </b>"));
        checkbox_text.setClickable(true);
        checkbox_text.setMovementMethod(LinkMovementMethod.getInstance());

        dob.setOnClickListener(v -> DatePicdob());

        resentotp.setOnClickListener(v -> {
            get_otp_data();

        });

        backbutton.setOnClickListener(v -> {

            if (page == 1) {

                Intent intent = new Intent(Signuppage.this, MainActivity.class);
                startActivity(intent);
                finish();

            } else if (page == 2) {

                linearone.setVisibility(View.VISIBLE);
                lineartwo.setVisibility(View.GONE);
                linearthree.setVisibility(View.GONE);
                page--;

            } else if (page == 3) {
                linearone.setVisibility(View.GONE);
                lineartwo.setVisibility(View.VISIBLE);
                linearthree.setVisibility(View.GONE);
                otpone.setText("");
                otptwo.setText("");
                otpthree.setText("");
                otpfour.setText("");
                page--;
            }
        });

        signupone.setOnClickListener(v ->
        {
//            submitFormone();
            String lastnumber = mobilenumber.getText().toString().substring(mobilenumber.getText().toString().length() - 4);

            lastmobiletext.setText("We have sent an OTP on ******" + lastnumber);

            if (isThereInternetConnection()) {
                submitFormtwo();
            } else {
                Toast.makeText(Signuppage.this, getString(R.string.checkinternet), Toast.LENGTH_LONG).show();

            }
        });

        signuptwo.setOnClickListener(v -> {

            String lastnumber = mobilenumber.getText().toString().substring(mobilenumber.getText().toString().length() - 4);

            lastmobiletext.setText("We have sent an OTP on ******" + lastnumber);

            if (isThereInternetConnection()) {
                submitFormtwo();
            } else {
                Toast.makeText(Signuppage.this, getString(R.string.checkinternet), Toast.LENGTH_LONG).show();

            }

        });

        signupthree.setOnClickListener(v -> {

            String otpstring = otpone.getText().toString() + "" + otptwo.getText().toString() + "" + otpthree.getText().toString() + "" + otpfour.getText().toString();

            if (isThereInternetConnection()) {
                long currenttimestam = System.currentTimeMillis() / 1000;
                if (Constants.apihittime == 0) {
                    if (otpstring.length() != 4) {
                        Toast.makeText(Signuppage.this, "Please Enter OTP", Toast.LENGTH_LONG).show();

                    } else {
                        verify_OTP("Button clicked");
                    }
                } else {
                    if ((currenttimestam - Constants.apihittime) > 160) {
                        if (otpstring.length() != 4) {
                            Toast.makeText(Signuppage.this, "Please Enter OTP", Toast.LENGTH_LONG).show();

                        } else {
                            verify_OTP("Button clicked");
                        }
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(Constants.apiresponse);
                            if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                                progressDialog.show();
                                create_user();
                            }
                        } catch (Exception e) {

                        }
                    }
                }

            } else {
                Toast.makeText(Signuppage.this, getString(R.string.checkinternet), Toast.LENGTH_LONG).show();

            }

        });

    }

    private void NetwordDetect() {

        boolean WIFI = false;

        boolean MOBILE = false;

        ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo[] networkInfo = new NetworkInfo[0];
        if (CM != null) {
            networkInfo = CM.getAllNetworkInfo();
        }

        for (NetworkInfo netInfo : networkInfo) {

            if (netInfo.getTypeName().equalsIgnoreCase("WIFI"))

                if (netInfo.isConnected())

                    WIFI = true;

            if (netInfo.getTypeName().equalsIgnoreCase("MOBILE"))

                if (netInfo.isConnected())

                    MOBILE = true;
        }

        if (WIFI) {
            IPaddress = GetDeviceipWiFiData();

        }

        if (MOBILE) {

            IPaddress = GetDeviceipMobileData();

        }

    }

    public String GetDeviceipMobileData() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return Formatter.formatIpAddress(inetAddress.hashCode());
                    }
                }
            }
        } catch (Exception ex) {

        }
        return null;
    }

    public String GetDeviceipWiFiData() {

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        @SuppressWarnings("deprecation")

        String ip = null;
        if (wm != null) {
            ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        }

        return ip;

    }

    private void DatePicdob() {

        Calendar mcurrentDate = Calendar.getInstance();
        mYear = mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        mcurrentDate.add(Calendar.DATE, -1);

        DatePickerDialog mDatePicker = new DatePickerDialog(Signuppage.this, (datepicker, selectedyear, selectedmonth, selectedday) -> {
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

        }, mYear, mMonth, mDay);
        mDatePicker.setTitle("Select date");
        mDatePicker.getDatePicker().setMaxDate((long) (mcurrentDate.getTimeInMillis() - (1000 * 60 * 60 * 24 * 365.25 * 18)));
        mDatePicker.getDatePicker().setMinDate((long) (mcurrentDate.getTimeInMillis() - (1000 * 60 * 60 * 24 * 365.25 * 65)));
        mDatePicker.show();

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

    public void get_otp_data() {

        final JSONObject json = new JSONObject();

        try {
            json.put("email_mobile", "" + mobilenumber.getText().toString());
            json.put("type", "business_loan_signup_mobile");
            json.put("hash", "" + Constants.hashkey);
            json.put("device_type", "Android");
            json.put("app_version", "" + SessionManager.get_appversion(prefs));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, BuildConfig.BASE_URL + "/v1/get-otp-data", json,
                response -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    try {
                        timer();
                        JSONObject jsonObject = new JSONObject(response.toString());

                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                            JSONObject jsonObjectresult = new JSONObject(jsonObject.getJSONObject("result").toString());
                            secret_key = jsonObjectresult.getString("secret_key");
                            SessionManager.save_secret_key(prefs, jsonObjectresult.getString("secret_key"));
                            startSMSListener();
                            Toast.makeText(Signuppage.this, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                        } else if (jsonObject.getString("status").equalsIgnoreCase("failed")) {

                            if (jsonObject.getString("message").equalsIgnoreCase("Mobile number entered is already registered")) {

                                Intent intent = new Intent(Signuppage.this, Loginpage.class);
                                intent.putExtra("mobile", mobilenumber.getText().toString());
                                startActivity(intent);
                                finish();

                                Toast.makeText(Signuppage.this, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            } else if (jsonObject.getString("message").equalsIgnoreCase("ok")) {
                                JSONObject jsonObjectresult = new JSONObject(jsonObject.getJSONObject("result").toString());
                                if (jsonObjectresult.getString("otp_status").equalsIgnoreCase("Same OTP Send")
                                        || jsonObjectresult.getString("otp_status").contains("OTP Already Sent")) {
                                    secret_key = jsonObjectresult.getString("secret_key");
                                    SessionManager.save_secret_key(prefs, jsonObjectresult.getString("secret_key"));
                                    startSMSListener();
                                }
                            }
                        }

                    } catch (Exception e) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        e.printStackTrace();
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

    public void verify_OTP(String enter_otp_type) {

        final JSONObject json = new JSONObject();

        try {
            String otpstring = otpone.getText().toString() + "" + otptwo.getText().toString() + "" + otpthree.getText().toString() + "" + otpfour.getText().toString();
            json.put("email_mobile", "" + mobilenumber.getText().toString());
            json.put("otp", "" + otpstring);
            json.put("type", "business_loan_signup_mobile");
            json.put("case", "MO");
            if (secret_key.equalsIgnoreCase("")) {
                json.put("secret_key", "" + SessionManager.get_secret_key(prefs));
            } else {
                json.put("secret_key", secret_key);
            }

            json.put("device_type", "Android");
            json.put("app_version", "" + SessionManager.get_appversion(prefs));
            json.put("enter_otp_type", enter_otp_type);
            json.put("app_type", "android-business-loan");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, BuildConfig.BASE_URL + "/v1/otp-verify", json,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());
                        Constants.apiresponse = response.toString();
                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                            Constants.apihittime = System.currentTimeMillis() / 1000;

//                            progressDialog.dismiss();
                            create_user();

                        } else if (jsonObject.getString("status").equalsIgnoreCase("failed")) {
                            Toast.makeText(getApplicationContext(), "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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

    public void create_user() {

        final JSONObject json = new JSONObject();

        try {
            json.put("first_name", "" + fname.getText().toString());
            json.put("middle_name", "" + mname.getText().toString());
            json.put("last_name", "" + lname.getText().toString());
            json.put("date_of_birth", "" + dob.getText().toString());
            json.put("pancard", "" + pan.getText().toString());
            json.put("email_id", "" + emailid.getText().toString());
            json.put("mobile_number", "" + mobilenumber.getText().toString());
            json.put("gender", "1");
            json.put("is_pancard_kyc", "0");
            json.put("pancard_holder_name", "" + fname.getText().toString() + " " + lname.getText().toString());
            json.put("correspondence_address", "Default");
            json.put("correspondence_pincode", "400001");
            json.put("correspondence_state", "MH");
            json.put("correspondence_city", "Default");
            json.put("signup_source", "business_loan_android");
            json.put("resource_pagename", "Business_Loan_Wishfin_Android");
            json.put("resource_source", "Business_Loan_Wishfin_Android");
            json.put("resource_querystring", "");
            json.put("resource_ip_address", IPaddress);
            json.put("source", "Wishfin_Android");
            json.put("utm_source", "");
            json.put("utm_medium", "");
            json.put("referrer_address", "Business_Loan_Wishfin_Android");
            json.put("querystring", "");
            json.put("is_system_generated", "1");
            json.put("type", "business_loan_signup_mobile");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, BuildConfig.BASE_URL + "/v1/signup", json,
                response -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());

                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                            Constants.apihittime = 0;

                            Intent intent = new Intent(Signuppage.this, LoanInformationPage.class);
                            intent.putExtra("source", "signup");
                            intent.putExtra("ipaddress", IPaddress);
                            JSONObject jsonObjectresult = new JSONObject(jsonObject.getJSONObject("result").toString());
                            SessionManager.save_firstname(prefs, fname.getText().toString());
                            SessionManager.save_mname(prefs, mname.getText().toString());
                            SessionManager.save_lastname(prefs, lname.getText().toString());
                            SessionManager.save_mobile(prefs, mobilenumber.getText().toString());
                            SessionManager.save_emailid(prefs, emailid.getText().toString());
                            SessionManager.save_dob(prefs, dob.getText().toString());
                            SessionManager.save_pan(prefs, pan.getText().toString());
                            SessionManager.save_login(prefs, "True");
                            SessionManager.save_logintype(prefs, "Signup");
                            SessionManager.save_app_time(prefs, "0");

                            Constants.cardresponse = "";
                            Constants.loanresponse = "";
                            SessionManager.save_masteruserid(prefs, jsonObjectresult.getString("master_user_id"));
                            SessionManager.save_mfuserid(prefs, jsonObjectresult.getString("mf_user_id"));
                            startActivity(intent);
                            finish();
                            Toast.makeText(Signuppage.this, "Welcome " + fname.getText().toString(), Toast.LENGTH_LONG).show();

                        }

                        Toast.makeText(Signuppage.this, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        e.printStackTrace();
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

    private void submitFormone() {
        if (!validateNumber()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        linearone.setVisibility(View.GONE);
        lineartwo.setVisibility(View.VISIBLE);
        linearthree.setVisibility(View.GONE);
        page = 2;

    }

    private void submitFormtwo() {

        if (!validateEmail()) {
            return;
        }

        if (!validateName()) {
            return;
        }

        if (!validatelastName()) {
            return;
        }

        if (!validatePAN()) {
            return;
        }
        if (!validateDOB()) {
            return;
        }

        if (fname.getText().toString().trim().equalsIgnoreCase(lname.getText().toString().trim())) {
            Toast.makeText(Signuppage.this, "First name and Last name can't be same", Toast.LENGTH_LONG).show();

            return;
        }

        if (!checkbox.isChecked()) {
            Toast.makeText(Signuppage.this, "Accept Terms and Condition", Toast.LENGTH_LONG).show();
            return;
        }

        linearone.setVisibility(View.GONE);
        lineartwo.setVisibility(View.GONE);
        linearthree.setVisibility(View.VISIBLE);
        otpone.requestFocus();
        page = 3;
        hideKeyboard(Signuppage.this);
        get_otp_data();

    }

    private void timer() {
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                resentotp.setClickable(false);
                resentotp.setText("Resend OTP in: " + millisUntilFinished / 1000 + " sec");
                backbutton.setVisibility(View.GONE);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                resentotp.setText("Resend OTP");
                resentotp.setClickable(true);
                backbutton.setVisibility(View.VISIBLE);
            }

        }.start();
    }

    private boolean validateNumber() {
        if (!mobilenumber.getText().toString().trim().matches("[5-9][0-9]{9}") || mobilenumber.getText().length() != 10) {
            mobilenumberhead.setTextColor(Color.parseColor("#FF0000"));
            mobilenumberhead.setText("Enter 10 Digits Mobile Number");
//            Toast.makeText(getApplicationContext(),"Enter 10 Digits Mobile Number",Toast.LENGTH_SHORT).show();
            requestFocus(mobilenumber);
            return false;
        } else {
            mobilenumberhead.setText("Mobile Number");
            mobilenumberhead.setTextColor(Color.parseColor("#304258"));
        }
        return true;
    }

    private boolean validateName() {
//        Pattern pattern = Pattern.compile("^[a-zA-Z]{2,15}$");
//        Matcher matcher = pattern.matcher(fname.getText().toString().trim());
        if (fname.getText().toString().trim().isEmpty() || fname.getText().length() < 2) {
            fnamehead.setTextColor(Color.parseColor("#FF0000"));
            fnamehead.setText("Enter Valid Name");
            requestFocus(fname);
            return false;
        } else {
            fnamehead.setText("Full Name (As Per Bank Records)");
            fnamehead.setTextColor(Color.parseColor("#304258"));
        }
        return true;
    }

    private boolean validatelastName() {
//        Pattern pattern = Pattern.compile("^[a-zA-Z]{2,15}$");
//        Matcher matcher = pattern.matcher(fname.getText().toString().trim());
        if (lname.getText().toString().trim().isEmpty() || lname.getText().length() < 2) {
            lnamehead.setTextColor(Color.parseColor("#FF0000"));
            lnamehead.setText("Enter Valid Last Name");
            requestFocus(lname);
            return false;
        } else {
            lnamehead.setText("Last Name (As Per Bank Records)");
            lnamehead.setTextColor(Color.parseColor("#304258"));
        }
        return true;
    }

    private boolean validateDOB() {

        if (dob.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(getApplicationContext(), "Provide Date Of Birth", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private boolean validatePAN() {

        String pannum = pan.getText().toString().trim();
        Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
        Matcher matcher = pattern.matcher(pannum);
        if (!matcher.matches() || pan.getText().length() != 10) {
            panhead.setTextColor(Color.parseColor("#FF0000"));
            panhead.setText("Enter Valid PAN");
            requestFocus(pan);
            return false;
        } else {
            panhead.setTextColor(Color.parseColor("#304258"));
            panhead.setText("PAN Card Number");
        }

        return true;
    }


    private boolean validateEmail() {
        String email = emailid.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            emailidhead.setTextColor(Color.parseColor("#FF0000"));
            emailidhead.setText("Enter Valid Email ID");
//            Toast.makeText(getApplicationContext(),"Enter Valid Email ID",Toast.LENGTH_SHORT).show();
            requestFocus(emailid);
            return false;
        } else {
            emailidhead.setText("Email ID");
            emailidhead.setTextColor(Color.parseColor("#304258"));
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

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
                case R.id.mobilenumber:
                    validateNumber();
                    break;
                case R.id.emailid:
                    validateEmail();
                    break;
                case R.id.fname:
                    validateName();
                    break;
                case R.id.lname:
                    validatelastName();
                    break;
                case R.id.pan:
                    validatePAN();
                    break;
                case R.id.otpone:
                    if (otpone.getText().toString().equalsIgnoreCase("")) {
                        otpone.requestFocus();
                    } else {
                        otptwo.requestFocus();
                    }
                    break;
                case R.id.otptwo:
                    if (otptwo.getText().toString().equalsIgnoreCase("")) {
                        otpone.requestFocus();
                    } else {
                        otpthree.requestFocus();
                    }
                    break;
                case R.id.otpthree:
                    if (otpthree.getText().toString().equalsIgnoreCase("")) {
                        otptwo.requestFocus();
                    } else {
                        otpfour.requestFocus();
                    }
                    break;
                case R.id.otpfour:
                    if (otpfour.getText().toString().equalsIgnoreCase("")) {
                        otpthree.requestFocus();
                    }
//                    login_api();
                    break;

            }
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void startSMSListener() {
        try {
            smsReceiver = new SMSReceiver();
            smsReceiver.setOTPListener(this);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
            Signuppage.this.registerReceiver(smsReceiver, intentFilter);

            SmsRetrieverClient client = SmsRetriever.getClient(Signuppage.this);

            Task<Void> task = client.startSmsRetriever();
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // API successfully started
                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Fail to start API
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOTPReceived(String otp) {
        if (broadcast) {
            broadcast = false;
            otpone.setText("");
            otptwo.setText("");
            otpthree.setText("");
            otpfour.setText("");
            otpone.setText(String.valueOf(otp.charAt(0)));
            otptwo.setText(String.valueOf(otp.charAt(1)));
            otpthree.setText(String.valueOf(otp.charAt(2)));
            otpfour.setText(String.valueOf(otp.charAt(3)));
            requestFocus(otpfour);
            unregisterReceiver(smsReceiver);
            if (smsReceiver != null) {
                LocalBroadcastManager.getInstance(Objects.requireNonNull(Signuppage.this)).unregisterReceiver(smsReceiver);
            }

            verify_OTP("Auto populate");
        }
    }

    @Override
    public void onOTPTimeOut() {

    }

    @Override
    public void onOTPReceivedError(String error) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (smsReceiver != null) {
            LocalBroadcastManager.getInstance(Signuppage.this).unregisterReceiver(smsReceiver);
        }
    }

    private int getCurrentVersionCode() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
