package com.ketchup.model.task;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity(tableName="task")
public class Task {
    @NonNull
    @PrimaryKey
    private String uuid;

    private String title;
    private String description;
    private Date writtenDate;
    private boolean completed;
    private int colorLabel;
    private Date dueDate;

    public static final int DEFAULT_COLOR = Color.rgb(215, 204, 200);

    public Task(String uuid) {
        this.uuid = uuid;
        this.title= "";
        this.completed = false;
        this.colorLabel = DEFAULT_COLOR;
        this.dueDate = null;
    }

    @Ignore
    public Task(String uuid, String title) {
        this.uuid = uuid;
        this.title = title;
        completed = false;
        this.colorLabel = DEFAULT_COLOR;
        this.dueDate = null;
    }

    @Ignore
    public Task(String uuid, String title, boolean completed) {
        this.uuid = uuid;
        this.title = title;
        this.completed = completed;
        this.colorLabel = DEFAULT_COLOR;
        this.dueDate = null;
    }

    @Ignore
    public Task(String uuid, String title, boolean completed, int colorLabel) {
        this.uuid = uuid;
        this.title = title;
        this.completed = completed;
        this.colorLabel = colorLabel;
        this.dueDate = null;
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

    public Date getWrittenDate() {
        return writtenDate;
    }

    public void setWrittenDate(Date writtenDate) {
        this.writtenDate = writtenDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getColorLabel() {
        return colorLabel;
    }

    public void setColorLabel(int colorLabel) {
        this.colorLabel = colorLabel;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
}
