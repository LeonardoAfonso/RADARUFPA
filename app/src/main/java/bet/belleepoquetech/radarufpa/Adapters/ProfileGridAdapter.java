package bet.belleepoquetech.radarufpa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import bet.belleepoquetech.radarufpa.helpers.AppController;
import bet.belleepoquetech.radarufpa.dao.PostItem;
import bet.belleepoquetech.radarufpa.helpers.FeedImageView;
import bet.belleepoquetech.radarufpa.R;

/**
 * Created by AEDI on 24/04/17.
 */

public class ProfileGridAdapter extends BaseAdapter{
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private LayoutInflater inflater;
    private Context context;
    private List<PostItem> posts;



    public ProfileGridAdapter(List<PostItem> post, Context context){
        this.posts = post;
        this.context = context;
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int position) {
        return posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.post_item, null);
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        FeedImageView postImg = (FeedImageView) convertView.findViewById(R.id.postImg);

        postImg.setImageUrl(posts.get(position).getImgUrl(),imageLoader);

        return convertView;
    }



    public void clearData(){
        posts.clear();
    }

}

