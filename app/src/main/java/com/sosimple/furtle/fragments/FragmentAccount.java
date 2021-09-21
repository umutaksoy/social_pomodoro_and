package com.sosimple.furtle.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.sosimple.furtle.R;
import com.sosimple.furtle.callbacks.CallBackSignup;
import com.sosimple.furtle.rests.RestAdapter;
import com.sosimple.furtle.utils.EqualSpacingItemDecoration;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentAccount extends Fragment {

    View root_view;
    private RecyclerView recyclerView;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1;
    private String signUpEmail, signUpUsername, signUpPassword, signUpCaptcha;
    private Call<CallBackSignup> callbackCall = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_login, container, false);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        SignInButton signInButton = root_view.findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(view -> googleSignIn());

        TextView titleText = (TextView) root_view.findViewById(R.id.login_title);
        RelativeLayout faceButton = (RelativeLayout) root_view.findViewById(R.id.facebookSignInButton);
        RelativeLayout googleButton = (RelativeLayout) root_view.findViewById(R.id.googleSignInButton);

        titleText.setText("COMING SOON");
        signInButton.setVisibility(View.GONE);
        faceButton.setVisibility(View.GONE);
        googleButton.setVisibility(View.GONE);

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

//TODO: THIS WILL BE USED FOR CONTENT FETCH
// requestAction(1);
        return root_view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        if (callbackCall != null) {

            if (callbackCall.isCanceled()){
                this.callbackCall.cancel();
            }

        }

        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void googleSignIn(){
        Log.e("umut", "googleSignIn button clicked : ");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Log.e("umut", "handleSignInResult: signed in successfully, display name : " + account.getDisplayName() + " email : " + account.getEmail());
            signUpEmail = account.getEmail().replace(" ", "");
            signUpUsername = account.getDisplayName().replace(" ", "");
            signUpPassword = "1234567890";
            signUpCaptcha = "1234567890";
            new Handler().postDelayed(this::requestPostData, 200);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("umut", "signInResult:failed code=" + e.getStatusCode());
//            updateUI(null);
        }
    }

    private void requestPostData() {
//        this.callbackCall = RestAdapter.createAPI().signUp(signUpEmail, signUpUsername, signUpPassword, signUpCaptcha);
        this.callbackCall = RestAdapter.createAPI().signUp("");
        this.callbackCall.enqueue(new Callback<CallBackSignup>() {
            public void onResponse(Call<CallBackSignup> call, Response<CallBackSignup> response) {

                Log.e("umutsignup", "response result : " + response.toString());
                Log.e("umutsignup", "response header : " + response.headers().toString());
                Log.e("umutsignup", "response message : " + response.message());

                try {
                    Log.e("umutsignup", "response body : " + response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                CallBackSignup responseHome = response.body();

                if (responseHome == null) {
                    Log.e("umutsignup", "onResponse: response is null");
                    onFailRequest();
                    return;

                }
                onSuccessRequest();
            }

            public void onFailure(Call<CallBackSignup> call, Throwable th) {
                Log.e("umutsignup","signup api onFailure " +  th.getMessage());
                if (!call.isCanceled()) {
                    onFailRequest();
                }
            }
        });
    }

    private void onFailRequest(){
        Log.e("umutsignup", "onFailRequest method is running");
    }

    private void onSuccessRequest(){
        Log.e("umutsignup", "onSuccessRequest method is running");
    }

}
