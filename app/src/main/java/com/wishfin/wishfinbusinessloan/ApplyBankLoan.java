package com.wishfin.wishfinbusinessloan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ApplyBankLoan extends AppCompatActivity {

    TextView continuebtn2, heading, subheading, cardname;
    RelativeLayout backbutton;
    KProgressHUD progressDialog;
    SharedPreferences prefs;
    RequestQueue queue;
    String lead_id = "", bank_code = "", bank_name = "", str_cityname = "", str_companyname = "";
    ArrayList<Gettersetterforall> citylist = new ArrayList<>();
    ArrayList<Gettersetterforall> companylist = new ArrayList<>();
    ArrayList<String> stringcitylist = new ArrayList<>();
    ArrayList<String> stringcompanylist = new ArrayList<>();
    EditText monthly_income, gross_salary, officeline1, officeline2, officeline3, officepincode;
    AutoCompleteTextView companyname, officecity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applybankloan);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        progressDialog = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel(getString(R.string.dialogtext)).setCancellable(false).setAnimationSpeed(1).setDimAmount(0.5f);

        queue = Volley.newRequestQueue(ApplyBankLoan.this);
        prefs = PreferenceManager.getDefaultSharedPreferences(ApplyBankLoan.this);

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

        backbutton = findViewById(R.id.backbutton);
        cardname = findViewById(R.id.cardname);
        continuebtn2 = findViewById(R.id.continuebtn2);
        heading = findViewById(R.id.heading);
        subheading = findViewById(R.id.subheading);
        companyname = findViewById(R.id.companyname);
        monthly_income = findViewById(R.id.monthly_income);
        gross_salary = findViewById(R.id.gross_salary);
        officeline1 = findViewById(R.id.officeline1);
        officeline2 = findViewById(R.id.officeline2);
        officeline3 = findViewById(R.id.officeline3);
        officepincode = findViewById(R.id.officepincode);
        officecity = findViewById(R.id.officecity);

        companyname.addTextChangedListener(textWatcher3);
        officecity.addTextChangedListener(textWatcher2);

        get_city_listing();

        cardname.setText(bank_name);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        continuebtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (companyname.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter Company Name", Toast.LENGTH_LONG).show();
                } else if (monthly_income.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter Monthly Income", Toast.LENGTH_LONG).show();
                } else if (gross_salary.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter Gross Salary", Toast.LENGTH_LONG).show();
                } else if (officeline1.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter Address", Toast.LENGTH_LONG).show();
                } else if (officecity.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter City", Toast.LENGTH_LONG).show();
                } else if (officepincode.getText().toString().length() < 6) {
                    Toast.makeText(getApplicationContext(), "Enter Pincode", Toast.LENGTH_LONG).show();
                } else {
                    progressDialog.show();
                    opt_bank();
                }


            }
        });
    }

    public void opt_bank() {
        final JSONObject json = new JSONObject();
        try {
            json.put("lead_id", SessionManager.get_lead_id(prefs));
            json.put("bank_code", bank_code);
            json.put("type", "hl");
            json.put("opt_status", "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/select-opted-bank", json, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response.toString());

                if (jsonObject.getString("status").equalsIgnoreCase("Success")) {

                    submit_application();
                }

            } catch (Exception e) {

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

    public void submit_application() {
        final JSONObject json = new JSONObject();
        try {
            json.put("lead_id", SessionManager.get_lead_id(prefs));
            json.put("bank_code", bank_code);
            json.put("employer_name", companyname.getText().toString());
            json.put("monthly_salary", monthly_income.getText().toString());
            json.put("net_salary", gross_salary.getText().toString());
            json.put("office_address", officeline1.getText().toString());
            json.put("office_city", officecity.getText().toString());
            json.put("office_pincode", officepincode.getText().toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/v1/home-loan-attribute", json, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response.toString());

                if (jsonObject.getString("status").equalsIgnoreCase("Success")) {

                    Intent intent = new Intent(ApplyBankLoan.this, ApplicationSubmitted.class);
                    intent.putExtra("lead_id", SessionManager.get_lead_id(prefs));
                    intent.putExtra("bank_code", bank_code);
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
                progressDialog.show();
                submit_application();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
    }

    private void get_city_listing() {

        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = BuildConfig.BASE_URL + "/city-list";
        StringRequest getRequest = new StringRequest(Request.Method.GET, url, response -> {
            // response

            try {
                citylist = new ArrayList<>();
                citylist.clear();
                stringcitylist = new ArrayList<>();
                stringcitylist.clear();

                JSONObject jsonObject = new JSONObject(response);

                if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                    JSONArray jsonArray = (jsonObject.getJSONArray("result"));

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject objectnew2 = jsonArray.getJSONObject(i);
                        Gettersetterforall pack = new Gettersetterforall();
                        pack.setCityname(objectnew2.getString("city"));
                        pack.setCityid(objectnew2.getString("id"));
                        pack.setStatename(objectnew2.getString("state"));
                        pack.setStatecode(objectnew2.getString("std_code"));

                        stringcitylist.add(objectnew2.getString("city"));
                        citylist.add(pack);

                    }
                    progressDialog.dismiss();

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, stringcitylist);
                    officecity.setThreshold(1);
                    officecity.setAdapter(adapter);

                } else if (jsonObject.getString("status").equalsIgnoreCase("failed")) {

                    progressDialog.dismiss();

                    Toast.makeText(getApplicationContext(), "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                }

            } catch (Exception e) {
                progressDialog.dismiss();

                e.printStackTrace();
            }
        }, error -> {
            // TODO Auto-generated method stub
            progressDialog.dismiss();
        }) {
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

    private void get_company_listing(String text) {

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = BuildConfig.BASE_URL + "/company-list?companyname=" + text;
        StringRequest getRequest = new StringRequest(Request.Method.GET, url, response -> {
            // response

            try {
                companylist = new ArrayList<>();
                stringcompanylist = new ArrayList<>();

                JSONObject jsonObject = new JSONObject(response);

                if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                    JSONArray jsonArray = (jsonObject.getJSONArray("result"));

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject objectnew2 = jsonArray.getJSONObject(i);
                        Gettersetterforall pack = new Gettersetterforall();
                        pack.setCityname(objectnew2.getString("company_name"));
                        stringcompanylist.add(objectnew2.getString("company_name"));
                        companylist.add(pack);

                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, stringcompanylist);
                    companyname.setThreshold(1);
                    companyname.setAdapter(adapter);

                } else if (jsonObject.getString("status").equalsIgnoreCase("failed")) {

                    Toast.makeText(getApplicationContext(), "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        }, error -> {
            // TODO Auto-generated method stub
        }) {
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

    TextWatcher textWatcher2 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            try {
                if (s.length() != 0) {
                    str_cityname = "";
                }
            } catch (Exception ignored) {

            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    TextWatcher textWatcher3 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            try {
                if (s.length() != 0) {
                    get_company_listing(s.toString().trim());
                }
            } catch (Exception ignored) {

            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };


}