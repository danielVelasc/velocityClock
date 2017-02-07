package mobile.dp.velocityalarmclock;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * @author Sharon Wong
 * @since February 4, 2017
 *
 * This class allows a user to set an alarm.
 */

public class SetAlarmFragment extends Fragment {

    SetAlarmFragmentListener mListener;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (v == null){
            v = inflater.inflate(R.layout.set_alarm_fragment, container, false);
        }
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SetAlarmFragmentListener) {
            mListener = (SetAlarmFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (v == null){
            v = inflater.inflate(R.layout.set_alarm_fragment, container, false);
        }
        return v;
    }

}
