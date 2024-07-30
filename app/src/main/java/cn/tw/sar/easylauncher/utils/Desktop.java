package cn.tw.sar.easylauncher.utils;
//桌面相关操作

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import cn.tw.sar.easylauncher.MainActivity;

public class Desktop {

    private Context context;

    public Desktop(Context context) {
        this.context = context;
    }

    /**
     * 清除默认桌面（采用先设置一个空的桌面为默认然后在将该空桌面禁用的方式来实现）
     *
     */
    @SuppressLint("WrongConstant")
    public void clearDefaultLauncher() {
        PackageManager pm = context.getPackageManager();
        String pn = context.getPackageName();
        String hn = MainActivity.class.getName();
        ComponentName mhCN = new ComponentName(pn, hn);
        Intent homeIntent = new Intent("android.intent.action.MAIN");
        homeIntent.addCategory("android.intent.category.HOME");
        homeIntent.addCategory("android.intent.category.DEFAULT");
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pm.setComponentEnabledSetting(mhCN, 1, 1);
        context.startActivity(homeIntent);
        pm.setComponentEnabledSetting(mhCN, 0, 1);
    }

    //弹出选择默认桌面
    public void SetDefaultLauncher(){
        try {
            Intent  paramIntent = new Intent("android.intent.action.MAIN");
            paramIntent.setComponent(new ComponentName("android", "com.android.internal.app.ResolverActivity"));
            paramIntent.addCategory("android.intent.category.DEFAULT");
            paramIntent.addCategory("android.intent.category.HOME");
            context.startActivity(paramIntent);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            Intent  paramIntent1 = new Intent("android.intent.action.MAIN");
            paramIntent1.setComponent(new ComponentName("com.huawei.android.internal.app", "com.huawei.android.internal.app.HwResolverActivity"));
            paramIntent1.addCategory("android.intent.category.DEFAULT");
            paramIntent1.addCategory("android.intent.category.HOME");
            context.startActivity(paramIntent1);

        }catch (Exception e){
            e.printStackTrace();
            startHuaweiSettingActOfDefLauncher();
        }


    }

    //打开华为设置页面，让用户选择默认桌面
    public void startHuaweiSettingActOfDefLauncher() {
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction(Intent.ACTION_MAIN);//"android.intent.action.MAIN"
        localIntentFilter.addCategory(Intent.CATEGORY_HOME);//"android.intent.category.HOME"
        Intent localIntent3 = new Intent(localIntentFilter.getAction(0));
        localIntent3.addCategory(localIntentFilter.getCategory(0));
        Intent localIntent4 = new Intent();
        localIntent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent4.setClassName("com.android.settings", "com.android.settings.Settings$PreferredSettingsActivity");
        localIntent4.putExtra("preferred_app_package_name", context.getPackageName());
        localIntent4.putExtra("preferred_app_class_name", context.getClass().getName());
        localIntent4.putExtra("is_user_confirmed", true);
        localIntent4.putExtra("preferred_app_intent", localIntent3);
        localIntent4.putExtra("preferred_app_intent_filter", localIntentFilter);
        localIntent4.putExtra("preferred_app_label", "默认桌面设置");
        context.startActivity(localIntent4);
    }

    //获取当前默认桌面，如果不是本App则弹出设置默认桌面对话框
    public void getDefaultHome() {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        final ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
        if (res.activityInfo == null) {
            //获取失败
        } else if (res.activityInfo.packageName.equals("android")) {
            // No default selected
            clearDefaultLauncher();
            SetDefaultLauncher();
        } else {
            if (!res.activityInfo.packageName.equals("net.xiaomy.dxsjb.client")){
                clearDefaultLauncher();
                SetDefaultLauncher();
            }

        }
    }
}