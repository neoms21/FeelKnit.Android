package com.qubittech.feelknit.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.qubittech.feelknit.adapters.FeelingsListAdapter;
import com.qubittech.feelknit.app.R;
import com.qubittech.feelknit.models.Feeling;
import com.qubittech.feelknit.services.TrackingService;
import com.qubittech.feelknit.util.ApplicationHelper;
import com.qubittech.feelknit.util.JsonHttpClient;
import com.qubittech.feelknit.util.UrlHelper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddFeelingFragment extends BackHandledFragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static String username = "";
    private static List<String> dbDefinedFeelings = new ArrayList<>();
    private String selectedFeeling = "";
    private ListView feelTextsListView;
    private TextView dropdownTextView;
    private EditText because;

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

        buildGoogleApiClient();
        Type collectionType = new TypeToken<List<String>>() {
        }.getType();
        Gson gson = new GsonBuilder().create();
        dbDefinedFeelings = gson.fromJson(ApplicationHelper.getFeelTexts(getActivity().getApplicationContext()), collectionType);
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    ProgressDialog dialog;
    private Feeling _feeling = null;
    private List<Feeling> relatedFeelings = null;

    private OnCreateFeelingClick mCallback;
    private Double currentLatitude = 0.0;
    private Double currentLongitude = 0.0;
    protected GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LinearLayout dropdownLinearLayout;

    // Container Activity must implement this interface
    public interface OnCreateFeelingClick {
        public void onFeelingCreated(Feeling feeling, List<Feeling> relatedFeelings);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View addFeelingView = inflater.inflate(R.layout.activity_feeling, container, false);
        _feeling = new Feeling();
        // spinnerFeelings = (Spinner) addFeelingView.findViewById(R.id.feelingSpinner);
        username = ApplicationHelper.getUserName(getActivity().getApplicationContext());

        because = (EditText) addFeelingView.findViewById(R.id.becauseText);
        final TextView so = (TextView) addFeelingView.findViewById(R.id.soText);
        dropdownTextView = (TextView) addFeelingView.findViewById(R.id.feeling_dropdown_textview);
        feelTextsListView = (ListView) addFeelingView.findViewById(R.id.feelingTextsListView);
        dropdownLinearLayout = (LinearLayout) addFeelingView.findViewById(R.id.dropdown_foldout_menu);
        final ArrayAdapter feelTextAdapter = new FeelingsListAdapter(getActivity(), R.layout.listview, dbDefinedFeelings, selectedFeeling);
        feelTextsListView.setAdapter(feelTextAdapter);
        feelTextAdapter.notifyDataSetChanged();
        dropdownTextView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dropdownLinearLayout.getVisibility() == View.GONE) {
                            openDropdown();
                        } else {
                            closeDropdown();
                        }
                    }
                });

        feelTextsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                selectedFeeling = dbDefinedFeelings.get(i);
                dropdownTextView.setText(selectedFeeling);
                closeDropdown();
                FeelingsListAdapter.selectedFeeling = selectedFeeling;
                feelTextAdapter.notifyDataSetChanged();
            }
        });

        Button save = (Button) addFeelingView.findViewById(R.id.btnSave);

        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(selectedFeeling.equals(""))
                {
                    Toast.makeText(getActivity().getApplicationContext(),"Please select a feeling", Toast.LENGTH_SHORT).show();
                    return;
                }
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

    /**
     * Animates in the dropdown list
     */
    private void openDropdown() {
        if (dropdownLinearLayout.getVisibility() != View.VISIBLE) {
            ScaleAnimation anim = new ScaleAnimation(1, 1, 0, 1);
            anim.setDuration(getResources().getInteger(R.integer.dropdown_amination_time));
            dropdownLinearLayout.startAnimation(anim);
            dropdownTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icn_dropdown_close, 0);
//            feelTextsListView
            dropdownLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    private void closeDropdown() {
        if (feelTextsListView.getVisibility() == View.VISIBLE) {
            ScaleAnimation anim = new ScaleAnimation(1, 1, 1, 0);
            anim.setDuration(getResources().getInteger(R.integer.dropdown_amination_time));
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    dropdownLinearLayout.setVisibility(View.GONE);
                }
            });
            dropdownTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icn_dropdown_open, 0);
            dropdownLinearLayout.startAnimation(anim);
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            currentLatitude = intent.getDoubleExtra("latitude", 0);
            currentLongitude = intent.getDoubleExtra("longitude", 0);

            Geocoder gcd = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
            List<Address> addresses = null;

            try {
                addresses = gcd.getFromLocation(currentLatitude, currentLongitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

          //  tempLocation.setText(String.format("%s%s - %s", currentLatitude.toString(), currentLongitude.toString(), addresses.get(0).getLocality()));
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

    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            currentLatitude = mLastLocation.getLatitude();
            currentLongitude = mLastLocation.getLongitude();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i("Feelknit", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i("Feelknit", "Connection suspended");
        mGoogleApiClient.connect();
    }

//    private View.OnTouchListener typeSpinnerTouchListener = new View.OnTouchListener() {
//
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            selected = true;
//            ((BaseAdapter) typeSpinnerAdapter).notifyDataSetChanged();
//            return false;
//        }
//    };

    private class SaveFeelingTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (because != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(because.getWindowToken(), 0);
            }
            if (!s.equals("Failure")) {
                getActivity().getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                dialog.dismiss();
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                Type collectionType = new TypeToken<List<Feeling>>() {
                }.getType();
                relatedFeelings = gson.fromJson(s, collectionType);
                mCallback.onFeelingCreated(_feeling, relatedFeelings);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            List<NameValuePair> args = new ArrayList<>();
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
            JsonHttpClient jsonHttpClient = new JsonHttpClient(getActivity().getApplicationContext());
            return jsonHttpClient.PostParams(UrlHelper.FEELINGS, args);
        }
    }
}
