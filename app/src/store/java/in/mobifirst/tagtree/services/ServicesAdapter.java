package in.mobifirst.tagtree.services;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.model.Service;


public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ViewHolder> {

    private List<Service> mServices;
    private ServicesFragment.ServiceItemListener mServiceItemListener;
    private Context mContext;

    public ServicesAdapter(Context context, List<Service> items, ServicesFragment.ServiceItemListener serviceItemListener) {
        mContext = context;
        setList(items);
        mServiceItemListener = serviceItemListener;
    }

    public void replaceData(List<Service> tokens) {
        setList(tokens);
        notifyDataSetChanged();
    }

    private void setList(List<Service> tokens) {
        mServices = tokens;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Service service = mServices.get(position);

        holder.name.setText(service.getName());
        holder.description.setText(service.getDescription());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mServiceItemListener.onServiceClick(service);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mServices == null)
            return 0;
        return mServices.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView name;
        protected TextView description;
        protected View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            name = (TextView) view.findViewById(R.id.name);
            description = (TextView) view.findViewById(R.id.description);
        }
    }
}
