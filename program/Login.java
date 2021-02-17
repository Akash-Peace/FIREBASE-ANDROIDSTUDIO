package com.example.whitecup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity {
    EditText lEmail, lPass;
    Button lLogin, lNewuser;
    ProgressBar lProgressbar;
    TextView lForgotpass, lmsg;
    FirebaseAuth lAuth;
    SignInButton gsignin;
    GoogleSignInOptions gso;
    GoogleSignInClient signInClient;
    public static final int greqcode = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        lEmail = findViewById(R.id.emaillog);
        lPass = findViewById(R.id.passlog);
        lLogin = findViewById(R.id.login);
        lNewuser = findViewById(R.id.newuser);
        lProgressbar = findViewById(R.id.progressBar2);
        lForgotpass = findViewById(R.id.forgotpass);
        lmsg = findViewById(R.id.msg);
        lAuth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("893367057376-b9921114kdo8ua47kdoh13ppc389as4m.apps.googleusercontent.com").requestEmail().build();
        signInClient = GoogleSignIn.getClient(this, gso);


        //Auto-Login through E-mail
        if (lAuth.getCurrentUser() != null){
            FirebaseUser user = lAuth.getCurrentUser();
            if (user.isEmailVerified()){
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }else {
                lmsg.setText("Please Verify your Account.");
                lmsg.setVisibility(View.INVISIBLE);
                lmsg.setVisibility(View.VISIBLE);
            }
        }

        //G-Signin Starter
        gsignin = findViewById(R.id.gsingin);
        gsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sign = signInClient.getSignInIntent();
                startActivityForResult(sign, greqcode);
            }
        });

        //ForgotPassword
        lForgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetpass = new EditText(v.getContext());
                AlertDialog.Builder passreset = new AlertDialog.Builder(v.getContext());
                passreset.setTitle("Forgot Password?");
                passreset.setMessage("Enter your E-mail to receive reset password link.");
                passreset.setView(resetpass);
                passreset.setPositiveButton("Send",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = resetpass.getText().toString().trim();
                        if (TextUtils.isEmpty(mail)){
                            lmsg.setText("E-mail is required to reset password.");
                            lmsg.setVisibility(View.INVISIBLE);
                            lmsg.setVisibility(View.VISIBLE);
                        } else {
                            lAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    lmsg.setText("Reset link sent to your E-mail.");
                                    lmsg.setVisibility(View.INVISIBLE);
                                    lmsg.setVisibility(View.VISIBLE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    lmsg.setText("Error! Reset link not sent.");
                                    lmsg.setVisibility(View.INVISIBLE);
                                    lmsg.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }
                });
                passreset.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                passreset.show();
            }
        });

        //Registration
        lNewuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = lEmail.getText().toString().trim();
                String pass = lPass.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    lEmail.setError("E-mail is required.");
                    return;
                }
                if (TextUtils.isEmpty(pass)){
                    lPass.setError("Password is required.");
                    return;
                }

                if (pass.length() < 8){
                    lPass.setError("Password contains at least 8 characters");
                    return;
                }

                lProgressbar.setVisibility(View.VISIBLE);


                lAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = lAuth.getCurrentUser();
                            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    lmsg.setText("Confirm the verification E-mail has been sent.");
                                    lmsg.setVisibility(View.INVISIBLE);
                                    lmsg.setVisibility(View.VISIBLE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    lmsg.setText("Error! E-mail not sent.");
                                    lmsg.setVisibility(View.INVISIBLE);
                                    lmsg.setVisibility(View.VISIBLE);
                                }
                            });
                            lProgressbar.setVisibility(View.INVISIBLE);
                            lNewuser.setEnabled(false);
                        }else{
                            FirebaseUser user = lAuth.getCurrentUser();
                            if (user.isEmailVerified()){
                                lmsg.setText("This Account is already registered.");
                                lmsg.setVisibility(View.INVISIBLE);
                                lmsg.setVisibility(View.VISIBLE);
                                lProgressbar.setVisibility(View.INVISIBLE);
                            }else {
                                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        lmsg.setText("Confirm the verification E-mail has been sent.");
                                        lmsg.setVisibility(View.INVISIBLE);
                                        lmsg.setVisibility(View.VISIBLE);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        lmsg.setText("Error! E-mail not sent.");
                                        lmsg.setVisibility(View.INVISIBLE);
                                        lmsg.setVisibility(View.VISIBLE);
                                    }
                                });
                                lProgressbar.setVisibility(View.INVISIBLE);
                                lNewuser.setEnabled(false);
                            }
                        }
                    }
                });
            }
        });

        //Login
        lLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = lEmail.getText().toString().trim();
                String pass = lPass.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    lEmail.setError("E-mail is required.");
                    return;
                }
                if (TextUtils.isEmpty(pass)){
                    lPass.setError("Password is required.");
                    return;
                }

                if (pass.length() < 8){
                    lPass.setError("Password contains at least 8 characters");
                    return;
                }

                lProgressbar.setVisibility(View.VISIBLE);

                lAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = lAuth.getCurrentUser();
                            if (user.isEmailVerified()){
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }else {
                                lmsg.setText("This account isn't verified! Register again or Verify the mail has been sent.");
                                lmsg.setVisibility(View.INVISIBLE);
                                lmsg.setVisibility(View.VISIBLE);
                                lProgressbar.setVisibility(View.INVISIBLE);
                            }
                        }else{
                            lmsg.setText("Invalid Mail-id or Password!");
                            lmsg.setVisibility(View.INVISIBLE);
                            lmsg.setVisibility(View.VISIBLE);
                            lProgressbar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
    }

    //G-Signin process
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == greqcode){
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount signInAccount = signInAccountTask.getResult(ApiException.class);
                AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                lAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        lmsg.setText("Google Signin failed!");
                        lmsg.setVisibility(View.INVISIBLE);
                        lmsg.setVisibility(View.VISIBLE);
                    }
                });
            }catch (ApiException e){
                e.printStackTrace();
            }
        }
    }

    //back button to exit
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}