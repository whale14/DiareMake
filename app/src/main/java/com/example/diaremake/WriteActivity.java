package com.example.diaremake;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.diaremake.databinding.ActivityWriteBinding;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WriteActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 0;
    private Uri filePath;
    ActivityWriteBinding binding;
    private ProgressDialog progressDialog;
    private String url;
    private FirebaseFirestore db;
    Intent i;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.write_tool_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_back_to_diary) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        i = getIntent();
        db = FirebaseFirestore.getInstance();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_write);

        setSupportActionBar(binding.writeToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.writeTitle.setText("일기쓰기");

        binding.writeImgChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        binding.writeTextEt.setHintTextColor(Color.parseColor("#aaffffff"));
        binding.writeTextEt.setTextColor(Color.WHITE);

        binding.alignSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text;
                switch (position) {
                    case 0 :
                        binding.writeTextEt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                        text = binding.writeTextEt.getText().toString();
                        binding.writeTextEt.setText(text);
                        break;
                    case 1 :
                        binding.writeTextEt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        text = binding.writeTextEt.getText().toString();
                        binding.writeTextEt.setText(text);
                        break;
                    case 2 :
                        binding.writeTextEt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                        text = binding.writeTextEt.getText().toString();
                        binding.writeTextEt.setText(text);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.saveDailyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadTitleImage();
            }
        });
    }

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
            SimpleDateFormat formatter_filename = new SimpleDateFormat("yyyyMMHH_mmss");
            SimpleDateFormat formatter_create = new SimpleDateFormat("yyyyMMHHmmss");
            Date now = new Date();
            final String filename = formatter_filename.format(now) + ".png";
            //storage 주소와 폴더 파일명을 지정해 준다.
            StorageReference storageRef = storage.getReference().child("daily/" + filename);
            url = storageRef.toString();
            //올라가거라...

            //데이터 초기화
            Date createDay = new Date();
            Long createDate = Long.parseLong(formatter_create.format(createDay));

            DailyModelData dailyModelData = new DailyModelData(createDate,
                    binding.dateEt.getText().toString(),
                    binding.weather.getText().toString(),
                    url, binding.writeTextEt.getText().toString(),
                    binding.writeTextEt.getTextAlignment());

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            db.collection(user.getUid()).document(i.getStringExtra("title")).collection("daily").document(String.valueOf(dailyModelData.getCreateDate())).set(dailyModelData);
            storageRef.putFile(filePath)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
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
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
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

                    binding.writeBackImg.setImageBitmap(bitmap);
                } catch (Exception ignored) {

                }
            } else if (resultCode ==  RESULT_CANCELED) {
                Toast.makeText(this, "사진선택취소", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
