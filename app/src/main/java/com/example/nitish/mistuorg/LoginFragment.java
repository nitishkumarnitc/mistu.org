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

import com.android.volley.NetworkError;
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

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener{
    private View rootView=null;
    private Context context=null;
    private static  final String TAG_NETCHECK="Login NetCheck";
    private Button btnLogin;
    private EditText inputEmail;
    private EditText inputPassword;
    private static final String TAG = "LoginFragment";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    public LoginFragment(){
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.fragment_login, container, false);
        context=inflater.getContext();

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
                signInUsingGoogle();
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
                    Log.d(TAG,"Name"+user.getDisplayName());
                    Log.d(TAG,"LOgin Provider"+user.getProviderId());
                    attemptLoginWithServer(user);

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

                    sendLoginRequestToFirebase();

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

    public void sendLoginRequestToFirebase(){
        Log.d(TAG_NETCHECK, "inside sendLoginRequestToFirebase");

        inputEmail= (EditText)rootView.findViewById(R.id.login_email);
        inputPassword= (EditText)rootView.findViewById(R.id.login_password);

        final String email_id=inputEmail.getText().toString();
        final String password=inputPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email_id, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(context,"Auth Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                        }

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

    private void signInUsingGoogle(){
        Toast.makeText(context, "Google signin called", Toast.LENGTH_SHORT).show();
        Intent signInIntent=Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    private void attemptLoginWithServer(FirebaseUser user){
        JSONObject values=new JSONObject();
        try {
            values.put(Constants.SERVER_TAG,"create_user");
            values.put(Constants.NAME,user.getDisplayName());
            values.put(Constants.EMAIL_ID,user.getEmail());
            values.put(Constants.GOOGLE_UID,user.getUid());
            values.put(Constants.LOGIN_PROVIDER,user.getProviderId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, Constants.HOME_URL + "login/", values,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG,"onResponse:"+response.toString());
                        parseSeverResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NetworkError){
                    Toast.makeText(context,Constants.NO_NET_WORK_CONNECTION, Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(request,TAG);

    }

    private void parseSeverResponse(JSONObject response){
        try {
            int success=Integer.parseInt(response.getString(Constants.KEY_SUCCESS));
            if(success==1){
                Log.d(TAG,"Login Successfull: going to home");
                JSONObject values=new JSONObject();
                values.put(Constants.USER_ID,Integer.parseInt(response.getString(Constants.USER_ID)));
                values.put(Constants.EMAIL_ID,response.getString(Constants.EMAIL_ID));
                values.put(Constants.GOOGLE_UID,response.getString(Constants.GOOGLE_UID));
                values.put(Constants.LOGIN_PROVIDER,response.getString(Constants.LOGIN_PROVIDER));
                values.put(Constants.NAME,response.getString(Constants.NAME));
                Constants.saveUserDetails(context,values);
                Constants.setUserLoginTrue(context);
                goToHome();
            }else if(success==2){
                Log.d(TAG,"should fill other details");
                JSONObject values=new JSONObject();
                values.put(Constants.USER_ID,Integer.parseInt(response.getString(Constants.USER_ID)));
                values.put(Constants.EMAIL_ID,response.getString(Constants.EMAIL_ID));
                values.put(Constants.GOOGLE_UID,response.getString(Constants.GOOGLE_UID));
                values.put(Constants.LOGIN_PROVIDER,response.getString(Constants.LOGIN_PROVIDER));
                values.put(Constants.NAME,response.getString(Constants.NAME));
                Constants.saveUserDetails(context,values);
                goToVerification();
            }else if(success==3){
                Log.d(TAG,"should verify your email address");
                Toast.makeText(context, "You need to verify your email address and Login again", Toast.LENGTH_SHORT).show();
            }else if(success==4){
                Log.d(TAG,"verification mail not sent, so contact iHelp");
            }else {
                Log.d(TAG,"UNKNOWN Eroror");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
