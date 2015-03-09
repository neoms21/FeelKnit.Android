package com.qubittech.feelknit.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import com.splunk.mint.Mint;

import org.apache.http.NameValuePair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CurrentFeelingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CurrentFeelingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrentFeelingsFragment extends BackHandledFragment {

    private List<Feeling> _feelings = null;
    ProgressDialog dialog;
    private ListView listview;

    private OnFragmentInteractionListener mListener;
    public static boolean fromMainActivity;

    public static CurrentFeelingsFragment newInstance(String param2) {
        CurrentFeelingsFragment fragment = new CurrentFeelingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public CurrentFeelingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.fragment_current_feelings, container, false);
        dialog = ProgressDialog.show(getActivity(), "Loading", "Please wait...", true);
        listview = (ListView) mainView.findViewById(R.id.currentFeelingsList);


        new FetchCurrentFeelingsTask().execute(ApplicationHelper.getUserName(getActivity().getApplicationContext()));
        return mainView;
    }

    @Override
    public void onPause() {
        super.onPause();
        int index = listview.getFirstVisiblePosition();
        ApplicationHelper.setRecentFeelingsIndex(getActivity().getApplicationContext(), fromMainActivity ? 0 : index);
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
        if (getArguments() != null) {
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    private class FetchCurrentFeelingsTask extends AsyncTask<String, Integer, String> {
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
            ArrayAdapter arrayAdapter = new RelatedFeelingsAdapter(getActivity(), R.layout.listview, _feelings);

            listview.setAdapter(arrayAdapter);
            listview.setDivider(new ColorDrawable());
            listview.setDividerHeight(15);
            arrayAdapter.notifyDataSetChanged();

            int savedIndex = ApplicationHelper.getRecentFeelingsIndex(getActivity().getApplicationContext());
            if (listview != null) {
                if (listview.getCount() > savedIndex && !fromMainActivity)
                    listview.setSelectionFromTop(savedIndex, 0);
                else
                    listview.setSelectionFromTop(0, 0);
            }
            fromMainActivity = false;
            dialog.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> args = new ArrayList<>();
            JsonHttpClient jsonHttpClient = new JsonHttpClient(getActivity().getApplicationContext());
            return jsonHttpClient.Get(UrlHelper.CURRENT, args);
        }
    }
}
