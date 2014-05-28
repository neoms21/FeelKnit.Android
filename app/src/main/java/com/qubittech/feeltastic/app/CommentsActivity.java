package com.qubittech.feeltastic.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.qubittech.feeltastic.models.Comment;
import com.qubittech.feeltastic.models.Feeling;

import java.util.List;

import util.DateFormatter;

/**
 * Created by Manoj on 19/05/2014.
 */
public class CommentsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments);
        Feeling feeling = (Feeling) getIntent().getSerializableExtra("feeling");

        TextView feel = (TextView) findViewById(R.id.feelingText);
        feel.setText(feeling.getFeelingText());

        TextView because = (TextView) findViewById(R.id.becauseText);
        because.setText(feeling.getReason());

        TextView so = (TextView) findViewById(R.id.soText);
        so.setText(feeling.getAction());

        ArrayAdapter arrayAdapter = new CommentsAdapater(CommentsActivity.this, R.layout.commentslistview, feeling.getComments());

        ListView listview = (ListView) findViewById(R.id.commentsList);
        // endTime = (System.nanoTime() - startTime) / 1000000000;

        listview.setAdapter(arrayAdapter);
        listview.setDivider(new ColorDrawable(0x99000000));
        listview.setDividerHeight(2);

        arrayAdapter.notifyDataSetChanged();

    }


    private class CommentsAdapater extends ArrayAdapter<Comment> {

        int resource;
        Context context;

        /*private view holder class*/
        private class ViewHolder {
            TextView commentTextView;
            TextView userTextView;
            TextView postedAtTextView;
        }

        public CommentsAdapater(Context context, int resource, List<Comment> comments) {

            super(context, resource, comments);
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
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();


            holder.userTextView.setText(comment.getUser());
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
}
