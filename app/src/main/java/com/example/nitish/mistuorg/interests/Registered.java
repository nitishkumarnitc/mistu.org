package com.example.nitish.mistuorg.interests;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.nitish.mistuorg.R;
import com.example.nitish.mistuorg.app.AppController;
import com.example.nitish.mistuorg.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Registered extends AppCompatActivity {

    private int type; // tells from where Registered class is being called . ( Verification or Home )

    private List<Interest> academicsList = new ArrayList<>();
    private List<Interest> sportsList = new ArrayList<>();
    private List<Interest> artsList = new ArrayList<>();
    private List<Interest> technicalList = new ArrayList<>();
    private List<Interest> othersList = new ArrayList<>();

    private String department, stream, name;
    private int userId;

    private ArrayList<String> addedInterests=new ArrayList<>();
    private ArrayList<Integer> academicsCheckedBoxIds=new ArrayList<>();
    private ArrayList<Integer> sportsCheckedBoxIds=new ArrayList<>();
    private ArrayList<Integer> artsCheckedBoxIds=new ArrayList<>();
    private ArrayList<Integer> techCheckedBoxIds=new ArrayList<>();
    private ArrayList<Integer> othersCheckedBoxIds=new ArrayList<>();



    private RecyclerView[] recyclerView=new RecyclerView[5];
    private InterestAdapter[] mAdapter=new InterestAdapter[5];
    private RecyclerView.LayoutManager[] mLayoutManager=new RecyclerView.LayoutManager[5];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);
            SharedPreferences sharedPreferences=this.getSharedPreferences(Constants.SHARED_PREF,MODE_PRIVATE);
            name =sharedPreferences.getString(Constants.NAME,"");
            department =sharedPreferences.getString(Constants.DEPARTMENT,"");
            stream = sharedPreferences.getString(Constants.STREAM,"");
            userId = sharedPreferences.getInt(Constants.USER_ID,0);

        TextView acadInfo=(TextView)findViewById(R.id.interest_main_activity_academic_note);
        acadInfo.setText(department);

        for (int i = 0; i < 5; i++) {
            recyclerView[i] = getInterestViewId(i);
            mAdapter[i] = getInterestAdapter(i);
            mLayoutManager[i] = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

            recyclerView[i].setLayoutManager(mLayoutManager[i]);
            recyclerView[i].setItemAnimator(new DefaultItemAnimator());
            recyclerView[i].addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL));
            recyclerView[i].setAdapter(mAdapter[i]);


            prepareInterestListView(i);
        }

        if(savedInstanceState!=null) {
            addedInterests = savedInstanceState.getStringArrayList("ADDED_INTEREST");
            academicsCheckedBoxIds = savedInstanceState.getIntegerArrayList("ACAD_CHECKEDBOX_IDS");

            String temp="";
            String temp2="";
            for(int i=0;i<addedInterests.size();i++){
                temp=temp+addedInterests.get(i)+"\n";
            }
            for(int i=0;i<academicsCheckedBoxIds.size();i++){
                temp2=temp2+academicsCheckedBoxIds.get(i)+"\n";
            }

            Log.i("TEST_SAVED",temp+temp2);


            for(int i=0;i<academicsCheckedBoxIds.size();i++) {
                View v=recyclerView[0].getLayoutManager().getChildAt(academicsCheckedBoxIds.get(i));
                //CheckBox checkBox=(CheckBox)v.findViewById(R.id.interest_checkBox);
                //checkBox.setChecked(true);
            }
        }

        recyclerView[0].addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView[0], new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Interest academics = academicsList.get(position);
                CheckBox checkBox=(CheckBox) view.findViewById(R.id.interest_checkBox);
                if(!academics.isChecked()){
                    checkBox.setChecked(true);
                    academics.setChecked(true);
                    addedInterests.add(academics.getName());
                }
                else{
                    checkBox.setChecked(false);
                    academics.setChecked(false);
                    addedInterests.remove(academics.getName());
                }

            }
            @Override
            public void onLongClick(View view,int position){

            }
        }));

        recyclerView[1].addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView[1], new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Interest sports = sportsList.get(position);
                CheckBox checkBox=(CheckBox) view.findViewById(R.id.interest_checkBox);
                if(!sports.isChecked()){
                    checkBox.setChecked(true);
                    sports.setChecked(true);
                    addedInterests.add(sports.getName());
                }
                else{
                    checkBox.setChecked(false);
                    sports.setChecked(false);
                    addedInterests.remove(sports.getName());
                }

            }

            @Override
            public void onLongClick(View view,int position){
            }
        }));

        recyclerView[2].addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView[2], new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Interest arts = artsList.get(position);
                CheckBox checkBox=(CheckBox) view.findViewById(R.id.interest_checkBox);
                if(!arts.isChecked()){
                    checkBox.setChecked(true);
                    arts.setChecked(true);
                    addedInterests.add(arts.getName());
                }
                else{
                    checkBox.setChecked(false);
                    arts.setChecked(false);
                    addedInterests.remove(arts.getName());
                }

            }

            @Override
            public void onLongClick(View view,int position){
            }
        }));

        recyclerView[3].addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView[3], new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Interest tech = technicalList.get(position);
                CheckBox checkBox=(CheckBox) view.findViewById(R.id.interest_checkBox);
                if(!tech.isChecked()){
                    checkBox.setChecked(true);
                    tech.setChecked(true);
                    addedInterests.add(tech.getName());
                }
                else{
                    checkBox.setChecked(false);
                    tech.setChecked(false);
                    addedInterests.remove(tech.getName());
                }

            }

            @Override
            public void onLongClick(View view,int position){
            }
        }));

        recyclerView[4].addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView[4], new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Interest others = othersList.get(position);
                CheckBox checkBox=(CheckBox) view.findViewById(R.id.interest_checkBox);
                if(!others.isChecked()){
                    checkBox.setChecked(true);
                    others.setChecked(true);
                    addedInterests.add(others.getName());
                }
                else{
                    checkBox.setChecked(false);
                    others.setChecked(false);
                    addedInterests.remove(others.getName());
                }

            }

            @Override
            public void onLongClick(View view,int position){
            }
        }));


    }


    private void prepareInterestListView(int position){
        String[] interestArray=null;
        TypedArray interestIcons=null;

        switch (position){
            case 0:
                interestArray=getAcademicInterestListByBranch(department,stream);
                interestIcons=getResources().obtainTypedArray(R.array.nav_drawer_icons);
                for(int i=0;i<interestArray.length;i++){
                    Interest interest=new Interest(interestArray[i],interestIcons.getResourceId(i%10,-1),false);
                    academicsList.add(interest);
                }

                break;
            case 1:
                interestArray=getResources().getStringArray(R.array.interests_sports);
                interestIcons=getResources().obtainTypedArray(R.array.nav_drawer_icons);
                for(int i=0;i<interestArray.length;i++){
                    Interest interest=new Interest(interestArray[i],interestIcons.getResourceId(i%10,-1),false);
                    sportsList.add(interest);
                }
                break;
            case 2:
                interestArray=getResources().getStringArray(R.array.interests_arts);
                interestIcons=getResources().obtainTypedArray(R.array.nav_drawer_icons);
                for(int i=0;i<interestArray.length;i++){
                    Interest interest=new Interest(interestArray[i],interestIcons.getResourceId(i%10,-1),false);
                    artsList.add(interest);
                }
                break;
            case 3:
                interestArray=getResources().getStringArray(R.array.interests_technical);
                interestIcons=getResources().obtainTypedArray(R.array.nav_drawer_icons);
                for(int i=0;i<interestArray.length;i++){
                    Interest interest=new Interest(interestArray[i],interestIcons.getResourceId(i%10,-1),false);
                    technicalList.add(interest);
                }
                break;

            case 4:
                interestArray=getResources().getStringArray(R.array.interests_others);
                interestIcons=getResources().obtainTypedArray(R.array.nav_drawer_icons);
                for(int i=0;i<interestArray.length;i++){
                    Interest interest=new Interest(interestArray[i],interestIcons.getResourceId(i%10,-1),false);
                    othersList.add(interest);
                }
                break;

            default:
                interestIcons=getResources().obtainTypedArray(R.array.nav_drawer_icons);
                break;
        }
        mAdapter[position].notifyDataSetChanged();
        interestIcons.recycle();
    }

    private String[] getAcademicInterestListByBranch(String branch,String  stream){
        Log.d("BRANCH",branch+ " "+ "yahi hai" );
        branch=branch.toUpperCase();
        if(branch.equals("CSE")){
            return getResources().getStringArray(R.array.interests_cse);
        }
        else if(branch.equals("ECE")){
            return getResources().getStringArray(R.array.interests_ece);
        }
        else if(branch.equals("EEE")){
            return getResources().getStringArray(R.array.interests_eee);
        }
        else if(branch.equals("MECH")){
            return getResources().getStringArray(R.array.interests_mech);
        }
        else if(branch.equals("CIVIL")){
            return getResources().getStringArray(R.array.interests_civil);
        }
        else if(branch.equals("PRO")){
            return getResources().getStringArray(R.array.interests_pro);
        }
        else if(branch.equals("ARCH")){
            return getResources().getStringArray(R.array.interests_arch);
        }
        else if(branch.equals("BIO")){
            return getResources().getStringArray(R.array.interests_bt);
        }
        else if(branch.equals("CHEM")){
            return getResources().getStringArray(R.array.interests_chem);
        }
        else if(branch.equals("EP")){
            return getResources().getStringArray(R.array.interests_ep);
        }
        else{
            return getResources().getStringArray(R.array.interests_cse);
        }
    }


    private RecyclerView getInterestViewId(int position){
        switch (position){
            case 0:
                return (RecyclerView) findViewById(R.id.recycler_academics);
            case 1:
                return (RecyclerView) findViewById(R.id.recycler_sports);
            case 2:
                return (RecyclerView) findViewById(R.id.recycler_arts);
            case 3:
                return (RecyclerView) findViewById(R.id.recycler_technical);
            case 4:
                return (RecyclerView) findViewById(R.id.recycler_others);
            default:
                return null;
        }
    }

    private InterestAdapter getInterestAdapter(int position){
        switch (position){
            case 0:
                return new InterestAdapter(academicsList);
            case 1:
                return new InterestAdapter(sportsList);
            case 2:
                return new InterestAdapter(artsList);
            case 3:
                return new InterestAdapter(technicalList);
            case 4:
                return new InterestAdapter(othersList);
            default:
                return null;
        }
    }

    public void onClickSubmit(View v){
        String temp="";
        String line="";
        ArrayList<String> finalList=new ArrayList<>();
        for(int i=0;i<addedInterests.size();i++){
            temp=temp+addedInterests.get(i)+"\n";
            line=addedInterests.get(i);
            line= line.replaceAll(" ", "_");
            line=line.replaceAll("-","_");
            line=line.toLowerCase();
            finalList.add(line);
        }

        if(type==0) { //called from verification class.

            if (addedInterests.size() > 0) {
                //send selected interests to server
                Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_SHORT).show();
                sendToServer(finalList);

                //sending signal to calling activity that the interests are selected
                Intent returnIntent = new Intent();
                returnIntent.putExtra("Selected", true);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Interests can't be empty!", Toast.LENGTH_SHORT).show();
            }
        }
        else { //called from home navigation drawer.
            Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_SHORT).show();
            sendToServer(finalList);
            finish();
        }

    }

    private void sendToServer(ArrayList<String> finalList){
        JSONObject values=new JSONObject();
        try {
            values.put("CODE","UserInterests");
            values.put("USERID",userId);
            values.put("VALUES",new JSONArray(finalList));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, Constants.HOME_URL + "interest/saveinterests.php/", values,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        int success=0;
                        int error=0;
                        try {
                            success=response.getInt("success");
                            error=response.getInt("error");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("RESULT",response.toString());

                        if(success==1){
                            Toast.makeText(Registered.this, "Thanks for sharing your interests !!!", Toast.LENGTH_SHORT).show();
                        }else if (error==1){
                            Toast.makeText(Registered.this, "Error in sharing the interest", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NetworkError){
                    Toast.makeText(Registered.this,Constants.NO_NET_WORK_CONNECTION, Toast.LENGTH_SHORT).show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(request,"Registere.java");

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("ADDED_INTEREST",addedInterests);
        outState.putIntegerArrayList("ACAD_CHECKEDBOX_IDS",academicsCheckedBoxIds);
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private Registered.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final Registered.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}
