package MyFirebase;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import manager.trade.techno.trademanager.R;

public class Tips_RecyclerViewHolders extends RecyclerView.ViewHolder{
    private static final String TAG = Tips_RecyclerViewHolders.class.getSimpleName();
    public TextView title;
    public TextView details;
    public TextView time;
    public List<Tips_dataobject> tips_dataobject;

    public Tips_RecyclerViewHolders(final View itemView, final List<Tips_dataobject> tips_dataobject) {
        super(itemView);
        this.tips_dataobject = tips_dataobject;
        title = (TextView)itemView.findViewById(R.id.tv_title_tips);
        details = (TextView)itemView.findViewById(R.id.tv_details_tips);
        time = (TextView)itemView.findViewById(R.id.tv_time_tips);


        // ** thi s code is for delet data to firebase by adapter view cliclk event===
        /*deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Delete icon has been clicked", Toast.LENGTH_LONG).show();
                String taskTitle = stockindex_object.get(getAdapterPosition()).getTitle();
                Log.d(TAG, "Stockindex Title " + taskTitle);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query applesQuery = ref.orderByChild("task").equalTo(taskTitle);
                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled", databaseError.toException());
                    }
                });
            }
        });*/
    }
}