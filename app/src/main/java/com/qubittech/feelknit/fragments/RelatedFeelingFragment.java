package com.qubittech.feelknit.fragments;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.qubittech.feelknit.adapters.RelatedFeelingsAdapter;
import com.qubittech.feelknit.app.R;
import com.qubittech.feelknit.models.Feeling;
import com.qubittech.feelknit.util.ApplicationHelper;
import com.qubittech.feelknit.util.ImageHelper;
import com.qubittech.feelknit.util.JsonHttpClient;
import com.qubittech.feelknit.util.UrlHelper;

import org.apache.http.NameValuePair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RelatedFeelingFragment extends BackHandledFragment {
    private ProgressDialog dialog;
    private View mainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.related_feelings, container, false);
        Bundle args = getArguments();
        if (args == null) {
            dialog = ProgressDialog.show(getActivity(), "Getting related feelings", "Please wait...", true);
            new fetchRelatedFeelingsTask().execute("");
        } else {
            Feeling feeling = (Feeling) args.getSerializable("feeling");
            List<Feeling> feelings = (List<Feeling>) args.getSerializable("relatedFeelings");
            ShowRelatedFeelings(feeling, feelings);
        }

        return mainView; //Intent feelingIntent = getIntent()
    }

    private void ShowRelatedFeelings(Feeling feeling, List<Feeling> feelings) {

        TextView userNameTextView = (TextView) mainView.findViewById(R.id.name);
        ImageView userIcon = (ImageView) mainView.findViewById(R.id.userIconImage);
        ImageHelper.setBitMap(userIcon, getActivity().getApplicationContext(), ApplicationHelper.getAvatar(getActivity().getApplicationContext()) , 100, 100);
        if(feelings == null)
            return;
        TextView feelTextView = (TextView) mainView.findViewById(R.id.tvFeelingLabel);
        TextView count = (TextView) mainView.findViewById(R.id.countLabel);
        feeling.setFirstFeeling(true);
        userNameTextView.setText("I");
        feelTextView.setText(feeling.getFeelingFormattedText("I"));
        count.setText(String.format("%d %s feeling %s currently", feelings.size(), feelings.size() == 1 ? "person" : "people", feeling.getFeelingText()));

        ArrayAdapter arrayAdapter = new RelatedFeelingsAdapter(getActivity(), R.layout.listview, feelings);

        ListView listview = (ListView) mainView.findViewById(R.id.relatedFeelingsList);

        listview.setAdapter(arrayAdapter);
        listview.setDivider(new ColorDrawable());
        listview.setDividerHeight(10);

        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public String getTagText() {
        return this.getTag();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    private class fetchRelatedFeelingsTask extends AsyncTask<String, Integer, List<Feeling>> {

        @Override
        protected void onPostExecute(List<Feeling> feelings) {
            dialog.dismiss();
            super.onPostExecute(feelings);
            if (feelings.size() > 0) {
                Feeling firstFeeling  = feelings.get(0);
                feelings.remove(0);
                ShowRelatedFeelings(firstFeeling, feelings.size() == 0 ? new ArrayList<Feeling>() : feelings);
            }
        }

        @Override
        protected List<Feeling> doInBackground(String... params) {
            List<NameValuePair> args = new ArrayList<>();
            JsonHttpClient jsonHttpClient = new JsonHttpClient(getActivity().getApplicationContext());
            String res = jsonHttpClient.Get(String.format(UrlHelper.RELATED_FEELINGS, ApplicationHelper.getUserName(getActivity().getApplicationContext())), args);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
            Type collectionType = new TypeToken<List<Feeling>>() {
            }.getType();

            return !res.equals("") ? (List<Feeling>) gson.fromJson(res, collectionType) : new ArrayList<Feeling>();
        }
    }
}
