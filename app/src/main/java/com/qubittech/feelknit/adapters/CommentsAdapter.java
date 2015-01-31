package com.qubittech.feelknit.adapters;

import android.app.Activity;
import android.content.Context;
import android.media.AsyncPlayer;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qubittech.feelknit.app.MainActivity;
import com.qubittech.feelknit.app.R;
import com.qubittech.feelknit.models.Comment;

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

/**
 * Created by Manoj on 19/06/2014.
 */
public class CommentsAdapter extends ArrayAdapter<Comment> {

    int resource;
    Context context;
    String feelingText;
    private final Feeling feeling1;

    /*private view holder class*/
    private class ViewHolder {
        TextView commentTextView;
        TextView userTextView;
        TextView postedAtTextView;
        TextView reportTextView;
        TextView blockingTextView;
        ImageView userImageView;
    }

    public CommentsAdapter(Context context, int resource, Feeling feeling) {

        super(context, resource, feeling.getComments());
        feeling1 = feeling;
        feelingText = feeling.getFeelingText();
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final Comment comment = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.commentslistview, null);
            holder = new ViewHolder();
            holder.commentTextView = (TextView) convertView.findViewById(R.id.comment);
            holder.postedAtTextView = (TextView) convertView.findViewById(R.id.postedAt);
            holder.reportTextView = (TextView) convertView.findViewById(R.id.lblReportComment);
            holder.userTextView = (TextView) convertView.findViewById(R.id.name);
            holder.userImageView = (ImageView) convertView.findViewById(R.id.userIconImage);
            holder.blockingTextView = (TextView) convertView.findViewById(R.id.commentBlockingView);
            holder.userTextView.setClickable(true);
            holder.userTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity mainActivity = (MainActivity) context;
                    mainActivity.ShowCommentsFragment(null, feelingText, comment.getUser()
                    );
                }
            });
            holder.reportTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    comment.setReported(true);
                    notifyDataSetChanged();
                    new reportCommentTask().execute(comment);
                }
            });
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        holder.userTextView.setText(ApplicationHelper.getUserName(getContext()).equals(comment.getUser()) ? "me" : comment.getUser());

        if (comment.getUser() != null && comment.getUser() == "me") {
            ImageHelper.setBitMap(holder.userImageView, context, ApplicationHelper.getAvatar(getContext()), 100, 100);
        } else {
            ImageHelper.setBitMap(holder.userImageView, context, comment.getUserAvatar(), 100, 100);
        }

        if (ApplicationHelper.getUserName(getContext()).equals(comment.getUser()) || comment.getUser() == "me") {
            holder.reportTextView.setVisibility(View.INVISIBLE);
        } else {
            holder.reportTextView.setVisibility(View.VISIBLE);
        }

        if (comment.isReported()) {
            holder.userTextView.setClickable(false);
            holder.reportTextView.setVisibility(View.INVISIBLE);
            holder.blockingTextView.setVisibility(View.VISIBLE);
            holder.postedAtTextView.setText("");
        } else {
            holder.userTextView.setClickable(true);
            holder.reportTextView.setClickable(true);
            holder.blockingTextView.setVisibility(View.INVISIBLE);
            holder.commentTextView.setText(comment.getText());
            holder.postedAtTextView.setText(DateFormatter.Format(comment.getPostedAt().toString()));
        }

        return convertView;
    }


    private class reportCommentTask extends AsyncTask<Comment, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Comment... params) {
            List<NameValuePair> args = new ArrayList<NameValuePair>();
            args.add(new BasicNameValuePair("feelingId", feeling1.getId()));
            args.add(new BasicNameValuePair("id", params[0].getId()));
            args.add(new BasicNameValuePair("reportedBy", ApplicationHelper.getUserName(getContext())));
            JsonHttpClient jsonHttpClient = new JsonHttpClient(getContext());
            jsonHttpClient.PostUrlParams(UrlHelper.REPORTCOMMENT, args);
            return true;
        }
    }
}
