package manager.trade.techno.trademanager;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import MyFirebase.Stockindex_RecyclerViewAdapter;
import MyFirebase.Stockindex;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainHomeFragment extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener{


    private static final String TAG = "index fragment";
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Stockindex_RecyclerViewAdapter recyclerViewAdapter;
    private DatabaseReference databaseReference;
    private List<Stockindex> allindex;


    SwipeRefreshLayout mSwipeRefreshLayout;


    String res1;
    Boolean isInternetPresent = false;

    public MainHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View convertView = inflater.inflate(R.layout.fragment_main_home, container, false);


        mSwipeRefreshLayout = (SwipeRefreshLayout)convertView.findViewById(R.id.swipe_refrash_home);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Refreshing data on server
                // creating connection detector class instance
                ConnectionDetector cd = new ConnectionDetector(getContext());
                // get Internet status
                isInternetPresent = cd.isConnectingToInternet();

                // check for Internet status
                if (isInternetPresent) {
                    // Internet Connection is Present
                    // make HTTP requests
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        new GetSensex().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        new GetSensex().execute();
                    }

                } else {
                    // Internet connection is not present
                    // Ask user to connect to Internet
                    Snackbar.make(convertView, "No internet connection!", Snackbar.LENGTH_LONG)
                            .setAction("Setting", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                                }
                            }).show();
                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                }
            }
        });

        // creating connection detector class instance
        ConnectionDetector cd = new ConnectionDetector(getContext());
        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent) {
            // Internet Connection is Present
            // make HTTP requests
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new GetSensex().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new GetSensex().execute();
            }

        } else {
            // Internet connection is not present
            // Ask user to connect to Internet
            Snackbar.make(convertView, "No internet connection!", Snackbar.LENGTH_LONG)
                    .setAction("Setting", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        }
                    }).show();

        }



//----------------------------------------------------------------------------------------------------------------------------------------------

        allindex = new ArrayList<Stockindex>();
        databaseReference = FirebaseDatabase.getInstance().getReference("index");
        databaseReference.keepSynced(true);
        recyclerView = (RecyclerView)convertView.findViewById(R.id.indexRecyclerView);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerViewAdapter = new Stockindex_RecyclerViewAdapter(getContext(), allindex);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                getAllTask(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        //================================


        return  convertView;

    }



    private void getAllTask(DataSnapshot dataSnapshot){
        recyclerView.setAdapter(recyclerViewAdapter);
        allindex.clear();
        recyclerViewAdapter.notifyDataSetChanged();
        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
            //Log.e("Count " ,""+dataSnapshot.getChildrenCount());

            Stockindex post = singleSnapshot.getValue(Stockindex.class);
            /*System.out.println("title: " + post.getTitle());
            System.out.println("diff: " + post.getDiff());*/
            allindex.add(new Stockindex(post.getTitle(),post.getIndexpoint(),post.getDiff(),post.getTime()));

        }

        recyclerViewAdapter.notifyDataSetChanged();
    }


    class GetSensex extends AsyncTask<Object, Void, String> {



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



            Request request = new Request.Builder()
                    .url("http://finance.google.com/finance/info?client=ig&q=INDEXBOM:SENSEX,nse:nifty,INDEXNIKKEI:NI225,INDEXNASDAQ%3A.IXIC,NSE%3AMCX")
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
                Map<String, Map<String, String>> index_root = new HashMap<String, Map<String, String>>();


                res1=res1.substring(3);
                JSONArray array_res = new JSONArray(res1);




                    //  Log.i("RESPONSE", res1);
                    JSONObject obj = array_res.getJSONObject(0);

                    String sensex = obj.getString("l_cur");
                    sensex= Html.fromHtml((String) sensex).toString();
                    String sensex_diff = obj.getString("c");
                    String sensex_diff_per = obj.getString("cp");
                    String sensex_time = obj.getString("lt");


                    Map<String, String> node_sensex = new HashMap<String, String>();
                    node_sensex.put("title", "SENSEX");
                    node_sensex.put("indexpoint", sensex);
                    node_sensex.put("diff", sensex_diff+" ( "+sensex_diff_per+" % )");
                    node_sensex.put("time", sensex_time);







                    // ==------nifty---------------------

                    JSONObject obj2 = array_res.getJSONObject(1);

                    String nifty = obj2.getString("l_cur");
                    nifty= Html.fromHtml((String) nifty).toString();
                    String nifty_diff = obj2.getString("c");
                    String nifty_diff_per = obj2.getString("cp");
                    String nifty_time = obj2.getString("lt");


                    Map<String, String> node_nifty = new HashMap<String, String>();
                    node_nifty.put("title", "NIFTY");
                    node_nifty.put("indexpoint", nifty);
                    node_nifty.put("diff", nifty_diff+" ( "+nifty_diff_per+" % )");
                    node_nifty.put("time", nifty_time);





                    //===========nasdaq============================

                    JSONObject obj3 = array_res.getJSONObject(3);

                    String nasdaq = obj3.getString("l_cur");
                    String nasdaq_diff = obj3.getString("c");
                    String nasdaq_diff_per = obj3.getString("cp");
                    String nasdaq_time = obj3.getString("lt");



                Map<String, String> node_nasdaq = new HashMap<String, String>();
                node_nasdaq.put("title", "NASDAQ");
                node_nasdaq.put("indexpoint", nasdaq);
                node_nasdaq.put("diff",nasdaq_diff+" ( "+nasdaq_diff_per+" % )");
                node_nasdaq.put("time", nasdaq_time);

                    //========nikkei-----------------

                    JSONObject obj4 = array_res.getJSONObject(2);

                    String nikkei = obj4.getString("l_cur");
                    String nikkei_diff = obj4.getString("c");
                    String nikkei_diff_per = obj4.getString("cp");
                    String nikkei_time = obj4.getString("lt");


                Map<String, String> node_nikkei = new HashMap<String, String>();
                node_nikkei.put("title", "NIKKEI");
                node_nikkei.put("indexpoint", nikkei);
                node_nikkei.put("diff",nikkei_diff+" ( "+nikkei_diff_per+" % )");
                node_nikkei.put("time", nikkei_time);

                    ///===mcx=====

                    JSONObject obj5 = array_res.getJSONObject(4);
                    String mcx = obj5.getString("l_cur");
                    mcx= Html.fromHtml((String) mcx).toString();
                    String mcx_diff = obj5.getString("c");
                    String mcx_diff_per = obj5.getString("cp");
                    String mcx_time = obj5.getString("lt");


                Map<String, String> node_mcx = new HashMap<String, String>();
                node_mcx.put("title", "MCX");
                node_mcx.put("indexpoint", mcx);
                node_mcx.put("diff",mcx_diff+" ( "+mcx_diff_per+" % )");
                node_mcx.put("time", mcx_time);
                //=================================================================

                index_root.put("5",node_mcx);
                index_root.put("4",node_nikkei);
                index_root.put("3",node_nasdaq);
                index_root.put("2", node_nifty);
                index_root.put("1", node_sensex);





                databaseReference.setValue(index_root);




            }

            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            //pg_bar.setVisibility(View.GONE);





        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("firebase TAG", "onConnectionFailed:" + connectionResult);
        Toast.makeText(getContext(), "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

}
