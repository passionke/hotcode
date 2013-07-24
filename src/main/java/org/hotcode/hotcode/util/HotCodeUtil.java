package org.hotcode.hotcode.util;

import org.objectweb.asm.Type;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * @author khotyn 13-7-2 PM7:45
 */
public class HotCodeUtil {

    private static final char FIELD_DELIMITER = '-';

    public static String getFieldKey(int access, String name, String desc) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name) && !Strings.isNullOrEmpty(desc),
                                    "Name and desc can not be null.");
        return access + FIELD_DELIMITER + name + FIELD_DELIMITER + desc;
    }

    public static String getMainClassNameFromAssitClassName(String assistClassName) {
        if (assistClassName == null) {
            return null;
        }

        int index = assistClassName.indexOf("$$$");
        return assistClassName.substring(0, index);
    }

    public static Type getBoxedType(final Type type) {
        switch (type.getSort()) {
            case Type.BYTE:
                return Type.getType(Byte.class);
            case Type.BOOLEAN:
                return Type.getType(Boolean.class);
            case Type.SHORT:
                return Type.getType(Short.class);
            case Type.CHAR:
                return Type.getType(Character.class);
            case Type.INT:
                return Type.getType(Integer.class);
            case Type.FLOAT:
                return Type.getType(Float.class);
            case Type.LONG:
                return Type.getType(Long.class);
            case Type.DOUBLE:
                return Type.getType(Double.class);
        }
        return type;
    }
}
