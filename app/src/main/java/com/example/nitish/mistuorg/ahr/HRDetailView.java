package com.example.nitish.mistuorg.ahr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class HRDetailView extends AppCompatActivity {

    private String name,category,title,description,tag1,tag2,tag3;
    private int picId,helpId,helpieId;
    private int currentUserId;
    private NetworkImageView helpie_pic;
    private int isAcceptClickable;
    private int notiId; //if activity is called from notification.
    private Button accept;
    private ImageLoader imageLoader= AppController.getInstance().getImageLoader();
    private String TAG="HRDetailView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hrdetail_view);

        Intent intent=getIntent();
        currentUserId= Constants.getCurrentUserID(this);

        name=intent.getStringExtra("NAME");
        category=intent.getStringExtra("CAT");
        title=intent.getStringExtra("TITLE");
        description=intent.getStringExtra("DES");
        tag1=intent.getStringExtra("TAG1");
        tag2=intent.getStringExtra("TAG2");
        tag3=intent.getStringExtra("TAG3");
        helpId=intent.getIntExtra("HELPID",0);
        helpieId=intent.getIntExtra("HELPIEID",0);
        isAcceptClickable=intent.getIntExtra("ACCEPT",1);
        notiId=intent.getIntExtra("NOTIID",-1); //-1 if it is called from classes other than notification class

        setLayoutData();
    }

    private void setLayoutData(){
        TextView name=(TextView)findViewById(R.id.hr_details_helpie_name);
        TextView cat=(TextView)findViewById(R.id.hr_details_category);
        TextView title=(TextView)findViewById(R.id.hr_details_title);
        TextView des=(TextView)findViewById(R.id.hr_details_description);
        TextView tag1=(TextView)findViewById(R.id.hr_details_tag1);
        TextView tag2=(TextView)findViewById(R.id.hr_details_tag2);
        TextView tag3=(TextView)findViewById(R.id.hr_details_tag3);
        accept=(Button)findViewById(R.id.hr_details_accept);
        if(isAcceptClickable==0 || currentUserId==helpieId){
            if(accept!=null){
                accept.setClickable(false);
            }
        }

        helpie_pic=(NetworkImageView)findViewById(R.id.hr_details_pic);
        name.setText(this.name);
        cat.setText(this.category);
        title.setText(this.title);
        des.setText(this.description);
        helpie_pic.setImageUrl(Constants.getImagesUrl(helpieId),imageLoader);

        if((this.tag1).equals("")){
            tag1.setVisibility(View.GONE);
        }
        else{
            tag1.setText(this.tag1);
        }

        if((this.tag2).equals("")){
            tag2.setVisibility(View.GONE);
        }
        else{
            tag2.setText(this.tag2);
        }

        if((this.tag3).equals("")){
            tag3.setVisibility(View.GONE);
        }
        else{
            tag3.setText(this.tag3);
        }


    }

    public void requestAccept(View v){
        if(accept!=null){
            accept.setClickable(false);
        }
        Toast.makeText(getApplicationContext(),"Thanks! we will notify the user about it!!!"
                ,Toast.LENGTH_LONG).show();
        sendToServer(helpId);
    }

    private void sendToServer(int helpId){
        JSONObject values=new JSONObject();
        try {
            values.put("CODE","accept");
            values.put("HELPERID",currentUserId);
            values.put("HELPID",helpId);
            values.put("NOTIID",notiId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, Constants.HOME_URL + "ahr", values,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG,response.toString());
                        parseJsonServerResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError)
                    Toast.makeText(getApplicationContext(),Constants.NO_NET_WORK_CONNECTION, Toast.LENGTH_SHORT).show();
            }
        });
        AppController.getInstance().addToRequestQueue(request,TAG);
    }

    private int parseJsonServerResponse(JSONObject result){
            int res=0;
            Log.e("RESULT",result.toString());
            try {
                JSONArray jsonArray = result.getJSONArray("server_response");
                JSONObject jo=jsonArray.getJSONObject(0);
                String success=jo.getString("success");
                Log.e("SUCCESS",success);
                if(success.equals("yes")){
                    res=1;
                }
            }catch (JSONException ex){
                ex.printStackTrace();
            }
            return res;
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppController.getInstance().cancelPendingRequest(TAG);
    }
}



