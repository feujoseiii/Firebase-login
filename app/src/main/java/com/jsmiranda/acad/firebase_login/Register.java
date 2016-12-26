package com.jsmiranda.acad.firebase_login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Register extends AppCompatActivity {

    private EditText alreadyHaveAccount; //login call to action
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDB;

    private Button regiterBtn;

    private EditText usernameField;
    private EditText passwordField;
    private EditText repasswordField;

    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        regiterBtn = (Button) findViewById(R.id.button2);
        alreadyHaveAccount = (EditText) findViewById(R.id.editText2);
        usernameField = (EditText) findViewById(R.id.editText4);
        passwordField = (EditText) findViewById(R.id.editText);
        repasswordField = (EditText) findViewById(R.id.editText3);
        dialog = new ProgressDialog(Register.this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDB = FirebaseDatabase.getInstance();
    }

    public void register_user(View v){

        final int MIN_USERNAME_LENGTH = 5;
        final int MIN_PASSWORD_LENGTH = 6;

        final String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String repassword = repasswordField.getText().toString().trim();

        //validations

        if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
            displayAlert("Unable to register","Incorrect email format");
            return;
        }

        if(username.isEmpty()){
            displayAlert("Unable to register","Email field cannot be empty");
            return;
        }

        if(username.length() < MIN_USERNAME_LENGTH){
            displayAlert("Unable to reigster","Username must be minimum of " + MIN_USERNAME_LENGTH + " characters");
            return;
        }

        if(password.isEmpty() || repassword.isEmpty()){
            displayAlert("Unable to register","Password field cannot be empty");
            return;
        }
        if(password.length() < MIN_PASSWORD_LENGTH ||
            repassword.length() < MIN_PASSWORD_LENGTH){
            displayAlert("Unable to register","Password must be minimum of " + MIN_PASSWORD_LENGTH + " characters");
            return;
        }

        if(!password.equals(repassword)){
            displayAlert("Unable to register","Password does not match");
            return;
        }

        dialog.setMessage("Registering..");
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        firebaseAuth.createUserWithEmailAndPassword(username,password)
                .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.dismiss();
                        if(!task.isSuccessful()){
                            displayAlert("Unable to register",task.getException().getMessage());
                        }else{
                            DatabaseReference firebaseDBRef = firebaseDB.getReference("users");
                            String uid = firebaseAuth.getCurrentUser().getUid();
                            User user = new User(username,"user");
                            firebaseDBRef.child(uid).setValue(user);
                            startActivity(new Intent(Register.this, User_Dashboard.class));
                            finish();
                        }
                    }
                });
    }

    private void displayAlert(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
        builder.setTitle(title).setMessage(message).setPositiveButton(android.R.string.ok,null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void login_activity(View v){
        startActivity(new Intent(Register.this, MainActivity.class));
        finish();
    }


}
