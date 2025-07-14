package com.fu.thinh_nguyen.qrfoodorder.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.api.ApiService;
import com.fu.thinh_nguyen.qrfoodorder.data.model.ResetPasswordDto;
import com.fu.thinh_nguyen.qrfoodorder.data.network.RetrofitClient;
import com.fu.thinh_nguyen.qrfoodorder.data.prefs.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText edtEmail,  edtPassword, edtConfirmPassword;
    private ApiService apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        ImageButton btnBack = findViewById(R.id.btnBack);
        Button btnResetPassword = findViewById(R.id.btnResetPassword);

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish(); // Optional: close LoginActivity so it's not in back stack
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // Optional animation
        });
        btnResetPassword.setOnClickListener(v -> doResetPassword());

    }

    private void doResetPassword() {
        if (!edtPassword.getText().toString().equals(edtConfirmPassword.getText().toString())) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
        resetPasswordDto.setEmail(edtEmail.getText().toString());
        resetPasswordDto.setNewPassword(edtPassword.getText().toString());

        String token = getIntent().getStringExtra("token");
        resetPasswordDto.setToken(token == null ? "" : token);

        TokenManager tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getInstance(tokenManager).create(ApiService.class);

        apiService.ResetPassword(resetPasswordDto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("API_RAW", "Status code: " + response.code());
                try {
                    Log.d("API_RAW", "Error body: " + (response.errorBody() != null ? response.errorBody().string() : "null"));
                } catch (Exception e) {
                    Log.e("API_RAW", "Error reading error body", e);
                }

                if (response.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, "Reset password successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Reset password failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
