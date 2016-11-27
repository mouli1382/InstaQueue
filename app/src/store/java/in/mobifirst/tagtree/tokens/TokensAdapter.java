package in.mobifirst.tagtree.tokens;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.model.Token;
import in.mobifirst.tagtree.util.TimeUtils;


public class TokensAdapter extends RecyclerView.Adapter<TokensAdapter.ViewHolder> {

    private List<Token> mTokens;
    private TokensFragment.TokenItemListener mTokenItemListener;
    private Context mContext;

    public TokensAdapter(Context context, List<Token> items, TokensFragment.TokenItemListener tokenItemListener) {
        mContext = context;
        setList(items);
        mTokenItemListener = tokenItemListener;
    }

    public TokensAdapter(List<Token> items) {
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
                .inflate(R.layout.item_token, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Token token = mTokens.get(position);

        if (token.isActive()) {
            holder.mTokenNumber.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        } else {
            holder.mTokenNumber.setTextColor(mContext.getResources().getColor(R.color.common_google_signin_btn_text_dark_focused));
        }

        holder.mTokenNumber.setText(token.getTokenNumber() + "");
        holder.mTokenBuzzButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call token Buzz
                mTokenItemListener.onActivateTokenClick(token);
            }
        });
        holder.mTokenCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call tokenComplete
                mTokenItemListener.onCompleteTokenClick(token);
            }
        });

        if (!TextUtils.isEmpty(token.getUserName())) {
            holder.mSenderName.setText(token.getUserName());
        } else {
            holder.mSenderName.setText(token.getPhoneNumber());
        }
        holder.mTokenDate.setText(TimeUtils.getDate(token.getTimestamp()));
        holder.mTokenTime.setText(TimeUtils.getTime(token.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        if (mTokens == null)
            return 0;
        return mTokens.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView mTokenNumber;
        protected TextView mSenderName;
        protected TextView mTokenDate;
        protected TextView mTokenTime;
        protected ImageButton mTokenBuzzButton;
        protected ImageButton mTokenCompleteButton;

        public ViewHolder(View view) {
            super(view);
            mTokenNumber = (TextView) view.findViewById(R.id.tokenNumber);
            mSenderName = (TextView) view.findViewById(R.id.tokenCustomerName);
            mTokenDate = (TextView) view.findViewById(R.id.tokenDate);
            mTokenTime = (TextView) view.findViewById(R.id.tokenTime);
            mTokenBuzzButton = (ImageButton) view.findViewById(R.id.imageBuzzButton);
            mTokenCompleteButton = (ImageButton) view.findViewById(R.id.imageCompleteButton);
        }
    }
}
