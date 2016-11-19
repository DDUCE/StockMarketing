package manager.trade.techno.trademanager;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapters.MyRecyclerAdapter_watchlist;
import DB.DatabaseHelper;
import DB.DatabaseHelper_Compnies;
import MyFirebase.Stockindex;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Watchlist_Firebase extends Fragment {

    private int visibleThreshold = 5;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private static final int HIDE_THRESHOLD = 20;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;

    SharedPreferences sharepref;

    Boolean isInternetPresent = false;
    private Paint p = new Paint();

    private AutoCompleteTextView autoComplete;
    Button btn_add,btn_refresh;
    DatabaseHelper dbh;
    SQLiteDatabase db;

    DatabaseHelper_Compnies myDbHelper;
    Cursor c,mCursor;

    String securitycode,longname;
    private ArrayAdapter<String> adapter;



    String res1,str_whatchlist_shares="";



    private static final String TAG = "watchlist fragment";


    private DatabaseReference databaseReference;
    RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    ArrayList<DataObject_Watchlist> results;



    public Watchlist_Firebase() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "fonts/ProductSans-Regular.ttf");

        View convertView = inflater.inflate(R.layout.fragment_my_watch_list, container, false);
        //==add this line to change all font to coustom font in fragments
        fontChanger.replaceFonts((ViewGroup)convertView);


        myDbHelper = new DatabaseHelper_Compnies(getContext());

        List<String> compnies=new ArrayList<String>();




        sharepref = getContext().getSharedPreferences("MyPref", getContext().MODE_PRIVATE);

        databaseReference = FirebaseDatabase.getInstance().getReference("watchlist");
        databaseReference.keepSynced(true);




//=====this code is for search edittext for add watchlist company names=========================
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

                    String company_securitcode = c.getString(0).toString();
                    String comapny_short_code=c.getString(1).toString();
                    String comapny_fullname=c.getString(2).toString();

                    compnies.add(comapny_fullname +"\n("+company_securitcode+")");

                } while (c.moveToNext());
            }

            myDbHelper.close();


        }catch(SQLException sqle){

            throw sqle;

        }catch(Exception excmain){

            excmain.printStackTrace();
        }

//==============================================================================




        String[] companies_arry = new String[compnies.size()];
        companies_arry = compnies.toArray(companies_arry);

        adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,companies_arry);

        autoComplete = (AutoCompleteTextView)convertView.findViewById(R.id.et_companyname);
        btn_add = (Button) convertView.findViewById(R.id.btn_add);
        btn_refresh = (Button) convertView.findViewById(R.id.btn_refrsh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetShareIndex_all().execute();
            }
        });

        // set adapter for the auto complete fields
        autoComplete.setAdapter(adapter);
        autoComplete.setThreshold(1);

        // when the user clicks an item of the drop-down list
        autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {





                longname=arg0.getItemAtPosition(arg2).toString();
                longname=longname.substring(0,longname.indexOf("\n")-1);

                securitycode=arg0.getItemAtPosition(arg2).toString();
                securitycode=securitycode.substring(securitycode.indexOf("\n")+1);

                autoComplete.setText(longname+securitycode);

            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(autoComplete.getText().toString().isEmpty()
                        || autoComplete.getText().equals("null")){
                    autoComplete.setError("Value Missing...");

                }else{
                    securitycode=securitycode.substring(1,securitycode.length()-1);
                    str_whatchlist_shares=securitycode;
                    HideSoftKeyboard.hideSoftKeyboard(getActivity());

                    new GetShareIndex().execute();


                }

            }
        });

//==========================================================================================================================




        // Initialize recycler view
        mRecyclerView = (RecyclerView)convertView.findViewById(R.id.post_recycler_view);



        final LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        results = new ArrayList<DataObject_Watchlist>();
        mAdapter = new MyRecyclerAdapter_watchlist(results);

        ItemTouchHelper swipeToDismissTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                // callback for drag-n-drop, false to skip this feature
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                // callback for swipe to dismiss, removing item from data and adapter

                if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT ){
                    AlertDialog.Builder alertbox = new AlertDialog.Builder(getContext());
                    alertbox.setMessage("Item will be removed from your WatchList.");
                    alertbox.setTitle("Delete Item ?");
                    alertbox.setIcon(R.drawable.appicon);

                    alertbox.setNeutralButton("Delete",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface arg0,int arg1) {

                                    DataObject_Watchlist clickedCategory = (DataObject_Watchlist)results.get(viewHolder.getAdapterPosition());
                                    String companycode = clickedCategory.getCompany_code();

                                    Query applesQuery = databaseReference.child(sharepref.getString("key_usermobno",null))
                                            .child(companycode);
                                    applesQuery.addListenerForSingleValueEvent(
                                            new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    // Get user value
                                                    for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                                                        singleSnapshot.getRef().removeValue();
                                                    }

                                                    // ...
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    Log.w("TAG", "getUser:onCancelled", databaseError.toException());
                                                    // ...
                                                }
                                            });
                                    Toast.makeText(getContext(), "Deleted...", Toast.LENGTH_LONG).show();
                                    results.remove(viewHolder.getAdapterPosition()); // results.remove(position); // for basic code
                                    mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());//mAdapter.notifyItemRemoved(position); // for basic code
                                    mAdapter.notifyDataSetChanged();


                                    //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new Watchlist_Firebase()).commit();


                                }
                            });
                    alertbox.show();


                } else {
                    /*removeView();
                    edit_position = position;
                    alertDialog.setTitle("Edit Country");
                    et_country.setText(countries.get(position));
                    alertDialog.show();*/
                }
            }


            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete_icon);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete_icon);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        });
        swipeToDismissTouchHelper.attachToRecyclerView(mRecyclerView);


        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView mRecyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (mRecyclerView == null || mRecyclerView.getChildCount() == 0) ? 0 : mRecyclerView.getChildAt(0).getTop();
                // mSwipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
//=========================================================================================================
                if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                    //onHide();


                    btn_refresh.animate().translationY(btn_refresh.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
                    //floatingActionButton.animate().translationY(floatingActionButton.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();

                    controlsVisible = false;
                    scrolledDistance = 0;
                } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                    //onShow();
                    btn_refresh.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                    // floatingActionButton.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();

                    controlsVisible = true;
                    scrolledDistance = 0;


                }

                if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
                    scrolledDistance += dy;
                }

                //======================================================================================================

                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                }

                //=======================================================================================================
            }

            @Override
            public void onScrollStateChanged(RecyclerView mRecyclerView, int newState) {
                super.onScrollStateChanged(mRecyclerView, newState);
            }


        });



        databaseReference.child(sharepref.getString("key_usermobno",null)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                getAllTask(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return convertView;
    }

    private void getAllTask(DataSnapshot dataSnapshot){


        mRecyclerView.setAdapter(mAdapter);
        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
            //Log.e("Count " ,""+dataSnapshot.getChildrenCount());


            DataObject_Watchlist stock_details = singleSnapshot.getValue(DataObject_Watchlist.class);
            String companname = stock_details.getFull_name();
            String company_code = stock_details.getCompany_code();
            String current_index = stock_details.getCurrent_index();
            String diff_index = stock_details.getDiff_index();
            String diff_per_index = stock_details.getDiff_per_index();
            String time_index = stock_details.getTime_index();
            String preivous_close = stock_details.getPreivous_close();

            //Log.e("diff indes " ,""+diff_index);

            results.add(new DataObject_Watchlist(company_code, current_index, diff_index, diff_per_index, time_index, preivous_close,companname));
            // Log.e("object",company_code);

            mAdapter.notifyDataSetChanged();
        }

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

            Log.d("url",str_whatchlist_shares);

            Request request = new Request.Builder()
                    .url("http://finance.google.com/finance/info?client=ig&q="+str_whatchlist_shares)
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



                Toast.makeText(getContext(), "Network connection ERROR or ERROR", Toast.LENGTH_LONG).show();
                //

                return;
            }


            try {


                mRecyclerView.setAdapter(mAdapter);
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


                  /*  DataObject_Watchlist obj12 = new DataObject_Watchlist(company_code, current_index, diff_index, diff_per_index, time_index, preivous_close);
                    // Log.d("object",EVENT);
                    results.add(obj12);
                    mAdapter.notifyDataSetChanged();*/

                    Map<String, String> stock_index_details = new HashMap<String, String>();
                    stock_index_details.put("full_name", longname);
                    stock_index_details.put("current_index", current_index);
                    stock_index_details.put("diff_index", diff_index);
                    stock_index_details.put("diff_per_index", diff_per_index);
                    stock_index_details.put("time_index", time_index);
                    stock_index_details.put("preivous_close", preivous_close);
                    stock_index_details.put("company_code",company_code);

                    //=================================================================


                    databaseReference.child(sharepref.getString("key_usermobno",null))
                            .child(company_code).setValue(stock_index_details);

                    autoComplete.setText("");
                   getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new Watchlist_Firebase()).commit();

                    Toast.makeText(getContext(), "Added...", Toast.LENGTH_LONG).show();


                }

            }

            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            //pg_bar.setVisibility(View.GONE);





        }
    }



    class GetShareIndex_all extends AsyncTask<Object, Void, String> {



        @Override
        protected void onPreExecute()//execute thaya pela
        {

            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");




        }

        @Override
        protected String doInBackground(Object... parametros) {

            // System.out.println("On do in back ground----done-------");

            for (DataObject_Watchlist p : results) {
                str_whatchlist_shares="bom:"+p.getCompany_code()+",";
                Log.d("all IDS",str_whatchlist_shares);
            }

            //Log.d("post execute", "Executando doInBackground   ingredients");
            // should be a singleton
            OkHttpClient client = new OkHttpClient();

            Log.d("url",str_whatchlist_shares);

            Request request = new Request.Builder()
                    .url("http://finance.google.com/finance/info?client=ig&q="+str_whatchlist_shares)
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



                Toast.makeText(getContext(), "Network connection ERROR or ERROR", Toast.LENGTH_LONG).show();
                //

                return;
            }


            try {


                mRecyclerView.setAdapter(mAdapter);
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


                   /* DataObject_Watchlist obj12 = new DataObject_Watchlist(company_code, current_index, diff_index, diff_per_index, time_index, preivous_close);
                    // Log.d("object",EVENT);
                    results.add(obj12);
                    mAdapter.notifyDataSetChanged();*/

//                   /* Map<String, String> stock_index_details = new HashMap<String, String>();
//                   // stock_index_details.put("full_name", longname);
//                    stock_index_details.put("current_index", current_index);
//                    stock_index_details.put("", diff_index);
//                    stock_index_details.put("", diff_per_index);
//                    stock_index_details.put("", time_index);
//                    stock_index_details.put("", preivous_close);
//                    stock_index_details.put("company_code",company_code);*/

                    databaseReference.child(sharepref.getString("key_usermobno",null)).child(company_code)
                            .child("current_index").setValue(current_index);
                    databaseReference.child(sharepref.getString("key_usermobno",null)).child(company_code)
                            .child("diff_index").setValue(diff_index);
                    databaseReference.child(sharepref.getString("key_usermobno",null)).child(company_code)
                            .child("diff_per_index").setValue(diff_per_index);
                    databaseReference.child(sharepref.getString("key_usermobno",null)).child(company_code)
                            .child("time_index").setValue(time_index);
                    databaseReference.child(sharepref.getString("key_usermobno",null)).child(company_code)
                            .child("preivous_close").setValue(preivous_close);

                    autoComplete.setText("");
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new Watchlist_Firebase()).commit();

                    Toast.makeText(getContext(), "Refresh...", Toast.LENGTH_LONG).show();



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
