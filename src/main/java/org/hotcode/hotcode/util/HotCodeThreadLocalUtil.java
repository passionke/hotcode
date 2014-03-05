package org.hotcode.hotcode.util;

public class HotCodeThreadLocalUtil {

    private static final ThreadLocal<Boolean> FIELD_ACCESS_THREAD_LOCAL = new ThreadLocal<Boolean>();
    private static final ThreadLocal<Boolean> LOADING_SHADOW_CLASS      = new ThreadLocal<Boolean>();

    public static void access() {
        FIELD_ACCESS_THREAD_LOCAL.set(true);
    }

    public static boolean isFirstAccess() {
        return FIELD_ACCESS_THREAD_LOCAL.get() == null;
    }

    public static void clearAccess() {
        FIELD_ACCESS_THREAD_LOCAL.remove();
    }

    public static void startLoadShadowClass() {
        LOADING_SHADOW_CLASS.set(true);
    }

    public static void endLoadShadowClass() {
        LOADING_SHADOW_CLASS.remove();
    }

    public static boolean loadingShadowClass() {
        return LOADING_SHADOW_CLASS.get() != null;
    }
}
