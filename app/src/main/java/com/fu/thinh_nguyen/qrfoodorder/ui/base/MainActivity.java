package com.fu.thinh_nguyen.qrfoodorder.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.api.ApiService;
import com.fu.thinh_nguyen.qrfoodorder.data.model.CurrentUserDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.UserDto;
import com.fu.thinh_nguyen.qrfoodorder.data.network.RetrofitClient;
import com.fu.thinh_nguyen.qrfoodorder.data.prefs.TokenManager;
import com.fu.thinh_nguyen.qrfoodorder.ui.auth.LoginActivity;
import com.fu.thinh_nguyen.qrfoodorder.ui.auth.RegisterActivity;
import com.fu.thinh_nguyen.qrfoodorder.ui.customer.CustomerMainActivity;
import com.fu.thinh_nguyen.qrfoodorder.ui.staff.StaffMainActivity;
import com.fu.thinh_nguyen.qrfoodorder.ui.manager.ManagerMainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TokenManager tokenManager;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tokenManager = new TokenManager(this);
        api = RetrofitClient.getInstance(tokenManager).create(ApiService.class);

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnSignup = findViewById(R.id.btnSignup);

        String token = tokenManager.get();
        if (token != null) {
            checkLoginWithToken();
        }

        btnLogin.setOnClickListener(v -> goToLogin());

        btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }


    private void checkLoginWithToken() {
        api.getCurrentUser().enqueue(new Callback<CurrentUserDto>() {
            @Override
            public void onResponse(Call<CurrentUserDto> call, Response<CurrentUserDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    goToRole(response.body().getRole());
                }
            }

            @Override
            public void onFailure(Call<CurrentUserDto> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi mạng, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
                Toast.makeText(this, "Unknown role: " + roleFixed, Toast.LENGTH_SHORT).show();
                goToLogin();
                return;
        }

        startActivity(intent);
        finish();
    }
}
