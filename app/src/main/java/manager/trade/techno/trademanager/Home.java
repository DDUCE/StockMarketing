package manager.trade.techno.trademanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import DB.DatabaseHelper;
import DB.DatabaseHelper_Compnies;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Home extends AppCompatActivity {


    SharedPreferences sharepref;

    DrawerLayout drawer;
    private MenuItem activeMenuItem;
    NavigationView navigationView;
    MaterialSearchView searchView;


    TextView tv_mobileno, tv_email_address_header;


    DatabaseHelper_Compnies myDbHelper;
    Cursor c;

    String comapny_fullname,str_company_scurityID,res1;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "fonts/ProductSans-Regular.ttf");
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
        //==2) for fragment hoy to====
        //== fontChanger.replaceFonts((ViewGroup) this.getView());
        //===3) for adepterview and handlerview na use mate====
        //==convertView = inflater.inflate(R.layout.listitem, null);
        //==fontChanger.replaceFonts((ViewGroup)convertView);


        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.drawable.appicon);

   /*     if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.parseColor("#1976D2"));
            window.setNavigationBarColor(Color.parseColor("#1976D2"));
        }*/


        sharepref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        myDbHelper = new DatabaseHelper_Compnies(Home.this);

        List<String> compnies=new ArrayList<String>();





        try{

            myDbHelper.openDataBase();

            c=myDbHelper.query("listingcompanies",new String[]{"SecurityCode",
                            "SecurityId",
                            "SecurityName",
                            "Status",
                            "Group_name",
                            "FaceValue",
                            "ISINNo",
                            "Industry",
                            "Instrument"} ,
                    null ,null, null,null, null);

            if(c.moveToFirst()) {
                do {

                    str_company_scurityID=c.getString(0).toString();
                    comapny_fullname=c.getString(2).toString();;

                    compnies.add(comapny_fullname+"\n("+str_company_scurityID+")");

                } while (c.moveToNext());
            }

            myDbHelper.close();


        }catch(SQLException sqle){

            throw sqle;

        }catch(Exception excmain){

            excmain.printStackTrace();
        }






        String[] companies_arry = new String[compnies.size()];
        companies_arry = compnies.toArray(companies_arry);






        FragmentTransaction tx;
        tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.frame, new Home_fragment());
        tx.commit();






        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setVoiceSearch(false);
        searchView.setHint("Enter Company Name");
        searchView.setCursorDrawable(R.drawable.custome_cursor);
        searchView.setSuggestions(companies_arry);
        searchView.setEllipsize(true);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Snackbar.make(findViewById(android.R.id.content), "Query: " + query, Snackbar.LENGTH_LONG).show();

                str_company_scurityID = query.substring(query.indexOf("(")+1,query.length()-1);
                comapny_fullname=query.substring(0,query.indexOf("("));
                new GetShareIndex().execute();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle Toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };
        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(Toggle);
        //calling sync state is necessay or else your hamburger icon wont show up
        Toggle.syncState();


        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        View header = navigationView.getHeaderView(0);
        tv_mobileno = (TextView) header.findViewById(R.id.tv_mobileno);
        tv_email_address_header = (TextView) header.findViewById(R.id.tv_email_address_header);

        tv_email_address_header.setText(sharepref.getString("key_useremail","NA"));
        tv_mobileno.setText(sharepref.getString("key_usermobno","NA"));




        // navigationView.setNavigationItemSelectedListener(this);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        assert navigationView != null;
        navigationView.setCheckedItem(R.id.nav_live);
        navigationView.getMenu().getItem(0).setChecked(true);
        //navigationView.setItemIconTintList(null);

        Menu menu =navigationView.getMenu();

        MenuItem admin_tips = menu.findItem(R.id.nav_admin_tips);

        String logedin_mobno=sharepref.getString("key_usermobno", "null");
        if(logedin_mobno.equals("8866875879") || logedin_mobno.equals("8866263371")  ) {
            admin_tips.setVisible(true);
        }else{
            admin_tips.setVisible(false);
        }


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                navigationView.getMenu().getItem(0).setChecked(false);


                //Checking if the item is in checked state or not, if not make it in checked state
                if (activeMenuItem != null) activeMenuItem.setChecked(false);
                activeMenuItem = menuItem;
                menuItem.setChecked(true);
                //else menuItem.setChecked(true);

                //Closing drawer on item click
                drawer.closeDrawers();
                Fragment fragment = null;

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {


                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_live:
                        // Toast.makeText(getApplicationContext(),"Shop",Toast.LENGTH_SHORT).show();
                        fragment = new Home_fragment();
                        break;

                    case R.id.nav_watchlist:

                        fragment= new Watchlist_Firebase();
                        break;

                    case R.id.nav_tips:
                        // Toast.makeText(getApplicationContext(),"Shop",Toast.LENGTH_SHORT).show();
                        fragment= new Tips();
                        break;

                  /*  case R.id.nav_topgainer:
                        // Toast.makeText(getApplicationContext(),"Shop",Toast.LENGTH_SHORT).show();
                        fragment= new TopGainer();
                        break;
                    case R.id.nav_toplooser:
                        // Toast.makeText(getApplicationContext(),"Shop",Toast.LENGTH_SHORT).show();
                        fragment= new TopLooser();
                        break;*/

                    case R.id.nav_share:
                        // Toast.makeText(getApplicationContext(),"Shop",Toast.LENGTH_SHORT).show();

                        Intent intentshare = new Intent(Intent.ACTION_SEND);
                        intentshare.setType("text/plain");
                        intentshare.putExtra(Intent.EXTRA_TEXT, "Indian Stock market apps for Traders and Broker to get price and index rate.");
                        startActivity(Intent.createChooser(intentshare, "Share"));


                        break;
                    case R.id.nav_feedback:
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (Exception e) {
                            // Log.d("TAG","Message ="+e);
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/")));
                        }
                        break;

                    case R.id.nav_currency:
                        fragment= new Currency_fragment();
                        break;




                    case R.id.nav_exit:
                        System.exit(0);
                        getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                        break;

                    case R.id.nav_aboutus:
                        // Toast.makeText(getApplicationContext(),"Shop",Toast.LENGTH_SHORT).show();
                        fragment= new Aboutus();
                        break;

                    case R.id.nav_logout:
                        //Toast.makeText(getApplicationContext(),"Logout",Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                        Snackbar snackbar = Snackbar
                                .make(findViewById(android.R.id.content), "  Thank You.!!!!", Snackbar.LENGTH_LONG);

                        // Changing message text color
                        snackbar.setActionTextColor(Color.BLUE);

                        // Changing action button text color
                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.YELLOW);
                        snackbar.show();

                        Toast.makeText(Home.this, "Logout Done !\nMiss you, Comeback Soon.  ", Toast.LENGTH_LONG).show();

                        sharepref.edit().putString("key_login","no").commit();
                        sharepref.edit().putString("key_useremail", "").apply();
                        sharepref.edit().putString("key_usermobno", "").apply();


                        break;

                    case R.id.nav_admin_tips:
                        // Toast.makeText(getApplicationContext(),"Shop",Toast.LENGTH_SHORT).show();
                        fragment= new Add_Tips();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Coming Soon...", Toast.LENGTH_SHORT).show();

                        break;

                }

                if (fragment != null) {
                    Log.d("fragment Tag",fragment.toString());

                    //fragmentTransaction.replace(R.id.frame, fragment);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).addToBackStack(null).commit();

                    getSupportActionBar().setTitle("Trade Manager");

                } else {
                    menuItem.setChecked(false);
                }


                return true;
            }
        });


    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (searchView.isSearchOpen()) {
                searchView.closeSearch();
            } else {

                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    System.exit(0);
                    getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    return;
                } else if(getFragmentManager().getBackStackEntryCount() == 0) {
                    this.doubleBackToExitPressedOnce = true;
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, new Home_fragment()).commit();
                    navigationView.getMenu().getItem(0).setChecked(true);
                    Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
                }else {


                    // getFragmentManager().popBackStack();
                }


                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*//noinspection SimplifiableIfStatement
        if (id == R.id.action_cart) {
            // Toast.makeText(getApplicationContext(),"Cart",Toast.LENGTH_SHORT).show();

            if(pemail.equalsIgnoreCase("demo@demo.com")){
                Toast.makeText(getApplicationContext(),"Register First !",Toast.LENGTH_SHORT).show();
            }else{
                startActivity(new Intent(Home.this,CartProducts.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
            return true;
        }


        if (id == R.id.action_dailyoffers) {
            // Toast.makeText(getApplicationContext(),"Cart",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Home.this,DailyOffers.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }




    class GetShareIndex extends AsyncTask<Object, Void, String> {



        @Override
        protected void onPreExecute()//execute thaya pela
        {

            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");




        }

        @Override
        protected String doInBackground(Object... parametros) {

            // System.out.println("On do in back ground----done-------");


            //Log.d("post execute", "Executando doInBackground   ingredients");

// should be a singleton
            OkHttpClient client = new OkHttpClient();
            /*HttpUrl.Builder urlBuilder = HttpUrl.parse("https://ajax.googleapis.com/ajax/services/search/images").newBuilder();
            urlBuilder.addQueryParameter("v", "1.0");
            urlBuilder.addQueryParameter("q", "android");
            urlBuilder.addQueryParameter("rsz", "8");
            String url = urlBuilder.build().toString();*/

            Log.d("url",str_company_scurityID);

            Request request = new Request.Builder()
                    .url("http://finance.google.com/finance/info?client=ig&q=bom:"+str_company_scurityID)
                    .build();



            try{
                //request mate nicheno code
                Response response = client.newCall(request).execute();

                res1=response.body().string();
                // Log.d("okhtp==",res1);

            }catch(Exception e){
                e.printStackTrace();

            }



//            progressDialog.dismiss();
            return res1;

        }



        @Override
        protected void onPostExecute(String result)
        {

            String response_string="";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);

            if (res1 == null || res1.equals("")) {



                Toast.makeText(Home.this, "Network connection ERROR or ERROR", Toast.LENGTH_LONG).show();
                //

                return;
            }


            try {



                // Log.d("result string",res1);
                res1=res1.substring(3);
                JSONArray array_res = new JSONArray(res1);

                for(int a =0;a<array_res.length();a++){


                    //  Log.i("RESPONSE", res1);
                    JSONObject obj = array_res.getJSONObject(a);

                    String company_code = obj.getString("t");
                    String current_index = obj.getString("l_cur");
                    current_index= Html.fromHtml((String) current_index).toString();
                    String diff_index = obj.getString("c");
                    String diff_per_index = obj.getString("cp");
                    String time_index = obj.getString("lt");
                    String preivous_close = obj.getString("pcls_fix");


                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Home.this);

                    // set title
                    alertDialogBuilder.setTitle(comapny_fullname);

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Details\n"+current_index+"\n"+diff_index+" ( "+diff_per_index+"%)\nLast Update : "+time_index)
                            .setCancelable(false)
                            /*.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    MainActivity.this.finish();
                                }
                            })*/
                            .setNegativeButton("OK",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();



                }

            }

            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            //pg_bar.setVisibility(View.GONE);





        }
    }




}

