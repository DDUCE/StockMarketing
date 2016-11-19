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

public class Stockindex_RecyclerViewAdapter extends RecyclerView.Adapter<Stockindex_RecyclerViewHolders> {
    private List<Stockindex> stockindex;
    protected Context context;
    public Stockindex_RecyclerViewAdapter(Context context, List<Stockindex> stockindex) {
        this.stockindex = stockindex;
        this.context = context;
    }
    @Override
    public Stockindex_RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        Stockindex_RecyclerViewHolders viewHolder = null;
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.index_raw_layout, parent, false);
        viewHolder = new Stockindex_RecyclerViewHolders(layoutView, stockindex);

        FontChangeCrawler fontChanger = new FontChangeCrawler(context.getAssets(), "fonts/ProductSans-Regular.ttf");
        fontChanger.replaceFonts((ViewGroup)layoutView);



        return viewHolder;
    }
    @Override
    public void onBindViewHolder(Stockindex_RecyclerViewHolders holder, int position) {
        holder.title.setText(stockindex.get(position).getTitle());
        holder.indexpoint.setText(stockindex.get(position).getIndexpoint());
        holder.diff.setText(stockindex.get(position).getDiff());
        holder.time.setText(stockindex.get(position).getTime());

        if(stockindex.get(position).getDiff().contains("+")){
            holder.imgindex.setImageResource(R.drawable.upmarket);
            holder.diff.setBackgroundColor(context.getResources().getColor(R.color.md_green_A700));
        }else{
            holder.imgindex.setImageResource(R.drawable.downmarket);
            holder.diff.setBackgroundColor(Color.RED);
        }

    }
    @Override
    public int getItemCount() {
        return this.stockindex.size();
    }
}