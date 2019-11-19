package com.example.diaremake;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.diaremake.databinding.ActivityDiaryMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DiaryMainActivity extends AppCompatActivity {

    ActivityDiaryMainBinding binding;
    FirebaseFirestore db;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_main);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_diary_main);
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();

        final Intent i = getIntent();
//        db.collection(user.getUid()).document(i.getStringExtra("title")).get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                Log.d("123", "onComplete: " + task.getResult().get("img").toString());
//                String uri = task.getResult().get("img").toString();
//                Toast.makeText(DiaryMainActivity.this, uri, Toast.LENGTH_SHORT).show();
//                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri);
//                Glide.with(getApplicationContext())
//                        .load(storageRef)
//                        .centerCrop()
//                        .into(binding.titleImg);
//                binding.titleText.setText(task.getResult().get("title").toString());
//            }
//        });
        db.collection(user.getUid()).whereEqualTo("img", i.getStringExtra("img")).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

            }
        });
    }
}
