package bet.belleepoquetech.radarufpa;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * Created by AEDI on 06/04/17.
 */

public class CommentListAdpter extends RecyclerView.Adapter<CommentListAdpter.ViewHolder> {
    private Context ctx;
    private List<CommentItem> commentItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public TextView status;
        public TextView timestamp;
        public TextView name;
        public NetworkImageView profilePic;

        public ViewHolder(View itemView) {
            super(itemView);
            status = (TextView)itemView.findViewById(R.id.txtStatusMsg);
            timestamp = (TextView)itemView.findViewById(R.id.timestamp);
            name = (TextView)itemView.findViewById(R.id.name);
            profilePic = (NetworkImageView) itemView.findViewById(R.id.profilePic);
        }
    }

    public CommentListAdpter(Context ctx, List<CommentItem> lista){
        this.commentItems = lista;
        this.ctx = ctx;
    }

    @Override
    public CommentListAdpter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(CommentListAdpter.ViewHolder holder, int position) {
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.status.setText(commentItems.get(position).getTexto());
        holder.timestamp.setText(commentItems.get(position).getTimestamp());
        holder.name.setText(commentItems.get(position).getName());

        if(commentItems.get(position).getProfilePic().equals("") && commentItems.get(position).getProfilePic() == null){
            holder.profilePic.setImageResource(R.drawable.profile_pic);
        }else{
            holder.profilePic.setErrorImageResId(R.drawable.profile_pic);
            holder.profilePic.setDefaultImageResId(R.drawable.profile_pic);
            holder.profilePic.setImageUrl(commentItems.get(position).getProfilePic(),imageLoader);
        }
    }

    @Override
    public int getItemCount() {
        return commentItems.size();
    }

    public void clearData(){
        commentItems.clear();
    }

}
