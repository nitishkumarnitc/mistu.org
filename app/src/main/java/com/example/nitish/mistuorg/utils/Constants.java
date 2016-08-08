package com.example.nitish.mistuorg.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nitish on 25-07-2016.
 */
public class Constants {
    // server home url
    public static final String HOME_URL = "http://www.mistu.org/ihelp/";
    public static final String IMAGES_URL="http://www.mistu.org/ihelp/login/pictures/";
    public static final String SERVER_RESPONSE="server_response";
    public static final String NO_NET_WORK_CONNECTION="No NetWork Connection";
    //Fire base app url
    public static final String FIREBASE_URL = "https://mistu-6ebce.firebaseio.com";
    //Constant to store shared preferences
    public static final String SHARED_PREF = "user_info";
    public static final String NOTIF_SHARED_PREF="notif_shared_pref";
    //To store boolean in shared preferences for if the device is registered or not
    public static final String REGISTERED_WITH_FCM = "registered";
    //To store the fire base id in shared preferences
    public static final String FCM_UNIQUE_ID = "FCMID";
    //register.php address in your server
    public static final String FCM_REGISTER_URL = "http://www.mistu.org/ihelp/pushnotification/register.php/";
    public static final String KEY_SUCCESS="success";
    public static final String KEY_ERROR="error";

    public static final String GOOGLE_UID="google_uid";
    public static final String LOGIN_PROVIDER="login_provider";
    public static final String USER_ID="user_id";
    public static final String EMAIL_ID="email_id";
    public static final String NAME="name";
    public static final String SEX="sex";
    public static final String DEPARTMENT="department";
    public static final String STREAM="stream";
    public static final String MOBILE="mobile";
    public static final String CITY="city";
    public static final String PASSWORD="password";
    public static final String IS_USER_LOGIN="is_user_login";

     public static final String HELP_ID="help_id";
     public static final String HELPIE_ID="helpie_id";
     public static final String TITLE="title";
     public static final String DESCRIPTION="description";
     public static final String SERVER_TAG="tag";
     public static final String TAG1="tag1";
     public static final String TAG2="tag2";
     public static final String TAG3="tag3";
     public static final String CATEGORY="categories";
     public static final String STATUS="status";
     public static final String HELPER_ID="helper_id";


    public static void  saveUserDetails(Context context,JSONObject response){
        SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        try {
            editor.putInt(USER_ID, Integer.valueOf(response.getString(USER_ID)));
            editor.putString(EMAIL_ID,response.getString(EMAIL_ID));
            editor.putString(NAME,response.getString(NAME));
            editor.putString(GOOGLE_UID,response.getString(GOOGLE_UID));
            editor.putString(LOGIN_PROVIDER,response.getString(LOGIN_PROVIDER));
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static boolean isUserLogin(Context context){
       SharedPreferences sharedPreferences= context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(IS_USER_LOGIN,false);
    }

    public static void setUserLoginTrue(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(Constants.IS_USER_LOGIN,true);
        editor.commit();
    }

    public static int getCurrentUserID(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);
        return sharedPreferences.getInt(USER_ID,-1);
    }

    public static void logOutUser(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(Constants.IS_USER_LOGIN,false);
        editor.commit();

        SharedPreferences sharedPreferences1=context.getSharedPreferences(NOTIF_SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1=sharedPreferences1.edit();
        editor1.putBoolean(Constants.REGISTERED_WITH_FCM,false);
        editor1.commit();

    }

    public static String getCurrentEmailID(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);
        return sharedPreferences.getString(EMAIL_ID,"");
    }

    public static String getImagesUrl(int id){
        return (IMAGES_URL+id+".jpg");
    }

    public static String getCurrentName(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);
        return sharedPreferences.getString(NAME,"");
    }

    public static boolean isRegisteredWithFCM(Context context){
        // Getting shared preference
        SharedPreferences sharedPreferences=context.getSharedPreferences(Constants.NOTIF_SHARED_PREF,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(Constants.REGISTERED_WITH_FCM,false);
    }

    public static  void setNotificationData(Context context,String fcmid){
        SharedPreferences sharedPreferences=context.getSharedPreferences(Constants.NOTIF_SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(Constants.FCM_UNIQUE_ID,fcmid);
        editor.putBoolean(Constants.REGISTERED_WITH_FCM,true);
        editor.commit();
    }

}

