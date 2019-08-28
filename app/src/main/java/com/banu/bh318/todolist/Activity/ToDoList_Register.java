package com.banu.bh318.todolist.Activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.banu.bh318.todolist.Modal.User;
import com.banu.bh318.todolist.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dmax.dialog.SpotsDialog;

/**
 * Created by bh318 on 23.08.2019.
 */

public class ToDoList_Register extends AppCompatActivity {

    private EditText emailText, passwordText, passwordText2;
    private Button registerBtn;
    private FirebaseAuth auth;
    private DatabaseReference ref;
    private FirebaseDatabase database;
    private String email, password, password2;
    private AlertDialog dialog;
    private ToDoList_Warning uyarilar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list_register);

        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();
        database = FirebaseDatabase.getInstance();

        emailText = (EditText) findViewById(R.id.rg_email);
        passwordText = (EditText) findViewById(R.id.rg_sifre);
        passwordText2 = (EditText) findViewById(R.id.rg_sifreTekrar);
        registerBtn = (Button) findViewById(R.id.rg_buton);

        dialog = new SpotsDialog.Builder().setContext(this).setTheme(R.style.Custom).build();
        uyarilar = new ToDoList_Warning(this);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailText.getText().toString().trim();
                password = passwordText.getText().toString().trim();
                password2 = passwordText2.getText().toString().trim();

                if (!email.equals(null) || email.equals("") || password.equals(null) || password.equals("") || password2.equals(null) || password2.equals("")) {
                    signUp(email, password);
                } else {
                    uyarilar.warning("Lütfen hiçbir alanı boş bırakmayınız!");
                }
            }
        });

    }

    public void signUp(final String email, final String password) {
        if (!password.equals(password2)) {
            uyarilar.warning("Şifreler uyuşmuyor!!");
        } else if (password.isEmpty() || email.isEmpty()) {
            uyarilar.warningNull("Lütfen boş alan bırakmayınız!!");
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                final FirebaseUser user = auth.getCurrentUser();
                                User newUser = new User(email, password);
                                ref.child("kullanicilar").child(user.getUid()).setValue(newUser)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                auth.signInWithEmailAndPassword(email, password)
                                                        .addOnCompleteListener(ToDoList_Register.this, new OnCompleteListener<AuthResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                if (task.isSuccessful()) {
                                                                    auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                uyarilar.registrationSuccesful();
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                            }
                        }
                    });
        }
    }
}
