package auto.ausiot.autosensor;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.URISyntaxException;

import auto.ausiot.util.Constants;
import mqtt.Subscriber;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WaterLineFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WaterLineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WaterLineFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ZONE = "1";
    private static final String ARG_LINE1 = "Line1";
    private static final String ARG_LINE2 = "Line2";
    private static final String ARG_UNIT_ID = "U00";
    private static final String ARG_MP = "Line2";

    // TODO: Rename and change types of parameters
    private String mParamZone;
    private String mParamLine1;
    private String mParamLine2;
    private String mParamUnitID;
    private static MediaPlayer mp;

    private OnFragmentInteractionListener mListener;

    public WaterLineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param zoneString Parameter 1.
     * @param line1String Parameter 2.
     * @return A new instance of fragment WaterLineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WaterLineFragment newInstance(String zoneString, String line1String,
                                                String line2String, String unitID,
                                                MediaPlayer mplayer) {
        WaterLineFragment fragment = new WaterLineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ZONE, zoneString);
        args.putString(ARG_LINE1, line1String);
        args.putString(ARG_LINE2, line2String);
        args.putString(ARG_UNIT_ID, unitID);
        mp = mplayer;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamZone = getArguments().getString(ARG_ZONE);
            mParamLine1 = getArguments().getString(ARG_LINE1);
            mParamLine2 = getArguments().getString(ARG_LINE2);
            mParamUnitID = getArguments().getString(ARG_UNIT_ID);
        }


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RadioGroup radioGroup_1 = (RadioGroup) getView().findViewById(R.id.button_sensor_fragment_1);
        //Button btnSensor = (Button) view.findViewById(R.id.water_line);

        radioGroup_1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                Button btn = (Button) getView().findViewById(R.id.button_indicator_1);
                if(checkedId == R.id.on_1) {
                   btn.setBackgroundResource(R.drawable.circle_indicator);
                   mp.start();
                   sendMQTTMsg(mParamUnitID,Constants.ACTION_R1_OPEN);
                } else if(checkedId == R.id.off_1) {
                    btn.setBackgroundResource(R.drawable.circle_indicator_off);
                    mp.start();
                    sendMQTTMsg(mParamUnitID,Constants.ACTION_R1_CLOSE);
                }
            }

        });


        RadioGroup radioGroup_2 = (RadioGroup) getView().findViewById(R.id.button_sensor_fragment_2);
        //Button btnSensor = (Button) view.findViewById(R.id.water_line);

        radioGroup_2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                Button btn = (Button) getView().findViewById(R.id.button_indicator_2);
                if(checkedId == R.id.on_2) {
                    btn.setBackgroundResource(R.drawable.circle_indicator);
                    mp.start();
                    sendMQTTMsg(mParamUnitID,Constants.ACTION_R2_OPEN);
                } else if(checkedId == R.id.off_2) {
                    btn.setBackgroundResource(R.drawable.circle_indicator_off);
                    mp.start();
                    sendMQTTMsg(mParamUnitID,Constants.ACTION_R2_CLOSE);
                }
            }

        });
        setLabels();
    }

    void setLabels(){
        TextView zone = (TextView) getView().findViewById(R.id.label_zone_fragment);
        TextView line1 = (TextView) getView().findViewById(R.id.label_line1_fragment);
        TextView line2 = (TextView) getView().findViewById(R.id.label_line2_fragment);
        zone.setText("Zone " + mParamZone);
        line1.setText("Line " + mParamLine1);
        line2.setText("Line " + mParamLine2);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_water_line, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



    public void sendMQTTMsg(String topic, String action) {
        try {
            Subscriber.sendMsg(topic, action);
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        //Topic

    }
}
