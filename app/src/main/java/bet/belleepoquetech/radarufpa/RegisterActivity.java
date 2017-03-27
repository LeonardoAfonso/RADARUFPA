package bet.belleepoquetech.radarufpa;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;

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

public class RegisterActivity extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener {
    private Button btnRegister;
    private  EditText edtNome;
    private  EditText edtEmail;
    private  EditText edtSenha;
    private  EditText edtNasc;
    private  EditText edtCurso;
    private Button cancelBtn;
    private String name;
    private String pass;
    private String email;
    private String newBirthday;
    private Date birthday;
    private String course;
    private SimpleDateFormat df;
    private SimpleDateFormat myFormat;
    private String urlRegister = "http://aedi.ufpa.br/~leonardo/radarufpa/index.php/api/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_register_layout);
        edtNome = (EditText)findViewById(R.id.name);
        edtEmail = (EditText)findViewById(R.id.email);
        edtSenha = (EditText)findViewById(R.id.password);
        edtNasc = (EditText)findViewById(R.id.birth);
        edtCurso= (EditText)findViewById(R.id.curso);
        btnRegister = (Button)findViewById(R.id.registerBtn);
        cancelBtn = (Button)findViewById(R.id.cancelBtn);

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

    public EditText getEdtNasc() {
        return edtNasc;
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

                boolean cancel = false;

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
                course = edtCurso.getText().toString();

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
                }else if(isCourseEmpty()){
                    edtCurso.setError(getString(R.string.error_field_required));
                    cancel = true;
                }

                if(!cancel){
                    HashMap<String,String> params = new HashMap<>();
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
                                        //Toast.makeText(getApplicationContext(),"AuthFailureError" ,Toast.LENGTH_LONG).show();
                                    } else if (error instanceof ServerError) {
                                        try {
                                            Toast.makeText(getApplicationContext(), getJsonError(error.networkResponse.data) ,Toast.LENGTH_LONG).show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
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
        if(json.has("course")){
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

    private boolean isCourseEmpty(){
        return TextUtils.isEmpty(course);
    }

    private boolean isDateValid(String data){
        int ano = Integer.parseInt(data.split("/")[2]);
        return ano<2000;
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
}


