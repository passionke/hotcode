package org.hotcode.hotcode.constant;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.Type;

/**
 * Constants of HotCode are defined here.
 * 
 * @author khotyn 2013-06-24 20:22:14
 */
public class HotCodeConstant {

    // =================== Field Names ===================
    /**
     * The field name of the field which holds the static fields of the class.
     */
    public static final String      HOTCODE_STATIC_FIELDS                = "__hotcode_static_fields__";
    /**
     * The field name of the field which holds the instance fields of the class.
     */
    public static final String      HOTCODE_INSTANCE_FIELDS              = "__hotcode_instance_fields__";
    /**
     * The field name of the field which is used for class reload.
     */
    public static final String      HOTCODE_CLASS_RELOADER_FIELDS        = "__hotcode_class_reloader_field__";

    // =================== Method Names ===================
    /**
     * The name of the HotCode "<clinit>" method for class reinitialize.
     */
    public static final String      HOTCODE_CLINIT_METHOD_NAME           = "__hotcode_clinit__";
    /**
     * The name of the method for interface static field reinitialize.
     */
    public static final String      HOTCODE_INTERFACE_CLINIT_METHOD_NAME = "__hotcode_interface_clinit__";
    /**
     * The name of the method that is add to class as a static method router.
     */
    public static final String      HOTCODE_STATIC_METHOD_ROUTER_NAME    = "__hotcode_static_method_router__";

    // =================== Method Descriptors ===================
    // First param: (methodName + methodDesc).hashCode(); Second param: parameters of the method.
    public static final String      HOTCODE_STATIC_METHOD_ROUTER_DESC    = Type.getMethodDescriptor(Type.getType(Object.class),
                                                                                                    Type.INT_TYPE,
                                                                                                    Type.getType(Object[].class));

    public static final Set<String> HOTCODE_ADDED_FIELDS                 = new HashSet<>();
    public static final Set<String> HOTCODE_ADDED_METHODS                = new HashSet<>();

    public static final String      HOTCODE_SHADOW_CLASS_POSTFIX         = "__$$shadow_class$$__";

    static {
        HOTCODE_ADDED_FIELDS.add(HOTCODE_STATIC_FIELDS);
        HOTCODE_ADDED_FIELDS.add(HOTCODE_INSTANCE_FIELDS);
        HOTCODE_ADDED_FIELDS.add(HOTCODE_CLASS_RELOADER_FIELDS);

        HOTCODE_ADDED_METHODS.add(HOTCODE_CLINIT_METHOD_NAME);
        HOTCODE_ADDED_METHODS.add(HOTCODE_INTERFACE_CLINIT_METHOD_NAME);
        HOTCODE_ADDED_METHODS.add(HOTCODE_STATIC_METHOD_ROUTER_NAME);
    }
}
