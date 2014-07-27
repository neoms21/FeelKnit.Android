package com.qubittech.feeltastic.app;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.qubittech.feeltastic.adapters.CommentsAdapater;
import com.qubittech.feeltastic.models.Comment;
import com.qubittech.feeltastic.models.Feeling;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import util.JsonHttpClient;
import util.UrlHelper;

/**
 * Created by Manoj on 19/05/2014.
 */
public class CommentsFragment extends Fragment {

    private ProgressDialog dialog;
    private String username;
    private ArrayAdapter arrayAdapter;
    private Feeling feeling;
    private String commentText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.comments, container, false);


        Bundle args = getArguments();

        feeling = (Feeling) args.getSerializable("feeling");
        username = args.getString("user");

        TextView username = (TextView) mainView.findViewById(R.id.name);
        username.setText(feeling.getUserName());

        TextView feel = (TextView) mainView.findViewById(R.id.tvFeelingLabel);
        feel.setText(feeling.getFeelingFormattedText(""));

        TextView count = (TextView) mainView.findViewById(R.id.countCommentsLabel);
        count.setText(String.format("%d comments on this feeling", feeling.getComments().size()));

        final EditText commentEdiText = (EditText) mainView.findViewById(R.id.newComment);

        ImageView saveCommentButton = (ImageView) mainView.findViewById(R.id.newCommentButton);

        saveCommentButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog = ProgressDialog.show(getActivity(), "Loading", "Please wait...", true);
                new SaveCommentTask().execute(commentEdiText.getText().toString(), feeling.getId());
            }
        });
        arrayAdapter = new CommentsAdapater(getActivity(), R.layout.commentslistview, feeling.getComments());
        ListView listview = (ListView) mainView.findViewById(R.id.commentsList);
        // endTime = (System.nanoTime() - startTime) / 1000000000;

        listview.setAdapter(arrayAdapter);
        listview.setDivider(new ColorDrawable());
        listview.setDividerHeight(10);
        arrayAdapter.notifyDataSetChanged();
        return mainView;
    }

    private class SaveCommentTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(final String... params) {
            commentText = params[0];
            String feelingId = params[1];



            List<NameValuePair> args = new ArrayList<NameValuePair>();
            args.add(new BasicNameValuePair("Text", params[0]));
            args.add(new BasicNameValuePair("User", username));
            JsonHttpClient jsonHttpClient = new JsonHttpClient();
            return jsonHttpClient.PostParams(UrlHelper.COMMENTS + "/" + feelingId, args);
        }

        @Override
        protected void onPostExecute(String s) {

            dialog.dismiss();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (feeling == null)
                        return;

                    Comment comment = new Comment();
                    comment.setUser(username);
                    comment.setText(commentText);
                    comment.setPostedAt(new Date().toString());
                    feeling.getComments().add(comment);
                    arrayAdapter.notifyDataSetChanged();

                }
            });
        }


    }

}
