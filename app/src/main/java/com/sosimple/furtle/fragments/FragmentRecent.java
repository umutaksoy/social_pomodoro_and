package com.sosimple.furtle.fragments;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.sosimple.furtle.R;
import com.sosimple.furtle.activities.ActivityRoom;
import com.sosimple.furtle.utils.EqualSpacingItemDecoration;

import java.util.Calendar;

public class FragmentRecent extends Fragment {

    View root_view;
    Button btnStart;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_recent, container, false);

        setHasOptionsMenu(true);

        TimePicker picker=(TimePicker)root_view.findViewById(R.id.timePicker);
        picker.setIs24HourView(true);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // Default POMODORO working time is 25 min
            picker.setHour(0);
            picker.setMinute(25);
        } else{
            // Default POMODORO working time is 25 min
            picker.setCurrentHour(0);
            picker.setCurrentMinute(25);
        }

        btnStart=(Button)root_view.findViewById(R.id.buttonTimePick);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int iChoosenHour = 0, iChoosenMin = 0;

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    Log.e("umuttimertest", "choosen hour : " + picker.getHour() + " choosen min : " + picker.getMinute());
                    iChoosenHour = picker.getHour();
                    iChoosenMin = picker.getMinute();
                } else{
                    Log.e("umuttimertest", "choosen time : " + picker.getCurrentHour() + " choosen min : " + picker.getCurrentMinute());
                    iChoosenHour = picker.getCurrentHour();
                    iChoosenMin = picker.getCurrentMinute();
                }

                Intent intent = new Intent(getContext(), ActivityRoom.class);
                intent.putExtra("choosenHour", iChoosenHour);
                intent.putExtra("choosenMin", iChoosenMin);
                getContext().startActivity(intent);
            }
        });

        return root_view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

}
