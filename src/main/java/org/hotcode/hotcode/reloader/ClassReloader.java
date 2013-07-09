package org.hotcode.hotcode.reloader;

import org.hotcode.hotcode.ClassRedefiner;
import org.hotcode.hotcode.ClassTransformer;
import org.hotcode.hotcode.constant.HotCodeConstant;
import org.hotcode.hotcode.resource.VersionedClassFile;
import org.hotcode.hotcode.structure.HotCodeClass;
import org.hotcode.hotcode.util.ClassDumper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Class reloader
 * 
 * @author khotyn 13-6-25 PM5:20
 */
public class ClassReloader {

    private Long               classReloaderManagerIndex;
    private Long               classIndex;
    private HotCodeClass       originClass;
    private HotCodeClass       reloadedClass;                           // contain new added fields & methods
    private VersionedClassFile versionedClassFile;
    private ClassLoader        classLoader;
    private AtomicLong         shadowIndexGenerator = new AtomicLong(0);

    public ClassReloader(Long classReloaderManagerIndex, Long classIndex, VersionedClassFile versionedClassFile,
                         HotCodeClass originClass, ClassLoader classLoader){
        this.classReloaderManagerIndex = classReloaderManagerIndex;
        this.classIndex = classIndex;
        this.versionedClassFile = versionedClassFile;
        this.originClass = originClass;
        this.classLoader = classLoader;
    }

    public boolean checkAndReload() {
        // try {
        // Long index = CRMManager.getIndex(this.classLoader);
        // ClassReloaderManager crm = CRMManager.getClassReloaderManager(index);
        // Class<?> shadowClass = crm.getShadowClass(this.originClass.getClassName());
        // System.out.println(shadowClass);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        return versionedClassFile.changed() && reload();
    }

    public HotCodeClass getOriginClass() {
        return originClass;
    }

    public void setReloadedClass(HotCodeClass reloadedClass) {
        this.reloadedClass = reloadedClass;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public VersionedClassFile getVersionedClassFile() {
        return versionedClassFile;
    }

    public String getShadowClassName() {
        return originClass.getClassName() + HotCodeConstant.HOTCODE_SHADOW_CLASS_POSTFIX + shadowIndexGenerator.get();
    }

    private boolean reload() {
        byte[] transformedClassFile = ClassTransformer.transformReloadClass(classReloaderManagerIndex, classIndex,
                versionedClassFile.reloadAndGetClassFile());
        ClassDumper.dump(originClass.getClassName().replace('.', '/'), transformedClassFile);
        try {
            ClassRedefiner.redefine(classLoader.loadClass(originClass.getClassName()), transformedClassFile);
            Class<?> klass = classLoader.loadClass(originClass.getClassName());

            // reinit class.
            try {
                Method method = klass.getMethod(HotCodeConstant.HOTCODE_CLINIT_METHOD_NAME);
                method.invoke(klass);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace(); // TODO
            }

            shadowIndexGenerator.incrementAndGet();
            
            return true;
        } catch (ClassNotFoundException e) {
            // TODO
            return false;
        }
    }
}
