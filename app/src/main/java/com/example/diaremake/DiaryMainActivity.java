package com.example.diaremake;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.diaremake.databinding.ActivityDiaryMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        binding.detailRecyclerView.setLayoutManager(layoutManager);
        final DailyAdapter adapter = new DailyAdapter();
        binding.detailRecyclerView.setAdapter(adapter);

        setSupportActionBar(binding.detailToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();

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
        Query query = db.collection(user.getUid()).document(i.getStringExtra("title")).collection("daily");
        ListenerRegistration listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                List<DailyModelData> it = queryDocumentSnapshots.toObjects(DailyModelData.class);
                Collections.sort(it, new Comparator<DailyModelData>() {
                    @Override
                    public int compare(DailyModelData o1, DailyModelData o2) {
                        if (o1.getCreateDate() == o2.getCreateDate()) return 0;
                        else  if (o1.getCreateDate() > o2.getCreateDate()) return -1;
                        else return -1;
                    }
                });

                adapter.setItems(it);
                adapter.notifyDataSetChanged();
            }
        });
        adapter.setOnDailyClickListener(new DailyAdapter.OnDailyClickListener() {
            @Override
            public void onClicked(DailyModelData model) {
//                Intent intent_read = new Intent(getApplicationContext(), ReadDailyActivity.class);
//                i.putExtra("create_date", model.getCreateDate());
//                i.putExtra("date_text", model.getDate());
//                i.putExtra("weather", model.getWeather());
//                i.putExtra("alignment", model.getAlignment());
//                i.putExtra("img_url", model.getImgUrl());
//                startActivity(intent_read);
                Bundle bundle = new Bundle();
                bundle.putString("img_url", model.getImgUrl());
                bundle.putString("date", model.getDate());
                bundle.putString("weather", model.getWeather());
                bundle.putString("text", model.getText());
                bundle.putInt("alignment", model.getAlignment());
                FragReadDialog dialog = FragReadDialog.getInstance(bundle);
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), FragReadDialog.TAG);
            }
        });
    }
    View.OnClickListener v = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.go_write_btn :
                    Intent intent_write = new Intent(getApplicationContext(),WriteActivity.class);
                    intent_write.putExtra("title", i.getStringExtra("title"));
                    startActivity(intent_write);
                    break;
            }
        }
    };

    private static class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.DailyViewHolder> {
        interface OnDailyClickListener {
            void onClicked(DailyModelData model);
        }

        private OnDailyClickListener mListener;

        private List<DailyModelData> mItems = new ArrayList<>();

        public DailyAdapter() {}

        public void setOnDailyClickListener(OnDailyClickListener listener) {
            mListener = listener;
        }

        public void setItems(List<DailyModelData> items) {
            this.mItems = items;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public DailyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_daily, parent, false);
            final DailyViewHolder viewHolder = new DailyViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        final DailyModelData item = mItems.get(viewHolder.getAdapterPosition());
                        mListener.onClicked(item);
                    }
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull DailyViewHolder holder, int position) {
            DailyModelData item = mItems.get(position);
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(item.getImgUrl().toString());
            Glide.with(holder.itemView)
                    .load(storageRef)
                    .centerCrop()
                    .into(holder.img);

            holder.date_text.setText(item.getDate());
            holder.date_text.setTextColor(Color.WHITE);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public static class DailyViewHolder extends RecyclerView.ViewHolder {
            SquareImage img;
            TextView date_text;

            public DailyViewHolder(@NonNull View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.daily_img);
                date_text = itemView.findViewById(R.id.daily_date);
            }
        }
    }
}
