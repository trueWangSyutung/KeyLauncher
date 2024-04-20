package cn.tw.sar.easylauncher.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.tw.sar.easylauncher.beam.DesktopIcon;

public class DesktopUtil {
    SharedPreferences sharedPref;

    public DesktopUtil(Context context) {
        sharedPref = context.getSharedPreferences("desktopIcons", Context.MODE_PRIVATE);
    }

    public Set<DesktopIcon> getDesktopIcons(int desktop) {
        String icons = sharedPref.getString("icons", "[]");
        //  Parse the JSON string and return the DesktopIcon
        Gson gson = new Gson();
        DesktopIcon[] desktopIcons = gson.fromJson(icons, DesktopIcon[].class);
        // 转为ArrayList
        ArrayList<DesktopIcon> desktopIcons1 = new ArrayList<DesktopIcon>();
        for (DesktopIcon desktopIcon : desktopIcons) {

            if (desktop==1 && desktopIcon.getShowDesktop()){
                desktopIcons1.add(desktopIcon);
            }
        }
        // 转为Set
        Set<DesktopIcon> desktopIcons2 = new HashSet<DesktopIcon>(desktopIcons1);
        return desktopIcons2;
    }

    public void addDesktopIcon(DesktopIcon desktopIcon) {
        //  Convert the DesktopIcon to a JSON string and save it
        Gson gson = new Gson();
        String icons = sharedPref.getString("icons", "[]");
        //  Parse the JSON string and return the DesktopIcon List
        DesktopIcon[] desktopIcons = gson.fromJson(icons, DesktopIcon[].class);
        // 转为ArrayList
        ArrayList<DesktopIcon> desktopIcons1 = new ArrayList<DesktopIcon>();
        for (DesktopIcon desktopIcon1 : desktopIcons) {
            desktopIcons1.add(desktopIcon1);
        }
        // 转为Set
        Set<DesktopIcon> desktopIcons2 = new HashSet<DesktopIcon>(desktopIcons1);

        //  Add the new DesktopIcon to the list
        desktopIcons2.add(desktopIcon);

        // 转为数组
        DesktopIcon[] desktopIcons3 = new DesktopIcon[desktopIcons2.size()];
        desktopIcons2.toArray(desktopIcons3);
        icons = gson.toJson(desktopIcons3);
        sharedPref.edit().putString("icons", icons).apply();
    }





}
