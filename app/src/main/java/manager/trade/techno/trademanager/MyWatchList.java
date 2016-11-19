package manager.trade.techno.trademanager;


import android.content.Context;
import android.content.DialogInterface;
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
import android.os.Build;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Adapters.MyRecyclerAdapter_watchlist;
import DB.DatabaseHelper;
import DB.DatabaseHelper_Compnies;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
        */
public class MyWatchList extends Fragment {
    Boolean isInternetPresent = false;
    private Paint p = new Paint();

    private AutoCompleteTextView autoComplete;
    Button btn_add;
    DatabaseHelper dbh;
    SQLiteDatabase db;

    DatabaseHelper_Compnies myDbHelper;
    Cursor c,mCursor;

    String securitycode,longname;

    private ArrayAdapter<String> adapter;

    RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    ArrayList results;

    String res1,str_whatchlist_shares="";


    public MyWatchList() {
// Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "fonts/ProductSans-Regular.ttf");
        // fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
        //==2) for fragment hoy to====
        //fontChanger.replaceFonts((ViewGroup) this.getView());
        //===3) for adepterview and handlerview na use mate====
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_my_watch_list, container, false);
        //==add this line to change all font to coustom font in fragments
        fontChanger.replaceFonts((ViewGroup)convertView);


        myDbHelper = new DatabaseHelper_Compnies(getContext());

        List<String> compnies=new ArrayList<String>();




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



                try {

                    dbh = new DatabaseHelper(getContext());
                    db = dbh.getWritableDatabase();

                    securitycode=securitycode.replace("(","");
                    securitycode=securitycode.replace(")","");
                    //longname=longname.replace(".","");
                    String selectQuery = "SELECT  * FROM " + DatabaseHelper.TABLE_NAME +";";
                    //Log.i("TAG", selectQuery);
                    mCursor = db.rawQuery(selectQuery, null);
                    mCursor.moveToFirst();



                    if((mCursor!=null || mCursor.getCount()>0 ) && mCursor.getCount()<5) {
                        // Log.d("insert==","1");

                        String repeatequery = "SELECT  * FROM " + DatabaseHelper.TABLE_NAME +" WHERE " + DatabaseHelper.company_code + " = '" + securitycode + "';";
                        //Log.i("TAG", selectQuery);
                        Cursor mCursor2 = db.rawQuery(repeatequery, null);
                        mCursor2.moveToFirst();


                        if(mCursor2.getCount()<=0) {
                            String insertquery = "INSERT INTO " + DatabaseHelper.TABLE_NAME + " ( " +
                                    DatabaseHelper.company_short_code + "," +
                                    DatabaseHelper.company_full_name + "," +
                                    DatabaseHelper.company_code +
                                    ") VALUES ( '" +
                                      "null','" +
                                    longname + "','" +
                                    securitycode+"');";
                            // Log.i("TAG", insertquery);
                            Cursor insert_cursor = db.rawQuery(insertquery, null);
                            insert_cursor.moveToFirst();
                            insert_cursor.close();
                            mCursor.close();
                            db.close();

                            Toast.makeText(getContext(), "Added...", Toast.LENGTH_LONG).show();


                        }else if(mCursor2.getCount()>0){
                            Toast.makeText(getContext(), "All Ready Added !!!", Toast.LENGTH_LONG).show();
                        }


                    }/*else if(mCursor.getCount()>4){

                        Toast.makeText(getContext(),"only 5 company add in free version",Toast.LENGTH_LONG).show();

                    }*/else{
                        String insertquery = "INSERT INTO " + DatabaseHelper.TABLE_NAME + " ( " +
                                DatabaseHelper.company_short_code + "," +
                                DatabaseHelper.company_full_name + "," +
                                DatabaseHelper.company_code +
                                ") VALUES ( '" +
                                "null','" +
                                longname + "','" +
                                securitycode+"');";
                        // Log.i("TAG", insertquery);
                        Cursor insert_cursor = db.rawQuery(insertquery, null);
                        insert_cursor.moveToFirst();
                        insert_cursor.close();
                        mCursor.close();
                        db.close();

                        Toast.makeText(getContext(), "First Added...", Toast.LENGTH_LONG).show();


                    }






                }catch (Exception Esql){
                    Esql.printStackTrace();
                }finally {
                    if (mCursor != null && !mCursor.isClosed())
                        mCursor.close();
                    db.close();

                }

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new MyWatchList()).commit();


                }

            }
        });

//==========================================================================================================================

        results = new ArrayList<DataObject_Watchlist>();
        mAdapter = new MyRecyclerAdapter_watchlist(results);


        // Initialize recycler view
        mRecyclerView = (RecyclerView)convertView.findViewById(R.id.post_recycler_view);



        LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);




        // init swipe to dismiss logic
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


                                    try {

                                        dbh = new DatabaseHelper(getContext());
                                        db = dbh.getWritableDatabase();

                                        DataObject_Watchlist clickedCategory = (DataObject_Watchlist)results.get(viewHolder.getAdapterPosition());
                                        String companycode = clickedCategory.getCompany_code();
                                        Log.d("companycode",companycode);

                                        String deletequery = "DELETE FROM " + DatabaseHelper.TABLE_NAME +" WHERE " + DatabaseHelper.company_code + "='" + companycode + "';";
                                        //Log.i("TAG", selectQuery);
                                        mCursor = db.rawQuery(deletequery, null);
                                        mCursor.moveToFirst();


                                    }catch (Exception Esql){
                                        Esql.printStackTrace();
                                    }finally {
                                        if (mCursor != null && !mCursor.isClosed())
                                            mCursor.close();
                                        db.close();

                                    }
                                    Toast.makeText(getContext(), "Deleted...", Toast.LENGTH_LONG).show();

                                    results.remove(viewHolder.getAdapterPosition()); // results.remove(position); // for basic code
                                    mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());//mAdapter.notifyItemRemoved(position); // for basic code



                                }
                            });
                    alertbox.show();

                    mAdapter.notifyDataSetChanged();
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

        return convertView;
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


                    DataObject_Watchlist obj12 = new DataObject_Watchlist(company_code, current_index, diff_index, diff_per_index, time_index, preivous_close,company_code);
                    // Log.d("object",EVENT);
                    results.add(obj12);
                    mAdapter.notifyDataSetChanged();


                }

            }

            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            //pg_bar.setVisibility(View.GONE);





        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);



        //====this is code for default load time code for geting data from table and parse to webservice==========


        ConnectionDetector cd = new ConnectionDetector(getContext());
        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();



        ///===calling index web service from google to live share rates====
        // check for Internet status
        if (isInternetPresent) {
            // Internet Connection is Present


            try {



                dbh = new DatabaseHelper(getContext());
                db = dbh.getWritableDatabase();
                // Select All Query

                String selectQuery = "SELECT  * FROM " + DatabaseHelper.TABLE_NAME+";";
                //Log.i("TAG day", selectQuery);
                mCursor = db.rawQuery(selectQuery, null);

                        /*String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.DURATION, DatabaseHelper.END,DatabaseHelper.START, DatabaseHelper.EVENT
                                ,DatabaseHelper.HREF, DatabaseHelper.CONTEST_ID,DatabaseHelper.RESOURCE_ID, DatabaseHelper.RESOURCE_NAME};
                        //Cursor mCursor = db.query(DatabaseHelper.TABLE_NAME, columns,null,  null, null, null, null);*/


                if (mCursor.moveToFirst()) {
                    do {
                        String company_code = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.company_code));


                        str_whatchlist_shares=str_whatchlist_shares+",bom:"+company_code;




                    } while (mCursor.moveToNext());
                }

                if(mCursor.getCount()==0){
                str_whatchlist_shares="null";
                }



                mCursor.close();
                db.close();

            }catch (Exception ecxe){
                ecxe.printStackTrace();
            }finally {
                if (mCursor != null) {
                    mCursor.close();
                    db.close();
                }
            }


            // make HTTP requests





            if(!str_whatchlist_shares.isEmpty() || str_whatchlist_shares!=null || !str_whatchlist_shares.equalsIgnoreCase("null")) {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new GetShareIndex().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new GetShareIndex().execute();
                }


            }else{

            }








        } else {
            // Internet connection is not present
            // Ask user to connect to Internet



        }



//========================================================================================


    }
}
