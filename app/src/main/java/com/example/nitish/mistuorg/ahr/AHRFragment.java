package com.example.nitish.mistuorg.ahr;


import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.app.Fragment;
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


@SuppressWarnings("ResourceType")
public class AHRFragment extends Fragment {
    private ListView ahrList;
    private Context context;
    private View rootView;
    private int catPosition;
    private int currentUserId;
    private String[] navMenuTitles;
    private String category;
    private ArrayList<Integer> helpieIdArray=new ArrayList<>();
    private String TAG="AHR FRAG";

    public AHRFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView=inflater.inflate(R.layout.fragment_ahr, container, false);
        context=inflater.getContext();

        catPosition=getArguments().getInt("CAT");
        currentUserId= Constants.getCurrentUserID(context);
        //currentUserId=getArguments().getInt("CURRENT_USER_ID");
        Log.i("AHR_FRAG_USER_ID",""+currentUserId);

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        category=navMenuTitles[catPosition];

        serverTransaction();
        return rootView;

    }

    private void serverTransaction(){
        JSONObject values=new JSONObject();
        try {
            values.put("CODE","ahr");
            values.put("CATEGORY",category);
            values.put("USERID",currentUserId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, Constants.HOME_URL + "ahr/", values,
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
                    if(error instanceof NetworkError){
                        Toast.makeText(context,Constants.NO_NET_WORK_CONNECTION, Toast.LENGTH_SHORT).show();
                    }
            }
        });
        AppController.getInstance().addToRequestQueue(request,TAG);
    }




        private void displayAHRList(JSONObject result)throws JSONException{
            TypedArray userPics=getResources().obtainTypedArray(R.array.nav_drawer_icons);     // array to store pics of users asking for help. (Hence TypedArray)
            ArrayList<AHRFragmentListItem> fragmentListItems=new ArrayList<AHRFragmentListItem>(); //array to store details of help Requests by users.
            AHRFragmentListAdapter adapter;
            int count=0;

            JSONArray jsonArray;
            int size;
            jsonArray=result.getJSONArray(Constants.SERVER_RESPONSE);
            size=jsonArray.length();

            for(int i=0;i<size;i++){
                JSONObject jo=jsonArray.getJSONObject(i);
                String name=jo.getString(Constants.NAME);
                String cat=jo.getString(Constants.CATEGORY);
                String title=jo.getString(Constants.TITLE);
                String description=jo.getString(Constants.DESCRIPTION);
                String helpId=jo.getString(Constants.HELP_ID);
                int helpieId=Integer.parseInt(jo.getString(Constants.HELPIE_ID));
                helpieIdArray.add(helpieId);
                String tag1;
                String tag2;
                String tag3;

                if(jo.isNull(Constants.TAG1)){
                    Log.i("TAG","tag 1");
                    tag1="";
                }else{
                    tag1=jo.getString(Constants.TAG1);
                }

                if(jo.isNull(Constants.TAG2)){
                    Log.i("TAG","tag 2");
                    tag2="";
                }else{
                    tag2=jo.getString(Constants.TAG2);
                }

                if(jo.isNull(Constants.TAG3)){
                    Log.i("TAG","tag 2");
                    tag3="";
                }else{
                    tag3=jo.getString(Constants.TAG3);
                }

                name=name.substring(0,1).toUpperCase() + name.substring(1);
                title=title.substring(0,1).toUpperCase()+title.substring(1);
                description=description.substring(0,1).toUpperCase()+description.substring(1);
                fragmentListItems.add(new AHRFragmentListItem(name,cat,title,description,
                        Integer.parseInt(helpId),helpieId,tag1,tag2,tag3));
            }

            userPics.recycle(); // Recycle the typed array must be done to avoid runtime exception.
            adapter = new AHRFragmentListAdapter(context, fragmentListItems);
            Log.i("ILLAALOLA",""+currentUserId);

            ahrList=(ListView)rootView.findViewById(R.id.hr_frag_listView);
            if(ahrList==null){
                Log.e("EMPTY", "List view is empty");
            }else {
                ahrList.setAdapter(adapter);
                ahrList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //list item on click :)
                    }
                });
            }


        }

    @Override
    public void onStop() {
        super.onStop();
        AppController.getInstance().cancelPendingRequest(TAG);
    }
}


