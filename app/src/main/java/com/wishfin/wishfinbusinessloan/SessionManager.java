package com.wishfin.wishfinbusinessloan;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {

    private static String ACCESS_TOKEN = "access_token";
    private static String DEVICE_TOKEN = "device_token";
    private static String EXPIRE_IN = "expires_in";
    private static String TOKEN_TYPE = "token_type";
    private static String REFRESH_TOKEN = "refresh_token";
    private static String SECRET_KEY = "secret_key";
    private static String CIBIL_ID = "cibil_id";
    private static String LEAD_ID = "lead_id";
    private static String CIBIL_SCORE = "cibil_score";
    private static String CIBIL_SCORE_CHECKED_OR_NOT = "cibil_score_checked_or_not";
    private static String CIBIL_FETCH_DATE = "cibil_date";
    private static String LOGIN = "login";
    private static String MASTER_USER_ID = "master_user_id";
    private static String MF_USER_ID = "mf_user_id";
    private static String FIRST_NAME = "first_name";
    private static String DISPLAY_NAME = "display_name";
    private static String DOB = "dob";
    private static String PAN = "pan";
    private static String MOBILE = "mobile";
    private static String EMAILID = "emailid";
    private static String APPVERSION = "app_version";
    private static String LASTSMSDATE = "last_sms_date";
    private static String LOGINTYPE = "login_type";
    private static String MIDDLE_NAME = "middle_name";
    private static String LAST_NAME = "last_name";
    private static String APPINSTALLDATE = "app_install_date";
    private static String HARDINQUIRY = "hard_inquiry";
    private static String APP_TIME = "app_time";
    private static String APP_LANG = "app_lang";
    private static String COMPANY_NAME = "company_name";
    private static String OCCUPATION = "occupation";
    private static String MONTHLY_INCOME = "monthly_income";
    private static String MARITAL_STATUS = "marital_status";
    private static String EMPLOYER_SECTOR = "employer_sector";
    private static String BUSINESS_NATURE = "business_nature";
    private static String OWNERSHIP_TYPE = "ownership_type";
    private static String INCOME_SOURCE = "income_source";
    private static String EDUCATION = "education";
    private static String WORK_EXP = "work_exp";
    private static String MOTHER_NAME = "mother_name";
    private static String GENDER = "gender";
    private static String CITY = "city";
    private static String LOAN_AMOUNT = "loanamount";
    private static String ANNUAL_TURNOVER = "annualturnover";

    private static String BUSINESS_YEAR = "business_year";
    private static String COMPANY_TYPE = "company_type";
    private static String NATURE_OF_BUSINESS = "nature_of_business";
    private static String INDUSTRY_TYPE = "industry_type";
    private static String SUB_INDUSTRY_TYPE = "sub_industry_type";
    private static String OWNERSHIP_RESIDENCE = "ownership_residence";
    private static String COLLATORAL_LOAN = "collatoral_loan";

    static void savePreference(SharedPreferences prefs, String key, Boolean value) {
        Editor e = prefs.edit();
        e.putBoolean(key, value);
        e.apply();
    }

    static void savePreference(SharedPreferences prefs, String key, int value) {
        Editor e = prefs.edit();
        e.putInt(key, value);
        e.apply();
    }

    private static void savePreference(SharedPreferences prefs, String key, String value) {
        Editor e = prefs.edit();
        e.putString(key, value);
        e.apply();
    }

    static void dataclear(SharedPreferences prefs) {
        Editor e = prefs.edit();
        e.clear();
        e.apply();
    }


    static void save_access_token(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, ACCESS_TOKEN, value);
    }

    public static String get_access_token(SharedPreferences prefs) {
        return prefs.getString(ACCESS_TOKEN, "");
    }

    static void save_device_token(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, DEVICE_TOKEN, value);
    }

    public static String get_device_token(SharedPreferences prefs) {
        return prefs.getString(DEVICE_TOKEN, "");
    }

    static void save_expirein(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, EXPIRE_IN, value);
    }

    public static String get_expirein(SharedPreferences prefs) {
        return prefs.getString(EXPIRE_IN, "");
    }

    static void save_token_type(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, TOKEN_TYPE, value);
    }

    public static String get_token_type(SharedPreferences prefs) {
        return prefs.getString(TOKEN_TYPE, "");
    }

    static void save_refresh_token(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, REFRESH_TOKEN, value);
    }

    public static String get_refresh_token(SharedPreferences prefs) {
        return prefs.getString(REFRESH_TOKEN, "");
    }


    static void save_secret_key(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, SECRET_KEY, value);
    }

    static String get_secret_key(SharedPreferences prefs) {
        return prefs.getString(SECRET_KEY, "");
    }

    static void save_cibil_id(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, CIBIL_ID, value);
    }

    public static String get_cibil_id(SharedPreferences prefs) {
        return prefs.getString(CIBIL_ID, "");
    }

    static void save_lead_id(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, LEAD_ID, value);
    }

    public static String get_lead_id(SharedPreferences prefs) {
        return prefs.getString(LEAD_ID, "");
    }


    static void save_cibil_score(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, CIBIL_SCORE, value);
    }

    static String get_cibil_score(SharedPreferences prefs) {
        return prefs.getString(CIBIL_SCORE, "");
    }

    static void save_cibil_fetch_date(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, CIBIL_FETCH_DATE, value);
    }

    static String get_cibil_fetch_date(SharedPreferences prefs) {
        return prefs.getString(CIBIL_FETCH_DATE, "");
    }

    static void save_login(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, LOGIN, value);
    }

    static String get_login(SharedPreferences prefs) {
        return prefs.getString(LOGIN, "");
    }

    static void save_masteruserid(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, MASTER_USER_ID, value);
    }

    static String get_masteruserid(SharedPreferences prefs) {
        return prefs.getString(MASTER_USER_ID, "");
    }


    static void save_mfuserid(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, MF_USER_ID, value);
    }

    static String get_mfuserid(SharedPreferences prefs) {
        return prefs.getString(MF_USER_ID, "");
    }

    static void save_firstname(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, FIRST_NAME, value);
    }

    public static String get_firstname(SharedPreferences prefs) {
        return prefs.getString(FIRST_NAME, "");
    }

    static void save_dob(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, DOB, value);
    }

    static String get_dob(SharedPreferences prefs) {
        return prefs.getString(DOB, "");
    }

    static void save_pan(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, PAN, value);
    }

    static String get_pan(SharedPreferences prefs) {
        return prefs.getString(PAN, "");
    }

    static void save_mobile(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, MOBILE, value);
    }

    public static String get_mobile(SharedPreferences prefs) {
        return prefs.getString(MOBILE, "");
    }

    static void save_emailid(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, EMAILID, value);
    }

    public static String get_emailid(SharedPreferences prefs) {
        return prefs.getString(EMAILID, "");
    }


    static void save_appversion(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, APPVERSION, value);
    }

    static String get_appversion(SharedPreferences prefs) {
        return prefs.getString(APPVERSION, "");
    }



    static void save_hardinquiry(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, HARDINQUIRY, value);
    }

    static String get_hardinquiry(SharedPreferences prefs) {
        return prefs.getString(HARDINQUIRY, "");
    }


//    static void save_lastccapplydate(SharedPreferences prefs, String value) {
//        com.wishfin_credit_card.SessionManager.savePreference(prefs, LASTSMSDATE, value);
//    }
//
//    static String get_lastccapplydate(SharedPreferences prefs) {
//        return prefs.getString(LASTSMSDATE, "");
//    }

//    static void save_lead_id(SharedPreferences prefs, String value) {
//        com.wishfin_credit_card.SessionManager.savePreference(prefs, APPINSTALLDATE, value);
//    }
//
//    static String get_lead_id(SharedPreferences prefs) {
//        return prefs.getString(APPINSTALLDATE, "");
//    }

//////////////////////////////////////////////


    public static void save_logintype(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, LOGINTYPE, value);
    }

    public static String get_logintype(SharedPreferences prefs) {
        return prefs.getString(LOGINTYPE, "");
    }

    static void save_mname(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, MIDDLE_NAME, value);
    }

    public static String get_mname(SharedPreferences prefs) {
        return prefs.getString(MIDDLE_NAME, "");
    }

    static void save_lastname(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, LAST_NAME, value);
    }

    public static String get_lastname(SharedPreferences prefs) {
        return prefs.getString(LAST_NAME, "");
    }

    static void save_cibil_checked_status(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, CIBIL_SCORE_CHECKED_OR_NOT, value);
    }

    public static String get_cibil_checked_status(SharedPreferences prefs) {
        return prefs.getString(CIBIL_SCORE_CHECKED_OR_NOT, "");
    }

    static void save_app_time(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, APP_TIME, value);
    }

    public static String get_app_time(SharedPreferences prefs) {
        return prefs.getString(APP_TIME, "");
    }

    static void save_app_lang(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, APP_LANG, value);
    }

    public static String get_app_lang(SharedPreferences prefs) {
        return prefs.getString(APP_LANG, "");
    }

    static void save_company_name(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, COMPANY_NAME, value);
    }

    public static String get_company_name(SharedPreferences prefs) {
        return prefs.getString(COMPANY_NAME, "");
    }


    static void save_occupation(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, OCCUPATION, value);
    }

    public static String get_occupation(SharedPreferences prefs) {
        return prefs.getString(OCCUPATION, "");
    }


    static void save_city(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, CITY, value);
    }

    public static String get_city(SharedPreferences prefs) {
        return prefs.getString(CITY, "");
    }

    static void save_monthly_income(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, MONTHLY_INCOME, value);
    }

    public static String get_monthly_income(SharedPreferences prefs) {
        return prefs.getString(MONTHLY_INCOME, "");
    }

    static void save_display_name(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, DISPLAY_NAME, value);
    }

    public static String get_display_name(SharedPreferences prefs) {
        return prefs.getString(DISPLAY_NAME, "");
    }

    static void save_mother_name(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, MOTHER_NAME, value);
    }

    public static String get_mother_name(SharedPreferences prefs) {
        return prefs.getString(MOTHER_NAME, "");
    }

    static void save_gender(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, GENDER, value);
    }

    public static String get_gender(SharedPreferences prefs) {
        return prefs.getString(GENDER, "");
    }

    static void save_work_exp(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, WORK_EXP, value);
    }

    public static String get_work_exp(SharedPreferences prefs) {
        return prefs.getString(WORK_EXP, "");
    }

    static void save_income_source(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, INCOME_SOURCE, value);
    }

    public static String get_income_source(SharedPreferences prefs) {
        return prefs.getString(INCOME_SOURCE, "");
    }

    static void save_employer_sector(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, EMPLOYER_SECTOR, value);
    }

    public static String get_employer_sector(SharedPreferences prefs) {
        return prefs.getString(EMPLOYER_SECTOR, "");
    }

    static void save_education(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, EDUCATION, value);
    }

    public static String get_education(SharedPreferences prefs) {
        return prefs.getString(EDUCATION, "");
    }

    static void save_marital_status(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, MARITAL_STATUS, value);
    }

    public static String get_marital_status(SharedPreferences prefs) {
        return prefs.getString(MARITAL_STATUS, "");
    }

    static void save_businessnature(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, BUSINESS_NATURE, value);
    }

    public static String get_businessnature(SharedPreferences prefs) {
        return prefs.getString(BUSINESS_NATURE, "");
    }

    static void save_ownershiptype(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, OWNERSHIP_TYPE, value);
    }

    public static String get_ownershiptype(SharedPreferences prefs) {
        return prefs.getString(OWNERSHIP_TYPE, "");
    }

    static void save_industry_type(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, INDUSTRY_TYPE, value);
    }

    public static String get_industry_type(SharedPreferences prefs) {
        return prefs.getString(INDUSTRY_TYPE, "");
    }

    static void save_loanamount(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, LOAN_AMOUNT, value);
    }

    static String get_loanamount(SharedPreferences prefs) {
        return prefs.getString(LOAN_AMOUNT, "");
    }

    static void save_annualturnover(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, ANNUAL_TURNOVER, value);
    }

    static String get_annualturnover(SharedPreferences prefs) {
        return prefs.getString(ANNUAL_TURNOVER, "");
    }

    //////////////////////////
    static void save_business_year(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, BUSINESS_YEAR, value);
    }

    static String get_business_year(SharedPreferences prefs) {
        return prefs.getString(BUSINESS_YEAR, "");
    }


    static void save_nature_business(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, NATURE_OF_BUSINESS, value);
    }

    static String get_nature_business(SharedPreferences prefs) {
        return prefs.getString(NATURE_OF_BUSINESS, "");
    }

    static void save_sub_industry_type(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, SUB_INDUSTRY_TYPE, value);
    }

    static String get_sub_industry_type(SharedPreferences prefs) {
        return prefs.getString(SUB_INDUSTRY_TYPE, "");
    }

    static void save_company_type(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, COMPANY_TYPE, value);
    }

    static String get_company_type(SharedPreferences prefs) {
        return prefs.getString(COMPANY_TYPE, "");
    }

    static void save_ownership_residence(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, OWNERSHIP_RESIDENCE, value);
    }

    static String get_ownership_residence(SharedPreferences prefs) {
        return prefs.getString(OWNERSHIP_RESIDENCE, "");
    }

    static void save_collatoral_loan(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, COLLATORAL_LOAN, value);
    }

    static String get_collatoral_loan(SharedPreferences prefs) {
        return prefs.getString(COLLATORAL_LOAN, "");
    }

}