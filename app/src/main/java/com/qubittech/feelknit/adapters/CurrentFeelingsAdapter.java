package com.qubittech.feelknit.adapters;

import android.app.Activity;
import android.content.Context;
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
import com.qubittech.feelknit.util.ApplicationHelper;
import com.qubittech.feelknit.util.DateFormatter;
import com.qubittech.feelknit.util.ImageHelper;
import com.qubittech.feelknit.util.JsonHttpClient;
import com.qubittech.feelknit.util.UrlHelper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class CurrentFeelingsAdapter extends ArrayAdapter<Feeling> {

    Context context;
    boolean isRunningOnEmulator = false;

    /*private view holder class*/
    private static class ViewHolder {
        TextView usernameTextView;
        TextView feelingTextView;
        TextView commentsCountTextView;
        TextView supportCountTextView;
        TextView feelingDateTextView;
        ImageView userIcon;
        Button commentButton;
        Button supportButton;
        Button reportButton;
        TextView blockingView;
    }


    public CurrentFeelingsAdapter(Context context, int resource, List<Feeling> feelings) {

        super(context, resource, feelings);
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
            holder.userIcon = (ImageView) convertView.findViewById(R.id.userIconImage);
            holder.supportButton = (Button) convertView.findViewById(R.id.btnSupport);
            holder.reportButton = (Button) convertView.findViewById(R.id.btnReport);
            holder.commentButton = (Button) convertView.findViewById(R.id.btnComment);
            holder.commentsCountTextView = (TextView) convertView.findViewById(R.id.commentsCount);
            holder.supportCountTextView = (TextView) convertView.findViewById(R.id.supportCount);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        setFeelingInListView(convertView, holder, feeling);
        holder.usernameTextView.setText(feeling.getUserName().equals(ApplicationHelper.getUserName(getContext())) ? "I" : feeling.getUserName());
        return convertView;
    }

    private void setFeelingInListView(View convertView, ViewHolder holder, Feeling feeling) {
        if (feeling.isReported()) {
            setReportedFeeling(convertView, holder, feeling);
        } else {
            setUnreportedFeeling(convertView, holder, feeling);
        }
    }

    private void setUnreportedFeeling(View convertView, final ViewHolder holder, final Feeling feeling) {
        holder.feelingDateTextView.setText(DateFormatter.Format(feeling.getFeelingDate()));
        holder.feelingTextView.setText(feeling.getFeelingFormattedText(feeling.getUserName().equals(ApplicationHelper.getUserName(getContext())) ? "I" : ""));
        holder.commentsCountTextView.setText(String.format("Comments (%d)", feeling.getComments().size()));
        holder.supportCountTextView.setText(String.format("Support (%d)", feeling.getSupportCount()));
        if (feeling.getUserAvatar() != null)
            ImageHelper.setBitMap(holder.userIcon, context, feeling.getUserAvatar(), 100, 100);
        else
            ImageHelper.setBitMap(holder.userIcon, context, "usericon", 100, 100);

        if (feeling.getSupportUsers().contains(ApplicationHelper.getUserName(getContext()))) {
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
        int color = getContext().getResources().getColor(R.color.lightButtonColor);
        if (feeling.getUserName().equals(ApplicationHelper.getUserName(getContext()))) {
            holder.reportButton.setClickable(false);
            holder.reportButton.setBackgroundColor(getContext().getResources().getColor(R.color.greyColor));
            holder.reportButton.setOnClickListener(null);
        } else {
            holder.reportButton.setClickable(true);
            holder.reportButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new reportFeelingTask().execute(feeling.getId());
                    feeling.setReported(true);
                    notifyDataSetChanged();
                }
            });
            EnableButton(holder.reportButton, color);//.setBackgroundColor(color);
        }

        EnableButton(holder.commentButton, color);//.setBackgroundColor(color);
        EnableButton(holder.supportButton, color);//.setBackgroundColor(color);

        holder.blockingView.setVisibility(View.INVISIBLE);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NavigateToCommentsView(feeling);
            }
        });
    }

    private void setReportedFeeling(View convertView, ViewHolder holder, Feeling feeling) {
        if (feeling.getUserAvatar() != null)
            ImageHelper.setBitMap(holder.userIcon, context, feeling.getUserAvatar(), 100, 100);

        holder.blockingView.setVisibility(View.VISIBLE);
        holder.usernameTextView.setText(feeling.getUserName());
        holder.feelingTextView.setText(feeling.getFeelingFormattedText(feeling.getUserName().equals(ApplicationHelper.getUserName(getContext())) ? "I" : ""));
        int color = getContext().getResources().getColor(R.color.greyColor);
        DisableButton(holder.commentButton, color);//.setBackgroundColor(color);
        DisableButton(holder.supportButton, color);//.setBackgroundColor(color);
        DisableButton(holder.reportButton, color);//.setBackgroundColor(color);
        convertView.setClickable(false);
    }

    private void DisableButton(Button button, int color) {
        button.setEnabled(false);
        button.setClickable(false);
        button.setBackgroundColor(color);
    }

    private void EnableButton(Button button, int color) {
        button.setEnabled(true);
        button.setClickable(true);
        button.setBackgroundColor(color);
    }

    private void manipulateSupportButton(Feeling feeling, Button supportButton) {

        if (feeling.getSupportUsers().contains(ApplicationHelper.getUserName(getContext()))) {
            supportButton.setText("Support");
            int existingCount = feeling.getSupportCount();
            feeling.setSupportCount(existingCount - 1);
            notifyDataSetChanged();
            feeling.getSupportUsers().remove(ApplicationHelper.getUserName(getContext()));
            new DecreaseSupportCountTask().execute(feeling.getId());
        } else {
            supportButton.setText("Un-Support");
            int existingCount = feeling.getSupportCount();
            feeling.getSupportUsers().add(ApplicationHelper.getUserName(getContext()));
            feeling.setSupportCount(existingCount + 1);
            notifyDataSetChanged();
            new IncreaseSupportCountTask().execute(feeling.getId());
        }
    }

    private void NavigateToCommentsView(Feeling feeling) {
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.ShowCommentsFragment(feeling, null, null, null);
    }

    private class IncreaseSupportCountTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            List<NameValuePair> args = new ArrayList<>();
            args.add(new BasicNameValuePair("feelingId", params[0]));
            args.add(new BasicNameValuePair("username", ApplicationHelper.getUserName(getContext())));
            JsonHttpClient jsonHttpClient = new JsonHttpClient(getContext());
            String supportUrl = UrlHelper.INCREASE_SUPPORT;
            jsonHttpClient.PostParams(supportUrl, args);
            return true;
        }
    }

    private class DecreaseSupportCountTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            List<NameValuePair> args = new ArrayList<>();
            args.add(new BasicNameValuePair("feelingId", params[0]));
            args.add(new BasicNameValuePair("username", ApplicationHelper.getUserName(getContext())));
            JsonHttpClient jsonHttpClient = new JsonHttpClient(getContext());
            String supportUrl = UrlHelper.DECREASE_SUPPORT;
            jsonHttpClient.PostParams(supportUrl, args);
            return true;
        }
    }

    private class reportFeelingTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            List<NameValuePair> args = new ArrayList<>();
            args.add(new BasicNameValuePair("feelingId", params[0]));
            args.add(new BasicNameValuePair("username", ApplicationHelper.getUserName(getContext())));
            JsonHttpClient jsonHttpClient = new JsonHttpClient(getContext());
            String emailUrl = UrlHelper.REPORTFEELING;
            jsonHttpClient.PostParams(emailUrl, args);
            return true;
        }
    }
}
