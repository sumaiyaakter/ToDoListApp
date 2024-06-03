package todolistapp;

import java.io.Serializable;

public class Task implements Serializable {
    private String title;
    private String description;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return title;
    }
}
