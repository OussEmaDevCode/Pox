package com.pewds.oussa.meetme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends AppCompatActivity implements OnCompleteListener<AuthResult> {
    FirebaseAuth mAuth;
    TextInputEditText email, password, useredit = null;
    Boolean sign = true;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);
        password = findViewById(R.id.login_password);
        email = findViewById(R.id.login_mail);
        useredit = findViewById(R.id.login_user);
        final TextInputLayout userLayout = findViewById(R.id.login_user_wrapper);
        final Button login = findViewById(R.id.signIn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sign) {
                    if (checkSignIn(email, password)) {
                        SignIn();
                    }
                } else {
                    if (checkSignUp(email, password, useredit)) {
                        SignUp();
                    }
                }
            }
        });
        final Button create = findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sign) {
                    sign = false;
                    userLayout.setVisibility(View.VISIBLE);
                    login.setText("Sign up");
                    create.setText("Already have an account");
                } else {
                    sign = true;
                    userLayout.setVisibility(View.GONE);
                    login.setText("Sign in");
                    create.setText("Create an account");
                }
            }
        });
    }

    private Boolean checkSignIn(TextInputEditText email, TextInputEditText password) {
        boolean status = true;
        if (password.getText() == null || password.getText().toString().isEmpty()) {
            status = false;
            password.setError("Password can't be empty");
        } else if (password.getText().toString().length() < 6) {
            password.setError("Password must be longer");
            status = false;
        }
        if (email.getText() == null || email.getText().toString().isEmpty()) {
            status = false;
            email.setError("Password can't be empty");
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            status = false;
            email.setError("email format isn't correct");
        }
        return status;
    }

    private Boolean checkSignUp(TextInputEditText email, TextInputEditText password, TextInputEditText user) {
        boolean status = true;
        if (password.getText() == null || password.getText().toString().isEmpty()) {
            status = false;
            password.setError("Password can't be empty");
        } else if (password.getText().toString().length() < 6) {
            password.setError("Password must be longer");
            status = false;
        }
        if (user.getText() == null || user.getText().toString().isEmpty()) {
            status = false;
            user.setError("User can't be empty");
        } else if (user.getText().toString().length() < 4) {
            user.setError("User must be longer");
            status = false;
        }
        if (email.getText() == null || email.getText().toString().isEmpty()) {
            status = false;
            email.setError("Password can't be empty");
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            status = false;
            email.setError("email format isn't correct");
        }
        return status;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
            if (!sign) {
                addUserNameToUser(task.getResult().getUser());
                Toast.makeText(this, "Signed up", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "welcome " + mAuth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
            }
            startActivity(new Intent(MainActivity.this, Chat.class));
        } else {
            if (task.getException() != null) {
                Toast.makeText(this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Unknow error, Please try again later!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addUserNameToUser(FirebaseUser user) {
        String username = useredit.getText().toString();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

    public void SignIn() {
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(this);
    }

    public void SignUp() {
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(this);
    }
}
