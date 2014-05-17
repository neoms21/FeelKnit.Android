package com.qubittech.feeltastic.app;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.qubittech.feeltastic.models.Feeling;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import util.JsonHttpClient;

/**
 * Created by Manoj on 04/05/2014.
 */
public class AddFeelingActivity extends Activity {


    private String[] feelings = {"Happy", "Sad", "Excited", "Interested", "King", "Loser"};
    private Spinner spinnerFeelings;
    private String selectedFeeling = "";

    private Feeling _feeling = null;
    private List<Feeling> _feelings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        _feeling = new Feeling();
        setContentView(R.layout.activity_feeling);
        spinnerFeelings = (Spinner) findViewById(R.id.spinnerFeeling);
        ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, feelings);
        adapter_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFeelings.setAdapter(adapter_state);
        //spinnerFeelings.setOnItemSelectedListener(this);

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
                new SaveUserTask().execute(selectedFeeling, "reason", "action");
            }
        });


    }

    private class SaveUserTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != "Failure") {
                System.out.println("OUTPUT:" + s);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                Type collectionType = new TypeToken<List<Feeling>>(){}.getType();
                _feelings= (List<Feeling>) gson.fromJson(s, collectionType);

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
            JsonHttpClient jsonHttpClient = new JsonHttpClient();
            return jsonHttpClient.PostParams("http://10.0.3.2/FeelKnitService/feelings", args);
        }
    }
}
