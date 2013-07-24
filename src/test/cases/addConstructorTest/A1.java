/**
 * @author khotyn 2013-07-03 10:18:08
 */
public class A {

    private int i;

    public A(){

    }

    public A(int str){
        i = str;
    }

    public boolean test() {
        A a = new A(100);
        return a.i == 100;
    }
}
