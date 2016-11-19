package manager.trade.techno.trademanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Signup extends AppCompatActivity {




    EditText et_name,et_mono,et_email;
    ShowHidePasswordEditText et_pwd,et_cnfpwd;
    Button btn_submit;

    String name,email,mobno,password,res,refreshedToken;

    Boolean isInternetPresent = false;
    CheckBox cb_terms;

    private DatabaseReference databaseReference;
    SharedPreferences sharepref;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "fonts/ProductSans-Regular.ttf");
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
        //==2) for fragment hoy to====
        //== fontChanger.replaceFonts((ViewGroup) this.getView());
        //===3) for adepterview and handlerview na use mate====
        //==convertView = inflater.inflate(R.layout.listitem, null);
        //==fontChanger.replaceFonts((ViewGroup)convertView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //==firebase configration
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.keepSynced(true);
        //-------------------------------------------------
        sharepref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);


        et_name = (EditText)this.findViewById(R.id.et_name);
        et_mono = (EditText)this.findViewById(R.id.et_mobno);
        et_email = (EditText)this.findViewById(R.id.et_email_signup);
        et_pwd = (ShowHidePasswordEditText)this.findViewById(R.id.et_pwd);
        et_cnfpwd = (ShowHidePasswordEditText)this.findViewById(R.id.et_cnfpwd);

        Linkify.MatchFilter myMatchFilter = new Linkify.MatchFilter() {
            @Override
            public boolean acceptMatch(CharSequence cs, int start, int end) {
                return start > 5;
            }
        };

        TextView myCustomLink2 = (TextView)this.findViewById(R.id.tv_termsread);
        Pattern pattern2 = Pattern.compile("[a-zA-Z]+");
        myCustomLink2.setText("Read: Terms and Conditions");
        Linkify.addLinks(myCustomLink2,pattern2, "http://www.google.com/", myMatchFilter, null);

        cb_terms=(CheckBox)this.findViewById(R.id.cb_terms);
        cb_terms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(cb_terms.isChecked()==true){
                    btn_submit.setEnabled(true);
                }else{
                    btn_submit.setEnabled(false);
                }
            }
        });


        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        final String email_txt = et_email.getText().toString().trim();

        et_email .addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                if (email_txt.matches(emailPattern) && s.length() > 0 )
                {
                    ///                et_email.setCompoundDrawables(null,null,android.graphics.drawable.,null);
                    //                  Toast.makeText(getApplicationContext(), "valid email address", Toast.LENGTH_SHORT).show();
                    // or
                    //textView.setText("valid email");
                }
                else
                {
//                    Toast.makeText(getApplicationContext(),"Invalid email address",Toast.LENGTH_SHORT).show();
                    //or
                    // textView.setText("invalid email");
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // other stuffs
            }
        });

        // creating connection detector class instance
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();


        btn_submit = (Button)this.findViewById(R.id.btn_submit_signup);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String before_attherate="";
                String after_attherate="";
                if(et_email.getText().toString().matches(emailPattern)){

                    before_attherate=et_email.getText().toString();
                    before_attherate=before_attherate.substring(0,before_attherate.indexOf("\u0040"));

                    after_attherate=et_email.getText().toString();
                    after_attherate=after_attherate.substring(after_attherate.indexOf("\u0040")+1);
                    after_attherate=after_attherate.substring(0,after_attherate.indexOf("."));


                }else{
                    et_email.setError("Enter Valid Email Address!");
                }
                if(et_name.getText().toString().length()==0){
                    et_name.setError("Enter Name!");
                }else if(et_mono.getText().toString().length()==0
                        || et_mono.getText().toString().length() < 10
                        || et_mono.getText().toString().equalsIgnoreCase("1234567890")){
                    et_mono.setError("Enter Correct Mobile Number!");
                }else if(et_email.getText().toString().length()==0 || !et_email.getText().toString().matches(emailPattern)
                        || et_email.getText().toString().matches("[0-9]+@[0-9]+@[0-9]")
                        || et_email.getText().toString().equalsIgnoreCase("abc@abc.com")
                        || before_attherate.equalsIgnoreCase(after_attherate)) {
                    et_email.setError("Enter Valid Email Address!");
                }else if(before_attherate.length()<=4 || before_attherate.equalsIgnoreCase("12345")
                        || before_attherate.equalsIgnoreCase("12345456789")
                        ||before_attherate.equalsIgnoreCase("1234567890") ||
                        before_attherate.equalsIgnoreCase("abcde") ||
                        before_attherate.equalsIgnoreCase("abcdef") ||
                        before_attherate.equalsIgnoreCase("qwert") ||
                        before_attherate.equalsIgnoreCase("qwerty") ||
                        before_attherate.equalsIgnoreCase("asdfg")||
                        before_attherate.equalsIgnoreCase("asdfgh")){
                    et_email.setError("Try other email please!!!");
                }else if(after_attherate.length()<=4 || after_attherate.equalsIgnoreCase("12345")
                        || after_attherate.equalsIgnoreCase("12345456789")
                        ||after_attherate.equalsIgnoreCase("1234567890") ||
                        after_attherate.equalsIgnoreCase("abcde") ||
                        after_attherate.equalsIgnoreCase("abcdef") ||
                        after_attherate.equalsIgnoreCase("qwert") ||
                        after_attherate.equalsIgnoreCase("qwerty") ||
                        after_attherate.equalsIgnoreCase("asdfg")||
                        after_attherate.equalsIgnoreCase("asdfgh")||
                        after_attherate.equalsIgnoreCase("email")){
                    et_email.setError("Try other email please!!!");
                }else if(et_pwd.getText().toString().length()==0 || et_pwd.getText().toString().length()<6
                        || et_pwd.getText().toString().matches("[0-9]")
                        || et_pwd.getText().toString().equalsIgnoreCase("123456")
                        || et_pwd.getText().toString().equalsIgnoreCase("1234567890")){
                    et_pwd.setError("Password must be more than 6 digit and Unique!");
                }else if(et_pwd.getText().toString().contains("#")){
                    et_pwd.setError("Please dont use #,% in your password.!");
                }else if(et_cnfpwd.getText().toString().length()==0 || !et_cnfpwd.getText().toString().equals(et_pwd.getText().toString())){
                    et_cnfpwd.setError("Password Not matched!!!");
                }
                else{
                    name=et_name.getText().toString();
                    email=et_email.getText().toString();
                    mobno=et_mono.getText().toString();
                    password=et_pwd.getText().toString();


                    if (isInternetPresent) {
                        // Internet Connection is Present
                        // make HTTP requests
                        //refreshedToken = FirebaseInstanceId.getInstance().getToken();
                        databaseReference.child(mobno).addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // Get user value

                                        String mobile_no = dataSnapshot.child("mobno").getValue(String.class);
                                        String pwd = dataSnapshot.child("pwd").getValue(String.class);


                                        Log.d("Mobile USEr = ",mobile_no+"\n password ="+pwd);
                                        if(mobno.equals(mobile_no)){
                                            Snackbar snackbar = Snackbar
                                                    .make(findViewById(android.R.id.content), " Already registered. ! ", Snackbar.LENGTH_LONG);

                                            // Changing message text color
                                            snackbar.setActionTextColor(Color.BLUE);

                                            // Changing action button text color
                                            View sbView = snackbar.getView();
                                            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                                            textView.setTextColor(Color.YELLOW);
                                            snackbar.show();

                                            Toast.makeText(Signup.this, "  Already registered. !   ", Toast.LENGTH_LONG).show();



                                        }else if(mobile_no ==  null  || pwd == null){
                                            new Signup_async().execute();
                                        } else {
                                            new Signup_async().execute();
                                        }

                                        // ...
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w("TAG", "getUser:onCancelled", databaseError.toException());
                                        new Signup_async().execute();
                                    }
                                });


                    }else{

                        Snackbar snackbar = Snackbar
                                .make(findViewById(android.R.id.content), " Sorry! No Internet!!!", Snackbar.LENGTH_LONG);

                        // Changing message text color
                        snackbar.setActionTextColor(Color.BLUE);

                        // Changing action button text color
                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.YELLOW);
                        snackbar.show();

                        Toast.makeText(Signup.this, "  No Internet Connection!!!.  ", Toast.LENGTH_LONG).show();


                    }




                }


            }
        });





    }




    class Signup_async extends AsyncTask<Object, Void, String> {

        private final static String TAG = "EntryActivity.EfetuaEntry";

        protected ProgressDialog progressDialog;
        String response_string;
        @Override
        protected void onPreExecute()//execute thaya pela
        {

            super.onPreExecute();
            //Log.d("pre execute", "Executando onPreExecute ingredients");

            //inicia diálogo de progress, mostranto processamento com servidor.
            progressDialog = ProgressDialog.show(Signup.this, "Loading", "Please Wait--------.", true, false);
            //progressDialog no use gol chakadu lavava mate thay.
        }

        @Override
        protected String doInBackground(Object... parametros) {

            System.out.println("On do in back ground----done-------");




            Map<String, String> user_details = new HashMap<String, String>();
            user_details.put("name", name);
            user_details.put("mobno", mobno);
            user_details.put("pwd",password);
            user_details.put("email", email);
            //=================================================================


            databaseReference.child(mobno).setValue(user_details);


            return res;

        }



        @Override
        protected void onPostExecute(String result)
        {

            System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);

            progressDialog.dismiss();
            Intent intent = new Intent(Signup.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // call this to finish the current activity



        }







    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
