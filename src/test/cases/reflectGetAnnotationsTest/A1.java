import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author zhuyong
 */
public class A {

    @Deprecated
    private int    int2  = 2;
    @SuppressWarnings("warning")
    public boolean bool2 = false;
    @Deprecated
    @Resource(name = "name2", lookup = "lookup2")
    private String str1  = "value1";

    public boolean test() throws Exception {
        Class<?> clazz = this.getClass();
        Field f = clazz.getDeclaredField("int2");
        Annotation[] annotations = f.getDeclaredAnnotations();

        f = clazz.getDeclaredField("bool2");
        Annotation[] annotations2 = f.getDeclaredAnnotations();

        f = clazz.getDeclaredField("str1");
        Annotation[] annotations3 = f.getDeclaredAnnotations();

        return annotations.length == 1 && annotations[0] instanceof Deprecated && annotations2.length == 0
               && annotations3.length == 2 && annotations3[0] instanceof Deprecated
               && annotations3[1] instanceof Resource && ((Resource) annotations3[1]).name().equals("name2")
               && ((Resource) annotations3[1]).lookup().equals("lookup2");
    }
}
