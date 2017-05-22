package in.mobifirst.tagtree.tokens;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.mobifirst.tagtree.R;

public class SnapAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private SnapFragment.TokenItemListener mTokenItemListener;
    private List<Snap> mSnaps = new ArrayList<>();

//    public SnapAdapter(Context context) {
//        mContext = context;
//    }
//
//    public void replaceData(List<Snap> snaps) {
//        setList(snaps);
//        notifyDataSetChanged();
//    }
//
//    private void setList(List<Snap> snaps) {
//        mSnaps = snaps;
//    }

    public SnapAdapter(Context context, List<Snap> snaps, SnapFragment.TokenItemListener tokenItemListener) {
        this.mContext = context;
        this.mSnaps = snaps;
        mTokenItemListener = tokenItemListener;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getGroupCount() {
        return mSnaps.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mSnaps.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mSnaps.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        ParentHolder parentHolder = null;

        Snap group = (Snap) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater userInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            convertView = userInflater.inflate(R.layout.item_group, null);
            convertView.setHorizontalScrollBarEnabled(true);

            parentHolder = new ParentHolder();
            convertView.setTag(parentHolder);

        } else {
            parentHolder = (ParentHolder) convertView.getTag();
        }

        parentHolder.timeOfDay = (TextView) convertView.findViewById(R.id.text_brand);
        parentHolder.timeOfDay.setText(group.getTimeRange());

        parentHolder.indicator = (ImageView) convertView.findViewById(R.id.image_indicator);

        if (isExpanded) {
            parentHolder.indicator.setImageResource(android.R.drawable.arrow_up_float);
        } else {
            parentHolder.indicator.setImageResource(android.R.drawable.arrow_down_float);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        Snap group = (Snap) getGroup(groupPosition);

        ChildHolder childHolder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_group_child, parent, false);
            childHolder = new ChildHolder();
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }


        childHolder.recyclerView = (RecyclerView) convertView.findViewById(R.id.recyclerView);
        childHolder.recyclerView.setLayoutManager(new GridLayoutManager(childHolder
                .recyclerView.getContext(), 3, LinearLayoutManager.VERTICAL, false));
        childHolder.recyclerView.setOnFlingListener(null);
        new LinearSnapHelper().attachToRecyclerView(childHolder.recyclerView);

        childHolder.recyclerView.setAdapter(new TokensIssueAdapter(mContext, group.getTokenList(), mTokenItemListener));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return 0;
    }

    private static class ChildHolder {
        static RecyclerView recyclerView;
    }

    private static class ParentHolder {
        TextView timeOfDay;
        ImageView indicator;
    }
}
