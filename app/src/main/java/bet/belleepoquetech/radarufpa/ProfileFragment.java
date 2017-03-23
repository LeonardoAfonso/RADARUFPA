package bet.belleepoquetech.radarufpa;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private SharedPreferences mSharedPreferences;
    private String urlUserData = "http://aedi.ufpa.br/~leonardo/radarufpa/index.php/api/userdata";


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

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        TextView txtNome = (TextView)rootView.findViewById(R.id.txtNome);
        TextView txtCurso = (TextView)rootView.findViewById(R.id.txtCurso);
        TextView txtNasc = (TextView)rootView.findViewById(R.id.txtNasc);

        mSharedPreferences = getContext().getSharedPreferences(getString(R.string.SharedPreferences),Context.MODE_PRIVATE);
        Log.i("token",mSharedPreferences.getString("token",null));
        final String token = mSharedPreferences.getString("token",null);

        HashMap<String,String>params = new HashMap<>();
        params.put("token",token);

        CustomJSONObjectResquest request = new CustomJSONObjectResquest(urlUserData+"?token="+token, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("UserResponse","reponse: " + response);
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
        });

        AppController.getInstance().addToRequestQueue(request);

        return rootView ;
    }
}
