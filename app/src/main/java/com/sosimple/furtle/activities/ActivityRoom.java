package com.sosimple.furtle.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.sosimple.furtle.R;
import com.sosimple.furtle.callbacks.CallBackSignup;
import com.sosimple.furtle.models.User;
import com.sosimple.furtle.rests.RestAdapter;
import com.sosimple.furtle.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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

    boolean bShowErrorMessage = false;

    //user variables
    User myUser;
    //profile picture
    ImageBadgeView myImage;
    //username
    TextView myUserText;

    //room's users
//    List<User> roomUsers;
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
    private Call<CallBackSignup> callbackCall = null;

    private String strBackendToken = null;
    private boolean bJoinNeeded = false;
    private boolean bFocusIsNeeded = false;
    //socketio
    private Socket mSocket = null;
    private String strSocketJoin = "join", strSocketFocus = "focus", strSocketJoined = "joined";
    private String strSocketFocusEnded = "focus ended", strSocketRoomInsidersChanged = "room insiders changed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO:log all details, especially errors (using tools.processLogs)

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

        //initialization
        myUser = new User();
        strBackendToken = null;
        bJoinNeeded = false;

        //TODO:if the room has less than 10 online users, show the bot users' data with random number of online users (for example bot users can be between 10-20)

        //USER'S COUNTDOWN AND BUTTONS
        playPauseButton = (ImageView) findViewById(R.id.playPauseButton);
        workingRestingText = (TextView) findViewById(R.id.workingText);
        countdownText = (TextView) findViewById(R.id.countdownText);

        //User's badge settings
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

        //GOOGLE SIGN IN
        initGoogleSignIn();

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
                    pauseCountdownTime();
                    //show play button
                    setPlayPauseButtonImage(true);
                    //reset the countdown timer
                    setCountdownText(getCountdownMiliSecond());
                    //try to send focus ended code to socket.io
                    unfocusInRoom();
                }else{
                    //show pause button
                    setPlayPauseButtonImage(false);
/*This is for pause, now we are using stop instead of pause. So, reset the countdown timer
                    //start the countdown timer again
                    if (lCountdownLeftTime > 0)
                        startCountdownTime(lCountdownLeftTime);
*/
                    startCountdownTime(getCountdownMiliSecond());

                    //try to send focus code to socket.io
                    focusInRoom();
                }

            }

        });

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

    private void initGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1066845752646-p4rhfb1vebfrkdm4c3l7v7h5028t77mq.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

        //try to check the user is signed in or not, silently
        mGoogleSignInClient.silentSignIn()
                .addOnCompleteListener(
                        this,
                        new OnCompleteListener<GoogleSignInAccount>() {
                            @Override
                            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                                handleSignInResult(task);
                            }
                        });

        //add google sign in button to view
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(view -> googleSignIn());

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

        if (mSocket != null){
            mSocket.disconnect();
            mSocket.off(strSocketJoin);
            mSocket.off(strSocketJoined, onJoined);
            mSocket.off(strSocketRoomInsidersChanged, roomInsidersChanged);
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

        if (callbackCall != null) {

            if (callbackCall.isCanceled()){
                this.callbackCall.cancel();
            }

        }

        if (mSocket != null){
            mSocket.disconnect();
            mSocket.off(strSocketJoin);
            mSocket.off(strSocketJoined, onJoined);
            mSocket.off(strSocketRoomInsidersChanged, roomInsidersChanged);
        }

        super.onDestroy();
    }

    void finishWorkingResting(){
        //play the gong sound
        ringTheBell();

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
        setPlayPauseButtonImage(true);
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

    void updateRoomActualData(List<User> tmpUsers, int iPage){

        boolean bIsNoUser = false;

        if (tmpUsers == null){
            bIsNoUser = true;
        }else if(tmpUsers.size() == 0){
            bIsNoUser = true;
        }

        if (bIsNoUser){
            //hide users
            user1.setVisibility(View.INVISIBLE);
            user2.setVisibility(View.INVISIBLE);
            user3.setVisibility(View.INVISIBLE);
            user4.setVisibility(View.INVISIBLE);
            user5.setVisibility(View.INVISIBLE);
            user6.setVisibility(View.INVISIBLE);
            user7.setVisibility(View.INVISIBLE);
            user8.setVisibility(View.INVISIBLE);
            user9.setVisibility(View.INVISIBLE);
            user10.setVisibility(View.INVISIBLE);
            user11.setVisibility(View.INVISIBLE);
            user12.setVisibility(View.INVISIBLE);
            user13.setVisibility(View.INVISIBLE);
            user14.setVisibility(View.INVISIBLE);
            user15.setVisibility(View.INVISIBLE);
            user16.setVisibility(View.INVISIBLE);
            sendToastMessage("There is no one in the room");
            return;

        }

        Log.e("umutsocket", "updateRoomActualData list size : " + tmpUsers.size());

        //user1
        if (tmpUsers.size() > iPage * 16){
            updateUserName(user1text, tmpUsers.get(iPage * 16).userName);
            updateProfileImage(user1image, tmpUsers.get(iPage * 16).userImageUrl);
            updateBadge(user1image, tmpUsers.get(iPage * 16).userPomodoro);
        }else{
            //hide user1
            user1.setVisibility(View.INVISIBLE);
        }

        //user2
        if (tmpUsers.size() > iPage * 16 + 1){
            updateUserName(user2text, tmpUsers.get(iPage * 16 + 1).userName);
            updateProfileImage(user2image, tmpUsers.get(iPage * 16 + 1).userImageUrl);
            updateBadge(user2image, tmpUsers.get(iPage * 16 + 1).userPomodoro);
        }else{
            //hide user2
            user2.setVisibility(View.INVISIBLE);
        }

        //user3
        if (tmpUsers.size() > iPage * 16 + 2){
            updateUserName(user3text, tmpUsers.get(iPage * 16 + 2).userName);
            updateProfileImage(user3image, tmpUsers.get(iPage * 16 + 2).userImageUrl);
            updateBadge(user3image, tmpUsers.get(iPage * 16 + 2).userPomodoro);
        }else{
            //hide user3
            user3.setVisibility(View.INVISIBLE);
        }

        //user4
        if (tmpUsers.size() > iPage * 16 + 3){
            updateUserName(user4text, tmpUsers.get(iPage * 16 + 3).userName);
            updateProfileImage(user4image, tmpUsers.get(iPage * 16 + 3).userImageUrl);
            updateBadge(user4image, tmpUsers.get(iPage * 16 + 3).userPomodoro);
        }else{
            //hide user4
            user4.setVisibility(View.INVISIBLE);
        }

        //user5
        if (tmpUsers.size() > iPage * 16 + 4){
            updateUserName(user5text, tmpUsers.get(iPage * 16 + 4).userName);
            updateProfileImage(user5image, tmpUsers.get(iPage * 16 + 4).userImageUrl);
            updateBadge(user5image, tmpUsers.get(iPage * 16 + 4).userPomodoro);
        }else{
            //hide user5
            user5.setVisibility(View.INVISIBLE);
        }

        //user6
        if (tmpUsers.size() > iPage * 16 + 5){
            updateUserName(user6text, tmpUsers.get(iPage * 16 + 5).userName);
            updateProfileImage(user6image, tmpUsers.get(iPage * 16 + 5).userImageUrl);
            updateBadge(user6image, tmpUsers.get(iPage * 16 + 5).userPomodoro);
        }else{
            //hide user6
            user6.setVisibility(View.INVISIBLE);
        }

        //user7
        if (tmpUsers.size() > iPage * 16 + 6){
            updateUserName(user7text, tmpUsers.get(iPage * 16 + 6).userName);
            updateProfileImage(user7image, tmpUsers.get(iPage * 16 + 6).userImageUrl);
            updateBadge(user7image, tmpUsers.get(iPage * 16 + 6).userPomodoro);
        }else{
            //hide user7
            user7.setVisibility(View.INVISIBLE);
        }

        //user8
        if (tmpUsers.size() > iPage * 16 + 7){
            updateUserName(user8text, tmpUsers.get(iPage * 16 + 7).userName);
            updateProfileImage(user8image, tmpUsers.get(iPage * 16 + 7).userImageUrl);
            updateBadge(user8image, tmpUsers.get(iPage * 16 + 7).userPomodoro);
        }else{
            //hide user8
            user8.setVisibility(View.INVISIBLE);
        }

        //user9
        if (tmpUsers.size() > iPage * 16 + 8){
            updateUserName(user9text, tmpUsers.get(iPage * 16 + 8).userName);
            updateProfileImage(user9image, tmpUsers.get(iPage * 16 + 8).userImageUrl);
            updateBadge(user9image, tmpUsers.get(iPage * 16 + 8).userPomodoro);
        }else{
            //hide user9
            user9.setVisibility(View.INVISIBLE);
        }

        //user10
        if (tmpUsers.size() > iPage * 16 + 9){
            updateUserName(user10text, tmpUsers.get(iPage * 16 + 9).userName);
            updateProfileImage(user10image, tmpUsers.get(iPage * 16 + 9).userImageUrl);
            updateBadge(user10image, tmpUsers.get(iPage * 16 + 9).userPomodoro);
        }else{
            //hide user10
            user10.setVisibility(View.INVISIBLE);
        }

        //user11
        if (tmpUsers.size() > iPage * 16 + 10){
            updateUserName(user11text, tmpUsers.get(iPage * 16 + 10).userName);
            updateProfileImage(user11image, tmpUsers.get(iPage * 16 + 10).userImageUrl);
            updateBadge(user11image, tmpUsers.get(iPage * 16 + 10).userPomodoro);
        }else{
            //hide user11
            user11.setVisibility(View.INVISIBLE);
        }

        //user12
        if (tmpUsers.size() > iPage * 16 + 11){
            updateUserName(user12text, tmpUsers.get(iPage * 16 + 11).userName);
            updateProfileImage(user12image, tmpUsers.get(iPage * 16 + 11).userImageUrl);
            updateBadge(user12image, tmpUsers.get(iPage * 16 + 11).userPomodoro);
        }else{
            //hide user12
            user12.setVisibility(View.INVISIBLE);
        }

        //user13
        if (tmpUsers.size() > iPage * 16 + 12){
            updateUserName(user13text, tmpUsers.get(iPage * 16 + 12).userName);
            updateProfileImage(user13image, tmpUsers.get(iPage * 16 + 12).userImageUrl);
            updateBadge(user13image, tmpUsers.get(iPage * 16 + 12).userPomodoro);
        }else{
            //hide user13
            user13.setVisibility(View.INVISIBLE);
        }

        //user14
        if (tmpUsers.size() > iPage * 16 + 13){
            updateUserName(user14text, tmpUsers.get(iPage * 16 + 13).userName);
            updateProfileImage(user14image, tmpUsers.get(iPage * 16 + 13).userImageUrl);
            updateBadge(user14image, tmpUsers.get(iPage * 16 + 13).userPomodoro);
        }else{
            //hide user14
            user14.setVisibility(View.INVISIBLE);
        }

        //user15
        if (tmpUsers.size() > iPage * 16 + 14){
            updateUserName(user15text, tmpUsers.get(iPage * 16 + 14).userName);
            updateProfileImage(user15image, tmpUsers.get(iPage * 16 + 14).userImageUrl);
            updateBadge(user15image, tmpUsers.get(iPage * 16 + 14).userPomodoro);
        }else{
            //hide user15
            user15.setVisibility(View.INVISIBLE);
        }

        //user16
        if (tmpUsers.size() > iPage * 16 + 15){
            updateUserName(user16text, tmpUsers.get(iPage * 16 + 15).userName);
            updateProfileImage(user16image, tmpUsers.get(iPage * 16 + 15).userImageUrl);
            updateBadge(user16image, tmpUsers.get(iPage * 16 + 15).userPomodoro);
        }else{
            //hide user16
            user16.setVisibility(View.INVISIBLE);
        }

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

    void saveUserLoggedIn(){
        //writes the user data to sharedpref

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
        //user is pressed the google sign in button, now you can show all errors to user
        bShowErrorMessage = true;
        //triggered when google sign in button is clicked. opens google account popup
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
        //the user signed in successfully, you can access all account details for now
        Log.e("umutgoogle", "handleSignInResult calisiyor");

        try {
            myUser.tokenId = null;

            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            Log.e("umutgoogle", "handleSignInResult acount alindi");

            if (account != null){
                Log.e("umutgoogle", "handleSignInResult account nulldan farkli");
                myUser.tokenId = account.getIdToken();

                Log.e("umutgoogle", "handleSignInResult google token bu geldi : " + account.getIdToken());

                myUser.userPomodoro = getActualPomodoro();
                myUser.userEmail = account.getEmail();
                myUser.userName = account.getDisplayName();

                if (account.getPhotoUrl() != null){
                    myUser.userImageUrl = account.getPhotoUrl().toString();
                }else{
                    myUser.userImageUrl = "";
                }

                if (myUser.tokenId != null){
                    Log.e("umutgoogle", "handleSignInResult token nulldan farkli, backend calisacak");
                    //save user data to sharedpref
                    saveUserLoggedIn();
                    //update user's profile image
                    updateProfileImage(myImage, myUser.userImageUrl);
                    //update user's username textfield
                    updateUserName(myUserText, myUser.userName);
                    //the user is signed in, no need to show sign in button, hide it
                    hideSignInButtons();
                    //send signed in user data to backend (to join and use room feature)
                    new Handler().postDelayed(this::postGoogleSignIn, 200);
                }else{
                    Log.e("umutgoogle", "handleSignInResult token null gelmis, backend calismayacak");
                    sendToastMessage("Unable to sign in, ERRORCODE 1");
                }

            }else{
                Log.e("umutgoogle", "handleSignInResult account null geldi");
                sendToastMessage("Unable to sign in, ERRORCODE 2");
            }

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("umutgoogle", "handleSignInResult patladi, detay : " + e.toString());
            sendToastMessage("Unable to sign in, ERRORCODE 3");
        }

        bShowErrorMessage = true;
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

    private void postGoogleSignIn(){
        JSONArray paramArray = new JSONArray();
        JSONObject paramObject = new JSONObject();

        try {
            paramObject.put("tokenId", myUser.tokenId);
            paramArray.put(paramObject);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("umutgoogle", "postGoogleSignIn patladi, detay : " + e.toString());
            sendToastMessage("Unable to login, ERRORCODE 4");
            return;
        }

        this.callbackCall = RestAdapter.createAPI().signInGoogle(paramArray.toString());
        this.callbackCall.enqueue(new Callback<CallBackSignup>() {
            public void onResponse(Call<CallBackSignup> call, Response<CallBackSignup> response) {

                int iResponseCode = 0;
                strBackendToken = null;

                try {
                    iResponseCode = response.code();
                    Log.e("umutsocket", "signin onResponse code : " + iResponseCode);
                } catch (Exception e) {
                    iResponseCode = 0;
                    Tools.processLogs(2, "kullanici kayit isleminden response code alinamadi, detay : " + e.toString());
                }

                //operation is successful, get the token from response json
                try {
                    if (iResponseCode == 201) {
                        assert response.body() != null;
                        strBackendToken = response.body().token;
                        Tools.processLogs(0, "backend google signin basarili, backend session token : " + response.body().token);
                    } else {
                        sendToastMessage("Unable to login, ERRORCODE 5");
                        onFailRequest();
                        Tools.processLogs(2, "backend google signin hatasi, response sonucu, kod : " + iResponseCode);
                        assert response.errorBody() != null;
                        Log.e("umutsocket", "signin onResponse error body : " + response.errorBody().toString());
                    }

                }catch (Exception e){
                    sendToastMessage("Unable to login, ERRORCODE 6");
                    Tools.processLogs(2, "kullanici kayit isleminden response code sonrasi hata, detay : " + e.toString());
                }

                if (strBackendToken != null){
                    bJoinNeeded = true;
                    bFocusIsNeeded = true;
                    initSocket();
                }

            }

            public void onFailure (Call < CallBackSignup > call, Throwable t){
                t.printStackTrace();
                sendToastMessage("Unable to login, ERRORCODE 7");
                Log.e("umutsignup2", "signup api onFailure " + t.getMessage());
                strBackendToken = null;
                onFailRequest();
            }

        });

    }

    private void joinRoom(){
        Log.e("umutsocket", "socketio joinRoom calisiyor");

        if (mSocket != null){
            Log.e("umutsocket", "socketio joinRoom socket nulldan farkli");

            if (mSocket.connected()){
                mSocket.emit(strSocketJoin, 1);
                bJoinNeeded = false;
                Log.e("umutsocket", "socketio joinRoom socket bagli, join komutu gonderiliyor");
            }else {
                Log.e("umutsocket", "socketio joinRoom socket bagli degil, once socket baglanip sonra join yapilacak");
            }

        }else{
            Log.e("umutsocket", "joinRoom socket null geldi ");
        }

    }

    private void focusInRoom(){

        if (mSocket != null){

            if (mSocket.connected()){

                if (!bIsPauseSituation){
                    mSocket.emit(strSocketFocus);
                    bFocusIsNeeded = false;
                    Log.e("umutsocket", "focusInRoom focus komutu socket tarafına gonderildi");
                }

            }else{
                Log.e("umutsocket", "focusInRoom socket baglanmamis, focus yapilamadi ");
            }

        }else{
            Log.e("umutsocket", "focusInRoom socket null geldi, focus yapilamadi ");
        }

    }

    private void unfocusInRoom(){

        if (mSocket != null){

            if (mSocket.connected()){

                if (!bIsPauseSituation){
                    mSocket.emit(strSocketFocusEnded);
                    Log.e("umutsocket", "unfocusInRoom focus ended komutu socket tarafına gonderildi");
                }

            }else{
                Log.e("umutsocket", "unfocusInRoom socket baglanmamis, focus ended yapilamadi ");
            }

        }else{
            Log.e("umutsocket", "unfocusInRoom socket null geldi, focus ended yapilamadi ");
        }

    }

    private void onFailRequest(){
        //TODO:burada kullaniciya kayit isleminin basarisiz oldugu mesajini vermek lazim
        Log.e("umutsignup", "onFailRequest calisti");
    }

    private void getRoomDataAndUpdateRoom(String strJSON){
        //TODO:room data json formati = [{"email":"muhammetumutaksoy@gmail.com","pp":"https:\/\/cdn.sosimple.tech\/profile_photos\/default.png","focusCount":"0"}]
        int iTmpLimit = 0, iMaxUserLimit = 100;

        List<User> tmpRoomUsers = new ArrayList<>();

        try {
            JSONArray mainJson = new JSONArray(strJSON);

            Log.e("umutsocket", "getRoomDataAndUpdateRoom jsonarray length : " + mainJson.length());

            for (int i = 0; i < mainJson.length(); i++){

                if (iTmpLimit < iMaxUserLimit){
                    iTmpLimit++;
                    String strEmail = mainJson.getJSONObject(i).getString("email");
                    String strProfileImage = mainJson.getJSONObject(i).getString("pp");
                    int iPomodoroCount = tryParseInt(mainJson.getJSONObject(i).getString("focusCount"));

                    if (!strEmail.equals(myUser.userEmail)){
                        User tmpUser = new User();
                        tmpUser.userEmail = strEmail;
                        tmpUser.userName = strEmail.substring(0, strEmail.indexOf("@"));
                        tmpUser.userImageUrl = strProfileImage;
                        tmpUser.userPomodoro = iPomodoroCount;
                        tmpRoomUsers.add(tmpUser);
                        Log.e("umutsocket", "getUserDataFromRoomJSON: " + i + ". kaydin emaili : " + strEmail);
                        Tools.processLogs(2, "room data email bu geldi : " + strEmail);
                    }

                }

            }

        } catch (JSONException e) {
            Tools.processLogs(2, "Jsondan email alirken hata olustu, hata detayi : " + e.toString());
            e.printStackTrace();
        }

        updateRoomActualData(tmpRoomUsers,0);
    }

    private Emitter.Listener onJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("umutsocket", "onjoined listener calisiyor");
                    Log.e("umutsocket", "onjoined roomid parametre olarak gelmesi lazim, ilk parametre : " + args[0]);

                    if (bFocusIsNeeded){
                        focusInRoom();
                    }

                }

            });
        }
    };

    private Emitter.Listener roomInsidersChanged = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("umutsocket", "roomInsidersChanged listener calisiyor");
/*
                    for (Object arg : args) {
                        Log.e("umutsocket", "roomInsidersChanged gelen parametreler : " + arg);
                    }
*/
                    //args[0] has user data in room, try to get that data from it
                    try {
                        getRoomDataAndUpdateRoom(args[0].toString());
                    }catch (Exception e){
                        sendToastMessage("Unable to communicate with room, ERRORCODE 8");
                        Log.e("umutsocket", "roomInsidersChanged room data parametre hatasi, detay : " + e.toString());
                    }

                }

            });

        }

    };

    private void initSocket(){
        Log.e("umutsocket", "initSocket calisiyor ");

        if (strBackendToken == null){
            Log.e("umutsocket", "initSocket backend token null geldi, socket islemleri yapilmayacak");
            return;
        }

        IO.Options opts = new IO.Options();
        opts.forceNew = true;
        opts.query = "credentials=" + strBackendToken;

        try {
            mSocket = IO.socket("https://furtle.sosimple.tech", opts);
            mSocket.on(strSocketJoined, onJoined);
            mSocket.on(strSocketRoomInsidersChanged, roomInsidersChanged);

            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e("umutsocket", "init socket: connected to server");

                    if (bJoinNeeded){
                        joinRoom();
                    }

                }
            });

            mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e("umutsocket", "init socket: disconnected from the server");
                }
            });

            mSocket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e("umutsocket", "init socket: connection error");
                    sendToastMessage("Unable to connect with room, ERRORCODE 9");
                }
            });

            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e("umutsocket", "init socket : connection timeout");
                    sendToastMessage("Room connection timeout, ERRORCODE 10");
                }
            });

            mSocket.on(Socket.EVENT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    sendToastMessage("Unexpected connection error, ERRORCODE 11");

                    for (Object arg : args)
                        Log.e("umutsocket", "init socket error, detay : " + arg);
                }
            });

            mSocket.connect();
        } catch (Exception e) {
            Log.e("umutsocket", "initSocket patladi, hata detayi : " + e.toString());
            e.printStackTrace();
        }

    }

    int tryParseInt(String value) {
        int iTmp = 0;

        try {
            iTmp = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            iTmp = 0;
        }

        return iTmp;
    }

    private void sendToastMessage(String strMessage){

        if (bShowErrorMessage){
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, strMessage, duration);
            toast.show();
        }

    }

    private void ringTheBell(){

        try {
            MediaPlayer mPlayer = MediaPlayer.create(ActivityRoom.this, R.raw.ding_bell_sound);
            mPlayer.start();
        }catch (Exception e){
            Log.e("umut", "ringTheBell hatasi, detay : " + e.toString());
        }

    }

}