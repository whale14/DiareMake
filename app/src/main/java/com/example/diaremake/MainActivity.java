package com.example.diaremake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.diaremake.databinding.ActivityMainBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.writeBtn.setOnClickListener(v);
        binding.logoutBtn.setOnClickListener(v);
    }

    View.OnClickListener v = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.write_btn:
                    startActivity(new Intent(getApplicationContext(), WriteActivity.class));
                    break;
                case R.id.logout_btn:
                    AuthUI.getInstance()
                            .signOut(getApplicationContext())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                public void onComplete(@NonNull Task<Void> task) {
                                    // ...
                                }
                            });
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                    break;
            }
        }
    };
}
