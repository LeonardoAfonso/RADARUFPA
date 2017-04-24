package bet.belleepoquetech.radarufpa;

import android.app.Activity;
import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

/**
 * Created by AEDI on 24/04/17.
 */

public class ProfileGridAdapter extends BaseAdapter{
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private LayoutInflater inflater;
    private Context context;
    private List<Posts> posts;



    public ProfileGridAdapter(List<Posts> post, Context context){
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

