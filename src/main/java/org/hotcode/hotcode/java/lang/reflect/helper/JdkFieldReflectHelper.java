package org.hotcode.hotcode.java.lang.reflect.helper;

import org.hotcode.hotcode.reloader.CRMHelper;

import java.lang.reflect.Field;

/**
 * @author zhuyong 2013-7-3 12:33:26
 */
public class JdkFieldReflectHelper {

    public static boolean hasShadowClass(Object object, Field field) {
        Class<?> shadowClass = CRMHelper.getShadowClass(object.getClass());

        if (shadowClass == null) {
            return false;
        }

        return true;
    }
}
