package com.example.nitish.mistuorg.notification;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
public class NotiACCFragment extends Fragment {
    private Context context;
    private View rootView;
    private int currentUserId;
    private String TAG="NotiACCFragment";

    public NotiACCFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context=inflater.getContext();
        rootView=inflater.inflate(R.layout.fragment_noti_acc, container, false);
        currentUserId= Constants.getCurrentUserID(context);
        startDBTransaction();
        return rootView;
    }


    private void startDBTransaction(){
        JSONObject values=new JSONObject();
        try {
            values.put("CODE","notiAcc");
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

        ArrayList<NotiACCListItem> notiACCListItems=new ArrayList<>(); //array to store details of help Requests by users.
        NotiACCListAdapter adapter;
        int count=0;

        JSONArray jsonArray;
        jsonArray=result.getJSONArray(Constants.SERVER_RESPONSE);
        int size=jsonArray.length();
        if(size==0){
            return false;
        }

        Log.i("JSON_NOTI",result.toString());



        for(int i=0;i<size;i++){
            JSONObject jo=jsonArray.getJSONObject(i);

            String name=jo.getString(Constants.NAME);
            String category=jo.getString(Constants.CATEGORY);
            String description=jo.getString("description");
            String tag1=jo.getString("tag1");
            String tag2=jo.getString("tag2");
            String tag3=jo.getString("tag3");
            String title=jo.getString("title");

            int hasContacted=Integer.parseInt (jo.getString("is_conf_clickable"));
            int hasSkipped=Integer.parseInt (jo.getString("is_skip_clickable"));

            int helperId=Integer.parseInt (jo.getString("helper_helpie_id"));
            int helpId=Integer.parseInt (jo.getString("help_id"));
            int notiId=Integer.parseInt (jo.getString("notif_id"));
            int currentUserId= this.currentUserId;

            float rating=2.0f; // we will change it later



            name=name.substring(0,1).toUpperCase() + name.substring(1);
            title=title.substring(0,1).toUpperCase()+title.substring(1);
            description=description.substring(0,1).toUpperCase()+description.substring(1);

            notiACCListItems.add(new NotiACCListItem(category,currentUserId,
            description,  hasContacted,  hasSkipped,  helperId,
            helpId,  name, notiId,  rating,  tag1,  tag2, tag3,  title));

        }
        adapter = new NotiACCListAdapter(context, notiACCListItems);

        ListView listView=(ListView)rootView.findViewById(R.id.noti_acc_frag_listView);
        if(listView==null){
            Log.e("EMPTY", "List view is empty");
        }else {
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //list item on click :)
                }
            });
        }

        return true;
    }
}
