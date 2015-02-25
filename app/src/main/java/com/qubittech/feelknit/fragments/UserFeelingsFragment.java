package com.qubittech.feelknit.fragments;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.qubittech.feelknit.adapters.UserFeelingsAdapter;
import com.qubittech.feelknit.app.MainActivity;
import com.qubittech.feelknit.app.R;
import com.qubittech.feelknit.models.Feeling;
import com.qubittech.feelknit.util.ApplicationHelper;
import com.qubittech.feelknit.util.JsonHttpClient;
import com.qubittech.feelknit.util.UrlHelper;
import com.splunk.mint.Mint;

import org.apache.http.NameValuePair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UserFeelingsFragment extends BackHandledFragment {

    private List<Feeling> _feelings = null;
    ProgressDialog dialog;
    private ListView listview;

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
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.user_feelings, container, false);
        dialog = ProgressDialog.show(getActivity(), "Loading", "Please wait...", true);
        Button newFeeling = (Button) mainView.findViewById(R.id.newFeelingButton);
        listview = (ListView) mainView.findViewById(R.id.userFeelingsList);

        newFeeling.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                activity.AddCreateFeelingFragment();
            }
        });
        new FetchUserFeelingsTask().execute(ApplicationHelper.getUserName(getActivity().getApplicationContext()));
        return mainView;
    }

    private class FetchUserFeelingsTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
            Type collectionType = new TypeToken<List<Feeling>>() {
            }.getType();
            try {
                _feelings = gson.fromJson(s, collectionType);
            } catch (Exception ex) {
                Mint.logException(ex);
                _feelings = null;
            }
            if (_feelings == null) {
                dialog.dismiss();
                return;
            }

            if (!_feelings.isEmpty())
                _feelings.get(0).setFirstFeeling(true);

            ArrayAdapter arrayAdapter = new UserFeelingsAdapter(getActivity(), R.layout.listview, _feelings);


            listview.setAdapter(arrayAdapter);
            listview.setDivider(new ColorDrawable());
            listview.setDividerHeight(15);
            arrayAdapter.notifyDataSetChanged();
            dialog.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> args = new ArrayList<>();
            JsonHttpClient jsonHttpClient = new JsonHttpClient(getActivity().getApplicationContext());
            return jsonHttpClient.Get(UrlHelper.USERNAME + params[0], args);
        }
    }

}
