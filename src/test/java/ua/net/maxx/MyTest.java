package ua.net.maxx;

import org.junit.Test;

public class MyTest {

    class A {
        public void d() {
            System.out.println("a");
        }
    }
    class B  extends A{
        public void d() {
            System.out.println("b");
        }
    }

    @Test
    public void test() {
        B b = new B();
        b.d();
        A a = (A) b;
        a.d();
    }

}
