package org.hotcode.hotcode;

import org.hotcode.hotcode.constant.HotCodeConstant;
import org.hotcode.hotcode.reloader.CRMManager;
import org.hotcode.hotcode.reloader.ClassReloaderManager;
import org.hotcode.hotcode.util.HotCodeThreadLocalUtil;

/**
 * @author khotyn.huangt 13-8-4 PM3:42
 */
public class HotCode {

    public static Class<?> findClass(String className) {
        if (!HotCodeThreadLocalUtil.loadingShadowClass()
            && className.contains(HotCodeConstant.HOTCODE_SHADOW_CLASS_POSTFIX)) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            ClassReloaderManager crm = CRMManager.getClassReloaderManager(classLoader);

            if (crm == null) {
                return null;
            }

            return crm.loadShadowClass(className);
        }
        return null;
    }
}
