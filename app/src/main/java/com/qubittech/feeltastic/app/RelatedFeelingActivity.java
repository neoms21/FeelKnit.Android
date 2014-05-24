package com.qubittech.feeltastic.app;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.qubittech.feeltastic.adapters.FeelingsAdapter;
import com.qubittech.feeltastic.models.Feeling;

import java.util.List;

/**
 * Created by Manoj on 13/05/2014.
 */
public class RelatedFeelingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.related_feelings);
        Feeling feeling = (Feeling) getIntent().getSerializableExtra("feeling");
        List<Feeling> feelings = (List<Feeling>) getIntent().getSerializableExtra("relatedFeelings");

        TextView feelTextView = (TextView) findViewById(R.id.feelingText);
        TextView reasonTextView = (TextView) findViewById(R.id.becauseText);
        TextView actionTextView = (TextView) findViewById(R.id.soText);
        TextView count = (TextView) findViewById(R.id.countLabel);

        feelTextView.setText(feeling.getFeelingText());
        count.setText(String.format("%d People feeling %s recently", feelings.size(), feeling.getFeelingText()));


        ArrayAdapter arrayAdapter = new FeelingsAdapter(RelatedFeelingActivity.this, R.layout.listview, feelings);

        ListView listview = (ListView) findViewById(R.id.relatedFeelingsList);
        // endTime = (System.nanoTime() - startTime) / 1000000000;


        listview.setAdapter(arrayAdapter);
        listview.setDivider(new ColorDrawable(0x99000000));
        listview.setDividerHeight(2);

        arrayAdapter.notifyDataSetChanged();


        //Intent feelingIntent = getIntent()
    }
}
