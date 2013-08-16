package org.hotcode.hotcode.util;

import org.hotcode.hotcode.adapter.ClassInfoCollectAdapter;
import org.hotcode.hotcode.structure.HotCodeClass;
import org.objectweb.asm.*;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * @author khotyn 13-7-2 PM7:45
 */
public class HotCodeUtil {

    private static final char FIELD_DELIMITER = '-';

    public static String getFieldKey(int access, String name, String desc) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name) && !Strings.isNullOrEmpty(desc),
                                    "Name and desc can not be null.");
        return access + FIELD_DELIMITER + name + FIELD_DELIMITER + desc;
    }

    public static String getMainClassNameFromAssistClassName(String assistClassName) {
        if (assistClassName == null) {
            return null;
        }

        int index = assistClassName.indexOf("$$$");
        return assistClassName.substring(0, index);
    }

    public static int getMethodIndex(String name, String desc) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name) && !Strings.isNullOrEmpty(desc),
                                    "Name and desc can not be null.");
        return (name + FIELD_DELIMITER + desc).hashCode();
    }

    public static String addThisToDesc(String originDesc, String className) {
        return "(L" + className.replace('.', '/') + ";" + originDesc.substring(1);
    }

    public static HotCodeClass collectClassInfo(byte[] classFile) {
        HotCodeClass result = new HotCodeClass();
        ClassReader cr = new ClassReader(classFile);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
        ClassVisitor cv = new ClassInfoCollectAdapter(cw, result);
        cr.accept(cv, ClassReader.EXPAND_FRAMES);
        return result;
    }

    /**
     * Get boxed type of a primary type, copy from {@link org.objectweb.asm.commons.GeneratorAdapter}
     * 
     * @param type
     * @return
     */
    public static Type getBoxedType(final Type type) {
        switch (type.getSort()) {
            case Type.BYTE:
                return Type.getType(Byte.class);
            case Type.BOOLEAN:
                return Type.getType(Boolean.class);
            case Type.SHORT:
                return Type.getType(Short.class);
            case Type.CHAR:
                return Type.getType(Character.class);
            case Type.INT:
                return Type.getType(Integer.class);
            case Type.FLOAT:
                return Type.getType(Float.class);
            case Type.LONG:
                return Type.getType(Long.class);
            case Type.DOUBLE:
                return Type.getType(Double.class);
        }
        return type;
    }

    public static int changeAccToPublic(int originAcc) {
        return ((originAcc & ~Opcodes.ACC_PRIVATE) & ~Opcodes.ACC_PROTECTED) | Opcodes.ACC_PUBLIC;
    }

    public static int changeAccToStatic(int originAcc) {
        return originAcc | Opcodes.ACC_STATIC;
    }
}
