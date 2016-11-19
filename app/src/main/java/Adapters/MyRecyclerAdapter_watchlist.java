package Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import DB.DatabaseHelper;
import DB.DatabaseHelper_Compnies;
import manager.trade.techno.trademanager.DataObject_Watchlist;
import manager.trade.techno.trademanager.FontChangeCrawler;
import manager.trade.techno.trademanager.MyScaler;
import manager.trade.techno.trademanager.R;
import manager.trade.techno.trademanager.Watchlist_Firebase;

public class MyRecyclerAdapter_watchlist extends RecyclerView
        .Adapter<MyRecyclerAdapter_watchlist
        .DataObject_postHolder> {

    DatabaseHelper_Compnies dbh;
    SQLiteDatabase db;
    Cursor c,mCursor;

    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private static ArrayList<DataObject_Watchlist> mDataset;
    private static Context mContext;

     static SharedPreferences sharepref;

    DatabaseHelper dbh2;
    SQLiteDatabase db2;




    public static class DataObject_postHolder extends RecyclerView.ViewHolder
           {
        TextView tv_title_companyname, tv_company_shortcode, tv_livepoints, tv_pointdiff, tv_pointdiff_per,tv_lastupdate,tv_previousclose;
        CardView cardview;
        ImageView img_share,img_updown,img_delete;

        public DataObject_postHolder(final View itemView) {
            super(itemView);


            tv_title_companyname = (TextView) itemView.findViewById(R.id.tv_title_companyname);
            tv_company_shortcode = (TextView) itemView.findViewById(R.id.tv_company_shortcode);
            tv_livepoints = (TextView) itemView.findViewById(R.id.tv_livepoints);
            tv_pointdiff = (TextView) itemView.findViewById(R.id.tv_pointdiff);
            tv_pointdiff_per = (TextView) itemView.findViewById(R.id.tv_pointdiff_per);
            tv_previousclose = (TextView) itemView.findViewById(R.id.tv_previousclose);
            tv_lastupdate = (TextView) itemView.findViewById(R.id.tv_lastupdate);



            img_updown=(ImageView) itemView.findViewById(R.id.img_updown);
            img_share=(ImageView) itemView.findViewById(R.id.img_share);
            img_delete=(ImageView) itemView.findViewById(R.id.img_delete);
            cardview=(CardView) itemView.findViewById(R.id.card_view);

            img_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {


                    AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                    alertbox.setMessage("Item will be removed from your WatchList.");
                    alertbox.setTitle("Delete Item ?");
                    alertbox.setIcon(R.drawable.appicon);

                    alertbox.setNeutralButton("Delete",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface arg0,int arg1) {

                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("watchlist");
                                    Query applesQuery = databaseReference.child(sharepref.getString("key_usermobno","na"))
                                            .child(mDataset.get(getAdapterPosition()).getCompany_code());
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
                                    Toast.makeText(mContext, "Deleted...", Toast.LENGTH_LONG).show();
                                    cardview.setVisibility(View.GONE);
                                    view.startAnimation(new MyScaler(1.0f, 1.0f, 1.0f, 0.0f, 500, view, true));
                                    //((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.frame, new Watchlist_Firebase()).commit();
                                }
                            });
                    alertbox.show();


                }
            });






        }


    }

   /* public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }*/

    public MyRecyclerAdapter_watchlist(ArrayList<DataObject_Watchlist> myDataset) {
        mDataset = myDataset;


    }

    @Override
    public DataObject_postHolder onCreateViewHolder(ViewGroup parent,
                                                    int viewType) {



        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_watchlist_cardview, parent, false);

         mContext = parent.getContext();



        FontChangeCrawler fontChanger = new FontChangeCrawler(mContext.getAssets(), "fonts/ProductSans-Regular.ttf");
        //fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
        //==2) for fragment hoy to====
        //== fontChanger.replaceFonts((ViewGroup) this.getView());
        //===3) for adepterview and handlerview na use mate====
        //==convertView = inflater.inflate(R.layout.listitem, null);
        fontChanger.replaceFonts((ViewGroup)view);

        DataObject_postHolder dataObjectHolder = new DataObject_postHolder(view);



        return dataObjectHolder;

    }

    @Override
    public void onBindViewHolder(final DataObject_postHolder holder,final int position) {

        sharepref = mContext.getApplicationContext().getSharedPreferences("MyPref",mContext.MODE_PRIVATE);


        holder.tv_title_companyname.setText(mDataset.get(position).getFull_name());
        holder.tv_livepoints.setText(mDataset.get(position).getCurrent_index());
        holder.tv_pointdiff.setText("("+mDataset.get(position).getDiff_index()+")");
        if(mDataset.get(position).getDiff_index().contains("+")){
            holder.img_updown.setImageResource(R.drawable.upmarket);
        }else{
            holder.img_updown.setImageResource(R.drawable.downmarket);
        }
        holder.tv_pointdiff_per.setText(mDataset.get(position).getDiff_per_index()+" % ");
        holder.tv_previousclose.setText(mDataset.get(position).getPreivous_close());
        holder.tv_lastupdate.setText(mDataset.get(position).getTime_index());


        // Log.i(LOG_TAG, "Adding Listener");
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  Toast.makeText(v.getContext(),"itemcode="+mDataset.get(position).getContestid(),Toast.LENGTH_LONG).show();

               /* Intent edit = new Intent(v.getContext(),Details_event.class);
                edit.putExtra("eventid",mDataset.get(position).getContestid());
                mContext.startActivity(edit);*/
            }
        });



        holder.img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentshare = new Intent(Intent.ACTION_SEND);
                intentshare.setType("text/plain");
                intentshare.putExtra(Intent.EXTRA_TEXT, "Event Details "
                        +"\nTitle : "+holder.tv_title_companyname.getText().toString()
                        +"\nWebHost : "+holder.tv_company_shortcode.getText().toString()
                        +"\nURL : "+holder.tv_livepoints.getText().toString()

                        +"\nfrom Coding Contests.\n\n"
                        + "https://play.google.com/store/apps/details?id=com.techno.jay.codingcontests&hl=en");
                mContext.startActivity(Intent.createChooser(intentshare, "Share"));

            }
        });





    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }



}