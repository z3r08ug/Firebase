package com.example.chris.firebase;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GithubAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = MainActivity.class.getSimpleName() + "_TAG";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    EditText etEmail;
    EditText etPassword;
    String email, password;
    private GoogleSignInClient googleSignInClient, mGoogleSignInClient;
    private CallbackManager callbackManager;
    LoginButton loginButton;
    private TwitterLoginButton mLoginButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    
        initFBTwitter();
        
        setContentView(R.layout.activity_main);
        
        bindViews();
        
        bindGoogle();
    
        bindFacebookButton();
    
        bindTwitterButton();
        
        
    }
    
    private void bindViews()
    {
        mAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
    }
    
    private void bindGoogle()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("877956356512-6u6kgeenk7fhp5s10v47k6f0t9n5a46p.apps.googleusercontent.com")
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    
    private void initFBTwitter()
    {
        FacebookSdk.sdkInitialize(getApplicationContext());
        
        TwitterAuthConfig authConfig =  new TwitterAuthConfig(
                getString(R.string.twitter_app_id),
                getString(R.string.twitter_secret));
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(authConfig)
                .debug(true)
                .build();
        Twitter.initialize(config);
    }
    
    private void bindFacebookButton()
    {
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }
        
            @Override
            public void onCancel()
            {
                updateUI(null, "Signin");
            }
        
            @Override
            public void onError(FacebookException e)
            {
                updateUI(null, "Signin");
                Log.d(TAG, "onError: " + e.toString());
            }
        });
    }
    
    private void bindTwitterButton()
    {
        mLoginButton = findViewById(R.id.twitter_login);
        mLoginButton.setCallback(new Callback<TwitterSession>()
        {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d(TAG, "twitterLogin:success" + result);
                handleTwitterSession(result.data);
            }
        
            @Override
            public void failure(TwitterException exception) {
                Log.w(TAG, "twitterLogin:failure", exception);
                updateUI(null, "Signin");
            }
        });
    }
    
    private void handleTwitterSession(TwitterSession session)
    {
        Log.d(TAG, "handleTwitterSession:" + session);
        
        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);
        
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, "onComplete: "+user);
                            updateUI(user, "Signin");
                        }
                        else
                        {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null, "Signin");
                        }
                    }
                });
    }
    
    private void handleFacebookAccessToken(AccessToken token)
    {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user, "Signin");
                        }
                        else
                        {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null, "Signin");
                        }
                    }
                });
    }
    
    public void onSignIn(View view)
    {
        getUserCredentials();
        
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user, "Signin");
                        }
                        else
                        {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null, "Signin");
                        }
                        
                        // ...
                    }
                });
    }
    
    public void getUserCredentials()
    {
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
    }
    
    public void onSignUp(View view)
    {
        getUserCredentials();
        
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user, "Signup");
                        }
                        else
                        {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null, "Signup");
                        }
                    }
                });
    }
    
    public void updateUI(FirebaseUser user, String type)
    {
        String message;
        if (type.equals("Signup"))
        {
            if (user != null)
            {
                message = "New Account for " + user.getEmail() + " created";
            }
            else
            {
                message = "Account creation failed.";
            }
        }
        else
        {
            if (user != null)
            {
                message = user.getEmail() + " signed in.";
            }
            else
            {
                message = "Account sign in failed.";
            }
        }
    
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        
        if (type.equals("Signin") && user != null)
        {
            startActivity(new Intent(this, MovieActivity.class));
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        mLoginButton.onActivityResult(requestCode, resultCode, data);
        
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 9001)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try
            {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            }
            catch (ApiException e)
            {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null, "Signin");
                // [END_EXCLUDE]
            }
        }
    }
    
    
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct)
    {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
//        showProgressDialog();
        // [END_EXCLUDE]
        
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user, "Signin");
                        }
                        else
                        {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null, "Signin");
                        }
//                        hideProgressDialog();
                    }
                });
    }
    
    public void onSignInGoogle(View view)
    {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    public void printHashKey(Context pContext) {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }
    public void onSignInGithub(View view)
    {
        String token = "967255f516cbdf4d9c5b07e88e6e3b20f753a38a";
        AuthCredential credential = GithubAuthProvider.getCredential(token);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                    
                        if (!task.isSuccessful())
                        {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user, "Signin");
                        }
                    }
                });
    }
    
    public void onSignOut(View view)
    {
        mAuth.signOut();
    }
}
