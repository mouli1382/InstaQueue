package in.mobifirst.tagtree.tokens;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.model.Token;


public class TokensIssueDisplayAdapter extends RecyclerView.Adapter<TokensIssueDisplayAdapter.ViewHolder> {

    private List<Token> mTokens;
    private TokensFragment.TokenItemListener mTokenItemListener;
    private Context mContext;

    public TokensIssueDisplayAdapter(List<Token> items, TokensFragment.TokenItemListener tokenItemListener) {
        setList(items);
        mTokenItemListener = tokenItemListener;
    }

    public TokensIssueDisplayAdapter(Context context, List<Token> items) {
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
                .inflate(R.layout.issue_token_display, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Token token = mTokens.get(position);

        if (token.isActive()) {
            holder.mTokenNumber.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
//            holder.mView.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_green_dark));
        } else {
//            holder.mView.setBackgroundColor(mContext.getResources().getColor(R.color.cardview_light_background));
            holder.mTokenNumber.setTextColor(mContext.getResources().getColor(R.color.common_google_signin_btn_text_dark_focused));
        }
        holder.mTokenNumber.setText(token.getTokenNumber() + "");
//        holder.mTime.setText(TimeUtils.getTime(token.getTimestamp()));
//        holder.mDate.setText(TimeUtils.getDate(token.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return mTokens.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView mTokenNumber;
        //        protected TextView mTime;
//        protected TextView mDate;
        protected View mView;

        public ViewHolder(View view) {
            super(view);
            mTokenNumber = (TextView) view.findViewById(R.id.token_number);
            mView = view;
//            mTime = (TextView) view.findViewById(R.id.time);
//            mDate = (TextView) view.findViewById(R.id.date);
        }
    }
}
