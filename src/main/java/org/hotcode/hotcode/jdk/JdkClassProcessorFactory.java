package org.hotcode.hotcode.jdk;

import java.lang.reflect.Field;
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

    public static final Map<Class<?>, Class<? extends ClassVisitor>> jdk_class_processor_holder = new HashMap<Class<?>, Class<? extends ClassVisitor>>();

    static {
        jdk_class_processor_holder.put(ClassLoader.class, ClassLoaderAdapter.class);
        jdk_class_processor_holder.put(Class.class, JdkClassAdapter.class);
        jdk_class_processor_holder.put(Field.class, JdkFieldAdapter.class);
    }

}
