package nowcoder;

import java.util.*;

public class Main {
    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        int m = -1, n = -1;
        int count = -1;
        List<Integer> result = new ArrayList<>();
        while (in.hasNext()) {
            m = in.nextInt();
            n = in.nextInt();
            count = 0;
            result.clear();

            for (int i = m; i <= n; i++) {
                int cur = i;
                while (cur > 0) {
                    count += Math.pow(cur % 10, 3);
                    cur = cur / 10;
                }

                if (count == i) {
                    result.add(i);
                }
            }

            if (result.size() == 0) {
                System.out.println("no");
            } else {
                System.out.print(result.get(0));
                for (int i = 1; i < result.size(); i++) {
                    System.out.print(" ");
                    System.out.print(result.get(i));
                }
                System.out.println();
            }

        }
    }
}