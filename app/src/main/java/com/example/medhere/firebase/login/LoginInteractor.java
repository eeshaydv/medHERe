package com.example.medhere.firebase.login;

import android.app.Activity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class LoginInteractor implements LoginContract.Intractor {

    private LoginContract.onLoginListener mOnLoginListener;
    private DatabaseReference userRef;

    LoginInteractor(LoginContract.onLoginListener onLoginListener) {
        this.mOnLoginListener = onLoginListener;
    }

    @Override
    public void performFirebaseLogin(Activity activity, String email, String password) {
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mOnLoginListener.onSuccess("");
                    } else {
                        mOnLoginListener.onFailure(Objects.requireNonNull(task.getException()).toString());
                    }
                });
    }

    @Override
    public void validateCredentials(Activity activity, String email, String password) {
        if (email.equals("")) {
            mOnLoginListener.onFailure(email);
            return;
        }

        if (password.equals("")) {
            mOnLoginListener.onFailure("Password is empty");
            return;
        }

        if (password.length() < 6) {
            mOnLoginListener.onFailure("Short password");
            return;
        }

        performFirebaseLogin(activity, email, password);

    }
}
