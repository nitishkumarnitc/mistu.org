package com.example.nitish.mistuorg.notification;

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

public class NotiContactReqFragment extends Fragment {
    private Context context;
    private View rootView;
    private int currentUserId;
    private String TAG="NotiContactReqFragment";

    public NotiContactReqFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context=inflater.getContext();
        rootView=inflater.inflate(R.layout.fragment_noti_contact_req, container, false);
        currentUserId= Constants.getCurrentUserID(context);
        startDBTransaction();
        return rootView;
    }

    private void startDBTransaction(){
        JSONObject values=new JSONObject();
        try {
            values.put("CODE","notiContactReq");
            values.put("USER_ID",currentUserId);
            Log.i("NOTII+USERID",""+currentUserId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, Constants.HOME_URL + "notifications/",values,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG,response.toString());
                        try {
                            displayNotiAccList(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NetworkError){
                    Toast.makeText(context, Constants.NO_NET_WORK_CONNECTION, Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest,TAG);


    }



    private boolean displayNotiAccList(JSONObject result)throws JSONException{

        ArrayList<NotiContactReqListItem> notiContactReqListItems=new ArrayList<>(); //array to store details of help Requests by users.
        NotiContactReqAdapter  adapter;
        int count=0;

        JSONArray jsonArray;
        jsonArray=result.getJSONArray(Constants.SERVER_RESPONSE);
        int size=jsonArray.length();
        if(size==0){
            return false;
        }

        Log.i("JSON_NOTI_CONT_REQ",result.toString());

        for(int i=0;i<size;i++){
            JSONObject jo=jsonArray.getJSONObject(i);

            String name=jo.getString(Constants.NAME);
            float rating=Float.parseFloat(jo.getString("rating"));
            int requesterID=Integer.parseInt(jo.getString(Constants.USER_ID));
            int type=Integer.parseInt(jo.getString("type"));
            String mobile=jo.getString("mobile");
            int hasUserConfirmed=Integer.parseInt(jo.getString("status"));

            notiContactReqListItems.add(new NotiContactReqListItem(name,rating,requesterID,type,mobile,hasUserConfirmed));

        }
        adapter = new  NotiContactReqAdapter(context,notiContactReqListItems);

        ListView listView=(ListView)rootView.findViewById(R.id.noti_contact_req_frag_listView);
        if(listView==null){
            Log.e("EMPTY", "List view is empty");
        }else {
            listView.setAdapter(adapter);
        }

        return true;
    }

}
