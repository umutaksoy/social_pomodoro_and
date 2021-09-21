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
import android.os.PowerManager;
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
import com.sosimple.furtle.models.UserViews;
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
    int iPlayButtonSituation = 0;
    ImageView playPauseButton;
    TextView workingRestingText;
    TextView countdownText;

    private CountDownTimer countDownTimer;

    boolean bShowErrorMessage = false;

    //user variables
    User myUser;
    //profile picture
    ImageBadgeView myImage;
    //username
    TextView myUserText;

    //room's users
//    List<User> roomUsers;
    List<UserViews> roomUserViews;

    LinearLayout roomPanel;
    LinearLayout signInPanel;
    LinearLayout loadingPanel;
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
    //wakelock
    PowerManager powerManager = null;
    PowerManager.WakeLock wakeLock = null;

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

        loadingPanel = (LinearLayout) findViewById(R.id.loadingContainer);
        roomPanel = (LinearLayout) findViewById(R.id.roomContainer);
        signInPanel = (LinearLayout) findViewById(R.id.buttonsContainer);
        showLoadingPanel();

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
        UserViews uv1 = new UserViews((LinearLayout) findViewById(R.id.user1), (ImageBadgeView) findViewById(R.id.userprofile1), (TextView) findViewById(R.id.username1));
        UserViews uv2 = new UserViews((LinearLayout) findViewById(R.id.user2), (ImageBadgeView) findViewById(R.id.userprofile2), (TextView) findViewById(R.id.username2));
        UserViews uv3 = new UserViews((LinearLayout) findViewById(R.id.user3), (ImageBadgeView) findViewById(R.id.userprofile3), (TextView) findViewById(R.id.username3));
        UserViews uv4 = new UserViews((LinearLayout) findViewById(R.id.user4), (ImageBadgeView) findViewById(R.id.userprofile4), (TextView) findViewById(R.id.username4));
        UserViews uv5 = new UserViews((LinearLayout) findViewById(R.id.user5), (ImageBadgeView) findViewById(R.id.userprofile5), (TextView) findViewById(R.id.username5));
        UserViews uv6 = new UserViews((LinearLayout) findViewById(R.id.user6), (ImageBadgeView) findViewById(R.id.userprofile6), (TextView) findViewById(R.id.username6));
        UserViews uv7 = new UserViews((LinearLayout) findViewById(R.id.user7), (ImageBadgeView) findViewById(R.id.userprofile7), (TextView) findViewById(R.id.username7));
        UserViews uv8 = new UserViews((LinearLayout) findViewById(R.id.user8), (ImageBadgeView) findViewById(R.id.userprofile8), (TextView) findViewById(R.id.username8));
        UserViews uv9 = new UserViews((LinearLayout) findViewById(R.id.user9), (ImageBadgeView) findViewById(R.id.userprofile9), (TextView) findViewById(R.id.username9));
        UserViews uv10 = new UserViews((LinearLayout) findViewById(R.id.user10), (ImageBadgeView) findViewById(R.id.userprofile10), (TextView) findViewById(R.id.username10));
        UserViews uv11 = new UserViews((LinearLayout) findViewById(R.id.user11), (ImageBadgeView) findViewById(R.id.userprofile11), (TextView) findViewById(R.id.username11));
        UserViews uv12 = new UserViews((LinearLayout) findViewById(R.id.user12), (ImageBadgeView) findViewById(R.id.userprofile12), (TextView) findViewById(R.id.username12));
        UserViews uv13 = new UserViews((LinearLayout) findViewById(R.id.user13), (ImageBadgeView) findViewById(R.id.userprofile13), (TextView) findViewById(R.id.username13));
        UserViews uv14 = new UserViews((LinearLayout) findViewById(R.id.user14), (ImageBadgeView) findViewById(R.id.userprofile14), (TextView) findViewById(R.id.username14));
        UserViews uv15 = new UserViews((LinearLayout) findViewById(R.id.user15), (ImageBadgeView) findViewById(R.id.userprofile15), (TextView) findViewById(R.id.username15));
        UserViews uv16 = new UserViews((LinearLayout) findViewById(R.id.user16), (ImageBadgeView) findViewById(R.id.userprofile16), (TextView) findViewById(R.id.username16));
        roomUserViews.add(uv1);
        roomUserViews.add(uv2);
        roomUserViews.add(uv3);
        roomUserViews.add(uv4);
        roomUserViews.add(uv5);
        roomUserViews.add(uv6);
        roomUserViews.add(uv7);
        roomUserViews.add(uv8);
        roomUserViews.add(uv9);
        roomUserViews.add(uv10);
        roomUserViews.add(uv11);
        roomUserViews.add(uv12);
        roomUserViews.add(uv13);
        roomUserViews.add(uv14);
        roomUserViews.add(uv15);
        roomUserViews.add(uv16);

        //update room badges as 0
        for (int i = 0; i < roomUserViews.size(); i++){
            updateBadge(roomUserViews.get(i).userProfileImage, 0);
        }

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
                setPlayButton(false);
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
        //add google sign in button to view
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(view -> googleSignIn());
        showLoadingPanel();

        try {
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

        }catch (Exception e){
            Tools.processLogs(0, "umut initgooglesignin crashed, detail : " + e.toString());
            showSignInButtons();
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

        try {
            if (mSocket != null){
                mSocket.disconnect();
                mSocket.off(strSocketJoin);
                mSocket.off(strSocketJoined, onJoined);
                mSocket.off(strSocketRoomInsidersChanged, roomInsidersChanged);
                Log.e("umutsocket", "onBackPressed socket off ");
            }
        }catch (Exception ignored){
            Log.e("umutsocket", "onBackPressed socket error, detail : " + ignored.toString());
        }

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

        try {
            if (mSocket != null){
                mSocket.disconnect();
                mSocket.off(strSocketJoin);
                mSocket.off(strSocketJoined, onJoined);
                mSocket.off(strSocketRoomInsidersChanged, roomInsidersChanged);
            }
        }catch (Exception ignored){ }

        super.onDestroy();
    }

    void finishWorkingResting(){
        //play the gong sound
        ringTheBell();
        setPlayButton(true);
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

        Log.e("umutcountdown", "startCountdownTime data : " + lCountDown);

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

        if (wakeLock != null){

            try {
                wakeLock.release();
            }catch (Exception ignored){

            }

        }

        if (powerManager == null){
            powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        }
        assert powerManager != null;
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "Furtle::Working");
        wakeLock.acquire(lCountDown);

    }

    void pauseCountdownTime(){
        countDownTimer.cancel();
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
            Log.e("umutsharedpref", "getActualPomodoro crashed, detail : " + e.toString());
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
            Log.e("umutsharedpref", "setActualPomodoro crashed, detail : " + e.toString());
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
            for (int i = 0; i < roomUserViews.size(); i++){
                roomUserViews.get(i).userLayout.setVisibility(View.INVISIBLE);
            }

            sendToastMessage("There is no one in the room");
            return;

        }

        Log.e("umutsocket", "updateRoomActualData list size : " + tmpUsers.size());

        //set userviews considering number of users in room
        for (int i = 0; i < 16; i++){

            int actI = (iPage * 16) + i;

            if (actI < tmpUsers.size()){
                roomUserViews.get(i).userLayout.setVisibility(View.VISIBLE);
                updateUserName(roomUserViews.get(i).userNameText, tmpUsers.get(actI).userName);
                updateProfileImage(roomUserViews.get(i).userProfileImage, tmpUsers.get(actI).userImageUrl);
                updateBadge(roomUserViews.get(i).userProfileImage, tmpUsers.get(actI).userPomodoro);
            }
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
                    Log.e("umutglideerror", "updateProfileImage error, detail : " + e.toString());
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
            Log.e("umutsharedpref", "setActualPomodoro crashed, detail : " + e.toString());
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
            Log.e("umutsignin", "onActivityResult signin process running ");

            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        //the user signed in successfully, you can access all account details for now
        Log.e("umutgoogle", "handleSignInResult running");

        try {
            myUser.tokenId = null;

            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            Log.e("umutgoogle", "handleSignInResult acount data is taken");

            if (account != null){
                Log.e("umutgoogle", "handleSignInResult account is not null");
                myUser.tokenId = account.getIdToken();

                Log.e("umutgoogle", "handleSignInResult google token : " + account.getIdToken());

                myUser.userPomodoro = getActualPomodoro();
                myUser.userEmail = account.getEmail();
                myUser.userName = account.getDisplayName();

                if (account.getPhotoUrl() != null){
                    myUser.userImageUrl = account.getPhotoUrl().toString();
                }else{
                    myUser.userImageUrl = "";
                }

                if (myUser.tokenId != null){
                    Log.e("umutgoogle", "handleSignInResult token is not null, backend codes will be run");
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
                    Log.e("umutgoogle", "handleSignInResult token is null, backend wont be run");
                    sendToastMessage("Unable to sign in, ERRORCODE 1");
                    showSignInButtons();
                }

            }else{
                Log.e("umutgoogle", "handleSignInResult account is null");
                sendToastMessage("Unable to sign in, ERRORCODE 2");
                showSignInButtons();
            }

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("umutgoogle", "handleSignInResult crashed, detail : " + e.toString());
            sendToastMessage("Unable to sign in, ERRORCODE 3");
            showSignInButtons();
        }

        bShowErrorMessage = true;
    }

    void showSignInButtons(){
        loadingPanel.setVisibility(View.INVISIBLE);
        roomPanel.setVisibility(View.VISIBLE);
        roomPanel.setAlpha(0.3f);
        signInPanel.setVisibility(View.VISIBLE);
        Log.e("umutpanel", "showSignInButtons method is run");
    }

    void hideSignInButtons(){
        loadingPanel.setVisibility(View.INVISIBLE);
        roomPanel.setVisibility(View.VISIBLE);
        roomPanel.setAlpha(1f);
        signInPanel.setVisibility(View.GONE);
        Log.e("umutpanel", "hideSignInButtons method is run");
    }

    void showLoadingPanel(){
        loadingPanel.setVisibility(View.VISIBLE);
        roomPanel.setVisibility(View.INVISIBLE);
        signInPanel.setVisibility(View.INVISIBLE);
        Log.e("umutpanel", "showLoadingPanel method is run");
    }

    private void postGoogleSignIn(){
        JSONArray paramArray = new JSONArray();
        JSONObject paramObject = new JSONObject();

        try {
            paramObject.put("tokenId", myUser.tokenId);
            paramArray.put(paramObject);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("umutgoogle", "postGoogleSignIn crashed, detail : " + e.toString());
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
                    Tools.processLogs(2, "there is no response code while user signup, detail : " + e.toString());
                }

                //operation is successful, get the token from response json
                try {
                    if (iResponseCode == 201) {
                        assert response.body() != null;
                        strBackendToken = response.body().token;
                        Tools.processLogs(0, "backend google signin success, backend session token : " + response.body().token);
                    } else {
                        sendToastMessage("Unable to login, ERRORCODE 5");
                        onFailRequest();
                        Tools.processLogs(2, "backend google signin error, response code: " + iResponseCode);
                        assert response.errorBody() != null;
                        Log.e("umutsocket", "signin onResponse error body : " + response.errorBody().toString());
                    }

                }catch (Exception e){
                    sendToastMessage("Unable to login, ERRORCODE 6");
                    Tools.processLogs(2, "some spesific error after user signup, detail : " + e.toString());
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
        Log.e("umutsocket", "socketio joinRoom running");

        try {

            if (mSocket != null){
                Log.e("umutsocket", "socketio joinRoom socket is not null");

                if (mSocket.connected()){
                    mSocket.emit(strSocketJoin, 1);
                    bJoinNeeded = false;
                    Log.e("umutsocket", "socketio joinRoom socket is connected, join command will be run");
                }else {
                    Log.e("umutsocket", "socketio joinRoom socket is not connected, first socket will be connected then join command will be run");
                }

            }else{
                Log.e("umutsocket", "joinRoom socket is null");
            }

        }catch (Exception e){
            Log.e("umutsocket", "joinRoom method crashed, detail : " + e.toString());
        }

    }

    private void focusInRoom(){

        try {

            if (mSocket != null){

                if (mSocket.connected()){

                    if (!bIsPauseSituation){
                        mSocket.emit(strSocketFocus);
                        bFocusIsNeeded = false;
                        Log.e("umutsocket", "focusInRoom focus command will be send to socket");
                    }

                }else{
                    Log.e("umutsocket", "focusInRoom socket is not connected, focus is not successful");
                }

            }else{
                Log.e("umutsocket", "focusInRoom socket is null, focus is not successful");
            }

        }catch (Exception e){
            Log.e("umutsocket", "focusInRoom method is crashed, detail : " + e.toString());
        }

    }

    private void unfocusInRoom(){

        try {

            if (mSocket != null){

                if (mSocket.connected()){

                    if (!bIsPauseSituation){
                        mSocket.emit(strSocketFocusEnded);
                        Log.e("umutsocket", "unfocusInRoom focus ended command is sent to socket");
                    }

                }else{
                    Log.e("umutsocket", "unfocusInRoom socket is not connected, focus ended is unsuccessfull");
                }

            }else{
                Log.e("umutsocket", "unfocusInRoom socket is null, focus ended is not successful");
            }

        }catch (Exception e){
            Log.e("umutsocket", "unfocusInRoom method is crashed, detail : " + e.toString());
        }

    }

    private void onFailRequest(){
        //TODO:must be shown to the user about signup process is failed
        Log.e("umutsignup", "onFailRequest is running");
    }

    private void getRoomDataAndUpdateRoom(String strJSON){
        //TODO:room data json format = [{"email":"muhammetumutaksoy@gmail.com","pp":"https:\/\/cdn.sosimple.tech\/profile_photos\/default.png","focusCount":"0"}]
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
                        Log.e("umutsocket", "getUserDataFromRoomJSON: " + i + "st row email : " + strEmail);
                        Tools.processLogs(2, "room data email : " + strEmail);
                    }

                }

            }

        } catch (JSONException e) {
            Tools.processLogs(2, "Failed to get email from Json, detail : " + e.toString());
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
                    Log.e("umutsocket", "onjoined listener is running");
                    Log.e("umutsocket", "onjoined roomid parameter : " + args[0]);

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
                    Log.e("umutsocket", "roomInsidersChanged listener is running");

                    //args[0] has user data in room, try to get that data from it
                    try {
                        getRoomDataAndUpdateRoom(args[0].toString());
                    }catch (Exception e){
                        sendToastMessage("Unable to communicate with room, ERRORCODE 8");
                        Log.e("umutsocket", "roomInsidersChanged room data parameter error, detail : " + e.toString());
                    }

                }

            });

        }

    };

    private void initSocket(){
        Log.e("umutsocket", "initSocket is running");

        if (strBackendToken == null){
            Log.e("umutsocket", "initSocket backend token is null, there will be no socket operation");
            return;
        }

        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.query = "credentials=" + strBackendToken;

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
                        Log.e("umutsocket", "init socket error, detail : " + arg);
                }
            });

            mSocket.connect();
        } catch (Exception e) {
            Log.e("umutsocket", "initSocket is crashed, detail : " + e.toString());
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

        try {

            if (bShowErrorMessage){
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, strMessage, duration);
                toast.show();
            }

        }catch (Exception e){
            Log.e("umutmessage", "sendToastMessage method is crashed, detail : " + e.toString());
        }

    }

    private void ringTheBell(){

        try {
            MediaPlayer mPlayer = MediaPlayer.create(ActivityRoom.this, R.raw.ding_bell_sound);
            mPlayer.start();
        }catch (Exception e){
            Log.e("umut", "ringTheBell method is crashed, detail : " + e.toString());
        }

    }

    private void setPlayButton(boolean bIsChronoTriggered){
        //iPlayButtonSituation values;
        //0 = the user is ready to work
        //1 = the user is working
        //2 = the user is ready to rest
        //3 = the user is resting

        //if triggered from chronometer, these are the all possibilities;
        //1 -> 2, chronometer is finished, the user finished the working
        //3 -> 0, chronometer is finished, the user finished the resting

        //if triggered from user action (button press), these are the all possibilities;
        //0 -> 1, the user started to work
        //1 -> 0, the user reset the chronometer, while working
        //2 -> 3, the user started to rest
        //3 -> 0, the user reset the chronometer, while working

        switch (iPlayButtonSituation){
            case 0:
                //the user started to work, set situation is working, show pause image, start the chronometer,
                // set text as working time, set focus in socket.io
                iPlayButtonSituation = 1;
                setPlayPauseButtonImage(false);
                setWorkingRestingText(true);
                startCountdownTime(getCountdownMiliSecond());
                focusInRoom();
                break;
            case 1:
                if (bIsChronoTriggered){
                    //the user finished the working, set situation ready to rest, show play image, set resting time, set text as resting time
                    iPlayButtonSituation = 2;
                    setPlayPauseButtonImage(true);
                    setWorkingRestingText(false);
                    setCountdownText(5 * 60 * 1000); //5 mins
                    increaseActualPomodoro();
                }else{
                    //the user reset the chronometer, set situation ready to work, show play image, set working time, set text as working time
                    iPlayButtonSituation = 0;
                    setPlayPauseButtonImage(true);
                    setWorkingRestingText(true);
                    pauseCountdownTime();
                    setCountdownText(getCountdownMiliSecond());
                }
                unfocusInRoom();
                break;
            case 2:
                //the user started to rest, set situation is resting, show pause image, start the chronometer
                iPlayButtonSituation = 3;
                setPlayPauseButtonImage(false);
                setWorkingRestingText(false);
                startCountdownTime(5 * 60 * 1000); //5 mins
                break;
            case 3:
                //the user reset or finished the rest, set situation ready to work, show play image, set working time, set text as working time
                iPlayButtonSituation = 0;
                setPlayPauseButtonImage(true);
                pauseCountdownTime();
                setWorkingRestingText(true);
                setCountdownText(getCountdownMiliSecond());
                break;
            default:
                break;
        }

    }

}