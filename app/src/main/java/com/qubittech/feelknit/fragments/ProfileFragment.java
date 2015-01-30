package com.qubittech.feelknit.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.qubittech.feelknit.app.MainActivity;
import com.qubittech.feelknit.app.R;
import com.qubittech.feelknit.app.SaveAvatarActivity;
import com.qubittech.feelknit.util.ApplicationHelper;
import com.qubittech.feelknit.util.ImageHelper;
import com.qubittech.feelknit.util.JsonHttpClient;
import com.qubittech.feelknit.util.UrlHelper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private View mainView;
    private TextView usernameTextView;
    private ImageView avatarImageView;
    private TextView emailTextView;
    private ProgressDialog dialog;


    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
//        args.put(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_profile, container, false);
        Bundle bundle = getArguments();
        final String avatar = bundle.getString("Avatar", "");
        usernameTextView = (TextView) mainView.findViewById(R.id.userName);
        emailTextView = (TextView) mainView.findViewById(R.id.userEmail);
        avatarImageView = (ImageView) mainView.findViewById(R.id.userAvatar);

        Button cancelButton = (Button) mainView.findViewById(R.id.cancelProfileButton);

        Button saveButton = (Button) mainView.findViewById(R.id.saveProfileButton);

        ImageHelper.setBitMap(avatarImageView, getActivity().getApplicationContext(), avatar, 100, 100);
        usernameTextView.setText(ApplicationHelper.getUserName(getActivity().getApplicationContext()));
        emailTextView.setText(ApplicationHelper.getUserEmail(getActivity().getApplicationContext()));
        avatarImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SaveAvatarActivity.class);
                intent.putExtra("Profile", true);
                startActivity(intent);
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startMainActivity(0);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                new saveProfileTask().execute(avatar, emailTextView.getText().toString());
                dialog = ProgressDialog.show(getActivity(), "Saving profile", "Please wait...", true);
            }
        });

        return mainView;
    }

    private void startMainActivity(int value) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("From", value);
        startActivity(intent);
    }


    private class saveProfileTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPostExecute(Boolean result) {
            dialog.dismiss();
            startMainActivity(0);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            List<NameValuePair> args = new ArrayList<NameValuePair>();
            ApplicationHelper.setAvatar(getActivity().getApplicationContext(),params[0]);
            ApplicationHelper.setUserEmail(getActivity().getApplicationContext(),params[1]);
            args.add(new BasicNameValuePair("username", ApplicationHelper.getUserName(getActivity().getApplicationContext())));
            args.add(new BasicNameValuePair("avatar", params[0]));
            args.add(new BasicNameValuePair("emailaddress", params[1]));
            JsonHttpClient jsonHttpClient = new JsonHttpClient(getActivity().getApplicationContext());
            String response = jsonHttpClient.PostParams(UrlHelper.SAVE_USER, args);

            return response == null || response.toLowerCase().contains("error") ? false : true;
        }
    }

}
