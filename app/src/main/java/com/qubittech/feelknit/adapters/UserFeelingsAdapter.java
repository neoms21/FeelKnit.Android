package com.qubittech.feelknit.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.qubittech.feelknit.app.MainActivity;
import com.qubittech.feelknit.app.R;
import com.qubittech.feelknit.models.Feeling;

import java.util.List;

import com.qubittech.feelknit.util.DateFormatter;

public class UserFeelingsAdapter extends ArrayAdapter<Feeling> {

    Context context;

    public UserFeelingsAdapter(Context context, int resource, List<Feeling> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final Feeling feeling = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.user_feeling_listview, null);
            holder = new ViewHolder();
            holder.feelingTextView = (TextView) convertView.findViewById(R.id.userFeelingText);
            holder.feelingTimeTextView = (TextView) convertView.findViewById(R.id.feelingTime);
            holder.countTextView = (TextView) convertView.findViewById(R.id.userFeelingCommentsCount);
            holder.supportCountTextView = (TextView) convertView.findViewById(R.id.userFeelingSupportCount);

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.ShowCommentsFragment(feeling, null, null, null);
            }
        });

        holder.feelingTextView.setText("I " + feeling.getFeelingFormattedText("I"));
        holder.feelingTimeTextView.setText(DateFormatter.Format(feeling.getFeelingDate().toString()));
        holder.countTextView.setText(feeling.getComments().size() + "  comments");
        holder.supportCountTextView.setText(feeling.getSupportCount() + "  supported");

        return convertView;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView feelingTextView;
        TextView feelingTimeTextView;
        TextView countTextView;
        TextView supportCountTextView;
    }


}
