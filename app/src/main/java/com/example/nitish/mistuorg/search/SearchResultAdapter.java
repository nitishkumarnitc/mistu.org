package com.example.nitish.mistuorg.search;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.nitish.mistuorg.R;
import com.example.nitish.mistuorg.app.AppController;
import com.example.nitish.mistuorg.profile.ProfileView;
import com.example.nitish.mistuorg.utils.Constants;
;
import java.util.ArrayList;

public class SearchResultAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<SearchResultUser> listItems=new ArrayList<>();
    private ImageLoader imageLoader= AppController.getInstance().getImageLoader();
    public SearchResultAdapter(Context context, ArrayList<SearchResultUser> listItems) {
        this.context = context;
        this.listItems = listItems;
    }


    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.helper_details_list_item, null);
        }
        ImageView tick=(ImageView)convertView.findViewById(R.id.user_confirmed);
        TextView name=(TextView)convertView.findViewById(R.id.helper_detail_name);
        final ImageView contact=(ImageView)convertView.findViewById(R.id.helper_detail_contact);

        NetworkImageView profilePic=(NetworkImageView) convertView.findViewById(R.id.helper_detail_pic);
        final SearchResultUser item=listItems.get(position);

        name.setText(item.getName());
        profilePic.setImageUrl(Constants.getImagesUrl(item.getUserId()),imageLoader);

        if(item.getHasUserConfirmed()!=1){
            contact.setVisibility(View.GONE);
        }


        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.getHasUserConfirmed()==1){
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    String mobileNo="tel:"+item.getMobile();
                    callIntent.setData(Uri.parse(mobileNo));
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    context.startActivity(callIntent);
                }
            }
        });


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ProfileView.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("HELPER_ID",item.getUserId());
                intent.putExtra(Constants.NAME,item.getName());
                intent.putExtra("MOBILE_NO",item.getMobile());
                intent.putExtra("HAS_USER_CONFIRMED",item.getHasUserConfirmed());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

}
