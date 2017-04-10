package bet.belleepoquetech.radarufpa;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by AEDI on 10/04/17.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public TextView status;
        public TextView timestamp;
        public TextView name;
        public NetworkImageView profilePic;
        public FeedImageView feedImageView;
        public ImageView likeBtn;
        public ImageView commentBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            status = (TextView)itemView.findViewById(R.id.txtStatusMsg);
            timestamp = (TextView)itemView.findViewById(R.id.timestamp);
            name = (TextView)itemView.findViewById(R.id.name);
            profilePic = (NetworkImageView) itemView.findViewById(R.id.profilePic);
            feedImageView = (FeedImageView) itemView.findViewById(R.id.feedImage1);
            likeBtn = (ImageView) itemView.findViewById(R.id.likeBtn);
            commentBtn = (ImageView) itemView.findViewById(R.id.commentBtn);
        }
    }

    private List<FeedItem> mDataset;
    private Context ctx;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private String LIKE_URL ="http://aedi.ufpa.br/~leonardo/radarufpa/index.php/api/like";
    private String DISLIKE_URL ="http://aedi.ufpa.br/~leonardo/radarufpa/index.php/api/dislike";
    private String COMMENT_URL ="http://aedi.ufpa.br/~leonardo/radarufpa/index.php/api/addcomment";
    private String GETCOMMENT_URL ="http://aedi.ufpa.br/~leonardo/radarufpa/index.php/api/getcomments";
    private SharedPreferences sp;
    private List<CommentItem> commentItems;
    private CommentListAdpter commentListAdapter;
    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context ctx, List<FeedItem> myDataset) {
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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        // - get element from your dataset at this position
        // - replace the contents of the view with that element


        holder.status.setText(mDataset.get(position).getStatus());
        holder.timestamp.setText(mDataset.get(position).getTimeStamp());
        holder.name.setText(mDataset.get(position).getName());
        holder.profilePic.setImageUrl(mDataset.get(position).getProfilePic(),imageLoader);

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

        if(mDataset.get(position).isLiked()){
            holder.likeBtn.setImageResource(R.drawable.icon_liked);
            holder.likeBtn.setTag("liked");
        }else{
            holder.likeBtn.setImageResource(R.drawable.icon_like);
            holder.likeBtn.setTag("notliked");
        }

        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView like = (ImageView) v;
                if(like.getTag()!= null && like.getTag().toString().equals("notliked")){
                    Toast.makeText(ctx.getApplicationContext(),"Liked",Toast.LENGTH_LONG).show();
                    like.setImageResource(R.drawable.icon_liked);
                    like.setTag("liked");
                    like(mDataset.get(position));
                    //notifyItemChanged(position);
                }else if(like.getTag()!= null && like.getTag().toString().equals("liked")){
                    Toast.makeText(ctx.getApplicationContext(),"Disliked",Toast.LENGTH_LONG).show();
                    like.setImageResource(R.drawable.icon_like);
                    like.setTag("notliked");
                    dislike(mDataset.get(position));
                    //notifyItemChanged(position);
                }
                //notifyDataSetChanged();
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

    public void like(FeedItem item){
        sp = this.ctx.getSharedPreferences(ctx.getString(R.string.SharedPreferences),Context.MODE_PRIVATE);
        Map<String,String> params = new HashMap<>();
        params.put("post_id", String.valueOf(item.getId()));
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


    public void dislike(FeedItem item){
        sp = this.ctx.getSharedPreferences(ctx.getString(R.string.SharedPreferences),Context.MODE_PRIVATE);
        Map<String,String> params = new HashMap<>();
        params.put("post_id", String.valueOf(item.getId()));
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

    public void addComment(FeedItem item, String comment){
        sp = this.ctx.getSharedPreferences(ctx.getString(R.string.SharedPreferences),Context.MODE_PRIVATE);
        Map<String,String> params = new HashMap<>();
        params.put("post_id", String.valueOf(item.getId()));
        params.put("texto",comment);
        CustomJSONObjectResquest commentReq = new CustomJSONObjectResquest(Request.Method.POST,COMMENT_URL+"?token="+sp.getString("token",null),params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.i("response", response.getString("message"));
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

        AppController.getInstance().addToRequestQueue(commentReq);

    }



    public void getComments(FeedItem item){
        sp = this.ctx.getSharedPreferences(ctx.getString(R.string.SharedPreferences),Context.MODE_PRIVATE);
        Map<String,String> params = new HashMap<>();
        params.put("post_id", String.valueOf(item.getId()));
        CustomJSONObjectResquest commentReq = new CustomJSONObjectResquest(Request.Method.POST,GETCOMMENT_URL+"?token="+sp.getString("token",null),params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    commentListAdapter.clearData();
                    JSONArray json = response.getJSONArray("comments");
                    Log.i("comments",json.get(0).toString());

                    for(int i=0;i<json.length();i++) {
                        JSONObject obj = (JSONObject) json.get(i);
                        Log.i("comments", "adicionando comentario " + i);

                        CommentItem item = new CommentItem();
                        item.setId(obj.getInt("id"));
                        item.setName(obj.getJSONObject("user").getString("name"));
                        item.setTexto(obj.getString("texto"));
                        item.setTimestamp("1491399067");
                        item.setProfilePic("http://api.androidhive.info/feed/img/nat.jpg");

                        commentItems.add(item);
                    }

                    commentListAdapter.notifyDataSetChanged();
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

        AppController.getInstance().addToRequestQueue(commentReq);

    }
    
    
}


