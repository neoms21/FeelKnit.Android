package com.qubittech.feelknit.fragments;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.qubittech.feelknit.adapters.RelatedFeelingsAdapter;
import com.qubittech.feelknit.app.R;
import com.qubittech.feelknit.models.Feeling;
import com.qubittech.feelknit.util.ApplicationHelper;
import com.qubittech.feelknit.util.JsonHttpClient;
import com.qubittech.feelknit.util.UrlHelper;

import org.apache.http.NameValuePair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class commentsFeelingsFragment extends BackHandledFragment {

    private View mainView;
    ProgressDialog dialog;
    private static List<Feeling> feelings;
    private ListView listview;

    public commentsFeelingsFragment() {
    }

    @Override
    public String getTagText() {
        return this.getTag();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.comments_feelings, container, false);

        //if (feelings == null) {
        dialog = ProgressDialog.show(getActivity(), "Getting feelings", "Please wait...", true);
        listview = (ListView) mainView.findViewById(R.id.commentsFeelingsList);
        new getMyFeelingsCommentsTask().execute();
//        } else
//            new populateListTask().execute();
        return mainView;
    }

    private class populateListTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPostExecute(String s) {
            populateFeelingsList(feelings);
        }

        @Override
        protected String doInBackground(String... strings) {
            return "";
        }
    }

    private class getMyFeelingsCommentsTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
            Type collectionType = new TypeToken<List<Feeling>>() {
            }.getType();
            feelings = gson.fromJson(s, collectionType);
            populateFeelingsList(feelings);
        }

        @Override
        protected String doInBackground(String... strings) {
            List<NameValuePair> args = new ArrayList<>();
            JsonHttpClient jsonHttpClient = new JsonHttpClient(getActivity().getApplicationContext());
            String commentsFeelingUrl = UrlHelper.COMMENTSFEELING;
            commentsFeelingUrl = String.format(commentsFeelingUrl, ApplicationHelper.getUserName(getActivity().getApplicationContext()));
            return jsonHttpClient.Get(commentsFeelingUrl, args);
        }
    }

    private void populateFeelingsList(List<Feeling> feelings) {
        ArrayAdapter arrayAdapter = new RelatedFeelingsAdapter(getActivity(), R.layout.listview, feelings);

        listview.setAdapter(arrayAdapter);
        listview.setDivider(new ColorDrawable());
        listview.setDividerHeight(15);
        arrayAdapter.notifyDataSetChanged();
    }
}
