package com.example.nitish.mistuorg.notification;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.nitish.mistuorg.R;
import com.example.nitish.mistuorg.app.AppController;
import com.example.nitish.mistuorg.profile.ProfileView;
import com.example.nitish.mistuorg.utils.Constants;

import java.util.ArrayList;

/**
 * Created by nitish on 02-08-2016.
 */
public class NotiContactReqAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<NotiContactReqListItem> listItems;
    private ImageLoader imageLoader= AppController.getInstance().getImageLoader();
    private int currentUserId;

    public NotiContactReqAdapter( Context context,ArrayList<NotiContactReqListItem> listItems) {
        this.listItems = listItems;
        this.context = context;
        currentUserId= Constants.getCurrentUserID(context);
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int i) {
        return listItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(view==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.noti_contact_req_list_item,null);
        }

        NetworkImageView imageView=(NetworkImageView) view.findViewById(R.id.noti_contact_req_list_item_img);
        final NotiContactReqListItem item=listItems.get(i);
        TextView textView= (TextView) view.findViewById(R.id.noti_contact_req_info);
        imageView.setImageUrl(Constants.getImagesUrl(item.getRequesterID()),imageLoader);

        String name=item.getName();
        if(item.getType()==0){
            textView.setText(name+" is requesting for your contact details");
        }
        else if(item.getType()==1){
            textView.setText(name+" has provided contact details");
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ProfileView.class);
                if(item.getType()==0){// 0 for request
                    intent.putExtra("TYPE","NOTIF_CONTACT_REQ_REQUESTED");
                }else if(item.getType()==1){// 1 for accepted
                    intent.putExtra("TYPE","NOTIF_CONTACT_REQ_ACCEPTED");
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("HELPER_ID",item.getRequesterID());
                intent.putExtra(Constants.NAME,item.getName());
                intent.putExtra("MOBILE_NO",item.getMobile());
                intent.putExtra("HAS_USER_CONFIRMED",item.getHasUserConfirmed());
                context.startActivity(intent);
            }
        });
        return null;
    }
}
