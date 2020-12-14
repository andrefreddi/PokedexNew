package com.example.apppokedex;

import java.util.List;

public class FeedEntry {

    private String num;
    private String name;
    private String img;
    private String height;
    private String weight;
    private List<String> type;
  //  private List<String> type;


    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "FeedEntry{" +
                "name='" + name + '\'' +
                ", num='" + num + '\'' +
                ", height='" + height + '\'' +
                ", weiht='" + weight + '\'' +
                ", imgURL='" + img + '\''
                 + ", type=" + type +'}';
    }
}