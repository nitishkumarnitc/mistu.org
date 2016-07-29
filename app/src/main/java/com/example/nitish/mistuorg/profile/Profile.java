package com.example.nitish.mistuorg.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import com.example.nitish.mistuorg.R;
import com.example.nitish.mistuorg.app.AppController;
import com.example.nitish.mistuorg.utils.Constants;
import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int currentUserId;

    private NetworkImageView userPic;
    private ImageLoader imageLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if(imageLoader==null){
            imageLoader= AppController.getInstance().getImageLoader();
        }

        currentUserId= Constants.getCurrentUserID(this);
        displayUserDetails();

        viewPager = (ViewPager) findViewById(R.id.profile_viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.profile_tabs);
        if(tabLayout!=null) {
            tabLayout.setupWithViewPager(viewPager);
        }

    }

    private void displayUserDetails(){
        SharedPreferences sharedPreferences=this.getSharedPreferences(Constants.SHARED_PREF,MODE_PRIVATE);
        String fullname=sharedPreferences.getString(Constants.FNAME,"")+" "+ sharedPreferences.getString(Constants.LNAME,"");
        String stream=sharedPreferences.getString(Constants.STREAM,"");
        String department=sharedPreferences.getString(Constants.DEPARTMENT,"");

        TextView name=(TextView)findViewById(R.id.profile_name);
        TextView branchStream=(TextView)findViewById(R.id.profile_branch_stream);
        userPic=(NetworkImageView)findViewById(R.id.profile_pic);

        name.setText(fullname);
        branchStream.setText(stream+","+department);

        userPic.setImageUrl(Constants.HOME_URL+"email/pictures/"+currentUserId+".jpg",imageLoader);

        TextView interests=(TextView)findViewById(R.id.profile_interests);
        if(interests!=null) {
            interests.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getApplicationContext(),ShowInterests.class);
                    intent.putExtra("USERID",currentUserId);
                    startActivity(intent);
                }
            });
        }

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new AcceptsFragment(), "Accepted");
        adapter.addFragment(new AskedFragment(), "Asked");
        adapter.addFragment(new HelpedFragment(), "Helped");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d("Fragment Clicked ",String.valueOf(position+1));
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            //return mFragmentList.size();
            Log.d("Fragment size",String.valueOf(mFragmentList.size()));
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
