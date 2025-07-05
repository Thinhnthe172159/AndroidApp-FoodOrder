package com.fu.thinh_nguyen.qrfoodorder.data.api;

import com.fu.thinh_nguyen.qrfoodorder.data.model.AuthResponseDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.CurrentUserDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.LoginDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.RegisterDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.UserDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/Auth/login")
    Call<AuthResponseDto> login(@Body LoginDto dto);

    @POST("api/Auth/register")
    Call<UserDto> register(@Body RegisterDto dto);

    @GET("/apiAuth/me")
    Call<CurrentUserDto> getCurrentUser();
}
