package com.fu.thinh_nguyen.qrfoodorder.data.api;

import com.fu.thinh_nguyen.qrfoodorder.data.model.CategoryDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.MenuItemDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.MenuSearchFilter;

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

public interface MenuItemService {

    // Menu endpoints
    @GET("api/Menu/items")
    Call<List<MenuItemDto>> getMenuItems();

    @GET("api/Menu/items/{id}")
    Call<MenuItemDto> getMenuItemById(@Path("id") int id);

    @POST("api/Menu/items/search")
    Call<List<MenuItemDto>> searchMenuItems(@Body MenuSearchFilter filter);

    // ApiService.java (phần Menu endpoints)
    @GET("api/Menu/items/search")
    Call<List<MenuItemDto>> searchMenuItemsByQuery(
            @Query("keyword") String keyword,
            @Query("categoryId") Integer categoryId,
            @Query("isAvailable") Boolean isAvailable,
            @Query("minPrice") Double minPrice,
            @Query("maxPrice") Double maxPrice
    );

    @Multipart
    @POST("api/Menu/items")
    Call<MenuItemDto> createMenuItem(
            @Part("Name") RequestBody name,
            @Part("Description") RequestBody description,
            @Part("Price") RequestBody price,
            @Part("CategoryId") RequestBody categoryId,
            @Part("IsAvailable") RequestBody isAvailable,
            @Part MultipartBody.Part imageFile
    );

    @Multipart
    @PUT("api/Menu/items/{id}")
    Call<MenuItemDto> updateMenuItem(
            @Path("id") int id,
            @Part("Name") RequestBody name,
            @Part("Description") RequestBody description,
            @Part("Price") RequestBody price,
            @Part("CategoryId") RequestBody categoryId,
            @Part("IsAvailable") RequestBody isAvailable,
            @Part MultipartBody.Part imageFile
    );

    @DELETE("api/Menu/items/{id}")
    Call<Void> deleteMenuItem(@Path("id") int id);

    @GET("api/Menu/categories")
    Call<List<CategoryDto>> getCategories();

}
