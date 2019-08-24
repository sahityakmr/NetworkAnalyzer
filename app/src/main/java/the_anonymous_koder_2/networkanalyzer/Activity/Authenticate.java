package the_anonymous_koder_2.networkanalyzer.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import the_anonymous_koder_2.networkanalyzer.Model.User;
import the_anonymous_koder_2.networkanalyzer.Preferences;
import the_anonymous_koder_2.networkanalyzer.R;

public class Authenticate extends AppCompatActivity implements View.OnClickListener{

    GoogleSignInClient signInClient;
    FirebaseAuth auth;
    private final int SIGNIN_REQ_CODE = 101;
    String TAG = "Auth Activity";
    private String sPass = "";
    private String sEmail = "";
    Context context;
    EditText email,pass;
    FirebaseUser user;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);
        context = this;
        initViews();
        auth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        signInClient = GoogleSignIn.getClient(this,gso);
    }

    private void initViews() {
        email = findViewById(R.id.auth_email);
        pass = findViewById(R.id.auth_pass);
        findViewById(R.id.google_login).setOnClickListener(this);
        findViewById(R.id.auth_btn).setOnClickListener(this);
    }

    private void signUp(String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "Signup : Success");
                            FirebaseUser user = auth.getCurrentUser();
                        }else {
                            Log.d(TAG, "Signup : Failed");
                            Toast.makeText(Authenticate.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn(String email, final String password){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "SignIn with Email : Success");
                            FirebaseUser user = auth.getCurrentUser();
                            Log.d(TAG, "on Email Sign In : Signed In User Email : "+user.getEmail());
                            Preferences.saveLoggedIn(context,true);
                            Preferences.saveUser(context,new User(user.getDisplayName(),user.getEmail(),user.getPhotoUrl().toString(),"",password));
                            onLoginSuccess();
                        }else {
                            Log.d(TAG, "SignIn with Email : Failed");
                            Toast.makeText(Authenticate.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.google_login:
                signIn();
                break;
            case R.id.auth_btn:
                if (isValid())
                    signIn(sEmail,sPass);
                    break;
        }
    }

    private void signIn() {
        @SuppressLint("RestrictedApi")
        Intent signInIntent = signInClient.getSignInIntent();
        startActivityForResult(signInIntent,SIGNIN_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case SIGNIN_REQ_CODE:
                @SuppressLint("RestrictedApi")
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
                break;
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            e.printStackTrace();
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
                            Log.d(TAG, "Google Login : Successful");
                            Log.d(TAG, "onComplete Google Sign IN : Signed In User Email : "+user.getEmail());
                            Preferences.saveLoggedIn(context,true);
                            Preferences.saveUser(context,new User(user.getDisplayName(),user.getEmail(),user.getPhotoUrl().toString(),"",""));
                            onLoginSuccess();
                        }else {
                            Log.d(TAG, "Google Login : Failed");
                        }
                    }
                });
    }

    public boolean isValid() {
        if((sEmail = email.getText().toString()).equals("")){
            email.setError("Enter Valid Email ID");
            email.requestFocus();
            return false;
        }
        if((sPass = pass.getText().toString()).equals("")){
            pass.setError("Enter Valid Password");
            pass.requestFocus();
            return false;
        }
        return true;
    }

    private void onLoginSuccess(){
        startActivity(new Intent(context,UserHome.class));
        finish();
    }
}
