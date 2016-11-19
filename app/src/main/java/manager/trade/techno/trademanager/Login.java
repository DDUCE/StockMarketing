package manager.trade.techno.trademanager;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {
    Button btn_login,btn_signup;
    TextView tv_forgetpassword;
    EditText et_mobno,et_password;
    String mobno,password,res,email_frg_pwd,refreshedToken;

    SharedPreferences sharepref;

    Boolean isInternetPresent = false;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);  //==1) for activity====
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "fonts/ProductSans-Regular.ttf");
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
        //==2) for fragment hoy to====
        //== fontChanger.replaceFonts((ViewGroup) this.getView());
        //===3) for adepterview and handlerview na use mate====
        //==convertView = inflater.inflate(R.layout.listitem, null);
        //==fontChanger.replaceFonts((ViewGroup)convertView);

        sharepref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        if (android.os.Build.VERSION.SDK_INT > 14) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //==firebase configration
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.keepSynced(true);
        //-------------------------------------------------



        btn_login=(Button)this.findViewById(R.id.btn_signin);
        btn_signup=(Button)this.findViewById(R.id.btn_sigup);

        et_mobno=(EditText)this.findViewById(R.id.et_mobileno);
        et_password=(EditText)this.findViewById(R.id.et_password);

        tv_forgetpassword=(TextView)this.findViewById(R.id.tv_forgetpassword);

        // creating connection detector class instance
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isInternetPresent) {
                    // Internet Connection is Present
                    // make HTTP requests


                    if(et_mobno.getText().toString().length()>10
                            ||et_mobno.getText().toString().length()<10){
                        et_mobno.setError("Enter Valid Mobile No !");
                    }else if(et_password.getText().toString().length()<6){
                        et_password.setError("Password must be 6 digit or more!");
                    }else {

                        mobno=et_mobno.getText().toString();
                        password=et_password.getText().toString();
                        refreshedToken = FirebaseInstanceId.getInstance().getToken();

                        databaseReference.child(mobno).addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // Get user value

                                        String mobile_no = dataSnapshot.child("mobno").getValue(String.class);
                                        String pwd = dataSnapshot.child("pwd").getValue(String.class);
                                        String email = dataSnapshot.child("email").getValue(String.class);


                                        Log.d("Mobile USEr = ",mobile_no+"\n password ="+pwd);
                                        if(mobno.equals(mobile_no) && pwd.equals(password)){

                                            sharepref.edit().putString("key_login","yes").apply();
                                            sharepref.edit().putString("key_useremail", email).apply();
                                            sharepref.edit().putString("key_usermobno", mobile_no).apply();

                                            startActivity(new Intent(Login.this,Home.class));
                                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                            finish();
                                        }else if(mobile_no ==  null  || pwd == null){
                                            Snackbar snackbar = Snackbar
                                                    .make(findViewById(android.R.id.content), " Sorry! No Data found!!!", Snackbar.LENGTH_LONG);

                                            // Changing message text color
                                            snackbar.setActionTextColor(Color.BLUE);

                                            // Changing action button text color
                                            View sbView = snackbar.getView();
                                            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                                            textView.setTextColor(Color.YELLOW);
                                            snackbar.show();

                                            Toast.makeText(Login.this, "  Sorry! No Data found!!!  ", Toast.LENGTH_LONG).show();

                                        } else {
                                            Snackbar snackbar = Snackbar
                                                    .make(findViewById(android.R.id.content), " Sorry! No Data found!!!", Snackbar.LENGTH_LONG);

                                            // Changing message text color
                                            snackbar.setActionTextColor(Color.BLUE);

                                            // Changing action button text color
                                            View sbView = snackbar.getView();
                                            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                                            textView.setTextColor(Color.YELLOW);
                                            snackbar.show();

                                            Toast.makeText(Login.this, "  Sorry! No Data found!!!  ", Toast.LENGTH_LONG).show();

                                        }

                                        // ...
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w("TAG", "getUser:onCancelled", databaseError.toException());
                                        // ...
                                    }
                                });

                    }


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

                    Toast.makeText(Login.this, "  No Internet Connection!!!.  ", Toast.LENGTH_LONG).show();


                }



            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Login.this,
                        Signup.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);


            }
        });

        final Dialog dialog = new Dialog(Login.this);



        tv_forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                dialog.setContentView(R.layout.forget_pwd_dialog);

                dialog.setCanceledOnTouchOutside(false);
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                /*lp.width = 900;
                lp.height = 700;*/
                lp.gravity = Gravity.CENTER;
                lp.dimAmount = 0;
                dialog.getWindow().setAttributes(lp);

                final EditText et_frg_pwd =(EditText)dialog.findViewById(R.id.et_frg_pwd);
                Button btn_submit = (Button) dialog.findViewById(R.id.btn_frg_pwd);


                btn_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        email_frg_pwd=et_frg_pwd.getText().toString();

                        if(et_frg_pwd.getText().toString().length()<10 || et_frg_pwd.getText().toString().length()>10)
                        {
                            et_frg_pwd.setError("Enter Valid  Mobile No. ");

                        }else{
                            email_frg_pwd=et_frg_pwd.getText().toString();
                           // new GetPassword().execute();
                            dialog.dismiss();
                        }

                    }
                });

                dialog.show();
            }
        });


    }








  /*  class GetPassword extends AsyncTask<Object, Void, String> {

        private final static String TAG = "EntryActivity.EfetuaEntry";

        protected ProgressDialog progressDialog;
        String item_ingre;
        @Override
        protected void onPreExecute()//execute thaya pela
        {

            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");

            //inicia diálogo de progress, mostranto processamento com servidor.
            progressDialog = ProgressDialog.show(Login.this, "Loading", "Please Wait...", true, false);
            //progressDialog no use gol chakadu lavava mate thay.
        }

        @Override
        protected String doInBackground(Object... parametros) {

            // System.out.println("On do in back ground----done-------");


            //Log.d("post execute", "Executando doInBackground   ingredients");



            try{
                //request mate nicheno code

                HttpClient client = new DefaultHttpClient();
                //String postURL = "http://169.254.76.188:8084/Sunil/order_entery";
                //HttpPost post = new HttpPost(postURL);

                HttpPost post = new HttpPost("http://arihantmart.com/androidapp/forgetpassword.php?email_address="+email_frg_pwd);//2015-5-15
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                //params.add(new BasicNameValuePair("key_item",item_ingredients));


                //response mate niche no code

                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                post.setEntity(ent);

                HttpResponse responsePOST = client.execute(post);
                HttpEntity resEntity = responsePOST.getEntity();
                if (resEntity != null) {
                    String resp = EntityUtils.toString(resEntity);
                    res = resp;

                    // System.out.println("response got from server----- "+resp);


                }}catch(Exception e){
                e.printStackTrace();

            }



//            progressDialog.dismiss();
            return res;

        }



        @Override
        protected void onPostExecute(String result)
        {

            String response_string="";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);

            if (res == null || res.equals("")) {

                progressDialog.dismiss();
                Toast.makeText(Login.this, "Network connection ERROR or ERROR", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                JSONObject obj = new JSONObject(res);


                // Log.i("RESPONSE", res);

                response_string = obj.getString("info");

                JSONArray array_res = new JSONArray(response_string);

                if (array_res.length() != 0) {

                    String Rpwd = array_res.getJSONObject(0).getString("password");


                    GMailSender mailsender = new GMailSender("technocratsappware@gmail.com", "technocratsappware@9033228796");
                    String[] toArr = {"technocratsappware@gmail.com", email_frg_pwd};
                    mailsender.set_to(toArr);
                    mailsender.set_from("technocratsappware@gmail.com");
                    mailsender.set_subject("Jainisam Forget Password");
                    mailsender.setBody("Hello.\nDear Member\n\n" +
                            "\nThis mail contains your password for Arihant Mart android application so we suggest you to delete it after read for safety." +
                            "\n\n" +
                            "\nPassword is:" +
                            "\n" + Rpwd +
                            "\n\nif you get this email by mistake or this is not concern with you we advise you to delete this mail as soon as possible and kindly inform to owner company Technocrats Appware, otherwise if it will contain private information so you may have to face problame issue regarding this." +
                            "\nThank You\n.........Auto Generated Mail.........");

                    try {
                        //mailsender.addAttachment("/sdcard/filelocation");

                        if (mailsender.send()) {
                            Toast.makeText(Login.this,
                                    "Email was sent successfully.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Login.this, "Email was not sent.",
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {

                        Log.e("MailApp", "Could not send email", e);
                    }


                    // Log.d("usr email", Ruser_email);


                }else{
                    Toast.makeText(Login.this, "Sorry!!!Email not registered with us!",
                            Toast.LENGTH_LONG).show();

                }
            }

            catch(Exception e){
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            progressDialog.dismiss();




        }
    }

*/








}
