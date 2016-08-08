package com.example.nitish.mistuorg.ask;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.nitish.mistuorg.R;
import com.example.nitish.mistuorg.ahr.AHR;
import com.example.nitish.mistuorg.app.AppController;
import com.example.nitish.mistuorg.utils.Constants;
import com.example.nitish.mistuorg.utils.ProgressDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class ASKFragment extends ProgressDialogFragment implements View.OnClickListener{
    private final String TAG="ASKFragment";
    private Context context;
    private View rootView;
    private int pos;
    private String tag1,tag2,tag3;
    private TextView catText;
    private TextView tagText;
    private String category="";
    private String tag="";

    private android.app.Fragment fragment=this;

    public ASKFragment() {
        // Required empty public constructor
    }
    public void setPos(int pos){
        String[] catNames= {"Academic","Emergency","Technical","Examination","Stationary","Finance","Medical","Placements","Sports" ,
                "Extra-Curricular","Contacts","Hostel Issues","Mess Issues","Others" };
        this.pos=pos;
        category=catNames[pos];
        catText.setText(category);

    }
    public void setTags(String tag1,String tag2,String tag3){
        this.tag1=tag1;
        this.tag2=tag2;
        this.tag3=tag3;
        tag="";

        if(tag1.length()>0)tag+=tag1;
        else tag1="null";
        if(tag2.length()>0)tag=tag+" "+tag2;
        else tag2="null";
        if(tag3.length()>0)tag=tag+" "+tag3;
        else tag3="null";
        tagText.setText(tag);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.fragment_ask, container, false);
        context=inflater.getContext();
        setListeners();

        return  rootView;
    }

    private void setListeners(){
        catText=(TextView)rootView.findViewById(R.id.cat_name);
        ImageView catIcon=(ImageView)rootView.findViewById(R.id.cat_icon);
        catText.setOnClickListener(this);
        catIcon.setOnClickListener(this);

        tagText=(TextView)rootView.findViewById(R.id.tag_name);
        ImageView tagIcon=(ImageView)rootView.findViewById(R.id.tag_icon);
        tagText.setOnClickListener(this);
        tagIcon.setOnClickListener(this);

        Button submit=(Button)rootView.findViewById(R.id.ask_submit);
        submit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int vID=v.getId();
        if(vID==R.id.cat_name || vID==R.id.cat_icon){
            Intent intent=new Intent(getActivity(),ASKCat.class);
            getActivity().startActivityForResult(intent,22);
        }
        else if(vID==R.id.tag_name || vID==R.id.tag_icon){
            Intent intent=new Intent(getActivity(),TAG.class);
            getActivity().startActivityForResult(intent,44);
        }
        else if(vID==R.id.ask_submit){
            //Toast.makeText(context,"Submit",Toast.LENGTH_SHORT).show();


            EditText title=(EditText)rootView.findViewById(R.id.title_edit);
            EditText des=(EditText)rootView.findViewById(R.id.des_edit);

            String titleStr=title.getText().toString();
            String desStr=des.getText().toString();

            if(titleStr.length()==0 || desStr.length()==0 ||category=="" || tag==""){
                Toast.makeText(context,"All fields are mandatory !!! ",Toast.LENGTH_SHORT).show();
            }else{
                //Toast.makeText(context, "Calling sendHelpRequest", Toast.LENGTH_SHORT).show();
                sendHelpRequest(titleStr,desStr);
            }

        }

    }

    private void sendHelpRequest(String title,String des){
        showProgressDialog("Sending Help Request");
        JSONObject values=new JSONObject();
        try {
            values.put("CODE","askforhelp");
            values.put("HELPIE_ID", Constants.getCurrentUserID(context));
            values.put("CATEGORY",category);
            values.put("TITLE",title);
            values.put("DESCRIPTION",des);
            values.put("TAG1",tag1);
            values.put("TAG2",tag2);
            values.put("TAG3",tag3);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest helpRequest=new JsonObjectRequest(Request.Method.POST, Constants.HOME_URL + "askforhelp/", values,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("ASK_FOR_HELP",response.toString());
                        try {
                            hideProgressDialog();
                            Button submit=(Button)rootView.findViewById(R.id.ask_submit);

                            if(response.getInt("success")==3){

                                Toast.makeText(context,"Your Request is under process, we will notify you as soon as we find someone to help you !!!"
                                        ,Toast.LENGTH_SHORT).show();
                            }
                            goToAhr();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NoConnectionError) {
                    hideProgressDialog();
                    Toast.makeText(context,"No Internet Connection",Toast.LENGTH_LONG).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(helpRequest,TAG);

    }

    private void goToAhr(){
        Intent ahrIntent=new Intent(context, AHR.class);
        startActivity(ahrIntent);
        getActivity().getFragmentManager().beginTransaction().remove(fragment).commit();
        getActivity().finish();
    }

}

