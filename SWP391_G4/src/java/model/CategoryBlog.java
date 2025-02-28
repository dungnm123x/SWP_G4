/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author admin
 */
public class CategoryBlog {
     private int categoryBlogId;
    private String categoryBlogName;
    private int status;

    public CategoryBlog() {
    }

    public CategoryBlog(int categoryBlogId, String categoryBlogName, int status) {
        this.categoryBlogId = categoryBlogId;
        this.categoryBlogName = categoryBlogName;
        this.status = status;
    }

    public int getCategoryBlogId() {
        return categoryBlogId;
    }

    public void setCategoryBlogId(int categoryBlogId) {
        this.categoryBlogId = categoryBlogId;
    }

    public String getCategoryBlogName() {
        return categoryBlogName;
    }

    public void setCategoryBlogName(String categoryBlogName) {
        this.categoryBlogName = categoryBlogName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    
}
