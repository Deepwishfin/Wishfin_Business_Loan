package com.wishfin.wishfinbusinessloan;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class TataCapitalApplyNow extends AppCompatActivity {

    KProgressHUD progressDialog;
    SharedPreferences prefs;
    RequestQueue queue;
    Spinner select_maritalstatus;
    ArrayList<String> statelistarraystring = new ArrayList<>();
    ArrayList<Gettersetterforall> statelistarray = new ArrayList<>();
    ArrayList<String> rcitylistarray = new ArrayList<>();
    ArrayList<String> ocitylistarray = new ArrayList<>();
    ArrayList<String> rpincodelistarray = new ArrayList<>();
    ArrayList<String> opincodelistarray = new ArrayList<>();
    ArrayList<String> companylistarraystring = new ArrayList<>();
    ArrayList<Gettersetterforall> companylistarray = new ArrayList<>();
    String str_city_name = "", str_state_name = "", str_pincode_name = "", str_branch_code = "", str_webtopNo = "",
            lead_id = "", bank_code = "", bank_name = "", str_first_name = "", str_middlename = "", str_last_name = "", str_company_name = "",
            str_loan_amount = "", current_age = "", str_ostate = "", str_ocity = "", str_opincode = "", str_rstate = "", str_rcity = "", str_rpincode = "";
    TextView continuebtn, heading;
    EditText monthlysalary, official_email, oaddreessline1, oaddreessline2, raddreessline1, raddreessline2;
    AutoCompleteTextView employername, ostate, ocity, opincode, rstate, rcity, rpincode;
    String ELIGIBILITY_NAME = "";
    String APPROVED_LOAN_AMOUNT = "";
    String APPROVED_LOAN_TENOR = "";
    String APPROVED_ABB = "";
    String APPROVED_FOIR = "";
    String APPROVED_IRR = "";
    String APPROVED_LTV = "";
    String DECISION = "";
    String PROCESSIN_FEE = "";

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
        rstate = findViewById(R.id.rstate);
        rcity = findViewById(R.id.rcity);
        rpincode = findViewById(R.id.rpincode);
        ostate = findViewById(R.id.ostate);
        ocity = findViewById(R.id.ocity);
        opincode = findViewById(R.id.opincode);
        heading = findViewById(R.id.heading);
        heading.setText(bank_name);


        employername.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                str_company_name = employername.getText().toString();

            }
        });

        rstate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                str_rstate = rstate.getText().toString();
                get_city(str_rstate, "r");
            }
        });
        ostate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                str_ostate = ostate.getText().toString();
                get_city(str_ostate, "o");
            }
        });

        rcity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                str_rcity = rcity.getText().toString();
                get_pincode(str_rcity, "r");
            }
        });
        ocity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                str_ocity = ocity.getText().toString();
                get_pincode(str_ocity, "o");
            }
        });

        rpincode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                str_rpincode = rpincodelistarray.get(position);

            }
        });
        opincode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                str_opincode = opincodelistarray.get(position);
            }
        });

        String dob = SessionManager.get_dob(prefs);

        Date date1;
        Date date2;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dates = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date1 = dates.parse(dob);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        try {
            date2 = dates.parse(dates.format(new Date()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        long difference = Math.abs(Objects.requireNonNull(date2).getTime() - Objects.requireNonNull(date1).getTime());
        long differenceDates = (difference / (24 * 60 * 60 * 1000)) / 365;

        current_age = Long.toString(differenceDates);

        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (select_maritalstatus.getSelectedItem().equals("Select")) {
                    Toast.makeText(TataCapitalApplyNow.this, "Select Marital status", Toast.LENGTH_SHORT).show();
                } else if (monthlysalary.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(TataCapitalApplyNow.this, "Enter Monthly Income", Toast.LENGTH_SHORT).show();
                } else if (official_email.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(TataCapitalApplyNow.this, "Enter Official Email", Toast.LENGTH_SHORT).show();
                } else if (str_company_name.equalsIgnoreCase("")) {
                    Toast.makeText(TataCapitalApplyNow.this, "Enter Company Name", Toast.LENGTH_SHORT).show();
                } else if (raddreessline1.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(TataCapitalApplyNow.this, "Enter Resident Address 1", Toast.LENGTH_SHORT).show();
                } else if (raddreessline2.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(TataCapitalApplyNow.this, "Enter Resident Address 2", Toast.LENGTH_SHORT).show();
                } else if (str_rstate.equalsIgnoreCase("")) {
                    Toast.makeText(TataCapitalApplyNow.this, "Enter Resident State", Toast.LENGTH_SHORT).show();
                } else if (str_rcity.equalsIgnoreCase("")) {
                    Toast.makeText(TataCapitalApplyNow.this, "Enter Resident City", Toast.LENGTH_SHORT).show();
                } else if (str_rpincode.equalsIgnoreCase("")) {
                    Toast.makeText(TataCapitalApplyNow.this, "Enter Resident Pincode", Toast.LENGTH_SHORT).show();
                } else if (oaddreessline1.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(TataCapitalApplyNow.this, "Enter Office Address 1", Toast.LENGTH_SHORT).show();
                } else if (oaddreessline2.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(TataCapitalApplyNow.this, "Enter Office Address 2", Toast.LENGTH_SHORT).show();
                } else if (str_ostate.equalsIgnoreCase("")) {
                    Toast.makeText(TataCapitalApplyNow.this, "Enter Office State", Toast.LENGTH_SHORT).show();
                } else if (str_ocity.equalsIgnoreCase("")) {
                    Toast.makeText(TataCapitalApplyNow.this, "Enter Office City", Toast.LENGTH_SHORT).show();
                } else if (str_opincode.equalsIgnoreCase("")) {
                    Toast.makeText(TataCapitalApplyNow.this, "Enter Office Pincode", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    checkduplicatelead();
                }

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
                get_state();
                get_companylist();
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

                    if (str_first_name.equalsIgnoreCase("") && str_middlename.equalsIgnoreCase("") && str_last_name.equalsIgnoreCase("")) {
                        str_last_name = SessionManager.get_lastname(prefs);
                        str_middlename = SessionManager.get_mname(prefs);
                        str_first_name = SessionManager.get_firstname(prefs);
                    }
                    if (str_first_name.equalsIgnoreCase("") && str_last_name.equalsIgnoreCase("")) {
                        str_last_name = SessionManager.get_lastname(prefs);
                        str_middlename = SessionManager.get_mname(prefs);
                        str_first_name = SessionManager.get_firstname(prefs);

                    } else if (str_first_name.equalsIgnoreCase("") && !str_last_name.equalsIgnoreCase("")) {

                        String parts[] = str_last_name.split(" ");
                        if (parts.length == 3) {
                            str_first_name = parts[0];
                            str_middlename = parts[1];
                            str_last_name = parts[2];
                        } else if (parts.length == 2) {
                            str_first_name = parts[0];
                            str_middlename = "";
                            str_last_name = parts[1];
                        } else if (parts.length == 1) {
                            str_first_name = str_last_name;
                        }

                    } else if (!str_first_name.equalsIgnoreCase("") && str_last_name.equalsIgnoreCase("")) {
                        String parts[] = str_first_name.split(" ");
                        if (parts.length == 3) {
                            str_first_name = parts[0];
                            str_middlename = parts[1];
                            str_last_name = parts[2];
                        } else if (parts.length == 2) {
                            str_first_name = parts[0];
                            str_middlename = "";
                            str_last_name = parts[1];
                        } else if (parts.length == 1) {
                            str_last_name = str_first_name;
                        }
                    }

                    checkpincode();


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
            json.put("pincode", SessionManager.get_pincode(prefs));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/bank-state-city-lists", json, response -> {


            try {

                JSONObject jsonObject = new JSONObject(response.toString());


                if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    JSONObject objectnew2 = jsonArray.getJSONObject(0);
                    str_city_name = objectnew2.getString("city");
                    str_state_name = objectnew2.getString("state");
                    str_branch_code = objectnew2.getString("branchcode");
                    str_pincode_name = objectnew2.getString("pincode");

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
            if (str_middlename.equalsIgnoreCase("")) {
                json.put("applicantName", str_first_name + " " + str_last_name);
            } else {
                json.put("applicantName", str_first_name + " " + str_middlename + " " + str_last_name);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/createwebtop-no", json, response -> {
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
                } else if (statusCode == 201) {
                    Toast.makeText(this, "201 aaya", Toast.LENGTH_SHORT).show();
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

    public void submitapplication() {
        final JSONObject json = new JSONObject();
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy HH:mm:ss", Locale.getDefault());
            String requestdateandtime = sdf.format(new Date());

            String gendervalue = "";
            if (SessionManager.get_gender(prefs).equalsIgnoreCase("1")) {
                gendervalue = "MALE";
            } else if (SessionManager.get_gender(prefs).equalsIgnoreCase("2")) {
                gendervalue = "FEMALE";
            } else {
                gendervalue = "TRANSGENDER";
            }

            json.put("webtopNo", str_webtopNo);
            json.put("requestTime", requestdateandtime);
            json.put("productName", "BL");
            json.put("firstName", str_first_name);
            json.put("lastName", str_last_name);
            json.put("namePrefix", "Mr.");
            json.put("gender", gendervalue);
            json.put("marital_status", select_maritalstatus.getSelectedItem().toString());
            json.put("dateOfBirth", SessionManager.get_dob(prefs));
            json.put("age", current_age);
            json.put("timeAtAddress", 0);
            json.put("rentAmount", 0);
            json.put("addressLine1", raddreessline1.getText().toString());
            json.put("addressLine2", raddreessline2.getText().toString());
            json.put("addressCity", str_rcity);
            json.put("addressPin", str_rpincode);
            json.put("addressState", str_rstate);
            json.put("resiCumOffice", "N");
            json.put("phoneNumber", SessionManager.get_mobile(prefs));
            json.put("emailId", SessionManager.get_emailid(prefs));
            json.put("office_emailId", official_email.getText().toString());
            json.put("panNumber", SessionManager.get_pan(prefs));
            json.put("noOfDependents", 0);
            json.put("noOfEarningMembers", 0);
            json.put("noOfFamilyMembers", 0);
            json.put("employerName", str_company_name);
            json.put("employementType", "0103");
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
            json.put("Paddressline1", raddreessline1.getText().toString());
            json.put("Paddressline2", raddreessline2.getText().toString());
            json.put("Pcity", str_city_name);
            json.put("Pstate", str_state_name);
            json.put("PpinNumber", str_pincode_name);
            json.put("Ofaddressline1", oaddreessline1.getText().toString());
            json.put("Ofaddressline2", oaddreessline2.getText().toString());
            json.put("Ofcity", str_ocity);
            json.put("Ofstate", str_ostate);
            json.put("OfpinNumber", str_opincode);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/tata/submit-application", json, response -> {
            try {

                JSONObject jsonObject = new JSONObject(response.toString());


                if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    JSONArray jsonArray = jsonObject1.getJSONArray("APPLICANT-RESPONSE");
                    JSONObject jsonObject2 = jsonArray.getJSONObject(0).getJSONObject("DECISION-RESPONSE");
                    JSONArray jsonArray1 = jsonObject2.getJSONArray("OTHER-ELIGIBLITY-DETAILS");
                    String eligible = "False";
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        if (jsonArray1.getJSONObject(i).getString("DECISION").equalsIgnoreCase("Declined")) {

                            eligible = "False";

                        } else {

                            eligible = "True";
                            ELIGIBILITY_NAME = jsonArray1.getJSONObject(i).getString("ELIGIBILITY-NAME");
                            APPROVED_LOAN_AMOUNT = jsonArray1.getJSONObject(i).getString("APPROVED-LOAN-AMOUNT");
                            APPROVED_LOAN_TENOR = jsonArray1.getJSONObject(i).getString("APPROVED-LOAN-TENOR");
                            APPROVED_ABB = jsonArray1.getJSONObject(i).getString("APPROVED-ABB");
                            APPROVED_FOIR = jsonArray1.getJSONObject(i).getString("APPROVED-FOIR");
                            APPROVED_IRR = jsonArray1.getJSONObject(i).getString("APPROVED-IRR");
                            APPROVED_LTV = jsonArray1.getJSONObject(i).getString("APPROVED-LTV");
                            DECISION = jsonArray1.getJSONObject(i).getString("DECISION");
                            PROCESSIN_FEE = jsonArray1.getJSONObject(i).getString("PROCESSING-FEE");

                            break;
                        }
                    }

                    if (eligible.equalsIgnoreCase("True")) {
                        ///////////show emi calculator page
                    } else {
                        //////pop show You are not eligible
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

    public void get_companylist() {
        final JSONObject json = new JSONObject();
        try {

            json.put("bank_code", "m016");
            json.put("name", "TATA");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/get-comapny-list", json, response -> {
            try {
                JSONObject jsonObject = new JSONObject(String.valueOf(response));

                companylistarray = new ArrayList<>();
                companylistarraystring = new ArrayList<>();

                if (jsonObject.getString("status").equalsIgnoreCase("Success")) {

                    JSONArray jsonArray = (jsonObject.getJSONArray("result"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject objectnew2 = jsonArray.getJSONObject(i);
                        Gettersetterforall pack = new Gettersetterforall();
                        pack.setState_name(objectnew2.getString("name"));
                        pack.setState_code(objectnew2.getString("id"));

                        companylistarraystring.add(objectnew2.getString("name"));
                        companylistarray.add(pack);


                    }

                    ArrayAdapter residentpincodeadapter = new ArrayAdapter<String>
                            (this, android.R.layout.select_dialog_item, companylistarraystring);
                    employername.setThreshold(1);
                    employername.setAdapter(residentpincodeadapter);

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

    public void get_state() {
        final JSONObject json = new JSONObject();
        try {

            json.put("bankCode", "m016_bl");
            json.put("type", "state");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/bank-state-city-lists", json, response -> {
            try {
                JSONObject jsonObject = new JSONObject(String.valueOf(response));

                statelistarray = new ArrayList<>();
                statelistarraystring = new ArrayList<>();

                if (jsonObject.getString("status").equalsIgnoreCase("Success")) {

                    JSONArray jsonArray = (jsonObject.getJSONArray("result"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject objectnew2 = jsonArray.getJSONObject(i);
                        Gettersetterforall pack = new Gettersetterforall();
                        pack.setState_name(objectnew2.getString("state"));
                        pack.setState_code(objectnew2.getString("stateid"));

                        statelistarraystring.add(objectnew2.getString("state"));
                        statelistarray.add(pack);


                    }

                    ArrayAdapter residentpincodeadapter = new ArrayAdapter<String>
                            (this, android.R.layout.select_dialog_item, statelistarraystring);
                    ostate.setThreshold(1);
                    ostate.setAdapter(residentpincodeadapter);
                    rstate.setThreshold(1);
                    rstate.setAdapter(residentpincodeadapter);
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

    public void get_city(String str_state_name, String type) {
        final JSONObject json = new JSONObject();
        try {

            json.put("bankCode", "m016_bl");
            json.put("state", str_state_name);
            json.put("type", "city");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/bank-state-city-lists", json, response -> {
            try {
                JSONObject jsonObject = new JSONObject(String.valueOf(response));

                if (type.equalsIgnoreCase("r")) {
                    rcitylistarray = new ArrayList<>();
                } else {
                    ocitylistarray = new ArrayList<>();
                }

                if (jsonObject.getString("status").equalsIgnoreCase("Success")) {

                    JSONArray jsonArray = (jsonObject.getJSONArray("result"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject objectnew2 = jsonArray.getJSONObject(i);

                        if (type.equalsIgnoreCase("r")) {
                            rcitylistarray.add(objectnew2.getString("city"));
                        } else {
                            ocitylistarray.add(objectnew2.getString("city"));
                        }


                    }

                    if (type.equalsIgnoreCase("r")) {
                        ArrayAdapter residentpincodeadapter = new ArrayAdapter<String>
                                (this, android.R.layout.select_dialog_item, rcitylistarray);
                        rcity.setThreshold(1);
                        rcity.setAdapter(residentpincodeadapter);
                        rpincode.setText("");
                    } else {
                        ArrayAdapter residentpincodeadapter = new ArrayAdapter<String>
                                (this, android.R.layout.select_dialog_item, ocitylistarray);
                        ocity.setThreshold(1);
                        ocity.setAdapter(residentpincodeadapter);
                        opincode.setText("");
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

    public void get_pincode(String str_city_name, String type) {
        final JSONObject json = new JSONObject();
        try {

            json.put("bankCode", "m016_bl");
            json.put("city", str_city_name);
            json.put("type", "pincode");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/bank-state-city-lists", json, response -> {
            try {
                JSONObject jsonObject = new JSONObject(String.valueOf(response));

                if (type.equalsIgnoreCase("r")) {
                    rpincodelistarray = new ArrayList<>();
                } else {
                    opincodelistarray = new ArrayList<>();
                }

                if (jsonObject.getString("status").equalsIgnoreCase("Success")) {

                    JSONArray jsonArray = (jsonObject.getJSONArray("result"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject objectnew2 = jsonArray.getJSONObject(i);

                        if (type.equalsIgnoreCase("r")) {
                            rpincodelistarray.add(objectnew2.getString("pincode"));
                        } else {
                            opincodelistarray.add(objectnew2.getString("pincode"));
                        }


                    }

                    if (type.equalsIgnoreCase("r")) {
                        ArrayAdapter residentpincodeadapter = new ArrayAdapter<String>
                                (this, android.R.layout.select_dialog_item, rpincodelistarray);
                        rpincode.setThreshold(1);
                        rpincode.setAdapter(residentpincodeadapter);
                    } else {
                        ArrayAdapter residentpincodeadapter = new ArrayAdapter<String>
                                (this, android.R.layout.select_dialog_item, opincodelistarray);
                        opincode.setThreshold(1);
                        opincode.setAdapter(residentpincodeadapter);
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
