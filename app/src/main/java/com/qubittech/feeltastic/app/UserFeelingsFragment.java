package com.qubittech.feeltastic.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.qubittech.feeltastic.adapters.RelatedFeelingsAdapter;
import com.qubittech.feeltastic.adapters.UserFeelingsAdapter;
import com.qubittech.feeltastic.models.Feeling;

import org.apache.http.NameValuePair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import util.JsonHttpClient;
import util.UrlHelper;

/**
 * Created by Manoj on 31/05/2014.
 */
public class UserFeelingsFragment extends Fragment {

    private List<Feeling> _feelings = null;
    ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.user_feelings, container, false);

        String username = "";
        dialog = ProgressDialog.show(getActivity(), "Loading", "Please wait...", true);
        Button newFeeling = (Button) mainView.findViewById(R.id.newFeelingButton);

//        mNavigationDrawerHelper = new NavigationDrawerHelper();
//        mNavigationDrawerHelper.init(this, this);
        newFeeling.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddFeelingActivity.class));
            }
        });
        username = getUserName(username);
        new FetchUserFeelingsTask().execute(username);
        return mainView;
    }

    private String getUserName(String username) {
        SharedPreferences settings = getActivity().getSharedPreferences("UserInfo", 0);
        if (settings != null) {
            username = settings.getString("Username", "").toString();
        }
        return username;
    }
//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int optionLib, long l) {
////        mCoursePagerAdapter.setCourseLib(optionLib);
//        mNavigationDrawerHelper.handleSelect(optionLib);
//    }


    private class FetchUserFeelingsTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            System.out.println("User Feelings:" + s);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
            Type collectionType = new TypeToken<List<Feeling>>() {
            }.getType();
            _feelings = (List<Feeling>) gson.fromJson(s, collectionType);
            _feelings.get(0).setFirstFeeling(true);

            ArrayAdapter arrayAdapter = new UserFeelingsAdapter(getActivity(), R.layout.listview, _feelings);

            ListView listview = (ListView) getView().findViewById(R.id.userFeelingsList);

            listview.setAdapter(arrayAdapter);
            listview.setDivider(new ColorDrawable());
            listview.setDividerHeight(15);
            arrayAdapter.notifyDataSetChanged();
            dialog.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> args = new ArrayList<NameValuePair>();
            JsonHttpClient jsonHttpClient = new JsonHttpClient();
            return jsonHttpClient.Get(UrlHelper.USERNAME + params[0], args);
        }
    }

}
