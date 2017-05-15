package bet.belleepoquetech.radarufpa.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bet.belleepoquetech.radarufpa.helpers.AppController;
import bet.belleepoquetech.radarufpa.helpers.CustomJSONObjectResquest;
import bet.belleepoquetech.radarufpa.helpers.ImageConverter;
import bet.belleepoquetech.radarufpa.dao.PostItem;
import bet.belleepoquetech.radarufpa.adapters.ProfileGridAdapter;
import bet.belleepoquetech.radarufpa.R;

public class ProfileFragment extends Fragment {
    private SharedPreferences mSharedPreferences;
    private String urlUserData = "http://aedi.ufpa.br/~leonardo/radarufpa/index.php/api/userdata";
    private String urlUserPost = "http://aedi.ufpa.br/~leonardo/radarufpa/index.php/api/userpost";
    private String urlImage = "http://aedi.ufpa.br/~leonardo/radarufpa/storage/app/";
    private GridView grid;
    private TextView txtNome;
    private TextView txtTipo;
    private TextView txtNasc;
    private TextView txtEmail;
    private TextView txtPost;
    private TextView txtSolvedPost;
    private String token;
    private List<PostItem> posts;
    ProfileGridAdapter adapter;


    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mSharedPreferences = getContext().getSharedPreferences(getString(R.string.SharedPreferences),Context.MODE_PRIVATE);
        Log.i("token",mSharedPreferences.getString("token",null));
        token = mSharedPreferences.getString("token",null);

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        final NetworkImageView circleView  = (NetworkImageView)rootView.findViewById(R.id.circleView);
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.teste);
        Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 400);
        circleView.setImageBitmap(circularBitmap);
        circleView.setDefaultImageResId(R.drawable.profile_pic);
        circleView.setErrorImageResId(R.drawable.profile_pic);

        txtNome = (TextView)rootView.findViewById(R.id.txtNome);
        txtTipo = (TextView)rootView.findViewById(R.id.txtCurso);
        txtNasc = (TextView)rootView.findViewById(R.id.txtNasc);
        txtEmail = (TextView)rootView.findViewById(R.id.txtEmail);
        txtPost = (TextView)rootView.findViewById(R.id.txtPost);
        txtSolvedPost = (TextView)rootView.findViewById(R.id.txtSolvedPost);
        grid = (GridView)rootView.findViewById(R.id.gridview);
        posts = new ArrayList<>();
        getUserPosts();
        adapter = new ProfileGridAdapter(posts,getContext());
        grid.setAdapter(adapter);


        Map<String,String>params = new HashMap<>();
        //params.put("user","usurario");

        CustomJSONObjectResquest request = new CustomJSONObjectResquest(urlUserData+"?token="+token, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("UserResponse","reponse: " + response);
                SharedPreferences.Editor edt = mSharedPreferences.edit();
                try {
                    edt.putString("id",response.getString("id"));
                    edt.putString("email",response.getString("email"));
                    edt.apply();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject j = response.getJSONObject("profile_picture");
                    if(!(j.getString("profile_pic_url").equals("null") || j.getString("profile_pic_url") == null)){
                        circleView.setImageUrl("http://aedi.ufpa.br/~leonardo/radarufpa/storage/app/"+j.getString("profile_pic_url"), AppController.getInstance().getImageLoader());
                    }
                    txtNome.setText(response.getString("name"));
                    txtTipo.setText(response.getString("type"));
                    txtEmail.setText(response.getString("email"));
                    SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date birthday = myFormat.parse(response.getString("birthdate"));
                        txtNasc.setText(df.format(birthday));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String body="";
                if(error.networkResponse.data!=null) {
                    try {
                        body = new String(error.networkResponse.data,"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                Log.e("Erro","Corpo \n" + body.split("</head>")[1]);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                HashMap<String, String> header = new HashMap<String, String>();
                header.put("Authorization","Bearer "+token);
                return(header);
            }
        };

        AppController.getInstance().addToRequestQueue(request);



        return rootView ;

    }

    public void getUserPosts(){
       final CustomJSONObjectResquest req  = new CustomJSONObjectResquest(urlUserPost + "?token=" + token, null, new Response.Listener<JSONObject>() {
           @Override
           public void onResponse(JSONObject response) {
               Log.i("response", String.valueOf(response));
               try {
                   adapter.clearData();

                   JSONArray json = response.getJSONArray("posts");
                   txtPost.setText(String.valueOf(json.length()));
                   for(int i=0;i<json.length();i++){
                        PostItem post = new PostItem();
                        post.setId(json.getJSONObject(i).getInt("id"));
                        post.setUser_id(json.getJSONObject(i).getInt("user_id"));
                        post.setImgUrl(urlImage+json.getJSONObject(i).getJSONObject("picture").getString("url"));
                        posts.add(post);
                   }

                   adapter.notifyDataSetChanged();

               } catch (JSONException e) {
                   e.printStackTrace();
               }


           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
                Log.i("erro","erro");
           }
       });

        AppController.getInstance().addToRequestQueue(req);
    }

}
