package com.wishfin.wishfinbusinessloan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmadrosid.svgloader.SvgLoader;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Dashboard extends AppCompatActivity implements View.OnClickListener {

    RecyclerView article_list;

    ////////////////////////////In app update//////////////////////
    private AppUpdateManager appUpdateManager;
    private InstallStateUpdatedListener installStateUpdatedListener;

    private static final int FLEXIBLE_APP_UPDATE_REQ_CODE = 123;
    //////////////////////////In app update////////////////////////
    private static final String TITLE_KEY = "version";
    private FirebaseRemoteConfig firebaseRemoteConfig;
    CustomPagerAdapter adapter;
    KProgressHUD progressDialog;
    SharedPreferences prefs;
    RequestQueue queue;
    ArrayList<Gettersetterforall> list1 = new ArrayList<>();
    Share_Adapter radio_question_list_adapter;
    ImageView editbtn;
    TextView loanamount, article_text, name;
    LinearLayout line1, line2, line5;
    String selected_banks = "", IPaddress = "", multiplequestion = "false", singlequestion = "false";
    WishFinAnalytics wishFinAnalytics;
    Dialog dialog;
    ExpandableListAdapter radio_question_list_adapter_expandable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        NetwordDetect();

        progressDialog = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel(getString(R.string.dialogtext)).setCancellable(false).setAnimationSpeed(1).setDimAmount(0.5f);

        progressDialog.show();
        queue = Volley.newRequestQueue(Dashboard.this);
        prefs = PreferenceManager.getDefaultSharedPreferences(Dashboard.this);
        wishFinAnalytics = new WishFinAnalytics(this);

        dialog = new Dialog(Dashboard.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);


/////////////////////////In app update Start///////////////
        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        installStateUpdatedListener = state -> {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            } else if (state.installStatus() == InstallStatus.INSTALLED) {
                removeInstallStateUpdateListener();
            } else {
//                Toast.makeText(getApplicationContext(), "InstallStateUpdatedListener: state: " + state.installStatus(), Toast.LENGTH_LONG).show();
            }
        };
        appUpdateManager.registerListener(installStateUpdatedListener);
        checkUpdate();
//////////////////////////In app update end///////////////////

//        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
//        FirebaseRemoteConfigSettings.Builder configBuilder = new FirebaseRemoteConfigSettings.Builder();
//        if (BuildConfig.DEBUG) {
//            long cacheInterval = 0;
//            configBuilder.setMinimumFetchIntervalInSeconds(cacheInterval);
//        }
//        firebaseRemoteConfig.setConfigSettingsAsync(configBuilder.build());
//        fetchRemoteTitle();
//
//        AppRate.with(this).setStoreType(StoreType.GOOGLEPLAY) //default is GOOGLEPLAY (Google Play), other options are
//                //           AMAZON (Amazon Appstore) and
//                //           SAMSUNG (Samsung Galaxy Apps)
//                .setInstallDays((byte) 0) // default 10, 0 means install day
//                .setLaunchTimes((byte) 1) // default 10
//                .setRemindInterval((byte) 2) // default 1
//                .setRemindLaunchTimes((byte) 2) // default 1 (each launch)
//                .setShowLaterButton(true) // default true
//                .setDebug(false).setTextRateNow(getString(R.string.rateus)).setTitle(getString(R.string.hello) + " " + SessionManager.get_firstname(prefs)).setMessage(getString(R.string.rateuspopup))// default false
//                //Java 8+: .setOnClickButtonListener(which -> Log.d(MainActivity.class.getName(), Byte.toString(which)))
//                .setOnClickButtonListener(which -> Log.d(MainActivity.class.getName(), Byte.toString(which))).monitor();
//
//        if (AppRate.with(this).getStoreType() == StoreType.GOOGLEPLAY) {
//            //Check that Google Play is available
//            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) != ConnectionResult.SERVICE_MISSING) {
//                // Show a dialog if meets conditions
//                AppRate.showRateDialogIfMeetsConditions(this);
//            }
//        } else {
//            // Show a dialog if meets conditions
//            AppRate.showRateDialogIfMeetsConditions(this);
//
//        }


        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        line5 = findViewById(R.id.line5);

        line1.setOnClickListener(this);
        line2.setOnClickListener(this);
        line5.setOnClickListener(this);

        article_list = findViewById(R.id.article_list);
        loanamount = findViewById(R.id.loanamount);
        article_text = findViewById(R.id.article_text);
        editbtn = findViewById(R.id.editbtn);
        name = findViewById(R.id.name);

        name.setText("Hi " + SessionManager.get_firstname(prefs));
        int loan_amount = Integer.parseInt(SessionManager.get_loanamount(prefs));
        loanamount.setText("₹ " + getFormatedAmount(loan_amount));

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(Dashboard.this) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(Dashboard.this) {
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

        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Dashboard.this, LoanInformationPage.class);
                intent.putExtra("source", "login");
                intent.putExtra("ipaddress", IPaddress);
                startActivity(intent);
                finish();
            }
        });

    }

    public void get_bank_quote_list(String id) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = BuildConfig.BASE_URL + "/bussiness-loan-create?lead_id=" + id;
        StringRequest getRequest = new StringRequest(Request.Method.GET, url, response -> {

            try {
                progressDialog.dismiss();
                JSONObject jsonObject = new JSONObject(response);
                list1 = new ArrayList<>();

                if (jsonObject.getString("status").equalsIgnoreCase("Success")) {


                    JSONArray jsonArray = (jsonObject.getJSONArray("result"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject objectnew2 = jsonArray.getJSONObject(i);
                        Gettersetterforall pack = new Gettersetterforall();
                        try {
                            pack.setInterest_rate(objectnew2.getString("interest_rate"));
                            pack.setEmi("0");
                            pack.setTenure(objectnew2.getString("tenure"));
                            pack.setLoan_amount(objectnew2.getString("loan_amount"));
                            pack.setProcessing_fee(objectnew2.getString("processing_fee"));
                        } catch (Exception e) {
                            pack.setInterest_rate("NA");
                            pack.setEmi("NA");
                            pack.setTenure("NA");
                            pack.setLoan_amount("NA");
                            pack.setProcessing_fee("NA");
                        }
                        pack.setBank_code(objectnew2.getString("bank_code"));
                        pack.setBank_name(objectnew2.getString("pl_bank_name"));
                        pack.setShow_logo(objectnew2.getString("show_logo"));
//                        pack.setApi_to_use(objectnew2.getString("api_to_use"));
                        pack.setImage_path(objectnew2.getString("image_path"));

                        if (selected_banks.contains(objectnew2.getString("bank_code"))) {
                            pack.setSelected_banks("True");
                        } else {
                            pack.setSelected_banks("False");
                        }
                        list1.add(pack);
                    }

                    if (list1.size() > 0) {
                        article_list.setVisibility(View.VISIBLE);

                        radio_question_list_adapter = new Share_Adapter(Dashboard.this, list1);
                        article_list.setAdapter(radio_question_list_adapter);
                        article_text.setText("Best Offers For You!");

                    } else {
                        article_text.setText("No Offers Found");
                        Toast.makeText(Dashboard.this, "No Banks Found", Toast.LENGTH_LONG).show();
                        article_list.setVisibility(View.GONE);

                    }


                } else if (jsonObject.getString("status").equalsIgnoreCase("failed")) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(Dashboard.this, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();

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
        queue.add(getRequest);

    }

    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.line5:
                Intent intent2 = new Intent(Dashboard.this, Profilepage.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.line2:
                Intent intent3 = new Intent(Dashboard.this, EMICalculator.class);
                startActivity(intent3);

                break;

        }
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
            ImageView imageView;
            TextView joiningfees, annualfees, viewdetails;
            RelativeLayout relit;

            MyViewHolder(View view) {
                super(view);

                imageView = view.findViewById(R.id.imageView);
                annualfees = view.findViewById(R.id.annualfees);
                joiningfees = view.findViewById(R.id.joiningfees);
                viewdetails = view.findViewById(R.id.viewdetails);
                relit = view.findViewById(R.id.relit);

            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_list_adapter, parent, false);

            return new MyViewHolder(itemView);
        }

        @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
        @Override
        public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            holder.joiningfees.setText(list_car.get(position).getInterest_rate() + "%");
            holder.annualfees.setText(list_car.get(position).getProcessing_fee());
            String uffuhg =list_car.get(position).getImage_path();
            try {
                Utilllllssss.fetchSvg(context, uffuhg, holder.imageView);
            } catch (Exception e) {

            }

            if (list_car.get(position).getSelected_banks().equalsIgnoreCase("True")) {
                holder.viewdetails.setText("Selected");
                holder.viewdetails.setClickable(false);
            } else {
                holder.viewdetails.setText("Apply Now");
                holder.viewdetails.setClickable(true);
            }

            holder.viewdetails.setTag(position);

            holder.viewdetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos = (int) v.getTag();

                    if (!list_car.get(pos).getSelected_banks().equalsIgnoreCase("True")) {
                        if (list_car.get(pos).getBank_code().equalsIgnoreCase("m105")) {
                            Intent intent = new Intent(Dashboard.this, KreditBeeApplyNow.class);
                            intent.putExtra("lead_id", SessionManager.get_lead_id(prefs));
                            intent.putExtra("bank_code", list_car.get(pos).getBank_code());
                            intent.putExtra("bank_name", list_car.get(pos).getBank_name());
                            startActivity(intent);
                        } else if (list_car.get(pos).getBank_code().equalsIgnoreCase("m104")) {
                            Intent intent = new Intent(Dashboard.this, MoneyViewApplyNow.class);
                            intent.putExtra("lead_id", SessionManager.get_lead_id(prefs));
                            intent.putExtra("bank_code", list_car.get(pos).getBank_code());
                            intent.putExtra("bank_name", list_car.get(pos).getBank_name());
                            startActivity(intent);
                        }else if (list_car.get(pos).getBank_code().equalsIgnoreCase("m036")) {
                            Intent intent = new Intent(Dashboard.this, LendingKartApplyNow.class);
                            intent.putExtra("lead_id", SessionManager.get_lead_id(prefs));
                            intent.putExtra("bank_code", list_car.get(pos).getBank_code());
                            intent.putExtra("bank_name", list_car.get(pos).getBank_name());
                            startActivity(intent);
                        }else if (list_car.get(pos).getBank_code().equalsIgnoreCase("m016")) {
                            Intent intent = new Intent(Dashboard.this, TataCapitalApplyNow.class);
                            intent.putExtra("lead_id", SessionManager.get_lead_id(prefs));
                            intent.putExtra("bank_code", list_car.get(pos).getBank_code());
                            intent.putExtra("bank_name", list_car.get(pos).getBank_name());
                            intent.putExtra("loan_amount", loanamount.getText().toString());
                            startActivity(intent);
                        }
                    }

                }
            });


        }

        @Override
        public int getItemCount() {
            return list_car.size();
        }

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
                get_bank_quote_list(SessionManager.get_lead_id(prefs));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
    }

    private int getappversion() {
        PackageInfo pInfo = null;
        try {
            pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = null;
        if (pInfo != null) {
            version = String.valueOf(pInfo.versionCode);
        }

        SessionManager.save_appversion(prefs, version);

        if (version != null) {
            return Integer.parseInt(version);
        } else {
            return 0;
        }
    }

    private void fetchRemoteTitle() {
        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                String value = firebaseRemoteConfig.getString(TITLE_KEY);
                if (Integer.parseInt(value) > getappversion()) {
                    Objects.requireNonNull(dialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
                    dialog.setContentView(R.layout.updateappdialog);
                    dialog.show();
                    TextView submit = dialog.findViewById(R.id.btnsubmit);
                    TextView btncancle = dialog.findViewById(R.id.btncancle);
                    submit.setOnClickListener(view -> {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("market://details?id=com.wishfin.wishfinbusinessloan"));
                            startActivity(intent);
                        } catch (Exception e) { //google play app is not installed
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.wishfin.wishfinbusinessloan&hl=en_IN&gl=in"));
                            startActivity(intent);
                        }
                    });
                    btncancle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                }
            }
        });
    }

    public void get_cibil_history() {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = BuildConfig.BASE_URL + "/historic-score?mobile=" + SessionManager.get_mobile(prefs);
        StringRequest getRequest = new StringRequest(Request.Method.GET, url, response -> {
            // response

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            try {
                JSONObject jsonObject = new JSONObject(response);
                {
                    if (!jsonObject.getString("status").equalsIgnoreCase("failed")) {

                        SessionManager.save_cibil_checked_status(prefs, "true");

                    } else {
                        SessionManager.save_cibil_checked_status(prefs, "false");
                        SessionManager.save_logintype(prefs, "Signup");
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        get_cibil_fulfill_order("normal");
                    }
                }
            } catch (Exception e) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                e.printStackTrace();
            }
        }, error -> {
            // TODO Auto-generated method stub
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                String bearer = "Bearer " + SessionManager.get_access_token(prefs);
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Accept", "application/json");
                params.put("Authorization", bearer);

                return params;
            }
        };
        queue.add(getRequest);

    }

    public void get_cibil_fulfill_order(String type) {

        String appendurl = "";
        final JSONObject json = new JSONObject();

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

                json.put("pancard", "");
                json.put("date_of_birth", "");
                json.put("annual_income", "");
                json.put("occupation", "");
            } else {
                appendurl = "/v1/cibil-fulfill-order";
                json.put("first_name", "" + SessionManager.get_firstname(prefs));
                json.put("middle_name", "" + SessionManager.get_mname(prefs));
                if (SessionManager.get_lastname(prefs).equalsIgnoreCase("")) {
                    json.put("last_name", "" + SessionManager.get_firstname(prefs));
                } else {
                    json.put("last_name", "" + SessionManager.get_lastname(prefs));
                }
                json.put("pancard", "" + SessionManager.get_pan(prefs));
                json.put("date_of_birth", "" + SessionManager.get_dob(prefs));
                json.put("email_id", "" + SessionManager.get_emailid(prefs));
                json.put("annual_income", "360000");
                json.put("occupation", "1");
                json.put("mobile_number", "" + SessionManager.get_mobile(prefs));
            }

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
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + appendurl, json, response -> {

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

                    }

                    if (cibil_status.equalsIgnoreCase("Inprogress") && apicall.equalsIgnoreCase("cibil-authentication-questions") && is_returning_customer.equalsIgnoreCase("1") || (cibil_status.equalsIgnoreCase("Pending") && apicall.equalsIgnoreCase("cibil-authentication-questions") && is_returning_customer.equalsIgnoreCase("1"))) {
                        authenticateuestionasync(type);
                    } else if (is_returning_customer.equalsIgnoreCase("1")) {

                    } else if (cibil_status.equalsIgnoreCase("success") && apicall.equalsIgnoreCase("cibil-customer-assets")) {

                        customerassets();

                    } else if (cibil_status.equalsIgnoreCase("Inprogress") && apicall.equalsIgnoreCase("cibil-authentication-questions") || (cibil_status.equalsIgnoreCase("Pending") && apicall.equalsIgnoreCase("cibil-authentication-questions"))) {
                        authenticateuestionasync(type);
                    } else if (cibil_status.equalsIgnoreCase("failed")) {

                        String message = "";
                        try {
                            message = jsonObject1.getString("message");
                        } catch (Exception e) {
                            JSONObject jsonObject2 = jsonObject1.getJSONObject("RequestError");
                            message = jsonObject2.getString("NO_HIT");
                            get_cibil_fulfill_order("NO_HIT");
                        }

                        Toast.makeText(Dashboard.this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject jsonObjectresult = new JSONObject(jsonObject.getJSONObject("result").toString());

                        if (jsonObjectresult.getString("cibil_score").equalsIgnoreCase("0") || jsonObjectresult.getString("cibil_score").equalsIgnoreCase("1")) {

                        } else {
//
                            Constants.cardresponse = "";
                            Constants.hardinquiryresponse = "";
                            Constants.ontimepaymentresponse = "";
                            Constants.ontimepaymentresponse = "";
                            Constants.loanresponse = "";

                            SessionManager.save_cibil_id(prefs, jsonObjectresult.getString("cibil_id"));
                            SessionManager.save_cibil_score(prefs, jsonObjectresult.getString("cibil_score"));
                            SessionManager.save_cibil_fetch_date(prefs, jsonObjectresult.getString("cibil_score_fetch_date"));
//
                        }
                    }

                } else if (jsonObject.getString("status").equalsIgnoreCase("failed")) {

                }

            } catch (Exception e) {

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

                        }
                    } catch (Exception ignored) {

                    }
                }
                error.printStackTrace();


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

    public void customerassets() {

        final JSONObject json = new JSONObject();
        try {
            json.put("cibil_id", SessionManager.get_cibil_id(prefs));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/v1/cibil-customer-assets", json, response -> {

            try {
                JSONObject jsonObject = new JSONObject(response.toString());
                {
                    String status = jsonObject.getString("status");

                    if (status.equalsIgnoreCase("success")) {

                        JSONObject jsonObjectresult = new JSONObject(jsonObject.getJSONObject("result").toString());

                        if (jsonObjectresult.getString("cibil_score").equalsIgnoreCase("0") || jsonObjectresult.getString("cibil_score").equalsIgnoreCase("1")) {


                        } else if (jsonObjectresult.isNull("cibil_id") || jsonObjectresult.getString("cibil_score").equalsIgnoreCase("-1")) {
//
//
                        } else {
//
                            SessionManager.save_logintype(prefs, "Login");
                            Constants.cardresponse = "";
                            Constants.hardinquiryresponse = "";
                            Constants.ontimepaymentresponse = "";
                            Constants.ontimepaymentresponse = "";
                            Constants.loanresponse = "";

//                                    logintype = "login";
                            SessionManager.save_cibil_id(prefs, jsonObjectresult.getString("cibil_id"));
                            SessionManager.save_cibil_score(prefs, jsonObjectresult.getString("cibil_score"));
                            SessionManager.save_cibil_fetch_date(prefs, jsonObjectresult.getString("cibil_score_fetch_date"));
//                                    String cibilfetchdate = "Last updated on " + coverteddate(SessionManager.get_cibil_fetch_date(prefs));
//                                    cibil_fetch_date.setText(cibilfetchdate);

                            Constants.refreshclick = "false";
//
                        }

                    } else {
                        JSONObject jsonObject2 = jsonObject.getJSONObject("message");
                        Toast.makeText(Dashboard.this, jsonObject2.getString("cibil_no_success"), Toast.LENGTH_SHORT).show();
                    }

                }
            } catch (Exception e) {

//                        showerrordialog();

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


    public void authenticateuestionasync(String type) {
        if (progressDialog != null) {
            progressDialog.show();
        }
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = BuildConfig.BASE_URL + "/v1/cibil-authentication-questions/" + SessionManager.get_cibil_id(prefs);
//        String url = Constants.BASE_URL + "/v1/cibil-authentication-questions/438981";
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
                                        skip.setText(getString(R.string.skip));
                                    } else {
                                        skip.setText(getString(R.string.resend));
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
                                                verifyanswerasync(jsonObject, type);
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
                                            verifyanswerasync(jsonObject, type);
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
                                        adapter = new CustomPagerAdapter(Dashboard.this, catlist);
                                        textone.setAdapter(adapter);
                                        singlequestion = "true";
                                        multiplequestion = "true";
                                        Button submit = dialog.findViewById(R.id.btnsubmit);
                                        Button skip = dialog.findViewById(R.id.btn_skip);
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
                                                    verifyanswerasync(jsonObject, type);
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
                                                verifyanswerasync(jsonObject, type);
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

                                        ArrayList<CibilQuestionanswergetset> top250 = new ArrayList<CibilQuestionanswergetset>();
                                        ArrayList<CibilQuestionanswergetset> headercustom = new ArrayList<CibilQuestionanswergetset>();
                                        Constants.radio_question_list = new ArrayList<String>();
                                        Constants.radio_option_list = new HashMap<String, ArrayList<CibilQuestionanswergetset>>();

                                        for (int k = 0; k < jsonObject2.length(); k++) {

                                            JSONObject objectnew2 = jsonObject2.getJSONObject(k);
                                            CibilQuestionanswergetset pack = new CibilQuestionanswergetset();
                                            pack.setQuestion((objectnew2.getString("FullQuestionText")));
                                            pack.setQuestionkey((objectnew2.getString("Key")));
                                            headercustom.add(pack);
                                            JSONArray jsonObject5 = objectnew2.getJSONArray("AnswerChoice");
                                            Constants.radio_question_list.add(objectnew2.getString("FullQuestionText"));

                                            top250 = new ArrayList<CibilQuestionanswergetset>();

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
                                        radio_question_list_adapter_expandable = new ExpandableListAdapter(Dashboard.this, Constants.radio_question_list, Constants.radio_option_list);
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
                                                    verifyanswerasync(jsonObject, type);
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
                                                verifyanswerasync(jsonObject, type);
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

    public void verifyanswerasync(JSONObject jsonstring, String type) {

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

                                authenticateuestionasync(type);

                            }
                        }
                    } catch (Exception e) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
//                        showerrordialog();
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

    @Override
    protected void onResume() {
        super.onResume();
        get_bank_quote_list(SessionManager.get_lead_id(prefs));
        get_cibil_history();
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

    private String getFormatedAmount(int amount) {
        return NumberFormat.getNumberInstance(Locale.US).format(amount);
    }

    ////////////////////////////In App update Function////////////////////

    private void checkUpdate() {

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                startUpdateFlow(appUpdateInfo);
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            }
        });
    }

    private void startUpdateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, Dashboard.FLEXIBLE_APP_UPDATE_REQ_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FLEXIBLE_APP_UPDATE_REQ_CODE) {
            if (resultCode == RESULT_CANCELED) {
//                Toast.makeText(getApplicationContext(), "Update canceled by user! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_OK) {
//                Toast.makeText(getApplicationContext(),"Update success! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
            } else {
//                Toast.makeText(getApplicationContext(), "Update Failed! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
                checkUpdate();
            }
        }
    }

    private void popupSnackBarForCompleteUpdate() {
        Snackbar.make(findViewById(android.R.id.content).getRootView(), "New app is ready!", Snackbar.LENGTH_INDEFINITE)

                .setAction("Install", view -> {
                    if (appUpdateManager != null) {
                        appUpdateManager.completeUpdate();
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.purple_500))
                .show();
    }

    private void removeInstallStateUpdateListener() {
        if (appUpdateManager != null) {
            appUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeInstallStateUpdateListener();
    }

}
