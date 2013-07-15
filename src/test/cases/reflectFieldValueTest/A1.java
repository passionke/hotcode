import java.lang.reflect.Field;

/**
 * @author zhuyong
 */
public class A {

    private String str2 = "value2";

    public boolean test() throws Exception {
        Class<?> clazz = this.getClass();
        Field f = clazz.getDeclaredField("str2");
        f.setAccessible(true);
        return f.get(this).toString().equals(str2);
    }
}
