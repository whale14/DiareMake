package com.example.diaremake;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

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
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("일기장 삭제");
            builder.setMessage("이 일기장에 작성한 모든 일기가 삭제됩니다. 정말 일기장을 삭제하시겠습니까?");
            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
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
            });
            builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_diary_main);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
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
                        else if (o1.getCreateDate() > o2.getCreateDate()) return -1;
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
            }
        });
    }

    View.OnClickListener v = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.go_write_btn:
                    Intent intent_write = new Intent(getApplicationContext(), WriteActivity.class);
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

        public DailyAdapter() {
        }

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
        public void onBindViewHolder(@NonNull final DailyViewHolder holder, int position) {
            DailyModelData item = mItems.get(position);
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(item.getImgUrl().toString());
            Glide.with(holder.itemView)
                    .load(storageRef)
                    .centerCrop()
                    .into(holder.img);

            holder.textDaily.setText(item.getText());
            holder.textDaily.setTextColor(Color.WHITE);
            holder.weatherDaily.setText(item.getWeather());
            holder.dateDaily.setText(item.getDate());
            holder.textDaily.setTextAlignment(item.getAlignment());
            holder.textDaily.setGravity(item.getGravity());
            holder.showImgBtn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_MOVE:
                            holder.textDaily.setVisibility(View.INVISIBLE);
                            break;
                        default:
                            holder.textDaily.setVisibility(View.VISIBLE);
                            break;
                    }
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public static class DailyViewHolder extends RecyclerView.ViewHolder {
            SquareImage img;
            TextView textDaily, dateDaily, weatherDaily;
            Button showImgBtn;

            public DailyViewHolder(@NonNull View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.daily_img);
                textDaily = itemView.findViewById(R.id.daily_text);
                dateDaily = itemView.findViewById(R.id.daily_date);
                weatherDaily = itemView.findViewById(R.id.daily_weather);
                showImgBtn = itemView.findViewById(R.id.show_img_btn);

            }
        }
    }
}
