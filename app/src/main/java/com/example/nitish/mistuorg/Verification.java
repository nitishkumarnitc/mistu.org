package com.example.nitish.mistuorg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.nitish.mistuorg.app.AppController;
import com.example.nitish.mistuorg.home.Home;
import com.example.nitish.mistuorg.interests.Registered;
import com.example.nitish.mistuorg.utils.Constants;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * Created by nitish on 26-07-2016.
 */
public class Verification extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener{

    private  boolean isInterestsSelected=false;
    private String userEmail;
    private int userId;
    private ImageView imageToUpload=null;
    private static final int RESULT_LOAD_IMAGE=1;
    private String TAG="Verification";
    GoogleApiClient mGoogleApiClient;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();


        SharedPreferences sharedPreferences=this.getSharedPreferences(Constants.SHARED_PREF,MODE_PRIVATE);
        userEmail=sharedPreferences.getString(Constants.EMAIL_ID,"");
        userId=sharedPreferences.getInt(Constants.USER_ID,0);

        TextView textView=(TextView)findViewById(R.id.verification_user_interests_selection);
        textView.setOnClickListener(this);

        View proceed=findViewById(R.id.verification_done);
        proceed.setOnClickListener(this);

        TextView choosePic=(TextView)findViewById(R.id.verification_choose_pic);
        choosePic.setOnClickListener(this);

        imageToUpload=(ImageView)findViewById(R.id.verification_pic_user);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        Intent intent;
        switch (id){
            case R.id.verification_user_interests_selection:
                intent=new Intent(this, Registered.class);
                intent.putExtra("TYPE",0); // 0 refers that registered class is called from verification class.
                startActivityForResult(intent,99);
                break;

            case R.id.verification_choose_pic:
                Intent galleryIntent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent,RESULT_LOAD_IMAGE);
                break;

            case R.id.verification_done:
                TextView phoneNumText=(TextView)findViewById(R.id.verification_phone_num);
                String phoneNum=phoneNumText.getText().toString();

                Boolean isContactValid=isValidPhoneNum(phoneNum);

                if(isContactValid) {
                    if(isInterestsSelected){
                        //encoding image
                        Bitmap image=((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
                        int nh = (int) ( image.getHeight() * (512.0 / image.getWidth()) );
                        Bitmap scaled = Bitmap.createScaledBitmap(image, 512, nh, true);

                        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                        scaled.compress(Bitmap.CompressFormat.JPEG, 15, byteArrayOutputStream);
                        String encodedImage= Base64.encodeToString(byteArrayOutputStream.toByteArray(),Base64.DEFAULT);


                        JSONObject values=new JSONObject();
                        try {
                            values.put("CODE","register");
                            values.put("EMAIL_ID",userEmail);
                            values.put("USER_ID",userId);
                            values.put("MOBILE",phoneNum);
                            values.put("ENCODED_IMAGE",encodedImage);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, Constants.HOME_URL + "login/complete_register.php"
                                , values, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("Response ofserver is",response.toString());
                                parseResponse(response);

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if(error instanceof NoConnectionError) {
                                    Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        AppController.getInstance().addToRequestQueue(jsonObjectRequest,"TAG");

                    }
                    else{
                        Toast.makeText(this,"Oops! you forgot to tell us your Interests and Skills.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                else{
                    if(!isContactValid){
                        Toast.makeText(this,"Oops! seems like your contact number is not valid.",Toast.LENGTH_SHORT).show();
                    }
                }
                break;


        }
    }

    private void parseResponse(JSONObject response){

        try {
            int success=Integer.parseInt(response.getString(Constants.KEY_SUCCESS));
            if(success==1){
                Constants.saveUserDetails(this,response);
                Constants.setUserLoginTrue(this);
                Intent homeIntent=new Intent(this, Home.class);
                startActivity(homeIntent);

            }else if(success==2){
                Toast.makeText(Verification.this, "Need to provide other details", Toast.LENGTH_SHORT).show();
                Constants.saveUserDetails(this,response);

            }else{
                Toast.makeText(Verification.this, "", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private boolean isValidPhoneNum(String phoneNum){
        if(phoneNum.length()==10){
            return true;
        }
        return false;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RESULT_LOAD_IMAGE && resultCode==RESULT_OK &&data!=null){
            Uri selectImage=data.getData();
            imageToUpload.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            imageToUpload.setImageURI(null);
            imageToUpload.setImageURI(selectImage);

        }

        else if (requestCode == 99 && resultCode == RESULT_OK) {
            // get the position from intent
            if(data.getExtras()!=null && data.hasExtra("Selected")){
                isInterestsSelected = data.getExtras().getBoolean("Selected");
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(Verification.this, "Logged Out, Login again to feel the remaining details", Toast.LENGTH_SHORT).show();
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        //updateUI(null);
                    }
                });

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
