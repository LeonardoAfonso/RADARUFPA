package bet.belleepoquetech.radarufpa;


import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class FeedFragment extends Fragment {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    private Cache cache;
    private Cache.Entry entry;
    //private String URL_FEED = "http://api.androidhive.info/feed/feed.json";
    private String URL_FEED = "http://aedi.ufpa.br/~leonardo/radarufpa/index.php/api/feed";
    private SwipeRefreshLayout swipeRefreshLayout;

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

        listView = (ListView) rootView.findViewById(R.id.list);

        feedItems = new ArrayList<>();

        listAdapter = new FeedListAdapter(getActivity(), feedItems);
        listView.setAdapter(listAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFeed();
            }
        });



        // These two lines not needed,
        // just to get the look of facebook (changing background color & hiding the icon)
        //getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3b5998")));
        //getActionBar().setIcon(

        new ColorDrawable(ContextCompat.getColor(getContext(),android.R.color.transparent));
        getFeed();
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
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
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
            listAdapter.clearData();
            JSONArray feedArray = response.getJSONArray("feed");

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                FeedItem item = new FeedItem();
                item.setId(feedObj.getInt("id"));
                JSONObject user = feedObj.getJSONObject("user");

                item.setName(user.getString("name"));

                // Image might be null sometimes
                JSONObject picture = feedObj.getJSONObject("picture");

                String image = picture.isNull("url") ? null : picture.getString("url");
                item.setImge("http://aedi.ufpa.br/~leonardo/radarufpa/storage/app/"+image);
                item.setStatus(feedObj.getString("descricao"));
                item.setProfilePic("http://api.androidhive.info/feed/img/nat.jpg");
                item.setTimeStamp(feedObj.getString("created_at"));

                /*
                String image = feedObj.isNull("image") ? null : feedObj.getString("image");
                item.setImge(image);
                item.setStatus(feedObj.getString("status"));
                item.setProfilePic(feedObj.getString("profilePic"));
                item.setTimeStamp(feedObj.getString("timeStamp"));
                */

                feedItems.add(item);
            }

            // notify data changes to list adapater
            listAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
