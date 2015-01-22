package com.qubittech.feelknit.fragments;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.qubittech.feelknit.adapters.CommentsAdapter;
import com.qubittech.feelknit.app.R;
import com.qubittech.feelknit.models.Comment;
import com.qubittech.feelknit.models.Feeling;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.qubittech.feelknit.util.ApplicationHelper;
import com.qubittech.feelknit.util.ImageHelper;
import com.qubittech.feelknit.util.JsonHttpClient;
import com.qubittech.feelknit.util.UrlHelper;

public class CommentsFragment extends Fragment {

    private ApplicationHelper applicationHelper;
    private ProgressDialog dialog;
    //    private String username;
    private ArrayAdapter arrayAdapter;
    private Feeling feeling;
    private String commentText;
    private EditText commentEdiText;
    private ImageView saveCommentButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        applicationHelper = (ApplicationHelper) getActivity().getApplicationContext();
        View mainView = inflater.inflate(R.layout.comments, container, false);

        Bundle args = getArguments();

        feeling = (Feeling) args.getSerializable("feeling");
        if (feeling == null)
            return mainView;
//        username = ApplicationHelper.UserName;

        ImageView userImageView = (ImageView) mainView.findViewById(R.id.userIconImage);
        if (feeling.getUser() != null && feeling.getUser().getAvatar() != null)
            ImageHelper.setBitMap(userImageView, getActivity().getApplicationContext(), feeling.getUser().getAvatar(), 100, 100);

        TextView feelingUserNameTextView = (TextView) mainView.findViewById(R.id.name);
        boolean currentUser = applicationHelper.getUserName().equals(feeling.getUserName());
        String feelingUserName = currentUser ? "I" : feeling.getUserName();
        feelingUserNameTextView.setText(feelingUserName);

        TextView feel = (TextView) mainView.findViewById(R.id.tvFeelingLabel);
        feel.setText(feeling.getFeelingFormattedText(currentUser ? "I" : ""));

        TextView count = (TextView) mainView.findViewById(R.id.countCommentsLabel);
        count.setText(String.format("%d comments on this feeling", feeling.getComments().size()));

        commentEdiText = (EditText) mainView.findViewById(R.id.newComment);
        saveCommentButton = (ImageView) mainView.findViewById(R.id.newCommentButton);

        commentEdiText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() == 0)
                    saveCommentButton.setVisibility(View.INVISIBLE);
                else
                    saveCommentButton.setVisibility(View.VISIBLE);
            }
        });

        saveCommentButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog = ProgressDialog.show(getActivity(), "Saving comment", "Please wait...", true);
                new SaveCommentTask().execute(commentEdiText.getText().toString(), feeling.getId());
            }
        });
        arrayAdapter = new CommentsAdapter(getActivity(), R.layout.commentslistview, feeling);
        ListView listview = (ListView) mainView.findViewById(R.id.commentsList);

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
            args.add(new BasicNameValuePair("User", applicationHelper.getUserName()));
            JsonHttpClient jsonHttpClient = new JsonHttpClient(applicationHelper);
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
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    commentEdiText.setText("");
                    Comment comment = new Comment();
                    comment.setUser("me"); // Because while in comments whenever anyone save it'll be that user
                    comment.setText(commentText);
                    comment.setPostedAt(sdf.format(new Date()));
                    feeling.getComments().add(comment);
                    arrayAdapter.notifyDataSetChanged();

                }
            });
        }


    }

}
