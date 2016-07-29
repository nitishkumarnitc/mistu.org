package com.example.nitish.mistuorg.profile;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.nitish.mistuorg.Contact;
import com.example.nitish.mistuorg.R;
import com.example.nitish.mistuorg.app.AppController;
import com.example.nitish.mistuorg.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AskedHelpInfo extends AppCompatActivity {
    private  int helpId;
    private CheckBox done;
    private int currentUserId;
    private String TAG="PROFILE_ASKED_HELP_INFO";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title=getIntent().getStringExtra("TITLE");
        helpId=getIntent().getIntExtra("HELPID",0);
        setContentView(R.layout.activity_asked_help_info);
        currentUserId= Constants.getCurrentUserID(this);

        TextView textView=(TextView)findViewById(R.id.asked_help_details_title);
        textView.setText(title);

        ImageView imgView=(ImageView)findViewById(R.id.asked_help_details_view);
        if(imgView!=null){
            imgView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //show details of help request
                }
            });
        }
        done=(CheckBox)findViewById(R.id.asked_help_details_checkBox);
        View helpDone=findViewById(R.id.asked_help_details_done_container);
        if(helpDone!=null){
            helpDone.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(done.isChecked()){
                        done.setChecked(false);
                    }else{
                        done.setChecked(true);
                    }
                }
            });
        }
        databaseTransaction();
    }

    private void databaseTransaction(){
        JSONObject values=new JSONObject();
        try {
            values.put("CODE","getHelpers");
            values.put("HELPID",helpId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, Constants.HOME_URL + "profile/helpers.php/",
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

            }
        });
        AppController.getInstance().addToRequestQueue(request,TAG);
    }



        private void displayAHRList(JSONObject result)throws JSONException{
            final ArrayList<HelperDetailListItem> listItems=new ArrayList<>(); //array to store details of help Requests by users.
            HelperDetailListAdapter adapter;
            int count=0;

            Log.d("HELPERs",result.toString());

            JSONArray jsonArray;
            int size;
            jsonArray=result.getJSONArray(Constants.SERVER_RESPONSE);
            size=jsonArray.length();

            if(size==0){
                return;
            }
            for(int i=0;i<size;i++){
                JSONObject jo=jsonArray.getJSONObject(i);

                String fname=jo.getString("fname");
                String lname=jo.getString("lname");
                String stream=jo.getString("stream");
                String dept=jo.getString("department");
                int helperId=Integer.parseInt(jo.getString("helperId"));

                fname=fname.substring(0,1).toUpperCase() + fname.substring(1);
                lname=lname.substring(0,1).toUpperCase() + lname.substring(1);
                String name=fname+" "+lname;
                String branchStream=stream+" , "+dept;

                listItems.add(new HelperDetailListItem(helperId,branchStream,name));
            }

            adapter = new HelperDetailListAdapter(listItems,getApplicationContext());

            ListView listView=(ListView)findViewById(R.id.asked_help_details_helpers_list);
            if(listView==null){
                Log.e("EMPTY", "List view is empty");
            }else {
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //list item on click :)
                        Intent intent=new Intent(getApplicationContext(), Contact.class);
                        intent.putExtra("TYPE",1);
                        intent.putExtra("USERID",listItems.get(position).getHelperId());
                        intent.putExtra("HELPIE_ID",currentUserId);
                        intent.putExtra("HELP_ID",helpId);
                        startActivity(intent);
                    }
                });
            }


        }
    }

