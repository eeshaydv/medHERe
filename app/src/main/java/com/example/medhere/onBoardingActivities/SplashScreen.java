package com.example.medhere.onBoardingActivities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.medhere.R;
import com.example.medhere.activities.FingerPrintActivity;
import com.example.medhere.activities.MainActivity;
import com.example.medhere.authentication.LoginActivity;
import com.example.medhere.authentication.VerificationActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {
    public FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(!haveNetworkConnection())
        { int backgroundColor = ContextCompat.getColor(this, R.color.yellow);
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Check your Internet Connection, You need Stable Internet Connection to use this App", Snackbar.LENGTH_LONG);
            snackbar.setBackgroundTint(backgroundColor);
            snackbar.setTextColor(ContextCompat.getColor(this,R.color.intro_title_color));
            snackbar.show();

        }
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (user == null) {
            Splash ob = new Splash();
            ob.start();
        } else {
            IsEmailVerified();
        }
    }

    private void IsEmailVerified() {
        if (user.isEmailVerified()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                FingerPrint();
            } else {
                noFingerPrint();
            }
        } else {
            startActivity(new Intent(SplashScreen.this, VerificationActivity.class));
            finish();
        }
    }

    private class Splash extends Thread {
        public void run() {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            startActivity(new Intent(SplashScreen.this, LoginActivity.class));
            finish();

        }
    }

    public void securityPreference() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            final FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference Ref = db.getReference().child("user_settings").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("settings").child("security_settings");
            Ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.child("keep_me_signed_in").equals("false")) {
                            if (snapshot.child("fingerprint_unlock").equals("true")) {
                                startActivity(new Intent(SplashScreen.this, FingerPrintActivity.class));
                                finish();
                            } else {
                                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                                finish();
                            }
                        } else {
                            startActivity(new Intent(SplashScreen.this, MainActivity.class));
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(SplashScreen.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void noFingerPrint() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            final FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference Ref = db.getReference().child("user_settings").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("settings").child("security_settings");

            Ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    if (snapshot.exists()) {
                        if (snapshot.child("keep_me_signed_in").equals("false")) {
                            startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(SplashScreen.this, MainActivity.class));
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        }
    }




    @TargetApi(Build.VERSION_CODES.M)
    private void FingerPrint() {

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        if (!fingerprintManager.isHardwareDetected()) {

            startActivity(new Intent(SplashScreen.this, LoginActivity.class));
            finish();
        } else {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                finish();
            } else {

                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                    finish();
                } else {

                    if (!keyguardManager.isKeyguardSecure()) {
                        startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                        finish();
                    } else {
                        securityPreference();
                    }
                }
            }
        }
    }
}