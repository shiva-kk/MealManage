package com.mealManage.model;

public class Placeholder {

    private int id;
    private String title;
    private String placeholder;
    private boolean isPublished;
    private String content;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getPlaceholder() { return placeholder; }
    public void setPlaceholder(String placeholder) { this.placeholder = placeholder; }
    public boolean isPublished() { return isPublished; }
    public void setPublished(boolean published) { isPublished = published; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
