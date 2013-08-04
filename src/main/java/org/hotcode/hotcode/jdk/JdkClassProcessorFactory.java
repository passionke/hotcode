package org.hotcode.hotcode.jdk;

import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.hotcode.hotcode.jdk.reflect.JdkClassAdapter;
import org.hotcode.hotcode.jdk.reflect.JdkFieldAdapter;
import org.objectweb.asm.ClassVisitor;

/**
 * JDK System Class Processor Factory
 * 
 * @author zhuyong 2013-7-3 12:20:12
 */
public class JdkClassProcessorFactory {

    public static final Map<Class<?>, Class<? extends ClassVisitor>> JDK_CLASS_PROCESSOR_HOLDER = new HashMap<>();

    static {
        JDK_CLASS_PROCESSOR_HOLDER.put(ClassLoader.class, ClassLoaderAdapter.class);
        JDK_CLASS_PROCESSOR_HOLDER.put(Class.class, JdkClassAdapter.class);
        JDK_CLASS_PROCESSOR_HOLDER.put(Field.class, JdkFieldAdapter.class);
        JDK_CLASS_PROCESSOR_HOLDER.put(URLClassLoader.class, URLClassLoaderAdapter.class);
    }
}
