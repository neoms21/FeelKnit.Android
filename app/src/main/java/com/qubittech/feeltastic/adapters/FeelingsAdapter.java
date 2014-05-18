package com.qubittech.feeltastic.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.qubittech.feeltastic.app.R;
import com.qubittech.feeltastic.models.Feeling;

import java.util.List;

/**
 * Created by Manoj on 18/05/2014.
 */
public class FeelingsAdapter extends ArrayAdapter<Feeling> {

    int resource;
    Context context;

    /*private view holder class*/
    private class ViewHolder {
        TextView usernameTextView;
        TextView reasonTextView;
        TextView actionTextView;
        TextView locationTextView;
    }


    public FeelingsAdapter(Context context, int resource, List<Feeling> feelings) {

        super(context, resource, feelings);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final Feeling feeling = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview, null);
            holder = new ViewHolder();
            holder.usernameTextView = (TextView) convertView.findViewById(R.id.username);
            holder.reasonTextView = (TextView) convertView.findViewById(R.id.reason);
            holder.actionTextView = (TextView) convertView.findViewById(R.id.action);
            holder.locationTextView = (TextView) convertView.findViewById(R.id.location);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

//        convertView.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent details = new Intent("com.web.jobserve.Activity.JobDetailsActivity");
//                details.putExtra("job", job);
//                context.startActivity(details);
//            }
//        });

        holder.usernameTextView.setText(feeling.getUserName());
        holder.reasonTextView.setText(feeling.getReason());
        holder.actionTextView.setText(feeling.getAction());
        //holder.actionTextView.setText(feeling.ge());

//        if (applied) {
//            convertView.setBackgroundColor(Color.rgb(201, 201, 201));
//            convertView.setDrawingCacheBackgroundColor(Color.rgb(201, 201, 201));
//        } else convertView.setBackgroundColor(Color.rgb(213, 199, 242));
        return convertView;
    }
}
