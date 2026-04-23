package com.cyberpower.functiontest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * DrawerAdapter
 * 兩層 Drawer 導航的適配器
 */
public class DrawerAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<MainActivity.DrawerGroup> data;
    private LayoutInflater inflater;

    public DrawerAdapter(Context context, List<MainActivity.DrawerGroup> data) {
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        return data.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return data.get(groupPosition).children.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return data.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return data.get(groupPosition).children.get(childPosition);
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
        GroupViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.drawer_group_item, parent, false);
            holder = new GroupViewHolder();
            holder.tvGroupName = convertView.findViewById(R.id.tv_group_name);
            holder.tvIndicator = convertView.findViewById(R.id.tv_indicator);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }

        MainActivity.DrawerGroup group = data.get(groupPosition);
        holder.tvGroupName.setText(group.name);

        // 如果有子項目，顯示指示器
        if (group.children.isEmpty()) {
            holder.tvIndicator.setVisibility(View.GONE);
        } else {
            holder.tvIndicator.setVisibility(View.VISIBLE);
            holder.tvIndicator.setText(isExpanded ? "▼" : "▶");
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                            View convertView, ViewGroup parent) {
        ChildViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.drawer_child_item, parent, false);
            holder = new ChildViewHolder();
            holder.tvChildName = convertView.findViewById(R.id.tv_child_name);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }

        String childName = data.get(groupPosition).children.get(childPosition);
        holder.tvChildName.setText(childName);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class GroupViewHolder {
        TextView tvGroupName;
        TextView tvIndicator;
    }

    static class ChildViewHolder {
        TextView tvChildName;
    }
}

