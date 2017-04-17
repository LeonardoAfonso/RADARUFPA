package bet.belleepoquetech.radarufpa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {
    private EditText edtComment;
    private ImageButton sendBtn;
    private RecyclerView lista;
    private LinearLayoutManager mLayoutManager;
    private List<CommentItem> commentItems;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CommentListAdpter adapter;
    private int id;
    private SharedPreferences sp;
    private String COMMENT_URL ="http://aedi.ufpa.br/~leonardo/radarufpa/index.php/api/addcomment";
    private String GETCOMMENT_URL ="http://aedi.ufpa.br/~leonardo/radarufpa/index.php/api/getcomments";
    private String IMAGE_URL = "http://aedi.ufpa.br/~leonardo/radarufpa/storage/app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent it = getIntent();
        id = it.getIntExtra("id",0);
        setContentView(R.layout.acitivity_comments);
        lista = (RecyclerView) findViewById(R.id.list_comment);
        lista.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(this);
        commentItems = new ArrayList<>();
        adapter = new CommentListAdpter(this,commentItems);
        lista.setAdapter(adapter);
        lista.setLayoutManager(mLayoutManager);
        edtComment = (EditText)findViewById(R.id.edtComment);
        sendBtn = (ImageButton)findViewById(R.id.sendBtn);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        getComments(id);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getComments(id);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeRefreshLayout.setRefreshing(true);
                addComment(id,edtComment.getText().toString());
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(edtComment.getWindowToken(), 0);
                adapter.clearData();
                getComments(id);
                edtComment.setText("");
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void getComments(int id){
        sp = getSharedPreferences(getString(R.string.SharedPreferences),Context.MODE_PRIVATE);
        Map<String,String> params = new HashMap<>();
        params.put("post_id", String.valueOf(id));
        CustomJSONObjectResquest commentReq = new CustomJSONObjectResquest(Request.Method.POST,GETCOMMENT_URL+"?token="+sp.getString("token",null),params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    adapter.clearData();
                    JSONArray json = response.getJSONArray("comments");

                    if(json.length() == 0){
                        Log.i("response","0");
                    }else{

                        Log.i("comments",json.get(0).toString());

                        for(int i=0;i<json.length();i++) {
                            JSONObject obj = (JSONObject) json.get(i);
                            Log.i("comments", "adicionando comentario " + i);

                            CommentItem item = new CommentItem();
                            item.setId(obj.getInt("id"));
                            item.setName(obj.getJSONObject("user").getString("name"));
                            item.setTexto(obj.getString("texto"));
                            item.setTimestamp("1491399067");
                            if(obj.getJSONObject("user").getJSONObject("profile_picture").isNull("profile_pic_url")){
                                item.setProfilePic("");
                            }else{
                                item.setProfilePic(IMAGE_URL+obj.getJSONObject("user").getJSONObject("profile_picture").getString("profile_pic_url"));
                            }
                            commentItems.add(item);
                        }
                    }

                    adapter.notifyDataSetChanged();
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

    public void addComment(int id, String comment){
        sp = getSharedPreferences(getString(R.string.SharedPreferences),Context.MODE_PRIVATE);
        Map<String,String> params = new HashMap<>();
        params.put("post_id", String.valueOf(id));
        params.put("texto",comment);
        CustomJSONObjectResquest commentReq = new CustomJSONObjectResquest(Request.Method.POST,COMMENT_URL+"?token="+sp.getString("token",null),params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.i("response", response.getString("response"));
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
