package com.example.nitish.mistuorg.profile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.example.nitish.mistuorg.R;
import com.example.nitish.mistuorg.app.AppController;
import com.example.nitish.mistuorg.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileView extends AppCompatActivity {
    private int currentUserId;
    private int helperID;// The person whose profile you  wants to View.
    private ImageLoader imageLoader= AppController.getInstance().getImageLoader();
    private int picId;
    private String fname,lname;
    private String branchStream;
    private float rating;
    private String TAG="PROFILE_VIEW";
    private ArrayList<String> interestList=new ArrayList<>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        if(getIntent()!=null){
            helperID=getIntent().getIntExtra("HELPER_ID",0);
        }else helperID=0;

        currentUserId= Constants.getCurrentUserID(this);
        setClickListeners();
        setValuesInLayout();
        setListView();
    }

    private void setClickListeners(){
        TextView contact=(TextView)findViewById(R.id.noti_acc_profile_view_contact);
        TextView skip=(TextView)findViewById(R.id.noti_acc_profile_view_skip);

        if(contact!=null){
            contact.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){

                }
            });

        }
        if(skip!=null){
            skip.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){

                }
            });
        }
    }

    private void setValuesInLayout(){
        TextView fullName=(TextView)findViewById(R.id.noti_acc_profile_view_helper_full_name);
        TextView branchStr=(TextView)findViewById(R.id.noti_acc_profile_view_helper_branch);
        RatingBar ratingBar=(RatingBar)findViewById(R.id.noti_acc_profile_view_helper_rating);
        NetworkImageView userPic=(NetworkImageView)findViewById(R.id.noti_acc_profile_view_helper_pic);

        userPic.setImageUrl((Constants.getImagesUrl((helperID!=0 )?helperID:currentUserId)),imageLoader);
        fullName.setText(fname+" "+lname);
        branchStr.setText(branchStream);
        ratingBar.setRating(rating);
    }

    private void setListView(){
            sendToServer();
    }

    private void sendToServer(){
        JSONObject values=new JSONObject();
        try {
            values.put("CODE","userInterests");
            if(helperID!=0){
                values.put("USER_ID",helperID);
            }
            else  values.put("USERID",currentUserId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, Constants.HOME_URL + "profile/interests.php/", values,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG,response.toString());
                        try {
                            parseJsonServerResponse(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NetworkError)
                    Toast.makeText(ProfileView.this,Constants.NO_NET_WORK_CONNECTION, Toast.LENGTH_SHORT).show();
            }
        });
        AppController.getInstance().addToRequestQueue(request,TAG);

    }

    private boolean parseJsonServerResponse(JSONObject result)throws JSONException{

        ArrayList<ShowInterestsListItem> listItems=new ArrayList<>(); //array to store details of help Requests by users.
        ShowInterestsListAdapter adapter;
        int count=0;

        JSONArray jsonArray;
        int size;
        jsonArray=result.getJSONArray(Constants.SERVER_RESPONSE);
        size=jsonArray.length();
        if(size<=0){
            return  false;
        }
        for(int i=0;i<size;i++){
            JSONObject jo=jsonArray.getJSONObject(i);
            listItems.add(new ShowInterestsListItem(false,jo.getString("interest")));
        }
        adapter=new ShowInterestsListAdapter(getApplicationContext(),listItems);
        ListView listView=(ListView) findViewById(R.id.noti_acc_profile_view_interests_list_view);
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
