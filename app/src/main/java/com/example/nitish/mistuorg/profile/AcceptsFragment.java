package com.example.nitish.mistuorg.profile;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class AcceptsFragment extends Fragment {
    private Context context;
    private View rootView;
    private int currentUserId;
    private String TAG="PROFILE_ACCEPTS_FRAGMENT";

    public AcceptsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context=inflater.getContext();
        rootView= inflater.inflate(R.layout.fragment_accepts, container, false);
        currentUserId= Constants.getCurrentUserID(context);
        getProfileAccepts();
        return rootView;
    }

    private void getProfileAccepts(){
        JSONObject values=new JSONObject();
        try {
            values.put("CODE","get_profile_notif");
            values.put("PROFILE_TYPE","accepted");
            values.put("USER_ID",currentUserId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, Constants.HOME_URL + "profile/notif.php/", values,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                            Log.d(TAG,response.toString());
                        try {
                            displayAcceptsList(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError) {
                    Toast.makeText(context, "No NetWork Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(request,TAG);

    }

        private void displayAcceptsList(JSONObject result)throws JSONException{
            ArrayList<ProfileListItem> listItems=new ArrayList<>(); //array to store details of help Requests by users.
            AcceptsAdapter adapter;
            int count=0;

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
                String sex=jo.getString("sex");
                String stream=jo.getString("stream");
                String dept=jo.getString("department");

                String cat=jo.getString("category");
                String title=jo.getString("title");
                String description=jo.getString("description");
                int helpId=Integer.parseInt(jo.getString("help_id"));
                int helpie_id=Integer.parseInt(jo.getString("helpie_id"));

                String tag1;
                String tag2;
                String tag3;

                if(jo.isNull("tag1")){
                    Log.i("TAG","tag 1");
                    tag1="";
                }else{
                    tag1=jo.getString("tag1");
                }

                if(jo.isNull("tag2")){
                    Log.i("TAG","tag 2");
                    tag2="";
                }else{
                    tag2=jo.getString("tag2");
                }

                if(jo.isNull("tag3")){
                    Log.i("TAG","tag 2");
                    tag3="";
                }else{
                    tag3=jo.getString("tag3");
                }

                fname=fname.substring(0,1).toUpperCase() + fname.substring(1);
                lname=lname.substring(0,1).toUpperCase() + lname.substring(1);
                title=title.substring(0,1).toUpperCase()+title.substring(1);
                description=description.substring(0,1).toUpperCase()+description.substring(1);

                listItems.add(new ProfileListItem(dept, cat, description, fname,
                helpId, lname, stream, sex, tag1,tag2,tag3,title,helpie_id));


            }

            adapter = new AcceptsAdapter(context,listItems);

            ListView profAccListView=(ListView)rootView.findViewById(R.id.profile_accepts_list_view);
            if(profAccListView==null){
                Log.e("EMPTY", "List view is empty");
            }else {
                profAccListView.setAdapter(adapter);
                profAccListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //list item on click :)
                    }
                });
            }


        }


}
