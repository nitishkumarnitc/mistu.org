package com.example.nitish.mistuorg.settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.nitish.mistuorg.R;
import com.example.nitish.mistuorg.app.AppController;
import com.example.nitish.mistuorg.utils.Constants;
import org.json.JSONException;
import org.json.JSONObject;

public class Reset extends AppCompatActivity {
    private int position;
    private String itemName;
    private EditText editText1,editText2;
    private String TAG="RESET";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        position=getIntent().getIntExtra("pos",-1);
        itemName=getIntent().getStringExtra("itemName");
        getSupportActionBar().setTitle(itemName);
        setView();
    }


    private void setView(){
        editText1=(EditText)findViewById(R.id.reset_editText1);
        editText2=(EditText)findViewById(R.id.reset_editText2);
        switch (position){
            case 0:
                if(editText1!=null) editText1.setHint("Old Password");
                if(editText2!=null) editText2.setHint("New Password");
                break;

            case 1:
                editText1.setVisibility(View.GONE);
                if(editText2!=null) editText2.setHint("New Email ID");
                break;

            case 2:
                editText1.setVisibility(View.GONE);
                if(editText2!=null) editText2.setHint("New Contact Number");
                break;

            case 3:
                if(editText1!=null) editText1.setHint("New First Name");
                if(editText2!=null) editText2.setHint("New Last Name");
                break;

            case 4:
                break;
        }
    }

    public void onClickDone(View v) {
        String itemName=null;
        String string1=null,string2=null;
        switch (position) {
            case 0:
                itemName="change_password";
                string1=editText1.getText().toString();
                string2=editText2.getText().toString();
                break;

            case 1:
                itemName="change_email_id";
                string2=editText2.getText().toString();
                break;

            case 2:
                itemName="change_contact_number";
                string2=editText2.getText().toString();
                break;

            case 3:
                itemName="change_name";
                string1=editText1.getText().toString();
                string2=editText2.getText().toString();
                break;

        }
        JSONObject values=new JSONObject();
        try {
            values.put("tag",itemName);
            values.put(Constants.USER_ID, Constants.getCurrentUserID(this));
            values.put(Constants.EMAIL_ID,Constants.getCurrentEmailID(this));
            values.put("string1",string1);
            values.put("string2",string2);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, Constants.HOME_URL + "login/", values,
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
                if (error instanceof NetworkError)
                    Toast.makeText(Reset.this,Constants.NO_NET_WORK_CONNECTION, Toast.LENGTH_SHORT).show();
            }
        });
        AppController.getInstance().addToRequestQueue(request,TAG);

    }

    private boolean parseJsonServerResponse(JSONObject result)throws JSONException{
        Log.d("SettingResult",result.toString());
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppController.getInstance().cancelPendingRequest(TAG);
    }
}
