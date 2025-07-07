package com.fu.thinh_nguyen.qrfoodorder.data.api;

import com.fu.thinh_nguyen.qrfoodorder.data.model.AuthResponseDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.CategoryDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.CurrentUserDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.LoginDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.MenuItemDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.MenuSearchFilter;
import com.fu.thinh_nguyen.qrfoodorder.data.model.RegisterDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.UserDto;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("api/Auth/login")
    Call<AuthResponseDto> login(@Body LoginDto dto);

    @POST("api/Auth/register")
    Call<UserDto> register(@Body RegisterDto dto);

    @GET("/apiAuth/me")
    Call<CurrentUserDto> getCurrentUser();

}
