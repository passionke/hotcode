package org.hotcode.hotcode.reloader;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.hotcode.hotcode.java.lang.ShadowClassLoader;

/**
 * Every {@link ClassLoader} has a {@link ClassReloaderManager} to manage the {@link ClassReloader} of the classes that
 * loaded by the {@link ClassLoader}.
 * 
 * @author khotyn 13-6-26 PM4:36
 */
public class ClassReloaderManager {

    private ClassLoader                classLoader;
    /**
     * Index generator.
     */
    private AtomicLong                 indexGenerator    = new AtomicLong(0);

    /**
     * Map from a internal name of a class to index.
     */
    private Map<String, Long>          classMap          = new ConcurrentHashMap<>();
    /**
     * Map from indexGenerator to class reloader.
     */
    private Map<Long, ClassReloader>   classReloaderMap  = new ConcurrentHashMap<>();

    private WeakReference<ClassLoader> shadowClassLoader = null;

    public ClassReloaderManager(ClassLoader classLoader){
        this.classLoader = classLoader;
        this.shadowClassLoader = new WeakReference<ClassLoader>(new ShadowClassLoader(classLoader));
    }

    public Long getIndex(String classInternalName) {
        return classMap.get(classInternalName);
    }

    public Long getNextAvailableIndex() {
        return indexGenerator.incrementAndGet();
    }

    public ClassReloader getClassReloader(long index) {
        return classReloaderMap.get(index);
    }

    public void putClassReloader(Long index, String classInternalName, ClassReloader classReloader) {
        classMap.put(classInternalName, index);
        classReloaderMap.put(index, classReloader);
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public Class<?> getShadowClass(String name) {
        if (shadowClassLoader.get() == null) {
            shadowClassLoader = new WeakReference<ClassLoader>(new ShadowClassLoader(classLoader));
        }
        try {
            return shadowClassLoader.get().loadClass(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace(); // TODO
        }

        return null;
    }
}
