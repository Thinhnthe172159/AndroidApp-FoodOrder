package com.fu.thinh_nguyen.qrfoodorder.ui.customer;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.model.CategoryDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.MenuItemDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.MenuSearchFilter;
import com.fu.thinh_nguyen.qrfoodorder.data.prefs.TokenManager;
import com.fu.thinh_nguyen.qrfoodorder.data.repository.MenuRepository;
import com.fu.thinh_nguyen.qrfoodorder.ui.adapter.CategoryAdapter;
import com.fu.thinh_nguyen.qrfoodorder.ui.adapter.MenuItemAdapter;
import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerMainActivity extends AppCompatActivity {
    private MenuRepository menuRepository;
    private TokenManager tokenManager;

    // UI Components
    private RecyclerView menuItemsRecyclerView;
    private RecyclerView categoriesRecyclerView;
    private ProgressBar progressBar;
    private LinearLayout emptyStateLayout;
    private TextInputEditText searchEditText;

    // Adapters
    private MenuItemAdapter menuItemAdapter;
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_main);

        tokenManager = new TokenManager(this);
        menuRepository = new MenuRepository(tokenManager);

        initViews();
        setupRecyclerViews();
        setupSearchListener();

        // Load data
        loadMenuItems();
        loadCategories();
    }

    private void initViews() {
        menuItemsRecyclerView = findViewById(R.id.menuItemsRecyclerView);
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        searchEditText = findViewById(R.id.searchEditText);
    }

    private void setupRecyclerViews() {
        // Setup menu items RecyclerView
        menuItemAdapter = new MenuItemAdapter(this::onMenuItemClick);
        menuItemsRecyclerView.setAdapter(menuItemAdapter);

        // Setup categories RecyclerView
        categoryAdapter = new CategoryAdapter(this::onCategoryClick);
        categoriesRecyclerView.setAdapter(categoryAdapter);
    }

    private void onMenuItemClick(MenuItemDto menuItem) {
        Toast.makeText(this, "Selected: " + menuItem.getName(), Toast.LENGTH_SHORT).show();
    }

    private void setupSearchListener() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                searchMenuItems(s.toString());
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        menuItemsRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmptyState(boolean show) {
        emptyStateLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        menuItemsRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void loadMenuItems() {
        showLoading(true);
        menuRepository.getMenuItems(new Callback<List<MenuItemDto>>() {
            @Override
            public void onResponse(Call<List<MenuItemDto>> call, Response<List<MenuItemDto>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<MenuItemDto> menuItems = response.body();
                    menuItemAdapter.updateItems(menuItems);
                    showEmptyState(menuItems.isEmpty());
                    Log.d("MenuItems", "Loaded " + menuItems.size() + " items");
                } else {
                    showEmptyState(true);
                    Toast.makeText(CustomerMainActivity.this, "Failed to load menu items", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MenuItemDto>> call, Throwable t) {
                showLoading(false);
                showEmptyState(true);
                Log.e("MenuItems", "Error: " + t.getMessage());
                Toast.makeText(CustomerMainActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCategories() {
        menuRepository.getCategories(new Callback<List<CategoryDto>>() {
            @Override
            public void onResponse(Call<List<CategoryDto>> call, Response<List<CategoryDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CategoryDto> categories = response.body();
                    // Cập nhật adapter cho categories
                    if (categoryAdapter != null) {
                        categoryAdapter.updateCategories(categories);
                    }
                    Log.d("Categories", "Loaded " + categories.size() + " categories");

                    // Log chi tiết từng category
                    for (CategoryDto category : categories) {
                        Log.d("Category", "ID: " + category.getId() +
                                ", Name: " + category.getName() +
                                ", Items: " + category.getMenuItemCount());
                    }
                } else {
                    Log.e("Categories", "Failed to load categories");
                }
            }

            @Override
            public void onFailure(Call<List<CategoryDto>> call, Throwable t) {
                Log.e("Categories", "Error: " + t.getMessage());
            }
        });
    }


    private void searchMenuItems(String keyword) {
        if (keyword.trim().isEmpty()) {
            loadMenuItems();
            return;
        }

        showLoading(true);
        MenuSearchFilter filter = new MenuSearchFilter();
        filter.setKeyword(keyword);
        filter.setIsAvailable(true);

        menuRepository.searchMenuItems(filter, new Callback<List<MenuItemDto>>() {
            @Override
            public void onResponse(Call<List<MenuItemDto>> call, Response<List<MenuItemDto>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<MenuItemDto> searchResults = response.body();
                    menuItemAdapter.updateItems(searchResults);
                    showEmptyState(searchResults.isEmpty());
                }
            }

            @Override
            public void onFailure(Call<List<MenuItemDto>> call, Throwable t) {
                showLoading(false);
                Log.e("SearchError", "Error: " + t.getMessage());
            }
        });
    }

    private void onCategoryClick(CategoryDto category) {
        // Filter by category
        MenuSearchFilter filter = new MenuSearchFilter();
        filter.setCategoryId(category.getId());
        filter.setIsAvailable(true);

        showLoading(true);
        menuRepository.searchMenuItems(filter, new Callback<List<MenuItemDto>>() {
            @Override
            public void onResponse(Call<List<MenuItemDto>> call, Response<List<MenuItemDto>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    menuItemAdapter.updateItems(response.body());
                    showEmptyState(response.body().isEmpty());
                }
            }

            @Override
            public void onFailure(Call<List<MenuItemDto>> call, Throwable t) {
                showLoading(false);
                Log.e("CategoryFilter", "Error: " + t.getMessage());
            }
        });
    }
}
