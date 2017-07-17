package com.arx_era.attendance;

/**
 * Created by Tronix99 on 14-07-2017.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashScreen extends Activity {

    private SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(SplashScreen.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }else {
            Intent intent = new Intent(SplashScreen.this, LoginRegisterActivity.class);
            startActivity(intent);
            finish();
        }
    }

}