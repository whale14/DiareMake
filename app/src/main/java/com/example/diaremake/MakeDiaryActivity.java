package com.example.diaremake;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.diaremake.databinding.ActivityMakeDiaryBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MakeDiaryActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ActivityMakeDiaryBinding binding;
    private static final int REQUEST_CODE = 0;
    private Uri filePath;
    private String url;
    private String title;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_diary);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_make_diary);

        binding.squareImage.setOnClickListener(v);
        binding.makeDiaryBtn.setOnClickListener(v);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
    }

    View.OnClickListener v = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.squareImage :
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, REQUEST_CODE);
                    break;
                case R.id.make_diary_btn :
                    title = binding.editText.getText().toString();
                    uploadTitleImage();
//                    uploadDB();

                    break;
            }
        }
    };



    private void uploadDB() {
        TitleModelData titleModelData = new TitleModelData(
                title,
                url);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection(user.getUid()).document(binding.editText.getText().toString()).set(titleModelData);
        Intent i = new Intent(getApplicationContext(),DiaryMainActivity.class);
        i.putExtra("title", title);
        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    filePath = data.getData();
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());

                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();

                    binding.squareImage.setImageBitmap(bitmap);
                } catch (Exception ignored) {

                }
            } else if (resultCode ==  RESULT_CANCELED) {
                Toast.makeText(this, "사진선택취소", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //upload the file
    private void uploadTitleImage() {
        //업로드할 파일이 있으면 수행
        if (filePath != null) {
            //업로드 진행 Dialog 보이기
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("업로드중...");
            progressDialog.show();

            //storage
            FirebaseStorage storage = FirebaseStorage.getInstance();

            //Unique한 파일명을 만들자.
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
            Date now = new Date();
            final String filename = formatter.format(now) + ".png";
            //storage 주소와 폴더 파일명을 지정해 준다.
            StorageReference storageRef = storage.getReference().child("title/" + filename);
            url = storageRef.toString();
            //올라가거라...
            TitleModelData titleModelData = new TitleModelData(
                    title,
                    url);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            db.collection(user.getUid()).document(filename.substring(0,13)).set(titleModelData);
            storageRef.putFile(filePath)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
                            Intent i = new Intent(getApplicationContext(),DiaryMainActivity.class);
                            i.putExtra("title", filename.substring(0,13));
                            startActivity(i);
                            finish();
                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") //이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }
}
