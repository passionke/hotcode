package org.hotcode.hotcode.reloader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * {@link ClassReloaderManager} manager.
 * 
 * @author khotyn 13-6-26 PM5:13
 */
public class CRMManager {

    private static AtomicLong                      indexGenerator          = new AtomicLong(0);
    private static Map<ClassLoader, Long>          classLoaderIndexMap     = new ConcurrentHashMap<ClassLoader, Long>();
    private static Map<Long, ClassReloaderManager> classReloaderManagerMap = new ConcurrentHashMap<Long, ClassReloaderManager>();

    public static Long getIndex(ClassLoader classLoader) {
        return classLoaderIndexMap.get(classLoader);
    }

    public static ClassReloaderManager getClassReloaderManager(long index) {
        return classReloaderManagerMap.get(index);
    }

    public static Long putClassReloaderManager(ClassLoader classLoader, ClassReloaderManager classReloaderManager) {
        Long index = indexGenerator.incrementAndGet();
        classLoaderIndexMap.put(classLoader, index);
        classReloaderManagerMap.put(index, classReloaderManager);
        return index;
    }

    public static void registerClassLoader(ClassLoader classLoader) {
        if (classLoaderIndexMap.get(classLoader) != null) {
            return;
        }

        putClassReloaderManager(classLoader, new ClassReloaderManager(classLoader));
    }

    public static ClassReloaderManager getClassReloaderManager(ClassLoader classLoader) {
        Long index = CRMManager.getIndex(classLoader);

        if (index == null) {
            return null;
        }

        return CRMManager.getClassReloaderManager(index);
    }

    public static ClassReloader getClassReloader(ClassLoader classLoader, String classInternalName) {
        ClassReloaderManager crm = getClassReloaderManager(classLoader);

        if (crm == null) {
            return null;
        }

        return crm.getClassReloader(crm.getIndex(classInternalName));
    }

    public static Class<?> getShadowClass(Class<?> originClass) {
        if (originClass.getClassLoader() != null) {
            ClassReloaderManager crm = getClassReloaderManager(originClass.getClassLoader());

            if (crm != null) {
                return crm.getShadowClass(originClass.getName());
            }
        }

        return null;
    }

    public static boolean hasShadowClass(Class<?> originClass) {
        Class<?> shadowClass = getShadowClass(originClass);

        if (shadowClass != null) {
            return true;
        }

        return false;
    }
}
