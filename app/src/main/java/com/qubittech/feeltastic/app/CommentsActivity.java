package com.qubittech.feeltastic.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.qubittech.feeltastic.models.Feeling;

/**
 * Created by Manoj on 19/05/2014.
 */
public class CommentsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments);
        Feeling feeling = (Feeling) getIntent().getSerializableExtra("feeling");
        TextView feel = (TextView) findViewById(R.id.commentsFeelingText);
        feel.setText(feeling.getFeelingText());
    }
}
