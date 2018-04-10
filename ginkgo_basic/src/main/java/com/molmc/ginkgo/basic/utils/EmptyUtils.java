package com.molmc.ginkgo.basic.utils;

import android.text.TextUtils;

import java.util.List;

public class EmptyUtils {

    public static boolean check(Object obj) {
        return obj == null;
    }

    public static boolean check(List list) {
        return list == null || list.isEmpty();
    }

    public static boolean check(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean check(CharSequence str) {
        return TextUtils.isEmpty(str);
    }
}
