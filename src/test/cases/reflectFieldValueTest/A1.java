import java.lang.reflect.Field;

/**
 * @author zhuyong
 */
public class A {

    private String str2 = "value2";
    private int    int2 = 2;

    public boolean test() throws Exception {
        Class<?> clazz = this.getClass();
        Field f = clazz.getDeclaredField("str2");
        f.setAccessible(true);

        Field f2 = clazz.getDeclaredField("int2");
        f2.setAccessible(true);
        return f.get(this).toString().equals(str2) && f2.getInt(this) == int2;
    }
}
