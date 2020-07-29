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
//    private RecyclerView recyclerView;
//    private AdapterRecent adapterRecent;
//    private SwipeRefreshLayout swipeRefreshLayout;
//    private ShimmerFrameLayout lyt_shimmer;
//    TimePickerDialog picker;
    EditText eTextTimePick;
    Button btnStart;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_recent, container, false);

        setHasOptionsMenu(true);

        TimePicker picker=(TimePicker)root_view.findViewById(R.id.timePicker);
        picker.setIs24HourView(true);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // Do something for lollipop and above versions
            picker.setHour(0);
            picker.setMinute(25);
        } else{
            // do something for phones running an SDK before lollipop
            picker.setCurrentHour(0);
            picker.setCurrentMinute(25);
        }

/*
        eTextTimePick=(EditText) root_view.findViewById(R.id.editTextTimePick);
        eTextTimePick.setInputType(InputType.TYPE_NULL);

        eTextTimePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int hour = 0;
                int minutes = 25;
                // time picker dialog
                picker = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                eTextTimePick.setText(sHour + ":" + sMinute);
                            }
                        }, hour, minutes, true);

                picker.show();
            }
        });
*/
        btnStart=(Button)root_view.findViewById(R.id.buttonTimePick);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int iChoosenHour = 0, iChoosenMin = 0;

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    // Do something for lollipop and above versions
                    Log.e("timertest", "choosen hour : " + picker.getHour() + " choosen min : " + picker.getMinute());
                    iChoosenHour = picker.getHour();
                    iChoosenMin = picker.getMinute();
                } else{
                    // do something for phones running an SDK before lollipop
                    Log.e("timertest", "choosen time : " + picker.getCurrentHour() + " choosen min : " + picker.getCurrentMinute());
                    iChoosenHour = picker.getCurrentHour();
                    iChoosenMin = picker.getCurrentMinute();
                }

                Intent intent = new Intent(getContext(), ActivityRoom.class);
                intent.putExtra("choosenHour", iChoosenHour);
                intent.putExtra("choosenMin", iChoosenMin);
                getContext().startActivity(intent);
            }
        });

//        lyt_shimmer = root_view.findViewById(R.id.shimmer_view_container);
//        swipeRefreshLayout = root_view.findViewById(R.id.swipe_refresh_layout_home);
//        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

//        recyclerView = root_view.findViewById(R.id.recyclerView);

//        recyclerView.addItemDecoration(new EqualSpacingItemDecoration(0));
//        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
//        recyclerView.setHasFixedSize(true);

        /*TODO: WILL BE OPENED AFTER ADDING AN ADAPTER
        //set data and list adapter
        adapterRecent = new AdapterRecent(getActivity(), recyclerView);
        recyclerView.setAdapter(adapterRecent);

        // on item list clicked
        adapterRecent.setOnItemClickListener((v, obj, position) -> {

        });

        // detect when scroll reach bottom
        adapterRecent.setOnLoadMoreListener(current_page -> {

        });
*/

        // on swipe list
/*
        swipeRefreshLayout.setOnRefreshListener(() -> {

        });
*/
//TODO: THIS WILL BE USED FOR CONTENT FETCH
// requestAction(1);
//        initShimmerLayout();
//        showNoItemView(true);

        return root_view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        swipeProgress(false);

//        lyt_shimmer.stopShimmer();
    }


/*
    private void showNoItemView(boolean show) {
        View lyt_no_item = root_view.findViewById(R.id.lyt_no_item_home);
        ((TextView) root_view.findViewById(R.id.no_item_message)).setText(R.string.msg_no_item);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_no_item.setVisibility(View.GONE);
        }
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
            swipeRefreshLayout.setRefreshing(show);
//            lyt_shimmer.setVisibility(View.GONE);
//            lyt_shimmer.stopShimmer();
            return;
        }
        swipeRefreshLayout.post(() -> {
            swipeRefreshLayout.setRefreshing(show);
//            lyt_shimmer.setVisibility(View.VISIBLE);
//            lyt_shimmer.startShimmer();
        });
    }
*/

/*
    private void initShimmerLayout() {
        View lyt_shimmer_default = root_view.findViewById(R.id.lyt_shimmer_default);
        View lyt_shimmer_compact = root_view.findViewById(R.id.lyt_shimmer_compact);

        lyt_shimmer_default.setVisibility(View.GONE);
        lyt_shimmer_compact.setVisibility(View.VISIBLE);

    }
*/
    @Override
    public void onResume() {
        super.onResume();
    }

}
