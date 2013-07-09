package org.hotcode.hotcode.jdk.reflect;

import org.hotcode.hotcode.constant.HotCodeConstant;
import org.hotcode.hotcode.reloader.CRMHelper;
import org.hotcode.hotcode.reloader.ClassReloader;
import org.hotcode.hotcode.reloader.ClassReloaderManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Field Reflect Helper
 * 
 * @author zhuyong 2013-7-3 12:33:26
 */
public class JdkReflectHelper {

    public static Field[] filterHotCodeFields(Field[] fields) {
        List<Field> rets = new ArrayList<Field>();
        for (Field f : fields) {
            if (f.getName().equals(HotCodeConstant.HOTCODE_ADDED_FIELDS)
                || f.getName().equals(HotCodeConstant.HOTCODE_STATIC_FIELDS)) {
                continue;
            }
            rets.add(f);
        }
        return rets.toArray(new Field[] {});
    }

    public static Field[] privateGetDeclaredFields0(Class<?> clazz, boolean publicOnly) {
        ClassReloaderManager classReloaderManager = CRMHelper.getClassReloaderManager(clazz.getClassLoader());
        ClassReloader classReloader = CRMHelper.getClassReloader(clazz.getClassLoader(), clazz.getName());
        Class<?> shadowClass = classReloaderManager.getShadowClass(clazz.getName());
        Field[] fields = null;
        if (publicOnly) {
            fields = shadowClass.getFields();
        } else {
            fields = shadowClass.getDeclaredFields();
        }
        try {
            List<Field> holderFields = new ArrayList<Field>();

            Field clazzField = Field.class.getDeclaredField("clazz");
            clazzField.setAccessible(true);

            for (Field f : fields) {
                clazzField.set(f, clazz);
                holderFields.add(f);
            }
            Method getDeclaredFields0Method = Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class);
            getDeclaredFields0Method.setAccessible(true);
            Field[] tranformFields = (Field[]) getDeclaredFields0Method.invoke(clazz, publicOnly);
            for (Field f : tranformFields) {
                if (f.getName().equals(HotCodeConstant.HOTCODE_ADDED_FIELDS)
                    || f.getName().equals(HotCodeConstant.HOTCODE_STATIC_FIELDS)) {
                    holderFields.add(f);
                }
            }

            return holderFields.toArray(new Field[] {});
        } catch (Exception e) {
            e.printStackTrace();
        }

        // unreached
        return new Field[0];
    }
}
