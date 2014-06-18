package com.stall10n.yemeklistesi.app;

import java.io.Serializable;

/**
 * Created by onur on 31.5.2014.
 */
public class DailyMenu implements Serializable
{
    private static final long serialVersionUID = 46547646;
    private String day;
    private String standard_menu;
    private String diet_menu;

    private String hash;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStandard_menu() {
        return standard_menu;
    }

    public void setStandard_menu(String standart_menu) {
        this.standard_menu = standart_menu;
    }

    public String getDiet_menu() {
        return diet_menu;
    }

    public void setDiet_menu(String diet_menu) {
        this.diet_menu = diet_menu;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
