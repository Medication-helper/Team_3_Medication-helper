package com.cookandroid.medication_helper;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cookandroid.medication_helper.R;
import com.cookandroid.medication_helper.dateRegister;
import com.cookandroid.medication_helper.medicCheck;
import com.cookandroid.medication_helper.medicRegister;

public class homeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.home, container, false);

        Button btnMediReg = view.findViewById(R.id.btnMediReg);
        Button btnMediCheck = view.findViewById(R.id.btnMediCheck);
        Button btnAlarmSet = view.findViewById(R.id.btnAlarmset);

        btnMediReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mediRegIntent = new Intent(getActivity(), medicRegister.class);
                startActivity(mediRegIntent);
            }
        });

        btnMediCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mediCheckIntent = new Intent(getActivity(), medicCheck.class);
                startActivity(mediCheckIntent);
            }
        });

        btnAlarmSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent alarmSetIntent = new Intent(getActivity(), dateRegister.class);
                startActivity(alarmSetIntent);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}