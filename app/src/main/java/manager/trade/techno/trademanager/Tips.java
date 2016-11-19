package manager.trade.techno.trademanager;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import MyFirebase.Tips_RecyclerViewAdapter;
import MyFirebase.Tips_dataobject;


/**
 * A simple {@link Fragment} subclass.
 */
public class Tips extends Fragment {



    private static final String TAG = "index fragment";
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Tips_RecyclerViewAdapter recyclerViewAdapter;
    private DatabaseReference databaseReference;
    private List<Tips_dataobject> allindex;


    SwipeRefreshLayout mSwipeRefreshLayout;


    String res1;
    Boolean isInternetPresent = false;



    public Tips() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for convertView fragment
        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "fonts/ProductSans-Regular.ttf");

        final View convertView = inflater.inflate(R.layout.fragment_tips, container, false);
        //==add convertView line to change all font to coustom font in fragments
        fontChanger.replaceFonts((ViewGroup)convertView);



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
                    databaseReference.orderByValue().limitToLast(100).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            getAllTask(dataSnapshot);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

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

        allindex = new ArrayList<Tips_dataobject>();
        databaseReference = FirebaseDatabase.getInstance().getReference("tips");
        databaseReference.keepSynced(true);
        recyclerView = (RecyclerView)convertView.findViewById(R.id.indexRecyclerView);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerViewAdapter = new Tips_RecyclerViewAdapter(getContext(), allindex);

        databaseReference.orderByValue().limitToLast(100).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                getAllTask(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        //================================



        return convertView;
    }

    private void getAllTask(DataSnapshot dataSnapshot){

        recyclerView.setAdapter(recyclerViewAdapter);
        allindex.clear();
        recyclerViewAdapter.notifyDataSetChanged();
        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
            //Log.e("Count " ,""+dataSnapshot.getChildrenCount());

            Tips_dataobject tips = singleSnapshot.getValue(Tips_dataobject.class);
            /*System.out.println("title: " + tips.getTitle());
            System.out.println("diff: " + tips.getDiff());*/
            allindex.add(new Tips_dataobject(tips.getTitle(),tips.getDetails(),tips.getTime()));

            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
        recyclerViewAdapter.notifyDataSetChanged();
    }


}
