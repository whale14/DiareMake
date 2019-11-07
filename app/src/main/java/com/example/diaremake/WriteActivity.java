package com.example.diaremake;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.diaremake.databinding.ActivityWriteBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;
import java.text.DateFormat;

public class WriteActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ActivityWriteBinding binding;
    private static final int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_write);

        binding.squareImage.setOnClickListener(v);
    }

    View.OnClickListener v = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, REQUEST_CODE);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());

                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();

                    binding.squareImage.setImageBitmap(bitmap);
                } catch (Exception e) {

                }
            } else if (resultCode ==  RESULT_CANCELED) {
                Toast.makeText(this, "사진선택취소", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
