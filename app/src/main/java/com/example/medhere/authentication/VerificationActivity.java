package com.example.medhere.authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.medhere.R;
import com.example.medhere.base.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;


public class VerificationActivity extends BaseActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    String user;

    EditText VEmail;
    Button Vbutton;
    Button ReSend;
    Button SignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        VEmail = findViewById(R.id.verification_email);
        Vbutton = findViewById(R.id.button_verify);
        ReSend = findViewById(R.id.button_resend);
        SignOut = findViewById(R.id.button_signout);


        mAuth = FirebaseAuth.getInstance();
        Vbutton.setOnClickListener(this);
        ReSend.setOnClickListener(this);
        SignOut.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            user = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            user = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        }
        VEmail.setText(user);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_signout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(VerificationActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.button_resend:
                sendVerification();
                break;
            case R.id.button_verify:
                reloadUser();
                IsEmailVerified();
                break;
        }
    }

    private void sendVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(VerificationActivity.this, (OnCompleteListener) task -> {
                        if (task.isSuccessful()) {
                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Sent Verification Email", Snackbar.LENGTH_SHORT);
                            snackbar.setBackgroundTint(ContextCompat.getColor(VerificationActivity.this, R.color.green));
                            snackbar.setTextColor(ContextCompat.getColor(VerificationActivity.this,R.color.intro_title_color));
                            snackbar.show();
                        } else {
                            Log.e("", "sendEmailVerification", task.getException());
                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),"Failed to send Verification Email, Try again", Snackbar.LENGTH_SHORT);
                            snackbar.setBackgroundTint(ContextCompat.getColor(VerificationActivity.this, R.color.red));
                            snackbar.setTextColor(ContextCompat.getColor(VerificationActivity.this,R.color.white));
                            snackbar.show();

                        }
                    });
        }
    }

    private void IsEmailVerified() {
        FirebaseUser C_user = mAuth.getCurrentUser();
        assert C_user != null;
        if (C_user.isEmailVerified()) {
            startActivity(new Intent(VerificationActivity.this, LoginActivity.class));
            finish();
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),"Email Not Verified", Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(ContextCompat.getColor(VerificationActivity.this, R.color.red));
            snackbar.setTextColor(ContextCompat.getColor(VerificationActivity.this,R.color.white));
            snackbar.show();}
    }

    private void reloadUser() {
        FirebaseUser C_user = mAuth.getCurrentUser();
        if (C_user != null) {
            mAuth.getCurrentUser().reload();
            C_user.reload();
        }
    }
}