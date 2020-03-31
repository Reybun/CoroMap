package com.example.coromap;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.coromap.mapper.PaysM;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder> {
    private ArrayList<PaysM> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView pays;
        public TextView mort;
        public TextView infected;
        public TextView nb;

        public MyViewHolder(LinearLayout v) {
            super(v);
            this.pays = v.findViewById(R.id.pays);
            this.mort = v.findViewById(R.id.mort);
            this.infected = v.findViewById(R.id.infected);
            this.nb = v.findViewById(R.id.nb);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DataAdapter(ArrayList<PaysM> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DataAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //Log.i("datasettt", ""+mDataset.get(position).getCountry() + mDataset.get(position).getLatest().getDeaths());
        holder.pays.setText(mDataset.get(position).getCountry());
        holder.mort.setText(String.valueOf(mDataset.get(position).getLatest().getDeaths()) + " Morts");
        holder.infected.setText(String.valueOf(mDataset.get(position).getLatest().getConfirmed()) + " Infectés");
        holder.nb.setText(String.valueOf(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}