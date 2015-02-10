package com.qubittech.feelknit.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.qubittech.feelknit.adapters.CommentsAdapter;
import com.qubittech.feelknit.app.R;
import com.qubittech.feelknit.models.Comment;
import com.qubittech.feelknit.models.Feeling;
import com.qubittech.feelknit.util.ApplicationHelper;
import com.qubittech.feelknit.util.ImageHelper;
import com.qubittech.feelknit.util.JsonHttpClient;
import com.qubittech.feelknit.util.UrlHelper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentsFragment extends BackHandledFragment {

    private ProgressDialog dialog;
    //    private String username;
    private ArrayAdapter arrayAdapter;
    private Feeling finalFeeling;
    private String commentText;
    private EditText commentEdiText;
    private ImageView saveCommentButton;
    private TextView countLabelView;
    private ImageView userImageView;
    private TextView feelingUserNameTextView;
    private TextView feel;
    private ListView listview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.comments, container, false);
//
        Bundle args = getArguments();

        finalFeeling = (Feeling) args.getSerializable("feeling");
        String feelingId = args.getString("feelingId");


        userImageView = (ImageView) mainView.findViewById(R.id.userIconImage);
        feelingUserNameTextView = (TextView) mainView.findViewById(R.id.name);
        feel = (TextView) mainView.findViewById(R.id.tvFeelingLabel);
        countLabelView = (TextView) mainView.findViewById(R.id.countCommentsLabel);
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


        listview = (ListView) mainView.findViewById(R.id.commentsList);


        if (finalFeeling == null && feelingId != null) {
            dialog = ProgressDialog.show(getActivity(), "Getting feeling", "Please wait...", true);
            new GetFeelingTask().execute(feelingId);
        } else {
            SetPropertiesOnScreen(finalFeeling);
        }

        return mainView;
    }

    @Override
    public String getTagText() {
        return this.getTag();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    private class GetFeelingTask extends AsyncTask<String, Integer, Feeling> {

        @Override
        protected Feeling doInBackground(final String... params) {
            String feelingId = params[0];
            List<NameValuePair> args = new ArrayList<>();
            JsonHttpClient jsonHttpClient = new JsonHttpClient(getActivity().getApplicationContext());
            String response = jsonHttpClient.Get(UrlHelper.FEELINGS + "/" + feelingId, args);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
            Type type = new TypeToken<Feeling>() {
            }.getType();

            return gson.fromJson(response, type);
        }

        @Override
        protected void onPostExecute(final Feeling feeling) {

            dialog.dismiss();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SetPropertiesOnScreen(feeling);
                }
            });
        }
    }

    private void SetPropertiesOnScreen(final Feeling feeling) {
        finalFeeling = feeling;
        if (feeling.getUserAvatar() != null)
            ImageHelper.setBitMap(userImageView, getActivity().getApplicationContext(), feeling.getUserAvatar(), 100, 100);

        boolean currentUser = ApplicationHelper.getUserName(getActivity().getApplicationContext()).equals(feeling.getUserName());
        String feelingUserName = currentUser ? "I" : feeling.getUserName();
        feelingUserNameTextView.setText(feelingUserName);

        feel.setText(feeling.getFeelingFormattedText(currentUser ? "I" : ""));
        countLabelView.setText(String.format("%d comments on this feeling", feeling.getComments().size()));

        saveCommentButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog = ProgressDialog.show(getActivity(), "Saving comment", "Please wait...", true);
                new SaveCommentTask().execute(commentEdiText.getText().toString(), feeling.getId());
            }
        });
        arrayAdapter = new CommentsAdapter(getActivity(), R.layout.commentslistview, feeling);

        listview.setAdapter(arrayAdapter);
        listview.setDivider(new ColorDrawable());
        listview.setDividerHeight(10);
        arrayAdapter.notifyDataSetChanged();
    }

    private class SaveCommentTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(final String... params) {
            commentText = params[0];
            String feelingId = params[1];
            List<NameValuePair> args = new ArrayList<>();
            args.add(new BasicNameValuePair("Text", params[0]));
            args.add(new BasicNameValuePair("User", ApplicationHelper.getUserName(getActivity().getApplicationContext())));
            JsonHttpClient jsonHttpClient = new JsonHttpClient(getActivity().getApplicationContext());
            return jsonHttpClient.PostParams(UrlHelper.COMMENTS + "/" + feelingId, args);
        }

        @Override
        protected void onPostExecute(String s) {

            dialog.dismiss();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (finalFeeling == null)
                        return;

                    if (commentEdiText != null) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(commentEdiText.getWindowToken(), 0);
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    commentEdiText.setText("");
                    Comment comment = new Comment();
                    comment.setUser("me"); // Because while in comments whenever anyone save it'll be that user
                    comment.setUserAvatar(ApplicationHelper.getAvatar(getActivity().getApplicationContext()));
                    comment.setText(commentText);
                    comment.setPostedAt(sdf.format(new Date()));
                    finalFeeling.getComments().add(comment);
                    arrayAdapter.notifyDataSetChanged();
                    if (countLabelView != null)
                        countLabelView.setText(String.format("%d comments on this feeling", finalFeeling.getComments().size()));

                }
            });
        }
    }
}
