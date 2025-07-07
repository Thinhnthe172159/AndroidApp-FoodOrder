package com.fu.thinh_nguyen.qrfoodorder.data.model;

import java.math.BigDecimal;

public class MenuSearchFilter {
    private String keyword;
    private Integer categoryId;
    private Boolean isAvailable;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    // Constructors
    public MenuSearchFilter() {}

    // Getters and Setters
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }

    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }
}
