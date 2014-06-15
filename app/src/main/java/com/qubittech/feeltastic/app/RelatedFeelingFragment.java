package com.qubittech.feeltastic.app;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.qubittech.feeltastic.adapters.RelatedFeelingsAdapter;
import com.qubittech.feeltastic.models.Feeling;

import java.util.List;

/**
 * Created by Manoj on 13/05/2014.
 */
public class RelatedFeelingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.related_feelings, container, false);


//        setContentView(R.layout.related_feelings);
        Bundle args = getArguments();

        Feeling feeling = (Feeling)args.getSerializable("feeling");
        List<Feeling> feelings = (List<Feeling>) args.getSerializable("relatedFeelings");

        TextView feelTextView = (TextView) mainView.findViewById(R.id.feelingText);
        TextView count = (TextView) mainView.findViewById(R.id.countLabel);

        feelTextView.setText(feeling.getFeelingFormattedText("I"));
        count.setText(String.format("%d People feeling %s recently", feelings.size(), feeling.getFeelingText()));

        ArrayAdapter arrayAdapter = new RelatedFeelingsAdapter(getActivity(), R.layout.listview, feelings);

        ListView listview = (ListView) mainView.findViewById(R.id.relatedFeelingsList);


        listview.setAdapter(arrayAdapter);
        listview.setDivider(new ColorDrawable());
        listview.setDividerHeight(10);

        arrayAdapter.notifyDataSetChanged();

        return mainView;
        //Intent feelingIntent = getIntent()
    }
}
