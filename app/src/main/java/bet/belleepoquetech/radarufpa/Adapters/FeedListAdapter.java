package bet.belleepoquetech.radarufpa.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import bet.belleepoquetech.radarufpa.helpers.AppController;
import bet.belleepoquetech.radarufpa.helpers.CustomJSONObjectResquest;
import bet.belleepoquetech.radarufpa.dao.FeedItem;
import bet.belleepoquetech.radarufpa.helpers.FeedImageView;
import bet.belleepoquetech.radarufpa.R;
import bet.belleepoquetech.radarufpa.activities.CommentActivity;

/**
 * Created by AEDI on 10/04/17.
 */

public class FeedListAdapter extends RecyclerView.Adapter<FeedListAdapter.ViewHolder> {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        // each data item is just a string in this case

        public TextView status;
        public TextView timestamp;
        public TextView name;
        public NetworkImageView profilePic;
        public FeedImageView feedImageView;
        public ImageView affectBtn;
        public ImageView seenBtn;
        public ImageView unknownBtn;
        public ImageView commentBtn;
        public CardView answerCard;
        public TextView answerText;
        public ImageView answerImg;
        public FeedItem feedItem;
        public ImageView moreBtn;
        public SharedPreferences sp;
        public int react;

        public ViewHolder(View itemView) {
            super(itemView);
            sp = itemView.getContext().getSharedPreferences(itemView.getContext().getString(R.string.SharedPreferences),Context.MODE_PRIVATE);
            feedItem = new FeedItem();
            status = (TextView)itemView.findViewById(R.id.txtStatusMsg);
            timestamp = (TextView)itemView.findViewById(R.id.timestamp);
            name = (TextView)itemView.findViewById(R.id.name);
            profilePic = (NetworkImageView) itemView.findViewById(R.id.profilePic);
            feedImageView = (FeedImageView) itemView.findViewById(R.id.feedImage1);
            affectBtn = (ImageView) itemView.findViewById(R.id.reactAffected);
            seenBtn = (ImageView) itemView.findViewById(R.id.reactSeen);
            unknownBtn = (ImageView) itemView.findViewById(R.id.reactUnknown);
            commentBtn = (ImageView) itemView.findViewById(R.id.commentBtn);
            answerCard = (CardView)itemView.findViewById(R.id.cardViewAnswer);
            answerText = (TextView)itemView.findViewById(R.id.answerText);
            answerImg = (ImageView) itemView.findViewById(R.id.answerImg);
            react =0;
            moreBtn = (ImageView) itemView.findViewById(R.id.moreBtn);
            moreBtn.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Selecione ação");
            //aqui q eu tenho q por a condicao de verificar se o usuario e o post sao o mesmo.

            if(feedItem.getUser_id() == Integer.parseInt(sp.getString("id",null))){
                menu.add(0, 1, 1, "Editar"); //groupId, itemId, order, title
                menu.add(0, 2, 2, "Excluir");
            }else{
                menu.add(0, 0, 0,  "Denunciar");
            }
        }
    }


    private List<FeedItem> mDataset;
    private Context ctx;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private String LIKE_URL ="http://aedi.ufpa.br/~leonardo/radarufpa/index.php/api/like";
    private String DISLIKE_URL ="http://aedi.ufpa.br/~leonardo/radarufpa/index.php/api/dislike";
    private String IMAGE_URL = "http://aedi.ufpa.br/~leonardo/radarufpa/storage/app/";
    private SharedPreferences sp;

    public FeedListAdapter(Context ctx, List<FeedItem> myDataset) {
        this.mDataset = myDataset;
        this.ctx = ctx;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.feedItem = mDataset.get(position);

        holder.status.setText(mDataset.get(position).getStatus());
        try {
            holder.timestamp.setText(DateUtils.getRelativeTimeSpanString(relativeTime(mDataset.get(position).getTimeStamp()), System.currentTimeMillis(),DateUtils.MINUTE_IN_MILLIS));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.name.setText(mDataset.get(position).getName());

        if(mDataset.get(position).getAnswer() == 1){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.answerCard.setCardBackgroundColor(ctx.getColor(R.color.resolvido));
            }else {
                holder.answerCard.setCardBackgroundColor(ctx.getResources().getColor(R.color.resolvido));
            }

            holder.answerText.setText(ctx.getString(R.string.solved_answer));
            //holder.answerImg.setImageResource(R.drawable.icon_solved);

        }else if (mDataset.get(position).getAnswer() == 2){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.answerCard.setCardBackgroundColor(ctx.getColor(R.color.solicitado));
            }else {
                holder.answerCard.setCardBackgroundColor(ctx.getResources().getColor(R.color.solicitado));
            }

            holder.answerText.setText(ctx.getString(R.string.listed_answer));
            //holder.answerImg.setImageResource(R.drawable.icon_solved);

        }else if (mDataset.get(position).getAnswer() == 3){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.answerCard.setCardBackgroundColor(ctx.getColor(R.color.naoresolvido));
            }else {
                holder.answerCard.setCardBackgroundColor(ctx.getResources().getColor(R.color.naoresolvido));
            }

            holder.answerText.setText(ctx.getString(R.string.closed_answer));
            //holder.answerImg.setImageResource(R.drawable.icon_solved);

        }else{

        }

        if(mDataset.get(position).getProfilePic().equals("") && mDataset.get(position).getProfilePic() == null){
            holder.profilePic.setImageResource(R.drawable.profile_pic);
        }else{
            holder.profilePic.setErrorImageResId(R.drawable.profile_pic);
            holder.profilePic.setDefaultImageResId(R.drawable.profile_pic);
            holder.profilePic.setImageUrl(IMAGE_URL+mDataset.get(position).getProfilePic(),imageLoader);
        }

        if (mDataset.get(position).getImge() != null) {
            holder.feedImageView.setImageUrl(mDataset.get(position).getImge(), imageLoader);
            holder.feedImageView.setVisibility(View.VISIBLE);
            holder.feedImageView
                    .setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });
        } else {
            holder.feedImageView.setVisibility(View.GONE);
        }

        if(mDataset.get(position).isReacted()){
            if(mDataset.get(position).getTypelike() == 1){
                holder.affectBtn.setImageResource(R.drawable.icon_affected_marked);
                holder.react=1;
            }else if(mDataset.get(position).getTypelike() == 2){
                holder.seenBtn.setImageResource(R.drawable.icon_seen_marked);
                holder.react=2;
            }else if(mDataset.get(position).getTypelike() == 3){
                holder.unknownBtn.setImageResource(R.drawable.icon_unknown_marked);
                holder.react=3;

            }
        }

        holder.affectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView react = (ImageView) v;
                if(holder.react != 1){
                    Toast.makeText(ctx.getApplicationContext(),"Affected",Toast.LENGTH_LONG).show();
                    react.setImageResource(R.drawable.icon_affected_marked);
                    holder.seenBtn.setImageResource(R.drawable.icon_seen);
                    holder.unknownBtn.setImageResource(R.drawable.icon_unknown);
                    if(holder.react != 0){dislike(mDataset.get(position),holder.react);}
                    like(mDataset.get(position),1);
                    holder.react=1;
                }else {
                    Toast.makeText(ctx.getApplicationContext(),"Unmarked",Toast.LENGTH_LONG).show();
                    react.setImageResource(R.drawable.icon_affected);
                    dislike(mDataset.get(position),holder.react);
                    holder.react = 0;

                }
            }
        });


        holder.seenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView react = (ImageView) v;
                if(holder.react != 2){
                    Toast.makeText(ctx.getApplicationContext(),"seen",Toast.LENGTH_LONG).show();
                    holder.affectBtn.setImageResource(R.drawable.icon_affected);
                    react.setImageResource(R.drawable.icon_seen_marked);
                    holder.unknownBtn.setImageResource(R.drawable.icon_unknown);
                    if(holder.react != 0){dislike(mDataset.get(position),holder.react);}
                    like(mDataset.get(position),2);
                    holder.react=2;
                }else {
                    Toast.makeText(ctx.getApplicationContext(),"Unmarked",Toast.LENGTH_LONG).show();
                    react.setImageResource(R.drawable.icon_seen);
                    dislike(mDataset.get(position),holder.react);
                    holder.react = 0;

                }
            }
        });

        holder.unknownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView react = (ImageView) v;
                if(holder.react != 3){
                    Toast.makeText(ctx.getApplicationContext(),"Unknown",Toast.LENGTH_LONG).show();
                    holder.affectBtn.setImageResource(R.drawable.icon_affected);
                    holder.seenBtn.setImageResource(R.drawable.icon_seen);
                    react.setImageResource(R.drawable.icon_unknown_marked);
                    if(holder.react != 0){dislike(mDataset.get(position),holder.react);}
                    like(mDataset.get(position),3);
                    holder.react=3;
                }else {
                    Toast.makeText(ctx.getApplicationContext(),"Unmarked",Toast.LENGTH_LONG).show();
                    react.setImageResource(R.drawable.icon_unknown);
                    dislike(mDataset.get(position),holder.react);
                    holder.react = 0;

                }
            }
        });


        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ctx,CommentActivity.class);
                it.putExtra("id",mDataset.get(position).getId());
                ctx.startActivity(it);
            }
        });

    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void clearData(){
        mDataset.clear();
    }

    public void like(FeedItem item, int type){
        sp = this.ctx.getSharedPreferences(ctx.getString(R.string.SharedPreferences),Context.MODE_PRIVATE);
        Map<String,String> params = new HashMap<>();
        params.put("post_id", String.valueOf(item.getId()));
        params.put("type",String.valueOf(type));
        CustomJSONObjectResquest likeReq = new CustomJSONObjectResquest(Request.Method.POST,LIKE_URL+"?token="+sp.getString("token",null),params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.i("response",response.getString("response"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    JSONObject json = new JSONObject( new String(error.networkResponse.data) );
                    Log.i("Erro",json.getString("erro"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    String body="";
                    if(error.networkResponse.data!=null) {
                        try {
                            body = new String(error.networkResponse.data,"UTF-8");
                        } catch (UnsupportedEncodingException ex) {
                            ex.printStackTrace();
                        }
                    }
                    Log.e("Erro","Corpo \n" + body.split("</head>")[1]);
                }
            }
        });

        AppController.getInstance().addToRequestQueue(likeReq);
    }


    public void dislike(FeedItem item, int type){
        sp = this.ctx.getSharedPreferences(ctx.getString(R.string.SharedPreferences),Context.MODE_PRIVATE);
        Map<String,String> params = new HashMap<>();
        params.put("post_id", String.valueOf(item.getId()));
        params.put("type",String.valueOf(type));
        CustomJSONObjectResquest dislikeReq = new CustomJSONObjectResquest(Request.Method.POST,DISLIKE_URL+"?token="+sp.getString("token",null),params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.i("response",response.getString("response"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    JSONObject json = new JSONObject( new String(error.networkResponse.data) );
                    Log.i("Erro",json.getString("erro"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    String body="";
                    if(error.networkResponse.data!=null) {
                        try {
                            body = new String(error.networkResponse.data,"UTF-8");
                        } catch (UnsupportedEncodingException ex) {
                            ex.printStackTrace();
                        }
                    }
                    Log.e("Erro","Corpo \n" + body.split("</head>")[1]);
                }
            }
        });

        AppController.getInstance().addToRequestQueue(dislikeReq);

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


