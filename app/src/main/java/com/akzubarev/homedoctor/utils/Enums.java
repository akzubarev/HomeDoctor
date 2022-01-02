package com.akzubarev.homedoctor.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Enums {

    public enum TimerState {
        CONTINIOUS, DISCRETE, INVISIBlE;

        public static TimerState convert(int i) {
            switch (i) {
                case 0:
                default:
                    return CONTINIOUS;
                case 1:
                    return DISCRETE;
                case 2:
                    return INVISIBlE;
            }
        }

        public static int convert(TimerState ts) {
            return ts.ordinal();
        }
    }

    public enum ButtonsPlace {
        RIGHT, LEFT;

        public static ButtonsPlace convert(int i) {
            switch (i) {
                case 0:
                default:
                    return RIGHT;
                case 1:
                    return LEFT;
            }
        }

        public static int convert(ButtonsPlace bp) {
            return bp.ordinal();
        }
    }

    public enum LayoutState {
        _789, _123;

        public static LayoutState convert(int i) {
            switch (i) {
                case 0:
                default:
                    return _789;
                case 1:
                    return _123;
            }
        }

        public static int convert(LayoutState ls) {
            return ls.ordinal();
        }
    }

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

    public enum twodates {
        equal,
        subsequent,
        unrelated
    }

    public static twodates isSubsequent(String prevdate, String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(sdf.parse(prevdate));
            c2.setTime(sdf.parse(date));
            if (c1.equals(c2))
                return twodates.equal;
            c1.add(Calendar.DATE, 1);
            if (c1.equals(c2))
                return twodates.subsequent;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return twodates.unrelated;
    }

}
