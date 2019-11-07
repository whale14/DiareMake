package com.example.diaremake;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.example.diaremake.databinding.FragMainBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class FragMain extends Fragment {
    private View view;
    private FragMainBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_main, container, false);
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_main, container, false);

        binding.goWriteBtn.setOnClickListener(v);
        binding.logoutBtn.setOnClickListener(v);

        return binding.getRoot();
    }
    View.OnClickListener v = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.go_write_btn:
                    startActivity(new Intent(getContext(), WriteActivity.class));
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
}
