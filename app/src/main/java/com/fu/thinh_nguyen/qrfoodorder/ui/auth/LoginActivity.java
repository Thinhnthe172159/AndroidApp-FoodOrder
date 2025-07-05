package com.fu.thinh_nguyen.qrfoodorder.ui.auth;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.api.ApiService;
import com.fu.thinh_nguyen.qrfoodorder.data.model.AuthResponseDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.LoginDto;
import com.fu.thinh_nguyen.qrfoodorder.data.network.RetrofitClient;
import com.fu.thinh_nguyen.qrfoodorder.data.prefs.TokenManager;
import com.fu.thinh_nguyen.qrfoodorder.ui.customer.CustomerMainActivity;
import com.fu.thinh_nguyen.qrfoodorder.ui.manager.ManagerMainActivity;
import com.fu.thinh_nguyen.qrfoodorder.ui.staff.StaffMainActivity;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity{
    private EditText edtUser, edtPass;
    private TokenManager tokenManager;
    private ApiService api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUser = findViewById(R.id.edtUsername);
        edtPass = findViewById(R.id.edtPassword);
        Button btnLogin = findViewById(R.id.btnLogin);

        tokenManager = new TokenManager(this);
        api = RetrofitClient.getInstance(tokenManager).create(ApiService.class);

        btnLogin.setOnClickListener(v -> {
            String u = edtUser.getText().toString().trim();
            String p = edtPass.getText().toString().trim();
            doLogin(u, p);
        });
    }

    private void doLogin(String u, String p) {
        try {
            LoginDto dto = new LoginDto();
            dto.setUsername(u);
            dto.setPassword(p);

            api.login(dto).enqueue(new Callback<AuthResponseDto>() {
                @Override
                public void onResponse(Call<AuthResponseDto> call, Response<AuthResponseDto> response) {
                    System.out.println("HTTP Code: " + response.code());
                    System.out.println("Body: " + response.body());
                    System.out.println("ErrorBody: " + response.errorBody());

                    if (response.isSuccessful() && response.body() != null) {
                        tokenManager.save(response.body().getToken());
                        String role = response.body().getUser().getRoleName();
                        goToRole(role);
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                }


                @Override
                public void onFailure(Call<AuthResponseDto> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(LoginActivity.this, "Unexpected error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void goToRole(String role) {
        try {
            if (role == null) {
                Toast.makeText(this, "Role is null!", Toast.LENGTH_SHORT).show();
                return;
            }

            String roleFixed = role.trim().toLowerCase();
            Intent intent;

            switch (roleFixed) {
                case "customer": intent = new Intent(this, CustomerMainActivity.class); break;
                case "staff": intent = new Intent(this, StaffMainActivity.class); break;
                case "manager": intent = new Intent(this, ManagerMainActivity.class); break;
                default:
                    Toast.makeText(this, "Unknown role: " + roleFixed, Toast.LENGTH_SHORT).show();
                    return;
            }

            startActivity(intent);
            finish();

        } catch (Exception e) {
            Toast.makeText(this, "Error when routing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
