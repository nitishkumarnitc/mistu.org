package com.example.nitish.mistuorg;

import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.nitish.mistuorg.home.Home;
import com.example.nitish.mistuorg.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private View rootView=null;
    private Context context=null;
    private ProgressDialog nDialog;
    private static  final String TAG_NETCHECK="Login NetCheck";
    private Button btnLogin;
    private EditText inputEmail;
    private EditText inputPassword;
    private static String KEY_SUCCESS = "success";


    public LoginFragment(){
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.fragment_login, container, false);
        context=inflater.getContext();


        inputEmail = (EditText)rootView.findViewById(R.id.login_email);
        inputPassword = (EditText)rootView.findViewById(R.id.login_password);

        btnLogin = (Button)rootView.findViewById(R.id.login_button);
        TextView forgotPass=(TextView)rootView.findViewById(R.id.begin_forgot_password);

        //Toast is generated when the email and pwd field or any is empty
        btnLogin.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if ((!inputEmail.getText().toString().equals("")) && (!inputPassword.getText().toString().equals(""))) {
                    sendLoginRequest();
                } else if ((!inputEmail.getText().toString().equals(""))) {
                    Toast.makeText(context, "Password can't be empty !!!", Toast.LENGTH_SHORT).show();
                } else if ((!inputPassword.getText().toString().equals(""))) {
                    Toast.makeText(context, "Email can't be empty !!!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Every field is mandatory to login !!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    public void sendLoginRequest(){
        Log.d(TAG_NETCHECK, "inside preExecute");
       nDialog = new ProgressDialog(getActivity());
        nDialog.setTitle("Loging in...");
        nDialog.setMessage("Loading..");
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(true);
        nDialog.show();

        inputEmail= (EditText)rootView.findViewById(R.id.login_email);
        inputPassword= (EditText)rootView.findViewById(R.id.login_password);

        final String email_id=inputEmail.getText().toString();
        final String password=inputPassword.getText().toString();
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("tag","login");
            jsonObject.put("email_id",email_id);
            jsonObject.put("password",password);
            Log.d("values sent are",email_id+ " "+ password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest objectRequest=new JsonObjectRequest(Request.Method.POST, Constants.HOME_URL + "login/", jsonObject
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                nDialog.dismiss();
                try {
                    Log.d("Server Response",response.toString());
                    int success=Integer.valueOf(response.getString(KEY_SUCCESS));
                    int error=Integer.valueOf(response.getString(Constants.KEY_ERROR));
                    if(success==1){
                            Constants.saveUserDetails(context,response);
                            Constants.setUserLoginTrue(context);
                            goToHome();

                    }else if (success==2){
                        Toast.makeText(context,"Oops! You forgot to verify your Email Id.",Toast.LENGTH_LONG).show();

                    }else if(error==1){
                            Toast.makeText(context, "Invalid Email id and password", Toast.LENGTH_SHORT).show();

                    }else if(success==0){
                            Constants.saveUserDetails(context,response);
                             goToVerification();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                nDialog.dismiss();
                if(error instanceof NoConnectionError) {
                    Toast.makeText(context,"No Internet Connection",Toast.LENGTH_LONG).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(objectRequest,"get_json_Array");
    }



    private void goToHome(){
        Intent intent = new Intent(context, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void goToVerification(){
        Toast.makeText(context,"Please fill up these required information again !",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(context, Verification.class);
        startActivity(intent);
    }
}
