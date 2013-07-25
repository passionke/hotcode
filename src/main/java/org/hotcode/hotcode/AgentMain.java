package org.hotcode.hotcode;

import java.io.InputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.util.Map.Entry;

import org.hotcode.hotcode.jdk.JdkClassProcessorFactory;
import org.hotcode.hotcode.util.ClassDumper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The entry of the HotCode agent.
 * 
 * @author khotyn 2013-06-24 20:21:43
 */
public class AgentMain {

    private static final Logger logger = LoggerFactory.getLogger(AgentMain.class);

    public static void premain(String agentArgs, Instrumentation inst) {
        ClassRedefiner.setInstrumentation(inst);
        redefineJdkClasses(inst);
    }

    private static void redefineJdkClasses(Instrumentation inst) {
        try {
            for (Entry<Class<?>, Class<? extends ClassVisitor>> entry : JdkClassProcessorFactory.JDK_CLASS_PROCESSOR_HOLDER.entrySet()) {
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                ClassVisitor cv = cw;
                Constructor<? extends ClassVisitor> c = entry.getValue().getConstructor(ClassVisitor.class);
                cv = c.newInstance(cv);
                InputStream is = ClassLoader.getSystemResourceAsStream(Type.getInternalName(entry.getKey()) + ".class");
                ClassReader cr = new ClassReader(is);
                cr.accept(cv, ClassReader.EXPAND_FRAMES);
                byte[] transformedByte = cw.toByteArray();
                ClassDumper.dump(Type.getInternalName(entry.getKey()), transformedByte);
                ClassDefinition definitions = new ClassDefinition(entry.getKey(), cw.toByteArray());
                inst.redefineClasses(definitions);
            }
        } catch (Exception e) {
            logger.error("Failed to redefine JDK classes.", e);
        }
    }
}
