package com.qubittech.feeltastic.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.qubittech.feeltastic.models.Feeling;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import util.JsonHttpClient;
import util.UrlHelper;

/**
 * Created by Manoj on 04/05/2014.
 */
public class AddFeelingActivity extends Activity {

    private static String username = "";
    private String[] feelings = {"Happy", "Sad", "Excited", "Interested", "King", "Loser"};
    private Spinner spinnerFeelings;
    private String selectedFeeling = "";
    ProgressDialog dialog;
    private Feeling _feeling = null;
    private List<Feeling> _feelings = null;

    private String TAG = "SpinnerHint";

    private LayoutInflater mInflator;
    private boolean selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        _feeling = new Feeling();
        setContentView(R.layout.activity_feeling);
        spinnerFeelings = (Spinner) findViewById(R.id.feelingText);
        username = getUserName();

        final EditText because = (EditText) findViewById(R.id.becauseText);
        final EditText so = (EditText) findViewById(R.id.soText);

        spinnerFeelings.setAdapter(typeSpinnerAdapter);
        spinnerFeelings.setOnItemSelectedListener(typeSelectedListener);
        spinnerFeelings.setOnTouchListener(typeSpinnerTouchListener);
        mInflator = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

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

        Button save = (Button) findViewById(R.id.btnSave);

        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog = ProgressDialog.show(AddFeelingActivity.this, "Loading", "Please wait...", true);
                new SaveFeelingTask().execute(selectedFeeling, because.getText().toString(), so.getText().toString());
            }
        });
    }

    private String getUserName() {
        String name = "";
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        if (settings != null) {

            name = settings.getString("Username", "").toString();
        }
        return name;
    }

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
                text.setText(feelings[position]);
            }
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return feelings[position];
        }

        @Override
        public int getCount() {
            return feelings.length;
        }

        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflator.inflate(
                        R.layout.customspinner, null);
            }
            text = (TextView) convertView.findViewById(R.id.text);
            text.setText(feelings[position]);
            return convertView;
        }

        ;
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

            if (s != "Failure") {
                dialog.dismiss();
                System.out.println("OUTPUT:" + s);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                Type collectionType = new TypeToken<List<Feeling>>() {
                }.getType();
                _feelings = (List<Feeling>) gson.fromJson(s, collectionType);

                Intent intent = new Intent(AddFeelingActivity.this, RelatedFeelingActivity.class);
                intent.putExtra("feeling", _feeling);
                intent.putExtra("relatedFeelings", (java.io.Serializable) _feelings);
                startActivity(intent);
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
            _feeling.setUserName(username);
//            args.add(new BasicNameValuePair("user", parentData.toString()));
            //  args.add(new BasicNameValuePair("content", parentData.toString()));
            JsonHttpClient jsonHttpClient = new JsonHttpClient();
            return jsonHttpClient.PostParams(UrlHelper.FEELINGS, args);
        }
    }
}
