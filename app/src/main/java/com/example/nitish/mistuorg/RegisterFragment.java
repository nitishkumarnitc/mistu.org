package com.example.nitish.mistuorg;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.nitish.mistuorg.app.AppController;
import com.example.nitish.mistuorg.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {


    private View rootView=null;
    private Context context=null;

    private ProgressDialog nDialog;
    private static  final String TAG_NETCHECK="Login NetCheck";
    private static final String TAG="Register Fragment";

    /**
     * Defining layout items.
     **/

    EditText inputFirstName;
    EditText inputLastName;
    EditText inputRollno;
    EditText inputEmail;
    EditText inputPassword;
    Button btnRegister;
    RadioGroup radioGroup;

    public String stream;
    public String department;
    public String sex;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_register, container, false);
        context=inflater.getContext();

        /**
         * Defining all layout items
         **/
        inputFirstName = (EditText)rootView.findViewById(R.id.register_fname);
        inputLastName = (EditText)rootView.findViewById(R.id.register_lname);
        inputRollno = (EditText)rootView.findViewById(R.id.register_rollno);
        inputEmail = (EditText)rootView.findViewById(R.id.register_email);
        inputPassword = (EditText)rootView.findViewById(R.id.register_password);
        btnRegister = (Button)rootView.findViewById(R.id.register_frag_button);

        radioGroup=(RadioGroup)rootView.findViewById(R.id.register_sex);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (  ( !inputRollno.getText().toString().equals(""))
                        && ( !inputPassword.getText().toString().equals(""))
                        && ( !inputFirstName.getText().toString().equals(""))
                        && ( radioGroup.getCheckedRadioButtonId()!= -1)
                        && ( !inputLastName.getText().toString().equals(""))
                        && ( !inputEmail.getText().toString().equals("")) )
                {
                    if(inputRollno.getText().toString().length() == 9 && checkFormat(inputRollno.getText().toString()) ){
                        sendRegisterRequest();
                    }else{
                        Toast.makeText(context,"Incorrect NITC Reg. Id format !!! ", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(context,"One or more fields are empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }



    private void sendRegisterRequest(){
        Log.d(TAG, "sending Register Request");
        nDialog = new ProgressDialog(getActivity());
        nDialog.setTitle("Processing");
        nDialog.setMessage("Loading..");
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(true);
        nDialog.show();

        int sexId=radioGroup.getCheckedRadioButtonId();
        if(sexId==R.id.register_female){
            sex="female";
        }else{
            sex="male";
        }

        setBranchStream(inputRollno.getText().toString());

        JSONObject params = new JSONObject();

        try {
            params.put("tag", "register");
            params.put(Constants.EMAIL_ID, inputEmail.getText().toString());
            params.put(Constants.PASSWORD, inputPassword.getText().toString());
            params.put(Constants.ROLLNO,inputRollno.getText().toString().toUpperCase());
            params.put(Constants.FNAME,inputFirstName.getText().toString());
            params.put(Constants.LNAME,inputLastName.getText().toString());
            params.put(Constants.SEX,sex);
            params.put(Constants.DEPARTMENT,department);
            params.put(Constants.STREAM,stream);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, Constants.HOME_URL + "login/", params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Server Response",response.toString());
                        nDialog.dismiss();
                        int success= 0;
                        int error=0;
                        try {
                            success = Integer.valueOf(response.getString(Constants.KEY_SUCCESS));
                            error=Integer.valueOf(response.getString(Constants.KEY_ERROR));
                            if(success==1){
                                Constants.saveUserDetails(context,response);
                                SharedPreferences sharedPreferences=context.getSharedPreferences(Constants.SHARED_PREF,Context.MODE_PRIVATE);
                                Log.d("User id is ",String.valueOf(sharedPreferences.getInt(Constants.USER_ID,0)));
                                Toast.makeText(context, "User id is"+String.valueOf(sharedPreferences.getInt(Constants.USER_ID,0)), Toast.LENGTH_SHORT).show();
                                goToVerification();

                            }
                            else if (error== 2){
                                Toast.makeText(context,"User Already Exists with this email ....", Toast.LENGTH_LONG).show();
                            }
                            else if (error== 3){
                                Toast.makeText(context,"Invalid Email id....", Toast.LENGTH_LONG).show();
                            }
                            else if(error==4){
                                Toast.makeText(context, "User Already Exists with this Roll No", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                    nDialog.dismiss();
                    if(error instanceof NoConnectionError)
                        Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
        AppController.getInstance().addToRequestQueue(request,TAG);
    }

    private boolean checkFormat(String regId){
        regId=regId.toUpperCase();
        if(regId.charAt(0)=='M'){
            if(regId.charAt(7)=='C'&& regId.charAt(8)=='A'){
                return true;
            }
        }

        if(regId.charAt(0)=='B'||regId.charAt(0)=='M'){
            if(regId.charAt(7)=='C'&& regId.charAt(8)=='S'){
                return true;
            }
            else if(regId.charAt(7)=='E'&& regId.charAt(8)=='C'){
                return true;
            }
            else if(regId.charAt(7)=='E'&& regId.charAt(8)=='E'){
                return true;
            }
            else if(regId.charAt(7)=='M'&& regId.charAt(8)=='E'){
                return true;
            }
            else if(regId.charAt(7)=='C'&& regId.charAt(8)=='E'){
                return true;
            }
            else if(regId.charAt(7)=='P'&& regId.charAt(8)=='E'){
                return true;
            }
            else if(regId.charAt(7)=='A'&& regId.charAt(8)=='R'){
                return true;
            }
            else if(regId.charAt(7)=='B'&& regId.charAt(8)=='T'){
                return true;
            }
            else if(regId.charAt(7)=='C'&& regId.charAt(8)=='H'){
                return true;
            }
            else if(regId.charAt(7)=='E'&& regId.charAt(8)=='P'){
                return true;
            }
        }
        else {
            return false;
        }
        return false;
    }

    private void setBranchStream(String regId){
        regId=regId.toUpperCase();
        if(regId.charAt(0)=='M'){
            stream="M-TECH";
            if(regId.charAt(7)=='C'&& regId.charAt(8)=='A'){
                department="CSE";
                stream="MCA";
                return;
            }
        }
        else if(regId.charAt(0)=='B'){
            stream="B-TECH";
        }

        if(regId.charAt(7)=='C'&& regId.charAt(8)=='S'){
            department="CSE";
        }
        else if(regId.charAt(7)=='E'&& regId.charAt(8)=='C'){
            department="ECE";
        }
        else if(regId.charAt(7)=='E'&& regId.charAt(8)=='E'){
            department="EEE";
        }
        else if(regId.charAt(7)=='M'&& regId.charAt(8)=='E'){
            department="MECH";
        }
        else if(regId.charAt(7)=='C'&& regId.charAt(8)=='E'){
            department="CIVIL";
        }
        else if(regId.charAt(7)=='P'&& regId.charAt(8)=='E'){
            department="PRO";
        }
        else if(regId.charAt(7)=='A'&& regId.charAt(8)=='R'){
            department="ARCH";
        }
        else if(regId.charAt(7)=='B'&& regId.charAt(8)=='T'){
            department="BIO";
        }
        else if(regId.charAt(7)=='C'&& regId.charAt(8)=='H'){
            department="CHEM";
        }
        else if(regId.charAt(7)=='E'&& regId.charAt(8)=='P'){
            department="EP";
        }
    }

    private void goToVerification(){
        Intent intent = new Intent(context, Verification.class);
        startActivity(intent);
    }
}

