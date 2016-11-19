package manager.trade.techno.trademanager;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import RSS.ReadRss;


/**
 * A simple {@link Fragment} subclass.
 */
public class News_fragment extends Fragment {

    RecyclerView recyclerView;
    public News_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View convertView = inflater.inflate(R.layout.fragment_news_fragment, container, false);


        recyclerView= (RecyclerView)convertView.findViewById(R.id.recyclerview);
        final LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);


        // creating connection detector class instance
        boolean isInternetPresent=false;
        ConnectionDetector cd = new ConnectionDetector(getContext());
        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent) {
            // Internet Connection is Present
            // make HTTP requests
            ReadRss readRss=new ReadRss(getContext(),recyclerView,"http://www.moneycontrol.com/rss/MCtopnews.xml");
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
