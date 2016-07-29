package com.example.nitish.mistuorg.search;
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
        TextView branch=(TextView)convertView.findViewById(R.id.helper_detail_branch);
        final ImageView contact=(ImageView)convertView.findViewById(R.id.helper_detail_contact);

        NetworkImageView profilePic=(NetworkImageView) convertView.findViewById(R.id.helper_detail_pic);
        SearchResultUser item=listItems.get(position);

        name.setText(item.getName());
        branch.setText(item.getBranchStream());
        profilePic.setImageUrl(Constants.getImagesUrl(item.getUserId()),imageLoader);

        if(!item.getHasUserConfirmed()){
            tick.setVisibility(View.GONE);
        }

        return convertView;
    }

}
