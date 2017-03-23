package bet.belleepoquetech.radarufpa;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;;
import android.support.v7.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button registerBtn;
    private Button cancelBtn;
    private String name;
    private String pass;
    private String email;
    private String newBirthday;
    private Date birthday;
    private String course;
    private SimpleDateFormat df;
    private SimpleDateFormat myFormat;
    private SharedPreferences mSharedPreferences;
    private String urlAuthenticate = "http://aedi.ufpa.br/~leonardo/radarufpa/index.php/api/authenticate";
    private String urlRegister = "http://aedi.ufpa.br/~leonardo/radarufpa/index.php/api/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        cancelBtn = (Button)findViewById(R.id.cancelBtn);
        registerBtn = (Button)findViewById(R.id.register);
        registerBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog registerDialog = new Dialog(LoginActivity.this);
                registerDialog.setContentView(R.layout.dialog_register_layout);
                registerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int device_TotalWidth = metrics.widthPixels;
                int device_TotalHeight = metrics.heightPixels;
                registerDialog.getWindow().setLayout(device_TotalWidth*80/100, device_TotalHeight*70/100);
                Button btnRegister = (Button)registerDialog.findViewById(R.id.registerBtn);
                btnRegister.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText edtNome = (EditText)registerDialog.findViewById(R.id.edtNome);
                        EditText edtEmail = (EditText)registerDialog.findViewById(R.id.edtEmail);
                        EditText edtSenha = (EditText)registerDialog.findViewById(R.id.edtSenha);
                        EditText edtNasc = (EditText)registerDialog.findViewById(R.id.edtNasc);
                        EditText edtCurso= (EditText)registerDialog.findViewById(R.id.edtCurso);
                        name = edtNome.getText().toString();
                        email = edtEmail.getText().toString();
                        pass = md5(edtSenha.getText().toString());
                        myFormat = new SimpleDateFormat("dd/MM/yyyy");
                        df = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            birthday = myFormat.parse(edtNasc.getText().toString());
                            newBirthday = df.format(birthday);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        course = edtCurso.getText().toString();
                        HashMap<String,String>params = new HashMap<>();
                        params.put("name",name);
                        params.put("email",email);
                        params.put("password",pass);
                        params.put("course",course);
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
                                        try {
                                            Log.i("response",response.getString("response"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.i("JSONResponse","Erro "+ error);
                                        if (error instanceof AuthFailureError) {
                                            // Toast.makeText(getApplicationContext(),"AuthFailureError" ,Toast.LENGTH_LONG).show();
                                        } else if (error instanceof ServerError) {
                                            Toast.makeText(getApplicationContext(),"Erro no servido, tente mais tarde.." ,Toast.LENGTH_LONG).show();
                                        } else if (error instanceof NetworkError) {
                                            //Toast.makeText(myContext,"NetworkError" ,Toast.LENGTH_LONG).show();
                                        } else if (error instanceof ParseError) {
                                            //Toast.makeText(myContext,"ParseError" ,Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                        );
                        AppController.getInstance().addToRequestQueue(req);
                        registerDialog.dismiss();
                    }
                });

                registerDialog.setCancelable(true);
                registerDialog.show();

                cancelBtn = (Button)registerDialog.findViewById(R.id.cancelBtn);
                cancelBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        registerDialog.dismiss();
                    }
                });
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    public void changeActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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

    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            //showProgress(true);
            HashMap<String,String>params = new HashMap<>();
            params.put("email",email);
            params.put("password",md5(password));
            CustomJSONObjectResquest req = new CustomJSONObjectResquest(
                    Request.Method.POST,
                    urlAuthenticate,
                    params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("JSONResponse","Sucesso: \n "+response);
                            mSharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.SharedPreferences),Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            try {
                                Log.i("token",response.getString("token"));
                                editor.putString("token",response.getString("token"));
                                editor.apply();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            changeActivity();
                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("JSONResponse","Erro "+ error);
                            if (error instanceof AuthFailureError) {
                                mEmailView.setError("Email incorreto");
                                mPasswordView.setError("Senha incorreta");
                               // Toast.makeText(getApplicationContext(),"AuthFailureError" ,Toast.LENGTH_LONG).show();
                            } else if (error instanceof ServerError) {
                                Toast.makeText(getApplicationContext(),"Erro no servido, tente mais tarde.." ,Toast.LENGTH_LONG).show();
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
        return password.length() > 4;
    }

    private boolean isNomevalid(){
        return true;
    }

    private boolean isCourseEmpty(){
        return true;
    }

    private boolean isDateValid(){
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}

