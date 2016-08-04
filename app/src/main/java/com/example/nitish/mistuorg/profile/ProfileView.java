package com.example.nitish.mistuorg.profile;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
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
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private String mobile;
    private int hasUserConfirmed;
    private int picId;
    private String name;
    private String branchStream;
    private float rating;
    private String type;
    private String TAG = "PROFILE_VIEW";
    private ArrayList<String> interestList = new ArrayList<>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        if (getIntent() != null) {
            type=getIntent().getStringExtra("TYPE");
            helperID = getIntent().getIntExtra("HELPER_ID", -1);
            name = getIntent().getStringExtra(Constants.FNAME);
            hasUserConfirmed = getIntent().getIntExtra("HAS_USER_CONFIRMED", 0);
            mobile = getIntent().getStringExtra("MOBILE_NO");
        } else {
            helperID = -1;
            name = Constants.getCurrentFname(this) + " " + Constants.getCurrentLname(this);
            hasUserConfirmed = 0;
        }

        currentUserId = Constants.getCurrentUserID(this);
        setClickListeners();
        setValuesInLayout();
        setListView();
    }

    private void setClickListeners() {
        ImageView callButton = (ImageView) findViewById(R.id.helper_detail_contact);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasUserConfirmed == 1 && (currentUserId != helperID)) {
                    Toast.makeText(ProfileView.this, "Calling "+mobile, Toast.LENGTH_SHORT).show();
                    callTheHelper();
                } else if (currentUserId == helperID) {
                    Toast.makeText(ProfileView.this, "You are trying to call yourself", Toast.LENGTH_SHORT).show();
                } else {
                    sendContactRequest();
                }
            }
        });

    }

    private void setValuesInLayout() {
        TextView fullName = (TextView) findViewById(R.id.noti_acc_profile_view_helper_full_name);
        TextView branchStr = (TextView) findViewById(R.id.noti_acc_profile_view_helper_branch);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.noti_acc_profile_view_helper_rating);
        NetworkImageView userPic = (NetworkImageView) findViewById(R.id.noti_acc_profile_view_helper_pic);

        userPic.setImageUrl((Constants.getImagesUrl((helperID != -1) ? helperID : currentUserId)), imageLoader);
        fullName.setText(name);
        branchStr.setText(branchStream);
        ratingBar.setRating(rating);
    }

    private void setListView() {
        sendToServer();
    }

    private void sendToServer() {
        JSONObject values = new JSONObject();
        try {
            values.put("CODE", "userInterests");
            if (helperID != 0) {
                values.put("USER_ID", helperID);
            } else values.put("USERID", currentUserId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constants.HOME_URL + "profile/interests.php/", values,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            parseJsonServerResponse(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError)
                    Toast.makeText(ProfileView.this, Constants.NO_NET_WORK_CONNECTION, Toast.LENGTH_SHORT).show();
            }
        });
        AppController.getInstance().addToRequestQueue(request, TAG);

    }

    private boolean parseJsonServerResponse(JSONObject result) throws JSONException {

        ArrayList<ShowInterestsListItem> listItems = new ArrayList<>(); //array to store details of help Requests by users.
        ShowInterestsListAdapter adapter;
        int count = 0;

        JSONArray jsonArray;
        int size;
        jsonArray = result.getJSONArray(Constants.SERVER_RESPONSE);
        size = jsonArray.length();
        if (size <= 0) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            JSONObject jo = jsonArray.getJSONObject(i);
            listItems.add(new ShowInterestsListItem(false, jo.getString("interest")));
        }
        adapter = new ShowInterestsListAdapter(getApplicationContext(), listItems);
        ListView listView = (ListView) findViewById(R.id.noti_acc_profile_view_interests_list_view);
        if (listView == null) {
            Log.e("EMPTY", "List view is empty");
        } else {
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

    private void callTheHelper() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        String mobileNo="tel:"+mobile;
        callIntent.setData(Uri.parse(mobileNo));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        this.startActivity(callIntent);
    }

    private void sendContactRequest(){

        JSONObject values=new JSONObject();
        try {
            values.put("CODE","sendContactRequest");
            values.put("USERID",currentUserId);
            values.put("HELPER_ID",helperID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("USER_ID,HELPER_ID",String.valueOf(currentUserId)+String.valueOf(helperID));
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, Constants.HOME_URL +"contacts/", values, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("CONTACT_REQUEST",response.toString());
                try {
                    if(Integer.parseInt(response.getJSONObject(Constants.SERVER_RESPONSE).getString("status"))==1){
                        Toast.makeText(ProfileView.this,"Contact Request sent,We will let u know once helper confirms it", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            if (error instanceof NetworkError)
                Toast.makeText(ProfileView.this, Constants.NO_NET_WORK_CONNECTION, Toast.LENGTH_SHORT).show();
            }
        });
        AppController.getInstance().addToRequestQueue(request,"CONTACT_REQUEST");
    }
}
