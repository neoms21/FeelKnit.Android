package com.qubittech.feelknit.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.qubittech.feelknit.app.R;
import com.qubittech.feelknit.util.ApplicationHelper;
import com.qubittech.feelknit.util.ImageHelper;

/**
 * Created by Manoj on 16/11/2014.
 */
public class AvatarListAdapter extends BaseAdapter {

    Context context;
    private String[] images;
    private String avatarId;

    /*private view holder class*/
    private class ViewHolder {
        ImageView avatarImageView;
    }

    public AvatarListAdapter(Context context, int resource, String[] images) {

        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int i) {
        return images[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        avatarId = (String)getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.avatar_listview_item, null);
            holder = new ViewHolder();
            holder.avatarImageView = (ImageView) convertView.findViewById(R.id.avatarImage);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        if(position == 6)
        {
            holder.avatarImageView.setBackgroundColor(Color.GREEN);
        }


        ImageHelper.setBitMap(holder.avatarImageView, context, avatarId, 100, 100);
        return convertView;
    }
}
