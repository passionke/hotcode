import java.lang.reflect.Field;

/**
 * @author zhuyong
 */
public class A {

    private String two = "two";

    public boolean test() throws Exception {
        Class<?> clazz = this.getClass();
        Field f = clazz.getDeclaredField("two");
        return f.getName().equalsIgnoreCase("two");
    }
}
