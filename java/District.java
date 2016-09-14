package com.bloodbank.kanbankasi;

/**
 * Created by Alisan on 11.4.2016.
 */
public class District {
    private String name;
    private int code;
    private int cityCode;


    public District(String name, int cityCode, int code) {
        this.name = name;
        this.cityCode = cityCode;
        this.code = code;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
