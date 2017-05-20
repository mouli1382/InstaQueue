package in.mobifirst.tagtree.tokens;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.display.TTSHelper;
import in.mobifirst.tagtree.model.Token;

public class DisplayAdapter extends RecyclerView.Adapter<DisplayAdapter.ViewHolder> {

    private List<Snap> mSnaps;
    private Context mContext;
    private long currentActiveToken;

    public DisplayAdapter(Context context) {
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
                .inflate(R.layout.adapter_snap_display, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Snap snap = mSnaps.get(position);

        String counter = snap.getTimeRange();
        List<Token> tokens = new ArrayList<>(snap.getTokenList());

        holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder
                .recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerView.setOnFlingListener(null);
        new LinearSnapHelper().attachToRecyclerView(holder.recyclerView);

        if (getItemCount() == 1) {
            holder.snapTextView.setVisibility(View.INVISIBLE);
            int lastActivatedTokenIndex = getLastActivatedTokenIndex(tokens);
            Log.e("lastActivatedTokenIndex", "INDEX = " + lastActivatedTokenIndex);
            if (lastActivatedTokenIndex != -1) {
                long activeToken = tokens.get(lastActivatedTokenIndex).getTokenNumber();
                holder.mTokenNumber.setText(activeToken + "");
                if (Long.valueOf(activeToken).compareTo(currentActiveToken) != 0) {
                    TTSHelper.getInstance().speak("Token number " + activeToken, mContext);
                    currentActiveToken = activeToken;
                }
                holder.mCardView.setBackgroundColor(Color.parseColor("#FFD600"));
                holder.mTokenNumber.setTextColor(mContext.getResources().getColor(R.color.common_google_signin_btn_text_dark_focused));
//                holder.name.setTextColor(Color.GREEN);
                holder.mCardView.setVisibility(View.VISIBLE);
                if (tokens.size() > lastActivatedTokenIndex + 1) {
                    holder.mNextInLineTextView.setVisibility(View.VISIBLE);
                } else {
                    holder.mNextInLineTextView.setVisibility(View.GONE);
                }
                holder.recyclerView.setAdapter(new TokensIssueDisplayAdapter(mContext, tokens.size() > lastActivatedTokenIndex + 1 ? tokens.subList(lastActivatedTokenIndex + 1, tokens.size()) : new ArrayList<Token>()));
            } else {
                holder.mCardView.setVisibility(View.GONE);
                holder.recyclerView.setAdapter(new TokensIssueDisplayAdapter(mContext, tokens));
            }
        } else {
            holder.mCardView.setVisibility(View.GONE);
            holder.mNextInLineTextView.setVisibility(View.GONE);
            holder.snapTextView.setText("Counter " + counter);
            holder.snapTextView.setVisibility(View.VISIBLE);
            holder.recyclerView.setAdapter(new TokensIssueDisplayAdapter(mContext, tokens.size() > 3 ? tokens.subList(0, 3) : tokens));
        }
    }

    private int getLastActivatedTokenIndex(List<Token> tokenList) {
        int index = -1;
        if (tokenList == null || tokenList.size() == 0)
            return index;

        for (int i = tokenList.size() - 1; i >= 0; --i) {
            Token token = tokenList.get(i);
            if (token.isActive()) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public int getItemCount() {
        return mSnaps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView snapTextView;
        public RecyclerView recyclerView;
        protected TextView mTokenNumber;
        protected View mCardView;
        protected TextView mNextInLineTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            snapTextView = (TextView) itemView.findViewById(R.id.snapTextView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
            mTokenNumber = (TextView) itemView.findViewById(R.id.token_number);
            mCardView = itemView.findViewById(R.id.issueTokenCard);
            mNextInLineTextView = (TextView) itemView.findViewById(R.id.nextInLine);
        }

    }
}

