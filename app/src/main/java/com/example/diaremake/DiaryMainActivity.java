package com.example.diaremake;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.diaremake.databinding.ActivityDiaryMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    FirebaseUser user;
    FirebaseStorage storage;
    Intent i;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_tool_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_delete_diary) {
            db.collection(user.getUid()).document(i.getStringExtra("title"))
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_diary_main);

        setSupportActionBar(binding.detailToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        binding.detailRecyclerView.setLayoutManager(layoutManager);

        binding.goWriteBtn.setOnClickListener(v);


        i = getIntent();
        db.collection(user.getUid()).document(i.getStringExtra("title"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Log.d("123", "onComplete: " + task.getResult().get("img").toString());
                        binding.detailTitle.setText(task.getResult().get("title").toString());
                        String uri = task.getResult().get("img").toString();
                        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri);
                        Glide.with(getApplicationContext())
                                .load(storageRef)
                                .centerCrop()
                                .into(binding.titleImg);
                    }
                });
    }
    View.OnClickListener v = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.go_write_btn :
                    startActivity(new Intent(getApplicationContext(), WriteActivity.class));
                    break;
            }
        }
    };
}
