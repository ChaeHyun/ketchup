package com.ketchup.model.task;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName="task")
public class Task {
    @NonNull
    @PrimaryKey
    private String uuid;

    private String title;
    private String description;
    private String writtenDate;
    private boolean completed;

    public Task(String uuid, String title) {
        this.uuid = uuid;
        this.title = title;
        completed = false;
    }

    @Ignore
    public Task(String uuid, String title, boolean completed) {
        this.uuid = uuid;
        this.title = title;
        this.completed = completed;
    }


    @NonNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NonNull String uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWrittenDate() {
        return writtenDate;
    }

    public void setWrittenDate(String writtenDate) {
        this.writtenDate = writtenDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

}
