package in.mobifirst.tagtree.tokens;


import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.model.Token;

public class SnapAdapter extends RecyclerView.Adapter<SnapAdapter.ViewHolder> {

    private List<Snap> mSnaps;
    private Context mContext;

    public SnapAdapter(Context context) {
        mContext = context;
        mSnaps = new ArrayList<>();
    }

    public void replaceData(List<Snap> snaps) {
        setList(snaps);
        notifyDataSetChanged();
    }

    private void setList(List<Snap> snaps) {
        mSnaps = snaps;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_snap, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Snap snap = mSnaps.get(position);

        int counter = snap.getCounter();
        List<Token> tokens = new ArrayList<>(snap.getTokenList());

        holder.snapTextView.setText("Counter " + counter);

//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
//        holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder
//                .recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerView.setLayoutManager(new GridLayoutManager(holder
                .recyclerView.getContext(), 3, LinearLayoutManager.VERTICAL, false));
        holder.recyclerView.setOnFlingListener(null);
        new LinearSnapHelper().attachToRecyclerView(holder.recyclerView);

        holder.recyclerView.setAdapter(new TokensIssueAdapter(mContext, tokens));
    }

    @Override
    public int getItemCount() {
        return mSnaps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView snapTextView;
        public RecyclerView recyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            snapTextView = (TextView) itemView.findViewById(R.id.snapTextView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
        }

    }
}

