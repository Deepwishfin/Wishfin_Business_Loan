package com.wishfin.wishfinbusinessloan;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class WishFinAnalytics extends Analytics {

    private FirebaseAnalytics firebaseAnalytics;
    private final String OPEN_APP = "open_app_new";
    private final String APP_OPENED = "app_opened_new";
    private final String ACTION_APP_OPENED = "action_app_launch_new";
    private final String RATE_APP = "rate_app";
    private final String ACTION_RATE_APP = "action_share_app";
    private final String RATED_APP = "rated_app";
    private final String OPEN_CIBIL_PAGE = "cibil_score_fetched";
    private final String CIBIL_PAGE_OPENED = "cibil_score_fetched";
    private final String ACTION_CIBIL_PAGE_OPENED = "action_cibil_score_fetched_launch";
    public WishFinAnalytics(Context context) {
        _context = context;
        init(_context);
    }

    @Override
    public void init(Context context) {
        if (firebaseAnalytics == null)
            firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    @Override
    public void openApp() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, OPEN_APP);
        bundle.putString(ACTION_APP_OPENED, APP_OPENED);
        firebaseAnalytics.logEvent(OPEN_APP, bundle);
    }

    @Override
    public void cibilPage() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, OPEN_CIBIL_PAGE);
        bundle.putString(ACTION_CIBIL_PAGE_OPENED, CIBIL_PAGE_OPENED);
        firebaseAnalytics.logEvent(OPEN_CIBIL_PAGE, bundle);
    }

    @Override
    public void rateapp() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, RATE_APP);
        bundle.putString(ACTION_RATE_APP, RATED_APP);
        firebaseAnalytics.logEvent(RATE_APP, bundle);
    }


}
