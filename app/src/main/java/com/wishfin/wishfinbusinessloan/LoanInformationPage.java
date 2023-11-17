package com.wishfin.wishfinbusinessloan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class LoanInformationPage extends AppCompatActivity {

    TextView nextone, nexttwo, nextthree, nextfour, nextfive, nextsix, nextseven, nexteight, view_all, delhi, mumbai, pune, banglore, chennai, jaipur;
    LinearLayout linearone, lineartwo, linearthree, linearfour, linearfive, linearsix, linearseven, lineareight;
    ImageView backbutton;
    int stepSize = 50000;
    SeekBar seekBar;
    EditText loanamount, businessyears;
    int page = 1;
    KProgressHUD progressDialog;
    SharedPreferences prefs;
    RequestQueue queue;
    String str_occupation = "2", str_annual_turnover = "1", str_company_type, str_nature_business, str_industry_type,
            str_sub_industry_type, str_business_place = "Owned by Self / Spouse", str_collateral_loan = "Property";
    String str_cityname = "Delhi";
    Spinner spinnercompanytype, spinnernaturebusiness, spinnerindustrytype, spinnersubindustrytype;
    RadioButton selfbusiness, selfproffessional, turnover_btw_10_40_lac, turnover_btw_40_lac_1_cr, turnover_btw_1_3_cr,
            turnover_over_3_cr, ownedbyself, ownedbyparents, rentedfamily, property, gold, car, billdiscounting, no;
    String IPaddress = "";
    ArrayList<String> list1 = new ArrayList<>();
    ArrayList<String> list2 = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loaninformationpage);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        progressDialog = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel(getString(R.string.dialogtext)).setCancellable(false).setAnimationSpeed(1).setDimAmount(0.5f);

        queue = Volley.newRequestQueue(LoanInformationPage.this);
        prefs = PreferenceManager.getDefaultSharedPreferences(LoanInformationPage.this);

        SessionManager.save_city(prefs, str_cityname);

        seekBar = findViewById(R.id.seekbar);
        loanamount = findViewById(R.id.loanamount);
        businessyears = findViewById(R.id.businessyears);
        nextone = findViewById(R.id.nextone);
        nexttwo = findViewById(R.id.nexttwo);
        nextthree = findViewById(R.id.nextthree);
        nextfour = findViewById(R.id.nextfour);
        nextfive = findViewById(R.id.nextfive);
        nextsix = findViewById(R.id.nextsix);
        nextseven = findViewById(R.id.nextseven);
        nexteight = findViewById(R.id.nexteight);

        linearone = findViewById(R.id.linearone);
        lineartwo = findViewById(R.id.lineartwo);
        linearthree = findViewById(R.id.linearthree);
        linearfour = findViewById(R.id.linearfour);
        linearfive = findViewById(R.id.linearfive);
        linearsix = findViewById(R.id.linearsix);
        linearseven = findViewById(R.id.linearseven);
        lineareight = findViewById(R.id.lineareight);

        backbutton = findViewById(R.id.backbutton);
        view_all = findViewById(R.id.view_all);
        delhi = findViewById(R.id.delhi);
        mumbai = findViewById(R.id.mumbai);
        pune = findViewById(R.id.pune);
        banglore = findViewById(R.id.banglore);
        chennai = findViewById(R.id.chennai);
        jaipur = findViewById(R.id.jaipur);

        spinnercompanytype = findViewById(R.id.spinnercompanytype);
        spinnernaturebusiness = findViewById(R.id.spinnernaturebusiness);
        spinnerindustrytype = findViewById(R.id.spinnerindustrytype);
        spinnersubindustrytype = findViewById(R.id.spinnersubindustrytype);

        selfbusiness = findViewById(R.id.selfbusiness);
        selfproffessional = findViewById(R.id.selfproffessional);
        turnover_btw_10_40_lac = findViewById(R.id.turnover_btw_10_40_lac);
        turnover_btw_40_lac_1_cr = findViewById(R.id.turnover_btw_40_lac_1_cr);
        turnover_btw_1_3_cr = findViewById(R.id.turnover_btw_1_3_cr);
        turnover_over_3_cr = findViewById(R.id.turnover_over_3_cr);
        ownedbyself = findViewById(R.id.ownedbyself);
        ownedbyparents = findViewById(R.id.ownedbyparents);
        rentedfamily = findViewById(R.id.rentedfamily);
        property = findViewById(R.id.property);
        gold = findViewById(R.id.gold);
        car = findViewById(R.id.car);
        billdiscounting = findViewById(R.id.billdiscounting);
        no = findViewById(R.id.no);


        spinnercompanytype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    str_company_type = spinnercompanytype.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinnernaturebusiness.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    str_nature_business = spinnernaturebusiness.getSelectedItem().toString();
                    get_industry_type_list();
                    str_industry_type="";

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerindustrytype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    str_industry_type = spinnerindustrytype.getSelectedItem().toString();
                    get_sub_industry_type_list();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnersubindustrytype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    str_sub_industry_type = spinnersubindustrytype.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        selfbusiness.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    str_occupation = "2";
                }
            }
        });
        selfproffessional.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    str_occupation = "1";
                }
            }
        });
        turnover_btw_10_40_lac.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    str_annual_turnover = "1";
                }
            }
        });
        turnover_btw_40_lac_1_cr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    str_annual_turnover = "2";
                }
            }
        });
        turnover_btw_1_3_cr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    str_annual_turnover = "3";
                }
            }
        });
        turnover_over_3_cr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    str_annual_turnover = "4";
                }
            }
        });

        ownedbyself.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    str_business_place = ownedbyself.getText().toString();
                }
            }
        });
        ownedbyparents.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    str_business_place = ownedbyparents.getText().toString();
                }
            }
        });
        rentedfamily.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    str_business_place = rentedfamily.getText().toString();
                }
            }
        });
        property.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    str_collateral_loan = property.getText().toString();
                }
            }
        });
        gold.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    str_collateral_loan = gold.getText().toString();
                }
            }
        });
        car.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    str_collateral_loan = car.getText().toString();
                }
            }
        });
        billdiscounting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    str_collateral_loan = billdiscounting.getText().toString();
                }
            }
        });
        no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    str_collateral_loan = no.getText().toString();
                }
            }
        });

        delhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_cityname = "Delhi";
                SessionManager.save_city(prefs, str_cityname);
                delhi.setBackgroundResource(R.drawable.textviewback);
                mumbai.setBackgroundResource(R.drawable.textviewback_white);
                pune.setBackgroundResource(R.drawable.textviewback_white);
                chennai.setBackgroundResource(R.drawable.textviewback_white);
                banglore.setBackgroundResource(R.drawable.textviewback_white);
                jaipur.setBackgroundResource(R.drawable.textviewback_white);
            }
        });
        mumbai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_cityname = "Mumbai";
                SessionManager.save_city(prefs, str_cityname);
                mumbai.setBackgroundResource(R.drawable.textviewback);
                delhi.setBackgroundResource(R.drawable.textviewback_white);
                pune.setBackgroundResource(R.drawable.textviewback_white);
                chennai.setBackgroundResource(R.drawable.textviewback_white);
                banglore.setBackgroundResource(R.drawable.textviewback_white);
                jaipur.setBackgroundResource(R.drawable.textviewback_white);
            }
        });
        pune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_cityname = "Pune";
                SessionManager.save_city(prefs, str_cityname);
                pune.setBackgroundResource(R.drawable.textviewback);
                mumbai.setBackgroundResource(R.drawable.textviewback_white);
                delhi.setBackgroundResource(R.drawable.textviewback_white);
                chennai.setBackgroundResource(R.drawable.textviewback_white);
                banglore.setBackgroundResource(R.drawable.textviewback_white);
                jaipur.setBackgroundResource(R.drawable.textviewback_white);
            }
        });
        banglore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_cityname = "Banglore";
                SessionManager.save_city(prefs, str_cityname);
                banglore.setBackgroundResource(R.drawable.textviewback);
                mumbai.setBackgroundResource(R.drawable.textviewback_white);
                pune.setBackgroundResource(R.drawable.textviewback_white);
                chennai.setBackgroundResource(R.drawable.textviewback_white);
                delhi.setBackgroundResource(R.drawable.textviewback_white);
                jaipur.setBackgroundResource(R.drawable.textviewback_white);
            }
        });
        chennai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_cityname = "Chennai";
                SessionManager.save_city(prefs, str_cityname);
                chennai.setBackgroundResource(R.drawable.textviewback);
                mumbai.setBackgroundResource(R.drawable.textviewback_white);
                pune.setBackgroundResource(R.drawable.textviewback_white);
                delhi.setBackgroundResource(R.drawable.textviewback_white);
                banglore.setBackgroundResource(R.drawable.textviewback_white);
                jaipur.setBackgroundResource(R.drawable.textviewback_white);
            }
        });
        jaipur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_cityname = "Jaipur";
                SessionManager.save_city(prefs, str_cityname);
                jaipur.setBackgroundResource(R.drawable.textviewback);
                mumbai.setBackgroundResource(R.drawable.textviewback_white);
                pune.setBackgroundResource(R.drawable.textviewback_white);
                chennai.setBackgroundResource(R.drawable.textviewback_white);
                banglore.setBackgroundResource(R.drawable.textviewback_white);
                delhi.setBackgroundResource(R.drawable.textviewback_white);
            }
        });

        view_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoanInformationPage.this, SelectCityPage.class);
                startActivity(intent);
            }
        });


        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (page == 1) {

                    finish();

                } else if (page == 2) {
                    linearone.setVisibility(View.VISIBLE);
                    lineartwo.setVisibility(View.GONE);
                    linearthree.setVisibility(View.GONE);
                    linearfour.setVisibility(View.GONE);
                    linearfive.setVisibility(View.GONE);
                    linearsix.setVisibility(View.GONE);
                    linearseven.setVisibility(View.GONE);
                    lineareight.setVisibility(View.GONE);
                    page = 1;

                } else if (page == 3) {

                    linearone.setVisibility(View.GONE);
                    lineartwo.setVisibility(View.VISIBLE);
                    linearthree.setVisibility(View.GONE);
                    linearfour.setVisibility(View.GONE);
                    linearfive.setVisibility(View.GONE);
                    linearsix.setVisibility(View.GONE);
                    linearseven.setVisibility(View.GONE);
                    lineareight.setVisibility(View.GONE);
                    page = 2;

                } else if (page == 4) {

                    linearone.setVisibility(View.GONE);
                    lineartwo.setVisibility(View.GONE);
                    linearthree.setVisibility(View.VISIBLE);
                    linearfour.setVisibility(View.GONE);
                    linearfive.setVisibility(View.GONE);
                    linearsix.setVisibility(View.GONE);
                    linearseven.setVisibility(View.GONE);
                    lineareight.setVisibility(View.GONE);
                    page = 3;

                } else if (page == 5) {

                    linearone.setVisibility(View.GONE);
                    lineartwo.setVisibility(View.GONE);
                    linearthree.setVisibility(View.GONE);
                    linearfour.setVisibility(View.VISIBLE);
                    linearfive.setVisibility(View.GONE);
                    linearsix.setVisibility(View.GONE);
                    linearseven.setVisibility(View.GONE);
                    lineareight.setVisibility(View.GONE);
                    page = 4;

                } else if (page == 6) {

                    linearone.setVisibility(View.GONE);
                    lineartwo.setVisibility(View.GONE);
                    linearthree.setVisibility(View.GONE);
                    linearfour.setVisibility(View.GONE);
                    linearfive.setVisibility(View.VISIBLE);
                    linearsix.setVisibility(View.GONE);
                    linearseven.setVisibility(View.GONE);
                    lineareight.setVisibility(View.GONE);
                    page = 5;

                } else if (page == 7) {

                    linearone.setVisibility(View.GONE);
                    lineartwo.setVisibility(View.GONE);
                    linearthree.setVisibility(View.GONE);
                    linearfour.setVisibility(View.GONE);
                    linearfive.setVisibility(View.GONE);
                    linearsix.setVisibility(View.VISIBLE);
                    linearseven.setVisibility(View.GONE);
                    lineareight.setVisibility(View.GONE);
                    page = 6;

                } else if (page == 8) {

                    linearone.setVisibility(View.GONE);
                    lineartwo.setVisibility(View.GONE);
                    linearthree.setVisibility(View.GONE);
                    linearfour.setVisibility(View.GONE);
                    linearfive.setVisibility(View.GONE);
                    linearsix.setVisibility(View.GONE);
                    linearseven.setVisibility(View.VISIBLE);
                    lineareight.setVisibility(View.GONE);
                    page = 7;

                }

            }
        });

        nextone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (str_occupation.equalsIgnoreCase("2")) {
                    linearone.setVisibility(View.GONE);
                    lineartwo.setVisibility(View.VISIBLE);
                    linearthree.setVisibility(View.GONE);
                    linearfour.setVisibility(View.GONE);
                    linearfive.setVisibility(View.GONE);
                    linearsix.setVisibility(View.GONE);
                    linearseven.setVisibility(View.GONE);
                    lineareight.setVisibility(View.GONE);
                    page = 2;
                    SessionManager.save_occupation(prefs, str_occupation);
                } else {
                    Toast.makeText(getApplicationContext(), "Under Dev", Toast.LENGTH_SHORT).show();
                }

            }
        });

        nexttwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (loanamount.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(LoanInformationPage.this, "Enter Loan Amount", Toast.LENGTH_SHORT).show();
                } else {
                    linearone.setVisibility(View.GONE);
                    lineartwo.setVisibility(View.GONE);
                    linearthree.setVisibility(View.VISIBLE);
                    linearfour.setVisibility(View.GONE);
                    linearfive.setVisibility(View.GONE);
                    linearsix.setVisibility(View.GONE);
                    linearseven.setVisibility(View.GONE);
                    lineareight.setVisibility(View.GONE);
                    page = 3;
                    SessionManager.save_loanamount(prefs, loanamount.getText().toString());
                }

            }
        });

        nextthree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                linearone.setVisibility(View.GONE);
                lineartwo.setVisibility(View.GONE);
                linearthree.setVisibility(View.GONE);
                linearfour.setVisibility(View.VISIBLE);
                linearfive.setVisibility(View.GONE);
                linearsix.setVisibility(View.GONE);
                linearseven.setVisibility(View.GONE);
                lineareight.setVisibility(View.GONE);
                page = 4;
                SessionManager.save_annualturnover(prefs, str_annual_turnover);

            }
        });

        nextfour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                linearone.setVisibility(View.GONE);
                lineartwo.setVisibility(View.GONE);
                linearthree.setVisibility(View.GONE);
                linearfour.setVisibility(View.GONE);
                linearfive.setVisibility(View.VISIBLE);
                linearsix.setVisibility(View.GONE);
                linearseven.setVisibility(View.GONE);
                lineareight.setVisibility(View.GONE);
                page = 5;

//                Toast.makeText(LoanInformationPage.this,  SessionManager.get_city(prefs), Toast.LENGTH_SHORT).show();


            }
        });

        nextfive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (businessyears.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(LoanInformationPage.this, "Enter years", Toast.LENGTH_SHORT).show();
                } else {
                    linearone.setVisibility(View.GONE);
                    lineartwo.setVisibility(View.GONE);
                    linearthree.setVisibility(View.GONE);
                    linearfour.setVisibility(View.GONE);
                    linearfive.setVisibility(View.GONE);
                    linearsix.setVisibility(View.VISIBLE);
                    linearseven.setVisibility(View.GONE);
                    lineareight.setVisibility(View.GONE);
                    page = 6;
                    SessionManager.save_business_year(prefs, businessyears.getText().toString());
                }

            }
        });

        nextsix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (str_company_type.equalsIgnoreCase("")) {
                    Toast.makeText(LoanInformationPage.this, "Select company type", Toast.LENGTH_SHORT).show();
                } else if (str_nature_business.equalsIgnoreCase("")) {
                    Toast.makeText(LoanInformationPage.this, "Select nature of business", Toast.LENGTH_SHORT).show();
                } else if (str_industry_type.equalsIgnoreCase("")) {
                    Toast.makeText(LoanInformationPage.this, "Select industry type", Toast.LENGTH_SHORT).show();
                } else if (str_sub_industry_type.equalsIgnoreCase("")) {
                    Toast.makeText(LoanInformationPage.this, "Select sub-industry type", Toast.LENGTH_SHORT).show();
                } else {

                    SessionManager.save_company_type(prefs, str_company_type);
                    SessionManager.save_nature_business(prefs, str_nature_business);
                    SessionManager.save_industry_type(prefs, str_industry_type);
                    SessionManager.save_sub_industry_type(prefs, str_sub_industry_type);

                    linearone.setVisibility(View.GONE);
                    lineartwo.setVisibility(View.GONE);
                    linearthree.setVisibility(View.GONE);
                    linearfour.setVisibility(View.GONE);
                    linearfive.setVisibility(View.GONE);
                    linearsix.setVisibility(View.GONE);
                    linearseven.setVisibility(View.VISIBLE);
                    lineareight.setVisibility(View.GONE);
                    page = 7;

                }


            }
        });

        nextseven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                linearone.setVisibility(View.GONE);
                lineartwo.setVisibility(View.GONE);
                linearthree.setVisibility(View.GONE);
                linearfour.setVisibility(View.GONE);
                linearfive.setVisibility(View.GONE);
                linearsix.setVisibility(View.GONE);
                linearseven.setVisibility(View.GONE);
                lineareight.setVisibility(View.VISIBLE);
                page = 8;
                SessionManager.save_ownership_residence(prefs, str_business_place);


            }
        });

        nexteight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SessionManager.save_collatoral_loan(prefs, str_collateral_loan);

                Intent intent = new Intent(LoanInformationPage.this, PersonalInformationPage.class);
                startActivity(intent);
            }
        });

        loanamount.addTextChangedListener(textWatcher1);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub

                progress = Math.round(progress / stepSize) * stepSize;
                seekBar.setProgress(progress);
                loanamount.setText("" + progress);
                SessionManager.save_loanamount(prefs, loanamount.getText().toString());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });

    }

    TextWatcher textWatcher1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            try {
                if (s.length() != 0) {
                    if (s.length() >= 5) {
                        seekBar.setProgress(Integer.parseInt(s.toString()));
                        loanamount.setSelection(s.length());
                    }
                }
            } catch (Exception ignored) {

            }
        }

        @Override
        public void afterTextChanged(Editable s) {


        }
    };


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

                    } else if (is_returning_customer.equalsIgnoreCase("1")) {

                    } else if (cibil_status.equalsIgnoreCase("success") && apicall.equalsIgnoreCase("cibil-customer-assets")) {

                        customerassets();

                    } else if (cibil_status.equalsIgnoreCase("Inprogress") && apicall.equalsIgnoreCase("cibil-authentication-questions") || (cibil_status.equalsIgnoreCase("Pending") && apicall.equalsIgnoreCase("cibil-authentication-questions"))) {

                    } else if (cibil_status.equalsIgnoreCase("failed")) {

                        String message = "";
                        try {
                            message = jsonObject1.getString("message");
                        } catch (Exception e) {
                            JSONObject jsonObject2 = jsonObject1.getJSONObject("RequestError");
                            message = jsonObject2.getString("NO_HIT");
                            get_cibil_fulfill_order("NO_HIT");
                        }

                        Toast.makeText(LoanInformationPage.this, message, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(LoanInformationPage.this, jsonObject2.getString("cibil_no_success"), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        get_cibil_history();
    }

    public void get_industry_type_list() {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = BuildConfig.BASE_URL + "/get-industry-by-nature/" + str_nature_business;
        StringRequest getRequest = new StringRequest(Request.Method.GET, url, response -> {

            try {
                progressDialog.dismiss();
                JSONObject jsonObject = new JSONObject(response);
                list1 = new ArrayList<>();
                list1.add("Select");
                list2=new ArrayList<>();

                if (jsonObject.getString("status").equalsIgnoreCase("Success")) {

                    JSONArray jsonArray = (jsonObject.getJSONArray("result"));
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject objectnew2 = jsonArray.getJSONObject(i);
                        list1.add(objectnew2.getString("name"));

                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list1);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerindustrytype.setAdapter(arrayAdapter);

                } else if (jsonObject.getString("status").equalsIgnoreCase("failed")) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(LoanInformationPage.this, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();

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

    public void get_sub_industry_type_list() {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = BuildConfig.BASE_URL + "/bussiness-loan-create/" + str_industry_type;
        StringRequest getRequest = new StringRequest(Request.Method.GET, url, response -> {

            try {
                progressDialog.dismiss();
                JSONObject jsonObject = new JSONObject(response);

                list2 = new ArrayList<>();
                list2.add("Select");

                if (jsonObject.getString("status").equalsIgnoreCase("Success")) {

                    JSONArray jsonArray = (jsonObject.getJSONArray("result"));
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject objectnew2 = jsonArray.getJSONObject(i);
                        list2.add(objectnew2.getString("name"));

                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list2);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnersubindustrytype.setAdapter(arrayAdapter);

                } else if (jsonObject.getString("status").equalsIgnoreCase("failed")) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(LoanInformationPage.this, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();

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


}
