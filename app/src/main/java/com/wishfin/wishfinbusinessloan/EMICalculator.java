package com.wishfin.wishfinbusinessloan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import im.dacer.androidcharts.PieHelper;
import im.dacer.androidcharts.PieView;

public class EMICalculator extends AppCompatActivity {

    LinearLayout line1, line5;
    PieView pieView;
    SeekBar loan_seekbar, int_rate_seekbar, tenure_seekbar;
    String tenure = "15";
    KProgressHUD progressDialog;
    SharedPreferences prefs;
    RequestQueue queue;
    TextView emirs, totalint, totalamount, amountinwords, tenure_value, interestrate_value, signupone;
    ImageView backbutton;
    EditText loanamount_value;
    ArrayList<Gettersetterforall> list1 = new ArrayList<>();
    Share_Adapter radio_question_list_adapter;
    RecyclerView article_list;

    String interest_per,loan_per;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emicalci);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        progressDialog = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel(getString(R.string.dialogtext)).setCancellable(false).setAnimationSpeed(1).setDimAmount(0.5f);

        queue = Volley.newRequestQueue(EMICalculator.this);
        prefs = PreferenceManager.getDefaultSharedPreferences(EMICalculator.this);

        line1 = findViewById(R.id.line1);
        line5 = findViewById(R.id.line5);
        backbutton = findViewById(R.id.backbutton);
        loan_seekbar = findViewById(R.id.loan_seekbar);
        int_rate_seekbar = findViewById(R.id.int_rate_seekbar);
        tenure_seekbar = findViewById(R.id.tenure_seekbar);
        emirs = findViewById(R.id.emirs);
        totalint = findViewById(R.id.totalint);
        totalamount = findViewById(R.id.totalamount);
        loanamount_value = findViewById(R.id.loanamount_value);
        interestrate_value = findViewById(R.id.interestrate_value);
        tenure_value = findViewById(R.id.tenure_value);
        amountinwords = findViewById(R.id.amountinwords);
        signupone = findViewById(R.id.signupone);

        article_list = findViewById(R.id.article_list);
        pieView = findViewById(R.id.pie_view);
        ArrayList<PieHelper> pieHelperArrayList = new ArrayList<>();
        pieView.setDate(pieHelperArrayList);



        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        line1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(EMICalculator.this, Dashboard.class);
                startActivity(intent2);
                finish();
            }
        });
        line5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(EMICalculator.this, Profilepage.class);
                startActivity(intent2);
            }
        });



        LinearLayoutManager layoutManager1 = new LinearLayoutManager(EMICalculator.this) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(EMICalculator.this) {
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


        signupone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                get_quote();
            }
        });


        loanamount_value.setText("5000000");
        amountinwords.setText(EnglishNumberToWords.convert(5000000));
        interestrate_value.setText("10.5");
        tenure_value.setText("15 years");
        progressDialog.show();
        get_quote();

        loanamount_value.addTextChangedListener(new MyTextWatcher(loanamount_value));
        interestrate_value.addTextChangedListener(new MyTextWatcher(interestrate_value));

        loan_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                loanamount_value.setText("" + progress);
                amountinwords.setText(EnglishNumberToWords.convert(progress));
                loanamount_value.setSelection(loanamount_value.getText().length());


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        int_rate_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                interestrate_value.setText("" + (float) progress / 10.0);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        tenure_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                tenure = String.valueOf(progress);
                tenure_value.setText(tenure + " years");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    public void get_quote() {
        final JSONObject json = new JSONObject();
        try {
            json.put("interest_rate", interestrate_value.getText().toString());
            json.put("loan_amount", loanamount_value.getText().toString());
            json.put("tenure", tenure);
            json.put("type", "undefined");
            json.put("emidata", "2");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BuildConfig.BASE_URL + "/emi-calculator", json, response -> {
            try {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                JSONObject jsonObject = new JSONObject(response.toString());
                list1 = new ArrayList<>();

                if (jsonObject.getString("status").equalsIgnoreCase("Success")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    interest_per=jsonObject1.getString("interestPercent");
                    loan_per=jsonObject1.getString("principalPercent");

                    JSONArray jsonArray = (jsonObject1.getJSONArray("tabularCalulation"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject objectnew2 = jsonArray.getJSONObject(i);
                        Gettersetterforall pack = new Gettersetterforall();

                        int emiamount= Integer.parseInt(objectnew2.getString("monthlyEMI"));
                        emirs.setText("₹ " + getFormatedAmount(emiamount));
                        int totalintamount= Integer.parseInt(objectnew2.getString("totalInterest"));
                        totalint.setText("₹ " + getFormatedAmount(totalintamount));
                        int totalamountamount= Integer.parseInt(objectnew2.getString("totalAmount"));
                        totalamount.setText("₹ " + getFormatedAmount(totalamountamount));
                        pack.setPricipal(objectnew2.getString("pricipal"));
                        pack.setInterestamount(objectnew2.getString("interestRate"));
                        pack.setBalanceamount(objectnew2.getString("balanceAmount"));
                        list1.add(pack);
                    }

                    randomSet(pieView);

                    radio_question_list_adapter = new Share_Adapter(EMICalculator.this, list1);
                    article_list.setAdapter(radio_question_list_adapter);

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
                get_quote();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
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
                case R.id.loanamount_value:
                    if (loanamount_value.getText().length() > 6) {
                        int value = Integer.parseInt(loanamount_value.getText().toString());
                        loan_seekbar.setProgress(value);
                        signupone.setVisibility(View.VISIBLE);
                    } else {
                        amountinwords.setText("Min value is 1 million");
                        signupone.setVisibility(View.GONE);
                    }
                    break;
                case R.id.interestrate_value:
//                    float value1= Float.parseFloat(interestrate_value.getText().toString());
//                    int_rate_seekbar.setProgress((int) value1);

                    break;


            }
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
            TextView interestamount, pricipalamount, balanceamount, srnumber;

            MyViewHolder(View view) {
                super(view);

                imageView = view.findViewById(R.id.imageView);
                pricipalamount = view.findViewById(R.id.pricipalamount);
                interestamount = view.findViewById(R.id.interestamount);
                balanceamount = view.findViewById(R.id.balanceamount);
                srnumber = view.findViewById(R.id.srnumber);

            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.emi_list_adapter, parent, false);

            return new MyViewHolder(itemView);
        }

        @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
        @Override
        public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            int principalamount= (Integer.parseInt(list_car.get(position).getPricipal()));
            holder.pricipalamount.setText(getFormatedAmount(principalamount));
            float interest= Float.parseFloat(list_car.get(position).getInterestamount());
            int value= (int) interest;
            holder.interestamount.setText(""+getFormatedAmount(value));
            int balanceamount= Integer.parseInt((list_car.get(position).getBalanceamount()));
            holder.balanceamount.setText(getFormatedAmount(balanceamount));
            int years=position + 1;
            holder.srnumber.setText(""+years);

        }

        @Override
        public int getItemCount() {
            return list_car.size();
        }

    }

    private void randomSet(PieView pieView) {
        ArrayList<PieHelper> pieHelperArrayList = new ArrayList<>();
        float interestval= Float.parseFloat(interest_per);
        float loanval= Float.parseFloat(loan_per);
        int greencolor=Color.parseColor("#4CAF50");
        int greycolor=Color.parseColor("#7C7979");
        pieHelperArrayList.add(new PieHelper(interestval, greencolor));
        pieHelperArrayList.add(new PieHelper(loanval, greycolor));
        pieView.selectedPie(PieView.NO_SELECTED_INDEX);
        pieView.showPercentLabel(true);
        pieView.setDate(pieHelperArrayList);
    }

    private String getFormatedAmount(int amount){
        return NumberFormat.getNumberInstance(Locale.US).format(amount);
    }


}