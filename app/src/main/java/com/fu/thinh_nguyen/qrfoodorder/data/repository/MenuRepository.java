package com.fu.thinh_nguyen.qrfoodorder.data.repository;

import com.fu.thinh_nguyen.qrfoodorder.data.api.MenuItemService;
import com.fu.thinh_nguyen.qrfoodorder.data.model.CategoryDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.MenuItemDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.MenuSearchFilter;
import com.fu.thinh_nguyen.qrfoodorder.data.network.RetrofitClient;
import com.fu.thinh_nguyen.qrfoodorder.data.prefs.TokenManager;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class MenuRepository {
    private final MenuItemService menuItemService;

    public MenuRepository(TokenManager tokenManager) {
        this.menuItemService = RetrofitClient.getInstance(tokenManager).create(MenuItemService.class);
    }

    public void getMenuItems(Callback<List<MenuItemDto>> callback) {
        Call<List<MenuItemDto>> call = menuItemService.getMenuItems();
        call.enqueue(callback);
    }

    public void getMenuItemById(int id, Callback<MenuItemDto> callback) {
        Call<MenuItemDto> call = menuItemService.getMenuItemById(id);
        call.enqueue(callback);
    }

    public void searchMenuItems(MenuSearchFilter filter, Callback<List<MenuItemDto>> callback) {
        Call<List<MenuItemDto>> call = menuItemService.searchMenuItems(filter);
        call.enqueue(callback);
    }

    public void searchMenuItemsByQuery(String keyword, Integer categoryId, Boolean isAvailable,
                                       BigDecimal minPrice, BigDecimal maxPrice, Callback<List<MenuItemDto>> callback) {
        Call<List<MenuItemDto>> call = menuItemService.searchMenuItemsByQuery(
                keyword, categoryId, isAvailable,
                minPrice != null ? minPrice.doubleValue() : null,
                maxPrice != null ? maxPrice.doubleValue() : null
        );
        call.enqueue(callback);
    }

    public void createMenuItem(String name, String description, BigDecimal price, Integer categoryId,
                               Boolean isAvailable, File imageFile, Callback<MenuItemDto> callback) {
        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody descBody = description != null ?
                RequestBody.create(MediaType.parse("text/plain"), description) : null;
        RequestBody priceBody = RequestBody.create(MediaType.parse("text/plain"), price.toString());
        RequestBody categoryBody = categoryId != null ?
                RequestBody.create(MediaType.parse("text/plain"), categoryId.toString()) : null;
        RequestBody availableBody = isAvailable != null ?
                RequestBody.create(MediaType.parse("text/plain"), isAvailable.toString()) : null;

        MultipartBody.Part imagePart = null;
        if (imageFile != null) {
            RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
            imagePart = MultipartBody.Part.createFormData("ImageFile", imageFile.getName(), imageBody);
        }

        Call<MenuItemDto> call = menuItemService.createMenuItem(
                nameBody, descBody, priceBody, categoryBody, availableBody, imagePart
        );
        call.enqueue(callback);
    }

    public void updateMenuItem(int id, String name, String description, BigDecimal price, Integer categoryId,
                               Boolean isAvailable, File imageFile, Callback<MenuItemDto> callback) {
        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody descBody = description != null ?
                RequestBody.create(MediaType.parse("text/plain"), description) : null;
        RequestBody priceBody = RequestBody.create(MediaType.parse("text/plain"), price.toString());
        RequestBody categoryBody = categoryId != null ?
                RequestBody.create(MediaType.parse("text/plain"), categoryId.toString()) : null;
        RequestBody availableBody = isAvailable != null ?
                RequestBody.create(MediaType.parse("text/plain"), isAvailable.toString()) : null;

        MultipartBody.Part imagePart = null;
        if (imageFile != null) {
            RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
            imagePart = MultipartBody.Part.createFormData("ImageFile", imageFile.getName(), imageBody);
        }

        Call<MenuItemDto> call = menuItemService.updateMenuItem(
                id, nameBody, descBody, priceBody, categoryBody, availableBody, imagePart
        );
        call.enqueue(callback);
    }

    public void deleteMenuItem(int id, Callback<Void> callback) {
        Call<Void> call = menuItemService.deleteMenuItem(id);
        call.enqueue(callback);
    }

    public void getCategories(Callback<List<CategoryDto>> callback) {
        Call<List<CategoryDto>> call = menuItemService.getCategories();
        call.enqueue(callback);
    }

}
