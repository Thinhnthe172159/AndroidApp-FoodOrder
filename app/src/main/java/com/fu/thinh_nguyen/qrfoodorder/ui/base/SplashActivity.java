package com.fu.thinh_nguyen.qrfoodorder.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.api.ApiService;
import com.fu.thinh_nguyen.qrfoodorder.data.model.CurrentUserDto;
import com.fu.thinh_nguyen.qrfoodorder.data.network.RetrofitClient;
import com.fu.thinh_nguyen.qrfoodorder.data.prefs.TokenManager;
import com.fu.thinh_nguyen.qrfoodorder.ui.auth.LoginActivity;
import com.fu.thinh_nguyen.qrfoodorder.ui.customer.CustomerMainActivity;
import com.fu.thinh_nguyen.qrfoodorder.ui.manager.ManagerMainActivity;
import com.fu.thinh_nguyen.qrfoodorder.ui.staff.StaffMainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private TokenManager tokenManager;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tokenManager = new TokenManager(this);
        api = RetrofitClient.getInstance(tokenManager).create(ApiService.class);

        new Handler().postDelayed(() -> {
            String token = tokenManager.get();
            if (token != null) {
                checkLoginWithToken();
            } else {
                goToOnboarding();
            }
        }, 2000);
    }

    private void checkLoginWithToken() {
        api.getCurrentUser().enqueue(new Callback<CurrentUserDto>() {
            @Override
            public void onResponse(Call<CurrentUserDto> call, Response<CurrentUserDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    goToRole(response.body().getRole());
                } else {
                    goToOnboarding();
                }
            }

            @Override
            public void onFailure(Call<CurrentUserDto> call, Throwable t) {
                Toast.makeText(SplashActivity.this, "Lỗi mạng, thử lại sau", Toast.LENGTH_SHORT).show();
                goToOnboarding();
            }
        });
    }

    private void goToOnboarding() {
        Intent intent = new Intent(SplashActivity.this, OnboardingActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToRole(String role) {
        Intent intent;
        String roleFixed = role.toLowerCase().trim();

        switch (roleFixed) {
            case "customer":
                intent = new Intent(this, CustomerMainActivity.class); break;
            case "staff":
                intent = new Intent(this, StaffMainActivity.class); break;
            case "manager":
                intent = new Intent(this, ManagerMainActivity.class); break;
            default:
                intent = new Intent(this, LoginActivity.class);
                break;
        }

        startActivity(intent);
        finish();
    }
}
