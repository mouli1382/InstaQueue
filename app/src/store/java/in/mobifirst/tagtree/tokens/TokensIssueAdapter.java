package in.mobifirst.tagtree.tokens;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.model.Token;
import in.mobifirst.tagtree.util.TimeUtils;


public class TokensIssueAdapter extends RecyclerView.Adapter<TokensIssueAdapter.ViewHolder> {

    private List<Token> mTokens;
    private SnapFragment.TokenItemListener mTokenItemListener;
    private Context mContext;

    public TokensIssueAdapter(Context context, List<Token> items, SnapFragment.TokenItemListener tokenItemListener) {
        mContext = context;
        setList(items);
        mTokenItemListener = tokenItemListener;
    }

    public TokensIssueAdapter(Context context, List<Token> items) {
        mContext = context;
        setList(items);
    }

    public void replaceData(List<Token> tokens) {
        setList(tokens);
        notifyDataSetChanged();
    }

    private void setList(List<Token> tokens) {
        mTokens = tokens;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.issue_token, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Token token = mTokens.get(position);

        if (token.isActive()) {
            holder.mCardView.setCardBackgroundColor(Color.parseColor("#1B5E20"));
        } else if (token.isCompleted()) {
            holder.mCardView.setCardBackgroundColor(Color.parseColor("#607D8B"));
        } else if (token.isIssued()) {
            holder.mCardView.setCardBackgroundColor(Color.parseColor("#673AB7"));
        }

        if (token.isUnknown()) {
            holder.mCardView.setEnabled(true);
            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTokenItemListener.onTokenClick(token);
                }
            });
        } else {
            holder.mCardView.setEnabled(false);
        }

        holder.mTokenNumber.setText(token.getTokenNumber() + "");
        holder.mTime.setText(TimeUtils.getTime(token.getAppointmentTime()));
        holder.mDate.setText(TimeUtils.getDate(token.getDate()));
    }

    @Override
    public int getItemCount() {
        return mTokens.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView mTokenNumber;
        protected TextView mTime;
        protected TextView mDate;
        protected CardView mCardView;

        public ViewHolder(View view) {
            super(view);
            mCardView = (CardView) view;
            mTokenNumber = (TextView) view.findViewById(R.id.token_number);
            mTime = (TextView) view.findViewById(R.id.time);
            mDate = (TextView) view.findViewById(R.id.date);
        }
    }
}
