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
import com.fu.thinh_nguyen.qrfoodorder.ui.customer.ScanQRActivity;
import com.fu.thinh_nguyen.qrfoodorder.ui.customer.ViewOrderActivity;
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

        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        NavigationView navigationView = fullView.findViewById(R.id.navigation_view);
        SetButtonByRole(navigationView);
        SetButtonAction(navigationView);
    }

    private void SetButtonAction(NavigationView navView){
        navView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            // điền các hành động cho các button ở đây
            if (item.getItemId() == R.id.nav_logout) {
                new TokenManager(this).clear();
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;
            }

            if(item.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }

            if (item.getItemId() == R.id.nav_scan_qr) {
                // Gọi activity quét mã QR
                startActivity(new Intent(this, ScanQRActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }
            if(item.getItemId() == R.id.view_my_order){
                startActivity(new Intent(this, ViewOrderActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }

            return true;
        });

    }

    private void SetButtonByRole(NavigationView navView){
        TokenManager tokenManager = new TokenManager(this);
        String role = tokenManager.getRole();
        if("customer".equalsIgnoreCase(role)){
            navView.getMenu().findItem(R.id.nav_scan_qr).setVisible(true);
            navView.getMenu().findItem(R.id.view_my_order).setVisible(true);
        }
        // them button staff
        if("staff".equalsIgnoreCase(role)){
            // them các button thuộc role nào ở đây
        }
        if("manager".equalsIgnoreCase(role)){

        }
    }
}
