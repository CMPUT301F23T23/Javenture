package com.example.javenture;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.rpc.context.AttributeContext;

import java.util.HashMap;

/**
 * Handles authentication with Firebase
 */
public class AuthenticationService {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public AuthenticationService() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public AuthenticationService(FirebaseAuth auth, FirebaseFirestore db) {
        mAuth = auth;
        this.db = db;
    }

    /**
     * Signs in anonymously
     * @param listener callback for when sign in is successful or failed
     */
    public void signInAnonymously(OnSignInListener listener) {
        if (mAuth.getCurrentUser() != null) {
            listener.onSignIn();
            return;
        }
        mAuth.signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("auth", "signInAnonymously:success");
                            // create user in db
                            FirebaseUser user = mAuth.getCurrentUser();
                            db.collection("users")
                                    .document(user.getUid())
                                    .set(new HashMap<>())
                                    .addOnSuccessListener(unused -> {
                                        Log.d("auth", "user created in db");
                                        listener.onSignIn();
                                    }).addOnFailureListener(e -> {
                                        Log.d("auth", "user creation failed");
                                        listener.onSignInFailed();
                                    });

                        } else {
                            Log.w("auth", "signInAnonymously:failure", task.getException());
                            listener.onSignInFailed();
                        }
                    }
                });
    }

    /**
     * Gets the currently signed in user
     * @return the currently signed in user or null if no user is signed in
     */
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    /**
     * Interface for callbacks when sign in is successful or failed
     */
    public interface OnSignInListener {
        void onSignIn();
        void onSignInFailed();
    }
}
