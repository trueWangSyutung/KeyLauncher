package cn.tw.sar.easylauncher.beam

import android.content.pm.ResolveInfo

data  class DesktopIcon(
    var type : Int,
    // 0: app, 1: QuickAction 2: Folder
    var packageName : String,
    var showDesktop : Boolean,
    var showDock : Boolean,
)