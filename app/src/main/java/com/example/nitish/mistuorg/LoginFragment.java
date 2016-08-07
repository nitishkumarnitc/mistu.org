package com.example.nitish.mistuorg;

import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.example.nitish.mistuorg.app.AppController;
import com.example.nitish.mistuorg.home.Home;
import com.example.nitish.mistuorg.settings.Reset;
import com.example.nitish.mistuorg.utils.Constants;
import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener{
    private View rootView=null;
    private Context context=null;
    private ProgressDialog nDialog;
    private static  final String TAG_NETCHECK="Login NetCheck";
    private Button btnLogin;
    private EditText inputEmail;
    private EditText inputPassword;
    private static String KEY_SUCCESS = "success";
    private static final String TAG = "LoginFragment";
    private static final int RC_SIGN_IN = 9001;
    NetworkImageView google_pic;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    public LoginFragment(){
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.fragment_login, container, false);
        context=inflater.getContext();
        google_pic=(NetworkImageView)rootView.findViewById(R.id.google_pic);

        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        final SignInButton signInButton=(SignInButton)rootView.findViewById(R.id.email_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        mAuth=FirebaseAuth.getInstance();
        mAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if (user!=null){
                    Log.d(TAG,"onAuthStateChanged:signed_in:"+user.getUid());
                    Log.d(TAG,"email_id:"+user.getEmail());
                    if(user.getPhotoUrl()!=null){
                        Log.d(TAG,"pic:"+user.getPhotoUrl());
                        ImageLoader imageLoader=AppController.getInstance().getImageLoader();
                        google_pic.setImageUrl(String.valueOf(user.getPhotoUrl()),imageLoader);
                    }
                    Log.d(TAG,"Name"+user.getDisplayName());

                }else{
                    Log.d(TAG,"onAuthStateChanged:signed_out");
                }
            }
        };


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

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent passWordResetIntent=new Intent(context, Reset.class);
                passWordResetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                passWordResetIntent.putExtra("pos",4);
                passWordResetIntent.putExtra("itemName","reset_password");
                startActivityForResult(passWordResetIntent,0);
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


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG,"onConnectionFailed:"+connectionResult);
        Toast.makeText(context, "Google Play Services error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount account=result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }else{
                Log.d(TAG,"Google SignIn Failed");
            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account){
        Log.d(TAG,"firebaseAuthWithGoogle:"+account.getId());
        //showProgressDialog
        AuthCredential credential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                        }
                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });

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

    private void signIn(){
        Toast.makeText(context, "Google signin called", Toast.LENGTH_SHORT).show();
        Intent signInIntent=Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    private void signOut(){
        mAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        // signOutThe User
                        Constants.logOutUser(context);
                        Toast.makeText(context, "SuccessfullyLoggedOut", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

}
