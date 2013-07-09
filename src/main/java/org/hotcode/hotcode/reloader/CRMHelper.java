package org.hotcode.hotcode.reloader;

/**
 * Class Reloader Manager Helper
 * 
 * @author zhuyong 13-7-9 10:24
 */
public class CRMHelper {

    public static ClassReloaderManager getClassReloaderManager(ClassLoader classLoader) {
        Long index = CRMManager.getIndex(classLoader);

        if (index == null) {
            return null;
        }

        return CRMManager.getClassReloaderManager(index);
    }

    public static ClassReloader getClassReloader(ClassLoader classLoader, String className) {
        ClassReloaderManager crm = CRMHelper.getClassReloaderManager(classLoader);

        if (crm == null) {
            return null;
        }

        return crm.getClassReloader(crm.getIndex(className));
    }

    public static Class<?> getShadowClass(Class<?> originClass) {
        ClassReloaderManager crm = CRMHelper.getClassReloaderManager(originClass.getClassLoader());

        if (crm != null) {
            return crm.getShadowClass(originClass.getName());
        }

        return null;
    }
}
