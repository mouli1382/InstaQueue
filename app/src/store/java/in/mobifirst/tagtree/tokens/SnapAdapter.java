package in.mobifirst.tagtree.tokens;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.model.Token;

public class SnapAdapter extends RecyclerView.Adapter<SnapAdapter.ViewHolder> implements GravitySnapHelper.SnapListener {

    //    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;

    private Map<Integer, Collection<Token>> mTokenMap;
    //    private ArrayList<Snap> mTokenMap;
    // Disable touch detection for parent recyclerView if we use vertical nested recyclerViews
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        }
    };

    public SnapAdapter() {
        mTokenMap = new HashMap<>();
    }

    public void replaceData(Map<Integer, Collection<Token>> tokenMap) {
        setMap(tokenMap);
        notifyDataSetChanged();
    }

    private void setMap(Map<Integer, Collection<Token>> tokenMap) {
        mTokenMap = tokenMap;
    }

    @Override
    public int getItemViewType(int position) {
//        Snap snap = mTokenMap.get(position);
//        switch (snap.getGravity()) {
//            case Gravity.CENTER_VERTICAL:
//                return VERTICAL;
//            case Gravity.CENTER_HORIZONTAL:
//                return HORIZONTAL;
//            case Gravity.START:
//                return HORIZONTAL;
//            case Gravity.TOP:
//                return VERTICAL;
//            case Gravity.END:
//                return HORIZONTAL;
//            case Gravity.BOTTOM:
//                return VERTICAL;
//        }
        return HORIZONTAL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = /*viewType == VERTICAL ? LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_snap_vertical, parent, false)
                :*/ LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_snap, parent, false);

//        if (viewType == VERTICAL) {
//            view.findViewById(R.id.recyclerView).setOnTouchListener(mTouchListener);
//        }

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        List<Token> tokens = new ArrayList<>(mTokenMap.get(position));
        holder.snapTextView.setText("Counter " + (tokens.get(0).getCounter() + 1));

//        if (snap.getGravity() == Gravity.START /*|| snap.getGravity() == Gravity.END*/) {
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder
                .recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerView.setOnFlingListener(null);
        new LinearSnapHelper().attachToRecyclerView(holder.recyclerView);
//            new GravitySnapHelper(Gravity.START, false, this).attachToRecyclerView(holder.recyclerView);
        /*} else if (snap.getGravity() == Gravity.CENTER_HORIZONTAL
                || snap.getGravity() == Gravity.CENTER_VERTICAL
                || snap.getGravity() == Gravity.CENTER) {
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder
                    .recyclerView.getContext(), snap.getGravity() == Gravity.CENTER_HORIZONTAL ?
                    LinearLayoutManager.HORIZONTAL : LinearLayoutManager.VERTICAL, false));
            holder.recyclerView.setOnFlingListener(null);
            new LinearSnapHelper().attachToRecyclerView(holder.recyclerView);
        } else { // Top / Bottom
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder
                    .recyclerView.getContext()));
            holder.recyclerView.setOnFlingListener(null);
            new GravitySnapHelper(snap.getGravity()).attachToRecyclerView(holder.recyclerView);
        }*/

        holder.recyclerView.setAdapter(new TokensIssueAdapter(/*snap.getGravity() == Gravity.START
                || snap.getGravity() == Gravity.END
                || snap.getGravity() == Gravity.CENTER_HORIZONTAL,*/ tokens));
    }

    @Override
    public int getItemCount() {
        return mTokenMap.size();
    }

    @Override
    public void onSnap(int position) {
        Log.d("Snapped: ", position + "");
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

