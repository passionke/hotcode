package org.hotcode.hotcode;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class redefiner, holds the instrumentation to redefine classes.
 * 
 * @author khotyn 2013-06-24 20:22:40
 */
public class ClassRedefiner {

    private static final Logger    logger = LoggerFactory.getLogger(ClassRedefiner.class);
    private static Instrumentation inst;

    public static void setInstrumentation(Instrumentation inst) {
        ClassRedefiner.inst = inst;
    }

    public static void redefine(Class<?> klass, byte[] classFile) {
        try {
            inst.redefineClasses(new ClassDefinition(klass, classFile));
        } catch (ClassNotFoundException | UnmodifiableClassException e) {
            logger.error("Failed to redefine class " + klass.getName() + ".", e);
        }
    }
}
