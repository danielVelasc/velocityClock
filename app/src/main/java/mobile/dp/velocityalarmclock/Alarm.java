package mobile.dp.velocityalarmclock;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Daniel Velasco
 * @since February 3, 2017
 * @version 1.0
 *
 */
public class Alarm implements Serializable {

    Boolean isActive;
    Date time;

    public Alarm(Date toSet) {
        time = toSet;
        isActive = true;
    }

}
