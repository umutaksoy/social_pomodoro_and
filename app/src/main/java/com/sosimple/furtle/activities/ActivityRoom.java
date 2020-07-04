package com.sosimple.furtle.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.sosimple.furtle.R;
import com.sosimple.furtle.models.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.nikartm.support.BadgePosition;
import ru.nikartm.support.ImageBadgeView;

public class ActivityRoom extends AppCompatActivity {
    final Context context = this;
    private AppBarLayout appBarLayout;
    static final String TAG = "ActivityRoom";

    Activity act = this;

    int iChoosenHour, iChoosenMin;
    long lCountdownSecond;
    long lCountdownLeftTime = 0;
    boolean bIsWorkingTime = true;
    boolean bIsPauseSituation = true;
    ImageView playPauseButton;
    TextView workingRestingText;
    TextView countdownText;

    private CountDownTimer countDownTimer;
    boolean bCountDownIsNeededToSet = true;

    //user variables
    boolean bIsUserLoggedIn = false;
    User myUser;
    //profile picture
    ImageBadgeView myImage;
    //username
    TextView myUserText;

    //room's users
    List<User> roomUsers;
    LinearLayout user1, user2, user3, user4, user5, user6, user7, user8;
    LinearLayout user9, user10, user11, user12, user13, user14, user15, user16;
    ImageBadgeView user1image, user2image, user3image, user4image;
    ImageBadgeView user5image, user6image, user7image, user8image;
    ImageBadgeView user9image, user10image, user11image, user12image;
    ImageBadgeView user13image, user14image, user15image, user16image;
    TextView user1text, user2text, user3text, user4text, user5text, user6text, user7text, user8text;
    TextView user9text, user10text, user11text, user12text, user13text, user14text, user15text, user16text;

    LinearLayout roomPanel;
    LinearLayout signInPanel;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_room);

        appBarLayout = (AppBarLayout) findViewById(R.id.appbarLayout);
        appBarLayout.setExpanded(true);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                isShow = false;

            }
        });

        //TODO:if the room has less than 10 online users, show the bot users' data with random number of online users (for example bot users can be between 10-20)

        //USER'S COUNTDOWN AND BUTTONS
        playPauseButton = (ImageView) findViewById(R.id.playPauseButton);
        workingRestingText = (TextView) findViewById(R.id.workingText);
        countdownText = (TextView) findViewById(R.id.countdownText);

        //User's badge settings
        myUser = new User();
        myUserText = (TextView) findViewById(R.id.myUserTxt);
        myImage = (ImageBadgeView) findViewById(R.id.myprofile);
        updateBadge(myImage, getActualPomodoro());

        //Room's users image and texts
        user1 = (LinearLayout) findViewById(R.id.user1);
        user2 = (LinearLayout) findViewById(R.id.user2);
        user3 = (LinearLayout) findViewById(R.id.user3);
        user4 = (LinearLayout) findViewById(R.id.user4);
        user5 = (LinearLayout) findViewById(R.id.user5);
        user6 = (LinearLayout) findViewById(R.id.user6);
        user7 = (LinearLayout) findViewById(R.id.user7);
        user8 = (LinearLayout) findViewById(R.id.user8);
        user9 = (LinearLayout) findViewById(R.id.user9);
        user10 = (LinearLayout) findViewById(R.id.user10);
        user11 = (LinearLayout) findViewById(R.id.user11);
        user12 = (LinearLayout) findViewById(R.id.user12);
        user13 = (LinearLayout) findViewById(R.id.user13);
        user14 = (LinearLayout) findViewById(R.id.user14);
        user15 = (LinearLayout) findViewById(R.id.user15);
        user16 = (LinearLayout) findViewById(R.id.user16);

        user1image = (ImageBadgeView) findViewById(R.id.user1profile);
        user2image = (ImageBadgeView) findViewById(R.id.user2profile);
        user3image = (ImageBadgeView) findViewById(R.id.user3profile);
        user4image = (ImageBadgeView) findViewById(R.id.user4profile);
        user5image = (ImageBadgeView) findViewById(R.id.user5profile);
        user6image = (ImageBadgeView) findViewById(R.id.user6profile);
        user7image = (ImageBadgeView) findViewById(R.id.user7profile);
        user8image = (ImageBadgeView) findViewById(R.id.user8profile);
        user9image = (ImageBadgeView) findViewById(R.id.user9profile);
        user10image = (ImageBadgeView) findViewById(R.id.user10profile);
        user11image = (ImageBadgeView) findViewById(R.id.user11profile);
        user12image = (ImageBadgeView) findViewById(R.id.user12profile);
        user13image = (ImageBadgeView) findViewById(R.id.user13profile);
        user14image = (ImageBadgeView) findViewById(R.id.user14profile);
        user15image = (ImageBadgeView) findViewById(R.id.user15profile);
        user16image = (ImageBadgeView) findViewById(R.id.user16profile);

        user1text = (TextView) findViewById(R.id.user1name);
        user2text = (TextView) findViewById(R.id.user2name);
        user3text = (TextView) findViewById(R.id.user3name);
        user4text = (TextView) findViewById(R.id.user4name);
        user5text = (TextView) findViewById(R.id.user5name);
        user6text = (TextView) findViewById(R.id.user6name);
        user7text = (TextView) findViewById(R.id.user7name);
        user8text = (TextView) findViewById(R.id.user8name);
        user9text = (TextView) findViewById(R.id.user9name);
        user10text = (TextView) findViewById(R.id.user10name);
        user11text = (TextView) findViewById(R.id.user11name);
        user12text = (TextView) findViewById(R.id.user12name);
        user13text = (TextView) findViewById(R.id.user13name);
        user14text = (TextView) findViewById(R.id.user14name);
        user15text = (TextView) findViewById(R.id.user15name);
        user16text = (TextView) findViewById(R.id.user16name);

        roomPanel = (LinearLayout) findViewById(R.id.roomContainer);
        signInPanel = (LinearLayout) findViewById(R.id.buttonsContainer);
        signInPanel.setVisibility(View.GONE);

        //GET CHOOSEN HOUR AND CHOOSEN MINUTE DATA THAT SET IN PREVIOUS SCREEN
        Intent tmpIntent = getIntent();

        try {
            iChoosenHour = tmpIntent.getIntExtra("choosenHour", 0);
        }catch (Exception e){
            iChoosenHour = 0;
        }

        try {
            iChoosenMin = tmpIntent.getIntExtra("choosenMin", 0);
        }catch (Exception e){
            iChoosenMin = 0;
        }

        lCountdownSecond = getCountdownMiliSecond();
        lCountdownLeftTime = lCountdownSecond;

        //Set the Countdown Timer Text
        setCountdownText(lCountdownSecond);

        //PLAY/PAUSE BUTTON CLICK EVENT
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                bIsPauseSituation = !bIsPauseSituation;

                Log.e("umutplaypause", "play button onClick pausesituation : " + bIsPauseSituation);
                Log.e("umutplaypause", "play button onClick lcountdowntime : " + lCountdownLeftTime);

                if (bIsPauseSituation){
                    //show play button
                    setPlayPauseButtonImage(true);
                    //pause the countdown timer
                    pauseCountdownTime();
                }else{
                    //show pause button
                    setPlayPauseButtonImage(false);

                    //start the countdown timer again
                    if (lCountdownLeftTime > 0)
                        startCountdownTime(lCountdownLeftTime);
                }

            }

        });

        //Check the user is logged in or not
        checkUserLoggedIn();

        //If the user is logged in, Get the user's username, profile image
        if (bIsUserLoggedIn){
            //hide sign in button
            hideSignInButtons();

            myUser = getUserData();

            if (myUser != null){
                //update my user's profile picture
                if (!myUser.userImageUrl.equals(""))
                    updateProfileImage(myImage, myUser.userImageUrl);
                //update my user's username
                if (!myUser.userName.equals(""))
                    updateUserName(myUserText, myUser.userName);
                //send my actual pomodoro to room
                sendActualPomodoro();
            }

        }

        //If the user is logged in, join the room and get the room's users data
        if (bIsUserLoggedIn){
            roomUsers = getRoomActualData();
            updateRoomActualDataPeriodically();
        }

        //If the user is not logged in, show the signin button to be able to join the room
        if (!bIsUserLoggedIn){
            showSignInButtons();

            //update room badges as 0
            updateBadge(user1image, 0);
            updateBadge(user2image, 0);
            updateBadge(user3image, 0);
            updateBadge(user4image, 0);
            updateBadge(user5image, 0);
            updateBadge(user6image, 0);
            updateBadge(user7image, 0);
            updateBadge(user8image, 0);
            updateBadge(user9image, 0);
            updateBadge(user10image, 0);
            updateBadge(user11image, 0);
            updateBadge(user12image, 0);
            updateBadge(user13image, 0);
            updateBadge(user14image, 0);
            updateBadge(user15image, 0);
            updateBadge(user16image, 0);
        }

        //GOOGLE SIGN IN
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(view -> googleSignIn());

        //TOOLBAR WITH BACK BUTTON
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorToolbarDark));
        setSupportActionBar(toolbar);
        final androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_share, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed()
    {
        //pause the countdown timer
        if (countDownTimer != null){
            pauseCountdownTime();
        }

//        showInterstitialAd();
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs
    }

    @Override
    public void onDestroy() {
        // could be in onPause or onStop
        //pause the countdown timer
        if (countDownTimer != null){
            pauseCountdownTime();
        }

        super.onDestroy();
    }

    void finishWorkingResting(){
        //TODO: play the gong sound

        //reset the countdown timer
        bCountDownIsNeededToSet = true;

        //reverse the working time boolean value. (working >> resting, resting >> working)
        bIsWorkingTime = !bIsWorkingTime;

        setWorkingRestingText(bIsWorkingTime); //(workingRestingText : working >> resting, resting >> working)
        setPlayPauseButtonImage(bIsWorkingTime); //(playPauseButton : play >> pause, pause >> play)

        if (bIsWorkingTime){
            //the user finished the resting time, it is the working time for now

            //set countdown time for choosen working time, but don't start the countdown automatically
            lCountdownLeftTime = getCountdownMiliSecond();
            //set countdown text
            setCountdownText(lCountdownLeftTime);
        }else{
            //the user finished the working time, it is the resting time for now

            increaseActualPomodoro();
            sendActualPomodoro();
            getRoomActualData();

            //start countdown automatically for 5 min resting time
            startCountdownTime(5 * 60 * 1000);
        }


    }

    void setWorkingRestingText(boolean pBIsWorkingTime){

        if (pBIsWorkingTime){
            //set working/resting text as "it's working time"
            workingRestingText.setText(getString(R.string.working_text));
        }else{
            //set working/resting text as "it's resting time"
            workingRestingText.setText(getString(R.string.resting_text));
        }

    }

    void setPlayPauseButtonImage(boolean pBIsPlayShown){

        if (pBIsPlayShown){
            //set play/pause button's image as play
            playPauseButton.setImageResource(R.drawable.play_button);
        }else{
            //set play/pause button's image as pause
            playPauseButton.setImageResource(R.drawable.pause_button);
        }

    }

    long getCountdownMiliSecond(){
        return (iChoosenHour * 60 + iChoosenMin) * 60 * 1000; //conversion to the milisecond;
    }

    void setCountdownText(long lTmpTime){
        int iLeftHour = 0, iLeftMin = 0, iLeftSecond = 0;

        if (lTmpTime > 0){
            iLeftHour = (int)(lTmpTime / 1000 / 60 / 60);
            iLeftMin = (int)((lTmpTime - (iLeftHour * 1000 * 60 * 60)) / 1000 / 60);
            iLeftSecond = (int)((lTmpTime - (iLeftHour * 1000 * 60 * 60) - (iLeftMin * 1000 * 60)) / 1000);
        }

        String strTimeLeft = String.format(Locale.ENGLISH,"%02d:%02d:%02d", iLeftHour, iLeftMin, iLeftSecond);
        countdownText.setText(strTimeLeft);

    }

    void startCountdownTime(long lCountDown){

        Log.e("umutcountdown", "startCountdownTime bilgisi : " + lCountDown);

        bCountDownIsNeededToSet = false;

        countDownTimer = new CountDownTimer(lCountDown, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                lCountdownLeftTime = millisUntilFinished;

                setCountdownText(lCountdownLeftTime);
            }

            @Override
            public void onFinish() {
                finishWorkingResting();
            }

        }.start();

    }

    void pauseCountdownTime(){
        countDownTimer.cancel();
        setPlayPauseButtonImage(false);
    }

    void increaseActualPomodoro(){
        //increase the user's actual pomodoro
        int iNewActualPomodoro = getActualPomodoro() + 1;

        //write new pomodoro to the sharedpref
        setActualPomodoro(iNewActualPomodoro);

        //update user's badge
        updateBadge(myImage, getActualPomodoro());

    }

    int getActualPomodoro(){
        int iTmpActPomodoro = 0;

        try {
            String strActualDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            SharedPreferences preferences = act.getPreferences(Context.MODE_PRIVATE);
            String strActualPomodoroDate = preferences.getString(getString(R.string.shared_pref_pom_date), "");

            Log.e("umutsharedpref", "getActualPomodoro actualdate : " + strActualDate);
            Log.e("umutsharedpref", "getActualPomodoro actualpomodorodate : " + strActualPomodoroDate);

            if (strActualDate.equals(strActualPomodoroDate)){
                iTmpActPomodoro = preferences.getInt(getString(R.string.shared_pref_pom_count), 0);
            }

        }catch (Exception e){
            Log.e("umutsharedpref", "getActualPomodoro patladi, detay : " + e.toString());
        }

        return iTmpActPomodoro;
    }

    void setActualPomodoro(int iTmpNewPomodoro){

        try {
            String strActualDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            SharedPreferences preferences = act.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(getString(R.string.shared_pref_pom_date), strActualDate);
            editor.putInt(getString(R.string.shared_pref_pom_count), iTmpNewPomodoro);
            editor.commit();
        }catch (Exception e){
            Log.e("umutsharedpref", "setActualPomodoro patladi, detay : " + e.toString());
        }

    }

    void sendActualPomodoro(){
        //TODO:send the user's actual pomodoro to the backend

    }

    List<User> getRoomActualData(){
        //TODO:get the room's actual data
        //the number of online users, online users' profile pictures, usernames, actual pomodoros
        List<User> tmpRoomUsers = null;

        return tmpRoomUsers;
    }

    void updateRoomActualData(int iPage){
        roomUsers = getRoomActualData();

        if (roomUsers == null)
            return;

        //user1
        if (roomUsers.size() >= iPage * 16){
            updateUserName(user1text, roomUsers.get(iPage * 16).userName);
            updateProfileImage(user1image, roomUsers.get(iPage * 16).userImageUrl);
            updateBadge(user1image, roomUsers.get(iPage * 16).userPomodoro);
        }else{
            //hide user1
            user1.setVisibility(View.INVISIBLE);
        }

        //user2
        if (roomUsers.size() >= iPage * 16 + 1){
            updateUserName(user2text, roomUsers.get(iPage * 16 + 1).userName);
            updateProfileImage(user2image, roomUsers.get(iPage * 16 + 1).userImageUrl);
            updateBadge(user2image, roomUsers.get(iPage * 16 + 1).userPomodoro);
        }else{
            //hide user2
            user2.setVisibility(View.INVISIBLE);
        }

        //user3
        if (roomUsers.size() >= iPage * 16 + 2){
            updateUserName(user3text, roomUsers.get(iPage * 16 + 2).userName);
            updateProfileImage(user3image, roomUsers.get(iPage * 16 + 2).userImageUrl);
            updateBadge(user3image, roomUsers.get(iPage * 16 + 2).userPomodoro);
        }else{
            //hide user3
            user3.setVisibility(View.INVISIBLE);
        }

        //user4
        if (roomUsers.size() >= iPage * 16 + 3){
            updateUserName(user4text, roomUsers.get(iPage * 16 + 3).userName);
            updateProfileImage(user4image, roomUsers.get(iPage * 16 + 3).userImageUrl);
            updateBadge(user4image, roomUsers.get(iPage * 16 + 3).userPomodoro);
        }else{
            //hide user4
            user4.setVisibility(View.INVISIBLE);
        }

        //user5
        if (roomUsers.size() >= iPage * 16 + 4){
            updateUserName(user5text, roomUsers.get(iPage * 16 + 4).userName);
            updateProfileImage(user5image, roomUsers.get(iPage * 16 + 4).userImageUrl);
            updateBadge(user5image, roomUsers.get(iPage * 16 + 4).userPomodoro);
        }else{
            //hide user5
            user5.setVisibility(View.INVISIBLE);
        }

        //user6
        if (roomUsers.size() >= iPage * 16 + 5){
            updateUserName(user6text, roomUsers.get(iPage * 16 + 5).userName);
            updateProfileImage(user6image, roomUsers.get(iPage * 16 + 5).userImageUrl);
            updateBadge(user6image, roomUsers.get(iPage * 16 + 5).userPomodoro);
        }else{
            //hide user6
            user6.setVisibility(View.INVISIBLE);
        }

        //user7
        if (roomUsers.size() >= iPage * 16 + 6){
            updateUserName(user7text, roomUsers.get(iPage * 16 + 6).userName);
            updateProfileImage(user7image, roomUsers.get(iPage * 16 + 6).userImageUrl);
            updateBadge(user7image, roomUsers.get(iPage * 16 + 6).userPomodoro);
        }else{
            //hide user7
            user7.setVisibility(View.INVISIBLE);
        }

        //user8
        if (roomUsers.size() >= iPage * 16 + 7){
            updateUserName(user8text, roomUsers.get(iPage * 16 + 7).userName);
            updateProfileImage(user8image, roomUsers.get(iPage * 16 + 7).userImageUrl);
            updateBadge(user8image, roomUsers.get(iPage * 16 + 7).userPomodoro);
        }else{
            //hide user8
            user8.setVisibility(View.INVISIBLE);
        }

        //user9
        if (roomUsers.size() >= iPage * 16 + 8){
            updateUserName(user9text, roomUsers.get(iPage * 16 + 8).userName);
            updateProfileImage(user9image, roomUsers.get(iPage * 16 + 8).userImageUrl);
            updateBadge(user9image, roomUsers.get(iPage * 16 + 8).userPomodoro);
        }else{
            //hide user9
            user9.setVisibility(View.INVISIBLE);
        }

        //user10
        if (roomUsers.size() >= iPage * 16 + 9){
            updateUserName(user10text, roomUsers.get(iPage * 16 + 9).userName);
            updateProfileImage(user10image, roomUsers.get(iPage * 16 + 9).userImageUrl);
            updateBadge(user10image, roomUsers.get(iPage * 16 + 9).userPomodoro);
        }else{
            //hide user10
            user10.setVisibility(View.INVISIBLE);
        }

        //user11
        if (roomUsers.size() >= iPage * 16 + 10){
            updateUserName(user11text, roomUsers.get(iPage * 16 + 10).userName);
            updateProfileImage(user11image, roomUsers.get(iPage * 16 + 10).userImageUrl);
            updateBadge(user11image, roomUsers.get(iPage * 16 + 10).userPomodoro);
        }else{
            //hide user11
            user11.setVisibility(View.INVISIBLE);
        }

        //user12
        if (roomUsers.size() >= iPage * 16 + 11){
            updateUserName(user12text, roomUsers.get(iPage * 16 + 11).userName);
            updateProfileImage(user12image, roomUsers.get(iPage * 16 + 11).userImageUrl);
            updateBadge(user12image, roomUsers.get(iPage * 16 + 11).userPomodoro);
        }else{
            //hide user12
            user12.setVisibility(View.INVISIBLE);
        }

        //user13
        if (roomUsers.size() >= iPage * 16 + 12){
            updateUserName(user13text, roomUsers.get(iPage * 16 + 12).userName);
            updateProfileImage(user13image, roomUsers.get(iPage * 16 + 12).userImageUrl);
            updateBadge(user13image, roomUsers.get(iPage * 16 + 12).userPomodoro);
        }else{
            //hide user13
            user13.setVisibility(View.INVISIBLE);
        }

        //user14
        if (roomUsers.size() >= iPage * 16 + 13){
            updateUserName(user14text, roomUsers.get(iPage * 16 + 13).userName);
            updateProfileImage(user14image, roomUsers.get(iPage * 16 + 13).userImageUrl);
            updateBadge(user14image, roomUsers.get(iPage * 16 + 13).userPomodoro);
        }else{
            //hide user14
            user14.setVisibility(View.INVISIBLE);
        }

        //user15
        if (roomUsers.size() >= iPage * 16 + 14){
            updateUserName(user15text, roomUsers.get(iPage * 16 + 14).userName);
            updateProfileImage(user15image, roomUsers.get(iPage * 16 + 14).userImageUrl);
            updateBadge(user15image, roomUsers.get(iPage * 16 + 14).userPomodoro);
        }else{
            //hide user15
            user15.setVisibility(View.INVISIBLE);
        }

        //user16
        if (roomUsers.size() >= iPage * 16 + 15){
            updateUserName(user16text, roomUsers.get(iPage * 16 + 15).userName);
            updateProfileImage(user16image, roomUsers.get(iPage * 16 + 15).userImageUrl);
            updateBadge(user16image, roomUsers.get(iPage * 16 + 15).userPomodoro);
        }else{
            //hide user16
            user16.setVisibility(View.INVISIBLE);
        }

    }

    void updateRoomActualDataPeriodically(){
        int delaySecond = 60;

        //get the Room Actual Data for each X seconds
        Handler handler = new Handler();
        int delay = delaySecond * 1000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                updateRoomActualData(0);

                handler.postDelayed(this, delay);
            }
        }, delay);

    }

    void updateBadge(ImageBadgeView badgeView, int badgeNum){
        Typeface typeface = Typeface.createFromAsset(getAssets(), "exo_regular.ttf");

        badgeView.setBadgeValue(badgeNum)
                .setBadgeOvalAfterFirst(true)
                .setBadgeTextSize(14)
                .setMaxBadgeValue(99)
                .setBadgeTextFont(typeface)
                .setBadgeBackground(getResources().getDrawable(R.drawable.rectangle_rounded))
                .setBadgePosition(BadgePosition.TOP_RIGHT)
                .setBadgeTextStyle(Typeface.NORMAL)
                .setShowCounter(true)
                .setBadgePadding(1);
    }

    void updateProfileImage(ImageBadgeView profileImage, String imageUrl){

        if (imageUrl != null){

            if (!imageUrl.equals("")){

                try {
                    Glide
                            .with(context)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_nav_profile)
                            .into(profileImage);

                }catch (Exception e){
                    Log.e("glideerror", "updateProfileImage hatasi, detay : " + e.toString());
                }

            }

        }

    }

    void updateUserName(TextView txtUserName, String strUserName){
        txtUserName.setText(strUserName);
    }

    void checkUserLoggedIn(){
        bIsUserLoggedIn = false;

        try {
            SharedPreferences preferences = act.getPreferences(Context.MODE_PRIVATE);
            bIsUserLoggedIn = preferences.getBoolean(getString(R.string.sharedpref_login), false);

        }catch (Exception e){
            Log.e("umutsharedpref", "checkUserLoggedIn patladi, detay : " + e.toString());
        }

    }

    User getUserData(){

        User tmpUser = null;

        try {
            SharedPreferences preferences = act.getPreferences(Context.MODE_PRIVATE);
            bIsUserLoggedIn = preferences.getBoolean(getString(R.string.sharedpref_login), false);

            if (bIsUserLoggedIn){
                tmpUser = new User();
                tmpUser.userEmail = preferences.getString(getString(R.string.sharedpref_email), "");
                tmpUser.userName = preferences.getString(getString(R.string.sharedpref_name), "");
                tmpUser.userImageUrl = preferences.getString(getString(R.string.sharedpref_image), "");
                tmpUser.userPomodoro = getActualPomodoro();
            }

        }catch (Exception e){
            Log.e("umutsharedpref", "checkUserLoggedIn patladi, detay : " + e.toString());
        }

        return tmpUser;
    }

    void saveUserLoggedIn(){

        try {

            if (myUser != null){
                SharedPreferences preferences = act.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(getString(R.string.sharedpref_login), true);
                editor.putString(getString(R.string.sharedpref_email), myUser.userEmail);
                editor.putString(getString(R.string.sharedpref_name), myUser.userName);
                editor.putString(getString(R.string.sharedpref_image), myUser.userImageUrl);
                editor.putInt(getString(R.string.shared_pref_pom_count), myUser.userPomodoro);
                editor.commit();
            }

        }catch (Exception e){
            Log.e("umutsharedpref", "setActualPomodoro patladi, detay : " + e.toString());
        }

    }

    void googleSignIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Log.e("umutsignin", "onActivityResult signin calisiyor ");

            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {

        Log.e("umutsignin", "handleSignInResult calisiyor");

        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            Log.e("umutsignin", "handleSignInResult acount alindi");

            if (account != null){
                Log.e("umutsignin", "handleSignInResult account nulldan farkli");
                // Signed in successfully, show authenticated UI.
                myUser.userEmail = account.getEmail();
                myUser.userName = account.getDisplayName();

                if (account.getPhotoUrl() != null){
                    myUser.userImageUrl = account.getPhotoUrl().toString();
                }else{
                    myUser.userImageUrl = "";
                }

                saveUserLoggedIn();
                updateProfileImage(myImage, myUser.userImageUrl);
                updateUserName(myUserText, myUser.userName);
                hideSignInButtons();
                updateRoomActualData(0);

                new Handler().postDelayed(this::requestPostData, 200);
            }else{
                Log.e("umutsignin", "handleSignInResult account null geldi");
            }

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("umutsignin", "handleSignInResult patladi, detay : " + e.toString());
        }

    }

    void showSignInButtons(){
//        signInPanel.setAlpha(0.4f);
        roomPanel.setAlpha(0.3f);
        signInPanel.setVisibility(View.VISIBLE);
    }

    void hideSignInButtons(){
        roomPanel.setAlpha(1f);
        signInPanel.setVisibility(View.GONE);
    }

    private void requestPostData() {
        //TODO:bu metodun backende kullanici kaydi atmasi saglanacak,
        // benzer yapi fragmenaccount.java dosyasinda var

    }

    private void joinRoom(){
        //TODO:kullanici login oldu ve calismaya basladi, backend tarafinda odaya giris yaptir
    }

    private void exitRoom(){
        //TODO:kullanici ekrandan cikti veya logout oldu, backend tarafinda odadan cikisini yaptir
    }

}