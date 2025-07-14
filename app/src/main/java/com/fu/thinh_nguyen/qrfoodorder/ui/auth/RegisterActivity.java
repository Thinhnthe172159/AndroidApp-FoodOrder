package com.fu.thinh_nguyen.qrfoodorder.ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.api.ApiService;
import com.fu.thinh_nguyen.qrfoodorder.data.model.RegisterDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.UserDto;
import com.fu.thinh_nguyen.qrfoodorder.data.network.RetrofitClient;
import com.fu.thinh_nguyen.qrfoodorder.data.prefs.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends Activity {

    private EditText etFullName, etEmail, etPhone, etUsername, etPassword, etConfirmPassword;
    private ImageButton btnBack;
    private Button btnRegister;
    private ApiService apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> doRegister());
    }

    private void doRegister() {
        if (etFullName == null || etEmail == null || etPhone == null || etUsername == null || etPassword == null || etConfirmPassword == null) {
            Toast.makeText(RegisterActivity.this, "Register error: Missing fields", Toast.LENGTH_SHORT).show();
            return;
        } else if (!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
            Toast.makeText(RegisterActivity.this, "Register error: Password doesn't match", Toast.LENGTH_SHORT).show();
            return;
        }

        RegisterDto registerDto = new RegisterDto();
        registerDto.setFullName(etFullName.getText().toString());
        registerDto.setEmail(etEmail.getText().toString());
        registerDto.setPhone(etPhone.getText().toString());
        registerDto.setUsername(etUsername.getText().toString());
        registerDto.setPassword(etPassword.getText().toString());
        registerDto.setConfirmPassword(etConfirmPassword.getText().toString());
        registerDto.setRoleId(3);
        TokenManager tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getInstance(tokenManager).create(ApiService.class);

        apiService.register(registerDto).enqueue(new Callback<UserDto>() {

            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                Log.d("API_RAW", "Status code: " + response.code());
                Log.d("API_RAW", "Error body: " + response.errorBody());
                Log.d("API_RAW", "Body is null? " + response.body().getFullName() );
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivity.this, "Register success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                Toast.makeText(RegisterActivity.this, "Register error: " + response.message(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<UserDto> call, Throwable throwable) {
                Toast.makeText(RegisterActivity.this, "Register error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
