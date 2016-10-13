package in.gm.instaqueue.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import in.gm.instaqueue.R;

public class TokenRecyclerViewHolder extends RecyclerView.ViewHolder {
    public TextView tokenTextView;
    public CircleImageView userImageView;
    public TextView tokenStoreNameView;

    public TokenRecyclerViewHolder(View v) {
        super(v);
        tokenTextView = (TextView) itemView.findViewById(R.id.tokenTextView);
        userImageView = (CircleImageView) itemView.findViewById(R.id.tokenRecyclerView);
        tokenStoreNameView = (TextView) itemView.findViewById(R.id.tokenStoreName);
    }
}
