package com.example.nitish.mistuorg.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nitish on 25-07-2016.
 */
public class Constants {

    // server home url
    public static final String HOME_URL = "http://www.mistu.org/ihelp/";
    public static final String IMAGES_URL="http://www.mistu.org/ihelp/email/pictures/";
    public static final String SERVER_RESPONSE="server_response";
    public static final String NO_NET_WORK_CONNECTION="No NetWork Connection";

    //Fire base app url
    public static final String FIREBASE_APP = null;

    //Constant to store shared preferences
    public static final String SHARED_PREF = "user_info";

    //To store boolean in shared preferences for if the device is registered or not
    public static final String REGISTERED = "registered";

    //To store the fire base id in shared preferences
    public static final String FCM_UNIQUE_ID = "FCMID";

    //register.php address in your server
    public static final String REGISTER_URL = "http://www.mistu.org/firebasepushnotification/register.php";
    public static final String KEY_SUCCESS="success";
    public static final String KEY_ERROR="error";

    public static final String USER_ID="user_id";
    public static final String EMAIL_ID="email_id";
    public static final String FNAME="fname";
    public static final String LNAME="lname";
    public static final String ROLLNO="rollno";
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
     public static final String TAG1="tag1";
     public static final String TAG2="tag2";
     public static final String TAG3="tag3";
     public static final String CATEGORY="categories";
    public static final String STATUS="status";


    public static void  saveUserDetails(Context context,JSONObject response){
        SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        try {
            editor.putInt(USER_ID, Integer.valueOf(response.getString(USER_ID)));
            editor.putString(EMAIL_ID,response.getString(EMAIL_ID));
            editor.putString(FNAME,response.getString(FNAME));
            editor.putString(LNAME,response.getString(LNAME));
            editor.putString(ROLLNO,response.getString(ROLLNO));
            editor.putString(DEPARTMENT,response.getString(DEPARTMENT));
            editor.putString(STREAM,response.getString(STREAM));
            editor.putString(SEX,response.getString(SEX));
          //  editor.putString(FCM_UNIQUE_ID,response.getString(FCM_UNIQUE_ID));
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static boolean isUserLogin(Context context){
       SharedPreferences sharedPreferences= context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean(IS_USER_LOGIN,false)==true){
            return true;
        } else return false;
    }

    public static void setUserLoginTrue(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(Constants.IS_USER_LOGIN,true);
        editor.commit();
    }

    public static int getCurrentUserID(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);
        return sharedPreferences.getInt(USER_ID,0);
    }

    public static void logOutUser(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(Constants.IS_USER_LOGIN,false);
        editor.commit();
    }

    public static String getCurrentEmailID(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);
        return sharedPreferences.getString(EMAIL_ID,"");
    }

    public static String getImagesUrl(int id){
        return (IMAGES_URL+id+".jpg");
    }

    public static String getCurrentFname(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);
        return sharedPreferences.getString(FNAME,"");
    }
    public static String getCurrentLname(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);
        return sharedPreferences.getString(LNAME,"");
    }

}

