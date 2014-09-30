package com.qubittech.feelknit.adapters;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.qubittech.feelknit.app.MainActivity;
import com.qubittech.feelknit.app.R;
import com.qubittech.feelknit.models.Feeling;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.qubittech.feelknit.util.ApplicationHelper;
import com.qubittech.feelknit.util.DateFormatter;
import com.qubittech.feelknit.util.JsonHttpClient;
import com.qubittech.feelknit.util.UrlHelper;

/**
 * Created by Manoj on 18/05/2014.
 */
public class RelatedFeelingsAdapter extends ArrayAdapter<Feeling> {

    int resource;
    Context context;
    boolean isUserFeelings = false;
    boolean isRunningOnEmulator = false;
    private final ApplicationHelper applicationHelper;

    /*private view holder class*/
    private class ViewHolder {
        TextView usernameTextView;
        TextView feelingTextView;
        TextView locationTextView;
        TextView commentsCountTextView;
        TextView supportCountTextView;
        TextView feelingDateTextView;
        ImageView userIcon;
        Button commentButton;
        Button supportButton;
        Button reportButton;
        TextView blockingView;
    }


    public RelatedFeelingsAdapter(Context context, int resource, List<Feeling> feelings) {

        super(context, resource, feelings);
        applicationHelper = (ApplicationHelper) context.getApplicationContext();
        isRunningOnEmulator = UrlHelper.isRunningOnEmulator();
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final Feeling feeling = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview, null);
            holder = new ViewHolder();
            holder.usernameTextView = (TextView) convertView.findViewById(R.id.name);
            holder.blockingView = (TextView) convertView.findViewById(R.id.blockingView);
            holder.feelingTextView = (TextView) convertView.findViewById(R.id.feelingText);
            holder.feelingDateTextView = (TextView) convertView.findViewById(R.id.feelingShared);
//            holder.locationTextView = (TextView) convertView.findViewById(R.id.location);
            holder.userIcon = (ImageView) convertView.findViewById(R.id.userIconImage);
            holder.supportButton = (Button) convertView.findViewById(R.id.btnSupport);
            holder.reportButton = (Button) convertView.findViewById(R.id.btnReport);
            holder.commentButton = (Button) convertView.findViewById(R.id.btnComment);
            holder.commentsCountTextView = (TextView) convertView.findViewById(R.id.commentsCount);
            holder.supportCountTextView = (TextView) convertView.findViewById(R.id.supportCount);
            setFeelingInListView(convertView, holder, feeling);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            setFeelingInListView(convertView, holder, feeling);
        }

        holder.usernameTextView.setText(feeling.getUserName());
        return convertView;
    }

    private void setFeelingInListView(View convertView, ViewHolder holder, Feeling feeling) {
        if (feeling.isReported()) {
            setReportedFeeling(holder, feeling);
        } else {
            setUnreportedFeeling(convertView, holder, feeling);
        }
    }

    private void setUnreportedFeeling(View convertView, final ViewHolder holder, final Feeling feeling) {
        holder.feelingDateTextView.setText(DateFormatter.Format(feeling.getFeelingDate()));
        holder.feelingTextView.setText(feeling.getFeelingFormattedText(""));
        holder.commentsCountTextView.setText(String.format("Comments (%d)", feeling.getComments().size()));
        holder.supportCountTextView.setText(String.format("Support (%d)", feeling.getSupportCount()));
        if (feeling.getSupportUsers().contains(applicationHelper.getUserName()))
        {
            holder.supportButton.setText("Un-Support");
        }
        holder.supportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manipulateSupportButton(feeling, holder.supportButton);
            }
        });

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
                feeling.setReported(true);
                notifyDataSetChanged();
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigateToCommentsView(feeling);
            }
        });
    }

    private void setReportedFeeling(ViewHolder holder, Feeling feeling) {
        holder.blockingView.setVisibility(View.VISIBLE);
        holder.blockingView.bringToFront();
        holder.usernameTextView.setText(feeling.getUserName());
        holder.feelingTextView.setText("");
        int color = getContext().getResources().getColor(R.color.greyColor);
        DisableButton(holder.commentButton, color);//.setBackgroundColor(color);
        DisableButton(holder.supportButton, color);//.setBackgroundColor(color);
        DisableButton(holder.reportButton, color);//.setBackgroundColor(color);
    }

    private void DisableButton(Button button, int color)
    {
        button.setEnabled(false);
        button.setClickable(false);
        button.setBackgroundColor(color);
    }

    private void manipulateSupportButton(Feeling feeling, Button supportButton) {

        if (feeling.getSupportUsers().contains(applicationHelper.getUserName())) {
            supportButton.setText("Support");
            int existingCount = feeling.getSupportCount();
            feeling.setSupportCount(existingCount - 1);
            notifyDataSetChanged();
            feeling.getSupportUsers().remove(applicationHelper.getUserName());
            new DecreaseSupportCountTask().execute(feeling.getId());
        } else {
            supportButton.setText("Un-Support");
            int existingCount = feeling.getSupportCount();
            feeling.getSupportUsers().add(applicationHelper.getUserName());
            feeling.setSupportCount(existingCount + 1);
            notifyDataSetChanged();
            new IncreaseSupportCountTask().execute(feeling.getId());
        }
    }

    private void NavigateToCommentsView(Feeling feeling) {
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.ShowCommentsFragment(feeling, null, null);
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
            args.add(new BasicNameValuePair("username", applicationHelper.getUserName()));
            JsonHttpClient jsonHttpClient = new JsonHttpClient();
            String supportUrl = UrlHelper.INCREASE_SUPPORT;
            String response = jsonHttpClient.PostUrlParams(supportUrl, args);
            return true;
        }
    }

    private class DecreaseSupportCountTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            List<NameValuePair> args = new ArrayList<NameValuePair>();
            args.add(new BasicNameValuePair("feelingId", params[0]));
            args.add(new BasicNameValuePair("username", applicationHelper.getUserName()));
            JsonHttpClient jsonHttpClient = new JsonHttpClient();
            String supportUrl = UrlHelper.DECREASE_SUPPORT;
            String response = jsonHttpClient.PostUrlParams(supportUrl, args);
            return true;
        }
    }

    private class reportFeelingTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            List<NameValuePair> args = new ArrayList<NameValuePair>();
            args.add(new BasicNameValuePair("feelingId", params[0]));
            args.add(new BasicNameValuePair("username", applicationHelper.getUserName()));
            JsonHttpClient jsonHttpClient = new JsonHttpClient();
            String emailUrl = UrlHelper.EMAILREPORT;
            String response = jsonHttpClient.PostUrlParams(emailUrl, args);
            return true;
        }
    }
}
