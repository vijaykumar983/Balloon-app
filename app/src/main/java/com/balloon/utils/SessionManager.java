package com.balloon.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;


import static android.content.Context.MODE_PRIVATE;

public class SessionManager extends BaseObservable {
    private final String IS_LOGIN = "isLoggedIn";
    private final String AUTH_TOKEN = "auth_token";

    private final String USER_ID = "user_id";
    private final String FULL_NAME = "full_name";
    private final String PHONE = "phone";
    private final String DEVICE_ID = "device_id";
    private final String ADDRESS = "address";
    private final String BIO = "bio";
    private final String PROFILE_IMAGE = "profile_image";

    private final String LOCATION = "location";
    private final String LATITUDE = "latitude";
    private final String LONGITUDE = "longitude";
    private final String SELECT_LOCATION = "select_location";
    private final String FIREBASE_ID = "firebase_id";


    private static SharedPreferences shared;
    private static SharedPreferences.Editor editor;
    private static SessionManager session;

    public static SessionManager getInstance(Context context) {
        if (session == null) {
            session = new SessionManager();
        }
        if (shared == null) {
            shared = context.getSharedPreferences("BalloonApp", MODE_PRIVATE);
            editor = shared.edit();
        }
        return session;
    }


    public boolean isLogin() {
        return shared.getBoolean(IS_LOGIN, false);
    }

    public void setLogin() {
        editor.putBoolean(IS_LOGIN, true);
        editor.commit();
    }

    public String getAuthToken() {
        return shared.getString(AUTH_TOKEN, "");
    }

    public void setAuthToken(String authToken) {
        editor.putString(AUTH_TOKEN, authToken);
        editor.commit();
    }

    public String getLOCATION() {
        return shared.getString(LOCATION, "");
    }

    public void setLOCATION(String location) {
        editor.putString(LOCATION, location);
        editor.commit();
    }
    public String getLATITUDE() {
        return shared.getString(LATITUDE, "");
    }

    public void setLATITUDE(String latitude) {
        editor.putString(LATITUDE, latitude);
        editor.commit();
    }
    public String getLONGITUDE() {
        return shared.getString(LONGITUDE, "");
    }

    public void setLONGITUDE(String longitude) {
        editor.putString(LONGITUDE, longitude);
        editor.commit();
    }
    public boolean isSELECT_LOCATION() {
        return shared.getBoolean(SELECT_LOCATION, false);
    }

    public void setSELECT_LOCATION() {
        editor.putBoolean(SELECT_LOCATION, true);
        editor.commit();
    }

    public String getUSER_ID() {
        return shared.getString(USER_ID, "");
    }

    public void setUSER_ID(String userId) {
        editor.putString(USER_ID, userId);
        editor.commit();
    }
    public String getFULL_NAME() {
        return shared.getString(FULL_NAME, "");
    }

    public void setFULL_NAME(String fullName) {
        editor.putString(FULL_NAME, fullName);
        editor.commit();
    }

    public String getPHONE() {
        return shared.getString(PHONE, "");
    }

    public void setPHONE(String phone) {
        editor.putString(PHONE, phone);
        editor.commit();
    }
    public String getADDRESS() {
        return shared.getString(ADDRESS, "");
    }
    public void setADDRESS(String address) {
        editor.putString(ADDRESS, address);
        editor.commit();
    }
    public String getBIO() {
        return shared.getString(BIO, "");
    }
    public void setBIO(String bio) {
        editor.putString(BIO, bio);
        editor.commit();
    }
    public String getPROFILE_IMAGE() {
        return shared.getString(PROFILE_IMAGE, "");
    }

    public void setPROFILE_IMAGE(String profileImage) {
        editor.putString(PROFILE_IMAGE, profileImage);
        editor.commit();
    }

    public String getFIREBASE_ID() {
        return shared.getString(FIREBASE_ID, "");
    }

    public void setFIREBASE_ID(String firebase_id) {
        editor.putString(FIREBASE_ID, firebase_id);
        editor.commit();
    }


    /*@Bindable("data")
    public UserData.Data getUserData() {

        UserData.Data userData = new UserData.Data();
        userData.setId(shared.getString(USER_ID, ""));
        userData.setFullname(shared.getString(FULL_NAME, ""));
        userData.setEmail(shared.getString(EMAIL, ""));
        userData.setPhone(shared.getString(PHONE, ""));
        userData.setStatus(shared.getString(STATUS, ""));
        userData.setDeviceId(shared.getString(DEVICE_ID, ""));
        userData.setVerifyStatus(shared.getString(VERIFY_STATUS, ""));
        userData.setFirstName(shared.getString(FIRST_NAME, ""));
        userData.setLastName(shared.getString(LAST_NAME, ""));
        userData.setDob(shared.getString(DOB, ""));
        userData.setGender(shared.getString(GENDER, ""));
        userData.setReferCode(shared.getString(REFER_CODE, ""));
        userData.setWallet(shared.getString(WALLET, ""));
        userData.setRewallet(shared.getString(REWALLET, ""));
        userData.setAddress(shared.getString(ADDRESS, ""));
        userData.setProfileImage(shared.getString(PROFILE_IMAGE, ""));
        userData.setPin(shared.getString(PIN, ""));
        userData.setUpi(shared.getString(UPI, ""));
        userData.setDelete(shared.getString(DELETE, ""));
        userData.setAddphone(shared.getString(ADD_PHONE, ""));
        return userData;
    }

    @Bindable("data")
    public void setUserData(UserData.Data userData) {

        editor.putString(USER_ID, userData.getId());
        editor.putString(FULL_NAME, userData.getFullname());
        editor.putString(EMAIL, userData.getEmail());

        editor.putString(PHONE, userData.getPhone());
        editor.putString(STATUS, userData.getStatus());
        editor.putString(DEVICE_ID, userData.getDeviceId());
        editor.putString(VERIFY_STATUS, userData.getVerifyStatus());
        editor.putString(FIRST_NAME, userData.getFirstName());
        editor.putString(LAST_NAME, userData.getLastName());
        editor.putString(DOB, userData.getDob());
        editor.putString(GENDER, userData.getGender());
        editor.putString(REFER_CODE, userData.getReferCode());
        editor.putString(WALLET, userData.getWallet());
        editor.putString(REWALLET, userData.getRewallet());
        editor.putString(ADDRESS, userData.getAddress());
        editor.putString(PROFILE_IMAGE, userData.getProfileImage());
        editor.putString(PIN, userData.getPin());
        editor.putString(UPI, userData.getUpi());
        editor.putString(DELETE, userData.getDelete());
        editor.putString(ADD_PHONE, userData.getAddphone());
        editor.commit();
    }*/


    public void logout() {
        editor.putString(USER_ID, "");
        editor.putString(FULL_NAME, "");
        editor.putString(PHONE, "");
        editor.putString(BIO, "");
        editor.putString(DEVICE_ID, "");
        editor.putString(FIREBASE_ID, "");
        editor.putString(ADDRESS, "");
        editor.putString(PROFILE_IMAGE, "");
        editor.putString(LOCATION, "");
        editor.putBoolean(SELECT_LOCATION, false);
        editor.putString(LATITUDE, "");
        editor.putString(LONGITUDE, "");
        editor.putString(AUTH_TOKEN, "");
        editor.putBoolean(IS_LOGIN, false);
        editor.commit();
    }
}