package com.sosimple.furtle.utils;

import android.content.Context;
import android.util.Log;

import com.sosimple.furtle.R;

public class Tools {

    private Context context;

    public Tools(Context context) {
        this.context = context;
    }

    public static void getTheme(Context context) {
        SharedPref sharedPref = new SharedPref(context);
        if (sharedPref.getIsDarkTheme()) {
            context.setTheme(R.style.AppDarkTheme);
        } else {
            context.setTheme(R.style.AppTheme);
        }
    }

    public static void processLogs(int iLogType, String strLogMessage){
        //TODO:log this message to the server
        //0:debug, 1:warning, 2:error

        switch (iLogType){
            case 0:
                Log.d("umutlogging", strLogMessage);
                break;
            case 1:
                Log.w("umutlogging", strLogMessage);
                break;
            case 2:
                Log.e("umutlogging", strLogMessage);
                break;
        }

    }
}
