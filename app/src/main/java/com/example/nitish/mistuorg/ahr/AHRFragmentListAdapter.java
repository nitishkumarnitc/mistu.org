package com.example.nitish.mistuorg.ahr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.nitish.mistuorg.R;
import com.example.nitish.mistuorg.app.AppController;
import com.example.nitish.mistuorg.utils.Constants;
import java.util.ArrayList;

public class AHRFragmentListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<AHRFragmentListItem> fragListItems;
    private AHRFragmentListItem item;
    private int currentUserId;
    private Button acceptButton;
    private ImageLoader imageLoader= AppController.getInstance().getImageLoader();

    public AHRFragmentListAdapter(Context context, ArrayList<AHRFragmentListItem> fragListItems) {
        this.context = context;
        this.fragListItems = fragListItems;
        this.currentUserId= Constants.getCurrentUserID(context);
    }


    @Override
    public int getCount() {
        return fragListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return fragListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.ahr_fragment_list_item, null);
        }

        View layout=convertView.findViewById(R.id.ahr_frag_list_rel_lay);
        NetworkImageView helpie_pic=(NetworkImageView)convertView.findViewById(R.id.hr_pic);
        TextView hrName=(TextView)convertView.findViewById(R.id.hr_name);
        TextView hrStreamBranch=(TextView)convertView.findViewById(R.id.hr_stream_branch);
        TextView hrTitle=(TextView)convertView.findViewById(R.id.hr_title);


        hrName.setText(fragListItems.get(position).getName());
        hrStreamBranch.setText(fragListItems.get(position).getStreamBranch());
        hrTitle.setText(fragListItems.get(position).getHrTitle());
        helpie_pic.setImageUrl(Constants.getImagesUrl(fragListItems.get(position).getHelpieId()),imageLoader);


        ImageView viewReq=(ImageView)convertView.findViewById(R.id.ahr_view_image);
        viewReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,HRDetailView.class);

                item= fragListItems.get(position);

                intent.putExtra("NAME",item.getName());
                intent.putExtra("BRANCH_STREAM",item.getStreamBranch());
                intent.putExtra("CAT",item.getCategory());
                intent.putExtra("TITLE",item.getHrTitle());
                intent.putExtra("DES",item.getHrDetails());
                intent.putExtra("HELPID",item.getHelpReqId());
                intent.putExtra("HELPIEID",item.getHelpieId());
                intent.putExtra("TAG1",item.getTag1());
                intent.putExtra("TAG2",item.getTag2());
                intent.putExtra("TAG3",item.getTag3());

                context.startActivity(intent);
            }
        });
        return convertView;
    }

}

