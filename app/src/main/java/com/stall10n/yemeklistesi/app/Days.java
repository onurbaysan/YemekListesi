package com.stall10n.yemeklistesi.app;

/**
 * Created by onur on 31.5.2014.
 */
public enum Days {

    Pazar(7),
    Pazartesi(1),
    Sali(2),
    Carsamba(3),
    Persembe(4),
    Cuma(5),
    Cumartesi(6);

    private final int value;

    private Days(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}