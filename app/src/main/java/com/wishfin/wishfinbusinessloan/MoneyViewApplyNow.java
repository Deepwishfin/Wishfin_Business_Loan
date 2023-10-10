package com.wishfin.wishfinbusinessloan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MoneyViewApplyNow extends AppCompatActivity {

    KProgressHUD progressDialog;
    SharedPreferences prefs;
    RequestQueue queue;
    String str_family_income = "", str_education = "",strpartnetrefid="", lead_id = "", bank_code = "", bank_name = "";
    Spinner select_family_income, select_education;
    TextView continuebtn, heading;
    EditText monthlysalary;

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
        select_education = findViewById(R.id.select_education);
        monthlysalary = findViewById(R.id.monthlysalary);
        heading = findViewById(R.id.heading);

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

                if (monthlysalary.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(MoneyViewApplyNow.this, "Please enter salary", Toast.LENGTH_SHORT).show();
                } else if (str_family_income.equalsIgnoreCase("")) {
                    Toast.makeText(MoneyViewApplyNow.this, "Please select family income", Toast.LENGTH_SHORT).show();
                } else if (str_education.equalsIgnoreCase("")) {
                    Toast.makeText(MoneyViewApplyNow.this, "Please select education", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    createlead();

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


    public void createlead() {
        final JSONObject json = new JSONObject();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("'Date\n'dd-MM-yyyy '\n\nand\n\nTime\n'HH:mm:ss z");
            String currentDateAndTime = sdf.format(new Date());
            strpartnetrefid="WISHFIN-ANDROID" + currentDateAndTime;

            json.put("partnerCode", "29");
            json.put("partnerRef", strpartnetrefid);
            json.put("phone", SessionManager.get_mobile(prefs));
            json.put("pan", SessionManager.get_pan(prefs));
            json.put("name", SessionManager.get_firstname(prefs) + " " + SessionManager.get_mname(prefs) + " " + SessionManager.get_lastname(prefs));
            json.put("gender", SessionManager.get_gender(prefs));
            json.put("dateOfBirth", SessionManager.get_dob(prefs));
            json.put("bureauPermission", "1");
            json.put("employmentType", "Salaried");
            json.put("incomeMode", "online");
            json.put("declaredIncome", monthlysalary.getText().toString());
            json.put("educationLevel", str_education);
            json.put("loanPurpose", "");
            json.put("annualFamilyIncome", str_family_income);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/money-view/create-lead", json, response -> {
            try {

                JSONObject jsonObject = new JSONObject(response.toString());
                JSONObject jsonObject1 = jsonObject.getJSONObject("result");

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                getoffers(jsonObject1.getString("id"));

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

    private void getoffers(String id) {
        final JSONObject json = new JSONObject();
        try {

            json.put("lead_id", id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/money-view/get-lead-status", json, response -> {
            try {

                JSONObject jsonObject = new JSONObject(response.toString());
                JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                JSONObject jsonObject2 = jsonObject1.getJSONObject("model");

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                getactivitystatus(id);

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

    private void getactivitystatus(String id) {
        final JSONObject json = new JSONObject();
        try {
            json.put("type", "bankstatement");
            json.put("leadId", id);
            json.put("partner_reference_id", strpartnetrefid);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/get-status", json, response -> {
            try {

                JSONObject jsonObject = new JSONObject(response.toString());

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                journeyurl(id);


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

            json.put("leadId", id);
            json.put("partner_reference_id", strpartnetrefid);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/money-view/get-journey-url", json, response -> {
            try {

                JSONObject jsonObject = new JSONObject(response.toString());
                JSONObject jsonObject1 = jsonObject.getJSONObject("result");

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(this, "" + jsonObject1.getString("msg"), Toast.LENGTH_SHORT).show();
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

