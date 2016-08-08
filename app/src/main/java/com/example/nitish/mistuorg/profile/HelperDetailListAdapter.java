package com.example.nitish.mistuorg.profile;


import android.app.Activity;
import android.content.Context;
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
import java.util.ArrayList;

public class HelperDetailListAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<HelperDetailListItem> listItems=new ArrayList<>();
    private ImageLoader imageLoader= AppController.getInstance().getImageLoader();

    public HelperDetailListAdapter(ArrayList<HelperDetailListItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
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
        if(convertView==null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.helper_details_list_item, null);
        }

        TextView name=(TextView)convertView.findViewById(R.id.helper_detail_name);
        final ImageView contact=(ImageView)convertView.findViewById(R.id.helper_detail_contact);
        //tick is used in search activity, here we are not using it. hence it is hidden
        ImageView tick=(ImageView)convertView.findViewById(R.id.user_confirmed);
        tick.setVisibility(View.GONE);

        HelperDetailListItem item=listItems.get(position);
        name.setText(item.getName());

        NetworkImageView helperImageView=(NetworkImageView) convertView.findViewById(R.id.helper_detail_pic);
        helperImageView.setImageUrl(Constants.getImagesUrl(item.getHelperId()),imageLoader);

        return convertView;
    }
}
