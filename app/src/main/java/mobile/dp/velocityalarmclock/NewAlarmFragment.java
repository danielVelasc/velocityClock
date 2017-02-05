package mobile.dp.velocityalarmclock;

import android.app.Fragment;
import android.content.Context;

/**
 * @author Daniel Velasco
 * @since February 05, 2017
 */

public class NewAlarmFragment extends Fragment {

    NewAlarmFragmentListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NewAlarmFragmentListener) {
            mListener = (NewAlarmFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NewAlarmFragmentListener");
        }
    }
}
