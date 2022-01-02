package com.akzubarev.homedoctor.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;


public class DataReader {
    public static final String COMPLEXITY_SETTINGS = "ComplexitySettings";
    public static final String ROUND_TIME_SETTINGS = "RoundTimeSettings";

    public static final String ROUND_TIME = "RoundTime";
    public static final String DISAP_ROUND_TIME = "DisapRoundTime";
    public static final String TIMER_STATE = "TimerState";
    public static final String BUTTONS_PLACE = "ButtonsPlace";
    public static final String LAYOUT_STATE = "LayoutState";
    public static final String GOAL = "Goal";
    public static final String THEME = "Theme";
    public static final String REMINDER = "Reminder";
    public static final String REMINDER_TIME = "ReminderTime";
    public static final String QUEUE = "QueueEnabled";

    private static final HashMap<String, Integer> defaultInts = new HashMap<>();
    private static final HashMap<String, String> defaultStrings = new HashMap<>();
    private static final HashMap<String, Boolean> defaultBooleans = new HashMap<>();


    static {
        //ints
        defaultInts.put(ROUND_TIME, 1);
        defaultInts.put(DISAP_ROUND_TIME, -1);
        defaultInts.put(GOAL, 5);

        //enums
        defaultInts.put(TIMER_STATE, 1); // continuous
        defaultInts.put(BUTTONS_PLACE, 0); // right
        defaultInts.put(LAYOUT_STATE, 0); // 789
        defaultInts.put(THEME, -1); // dark

        //booleans
        defaultBooleans.put(REMINDER, false);
        defaultBooleans.put(QUEUE, false);

        //strings
        defaultStrings.put(REMINDER_TIME, "18:00");
    }

    public static int[] convertIntegers(ArrayList<Integer> integers) {
        int[] ret = new int[integers.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = integers.get(i);
        }
        return ret;
    }

    static private int[] StrToIntArr(String savedString) {
        StringTokenizer st = new StringTokenizer(savedString, ",");
        ArrayList<Integer> savedList = new ArrayList<>();
        int i = 0;
        while (st.hasMoreTokens()) {
            savedList.add(Integer.parseInt(st.nextToken()));
        }
        return convertIntegers(savedList);
    }

    static public int[][] readAllowedTasks(Context p_context) {
        SharedPreferences prefs = p_context.getSharedPreferences(COMPLEXITY_SETTINGS, Context.MODE_PRIVATE);
        String savedString = "";
        int[] Add;
        if (prefs.contains("Add")) {
            savedString = prefs.getString("Add", null);
            Add = StrToIntArr(savedString);
        } else {
            Add = new int[0];
        }
        int[] Sub;
        if (prefs.contains("Sub")) {
            savedString = prefs.getString("Sub", null);
            Sub = StrToIntArr(savedString);
        } else {
            Sub = new int[0];
        }
        int[] Mul;
        if (prefs.contains("Mul")) {
            savedString = prefs.getString("Mul", null);
            Mul = StrToIntArr(savedString);
        } else {
            Mul = new int[0];
        }
        int[] Div;
        if (prefs.contains("Div")) {
            savedString = prefs.getString("Div", null);
            Div = StrToIntArr(savedString);
        } else {
            Div = new int[0];
        }


        return new int[][]{Add, Sub, Mul, Div};
    }

    static public boolean checkComplexity(int p_action, int p_complexity, Context p_context) {
        int[][] l_allowedTasks = DataReader.readAllowedTasks(p_context);
        for (int i = 0; i < l_allowedTasks[p_action].length; ++i)
            if (l_allowedTasks[p_action][i] == p_complexity)
                return true;
        return false;
    }

    public static void SaveInt(int value, String name, Context p_context) {
        SharedPreferences.Editor editor = p_context.getSharedPreferences(ROUND_TIME_SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putInt(name, value);
        editor.apply();
    }

    static public int GetInt(String name, Context p_context) {
        if (!defaultInts.containsKey(name))
            throw new IllegalArgumentException();
        else {
            SharedPreferences prefs = p_context.getSharedPreferences(ROUND_TIME_SETTINGS, Context.MODE_PRIVATE);
            return prefs.getInt(name, defaultInts.get(name));
        }
    }

    public static void SaveString(String value, String name, Context p_context) {
        SharedPreferences.Editor editor = p_context.getSharedPreferences(ROUND_TIME_SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putString(name, value);
        editor.apply();
    }

    static public String GetString(String name, Context p_context) {
        if (!defaultStrings.containsKey(name))
            throw new IllegalArgumentException();
        else {
            SharedPreferences prefs = p_context.getSharedPreferences(ROUND_TIME_SETTINGS, Context.MODE_PRIVATE);
            return prefs.getString(name, defaultStrings.get(name));
        }
    }

    public static void SaveBoolean(Boolean value, String name, Context p_context) {
        SharedPreferences.Editor editor = p_context.getSharedPreferences(ROUND_TIME_SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putBoolean(name, value);
        editor.apply();
    }

    static public Boolean GetBoolean(String name, Context p_context) {
        if (!defaultBooleans.containsKey(name))
            throw new IllegalArgumentException();
        else {
            SharedPreferences prefs = p_context.getSharedPreferences(ROUND_TIME_SETTINGS, Context.MODE_PRIVATE);
            return prefs.getBoolean(name, defaultBooleans.get(name));
        }
    }

    public static void SaveQueue(String json, Context p_context) {
        SharedPreferences.Editor editor = p_context.getSharedPreferences(ROUND_TIME_SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putString("Queue", json);
        editor.apply();
    }

    static public String GetQueue(Context p_context) {
        SharedPreferences prefs = p_context.getSharedPreferences(ROUND_TIME_SETTINGS, Context.MODE_PRIVATE);
        return prefs.getString("Queue", "");
    }

    public static void SaveStat(String json, Context p_context) {
        SharedPreferences.Editor editor = p_context.getSharedPreferences(ROUND_TIME_SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putString("Stat", json);
        editor.apply();
    }

    static public String GetStat(Context p_context) {
        SharedPreferences prefs = p_context.getSharedPreferences(ROUND_TIME_SETTINGS, Context.MODE_PRIVATE);
        return prefs.getString("Stat", "");
    }

}
