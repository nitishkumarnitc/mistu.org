package com.example.nitish.mistuorg.notification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.nitish.mistuorg.Contact;
import com.example.nitish.mistuorg.R;
import com.example.nitish.mistuorg.app.AppController;
import com.example.nitish.mistuorg.utils.Constants;

import java.util.ArrayList;

public class NotiACCListAdapter  extends BaseAdapter {

    private Context context;
    private ArrayList<NotiACCListItem> listItems;
    private ImageLoader imageLoader= AppController.getInstance().getImageLoader();
    private int currentUserId;

    NotiACCListAdapter(Context context,ArrayList<NotiACCListItem> listItems){
        this.context=context;
        this.listItems=listItems;
        currentUserId= Constants.getCurrentUserID(context);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.noti_accepts_list_item,null);
        }

        TextView acceptInfo=(TextView)convertView.findViewById(R.id.noti_acc_info);
        TextView shortTitle=(TextView)convertView.findViewById(R.id.noti_acc_list_help_title);
        NetworkImageView helperPic=(NetworkImageView) convertView.findViewById(R.id.noti_acc_list_item_img);

        String name=listItems.get(position).getName();
        name= "<b>" + name + "</b> "+" "+"wants to help you !!!";
        String title=" " +listItems.get(position).getTitle();


        acceptInfo.setText(Html.fromHtml(name));
        shortTitle.setText(title);

        helperPic.setImageUrl(Constants.HOME_URL+"email/pictures/"+listItems.get(position).getHelperId()+".jpg",imageLoader);

        ImageView contactPic=(ImageView)convertView.findViewById(R.id.noti_phone_logo);
        final NotiACCListItem item=listItems.get(position);


       /* helperPic.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //show profile of the helper (using helper Id)
                Intent intent=new Intent(context , ProfileView.class);
                //fill putExtra to send values to new profile View activity
                context.startActivity(intent);
            }
        });*/

        contactPic.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                NotiACCListItem item=listItems.get(position);
                Intent intent=new Intent(context, Contact.class);
                intent.putExtra("TYPE",0); //type zero means called from notification
                intent.putExtra("USERID",item.getHelperId()); //helperId is user id for contact class
                intent.putExtra("HELP_ID",item.getHelpId());
                intent.putExtra("NOTI_ID",item.getNotiId());
                intent.putExtra("HELPIE_ID",item.getCurrentUserId());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

}

