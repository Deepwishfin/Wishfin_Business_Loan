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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectCityPage extends AppCompatActivity {

    KProgressHUD progressDialog;
    SharedPreferences prefs;
    RequestQueue queue;
    AutoCompleteTextView rcity;
    ArrayList<Gettersetterforall> citylist = new ArrayList<>();
    ArrayList<String> stringcitylist = new ArrayList<>();
    ImageView backbutton;
    String str_cityname="";
    TextView continuebtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectcity);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        progressDialog = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel(getString(R.string.dialogtext)).setCancellable(false).setAnimationSpeed(1).setDimAmount(0.5f);

        queue = Volley.newRequestQueue(SelectCityPage.this);
        prefs = PreferenceManager.getDefaultSharedPreferences(SelectCityPage.this);

        rcity=findViewById(R.id.rcity);
        continuebtn=findViewById(R.id.continuebtn);
        backbutton=findViewById(R.id.backbutton);

        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(str_cityname.equalsIgnoreCase(""))
                {
                    Toast.makeText(getApplicationContext(), "Enter City", Toast.LENGTH_SHORT).show();

                }else {
                    SessionManager.save_city(prefs, str_cityname);
                    finish();
                }
            }
        });
        rcity.setOnItemClickListener((parent, view, position, id) -> {

            str_cityname = parent.getItemAtPosition(position).toString();
        });

        rcity.addTextChangedListener(textWatcher2);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        get_city_listing();

    }

    private void get_city_listing() {

        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = BuildConfig.BASE_URL + "/city-list";
        StringRequest getRequest = new StringRequest(Request.Method.GET, url, response -> {
            // response

            try {
                citylist = new ArrayList<>();
                stringcitylist = new ArrayList<>();

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
                    rcity.setThreshold(1);
                    rcity.setAdapter(adapter);


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
}
