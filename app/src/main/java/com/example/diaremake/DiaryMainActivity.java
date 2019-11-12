package com.example.diaremake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.diaremake.databinding.ActivityDiaryMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class DiaryMainActivity extends AppCompatActivity {

    ActivityDiaryMainBinding binding;
    FirebaseFirestore db;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_main);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_diary_main);
        user = FirebaseAuth.getInstance().getCurrentUser();
        Intent i = getIntent();
        Log.d("123", "onCreate: " + i.getStringExtra("title"));
//        Task<QuerySnapshot> collection = db.collection(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                Log.d("123", "onComplete: " + task.getResult().toString());
//            }
//        });
        DocumentReference documentReference = db.collection(user.getUid()).document(i.getStringExtra("title"));
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.d("123", "onComplete: " + task.getResult());
            }
        });

    }
//    Glide.with(getApplicationContext()).load(task.getResult().get("img").toString()).into(binding.titleImg);
}
