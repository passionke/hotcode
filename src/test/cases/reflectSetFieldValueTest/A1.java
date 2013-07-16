import java.lang.reflect.Field;

/**
 * @author zhuyong
 */
public class A {

    private String  str2  = "value2";
    private int     int2  = 2;
    private boolean bool2 = true;

    public boolean test() throws Exception {
        Class<?> clazz = this.getClass();
        Field f = clazz.getDeclaredField("str2");
        f.setAccessible(true);
        f.set(this, "value3");

        Field f2 = clazz.getDeclaredField("int2");
        f2.setAccessible(true);
        f2.setInt(this, 3);

        Field f3 = clazz.getDeclaredField("bool2");
        f3.setAccessible(true);
        f3.setBoolean(this, false);

        return str2.equals("value3") && int2 == 3 && f3.getBoolean(this) == false;
    }
}
