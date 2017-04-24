package bet.belleepoquetech.radarufpa;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class FeedFragment extends Fragment {
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private FeedListAdapter adapter;
    private List<FeedItem> feedItems;
    private Cache cache;
    private Cache.Entry entry;
    private String URL_FEED = "http://aedi.ufpa.br/~leonardo/radarufpa/index.php/api/feed";
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager mLayoutManager;
    private SharedPreferences sp;

    public FeedFragment() {
        // Required empty public constructor
    }


    public static FeedFragment newInstance() {
        FeedFragment fragment = new FeedFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFeed();
            }
        });

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerList);
        recyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getContext());
        feedItems = new ArrayList<>();
        getFeed();
        adapter = new FeedListAdapter(getContext(),feedItems);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(mLayoutManager);

        new ColorDrawable(ContextCompat.getColor(getContext(),android.R.color.transparent));
        return rootView;
    }

    private void getFeed(){
        // We first check for cached request
        cache = AppController.getInstance().getRequestQueue().getCache();
        entry = cache.get(URL_FEED);
        if (entry != null) {
            // fetch the data from cache
            try {
                String data = new String(entry.data, "UTF-8");
                try {
                    parseJsonFeed(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            // making fresh volley request and getting json
            CustomJSONObjectResquest jsonReq = new CustomJSONObjectResquest(Request.Method.GET,
                    URL_FEED, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, "Response: " + response);
                    if (response != null) {
                        parseJsonFeed(response);
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                   Log.i(TAG, "Error: " + new String(error.networkResponse.data));
                }
            });

            // Adding request to volley request queue
            AppController.getInstance().addToRequestQueue(jsonReq);
        }
        swipeRefreshLayout.setRefreshing(false);
    }


    private void parseJsonFeed(JSONObject response) {
        try {
            adapter.clearData();

            JSONArray feedArray = response.getJSONArray("feed");

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                FeedItem item = new FeedItem();

                item.setId(feedObj.getInt("id"));

                JSONObject user = feedObj.getJSONObject("user");
                JSONArray like = feedObj.getJSONArray("like");

                sp = getContext().getSharedPreferences(getString(R.string.SharedPreferences), Context.MODE_PRIVATE);

                item.setLiked(hasLiked(like,sp.getString("id",null),feedObj.getInt("id")));

                item.setName(user.getString("name"));

                // Image might be null sometimes
                JSONObject picture = feedObj.getJSONObject("picture");

                String image = picture.isNull("url") ? null : picture.getString("url");
                item.setImge("http://aedi.ufpa.br/~leonardo/radarufpa/storage/app/"+image);
                item.setStatus(feedObj.getString("descricao"));
                //item.setAnswer(feedObj.getInt("answer"));

                if(user.getJSONObject("profile_picture").isNull("profile_pic_url")){
                    item.setProfilePic("");
                }else {
                    item.setProfilePic(user.getJSONObject("profile_picture").getString("profile_pic_url"));
                }
                item.setTimeStamp(feedObj.getString("created_at"));


                feedItems.add(item);
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean hasLiked(JSONArray jsonArray, String user_id, int post_id) throws JSONException {
        Boolean v = false;
        if(jsonArray.length() == 0){
            Log.i("curtida","array eh vazio");
            return false;
       }else{
           for(int i=0;i<jsonArray.length();i++){
               JSONObject json = (JSONObject) jsonArray.get(i);
               if(String.valueOf(json.getInt("user_id")).equals(user_id) && json.getInt("post_id") == post_id ){
                   Log.i("curtida","post:"+post_id+" tem curtida");
                   v = true;
               }
           }
           return v;
       }
    }

}
