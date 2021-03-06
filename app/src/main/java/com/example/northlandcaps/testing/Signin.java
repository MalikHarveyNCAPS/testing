package com.example.northlandcaps.testing;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.onesignal.OneSignal;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class Signin extends AppCompatActivity {
    Button signout, notifcation;
    private FirebaseAuth mAuth;
    TextView username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        mAuth = FirebaseAuth.getInstance();
        signout = findViewById(R.id.signout);
        username = findViewById(R.id.tvName);
        notifcation = findViewById(R.id.notification);

        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        //Fetch the Display name of current User
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            username.setText("Welcome " + user.getDisplayName());
        }


        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                OneSignal.sendTag("User_ID", "");
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        notifcation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] SynergyEmails = {"ruinhard@gmail.com", "harveym4662@gmail.com","user1@gmail.com"}; //an array containing all emails of personnel
                for (int y = 0; y < SynergyEmails.length; y++) { //repeats the sendNotif until every signed in user has gotten an notif
                    if (SynergyEmails[y].equals(MainActivity.LoggedIn_User_Email)) { //if the phone that will recieve the notif has the same email as yours (aka your own phone), skip
                        continue;
                    }
                    sendNotification(SynergyEmails[y]);
                }
            }
        });

    }

    private void sendNotification(final String SynergyEmail) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                    int SDK_INT = android.os.Build.VERSION.SDK_INT;
                    if (SDK_INT > 8) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                .permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        try {
                            String jsonResponse;

                            URL url = new URL("https://onesignal.com/api/v1/notifications");
                            HttpURLConnection con = (HttpURLConnection) url.openConnection();
                            con.setUseCaches(false);
                            con.setDoOutput(true);
                            con.setDoInput(true);

                            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                            con.setRequestProperty("Authorization", "Basic Mzk2YWEzOGUtMWEwNC00ZmZkLWFiMTQtN2JkYjNlNWZkYTk0"); //Rest API Key
                            con.setRequestMethod("POST");

                            String strJsonBody = "{"
                                    + "\"app_id\": \"43140cf2-d7a7-4430-8404-e0c6e644a11e\"," //One Signal app id

                                    + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + SynergyEmail + "\"}],"

                                    + "\"data\": {\"foo\": \"bar\"},"
                                    + "\"contents\": {\"en\": \"EMERGENCY\"}"
                                    + "}";


                            System.out.println("strJsonBody:\n" + strJsonBody);

                            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                            con.setFixedLengthStreamingMode(sendBytes.length);

                            OutputStream outputStream = con.getOutputStream();
                            outputStream.write(sendBytes);

                            int httpResponse = con.getResponseCode();
                            System.out.println("httpResponse: " + httpResponse);

                            if (httpResponse >= HttpURLConnection.HTTP_OK
                                    && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                                Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                                scanner.close();
                            } else {
                                Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                                scanner.close();
                            }
                            System.out.println("jsonResponse:\n" + jsonResponse);

                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                }
        });
    }
}
