package com.fu.thinh_nguyen.qrfoodorder.ui.base;

import android.os.Bundle;
import android.util.Log;
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
    }
}
