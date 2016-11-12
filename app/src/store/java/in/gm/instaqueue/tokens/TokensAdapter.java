package in.mobifirst.tagtree.tokens;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
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

        try {
            if (token.getSenderPic() == null)
                return;
                //holder.mSenderPic.setImageURI(Uri.parse(token.getSenderPic()));
            URL url = new URL(token.getSenderPic());
            new DownloadImageTask(holder.mSenderPic)
                    .execute(token.getSenderPic());

        } catch(IOException e) {
            System.out.println(e);
        }
        holder.mSenderPic.invalidate();
        holder.mSenderName.setText(token.getSenderName());
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
        protected TextView mSenderName;
        protected ImageView mSenderPic;

        public ViewHolder(View view) {
            super(view);
            mTokenNumber = (TextView) view.findViewById(R.id.tokenTextView);
            mSenderName = (TextView) view.findViewById(R.id.senderName);
            mSenderPic = (ImageView) view.findViewById(R.id.senderImage);
        }
    }
}
