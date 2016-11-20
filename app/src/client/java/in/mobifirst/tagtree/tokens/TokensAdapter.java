package in.mobifirst.tagtree.tokens;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.List;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.model.Token;
import in.mobifirst.tagtree.tokens.viewholder.FirebaseViewHolder;
import in.mobifirst.tagtree.util.TimeUtils;

public class TokensAdapter extends RecyclerView.Adapter<FirebaseViewHolder> {

    private List<Token> mTokens;
    private Context mContext;
    private TokensFragment.TokenItemListener mTokenItemListener;
    private String mActivatedTokenId;

    public TokensAdapter(Context context, List<Token> items, TokensFragment.TokenItemListener tokenItemListener) {
        mTokenItemListener = tokenItemListener;
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
    public FirebaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_token, parent, false);
        return new FirebaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FirebaseViewHolder holder, int position) {
        Token token = mTokens.get(position);
        holder.mTokenNumber.setText(token.getTokenNumber() + "");
        holder.mStoreName.setText(token.getSenderName() + "");

        Glide.with(mContext).load(token.getSenderPic())
                .centerCrop().placeholder(R.drawable.ic_account_circle_black_36dp).crossFade()
                .into(holder.mImageView);

        holder.mDate.setText(TimeUtils.getDate(token.getTimestamp()));
        holder.mTime.setText(TimeUtils.getTime(token.getTimestamp()));
        holder.mCounterNumber.setText("" + token.getCounter());
        holder.mArea.setText(token.getAreaName());

        if (token.isActive()) {
            holder.mTokenNumber.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
            mTokenItemListener.handleTokenStatus(token);
        }

//        if (bundle != null) {
//            mTokenId = bundle.getString(ApplicationConstants.TOKEN_ID_KEY);
//        }
//        if (mTokenId != null && token.getuId().equals(mTokenId)) {
//            animateTokenNumber(holder.mTokenNumber);
//        }
    }

    @Override
    public int getItemCount() {
        return mTokens.size();
    }
}
