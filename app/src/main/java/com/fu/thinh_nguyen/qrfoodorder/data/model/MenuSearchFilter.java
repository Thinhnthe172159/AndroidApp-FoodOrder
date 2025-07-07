package com.fu.thinh_nguyen.qrfoodorder.data.model;

import java.math.BigDecimal;

public class MenuSearchFilter {
    private String Keyword;
    private Integer CategoryId;
    private Boolean IsAvailable;
    private BigDecimal MinPrice;
    private BigDecimal MaxPrice;

    // Constructors
    public MenuSearchFilter() {}

    // Getters and Setters
    public String getKeyword() { return Keyword; }
    public void setKeyword(String keyword) { this.Keyword = keyword; }

    public Integer getCategoryId() { return CategoryId; }
    public void setCategoryId(Integer categoryId) { this.CategoryId = categoryId; }

    public Boolean getIsAvailable() { return IsAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.IsAvailable = isAvailable; }

    public BigDecimal getMinPrice() { return MinPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.MinPrice = minPrice; }

    public BigDecimal getMaxPrice() { return MaxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.MaxPrice = maxPrice; }
}
