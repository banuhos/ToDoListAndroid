package com.banu.bh318.todolist.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import dmax.dialog.SpotsDialog;

import com.banu.bh318.todolist.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ToDoList_Login extends AppCompatActivity {

    private EditText emailEditText,passwordEditText;
    private Button login,register,forgot;
    private FirebaseAuth auth;
    private DatabaseReference ref;
    private FirebaseDatabase database;
    private String email,password;
    private AlertDialog dialog;
    private ToDoList_Warning uyarilar;
    private ConstraintLayout coordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean internet = hasActiveInternetConnection();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (internet == false) {
            setContentView(R.layout.activity_to_do_list_internet_kontrol);

            coordinatorLayout = findViewById(R.id.coordinatorLayout);

            final Snackbar snackBar = Snackbar.make(coordinatorLayout, "İnternet bağlantısı yok \n", Snackbar.LENGTH_INDEFINITE);
            snackBar.setActionTextColor(Color.WHITE).setAction("Kapat", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackBar.dismiss();
                }
            });
            snackBar.show();
        } else {
            if (user == null) {
                setContentView(R.layout.activity_to_do_list_login);

                auth = FirebaseAuth.getInstance();
                ref = FirebaseDatabase.getInstance().getReference();
                database = FirebaseDatabase.getInstance();

                emailEditText = (EditText) findViewById(R.id.email_editText);
                passwordEditText = (EditText) findViewById(R.id.password_editText);
                login = (Button) findViewById(R.id.btnLogin);
                register = (Button) findViewById(R.id.btnRegister);
                forgot=(Button) findViewById(R.id.btnForgot);

                dialog = new SpotsDialog.Builder().setContext(this).setTheme(R.style.Custom).build();
                uyarilar = new ToDoList_Warning(this);

                register.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ToDoList_Login.this, ToDoList_Register.class);
                        startActivity(intent);
                    }
                });

                login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        email = emailEditText.getText().toString().trim();
                        password = passwordEditText.getText().toString().trim();
                        if (!(email.equals(null) || email.equals("") || password.equals(null) || password.equals(""))) {
                            login(email, password);
                        } else {
                            uyarilar.warning("Lütfen hiçbir alanı boş bırakmayınız!");
                        }
                    }
                });

                forgot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        forgotPassword();
                    }
                });
            }else{
                Intent main = new Intent(ToDoList_Login.this, ToDoList_Kategoriler.class);
                startActivity(main);
            }
        }
    }
    public void login( final String email, final String password) {
        dialog.show();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            if (auth.getCurrentUser().isEmailVerified()) {
                                database.getReference().child("kullanicilar").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String s_email=dataSnapshot.child("email").getValue().toString();
                                            String s_pass=dataSnapshot.child("sifre").getValue().toString();
                                            if(email.equals(s_email)||password.equals(s_pass)){
                                                if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                                                    Intent login = new Intent(ToDoList_Login.this, ToDoList_Kategoriler.class);
                                                    startActivity(login);
                                                }
                                            }else {
                                                uyarilar.registrationFailed();
                                            }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                            } else {
                                uyarilar.mailControl();
                            }

                        } else {
                            uyarilar.registrationFailed();
                            dialog.dismiss();
                        }
                    }
                });
    }

    public void forgotPassword(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.to_do_list_sifre_reset_popup);
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(lp);

        final EditText emailAdresi = dialog.findViewById(R.id.forgotEmail);
        Button button = dialog.findViewById(R.id.resetButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(emailAdresi.getText().toString().trim().equals("")||emailAdresi.getText().toString().trim().equals(null))){
                    FirebaseAuth.getInstance().sendPasswordResetEmail(emailAdresi.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        uyarilar.resetBasarili();
                                    }else{
                                        uyarilar.resetBasarisiz();
                                    }
                                }
                            });
                }else{
                    uyarilar.hata("Lütfen kayıtlı e-posta adresinizi girin.");
                }

                dialog.dismiss();
            }
        });
    }
    public boolean hasActiveInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        android.net.NetworkInfo datac = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (wifi != null & datac != null)
                && (wifi.isConnected() | datac.isConnected());
    }
}
