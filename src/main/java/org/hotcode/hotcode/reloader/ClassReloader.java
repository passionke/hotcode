package org.hotcode.hotcode.reloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.hotcode.hotcode.ClassRedefiner;
import org.hotcode.hotcode.ClassTransformer;
import org.hotcode.hotcode.constant.HotCodeConstant;
import org.hotcode.hotcode.resource.VersionedClassFile;
import org.hotcode.hotcode.structure.HotCodeClass;
import org.hotcode.hotcode.util.ClassDumper;

/**
 * Class reloader
 * 
 * @author khotyn 13-6-25 PM5:20
 */
public class ClassReloader {

    private Long               classReloaderManagerIndex;
    private Long               classIndex;
    private HotCodeClass       originClass;
    private VersionedClassFile versionedClassFile;
    private ClassLoader        classLoader;

    public ClassReloader(Long classReloaderManagerIndex, Long classIndex, VersionedClassFile versionedClassFile,
                         HotCodeClass originClass, ClassLoader classLoader){
        this.classReloaderManagerIndex = classReloaderManagerIndex;
        this.classIndex = classIndex;
        this.versionedClassFile = versionedClassFile;
        this.originClass = originClass;
        this.classLoader = classLoader;
    }

    public boolean checkAndReload() {
        return versionedClassFile.changed() && reload();
    }

    public HotCodeClass getOriginClass() {
        return originClass;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public byte[] getClassFileByte() {
        return versionedClassFile.getClassFile();
    }

    public void initInterface() {
        Class klass = CRMManager.getClassReloaderManager(classReloaderManagerIndex).loadAssistClass(originClass.getClassName());
        try {
            Method method = klass.getMethod(HotCodeConstant.HOTCODE_INTERFACE_CLINIT_METHOD_NAME);
            method.invoke(klass);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace(); // TODO
        }
    }

    private boolean reload() {
        byte[] transformedClassFile = ClassTransformer.transformReloadClass(classReloaderManagerIndex, classIndex,
                                                                            versionedClassFile.reloadAndGetClassFile());
        ClassDumper.dump(originClass.getClassName().replace('.', '/'), transformedClassFile);
        try {
            Class klass = classLoader.loadClass(originClass.getClassName());
            ClassRedefiner.redefine(klass, transformedClassFile);
            CRMManager.getClassReloaderManager(classReloaderManagerIndex).incAssitClasssIndexGenerator();
            reinit(klass);
            return true;
        } catch (ClassNotFoundException e) {
            // TODO
            return false;
        }
    }

    private void reinit(Class klass) {
        int access = originClass.getAccess();

        if (Modifier.isInterface(access)) {
            initInterface();
        } else {
            try {
                Method method = klass.getMethod(HotCodeConstant.HOTCODE_CLINIT_METHOD_NAME);
                method.invoke(klass);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace(); // TODO
            }
        }
    }
}
