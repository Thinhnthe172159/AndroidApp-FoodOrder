package com.fu.thinh_nguyen.qrfoodorder.data.model;

import java.math.BigDecimal;

public class UpdateMenuItemDto {
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean isAvailable;
    private Integer categoryId;

    // Constructors
    public UpdateMenuItemDto() {}

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
}
