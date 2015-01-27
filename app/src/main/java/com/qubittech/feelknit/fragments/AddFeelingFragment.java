package com.qubittech.feelknit.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.qubittech.feelknit.app.R;
import com.qubittech.feelknit.models.Feeling;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.qubittech.feelknit.services.TrackingService;
import com.qubittech.feelknit.util.ApplicationHelper;
import com.qubittech.feelknit.util.JsonHttpClient;
import com.qubittech.feelknit.util.UrlHelper;

public class AddFeelingFragment extends Fragment {

    private static String username = "";
    private static List<String> dbDefinedFeelings;
    private Spinner spinnerFeelings;
    private String selectedFeeling = "";
    ProgressDialog dialog;
    private Feeling _feeling = null;
    private List<Feeling> relatedFeelings = null;
    private ApplicationHelper applicationHelper;
    private String TAG = "SpinnerHint";

    private LayoutInflater mInflator;
    private boolean selected;
    private OnCreateFeelingClick mCallback;
    private Double currentLatitude = 5.4;
    private Double currentLongitude = 7.8;

    // Container Activity must implement this interface
    public interface OnCreateFeelingClick {
        public void onFeelingCreated(Feeling feeling, List<Feeling> relatedFeelings);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        applicationHelper = (ApplicationHelper) getActivity().getApplicationContext();
        dbDefinedFeelings = ((ApplicationHelper) (getActivity().getApplicationContext())).getFeelTexts();
        View addFeelingView = inflater.inflate(R.layout.activity_feeling, container, false);
        _feeling = new Feeling();
        spinnerFeelings = (Spinner) addFeelingView.findViewById(R.id.feelingSpinner);
        username = applicationHelper.getUserName();

        final EditText because = (EditText) addFeelingView.findViewById(R.id.becauseText);
        final TextView so = (TextView) addFeelingView.findViewById(R.id.soText);

        spinnerFeelings.setAdapter(typeSpinnerAdapter);
        spinnerFeelings.setOnItemSelectedListener(typeSelectedListener);
        spinnerFeelings.setOnTouchListener(typeSpinnerTouchListener);
        mInflator = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        spinnerFeelings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position,
                                       long id) {
                spinnerFeelings.setSelection(position);
                selectedFeeling = (String) spinnerFeelings.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        Button save = (Button) addFeelingView.findViewById(R.id.btnSave);

        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog = ProgressDialog.show(getActivity(), "Loading", "Please wait...", true);
                new SaveFeelingTask().execute(selectedFeeling, because.getText().toString(), so.getText().toString());
            }
        });

        TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tm.getNetworkOperatorName();
        if ("".equals(networkOperator)) {
            // Emulator
        } else {
            getActivity().startService(new Intent(TrackingService.ACTION_START_MONITORING));
            // Device
        }

        return addFeelingView;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            currentLatitude = intent.getDoubleExtra("latitude", 0);
            currentLongitude = intent.getDoubleExtra("longitude", 0);

            Geocoder gcd = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
            List<Address> addresses = null;

            try {
                addresses = gcd.getFromLocation(currentLatitude, currentLongitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

             //location.setText(String.format("%s%s", currentLatitude.toString(), currentLongitude.toString()));
        }
    };


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnCreateFeelingClick) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

//    private String getUserName() {
//        String name = "";
//        SharedPreferences settings = getActivity().getSharedPreferences("UserInfo", 0);
//        if (settings != null) {
//
//            name = settings.getString("Username", "").toString();
//        }
//        return name;
//    }

    private SpinnerAdapter typeSpinnerAdapter = new BaseAdapter() {

        private TextView text;
        //  private int count = 3;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflator.inflate(R.layout.row_spinner, null);
            }
            text = (TextView) convertView.findViewById(R.id.spinnerTarget);
            if (!selected) {
                text.setText("Please select a value");
            } else {
                text.setText(dbDefinedFeelings.get(position));
            }
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return dbDefinedFeelings.get(position);
        }

        @Override
        public int getCount() {
            return dbDefinedFeelings.size();
        }

        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflator.inflate(R.layout.customspinner, null);
            }
            text = (TextView) convertView.findViewById(R.id.text);
            text.setText(dbDefinedFeelings.get(position));
            return convertView;
        }

    };

    private AdapterView.OnItemSelectedListener typeSelectedListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            Log.d(TAG, "user selected : "
                    + spinnerFeelings.getSelectedItem().toString());

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private View.OnTouchListener typeSpinnerTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            selected = true;
            ((BaseAdapter) typeSpinnerAdapter).notifyDataSetChanged();
            return false;
        }
    };

    private class SaveFeelingTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (!s.equals("Failure")) {
                dialog.dismiss();
                System.out.println("OUTPUT:" + s);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                Type collectionType = new TypeToken<List<Feeling>>() {
                }.getType();
                relatedFeelings = gson.fromJson(s, collectionType);
                mCallback.onFeelingCreated(_feeling, relatedFeelings);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            List<NameValuePair> args = new ArrayList<NameValuePair>();
            args.add(new BasicNameValuePair("feelingText", params[0]));
            _feeling.setFeelingText(params[0]);
            args.add(new BasicNameValuePair("reason", params[1]));
            _feeling.setReason(params[1]);
            args.add(new BasicNameValuePair("action", params[2]));
            _feeling.setAction(params[2]);
            args.add(new BasicNameValuePair("username", username));
            args.add(new BasicNameValuePair("latitude", currentLatitude.toString()));
            args.add(new BasicNameValuePair("longitude", currentLongitude.toString()));
            _feeling.setUserName(username);
//            args.add(new BasicNameValuePair("user", parentData.toString()));
            //  args.add(new BasicNameValuePair("content", parentData.toString()));
            JsonHttpClient jsonHttpClient = new JsonHttpClient(applicationHelper);
            return jsonHttpClient.PostParams(UrlHelper.FEELINGS, args);
        }
    }
}
