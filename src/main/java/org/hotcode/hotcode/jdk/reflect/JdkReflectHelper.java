package org.hotcode.hotcode.jdk.reflect;

import org.apache.commons.lang.StringUtils;
import org.hotcode.hotcode.constant.HotCodeConstant;
import org.hotcode.hotcode.reloader.CRMManager;
import org.hotcode.hotcode.reloader.ClassReloader;
import org.hotcode.hotcode.reloader.ClassReloaderManager;
import org.hotcode.hotcode.structure.FieldsHolder;
import org.hotcode.hotcode.util.HotCodeThreadLocalUtil;
import org.hotcode.hotcode.util.HotCodeUtil;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Field Reflect Helper
 * 
 * @author zhuyong 2013-7-3 12:33:26
 */
public class JdkReflectHelper {

    /**
     * Remove HotCode added fields
     */
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

    /**
     * Get Fields from Shadow Class
     */
    public static Field[] privateGetDeclaredFields0(Class<?> clazz, boolean publicOnly) {
        ClassReloaderManager classReloaderManager = CRMManager.getClassReloaderManager(clazz.getClassLoader());
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

    public static boolean isTransformFieldAccess(Class<?> originClass) {
        if (CRMManager.hasShadowClass(originClass) && HotCodeThreadLocalUtil.isFirstAccess()) {
            return true;
        }

        return false;
    }

    private static boolean isNewField(Field field) {
        Object originClass = field.getClass();
        ClassReloader classReloader = CRMManager.getClassReloader(originClass.getClass().getClassLoader(),
                                                                  originClass.getClass().getName());
        if (classReloader == null || classReloader.getReloadedClass() == null) {
            return false;
        }
        return classReloader.getReloadedClass().hasField(field.getModifiers(), field.getName(),
                                                         Type.getDescriptor(field.getType()))
               && classReloader.getOriginClass().hasField(field.getModifiers(), field.getName(),
                                                          Type.getDescriptor(field.getType()));
    }

    /**
     * Get field value
     */
    public static Object getFieldValue(Object object, Field field) {
        ClassReloader classReloader = CRMManager.getClassReloader(object.getClass().getClassLoader(),
                                                                  object.getClass().getName());
        if (classReloader == null) {
            return null;
        }

        try {
            if (isNewField(field)) {
                Object fieldHolder = null;
                if (Modifier.isStatic(field.getModifiers())) {
                    fieldHolder = getOriginFieldValue(object.getClass(), object, HotCodeConstant.HOTCODE_STATIC_FIELDS);
                } else {
                    fieldHolder = getOriginFieldValue(object.getClass(), object,
                                                      HotCodeConstant.HOTCODE_INSTANCE_FIELDS);
                }

                if (fieldHolder instanceof FieldsHolder) {
                    return ((FieldsHolder) fieldHolder).getField(HotCodeUtil.getFieldKey(field.getName(),
                                                                                         Type.getDescriptor(field.getType())));
                }
            } else {
                Field originField = getOriginField(object.getClass(), field.getName());
                return originField.get(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Field getOriginField(Class<?> clazz, String fieldName) {
        try {
            Method getDeclaredFields0Method = Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class);
            getDeclaredFields0Method.setAccessible(true);
            Field[] originFields = (Field[]) getDeclaredFields0Method.invoke(clazz, false);
            for (Field f : originFields) {
                if (StringUtils.equals(fieldName, f.getName())) {
                    return f;
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
        }

        return null;
    }

    public static Object getOriginFieldValue(Object target, String fieldName) {
        return getOriginFieldValue(target.getClass(), target, fieldName);
    }

    public static Object getOriginFieldValue(Class<?> clazz, Object target, String fieldName) {
        try {
            Field field = getOriginField(clazz, fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setOriginFieldValue(Object target, String fieldName, Object fieldValue) {
        setOriginFieldValue(target.getClass(), target, fieldName, fieldValue);
    }

    public static void setOriginFieldValue(Class<?> clazz, Object target, String fieldName, Object fieldValue) {
        try {
            Field field = getOriginField(clazz, fieldName);
            field.setAccessible(true);
            field.set(target, fieldValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
