package manager.trade.techno.trademanager;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import DB.DatabaseHelper;
import DB.DatabaseHelper_Compnies;

public class MyRecyclerAdapter_watchlist extends RecyclerView
        .Adapter<MyRecyclerAdapter_watchlist
        .DataObject_postHolder> {

    DatabaseHelper_Compnies dbh;
    SQLiteDatabase db;
    Cursor c,mCursor;

    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private static ArrayList<DataObject_Watchlist> mDataset;
    private static Context mContext;

     SharedPreferences sharepref;

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

        try {



            dbh = new DatabaseHelper_Compnies(mContext);
            db = dbh.getWritableDatabase();
            // Select All Query

            String selectQuery =  "SELECT  * FROM listingcompanies WHERE SecurityCode = '" + mDataset.get(position).getCompany_code() + "';";
            //Log.i("TAG", selectQuery);
            //Log.i("TAG day", selectQuery);
             mCursor = db.rawQuery(selectQuery, null);

                        /*String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.DURATION, DatabaseHelper.END,DatabaseHelper.START, DatabaseHelper.EVENT
                                ,DatabaseHelper.HREF, DatabaseHelper.CONTEST_ID,DatabaseHelper.RESOURCE_ID, DatabaseHelper.RESOURCE_NAME};
                        //Cursor mCursor = db.query(DatabaseHelper.TABLE_NAME, columns,null,  null, null, null, null);*/


            if (mCursor.moveToFirst()) {
                do {
                    String company_short_code = mCursor.getString(mCursor.getColumnIndex("SecurityId"));
                    String company_full_name = mCursor.getString(mCursor.getColumnIndex("SecurityName"));
                    //String company_code = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.company_code));

                    holder.tv_title_companyname.setText(company_full_name);
                    holder.tv_company_shortcode.setText(company_short_code);

                } while (mCursor.moveToNext());
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

        holder.tv_livepoints.setText(mDataset.get(position).getCompany_code().replace("Rs.",""));
        holder.tv_pointdiff.setText("("+mDataset.get(position).getDiff_index()+")");
        if(mDataset.get(position).getDiff_index().contains("+")){
            holder.img_updown.setImageResource(R.drawable.upmarket);
        }else{
            holder.img_updown.setImageResource(R.drawable.downmarket);
        }
        holder.tv_pointdiff_per.setText(mDataset.get(position).getDiff_per_index()+" % ");
        holder.tv_previousclose.setText(mDataset.get(position).getPreivous_close());
        holder.tv_lastupdate.setText(mDataset.get(position).getPreivous_close());


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


        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {


                AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                alertbox.setMessage("Item will be removed from your WatchList.");
                alertbox.setTitle("Delete Item ?");
                alertbox.setIcon(R.drawable.appicon);

                alertbox.setNeutralButton("Delete",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0,int arg1) {


                                try {

                                    dbh2 = new DatabaseHelper(mContext);
                                    db2 = dbh2.getWritableDatabase();


                                    String deletequery = "DELETE FROM " + DatabaseHelper.TABLE_NAME +" WHERE " + DatabaseHelper.company_code + " = '" + mDataset.get(position).getCompany_code() + "';";
                                   Log.i("TAG", deletequery);
                                    mCursor = db2.rawQuery(deletequery, null);
                                    mCursor.moveToFirst();
                                    mCursor.close();






                                }catch (Exception Esql){
                                    Esql.printStackTrace();
                                }finally {
                                    if (mCursor != null && !mCursor.isClosed())
                                        mCursor.close();
                                    db2.close();

                                }
                                Toast.makeText(mContext, "Deleted...", Toast.LENGTH_LONG).show();
                                holder.cardview.setVisibility(View.GONE);
                                view.startAnimation(new MyScaler(1.0f, 1.0f, 1.0f, 0.0f, 500, view, true));

                            }
                        });
                alertbox.show();


            }
        });


    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }



}