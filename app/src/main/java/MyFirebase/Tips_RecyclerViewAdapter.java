package MyFirebase;



import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import manager.trade.techno.trademanager.FontChangeCrawler;
import manager.trade.techno.trademanager.R;

public class Tips_RecyclerViewAdapter extends RecyclerView.Adapter<Tips_RecyclerViewHolders> {
    private List<Tips_dataobject> tips_dataobject;
    protected Context context;
    public Tips_RecyclerViewAdapter(Context context, List<Tips_dataobject> tips_dataobject) {
        this.tips_dataobject = tips_dataobject;
        this.context = context;
    }
    @Override
    public Tips_RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        Tips_RecyclerViewHolders viewHolder = null;
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tips_raw_layout, parent, false);
        viewHolder = new Tips_RecyclerViewHolders(layoutView, tips_dataobject);

        FontChangeCrawler fontChanger = new FontChangeCrawler(context.getAssets(), "fonts/ProductSans-Regular.ttf");
        fontChanger.replaceFonts((ViewGroup)layoutView);



        return viewHolder;
    }
    @Override
    public void onBindViewHolder(Tips_RecyclerViewHolders holder, int position) {
        holder.title.setText(tips_dataobject.get(position).getTitle());
        holder.details.setText(tips_dataobject.get(position).getDetails());
        holder.time.setText(tips_dataobject.get(position).getTime());

    }
    @Override
    public int getItemCount() {
        return this.tips_dataobject.size();
    }
}