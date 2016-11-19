package MyFirebase;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import manager.trade.techno.trademanager.R;

public class Stockindex_RecyclerViewHolders extends RecyclerView.ViewHolder{
    private static final String TAG = Stockindex_RecyclerViewHolders.class.getSimpleName();
    public TextView title;
    public TextView indexpoint;
    public TextView diff,time;
    public ImageView imgindex;
    public List<Stockindex> stockindex_object;
    public Stockindex_RecyclerViewHolders(final View itemView, final List<Stockindex> stockindex_object) {
        super(itemView);
        this.stockindex_object = stockindex_object;
        title = (TextView)itemView.findViewById(R.id.title);
        indexpoint = (TextView)itemView.findViewById(R.id.indexpoint);
        diff = (TextView)itemView.findViewById(R.id.diff);
        time = (TextView)itemView.findViewById(R.id.time);
        imgindex = (ImageView) itemView.findViewById(R.id.img_index);


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