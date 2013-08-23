import java.io.File;
import java.lang.System;
import java.lang.Thread;
import java.lang.reflect.Method;

/**
 * @author khotyn 2013-07-03 10:18:08
 */
public class Base {

    public static void main(String[] args) throws Exception {
        String caseName = args[0];
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File baseClassFile = new File(classLoader.getResource("Base.class").getFile());
        long lastModified = baseClassFile.lastModified();
        Class<?> klass = classLoader.loadClass("A");
        baseClassFile.setLastModified(System.currentTimeMillis());
        Method method = klass.getMethod("test");
        Boolean result = (Boolean) method.invoke(klass.newInstance());

        while (true) {
            if (baseClassFile.lastModified() > lastModified) {
                break;
            }
        }

        Boolean reloadedResult = (Boolean) method.invoke(klass.newInstance());

        if (!result || !reloadedResult) {
            System.err.println(caseName + "Failed!!");
            System.err.println("Result before reload is " + result);
            System.err.println("Result after reload is " + reloadedResult);
            System.exit(1);
        }

        System.out.println("Success!");
    }
}
