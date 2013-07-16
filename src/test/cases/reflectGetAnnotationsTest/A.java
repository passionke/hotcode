import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author zhuyong
 */
public class A {

    @Resource(name = "name1", lookup = "lookup1")
    private String str1 = "value1";

    public boolean test() throws Exception {
        Class<?> clazz = this.getClass();
        Field f = clazz.getDeclaredField("str1");

        Annotation[] annotations = f.getDeclaredAnnotations();

        return annotations.length == 1 && annotations[0] instanceof Resource
               && ((Resource) annotations[0]).name().equals("name1")
               && ((Resource) annotations[0]).lookup().equals("lookup1");
    }
}
