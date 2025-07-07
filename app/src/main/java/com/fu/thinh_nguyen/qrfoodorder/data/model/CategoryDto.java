package com.fu.thinh_nguyen.qrfoodorder.data.model;

public class CategoryDto {
    private int id;
    private String name;
    private int menuItemCount;

    // Constructors
    public CategoryDto() {}

    public CategoryDto(int id, String name, int menuItemCount) {
        this.id = id;
        this.name = name;
        this.menuItemCount = menuItemCount;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMenuItemCount() {
        return menuItemCount;
    }

    public void setMenuItemCount(int menuItemCount) {
        this.menuItemCount = menuItemCount;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "CategoryDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", menuItemCount=" + menuItemCount +
                '}';
    }

    // equals and hashCode for comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryDto that = (CategoryDto) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
