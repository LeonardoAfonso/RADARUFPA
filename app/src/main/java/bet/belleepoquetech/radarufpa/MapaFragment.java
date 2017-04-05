package bet.belleepoquetech.radarufpa;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.DisplayMetrics;
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
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private SharedPreferences mSharedPreferences;
    private Dialog view;
    static final int REQUEST_TAKE_PHOTO = 1;
    private ImageView img;
    private Spinner pontoSpn;
    String mCurrentPhotoPath;
    private Uri mcurrentPhotoUri;
    private String UPLOAD_URL = "http://aedi.ufpa.br/~leonardo/radarufpa/index.php/api/publication";


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
        mSharedPreferences = getContext().getSharedPreferences(getResources().getString(R.string.SharedPreferences), Context.MODE_PRIVATE);
        Log.i("Token Mapa: ",mSharedPreferences.getString("token",null));
        GoogleMapOptions options = new GoogleMapOptions();
        options.zOrderOnTop(true);
        View root = inflater.inflate(R.layout.fragment_mapa, container, false);
        fab2 = (FloatingActionButton) root.findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) root.findViewById(R.id.fab3);
        fab4 = (FloatingActionButton) root.findViewById(R.id.fab4);
        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_foward);
        rotate_backward = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_backward);


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
        if (marker != null) {
            marker.remove();
        }
        Log.i("FAB 1", String.valueOf(fab2.getVisibility()));
        fab2.show();
        Log.i("FAB 2", String.valueOf(fab2.getVisibility()));
        customAddMarker(new LatLng(latLng.latitude, latLng.longitude), "", "");
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.i("OK", "criou foto");
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i("Erro", "Erro ao tirar foto");

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                mcurrentPhotoUri = FileProvider.getUriForFile(getContext(), "bet.belleepoquetech.radarufpa.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mcurrentPhotoUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_CANCELED) {
            //if (data != null) {
                if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
                    File file = new File(mCurrentPhotoPath);
                    galleryAddPic();
                    picDialog(Uri.fromFile(file));

                    Log.i("Foto","Retornou a foto");
                }
                else{
                    Log.i("Foto","requestcode e resultcode deu diferente");
                }
           // }else{
            //    Log.i("foto","data eh null");
            //}
        }else{
            Log.i("foto","resultCode eh igual a Result_canceled");
        }
    }

    public void customAddMarker(LatLng latlng, String title, String snippet) {
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

    public void animateFAB() {
        if (isFabOpen) {
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

    public void picDialog(final Uri imagem) {
        view = new Dialog(getContext());
        view.setContentView(R.layout.pic_dialog_layout);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int device_TotalWidth = metrics.widthPixels;
        int device_TotalHeight = metrics.heightPixels;

        view.getWindow().setLayout(device_TotalWidth*80/100, device_TotalHeight*70/100);
        view.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        img = (ImageView) view.findViewById(R.id.imageView);
        //setPic();
        img.setImageURI(imagem);

        Button btn = (Button) view.findViewById(R.id.btnCancelar);
        Button btnSalvar = (Button) view.findViewById(R.id.btnSalvar);

        pontoSpn = (Spinner) view.findViewById(R.id.pontoSpn);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.tipo_pontos_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pontoSpn.setAdapter(adapter);
        final EditText edtDesc = (EditText) view.findViewById(R.id.edtDesc);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.dismiss();
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPublication(edtDesc.getText().toString(),String.valueOf(marker.getPosition().latitude) +":"+String.valueOf(marker.getPosition().longitude));
            }
        });

        view.show();
    }

    private void doPublication(final String descricao, final String latlong) {
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, UPLOAD_URL+"?token="+mSharedPreferences.getString("token",null), new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    String status = result.getString("status");
                    String message = result.getString("message");

                    if (status.equals(1)) {
                        // tell everybody you have succed upload image and post strings
                        Log.i("Message", message);
                    } else {
                        Log.i("Unexpected", message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    Log.i("erro",result.split("</head>")[1]);
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {

            @Override
            public Map<String,String> getHeaders(){
                Map<String,String> headers = new HashMap<>();
                headers.put("Authorization:","Bearer "+mSharedPreferences.getString("token",null));
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("descricao", descricao);
                params.put("latlong", latlong);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("public_pic", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromUri(getContext(),mcurrentPhotoUri), "image/jpeg"));
                //params.put("cover", new DataPart("file_cover.jpg", AppHelper.getFileDataFromDrawable(getContext(), mCoverImage.getDrawable()), "image/jpeg"));

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(multipartRequest);
        view.dismiss();
    }


    private void setPic() {
        // Get the dimensions of the View
        int targetW = img.getWidth();
        int targetH = img.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        img.setImageBitmap(bitmap);
    }

}


