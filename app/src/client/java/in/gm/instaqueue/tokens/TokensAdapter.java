package in.mobifirst.tagtree.tokens;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.model.Token;

public class TokensAdapter extends RecyclerView.Adapter<TokensAdapter.ViewHolder> {

    private List<Token> mTokens;
    private TokensFragment.TokenItemListener mTokenItemListener;

    public TokensAdapter(List<Token> items, TokensFragment.TokenItemListener tokenItemListener) {
        setList(items);
        mTokenItemListener = tokenItemListener;
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
    }

//    private String getDateAndAuthor(String date, String author) {
//        Date parsed = null;
//        try {
//            parsed = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        StringBuilder sb = new StringBuilder(author + "\n");
//        if (parsed != null)
//            sb.append(android.text.format.DateFormat.format("MMMM dd, yyyy", parsed.getTime()));
//        else
//            sb.append(date);
//
//        return sb.toString();
//    }

    @Override
    public int getItemCount() {
        return mTokens.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView mTokenNumber;

        public ViewHolder(View view) {
            super(view);
            mTokenNumber = (TextView) view.findViewById(R.id.tokenTextView);
        }
    }
}
