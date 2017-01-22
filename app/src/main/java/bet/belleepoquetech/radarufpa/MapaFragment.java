package bet.belleepoquetech.radarufpa;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapaFragment extends Fragment implements OnMapReadyCallback ,OnMapClickListener, OnMapLongClickListener, GoogleMap.InfoWindowAdapter ,GoogleMap.OnInfoWindowClickListener,OnCameraIdleListener, View.OnClickListener{
    private SupportMapFragment mapFragment;
    //private LinearLayout superContainer;
    private Marker marker;
    private GoogleMap map;
    private TextView mTapTextView;
    private Button addButton;
    private Button seeButton;
    private Button canButton;
    private Dialog myDialog;
    private Dialog infoDialog;
    private ListView mListaMensagens;
    private ArrayAdapter<CharSequence> mAdapter;
    private Map<String, String> params;
    private RequestQueue rq;
    private String url;
    private String url2;
    //private ArrayList<Ponto> pto = new ArrayList<>();
    SharedPreferences sharedPr;

    private GoogleMap mMap;

    public static MapaFragment newInstance(){
        MapaFragment fragment = new MapaFragment();
        return fragment;
    }

    public MapaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        sharedPr = getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        GoogleMapOptions options = new GoogleMapOptions();
        options.zOrderOnTop(true);
        View root = inflater.inflate(R.layout.fragment_mapa,container,false);
        mTapTextView = (TextView) root.findViewById(R.id.tap_text);
        addButton = (Button) root.findViewById(R.id.addButton);
        seeButton = (Button) root.findViewById(R.id.seeButton);
        addButton.setOnClickListener(this);
        canButton = (Button) root.findViewById(R.id.canButton);
        canButton.setOnClickListener(this);
        mapFragment = SupportMapFragment.newInstance(options);
        mapFragment.getMapAsync(this);
        seeButton.setOnClickListener(this);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.llContainer,mapFragment);
        ft.commit();

        //url ="/http://200.239.79.211/MapadoMaroto/consultaPontos";
        //url2 = "http://200.239.79.211//MapadoMaroto/salvaPonto.php";

        //url = "http://10.0.2.2:8888/MapadoMaroto/";
        //rq = Volley.newRequestQueue(getContext());
        //pegarPontos();

        return root;
    }


    public void addButtonMtd (){
        if (marker == null) {
            Toast.makeText(getContext(), "Primeiro marque um ponto no Mapa", Toast.LENGTH_SHORT).show();
            Log.i("Script","addbutton");
        } else {
            //callDialog();
            Toast.makeText(getContext(), "Ponto Marcado Lat: " + marker.getPosition().latitude + " Lng: " + marker.getPosition().longitude, Toast.LENGTH_SHORT).show();
        }
    }

    public void canButtonMtd(){
        if(marker != null){
            marker.remove();
            marker=null;
            canButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        map = googleMap;

        map.getUiSettings().setMapToolbarEnabled(false);
        //addMarkersToMap();
        //rq.cancelAll("tag");
        //addPointToMap();

        map.setOnMapClickListener(this);
        map.setOnMapLongClickListener(this);
        map.setOnCameraIdleListener(this);
        map.setOnInfoWindowClickListener(this);
        map.setInfoWindowAdapter(this);
        map.getUiSettings().setZoomControlsEnabled(true);

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(-1.4749331,-48.4555419),15);
        map.moveCamera(update);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.animateCamera(update, 5000, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {

            }

            @Override
            public void onCancel() {

            }
        });


    }

    public boolean exist(){
        if(sharedPr.contains("last_id")){
            return false;
        }else{
            return true;}
    }

    public void customAddMarker(LatLng latlng, String title, String snippet){
        MarkerOptions options = new MarkerOptions();
        options.position(latlng).title(title).snippet(snippet);
        marker = map.addMarker(options);
    }

    /*
    public void customAddStaticMarker(LatLng latlng, String title, String snippet,String estado){
        MarkerOptions options = new MarkerOptions();
        options.position(latlng).title(title).snippet(snippet);

        switch(title) {
            case "Bebedouro":
                switch(estado){
                    case "Ruim": options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.fountainicon_bad));break;
                    case "Medio": options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.fountainicon_warning));break;
                    case "Bom": options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.fountainicon_good));break;
                };break;
            case "Banheiro Masculino":
                switch(estado){
                    case "Ruim": options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.wcicon_bad_masc));break;
                    case "Medio": options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.wcicon_warning_masc));break;
                    case "Bom": options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.wcicon_good_masc));break;
                };break;
            case "Banheiro Feminino":
                switch(estado){
                    case "Ruim": options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.wcicon_bad));break;
                    case "Medio": options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.wcicon_warning));break;
                    case "Bom": options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.wcicon_good));break;
                };break;
            case "Ponto de Acessibilidade":
                switch(estado){
                    case "Ruim": options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.accessibilityicon_bad));break;
                    case "Medio": options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.accessibilityicon_warning));break;
                    case "Bom": options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.accessibilityicon_good));break;
                };break;
            default:
        }
        map.addMarker(options);
    }*/


    @Override
    public void onCameraIdle() {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        mTapTextView.setText("tapped, point=" + latLng.latitude+" "+latLng.longitude);
        Log.i("Script","tapped, point=" + latLng);
        if(marker !=null){
            marker.remove();
        }
        customAddMarker(new LatLng(latLng.latitude,latLng.longitude),"", "");
        canButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.addButton:addButtonMtd();break;
            case R.id.canButton:canButtonMtd();break;
            case R.id.seeButton:break;
        }
    }

    /*
    public void callDialog()
    {
        myDialog = new Dialog(getContext());
        myDialog.setContentView(R.layout.dialog_form_layout);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btnCancelar = (Button) myDialog.findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                marker.remove();
                marker = null;
                canButton.setVisibility(View.GONE);
            }
        });

        Button btnSalvar = (Button) myDialog.findViewById(R.id.btnSalvar);
        final Spinner pontoSpn = (Spinner) myDialog.findViewById(R.id.pontoSpn);
        ArrayAdapter <CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.tipo_pontos_array,android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pontoSpn.setAdapter(adapter);

        final Spinner situacaoSpn = (Spinner) myDialog.findViewById(R.id.situacaoSpn);
        ArrayAdapter <CharSequence> adapterSit = ArrayAdapter.createFromResource(getContext(),R.array.estado_pontos_array,android.R.layout.simple_spinner_dropdown_item);
        adapterSit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        situacaoSpn.setAdapter(adapterSit);

        final EditText edtDesc = (EditText) myDialog.findViewById(R.id.edtDesc);
        //final EditText  edtTipo = (EditText) myDialog.findViewById(R.id.edtTipo);

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String snpt = "Estado: "+situacaoSpn.getSelectedItem().toString()+"\nDescrição: "+edtDesc.getText().toString();
                //Toast.makeText(getContext(),"Irá pegar as informações e salva-las no servidor",Toast.LENGTH_SHORT).show();
                customAddStaticMarker(new LatLng(marker.getPosition().latitude,marker.getPosition().longitude),pontoSpn.getSelectedItem().toString(),snpt,situacaoSpn.getSelectedItem().toString());
                salvarPontos(new LatLng(marker.getPosition().latitude,marker.getPosition().longitude),snpt,pontoSpn.getSelectedItem().toString(),situacaoSpn.getSelectedItem().toString(),"TESTE");
                myDialog.dismiss();
                marker.remove();
                marker = null;
                canButton.setVisibility(View.GONE);
            }
        });

        myDialog.setCancelable(true);
        myDialog.show();
    }*/


    @Override
    public void onInfoWindowClick(Marker marker) {/*
        infoDialog = new Dialog(getContext());
        infoDialog.setContentView(R.layout.dialog_info_layout);
        infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mListaMensagens = (ListView) infoDialog.findViewById(R.id.listView2);
        mAdapter = ArrayAdapter.createFromResource(getContext(),R.array.tipo_pontos_array,android.R.layout.simple_spinner_dropdown_item);

        mListaMensagens.setAdapter(mAdapter);
        infoDialog.setCancelable(true);
        infoDialog.show();*/
    }



    @Override
    public View getInfoWindow(Marker marker) {
        TextView tv = new TextView(getContext());
        tv.setText(marker.getSnippet());
        tv.setBackgroundColor(Color.WHITE);
        return tv;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    /*
    public void pegarPontos(){
        final SharedPreferences.Editor edt = sharedPr.edit();

        if (exist()){

            edt.putString("last_id","0");
            edt.commit();

        }


        params = new HashMap<String, String>();
        params.put("last_id",sharedPr.getString("last_id",null));
        CustomJSONArrayRequest request = new CustomJSONArrayRequest(Request.Method.GET,
                url,
                params,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject json;
                        Toast.makeText(getContext(), "Response:Sucesso", Toast.LENGTH_LONG).show();
                        try {
                            json = response.getJSONObject(0);
                            if(json.has("sit")) {
                                String sit = json.getString("sit");
                                Toast.makeText(getContext(), "Response: " + sit, Toast.LENGTH_LONG).show();
                            }else{
                                Log.i("JSON","Entrou na iteracao");
                                for (int i=0;i<response.length();i++) {
                                    json = response.getJSONObject(i);
                                    Ponto ponto = new Ponto();
                                    ponto.setId(json.getInt("idPonto"));
                                    ponto.setLatLng(new LatLng(json.getDouble("lat"), json.getDouble("lng")));
                                    ponto.setDescricao(json.getString("descricaoPonto"));
                                    ponto.setEstado(json.getString("estadoPonto"));
                                    ponto.setTipo(json.getString("tipoPonto"));
                                    ponto.setUsercriacao(json.getString("userCriacao"));
                                    //ponto.setDatacriacao(json.getString("dataCriacao"));
                                    pto.add(i, ponto);

                                }
                                edt.putString("last_id", String.valueOf(json.getInt("idPonto")));
                                edt.commit();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Error: "+error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        request.setTag("tag");
        rq.add(request);
    }*/

    /*
    private void addPointToMap(){
        for(int i=0;i<pto.size();i++) {
            //MarkerOptions mo = new MarkerOptions();
            //mo.position(pto.get(i).getLatLng()).title(pto.get(i).getTipo()).snippet(pto.get(i).getDescricao() + " " + pto.get(i).getEstado());
            //Log.i("Points: ",pto.get(i).getLatLng().toString()+" "+pto.get(i).getTipo()+" "+pto.get(i).getDescricao());
            //map.addMarker(mo);
            customAddStaticMarker(pto.get(i).getLatLng(),pto.get(i).getTipo(),pto.get(i).getDescricao() + " " + pto.get(i).getEstado(),pto.get(i).getEstado());
        }
    }*/

    /*
    public void salvarPontos(LatLng latLng, String descricao, String tipo, String estado, String user){
        params = new HashMap<>();
        params.put("userCriacao",user);
        params.put("lat",String.valueOf(latLng.latitude));
        params.put("lng",String.valueOf(latLng.longitude));
        params.put("descricaoPonto",descricao);
        params.put("tipoPonto", tipo);
        params.put("estadoPonto",estado);
        CustomJSONArrayRequest request = new CustomJSONArrayRequest(Request.Method.POST,
                url2,
                params,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        Toast.makeText(getContext(), "Response:"+response, Toast.LENGTH_LONG).show();
                        JSONObject json;
                        try{
                            json = response.getJSONObject(0);
                            if(json.has("sit")) {
                                String sit = json.getString("sit");
                                Toast.makeText(getContext(), "Response: " + sit, Toast.LENGTH_LONG).show();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Error: "+error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        request.setTag("tag");
        rq.add(request);
    }*/



}