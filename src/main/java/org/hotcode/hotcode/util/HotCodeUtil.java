package org.hotcode.hotcode.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * @author khotyn 13-7-2 PM7:45
 */
public class HotCodeUtil {

    private static final char FIELD_DELIMITER = '-';

    public static String getFieldKey(String name, String desc) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name) && !Strings.isNullOrEmpty(desc),
                                    "Name and desc can not be null.");
        return name + FIELD_DELIMITER + desc;
    }

    public static String getMainClassNameFromAssitClassName(String assistClassName) {
        if (assistClassName == null) {
            return null;
        }

        int index = assistClassName.indexOf("$$$");
        return assistClassName.substring(0, index);
    }
}
