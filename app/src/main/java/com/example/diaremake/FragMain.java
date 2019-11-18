package com.example.diaremake;

import android.content.Intent;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
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

        binding.goWriteBtn.setOnClickListener(v);
        binding.logoutBtn.setOnClickListener(v);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        List<Diaries> item = new ArrayList<>();
        final DiaryAdapter adapter = new DiaryAdapter();
        binding.recyclerView.setAdapter(adapter);

        assert user != null;
        db.collection(user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List it = new ArrayList();
                it.add(task.getResult().getDocuments().toArray());
                adapter.setItems(it);
                adapter.notifyDataSetChanged();
                Log.d(TAG, "onComplete: " +it.toString());
            }
        });
        return binding.getRoot();
    }
    View.OnClickListener v = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.go_write_btn:
                    startActivity(new Intent(getContext(), MakeDiaryActivity.class));
                    break;
                case R.id.logout_btn:
                    AuthUI.getInstance()
                            .signOut(getContext())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                public void onComplete(@NonNull Task<Void> task) {
                                    // ...
                                }
                            });
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    getActivity().finish();
                    break;
            }
        }
    };
    private static class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {
        interface OnDiaryClickListener {
            void onDiaryClicked(Diaries model);
        }

        private OnDiaryClickListener mListener;

        private List<Diaries> mItems = new ArrayList<>();

        public DiaryAdapter() {}

        public DiaryAdapter(OnDiaryClickListener listener) {
            mListener = listener;
        }

        public void setItems(List<Diaries> items) {
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
                        final Diaries item = mItems.get(viewHolder.getAdapterPosition());
                        mListener.onDiaryClicked(item);
                    }
                }
            });
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
            Diaries item = mItems.get(position);
            Glide.with(holder.titleImage).load(mItems.get(position).getImg()).into(holder.titleImage);
            holder.titleText.setText("짠");

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
