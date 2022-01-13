package com.akzubarev.homedoctor.utils;

public class Enums {

    public enum Theme {
        DARK, LIGHT, SYSTEM;

        public static Theme convert(int i) {
            switch (i) {
                case 0:
                default:
                    return DARK;
                case 1:
                    return LIGHT;
                case 2:
                    return SYSTEM;
            }
        }

        public static int convert(Theme t) {
            return t.ordinal();
        }
    }
}
