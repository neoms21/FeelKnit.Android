package com.qubittech.feelknit.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qubittech.feelknit.app.R;
import com.qubittech.feelknit.util.ImageHelper;

import java.util.List;

/**
 * Created by Manoj on 06/02/2015.
 */
public class FeelingsListAdapter extends ArrayAdapter<String> {

    Context context;
    private String feelText;

    public static String selectedFeeling;

    /*private view holder class*/
    private class ViewHolder {
        TextView feelingTextView;
    }

    public FeelingsListAdapter(Context context, int resource, List<String> feelings, String selectedFeeling) {

        super(context, resource, feelings);
        this.context = context;
        this.selectedFeeling = selectedFeeling;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        feelText = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.feelingslist_listview_item, null);
            holder = new ViewHolder();
            //   holder.checkImageView = (ImageView) convertView.findViewById(R.id.feelingCheck);
            holder.feelingTextView = (TextView) convertView.findViewById(R.id.feelingText);

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        if (selectedFeeling != null && selectedFeeling == feelText) {
            holder.feelingTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icn_dropdown_checked, 0);
        } else {
            holder.feelingTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        holder.feelingTextView.setText(feelText);

        return convertView;
    }
}