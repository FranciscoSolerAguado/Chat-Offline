package org.fran.chatoffline.utils;

import java.util.regex.Pattern;

public class ReggexUtil {
    public static final Pattern GMAIL_REGEX = Pattern.compile("^[a-zA-Z0-9._%+-]+@gmail\\.com$");
    public static final Pattern TELEFONO_REGEX = Pattern.compile("^[6789]\\d{8}$");


}
