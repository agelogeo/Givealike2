package com.agelogeo.givealike2;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 1;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    CheckBox agree_checkBox;
    SignInButton signInButton;
    Button terms_button , privacy_button;
    boolean isFragmentOn = false;
    FrameLayout fragmentFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        agree_checkBox = findViewById(R.id.agree_checkBox);
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setEnabled(false);
        terms_button = findViewById(R.id.terms_button);
        privacy_button = findViewById(R.id.privacy_button);
        fragmentFrameLayout = findViewById(R.id.fragmentFrameLayout);

        setTitle("Welcome!");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            // Move to Drawer Activity
            Snackbar.make(findViewById(R.id.loginMainLayout),"Welcome back "+currentUser.getDisplayName()+"!", Snackbar.LENGTH_SHORT).show();
            logInMainFragment(currentUser);
        }else{
            // Must log in
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestProfile()
                    .requestIdToken("830291595603-184l25eg849o2onm1vhajm789814mj16.apps.googleusercontent.com")
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            findViewById(R.id.sign_in_button).setOnClickListener(this);
            Snackbar.make(findViewById(R.id.loginMainLayout),"Hello user, you must log in first!", Snackbar.LENGTH_SHORT).show();
        }

        agree_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    signInButton.setEnabled(true);
                }else{
                    signInButton.setEnabled(false);
                }
            }
        });

        terms_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new TermsFragment(),false);
            }
        });

        privacy_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new PrivacyFragment(),false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            // ...
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("Google", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Google", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Google", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(),"Success!",Toast.LENGTH_SHORT).show();
                            logInMainFragment(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Google", "signInWithCredential:failure", task.getException());
                            //updateUI(null);
                        }

                    }
                });
    }



    public void logInMainFragment(FirebaseUser account){
        Intent intent = new Intent(this,MainActivity.class);
        Log.i("account",account.toString());
        intent.putExtra("name",account.getDisplayName());
        intent.putExtra("email",account.getEmail());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if(account.getPhotoUrl() != null)
            intent.putExtra("profile_pic",account.getPhotoUrl().toString());
        else
            intent.putExtra("profile_pic","");
        startActivity(intent);
    }

    public void setFragment(Fragment fragment,boolean withAnimation) {
        isFragmentOn = true;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(withAnimation)
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        transaction.replace(R.id.fragmentFrameLayout,fragment);
        transaction.commit();
        fragmentFrameLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if(isFragmentOn){
            fragmentFrameLayout.setVisibility(View.INVISIBLE);
            isFragmentOn = false;
        }else{
            super.onBackPressed();
        }
    }
}
