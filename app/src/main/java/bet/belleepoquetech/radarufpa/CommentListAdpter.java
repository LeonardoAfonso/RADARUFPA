package bet.belleepoquetech.radarufpa;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * Created by AEDI on 06/04/17.
 */

public class CommentListAdpter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<CommentItem> commentItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();


    public CommentListAdpter(Activity activity, List<CommentItem> commentItems) {
        this.activity = activity;
        this.commentItems = commentItems;
    }

    @Override
    public int getCount() {
        return commentItems.size();
    }

    @Override
    public Object getItem(int position) {
        return commentItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.comment_item, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView timestamp = (TextView) convertView
                .findViewById(R.id.timestamp);
        TextView statusMsg = (TextView) convertView
                .findViewById(R.id.txtStatusMsg);
        NetworkImageView profilePic = (NetworkImageView) convertView
                .findViewById(R.id.profilePic);

        final CommentItem item = commentItems.get(position);

        name.setText(item.getName());

        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(item.getTimestamp()),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        timestamp.setText(timeAgo);

        // Chcek for empty status message
         if (!TextUtils.isEmpty(item.getTexto())) {
            statusMsg.setText(item.getTexto());
            statusMsg.setVisibility(View.VISIBLE);
        } else {
            statusMsg.setVisibility(View.GONE);
        }

        profilePic.setImageUrl(item.getProfilePic(), imageLoader);

        return convertView;
    }

    public void clearData(){
        commentItems.clear();
    }
}
