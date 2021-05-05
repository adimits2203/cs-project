package com.my.project.util;

import org.apache.commons.lang3.StringUtils;

public class Utils {

    public static boolean isWindow(String notBlank, String notBlank2) {
        return StringUtils.isNotBlank(notBlank) && StringUtils.isNotBlank(notBlank2);
    }
}
