package com.qubittech.feelknit.fragments;

import android.support.v4.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.qubittech.feelknit.adapters.RelatedFeelingsAdapter;
import com.qubittech.feelknit.app.R;
import com.qubittech.feelknit.models.Feeling;

import java.util.List;

/**
 * Created by Manoj on 13/05/2014.
 */
public class RelatedFeelingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.related_feelings, container, false);

        Bundle args = getArguments();
        if (args == null) {
        } else {
            Feeling feeling = (Feeling) args.getSerializable("feeling");
            List<Feeling> feelings = (List<Feeling>) args.getSerializable("relatedFeelings");

            TextView userNameTextView = (TextView) mainView.findViewById(R.id.name);

            TextView feelTextView = (TextView) mainView.findViewById(R.id.tvFeelingLabel);
            TextView count = (TextView) mainView.findViewById(R.id.countLabel);
            feeling.setFirstFeeling(true);
            userNameTextView.setText("I");
            feelTextView.setText(feeling.getFeelingFormattedText("I"));
            count.setText(String.format("%d %s feeling %s currently", feelings.size(), feelings.size() == 1 ? "person" : "people", feeling.getFeelingText()));

            ArrayAdapter arrayAdapter = new RelatedFeelingsAdapter(getActivity(), R.layout.listview, feelings);

            ListView listview = (ListView) mainView.findViewById(R.id.relatedFeelingsList);

            listview.setAdapter(arrayAdapter);
            listview.setDivider(new ColorDrawable());
            listview.setDividerHeight(10);

            arrayAdapter.notifyDataSetChanged();

        }

        return mainView; //Intent feelingIntent = getIntent()
    }
}
