import java.lang.reflect.Field;

/**
 * @author zhuyong
 */
public class A {

    private String one = "one";

    public boolean test() throws Exception {
        Class<?> clazz = this.getClass();
        Field f = clazz.getDeclaredField("one");
        return f != null;
    }
}
