package the_anonymous_koder_2.networkanalyzer.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import the_anonymous_koder_2.networkanalyzer.Model.User;
import the_anonymous_koder_2.networkanalyzer.Preferences;
import the_anonymous_koder_2.networkanalyzer.R;

public class Splash extends AppCompatActivity{
    GoogleSignInClient signInClient;
    private final String TAG = "Splash";
    boolean authEnabled = false;
    FirebaseAuth auth;
    Context context;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = this;
        auth = FirebaseAuth.getInstance();
        if(authEnabled) {
            if (Preferences.getLoggedIn(context)) {
                Log.d(TAG, "onCreate: getLoggedIn true");
                checkPreviousLogin();
            } else {
                Log.d(TAG, "onCreate: getLoggedIn false");
                startAuthenticate();
            }
        }
        else {
            startMain();
        }
    }

    @SuppressLint("RestrictedApi")
    private void checkPreviousLogin() {
        Log.d(TAG, "checkPreviousLogin: Validating Previous Login");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        signInClient = GoogleSignIn.getClient(this,gso);
        tryGoogleSignIn();
    }

    private void tryEmailSignIn() {
        User user = Preferences.getUser(context);
        signIn(user.getEmail(),user.getPassword());
    }

    private void tryGoogleSignIn() {
        @SuppressLint("RestrictedApi")
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account == null){
            Log.d(TAG, "tryGoogleSignIn: No Google Login Found");
            tryEmailSignIn();
        }else {
            Log.d(TAG, "tryGoogleSignIn: Google Login Found : "+account.getEmail());
            Log.d(TAG, "tryGoogleSignIn: Trying Firebase Login");
            firebaseAuthWithGoogle(account);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            Log.d(TAG, "Firebase Login : Successful, Signed in with: "+user.getEmail());
                            startMain();
                        }else {
                            Log.d(TAG, "Firebase Login : Failed");
                            startAuthenticate();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                startAuthenticate();
            }
        });
    }

    private void signIn(String email, String password){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = auth.getCurrentUser();
                            Log.d(TAG, "Signed In User Email : "+user.getEmail());
                            startMain();
                        }else {
                            Log.d(TAG, "SignIn with Email : Failed");
                            Toast.makeText(context, "Authentication Failed", Toast.LENGTH_SHORT).show();
                            startAuthenticate();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                startAuthenticate();
            }
        });
    }

    private void startAuthenticate(){
        startActivity(new Intent(context,Authenticate.class));
        finish();
    }

    private void startMain(){
        startActivity(new Intent(context,UserHome.class));
        finish();
    }
}