package com.qubittech.feeltastic.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.qubittech.feeltastic.app.CommentsFragment;
import com.qubittech.feeltastic.app.MainActivity;
import com.qubittech.feeltastic.app.R;
import com.qubittech.feeltastic.models.Feeling;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import util.UrlHelper;

/**
 * Created by Manoj on 18/05/2014.
 */
public class RelatedFeelingsAdapter extends ArrayAdapter<Feeling> {

    int resource;
    Context context;
    boolean isUserFeelings = false;
    boolean isRunningOnEmulator = false;

    /*private view holder class*/
    private class ViewHolder {
        TextView usernameTextView;
        TextView feelingTextView;
        TextView locationTextView;
        TextView countTextView;
        ImageView userIcon;
        Button commentButton;
        Button supportButton;
        Button reportButton;
    }


    public RelatedFeelingsAdapter(Context context, int resource, List<Feeling> feelings) {

        super(context, resource, feelings);
        isRunningOnEmulator = UrlHelper.isRunningOnEmulator();
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
            holder.usernameTextView = (TextView) convertView.findViewById(R.id.name);
            holder.feelingTextView = (TextView) convertView.findViewById(R.id.feelingText);
//            holder.locationTextView = (TextView) convertView.findViewById(R.id.location);
            holder.userIcon = (ImageView) convertView.findViewById(R.id.userIconImage);
            holder.supportButton = (Button) convertView.findViewById(R.id.btnSupport);
            holder.reportButton = (Button) convertView.findViewById(R.id.btnReport);
            holder.commentButton = (Button) convertView.findViewById(R.id.btnComment);
            holder.countTextView = (TextView) convertView.findViewById(R.id.commentsCount);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                MainActivity mainActivity = (MainActivity) context;
                mainActivity.ShowCommentsFragment(feeling);

//                Intent commentsActivityIntent = new Intent(getContext(), CommentsFragment.class);
//                commentsActivityIntent.putExtra("feeling", feeling);
//                context.startActivity(commentsActivityIntent);
            }
        });

        holder.usernameTextView.setText(feeling.getUserName());
        holder.feelingTextView.setText(feeling.getFeelingFormattedText(""));
        holder.countTextView.setText(feeling.getComments().size() + "  comments");

        //  if(!isRunningOnEmulator) holder.locationTextView.setText(getLocation(feeling));

        return convertView;
    }


    private String getLocation(Feeling feeling) {
        Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(feeling.getLatitude(), feeling.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addresses.size() > 0 ? addresses.get(0).getLocality() : "";

    }
}
