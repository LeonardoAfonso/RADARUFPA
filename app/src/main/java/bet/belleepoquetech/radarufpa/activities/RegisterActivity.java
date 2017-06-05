package bet.belleepoquetech.radarufpa.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bet.belleepoquetech.radarufpa.helpers.AppController;
import bet.belleepoquetech.radarufpa.helpers.AppHelper;
import bet.belleepoquetech.radarufpa.helpers.CustomJSONObjectResquest;
import bet.belleepoquetech.radarufpa.R;
import bet.belleepoquetech.radarufpa.helpers.VolleyMultipartRequest;

public class RegisterActivity extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener {
    private View progressBar;
    private View registerView;
    private ImageView profile_pic;
    private ImageView camera;
    private Button btnRegister;
    private  EditText edtNome;
    private  EditText edtEmail;
    private  EditText edtSenha;
    private  EditText edtNasc;
    private Spinner spinner;
    private Button cancelBtn;
    private String name;
    private String pass;
    private String email;
    private String newBirthday;
    private Date birthday;
    private String spn;
    private SimpleDateFormat df;
    private SimpleDateFormat myFormat;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;
    private Uri mcurrentPhotoUri;
    File photoFile = null;
    private boolean cancel = false;
    private String urlRegister = "http://aedi.ufpa.br/~leonardo/radarufpa/index.php/api/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        edtNome = (EditText)findViewById(R.id.name);
        edtEmail = (EditText)findViewById(R.id.email);
        edtSenha = (EditText)findViewById(R.id.password);
        edtNasc = (EditText)findViewById(R.id.birth);
        spinner = (Spinner) findViewById(R.id.spinner);
        btnRegister = (Button)findViewById(R.id.registerBtn);
        cancelBtn = (Button)findViewById(R.id.cancelBtn);
        registerView = findViewById(R.id.register_form);
        progressBar = findViewById(R.id.register_progress);
        profile_pic = (ImageView) findViewById(R.id.profile_pic);
        camera = (ImageView)findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.tipo_usuario_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        edtNasc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

    }

    private String md5(String in) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(in.getBytes());
            byte[] a = digest.digest();
            int len = a.length;
            StringBuilder sb = new StringBuilder(len << 1);
            for (int i = 0; i < len; i++) {
                sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(a[i] & 0x0f, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
        return null;
    }


    private void attemptRegister(){
                edtNome.setError(null);
                edtEmail.setError(null);
                edtSenha.setError(null);
                edtNasc.setError(null);

                name = edtNome.getText().toString();
                email = edtEmail.getText().toString();
                pass = md5(edtSenha.getText().toString());

                myFormat = new SimpleDateFormat("dd/MM/yyyy");
                df = new SimpleDateFormat("yyyy-MM-dd");

                try {
                    if (TextUtils.isEmpty(edtNasc.getText().toString())) {
                        edtNasc.setError(getString(R.string.error_field_required));
                        cancel = true;
                    }else{
                        birthday = myFormat.parse(edtNasc.getText().toString());
                        newBirthday = df.format(birthday);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                spn = spinner.getSelectedItem().toString();

                if (!TextUtils.isEmpty(pass) && !isPasswordValid(pass)) {
                    edtSenha.setError(getString(R.string.error_invalid_password));
                    cancel = true;
                }

                if (TextUtils.isEmpty(email)) {
                    edtEmail.setError(getString(R.string.error_field_required));
                    cancel = true;
                } else if (!isEmailValid(email)) {
                    edtEmail.setError(getString(R.string.error_invalid_email));
                    cancel = true;
                }else if (TextUtils.isEmpty(name)) {
                    edtNome.setError(getString(R.string.error_field_required));
                    cancel = true;
                } else if (!isNameValid(name)) {
                    edtNome.setError(getString(R.string.error_invalid_name));
                    cancel = true;
                }

                if(!isDateValid(edtNasc.getText().toString())){
                    edtNasc.setError(getString(R.string.error_invalid_date));
                    cancel = true;
                }


                AlertDialog.Builder alert = new AlertDialog.Builder(RegisterActivity.this);
                alert.setCancelable(true)
                        .setTitle("Deseja se cadastrar no RadarUFPA?")
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(!cancel){
                                    showProgress(true);
                                    if(photoFile == null){
                                        registerWithouPic();
                                        Log.i("register","sem foto");
                                    }else{
                                        registerWithPic();
                                        Log.i("register","com foto");
                                    }
                                }
                            }
                })
                        .create()
                        .show();

    }

    public String getJsonError(byte bytes[]) throws JSONException {
        JSONObject json = new JSONObject(new String(bytes));
        String msg = "";

        if(json.has("name")){
            msg+= json.getString("name")+"\n";
        }
        if(json.has("email")){
            msg+= json.getString("email")+"\n";
        }
        if(json.has("type")){
            msg+= json.getString("course")+"\n";
        }
        if(json.has("password")) {
            msg += json.getString("password") + "\n";
        }
        return msg;
    }

    private boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            return true;
        }else{
            return false;
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 6;
    }

    private boolean isNameValid(String name){
        return name.length()>10;
    }

    private boolean isDateValid(String data){
        int ano = Integer.parseInt(data.split("/")[2]);
        int anoAtual = Calendar.getInstance().get(Calendar.YEAR);
        return (anoAtual-ano >= 16);
    }

    public void showDatePickerDialog(View v) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog newFragment = new DatePickerDialog(RegisterActivity.this,this, year, month, day);
        newFragment.show();
    }

    public void onDateSet(DatePicker view, int year, int m, int day) {
        int month = m+1;
        if(day < 10 && month < 10){
            edtNasc.setText("0"+day+"/0"+month+"/"+year);
        }else if(day <10){
            edtNasc.setText("0"+day+"/"+month+"/"+year);
        }else if (month<10){
            edtNasc.setText(day+"/0"+month+"/"+year);
        }else{
            edtNasc.setText(day+"/"+month+"/"+year);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            registerView.setVisibility(show ? View.GONE : View.VISIBLE);
            registerView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    registerView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            registerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                photoFile = createImageFile();
                Log.i("OK", "criou foto");
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i("Erro", "Erro ao tirar foto");

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                mcurrentPhotoUri = FileProvider.getUriForFile(getApplicationContext(), "bet.belleepoquetech.radarufpa.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mcurrentPhotoUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_CANCELED) {
            //if (data != null) {
            if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
                File file = new File(mCurrentPhotoPath);
                //mcurrentPhotoUri = Uri.fromFile(file);
                //galleryAddPic();
                setPic();
                Log.i("Foto","Retornou a foto");
                Log.i("URI", mcurrentPhotoUri.toString());
                Log.i("PATH",mCurrentPhotoPath.toString());
            }
        }else{
            Log.i("foto","resultCode eh igual a Result_canceled");
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "RADAR_PROFILE_PIC" + timeStamp + "_";
        File storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = 400;
        int targetH = 300;

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
        profile_pic.setImageBitmap(bitmap);
    }

    public void registerWithouPic() {
        HashMap<String,String> params = new HashMap<>();
        params.put("name",name);
        params.put("email",email);
        params.put("password",pass);
        params.put("usertype",spn);
        params.put("birthdate",newBirthday);
        CustomJSONObjectResquest req = new CustomJSONObjectResquest(
                Request.Method.POST,
                urlRegister,
                params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("JSONResponse","Sucesso: \n "+response);
                        Toast.makeText(getApplicationContext(),"Login criado com sucesso!" ,Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("JSONResponse","Erro "+ error);
                        showProgress(false);
                        if (error instanceof AuthFailureError) {
                            //Toast.makeText(getApplicationContext(),"AuthFailureError" ,Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            try {

                                Toast.makeText(getApplicationContext(), getJsonError(error.networkResponse.data),Toast.LENGTH_LONG).show();
                                Log.e("erro",new String(error.networkResponse.data));

                            } catch (JSONException e) {
                                Log.e("erro",new String(error.networkResponse.data).split("</head>")[0]);
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),"Houve um erro no servidor. Tente mais tarde",Toast.LENGTH_LONG).show();
                            }
                        } else if (error instanceof NetworkError) {
                                        //Toast.makeText(myContext,"NetworkError" ,Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                                        //Toast.makeText(myContext,"ParseError" ,Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(req);
    }

    public void registerWithPic(){
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, urlRegister, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                Log.i("resposta",resultResponse.split("</head>")[0]);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    String message = result.getString("response");
                    Toast.makeText(getApplicationContext(), "Login criado com sucesso ",Toast.LENGTH_LONG).show();
                    Log.i("Message", message);
                    onBackPressed();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }else if(error.getClass().equals(ServerError.class)){
                        try {
                            Toast.makeText(getApplicationContext(), getJsonError(networkResponse.data),Toast.LENGTH_LONG).show();
                            String result = new String(networkResponse.data);
                            Log.i("erro",result.split("</head>")[0]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    String result = new String(networkResponse.data);
                    Log.i("erro",result.split("</head>")[0]);
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String,String> params = new HashMap<>();
                params.put("name",name);
                params.put("email",email);
                params.put("password",pass);
                params.put("usertype",spn);
                params.put("birthdate",newBirthday);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("profile_pic", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromUri(getApplicationContext(),mcurrentPhotoUri), "image/jpeg"));
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(multipartRequest);
    }
    
}


