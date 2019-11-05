package com.example.diaremake;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.diaremake.databinding.ActivityWriteBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;

public class WriteActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        ActivityWriteBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_write);

        binding.squareImage.setOnClickListener(v);
    }

    View.OnClickListener v = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("1", "onClick: 1");
        }
    };
}
