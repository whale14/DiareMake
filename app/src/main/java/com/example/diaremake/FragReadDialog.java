package com.example.diaremake;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.diaremake.databinding.FragCalBinding;
import com.example.diaremake.databinding.FragReadDialogBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FragReadDialog extends DialogFragment {
    public static final String TAG = "dialog_event";
    private View view;
    private FragReadDialogBinding binding;
    private Bundle bundle;

    public FragReadDialog() {
    }

    public static FragReadDialog getInstance(Bundle bundle) {
        FragReadDialog e = new FragReadDialog();
        return e;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_read_dialog, container, false);
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_read_dialog, container, false);


        bundle = getArguments();
        Log.d(TAG, "onCreateView: " + bundle.getString("img_url"));
        String url = bundle.getString("img_url");
        Log.d(TAG, "onCreateView: " + url + "it is url");
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        Glide.with(this).load(storageRef).centerCrop().into(binding.readImg);
        binding.readDate.setText(bundle.getString("date"));
        binding.readWeather.setText(bundle.getString("weather"));
        binding.readText.setText(bundle.getString("text"));
        binding.readText.setTextAlignment(bundle.getInt("alignment"));

        return view;
    }

}