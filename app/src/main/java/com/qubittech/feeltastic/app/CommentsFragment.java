package com.qubittech.feeltastic.app;

import android.app.ProgressDialog;
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
import com.qubittech.feeltastic.models.Feeling;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import util.JsonHttpClient;
import util.UrlHelper;

/**
 * Created by Manoj on 19/05/2014.
 */
public class CommentsFragment extends Fragment {

    private ProgressDialog dialog;
    private String username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.comments, container, false);


        Bundle args = getArguments();

        final Feeling feeling = (Feeling) args.getSerializable("feeling");
        username = args.getString("user");

        TextView username = (TextView) mainView.findViewById(R.id.name);
        username.setText(feeling.getUserName());

        TextView feel = (TextView) mainView.findViewById(R.id.feelingText);
        feel.setText(feeling.getFeelingText());

        TextView because = (TextView) mainView.findViewById(R.id.becauseText);
        because.setText(feeling.getReason());

        TextView so = (TextView) mainView.findViewById(R.id.soText);
        so.setText(feeling.getAction());

        TextView count = (TextView) mainView.findViewById(R.id.countCommentsLabel);
        count.setText(String.format("%d comments on this feeling", feeling.getComments().size()));

        final EditText commentEdiText = (EditText) mainView.findViewById(R.id.newComment);

        ImageView saveCommentButton = (ImageView) mainView.findViewById(R.id.newCommentButton);

        saveCommentButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               // dialog = ProgressDialog.show(getActivity(), "Loading", "Please wait...", true);
                new SaveCommentTask().execute(commentEdiText.getText().toString(), feeling.getId());
            }
        });
        ArrayAdapter arrayAdapter = new CommentsAdapater(getActivity(), R.layout.commentslistview, feeling.getComments());

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
        protected String doInBackground(String... params) {
            String commentText = params[0];
            String feelingId = params[1];

            List<NameValuePair> args = new ArrayList<NameValuePair>();
            args.add(new BasicNameValuePair("Text", params[0]));
            args.add(new BasicNameValuePair("User", username));

//            args.add(new BasicNameValuePair("user", parentData.toString()));
            //  args.add(new BasicNameValuePair("content", parentData.toString()));
            JsonHttpClient jsonHttpClient = new JsonHttpClient();
            return jsonHttpClient.PostParams(UrlHelper.COMMENTS + "/" + feelingId, args);
        }
    }

}
