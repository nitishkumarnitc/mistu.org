package com.example.nitish.mistuorg.profile;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.nitish.mistuorg.R;
import com.example.nitish.mistuorg.app.AppController;
import com.example.nitish.mistuorg.utils.Constants;
import java.util.ArrayList;


public class AcceptsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ProfileListItem> listItems=new ArrayList<>();
    public AcceptsAdapter(Context context, ArrayList<ProfileListItem> listItems) {
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
            LayoutInflater mInflater=(LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView=mInflater.inflate(R.layout.profile_accepts_list_item,null);
        }
        ProfileListItem item=listItems.get(position);
        TextView category=(TextView)convertView.findViewById(R.id.profile_accepts_item_category);
        TextView title=(TextView)convertView.findViewById(R.id.profile_accepts_item_title);
        TextView name=(TextView)convertView.findViewById(R.id.profile_accepts_item_helpie_name);

        category.setText(item.getCategory());
        title.setText(item.getTitle());
        name.setText(item.getName());


        ImageLoader imageLoader= AppController.getInstance().getImageLoader();
        NetworkImageView helpie_pic=(NetworkImageView) convertView.findViewById(R.id.profile_accepts_item_pic);
        helpie_pic.setImageUrl(Constants.getImagesUrl(item.getUserId()),imageLoader);

        return convertView;
    }
}
