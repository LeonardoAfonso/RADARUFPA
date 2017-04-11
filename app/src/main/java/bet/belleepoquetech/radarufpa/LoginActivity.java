package bet.belleepoquetech.radarufpa;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity{
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button registerBtn;
    private SharedPreferences mSharedPreferences;
    private String urlAuthenticate = "http://aedi.ufpa.br/~leonardo/radarufpa/index.php/api/authenticate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        //mProgressView = (ProgressBar) findViewById(R.id.login_progress);
        //mProgressView.setIndeterminate(true);
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

        registerBtn = (Button)findViewById(R.id.register);
        registerBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(it);
            }
        });
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
            //mProgressView.setVisibility(View.VISIBLE);
            showProgress(true);
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
                            //mProgressView.setVisibility(View.GONE);
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
                            //mProgressView.setVisibility(View.GONE);
                            showProgress(false);
                            if (error instanceof AuthFailureError) {
                                mEmailView.setError("Email pode estar incorreto");
                                mPasswordView.setError("Senha pode estar incorreta");
                                Log.e("erro",new String(error.networkResponse.data));
                                Toast.makeText(getApplicationContext(),"Crendenciais incorretas" ,Toast.LENGTH_LONG).show();
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

}

