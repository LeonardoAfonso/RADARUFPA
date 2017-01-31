package bet.belleepoquetech.radarufpa;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.FragmentTransaction;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

public class MapaFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private SupportMapFragment mapFragment;
    private Marker marker;
    private GoogleMap map;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private FloatingActionButton fab4;
    private Boolean isFabOpen = false;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;
    private String UPLOAD_URL ="http://simplifiedcoding.16mb.com/VolleyUpload/upload.php";
    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";


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
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),"bet.belleepoquetech.radarufpa.fileprovider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_CANCELED) {
            if(data != null) {
                if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
                    File file = new File(mCurrentPhotoPath);
                    picDialog(Uri.fromFile(file));
                }
            }
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
        String imageFileName = "RADAR_PIC" + timeStamp + "_";
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

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
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

    public void picDialog(Uri imagem){
        final Dialog view = new Dialog(getContext());
        view.setContentView(R.layout.pic_dialog_layout);
        ImageView img = (ImageView)view.findViewById(R.id.imageView);
        view.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        img.setImageURI(imagem);
        Button btn = (Button) view.findViewById(R.id.btnCancelar);
        Button btnSalvar = (Button) view.findViewById(R.id.btnSalvar);
        final Spinner pontoSpn = (Spinner) view.findViewById(R.id.pontoSpn);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.tipo_pontos_array,android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pontoSpn.setAdapter(adapter);

        final Spinner situacaoSpn = (Spinner) view.findViewById(R.id.situacaoSpn);
        ArrayAdapter <CharSequence> adapterSit = ArrayAdapter.createFromResource(getContext(),R.array.estado_pontos_array,android.R.layout.simple_spinner_dropdown_item);
        adapterSit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        situacaoSpn.setAdapter(adapterSit);
        final EditText edtDesc = (EditText) view.findViewById(R.id.edtDesc);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.dismiss();
            }
        });
        view.show();
    }

    private void uploadImage(){
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(getContext(),"Uploading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(getContext(), s , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(getContext(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                //String image = getStringImage(bitmap);

                //Getting Image Name
                //String name = editTextName.getText().toString().trim();

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                //params.put(KEY_IMAGE, image);
                //params.put(KEY_NAME, name);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueueSingleton requestQueue = RequestQueueSingleton.getInstance(getContext());

        //Adding request to the queue
        requestQueue.addToRequestQueue(stringRequest);
    }



    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


}
