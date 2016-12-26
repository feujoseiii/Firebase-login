package com.jsmiranda.acad.firebase_login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;

public class MainActivity extends AppCompatActivity {

    TextView notRegistered; //not registered call to action

    private EditText usernameField;
    private EditText passwordField;

    //firebase stuff
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    //roles
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notRegistered = (EditText) findViewById(R.id.editText);

        //user stuff
        usernameField = (EditText) findViewById(R.id.editText2);
        passwordField = (EditText) findViewById(R.id.editText3);

        //firebase stuff
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    public void login_user(View v){
        String user_username = usernameField.getText().toString().trim();
        String user_password = passwordField.getText().toString().trim();

        if(user_username.isEmpty()){
            displayDialog("Unable to login", "Username must not be empty");
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(user_username).matches()){
            displayDialog("Unable to login", "Invalid email format");
        }

        if (user_password.isEmpty()){
            displayDialog("Unable to login", "Password must not be empty");
        }

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Logging in");
        progressDialog.setMessage("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(user_username,user_password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String uid = firebaseAuth.getCurrentUser().getUid();
                        databaseReference = firebaseDatabase.getReference();
                        DatabaseReference userRole = databaseReference.child("users").child(uid).child("role");
                        userRole.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                               switch (dataSnapshot.getValue().toString()){
                                   case "admin":
                                       progressDialog.dismiss();
                                       startActivity(new Intent(MainActivity.this, Admin_Dashboard.class));
                                       finish();
                                       break;
                                   case "user":
                                       progressDialog.dismiss();
                                       startActivity(new Intent(MainActivity.this,User_Dashboard.class));
                                       finish();
                                       break;
                                   default:
                                       return;
                               }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
    }

    public void displayDialog(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(title).setMessage(message).setPositiveButton(android.R.string.ok,null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void register_activity(View v){
        Intent register = new Intent(MainActivity.this, Register.class);
        startActivity(register);
        finish();
    }
}
