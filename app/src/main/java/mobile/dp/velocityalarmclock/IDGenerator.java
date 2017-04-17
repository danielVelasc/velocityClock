package mobile.dp.velocityalarmclock;

import android.content.Context;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * /**
 * A singleton class to keep track of notification id's
 *
 * @author Daniel Velasco
 * @since April 17, 2017
 */

public class IDGenerator implements Serializable {
    private static final long serialVersionUID = 183742938754926378L;
    private static final String ID_GENERATOR_FILE_NAME = "id-generator";

    private final static AtomicInteger c = new AtomicInteger(0);

    /**
     * @return a unique id.
     */
    public static int getID() {
        return c.incrementAndGet();
    }

    public static void saveID(Context context) {
        ObjectOutputStream outputStream;

        try {
            outputStream =  new ObjectOutputStream(context.openFileOutput(ID_GENERATOR_FILE_NAME, Context.MODE_PRIVATE));

            outputStream.writeInt(c.intValue());

            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadID(Context context) {
        ObjectInputStream inputStream;

        try {
            inputStream =  new ObjectInputStream(context.openFileInput(ID_GENERATOR_FILE_NAME));

            c.set(inputStream.readInt());

            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}