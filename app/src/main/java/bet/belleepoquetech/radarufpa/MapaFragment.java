package bet.belleepoquetech.radarufpa;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.FragmentTransaction;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapaFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private SupportMapFragment mapFragment;
    private Marker marker;
    private GoogleMap map;
    private FloatingActionButton fab;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    public static MapaFragment newInstance() {
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
        GoogleMapOptions options = new GoogleMapOptions();
        options.zOrderOnTop(true);
        View root = inflater.inflate(R.layout.fragment_mapa, container, false);
        fab = (FloatingActionButton) root.findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        mapFragment = SupportMapFragment.newInstance(options);
        mapFragment.getMapAsync(this);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.llContainer, mapFragment);
        ft.commit();
        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMapToolbarEnabled(false);
        //rq.cancelAll("tag");
        //map.getUiSettings().setZoomControlsEnabled(true);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(-1.4749331, -48.4555419), 16);
        map.moveCamera(update);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setOnMapClickListener(this);
        map.animateCamera(update, 5000, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
            }
            @Override
            public void onCancel() {
            }
        });
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(marker !=null){
            marker.remove();
        }
        fab.setVisibility(View.VISIBLE);
        customAddMarker(new LatLng(latLng.latitude,latLng.longitude),"", "");
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //mImageView.setImageBitmap(imageBitmap);
        }
    }

    public void customAddMarker(LatLng latlng, String title, String snippet){
        MarkerOptions options = new MarkerOptions();
        options.position(latlng).title(title).snippet(snippet);
        marker = map.addMarker(options);
    }


}
