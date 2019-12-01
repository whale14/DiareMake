package com.example.diaremake;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diaremake.databinding.FragUserInfoBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FragUserInfo extends Fragment {
    private View view;
    private FragmentManager fm;
    private FragmentTransaction ft;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_user_info, container, false);
        FragUserInfoBinding binding = DataBindingUtil.inflate(inflater, R.layout.frag_user_info, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        binding.userNameText.setText(user.getDisplayName());
        return binding.getRoot();
    }

    private static class RecByDateAdapter extends RecyclerView.Adapter<RecByDateAdapter.DailyViewHolder> {
        interface $interface$ {
            void $method$(DailyModelData model);
        }

        private $interface$ mListener;

        private List<DailyModelData> mItems = new ArrayList<>();

        public RecByDateAdapter() {}

        public RecByDateAdapter($interface$ listener) {
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
                    .inflate(R.layout.item_diaries, parent, false);
            final DailyViewHolder viewHolder = new DailyViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        final DailyModelData item = mItems.get(viewHolder.getAdapterPosition());
                        mListener.$method$(item);
                    }
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull DailyViewHolder holder, int position) {
            DailyModelData item = mItems.get(position);
            // TODO : 데이터를 뷰홀더에 표시하시오
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public static class DailyViewHolder extends RecyclerView.ViewHolder {
            // TODO : 뷰홀더 완성하시오

            public DailyViewHolder(@NonNull View itemView) {
                super(itemView);
                // TODO : 뷰홀더 완성하시오
            }
        }
    }
}
