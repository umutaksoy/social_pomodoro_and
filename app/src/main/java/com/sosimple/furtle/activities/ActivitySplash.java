package com.sosimple.furtle.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.sosimple.furtle.R;
import com.sosimple.furtle.config.AppConfig;
import com.sosimple.furtle.utils.SharedPref;
import com.sosimple.furtle.utils.Tools;

public class ActivitySplash extends AppCompatActivity {

    Boolean isCancelled = false;
    private ProgressBar progressBar;
    String id = "0";
    String url = "";
    ImageView img_splash;
    SharedPref sharedPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        setContentView(R.layout.activity_splash);


        sharedPref = new SharedPref(this);
/*FOR DARK/LIGHT THEME SELECTION
        img_splash = findViewById(R.id.img_splash);
        if (sharedPref.getIsDarkTheme()) {
            img_splash.setImageResource(R.drawable.bg_splash_dark);
        } else {
            img_splash.setImageResource(R.drawable.bg_splash_default);
        }
*/
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        if(getIntent().hasExtra("nid")) {
            id = getIntent().getStringExtra("nid");
            url = getIntent().getStringExtra("external_link");
        }

        new Handler().postDelayed(() -> {
            if(!isCancelled) {
                if(id.equals("0")) {
                    if (url.equals("") || url.equals("no_url")) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent a = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(a);

                        Intent b = new Intent(Intent.ACTION_VIEW);
                        b.setData(Uri.parse(url));
                        startActivity(b);

                        finish();
                    }
                } else {
                    Intent a = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(a);
/*THIS IS FOR SHOWING SPECIFIC CONTENT THAT COMES FROM NOTIFICATION
                    Intent b = new Intent(getApplicationContext(), ActivityNotificationDetail.class);
                    b.putExtra("id", id);
                    startActivity(b);
*/
                    finish();
                }
            }
        }, AppConfig.SPLASH_TIME);

    }
}
