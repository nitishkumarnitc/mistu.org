package com.example.nitish.mistuorg;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    private FirebaseAuth mAuth;
    private  FirebaseAuth.AuthStateListener mAuthStateListener;
    private View rootView=null;
    private Context context=null;
    private static  final String TAG_NETCHECK="Login NetCheck";
    private static final String TAG="Register Fragment";

    EditText inputName;
    EditText inputEmail;
    EditText inputPassword;
    Button btnRegister;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_register, container, false);
        context=inflater.getContext();

        mAuth=FirebaseAuth.getInstance();
        mAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user!=null){
                    Log.d(TAG,"onAuthStateChanged:signedIn"+user.getUid());
                    Log.d(TAG,"Login Provider:"+user.getProviderId());
                    sendRegisterRequest(user);
                }else{
                    Log.d(TAG,"onAuthStateChanged:signedOut");
                }
            }
        };


        /**
         * Defining all layout items
         **/
        inputName = (EditText)rootView.findViewById(R.id.register_name);
        inputEmail = (EditText)rootView.findViewById(R.id.register_email);
        inputPassword = (EditText)rootView.findViewById(R.id.register_password);
        btnRegister = (Button)rootView.findViewById(R.id.register_frag_button);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ( ( !inputPassword.getText().toString().equals(""))
                        && ( !inputName.getText().toString().equals(""))
                        && ( !inputEmail.getText().toString().equals(""))){
                    createAccount(inputEmail.getText().toString(),inputPassword.getText().toString());
                }

            }
        });

        return rootView;
    }


    private void createAccount(String email,String password){
        Log.d(TAG,"createAccount"+email);

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Toast.makeText(context,"auth_failed",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Log.d(TAG,"user_created");
                }
            }
        });
    }


    private void sendRegisterRequest(FirebaseUser user){
        Log.d(TAG, "sending Register Request");

        JSONObject params = new JSONObject();

        try {
            params.put(Constants.SERVER_TAG, "create_user");
            params.put(Constants.EMAIL_ID, user.getEmail());
            params.put(Constants.GOOGLE_UID,user.getUid());
            params.put(Constants.LOGIN_PROVIDER,user.getProviderId());
            params.put(Constants.NAME,inputName.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, Constants.HOME_URL + "login/", params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Server Response",response.toString());
                        parseServerResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                    if(error instanceof NoConnectionError)
                        Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
        AppController.getInstance().addToRequestQueue(request,TAG);
    }





    private void goToVerification(){
        Intent intent = new Intent(context, Verification.class);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthStateListener!=null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    private void parseServerResponse(JSONObject response){
        try {
            int success=Integer.parseInt(response.getString(Constants.KEY_SUCCESS));
            if(success==2){
                Log.d(TAG,response.toString());
                JSONObject jsonObject=new JSONObject();
                jsonObject.put(Constants.USER_ID,Integer.parseInt(response.getString(Constants.USER_ID)));
                jsonObject.put(Constants.EMAIL_ID,response.getString(Constants.EMAIL_ID));
                jsonObject.put(Constants.NAME,response.getString(Constants.NAME));
                jsonObject.put(Constants.GOOGLE_UID,response.getString(Constants.GOOGLE_UID));
                jsonObject.put(Constants.LOGIN_PROVIDER,response.getString(Constants.LOGIN_PROVIDER));
                Constants.saveUserDetails(context,jsonObject);
                goToVerification();
            }else{
                Log.d(TAG,response.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

