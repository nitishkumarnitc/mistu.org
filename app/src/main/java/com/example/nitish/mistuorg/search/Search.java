package com.example.nitish.mistuorg.search;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.nitish.mistuorg.R;
import com.example.nitish.mistuorg.app.AppController;
import com.example.nitish.mistuorg.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Search extends AppCompatActivity {
    private TextView resultHeader,noResult;
    private AutoCompleteTextView ed1;
    private LinearLayout searchLayout;
    private String TAG="Search";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchLayout=(LinearLayout)findViewById(R.id.search_linear_layout);

        resultHeader=(TextView)findViewById(R.id.search_result);
        resultHeader.setVisibility(View.INVISIBLE);

        noResult=(TextView)findViewById(R.id.search_no_result);
        noResult.setVisibility(View.INVISIBLE);

        ed1=(AutoCompleteTextView)findViewById(R.id.search_user_text);

        String[] cse=getResources().getStringArray(R.array.interests_cse);
        String[] eee=getResources().getStringArray(R.array.interests_eee);
        String[] ece=getResources().getStringArray(R.array.interests_ece);
        String[] mech=getResources().getStringArray(R.array.interests_mech);
        String[] civil=getResources().getStringArray(R.array.interests_civil);
        String[] chem=getResources().getStringArray(R.array.interests_chem);
        String[] pro=getResources().getStringArray(R.array.interests_pro);
        String[] bio=getResources().getStringArray(R.array.interests_bt);
        String[] arch=getResources().getStringArray(R.array.interests_arch);
        String[] ep=getResources().getStringArray(R.array.interests_ep);

        String[] sports=getResources().getStringArray(R.array.interests_sports);
        String[] arts=getResources().getStringArray(R.array.interests_arts);
        String[] technical=getResources().getStringArray(R.array.interests_technical);
        String[] others=getResources().getStringArray(R.array.interests_others);

        String[] interestsList=generalConcatAll(cse,eee,ece,mech,civil,chem,pro,bio,arch,ep,sports,arts,technical,others);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, interestsList);
        ed1.setThreshold(2);
        ed1.setAdapter(adapter);
        ImageView searchIcon=(ImageView)findViewById(R.id.search_icon_small);

        if(searchIcon!=null) {
            searchIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userIp=ed1.getText().toString();
                    if(resultHeader!=null){
                        resultHeader.setVisibility(View.VISIBLE);
                        String displayInfo="Showing results for "+userIp;
                        resultHeader.setText(displayInfo);
                    }

                    try {
                        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(searchLayout.getWindowToken(), 0);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                    userIp=convertFormat(userIp); //change into database format
                    sendToServer(userIp);

                }
            });
        }
    }

    private String convertFormat(String userIp){
        userIp=userIp.toLowerCase().replace(" ","_");
        return userIp;
    }

    private String[] generalConcatAll(String[]... jobs) {
        int len = 0;
        for (final String[] job : jobs) {
            len += job.length;
        }

        final String[] result = new String[len];

        int currentPos = 0;
        for (final String[] job : jobs) {
            System.arraycopy(job, 0, result, currentPos, job.length);
            currentPos += job.length;
        }

        return result;
    }


    private void sendToServer(String userIp){
        JSONObject values=new JSONObject();
        try {
            values.put("CODE","userInterests");
            values.put("INTEREST",userIp);
            values.put("USER_ID",Constants.getCurrentUserID(this));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, Constants.HOME_URL + "search/", values,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG,response.toString());
                        try {
                            if(parseJsonServerResponse(response)){

                            }else {
                                if(noResult!=null){
                                    noResult.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NetworkError)
                    Toast.makeText(Search.this, Constants.NO_NET_WORK_CONNECTION, Toast.LENGTH_SHORT).show();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest,TAG);

    }

    private boolean parseJsonServerResponse(JSONObject result)throws JSONException{
            final ArrayList<SearchResultUser> listItems=new ArrayList<>(); //array to store details of help Requests by users.
            SearchResultAdapter adapter;
            ListView listView=(ListView)findViewById(R.id.search_list_view);

            JSONArray jsonArray;
            int size;
            jsonArray=result.getJSONArray(Constants.SERVER_RESPONSE);
            size=jsonArray.length();

            //Log.d("Profile",result.toString());

            if(size==0){
                adapter = new SearchResultAdapter(getApplicationContext(),listItems);
                if(listView!=null){
                    listView.setAdapter(adapter);
                }
                return false;
            }


            for(int i=0;i<size;i++){
                JSONObject jo=jsonArray.getJSONObject(i);

                String fname=jo.getString(Constants.FNAME);
                String lname=jo.getString(Constants.LNAME);
                String stream=jo.getString(Constants.STREAM);
                String dept=jo.getString(Constants.DEPARTMENT);
                int userId=Integer.parseInt(jo.getString(Constants.USER_ID));
                String mobile=jo.getString(Constants.MOBILE);
                int hasUserConfirmed=Integer.parseInt(jo.getString(Constants.STATUS));

                fname=fname.substring(0,1).toUpperCase() + fname.substring(1);
                lname=lname.substring(0,1).toUpperCase() + lname.substring(1);

                String branchStream=dept+", "+stream;

                listItems.add(new SearchResultUser(branchStream,hasUserConfirmed,fname+" "+lname,userId,mobile));

            }

            adapter = new SearchResultAdapter(getApplicationContext(),listItems);

            if(listView==null){
                Log.e("EMPTY", "List view is empty");
            }else {
                listView.setAdapter(adapter);
            }

        return true;
        }

    @Override
    protected void onStop() {
        super.onStop();
        AppController.getInstance().cancelPendingRequest(TAG);
    }
}

