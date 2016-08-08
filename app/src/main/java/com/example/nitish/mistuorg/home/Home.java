package com.example.nitish.mistuorg.home;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nitish.mistuorg.Begin;
import com.example.nitish.mistuorg.R;
import com.example.nitish.mistuorg.ahr.AHR;
import com.example.nitish.mistuorg.app.AppController;
import com.example.nitish.mistuorg.ask.ASKActivity;
import com.example.nitish.mistuorg.interests.Registered;
import com.example.nitish.mistuorg.login.GoogleLoginActivity;
import com.example.nitish.mistuorg.notification.Notification;
import com.example.nitish.mistuorg.notification.NotificationListener;
import com.example.nitish.mistuorg.profile.Profile;
import com.example.nitish.mistuorg.search.Search;
import com.example.nitish.mistuorg.settings.Setting;
import com.example.nitish.mistuorg.utils.Constants;
import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ResourceType")
public class Home extends FragmentActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mDrawerTitle; // nav drawer title
    private CharSequence mTitle; // used to store app title
    private int currentPosition=0;
    private int currentUserId;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_home_layout);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();



        if(Constants.isRegisteredWithFCM(Home.this)){
            //Start your notification listener
            Toast.makeText(Home.this,"device already Registered", Toast.LENGTH_SHORT).show();
            //startService(new Intent(getBaseContext(),NotificationListener.class));
        }else{
            Toast.makeText(Home.this,"device not Registered", Toast.LENGTH_SHORT).show();
           // registerDeviceWithFCM();
        }
        Firebase.setAndroidContext(this);

        currentUserId=Constants.getCurrentUserID(this);
        Log.d("USER_ID",String.valueOf(currentUserId));

        setNavigationDrawer();

        ImageView notiImg=(ImageView)findViewById(R.id.new_home_icon1);
        ImageView ahrImg=(ImageView)findViewById(R.id.new_home_icon2);
        ImageView askImg=(ImageView)findViewById(R.id.new_home_icon4);
        ImageView searchImg=(ImageView)findViewById(R.id.new_home_icon8);


        notiImg.setOnClickListener(this);
        ahrImg.setOnClickListener(this);
        askImg.setOnClickListener(this);
        searchImg.setOnClickListener(this);

    }

    void setNavigationDrawer(){
        TypedArray navMenuIcons;     // array to store navigation list icons. (Hence TypedArray)
        String[] navMenuTitles;
        ImageView navDrawImage;
        HomeNavDrawAdapter adapter;

        navMenuTitles = getResources().getStringArray(R.array.home_nav_drawer_items); // load slide menu items
        navMenuIcons = getResources().obtainTypedArray(R.array.home_nav_drawer_icons); // nav drawer icons from resources

        ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<>(); //array to store navigation list titles.
        // adding nav drawer items to array
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1))); // Profile
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1))); // Search
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));  //Interests
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1))); //Settings
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1))); //Logout

        // Recycle the typed array must be done to avoid runtime exception.
        navMenuIcons.recycle();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.new_home_drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.new_home_slider);

        // setting the nav drawer list adapter
        adapter = new HomeNavDrawAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);

        //setting the listener to the navigation drawer
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());


        navDrawImage=(ImageView)findViewById(R.id.new_home_icon5);
        if(navDrawImage!=null) {
            navDrawImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                }
            });
        }
    }

    public void onClick(View v) {
        Intent intent;
        Fragment fragment;
        switch (v.getId() /*to get clicked view id**/) {
           case R.id.new_home_icon1:
                intent=new Intent(this, Notification.class);
                intent.putExtra("CURRENT_USER_ID",currentUserId);
                startActivity(intent);
                break;
             case R.id.new_home_icon2:
                intent=new Intent(this,AHR.class);
                intent.putExtra("CURRENT_USER_ID",currentUserId);
                startActivity(intent);
                break;
            case R.id.new_home_icon4:
                intent=new Intent(this, ASKActivity.class);
                intent.putExtra("CURRENT_USER_ID",currentUserId);
                startActivity(intent);
                break;
            case R.id.new_home_icon8:
                intent=new Intent(this, Search.class);
                startActivity(intent);
                break;
            /*
            case R.id.new_home_icon7:
                intent=new Intent(this, Profile.class);
                intent.putExtra("CURRENT_USER_ID",currentUserId);
                startActivity(intent);
                break; */

            default:
                break;


        }
    }


    private  void setFragment(Fragment fragment){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft=fragmentManager.beginTransaction();
        ft.replace(R.id.home_container, fragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    /**
     * Displaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        Intent intent=null;
        switch (position) {
            case 0:
                intent=new Intent(this,Profile.class);
                intent.putExtra("CURRENT_USER_ID",currentUserId);
                startActivity(intent);
                break;
            case 1:
                intent=new Intent(this, Search.class);
                intent.putExtra("CURRENT_USER_ID",currentUserId);
                startActivity(intent);
                break;
            case 2:
                intent=new Intent(this, Registered.class);
                startActivity(intent);
                break;
            case 3:
                intent=new Intent(this, Setting.class);
                startActivity(intent);
                break;
            case 4:
                Constants.logOutUser(getApplicationContext());
                mAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                Log.d("HOME,SIGN OUT CALLED",status.toString());
                            }
                        });

                Intent login=new Intent(getApplicationContext(), GoogleLoginActivity.class);
                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(login);
                finish();
                break;
            case 5:
                //fragment = new WhatsHotFragment();
                break;
            default:
                break;
        }

    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        //mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        // mDrawerToggle.onConfigurationChanged(newConfig);
    }



    private void registerDeviceWithFCM(){
        Firebase firebase=new Firebase(Constants.FIREBASE_URL);
        Firebase newFirebase=firebase.push();

        // Creating a map to store key value pair
        Map<String,String> val=new HashMap<>();

        // pushing msg=none in the map
        val.put("msg","none");

        newFirebase.setValue(val);

        String uniqueId=newFirebase.getKey();
        Toast.makeText(Home.this, uniqueId, Toast.LENGTH_SHORT).show();

        sendFCMIDToServer(uniqueId);

    }

    private void sendFCMIDToServer(final String uniqueId){
       // PDialog.showProgressDialog(this,"Registering Notification Services");
        final  String email=Constants.getCurrentEmailID(this).trim();

        //Creating a string request
        StringRequest req=new StringRequest(Request.Method.POST, Constants.FCM_REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       // PDialog.hideProgressDialog(Home.this);

                        Log.d("ResponseOfServer",response.trim());
                        // if success
                        if(response.trim().equalsIgnoreCase("success")){
                            Log.d("FIREBASE","REGISTERED SUCCESSFULLY");
                            Constants.setNotificationData(Home.this,uniqueId);
                            startService(new Intent(getBaseContext(),NotificationListener.class));
                        }
                        else if(response.trim().equalsIgnoreCase("failure")) {
                            //Toast.makeText(getApplicationContext(), "Choose a different email", Toast.LENGTH_SHORT).show();
                            Log.d("FIREBASE","choose different email_id");
                        }else {
                            Log.d("FIREBASE","unknown error");
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error instanceof NetworkError){
                            Toast.makeText(Home.this, Constants.NO_NET_WORK_CONNECTION, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //adding parameters to post request as we need to send firebase id and email
                params.put("firebaseid", uniqueId);
                params.put("email", email);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(req,"FCM_REGISTER");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Home","onConnectionFailed:"+connectionResult);
        Toast.makeText(this, "Google Play Services error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to Exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

}

