package extend;

import org.springframework.beans.BeanUtils;

/**
 * Created by zlren on 17/6/25.
 */
public class Main {
    public static void main(String[] args) throws IllegalAccessException {

        A a = new A();
        a.setName("a");
        a.setName2("a2");

        B b = new B();

        // CopyUtil.copyObj(a, b);
        // System.out.println(b.getName());

        BeanUtils.copyProperties(a, b);
        System.out.println(b.getName() + " " + b.getName2());
    }
}
