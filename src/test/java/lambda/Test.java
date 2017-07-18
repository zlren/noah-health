package lambda;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by zlren on 2017/7/17.
 */
public class Test {
    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        Stream<Integer> filterList = list.stream().filter(n -> n > 2);


        list.forEach(System.out::print);
        System.out.println();
        filterList.forEach(System.out::print);
    }
}
