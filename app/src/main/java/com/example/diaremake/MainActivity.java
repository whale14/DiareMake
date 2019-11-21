package com.example.diaremake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import com.example.diaremake.databinding.ActivityMainBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private FragMain fragMain;
    private FragCal fragCal;
    private FragUserInfo fragUserInfo;
    private FragmentManager fm;
    private FragmentTransaction ft;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_tool_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_create_diary) {
            startActivity(new Intent(getApplicationContext(), MakeDiaryActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(binding.mainToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        setFrag(0);
                        break;
                    case R.id.action_cal:
                        setFrag(1);
                        break;
                    case R.id.action_user:
                        setFrag(2);
                        break;
                }
                return true;
            }
        });

        fragMain = new FragMain();
        fragCal = new FragCal();
        fragUserInfo = new FragUserInfo();

        setFrag(0);

    }

    //프래그먼트 교체 실행문
    private void setFrag(int n) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n) {
            case 0:
                ft.replace(R.id.main_frame, fragMain);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.main_frame, fragCal);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.main_frame, fragUserInfo);
                ft.commit();
                break;
        }
    }


}
