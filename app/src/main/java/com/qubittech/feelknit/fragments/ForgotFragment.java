package com.qubittech.feelknit.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qubittech.feelknit.app.R;

/**
 * Created by Manoj on 13/09/2014.
 */
public class ForgotFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View forgotView = inflater.inflate(R.layout.forgot, container, false);

        return forgotView;
    }
}
