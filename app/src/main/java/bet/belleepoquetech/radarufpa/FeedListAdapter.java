package bet.belleepoquetech.radarufpa;

/**
 * Created by AEDI on 17/02/17.
 */

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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

public class FeedListAdapter extends BaseAdapter {
    private String LIKE_URL ="http://aedi.ufpa.br/~leonardo/radarufpa/index.php/api/like";
    private String DISLIKE_URL ="http://aedi.ufpa.br/~leonardo/radarufpa/index.php/api/dislike";
    private String COMMENT_URL ="http://aedi.ufpa.br/~leonardo/radarufpa/index.php/api/addcomment";
    private String GETCOMMENT_URL ="http://aedi.ufpa.br/~leonardo/radarufpa/index.php/api/getcomments";
    private SharedPreferences sp;
    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;
    private List<CommentItem> commentItems;
    private CommentListAdpter commentListAdapter;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public FeedListAdapter(Activity activity, List<FeedItem> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
    }


    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int location) {
        return feedItems.get(location);
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
            convertView = inflater.inflate(R.layout.feed_item, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView timestamp = (TextView) convertView
                .findViewById(R.id.timestamp);
        TextView statusMsg = (TextView) convertView
                .findViewById(R.id.txtStatusMsg);
        TextView url = (TextView) convertView.findViewById(R.id.txtUrl);
        NetworkImageView profilePic = (NetworkImageView) convertView
                .findViewById(R.id.profilePic);
        FeedImageView feedImageView = (FeedImageView) convertView
                .findViewById(R.id.feedImage1);

        final FeedItem item = feedItems.get(position);

        name.setText(item.getName());

        // Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(item.getTimeStamp()),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        timestamp.setText(timeAgo);

        // Chcek for empty status message
        if (!TextUtils.isEmpty(item.getStatus())) {
            statusMsg.setText(item.getStatus());
            statusMsg.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            statusMsg.setVisibility(View.GONE);
        }

        // user profile pic
        profilePic.setImageUrl(item.getProfilePic(), imageLoader);

        // Feed image
        if (item.getImge() != null) {
            feedImageView.setImageUrl(item.getImge(), imageLoader);
            feedImageView.setVisibility(View.VISIBLE);
            feedImageView
                    .setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });
        } else {
            feedImageView.setVisibility(View.GONE);
        }

        final ImageView like;
        like = (ImageView) convertView.findViewById(R.id.likeBtn);

        if(item.isLiked()){
            like.setImageResource(R.drawable.icon_liked);
            like.setTag("liked");
        }else{
            like.setImageResource(R.drawable.icon_like);
            like.setTag("notliked");
        }


        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView like = (ImageView) v;
                if(like.getTag()!= null && like.getTag().toString().equals("notliked")){
                    Toast.makeText(activity.getApplicationContext(),"Liked",Toast.LENGTH_LONG).show();
                    like.setImageResource(R.drawable.icon_liked);
                    like.setTag("liked");
                    like(item);
                    //notifyDataSetChanged();
                }else if(like.getTag()!= null && like.getTag().toString().equals("liked")){
                    Toast.makeText(activity.getApplicationContext(),"Disliked",Toast.LENGTH_LONG).show();
                    like.setImageResource(R.drawable.icon_like);
                    like.setTag("notliked");
                    dislike(item);
                    //notifyDataSetChanged();
                }
                //notifyDataSetChanged();
            }
        });
        final ImageView comment;
        comment = (ImageView)convertView.findViewById(R.id.commentBtn);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog commentDialog = new Dialog(activity);
                commentDialog.setContentView(R.layout.comments_dialog_layout);
                commentDialog.setCanceledOnTouchOutside(true);
                commentDialog.setCancelable(true);
                ImageView img = (ImageView) commentDialog.findViewById(R.id.imgComment);
                ImageButton btn = (ImageButton) commentDialog.findViewById(R.id.sendBtn);
                final EditText edt = (EditText)commentDialog.findViewById(R.id.edtComment);

                ListView list = (ListView) commentDialog.findViewById(R.id.list_comment);
                commentItems = new ArrayList<>();
                commentListAdapter = new CommentListAdpter(activity,commentItems);
                list.setAdapter(commentListAdapter);
                getComments(item);
                commentListAdapter.notifyDataSetChanged();

                if(item.isLiked()){
                    img.setImageResource(R.drawable.icon_liked);
                }else{
                    img.setImageResource(R.drawable.icon_like);
                }

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String comment = edt.getText().toString();
                        addComment(item,comment);
                        edt.setText("");
                    }
                });


                commentDialog.show();
            }
        });

        return convertView;
    }

    public void clearData(){
        feedItems.clear();
    }

    public void like(FeedItem item){
        sp = this.activity.getSharedPreferences(activity.getString(R.string.SharedPreferences),Context.MODE_PRIVATE);
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
        sp = this.activity.getSharedPreferences(activity.getString(R.string.SharedPreferences),Context.MODE_PRIVATE);
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
        sp = this.activity.getSharedPreferences(activity.getString(R.string.SharedPreferences),Context.MODE_PRIVATE);
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
        sp = this.activity.getSharedPreferences(activity.getString(R.string.SharedPreferences),Context.MODE_PRIVATE);
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
