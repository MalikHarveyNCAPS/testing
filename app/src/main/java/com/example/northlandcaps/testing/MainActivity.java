package com.example.northlandcaps.testing;

import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.onesignal.OneSignal;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText email,password,name;
    private Button signin,signup;
    FirebaseUser user;
    static String LoggedIn_User_Email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        signin = findViewById(R.id.loginbtn);
        signup = findViewById(R.id.signup);
        email = findViewById(R.id.usernametxt);
        password = findViewById(R.id.passwordtxt);
        name = findViewById(R.id.name);
        //check if User is Already LoggedIn
        if(mAuth.getCurrentUser()!=null){
            //User NOT logged in
            user = mAuth.getCurrentUser();
            LoggedIn_User_Email = user.getEmail();
            OneSignal.sendTag("User_ID", LoggedIn_User_Email);
            finish();
            startActivity(new Intent(getApplicationContext(),Signin.class));

        }



        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getemail = email.getText().toString().trim();
                String getpassword = password.getText().toString().trim();
                callsignin(getemail,getpassword);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getemail = email.getText().toString().trim();
                String getpassword = password.getText().toString().trim();
                callsignup(getemail,getpassword);
            }
        });


    }
    //Create user

    private void callsignup(String email,String password){
    mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            Log.d("TESTING", "Sign up successful");
            if (!task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(MainActivity.this,"Sign up failed",Toast.LENGTH_SHORT).show();
                } else {
                    userProfile();
                    Toast.makeText(MainActivity.this,"Created Account",Toast.LENGTH_SHORT).show();
                    // If sign in fails, display a message to the user.
                    Log.d("TESTING", "Created Account", task.getException());
                }

            // ...
            }
        });
    }

    private void userProfile()
    {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!= null)
        {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name.getText().toString().trim())
                    //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))  // here you can set image link also.
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("TESTING", "User profile updated.");
                            }
                        }
                    });
        }
    }
    private void callsignin(String email,String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TESTING", "sign In Successful:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("TESTING", "signInWithEmail:failed", task.getException());
                            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            user = mAuth.getCurrentUser();
                            LoggedIn_User_Email = user.getEmail();
                            OneSignal.sendTag("User_ID", LoggedIn_User_Email);
                            Intent i = new Intent(MainActivity.this, Signin.class);
                            finish();
                            startActivity(i);
                        }
                    }
                });

    }
}
