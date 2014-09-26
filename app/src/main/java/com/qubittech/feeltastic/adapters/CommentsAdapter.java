package com.qubittech.feeltastic.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.qubittech.feeltastic.app.MainActivity;
import com.qubittech.feeltastic.app.R;
import com.qubittech.feeltastic.models.Comment;

import java.util.List;

import com.qubittech.feeltastic.models.Feeling;
import com.qubittech.feeltastic.util.ApplicationHelper;
import com.qubittech.feeltastic.util.DateFormatter;

/**
 * Created by Manoj on 19/06/2014.
 */
public class CommentsAdapter extends ArrayAdapter<Comment> {

    int resource;
    Context context;
    String feelingText;
    private final ApplicationHelper applicationHelper;

    /*private view holder class*/
    private class ViewHolder {
        TextView commentTextView;
        TextView userTextView;
        TextView postedAtTextView;
    }

    public CommentsAdapter(Context context, int resource, Feeling feeling) {

        super(context, resource, feeling.getComments());
        applicationHelper = (ApplicationHelper) context.getApplicationContext();
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
            holder.userTextView = (TextView) convertView.findViewById(R.id.name);
            holder.userTextView.setClickable(true);
            holder.userTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity mainActivity = (MainActivity) context;
                    mainActivity.ShowCommentsFragment(null, feelingText, comment.getUser()
                    );
                }
            });
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();


        holder.userTextView.setText(applicationHelper.getUserName().equals(comment.getUser()) ? "me" : comment.getUser());
        holder.commentTextView.setText(comment.getText());
        holder.postedAtTextView.setText(DateFormatter.Format(comment.getPostedAt().toString()));
        //holder.soTextView.setText(feeling.ge());

//        if (applied) {
//            convertView.setBackgroundColor(Color.rgb(201, 201, 201));
//            convertView.setDrawingCacheBackgroundColor(Color.rgb(201, 201, 201));
//        } else convertView.setBackgroundColor(Color.rgb(213, 199, 242));
        return convertView;
    }
}
