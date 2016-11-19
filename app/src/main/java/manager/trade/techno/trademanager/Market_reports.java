package manager.trade.techno.trademanager;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import RSS.ReadRss;


/**
 * A simple {@link Fragment} subclass.
 */
public class Market_reports extends Fragment {
    RecyclerView recyclerView;

    public Market_reports() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View convertView = inflater.inflate(R.layout.fragment_market_reports, container, false);


        FloatingActionButton fab = (FloatingActionButton)convertView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        recyclerView= (RecyclerView)convertView.findViewById(R.id.recyclerview);


        // creating connection detector class instance
        boolean isInternetPresent=false;
        ConnectionDetector cd = new ConnectionDetector(getContext());
        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent) {
            // Internet Connection is Present
            // make HTTP requests
            ReadRss readRss=new ReadRss(getContext(),recyclerView,"http://www.moneycontrol.com/rss/marketreports.xml");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                readRss.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                readRss.execute();
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


        return convertView;

    }

}
