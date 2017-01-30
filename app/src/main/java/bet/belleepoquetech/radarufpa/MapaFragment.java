package bet.belleepoquetech.radarufpa;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.FragmentTransaction;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MapaFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private SupportMapFragment mapFragment;
    private Marker marker;
    private GoogleMap map;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private FloatingActionButton fab4;
    private Boolean isFabOpen = false;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath;


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
        fab2 = (FloatingActionButton) root.findViewById(R.id.fab2);
        fab3 = (FloatingActionButton)root.findViewById(R.id.fab3);
        fab4 = (FloatingActionButton)root.findViewById(R.id.fab4);
        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_foward);
        rotate_backward = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_backward);

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marker.remove();
                fab3.startAnimation(fab_close);
                fab4.startAnimation(fab_close);
                //fab.startAnimation(fab_close);
                fab2.hide();
                isFabOpen = false;
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
        Log.i("FAB 1", String.valueOf(fab2.getVisibility()));
        fab2.show();
        Log.i("FAB 2", String.valueOf(fab2.getVisibility()));
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
            picDialog(imageBitmap);
        }
    }

    public void customAddMarker(LatLng latlng, String title, String snippet){
        MarkerOptions options = new MarkerOptions();
        options.position(latlng).title(title).snippet(snippet);
        marker = map.addMarker(options);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void animateFAB(){
        if(isFabOpen){
            fab2.startAnimation(rotate_backward);
            fab3.startAnimation(fab_close);
            fab4.startAnimation(fab_close);
            fab3.setClickable(false);
            fab4.setClickable(false);
            isFabOpen = false;
        } else {
            fab2.startAnimation(rotate_forward);
            fab3.startAnimation(fab_open);
            fab4.startAnimation(fab_open);
            fab3.setClickable(true);
            fab4.setClickable(true);
            isFabOpen = true;
        }
    }

    public void picDialog(Bitmap imagem){
        final Dialog view = new Dialog(getContext());
        view.setContentView(R.layout.pic_dialog_layout);
        ImageView img = (ImageView)view.findViewById(R.id.imageView);
        img.setImageBitmap(imagem);
        Button btn = (Button) view.findViewById(R.id.button3);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.dismiss();
            }
        });
        view.show();
    }


}
