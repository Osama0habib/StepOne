package com.example.stepone;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

EditText eMail , passWord , userName;
Button signUpBtn;
FirebaseAuth auth;
DatabaseReference reference;
TextView mLogin;
FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null){
            Intent intent = new Intent(RegisterActivity.this , MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



eMail = findViewById(R.id.signinemail);
passWord = findViewById(R.id.signinpassword);
userName = findViewById(R.id.userName);
signUpBtn = findViewById(R.id.signInButton);
auth= FirebaseAuth.getInstance();
mLogin =findViewById(R.id.signinref);
mLogin.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
    }
});
//mLogin.setOnClickListener(new View.OnClickListener() {
//    @Override
//    public void onClick(View v) {
//        Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
//    }
//});

signUpBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String txt_username = userName.getText().toString();
        String txt_email = eMail.getText().toString();
        String txt_password = passWord.getText().toString();

        if(TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
            Toast.makeText(RegisterActivity.this,"All Fields Are Required",Toast.LENGTH_SHORT).show();
        }else if (txt_password.length() < 8){
            Toast.makeText(RegisterActivity.this,"Password Must Be At Least 8 Characters",Toast.LENGTH_SHORT).show();
        }else {
            register(txt_username ,txt_email , txt_password);
        }
    }
});
    }
    private void register (final String username , String email , String password){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    String userID = firebaseUser.getUid();
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                    HashMap<String,String> hashMap =new HashMap<>();
                    hashMap.put("id",userID);
                    hashMap.put("username",username);
                    hashMap.put("imageURL","default");
                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                              if(task.isSuccessful()){
                                  Toast.makeText(RegisterActivity.this,"SignUp Complete",Toast.LENGTH_SHORT).show();
                                  Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                  startActivity(intent);
                                  finish();
                              }
                        }
                    });
                }else {
                    Toast.makeText(RegisterActivity.this,"You Can't Register Using This Email Or Password",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}