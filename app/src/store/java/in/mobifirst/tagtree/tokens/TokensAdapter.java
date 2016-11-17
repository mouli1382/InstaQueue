package in.mobifirst.tagtree.tokens;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.model.Token;


public class TokensAdapter extends RecyclerView.Adapter<TokensAdapter.ViewHolder> {

    private List<Token> mTokens;
    private TokensFragment.TokenItemListener mTokenItemListener;

    public TokensAdapter(List<Token> items, TokensFragment.TokenItemListener tokenItemListener) {
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


    /*private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
           bmImage.setImageBitmap(result);
        }
    }*/

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_token, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Token token = mTokens.get(position);
        holder.mTokenNumber.setText(token.getTokenNumber() + "");
        holder.mTokenBuzzButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call token Buzz
                mTokenItemListener.onActivateTokenClick(token);
            }
        });
        holder.mTokenCompleteButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call tokenComplete
                mTokenItemListener.onCompleteTokenClick(token);
            }
        });

        /*try {
            if (token.getSenderPic() == null)
                return;
                //holder.mSenderPic.setImageURI(Uri.parse(token.getSenderPic()));
            URL url = new URL(token.getSenderPic());
            new DownloadImageTask(holder.mSenderPic)
                    .execute(token.getSenderPic());

        } catch(IOException e) {
            System.out.println(e);
        }*/

        holder.mSenderName.setText(token.getSenderName());

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(token.getTimestamp());
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();

        holder.mTokenDate.setText(date);
    }

    @Override
    public int getItemCount() {
        if (mTokens ==  null)
            return 0;
        return mTokens.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView mTokenNumber;
        protected TextView mSenderName;
        protected EditText mTokenDate;
        protected ImageButton mTokenBuzzButton;
        protected ImageButton mTokenCompleteButton;

        public ViewHolder(View view) {
            super(view);
            mTokenNumber = (TextView) view.findViewById(R.id.tokenNumber);
            mSenderName = (TextView) view.findViewById(R.id.tokenCustomerName);
            mTokenDate = (EditText) view.findViewById(R.id.tokenDate);
            mTokenBuzzButton = (ImageButton) view.findViewById(R.id.imageBuzzButton);
            mTokenCompleteButton = (ImageButton) view.findViewById(R.id.imageCompleteButton);
        }
    }
}
