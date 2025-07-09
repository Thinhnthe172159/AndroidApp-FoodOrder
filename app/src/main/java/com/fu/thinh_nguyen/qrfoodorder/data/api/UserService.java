package com.fu.thinh_nguyen.qrfoodorder.data.api;

import com.fu.thinh_nguyen.qrfoodorder.data.model.UserDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {

    // GET: api/users
    @GET("api/User")
    Call<List<UserDto>> getAllUsers();

    // GET: /userProfile (authorized)
    @GET("userProfile")
    Call<UserDto> getUserProfile();

    // PUT: api/User/updateProfile (authorized)
    @PUT("api/User/updateProfile")
    Call<UserDto> updateUserProfile(
            @Body UserDto userDto
    );

    // DELETE: api/User/{id}
    @DELETE("api/User/{id}")
    Call<Void> deleteUser(@Path("id") int id);

}