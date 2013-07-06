package org.hotcode.hotcode;

/**
 * Class loader used to load assist classes.
 * 
 * @author khotyn 13-7-6 PM10:08
 */
public class AssistClassClassLoader extends ClassLoader {

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }
}
