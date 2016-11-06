package in.gm.instaqueue.tokens;

import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.HashMap;
import java.util.List;

import in.gm.instaqueue.R;

/**
 * Created by ngonapa on 11/5/2016.
 */



public class CustomFieldAdapter extends RecyclerView.Adapter<CustomFieldAdapter.ViewHolder> {

    private List<Pair<String, String>> mCustomValues;


    public CustomFieldAdapter(List<Pair<String,String>> items) {
        setList(items);

    }

    public void replaceData(List<Pair<String,String>> tokens) {
        setList(tokens);
        notifyDataSetChanged();
    }

    private void setList(List<Pair<String,String>> tokens) {
        mCustomValues = tokens;
    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_custom_fields, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mCustomName.setText(mCustomValues.get(position).first);
        holder.mCustomValue.setText(mCustomValues.get(position).second);

    }


    @Override
    public int getItemCount() {
        return mCustomValues.size();
    }

    public  void AddValue(Pair<String,String> newPair)
    {
        mCustomValues.add(newPair);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected EditText mCustomName;
        protected EditText mCustomValue;

        public ViewHolder(View view) {
            super(view);
            mCustomName = (EditText) view.findViewById(R.id.customName);
            mCustomValue = (EditText) view.findViewById(R.id.customValue);
        }
    }
}