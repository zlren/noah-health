import com.yhch.util.TimeUtil;

import java.util.Date;

/**
 * Created by zlren on 2017/7/17.
 */
public class TimeTest {
    public static void main(String[] args) {
        Date oneYearAfterTime = TimeUtil.getOneYearAfterTime();
        Date now = new Date();

        System.out.println(oneYearAfterTime);
        System.out.println(now);

        System.out.println(now.before(oneYearAfterTime));

    }
}
