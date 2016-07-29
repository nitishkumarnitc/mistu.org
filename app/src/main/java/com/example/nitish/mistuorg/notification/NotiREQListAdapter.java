package com.example.nitish.mistuorg.notification;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
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
import com.example.nitish.mistuorg.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotiREQListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<NotiREQListItem> listItems;
    private ImageLoader imageLoader= AppController.getInstance().getImageLoader();
    NotiREQListAdapter(Context context,ArrayList<NotiREQListItem> listItems){
        this.context=context;
        this.listItems=listItems;
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
            convertView=inflater.inflate(R.layout.noti_request_list_item,null);
        }

        TextView reqInfo=(TextView)convertView.findViewById(R.id.noti_req_info);
        TextView title=(TextView)convertView.findViewById(R.id.noti_req_list_title);
        NetworkImageView helpie_pic=(NetworkImageView)convertView.findViewById(R.id.noti_req_list_item_img);

        final NotiREQListItem item=listItems.get(position);

        String name=item.getName();
        String helpInfo="Seems like you can help "+"<b>" + name + "</b>"+".";
        reqInfo.setText(Html.fromHtml(helpInfo));
        title.setText(item.getTitle());
        helpie_pic.setImageUrl(Constants.HOME_URL+"email/pictures/"+item.getHelpieId()+".jpg",imageLoader);


        ImageView viewDetail=(ImageView)convertView.findViewById(R.id.noti_req_view_img);
        if(viewDetail!=null){
            viewDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                  /*  Intent intent=new Intent(context, HRDetailView.class);
                    intent.putExtra("NAME",item.getName());
                    intent.putExtra("BRANCH_STREAM",item.getBranchStream());
                    intent.putExtra("CAT",item.getCategory());
                    intent.putExtra("TITLE",item.getTitle());
                    intent.putExtra("DES",item.getDescription());
                    intent.putExtra("TAG1",item.getTag1());
                    intent.putExtra("TAG2",item.getTag2());
                    intent.putExtra("TAG3",item.getTag3());
                    intent.putExtra("HELPID",item.getHelpId());
                    intent.putExtra("HELPIEID",item.getHelpieId());
                    intent.putExtra("ACCEPT",item.getIsAccepted());
                    intent.putExtra("NOTIID",item.getNotiId());
                    context.startActivity(intent);*/

                }
            });
        }

        return convertView;
    }

    private void sendDataToServer(NotiREQListItem item){
        JSONObject values=new JSONObject();
        String helpURL="http://mistu.org/processNotiClicks.php/";
        try {
            values.put("CODE","notiReqAccept");
            values.put("USER_ID",item.getCurrentUserId());
            values.put("HELP_ID",item.getHelpId());
            values.put("NOTI_ID",item.getNotiId());
            values.put("HELPIE_ID",item.getHelpieId());

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
