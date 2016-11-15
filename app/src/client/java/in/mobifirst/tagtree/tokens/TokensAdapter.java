package in.mobifirst.tagtree.tokens;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.model.Token;
import in.mobifirst.tagtree.util.TimeUtils;

public class TokensAdapter extends RecyclerView.Adapter<TokensAdapter.ViewHolder> {

    private List<Token> mTokens;

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
        Token token = mTokens.get(position);
        holder.mTokenNumber.setText(token.getTokenNumber() + "");
        holder.mStoreName.setText(token.getSenderName() + "");

        Glide.with(holder.mImageView.getContext()).load(token.getSenderPic())
                .centerCrop().placeholder(R.drawable.ic_account_circle_black_36dp).crossFade()
                .into(holder.mImageView);

        holder.mDate.setText(TimeUtils.getDate(token.getTimestamp()));
        holder.mCounterNumber.setText(token.getCounter());

        /*holder.mChronoMeter.setFormat("HH:mm:ss");
        holder.mChronoMeter.setBase(token.getTimestamp());
        holder.mChronoMeter.start();*/
    }

    @Override
    public int getItemCount() {
        return mTokens.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView mTokenNumber;
        protected TextView mStoreName;
        protected ImageView mImageView;
        protected EditText mDate;
        protected TextView mCounterNumber;

        public ViewHolder(View view) {
            super(view);
            mTokenNumber = (TextView) view.findViewById(R.id.tokenNumberText);
            mDate = (EditText) view.findViewById(R.id.tokenDate);
            mStoreName = (TextView) view.findViewById(R.id.tokenStoreName);
            mImageView = (ImageView) view.findViewById(R.id.storeImageView);
            mTokenNumber = (TextView) view.findViewById(R.id.counterToken);
        }
    }
}
