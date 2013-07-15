import java.lang.reflect.Field;

/**
 * @author zhuyong
 */
public class A {

    private String str1 = "value1";

    public boolean test() throws Exception {
        Class<?> clazz = this.getClass();
        Field f = clazz.getDeclaredField("str1");
        f.setAccessible(true);
        return f.get(this).toString().equals(str1);
    }
}
