package com.qubittech.feeltastic.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

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

        TextView feelTextView = (TextView) findViewById(R.id.tvFeelingText);
        TextView reasonTextView = (TextView) findViewById(R.id.etReason);
        TextView actionTextView = (TextView) findViewById(R.id.etAction);
        TextView count = (TextView) findViewById(R.id.countLabel);

        feelTextView.setText(feeling.getFeelingText());
        count.setText(feelings.size() + "no of people feel same");
        //Intent feelingIntent = getIntent()
    }
}
