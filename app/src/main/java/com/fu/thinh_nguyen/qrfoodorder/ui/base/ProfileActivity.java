package com.fu.thinh_nguyen.qrfoodorder.ui.base;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.api.ApiService;
import com.fu.thinh_nguyen.qrfoodorder.data.api.UserService;
import com.fu.thinh_nguyen.qrfoodorder.data.model.UserDto;
import com.fu.thinh_nguyen.qrfoodorder.data.network.RetrofitClient;
import com.fu.thinh_nguyen.qrfoodorder.data.prefs.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends BaseActivity {

    private TextView tvFullName, tvPhone, tvEmail;
    private Button btnEditProfile, btnSave;
    private EditText etFullName, etEmail, etPhone;
    private UserService userService;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tokenManager = new TokenManager(this);
        userService = RetrofitClient.getInstance(tokenManager).create(UserService.class);

        tvFullName = findViewById(R.id.tvFullName);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnSave = findViewById(R.id.btnSave);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);


        userService.getUserProfile().enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                Log.d("API_RAW", "Response body: " + response.body());
                Log.d("API_RAW", "Error body: " + response.errorBody());
                Log.d("token", "tokenManager code: " + tokenManager.get());
                if (response.isSuccessful() && response.body() != null) {
                    tvFullName.setText(response.body().getFullName());
                    tvPhone.setText(response.body().getPhone());
                    tvEmail.setText(response.body().getEmail());
                }
            }

            @Override
            public void onFailure(Call<UserDto> call, Throwable throwable) {
                Toast.makeText(ProfileActivity.this, "Network error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnEditProfile.setOnClickListener(v -> {
            // Set EditText visible and prefill
            etFullName.setText(tvFullName.getText().toString());
            etPhone.setText(tvPhone.getText().toString());
            etEmail.setText(tvEmail.getText().toString());

            etFullName.setVisibility(View.VISIBLE);
            etPhone.setVisibility(View.VISIBLE);
            etEmail.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.VISIBLE);

            // Hide TextViews and Edit button
            tvFullName.setVisibility(View.GONE);
            tvPhone.setVisibility(View.GONE);
            tvEmail.setVisibility(View.GONE);
            btnEditProfile.setVisibility(View.GONE);
        });

        btnSave.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            UserDto updatedUser = new UserDto();
            //set data null not update
            updatedUser.setId(1);
            updatedUser.setUsername("thinh");
            updatedUser.setRoleId(3);
            updatedUser.setRoleName("thinh");

            updatedUser.setFullName(fullName);
            updatedUser.setEmail(email);
            updatedUser.setPhone(phone);
            userService = RetrofitClient.getInstance(tokenManager).create(UserService.class);
            userService.updateUserProfile(updatedUser).enqueue(new Callback<UserDto>() {
                @Override
                public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                    Log.d("API_RAW", "Status code: " + response.code());
                    Log.d("API_RAW", "Error body: " + response.errorBody());
                    Log.d("API_RAW", "Body is null? " + response.body().getFullName() );
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();

                        // Update TextViews
                        tvFullName.setText(response.body().getFullName());
                        tvPhone.setText(response.body().getPhone());
                        tvEmail.setText(response.body().getEmail());

                        // Show TextViews
                        tvFullName.setVisibility(View.VISIBLE);
                        tvPhone.setVisibility(View.VISIBLE);
                        tvEmail.setVisibility(View.VISIBLE);
                        btnEditProfile.setVisibility(View.VISIBLE);

                        // Hide EditTexts
                        etFullName.setVisibility(View.GONE);
                        etPhone.setVisibility(View.GONE);
                        etEmail.setVisibility(View.GONE);
                        btnSave.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserDto> call, Throwable t) {
                    Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
