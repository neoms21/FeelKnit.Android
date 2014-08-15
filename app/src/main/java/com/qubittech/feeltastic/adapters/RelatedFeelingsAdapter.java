package com.qubittech.feeltastic.adapters;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.qubittech.feeltastic.app.MainActivity;
import com.qubittech.feeltastic.app.R;
import com.qubittech.feeltastic.models.Feeling;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.qubittech.feeltastic.util.ApplicationHelper;
import com.qubittech.feeltastic.util.GMailSender;
import com.qubittech.feeltastic.util.JsonHttpClient;
import com.qubittech.feeltastic.util.UrlHelper;

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
        TextView commentsCountTextView;
        TextView supportCountTextView;
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
            holder.supportButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new IncreaseSupportCountTask().execute(feeling.getId());
                }
            });
            holder.reportButton = (Button) convertView.findViewById(R.id.btnReport);
            holder.commentButton = (Button) convertView.findViewById(R.id.btnComment);
            holder.commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavigateToCommentsView(feeling);
                }
            });
            holder.reportButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new reportFeelingTask().execute(feeling.getId().toString());
                }
            });

            holder.commentsCountTextView = (TextView) convertView.findViewById(R.id.commentsCount);
            holder.supportCountTextView = (TextView) convertView.findViewById(R.id.supportCount);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigateToCommentsView(feeling);
            }
        });

        holder.usernameTextView.setText(feeling.getUserName());
        holder.feelingTextView.setText(feeling.getFeelingFormattedText(""));
        holder.commentsCountTextView.setText(String.format("Comments (%d)", feeling.getComments().size()));
        holder.supportCountTextView.setText(String.format("Support (%d)", feeling.getSupportCount()));
        return convertView;
    }

    private void NavigateToCommentsView(Feeling feeling) {
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.ShowCommentsFragment(feeling);
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

    private class IncreaseSupportCountTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            List<NameValuePair> args = new ArrayList<NameValuePair>();
            args.add(new BasicNameValuePair("feelingId", params[0]));
            JsonHttpClient jsonHttpClient = new JsonHttpClient();
            String supportUrl = UrlHelper.SUPPORT;
            String response = jsonHttpClient.PostParams(supportUrl, args);
            return true;
        }
    }

    private class reportFeelingTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            List<NameValuePair> args = new ArrayList<NameValuePair>();
            args.add(new BasicNameValuePair("feelingId", params[0]));
            args.add(new BasicNameValuePair("username", ApplicationHelper.UserName));
            JsonHttpClient jsonHttpClient = new JsonHttpClient();
            String supportUrl = UrlHelper.EMAILREPORT;
            String response = jsonHttpClient.PostParams(supportUrl, args);
            return true;
        }
    }
}
