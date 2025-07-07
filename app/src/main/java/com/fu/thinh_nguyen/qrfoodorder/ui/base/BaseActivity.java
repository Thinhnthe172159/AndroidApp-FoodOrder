package com.fu.thinh_nguyen.qrfoodorder.ui.base;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.prefs.TokenManager;
import com.fu.thinh_nguyen.qrfoodorder.ui.auth.LoginActivity;
import com.google.android.material.navigation.NavigationView;

public class BaseActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;
    private ImageButton btnMenu;

    @Override
    public void setContentView(int layoutResID) {
        View fullView = LayoutInflater.from(this)
                .inflate(R.layout.drawer_base, null);

        // Khung để inject layout con
        FrameLayout container = fullView.findViewById(R.id.container);
        LayoutInflater.from(this).inflate(layoutResID, container, true);
        super.setContentView(fullView);

        drawerLayout = fullView.findViewById(R.id.drawer_layout);
        btnMenu       = fullView.findViewById(R.id.btn_menu);
        NavigationView navigationView = fullView.findViewById(R.id.navigation_view);

        // Mở drawer khi nhấn nút menu
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Xử lý click menu
        navigationView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawer(GravityCompat.START);   // đóng ngăn kéo

            if (item.getItemId() == R.id.nav_logout) {
                // 1) Xoá token
                new TokenManager(this).clear();
                // 2) Quay về LoginActivity
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;
            }


            return true;
        });
    }
}
