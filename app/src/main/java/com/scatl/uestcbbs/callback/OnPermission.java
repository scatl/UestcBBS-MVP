package com.scatl.uestcbbs.callback;

public interface OnPermission {
    void onGranted();  //同意授权
    void onRefusedWithNoMoreRequest();  //拒绝并且选中不再提示
    void onRefused();  //拒绝但没有选中不再提示
}
