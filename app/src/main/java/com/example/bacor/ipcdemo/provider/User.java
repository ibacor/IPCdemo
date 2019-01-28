package com.example.bacor.ipcdemo.provider;

public class User {
    private int userId;
    private String name;
    private int sex;

    public User(int userId, String name, int sex){
        this.userId = userId;
        this.name = name;
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "userId: " + userId + "  name: " + name + "  sex: " + sex;
    }
}
