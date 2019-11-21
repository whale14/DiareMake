package com.example.diaremake;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.diaremake.databinding.FragMainBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FragMain extends Fragment {
    private View view;
    private FragMainBinding binding;

    private static final String TAG = MainActivity.class.getSimpleName();

    FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_main, container, false);
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_main, container, false);

        binding.logoutBtn.setOnClickListener(v);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        final DiaryAdapter adapter = new DiaryAdapter();
        binding.recyclerView.setAdapter(adapter);

        assert user != null;
        Query query = db.collection(user.getUid());
        ListenerRegistration listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                List<TitleModelData> it = queryDocumentSnapshots.toObjects(TitleModelData.class);
                Collections.sort(it, new Comparator<TitleModelData>() {
                    @Override
                    public int compare(TitleModelData o1, TitleModelData o2) {
                        Long day1 = Long.parseLong(o1.getImg().substring(33,41) + o1.getImg().substring(42,46));
                        Long day2 = Long.parseLong(o2.getImg().substring(33,41) + o2.getImg().substring(42,46));

                        if (day1.equals(day2)) return 0;
                        else  if (day1 >day2) return -1;
                        else return -1;
                    }
                });
                adapter.setItems(it);
                adapter.notifyDataSetChanged();
                Log.d(TAG, "onComplete: " +it.toString());
            }
        });

        adapter.setOnDiaryClickListener(new DiaryAdapter.OnDiaryClickListener() {
            @Override
            public void onDiaryClicked(TitleModelData model) {
                Log.d(TAG, "onClick: " + model.getImg().substring(33,46));
                Intent i = new Intent(getContext(), DiaryMainActivity.class);
                i.putExtra("title", model.getImg().substring(33,46));
                startActivity(i);
            }
        });
        return binding.getRoot();
    }
    View.OnClickListener v = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.logout_btn) {
                AuthUI.getInstance()
                        .signOut(getContext())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...
                            }
                        });
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
            }
        }
    };

    //다이어리 리사이클러 어댑터
    private static class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {
        interface OnDiaryClickListener {
            void onDiaryClicked(TitleModelData model);
        }

        private OnDiaryClickListener mListener;

        private List<TitleModelData> mItems = new ArrayList<>();

        public DiaryAdapter() {}

        public void setOnDiaryClickListener(OnDiaryClickListener listener) {
            mListener = listener;
        }

        public void setItems(List<TitleModelData> items) {
            this.mItems = items;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_diaries, parent, false);
            final DiaryViewHolder viewHolder = new DiaryViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        final TitleModelData item = mItems.get(viewHolder.getAdapterPosition());
                        mListener.onDiaryClicked(item);;
                    }
                }
            });
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
            TitleModelData item = mItems.get(position);
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(item.getImg().toString());
            Glide.with(holder.itemView)
                    .load(storageRef)
                    .centerCrop()
                    .into(holder.titleImage);

            holder.titleText.setText(item.getTitle());
            holder.titleText.setTextColor(Color.WHITE);

        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public static class DiaryViewHolder extends RecyclerView.ViewHolder {
            SquareImage titleImage;
            TextView titleText;

            public DiaryViewHolder(@NonNull View itemView) {
                super(itemView);
                titleImage = itemView.findViewById(R.id.diaries_img);
                titleText = itemView.findViewById(R.id.diaries_text);
            }
        }
    }
}
