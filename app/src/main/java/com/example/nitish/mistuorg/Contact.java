package com.example.nitish.mistuorg;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.example.nitish.mistuorg.app.AppController;
import com.example.nitish.mistuorg.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Contact extends AppCompatActivity {
    private String name;
    private String branchStream;
    private int userId,helpId,helpieId,notiId;
    private NetworkImageView userPic;
    private TextView emailText,phoneText,nameText,branchText;
    private View emailContainer,phoneContainer;
    private ImageLoader imageLoader= AppController.getInstance().getImageLoader();
    private  String TAG="CONTACTS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int type;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        type=getIntent().getIntExtra("TYPE",-1);
        if(type==0){
            //contact class called from notification fragment
            userId=getIntent().getIntExtra("USERID",0);
            helpId=getIntent().getIntExtra("HELP_ID",0);
            helpieId=getIntent().getIntExtra("HELPIE_ID",0);
            notiId=getIntent().getIntExtra("NOTI_ID",0);

            Log.i("NOTI_DONE","<------"+type+" "+ userId+" "+helpieId+" "+helpId +" "+notiId+" ----------->");


        }
        else if(type==1){
            //contact class called from profile Asked fragment
            userId=getIntent().getIntExtra("USERID",0);
            helpId=getIntent().getIntExtra("HELP_ID",0);
            helpieId=getIntent().getIntExtra("HELPIE_ID",0);
            notiId= -1; // to show the server to take appropriate action
            Log.i("PROF_DONE","<------"+type+" "+ userId+" "+helpieId+" "+helpId +notiId+" ----------->");

        }
        else{
            finish();
        }

        setValuesOfLayout();

    }

    private void setValuesOfLayout(){
        userPic=(NetworkImageView)findViewById(R.id.contact_pic);
        nameText=(TextView)findViewById(R.id.contact_name);
        branchText=(TextView)findViewById(R.id.contact_branch);

        emailText=(TextView)findViewById(R.id.contact_emailID);
        phoneText=(TextView)findViewById(R.id.contact_phoneNum);

        emailContainer=findViewById(R.id.contact_email_container);
        phoneContainer=findViewById(R.id.contact_phone_num_container);
        databaseTransaction();

    }



    private void databaseTransaction(){
        JSONObject values=new JSONObject();
        try {
            values.put("CODE","getContact");
            values.put("USERID",userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, Constants.HOME_URL + "contacts/", values,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG,response.toString());
                        try {
                            displayAHRList(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError){
                    Toast.makeText(Contact.this, Constants.NO_NET_WORK_CONNECTION, Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(request,TAG);

    }

           private void displayAHRList(JSONObject result)throws JSONException{

            Log.d("ContactInfo",result.toString());

            JSONArray jsonArray;
            int size;
            jsonArray=result.getJSONArray(Constants.SERVER_RESPONSE);
            size=jsonArray.length();

            if(size==0){
                return;
            }

            JSONObject jo=jsonArray.getJSONObject(0);

            String fname=jo.getString(Constants.FNAME);
            String lname=jo.getString(Constants.LNAME);
            String stream=jo.getString(Constants.STREAM);
            String dept=jo.getString(Constants.DEPARTMENT);
            final String mobile=jo.getString(Constants.MOBILE);
            String city=jo.getString(Constants.CITY);
            final String emailId=jo.getString(Constants.EMAIL_ID);

            fname=fname.substring(0,1).toUpperCase() + fname.substring(1);
            lname=lname.substring(0,1).toUpperCase() + lname.substring(1);
            String name=fname+" "+lname;
            String branchStream=stream+" , "+dept;


            nameText.setText(name);
            branchText.setText(branchStream);
            emailText.setText(emailId);
            phoneText.setText(mobile);
            userPic.setImageUrl(Constants.IMAGES_URL+userId+".jpg",imageLoader);

            emailContainer.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String[] addresses={emailId};
                    String subject="Thanks for accepting my request on iHelp";
                    composeEmail(addresses,subject);
                    sendConfirmationToServer();
                }

                public void composeEmail(String[] addresses, String subject) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, addresses);
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            });

            phoneContainer.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    dialPhoneNumber(mobile);
                    sendConfirmationToServer();

                }

                public void dialPhoneNumber(String phoneNumber) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phoneNumber));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            });
        }

        private void sendConfirmationToServer(){

            JSONObject values=new JSONObject();
            try {
                values.put("CODE","userContacted");
                values.put("USER_ID",helpieId); //who is contacting helper ? helpie :)
                values.put("HELP_ID",helpId);
                values.put("NOTI_ID",notiId);
                values.put("HELPIE_ID",helpieId);
                values.put("HELPER_ID",userId);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


