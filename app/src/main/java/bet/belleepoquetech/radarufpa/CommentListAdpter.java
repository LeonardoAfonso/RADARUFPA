package bet.belleepoquetech.radarufpa;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by AEDI on 06/04/17.
 */

public class CommentListAdpter extends RecyclerView.Adapter<CommentListAdpter.ViewHolder>  {
    private Context ctx;
    private List<CommentItem> commentItems;
    private int position;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        // each data item is just a string in this case
        public CommentItem item;
        public TextView status;
        public TextView timestamp;
        public TextView name;
        public NetworkImageView profilePic;
        public SharedPreferences sp;

        public ViewHolder(View itemView) {
            super(itemView);
            sp = itemView.getContext().getSharedPreferences(itemView.getContext().getString(R.string.SharedPreferences),Context.MODE_PRIVATE);
            item = new CommentItem();
            status = (TextView)itemView.findViewById(R.id.txtStatusMsg);
            timestamp = (TextView)itemView.findViewById(R.id.timestamp);
            name = (TextView)itemView.findViewById(R.id.name);
            profilePic = (NetworkImageView) itemView.findViewById(R.id.profilePic);

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Selecione ação");
            //aqui q eu tenho q por a condicao de verificar se o usuario e o comment sao o mesmo.
            menu.add(0, 0, 0,  "Copiar");
            if(item.getUser_id() ==Integer.parseInt(sp.getString("id",null))){
                menu.add(0, 1, 1, "Editar"); //groupId, itemId, order, title
                menu.add(0, 2, 2, "Excluir");
            }
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
    public void onBindViewHolder(CommentListAdpter.ViewHolder holder, final int position) {
        final SharedPreferences sp = ctx.getSharedPreferences(ctx.getString(R.string.SharedPreferences),Context.MODE_PRIVATE);
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        // - get element from your dataset at this position
        // - replace the contents of the view with that element


        holder.item = commentItems.get(position);
        holder.status.setText(commentItems.get(position).getTexto());

        try {
            holder.timestamp.setText(DateUtils.getRelativeTimeSpanString(relativeTime(commentItems.get(position).getTimestamp()),System.currentTimeMillis(),DateUtils.SECOND_IN_MILLIS));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.name.setText(commentItems.get(position).getName());

        if(commentItems.get(position).getProfilePic().equals("") && commentItems.get(position).getProfilePic() == null){
            holder.profilePic.setImageResource(R.drawable.profile_pic);
        }else{
            holder.profilePic.setErrorImageResId(R.drawable.profile_pic);
            holder.profilePic.setDefaultImageResId(R.drawable.profile_pic);
            holder.profilePic.setImageUrl(commentItems.get(position).getProfilePic(),imageLoader);
        }

        holder.status.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(position);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentItems.size();
    }

    public void clearData(){
        commentItems.clear();
    }

    public long relativeTime(String timestamp) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date parsed = sdf.parse(timestamp);

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        sdf2.setTimeZone(TimeZone.getTimeZone("America/Belem"));
        long time = sdf2.parse(sdf2.format(parsed)).getTime();
        return time;
    }

}
