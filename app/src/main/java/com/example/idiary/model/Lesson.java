package com.example.idiary.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Lesson implements Comparable<Lesson>, Serializable {
    private long id;
    private LocalDate date;
    private String name;
    private int num; //порядковый номер урока в дне
    private String homeWork;
    private boolean isDone;

    public Lesson(long id, LocalDate date, String name, int num, String homeWork, boolean isDone) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.num = num;
        this.homeWork = homeWork;
        this.isDone = isDone;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getHomeWork() {
        return homeWork;
    }

    public void setHomeWork(String homeWork) {
        this.homeWork = homeWork;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }


    @Override
    public int compareTo(Lesson o) {
        if (num > o.num) return 1;
        if (num < o.num) return -1;
        return 0;
    }
}
