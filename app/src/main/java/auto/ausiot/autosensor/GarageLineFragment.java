package auto.ausiot.autosensor;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.URISyntaxException;
import java.util.Date;

import auto.ausiot.util.Constants;
import mqtt.HeartBeatCallBack;
import mqtt.Subscriber;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GarageLineFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GarageLineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GarageLineFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ZONE = "1";
    private static final String ARG_LINE1 = "Garage Controller";
    private static final String ARG_LINE2 = "Line2";
    private static final String ARG_UNIT_ID = "U00";
    private static final String ARG_MP = "Line2";

    // TODO: Rename and change types of parameters
    private String mParamZone;
    private String mParamLine1;
    private String mParamLine2;
    private String mParamUnitID;
    private static MediaPlayer mp;
    private int state = -1;
    private boolean last_known_door_state = false; // True for Open , False for Close
    public static Date last__disabled_time = new Date();

    private OnFragmentInteractionListener mListener;

    private static boolean mIsClicked =true;
    public GarageLineFragment() {
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
    public static GarageLineFragment newInstance(String zoneString, String line1String,
                                                 String line2String, String unitID,
                                                 MediaPlayer mplayer) {
        GarageLineFragment fragment = new GarageLineFragment();
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
//        RadioGroup radioGroup_1 = (RadioGroup) getView().findViewById(R.id.button_sensor_fragment_1);
//        //Button btnSensor = (Button) view.findViewById(R.id.water_line);
//
//        radioGroup_1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                // find which radio button is selected
//                //Button btn = (Button) getView().findViewById(R.id.button_indicator_1);
//                if(checkedId == R.id.on_1) {
//                    //btn.setBackgroundResource(R.drawable.circle_indicator);
//                   mp.start();
//                   sendMQTTMsg(mParamUnitID,Constants.ACTION_R1_OPEN);
//                } else if(checkedId == R.id.off_1) {
//                    //btn.setBackgroundResource(R.drawable.circle_indicator_off);
//                    mp.start();
//                    sendMQTTMsg(mParamUnitID,Constants.ACTION_R1_CLOSE);
//                }
//            }
//
//        });

        final Button btn_indicator = (Button) getView().findViewById(R.id.button_indicator_1);
        final Button btn_controller = (Button) getView().findViewById(R.id.button_single_contoller);
        btn_controller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(state == 1){
                    mp.start();
                    sendMQTTMsg(mParamUnitID,Constants.ACTION_R1_CLOSE);
                    last_known_door_state = true; //Open
                    btn_controller.setAlpha(.5f);
                    btn_indicator.setAlpha(.5f);
                    btn_controller.setEnabled(false);
                }else if (state == 0){
                    mp.start();
                    sendMQTTMsg(mParamUnitID,Constants.ACTION_R1_OPEN);
                    last_known_door_state = false; //Close
                    btn_controller.setAlpha(.5f);
                    btn_indicator.setAlpha(.5f);
                    btn_controller.setEnabled(false);
                }else if (state == -1){
                    Toast.makeText(getActivity(), "Action cannot be performed at this time !" ,Toast.LENGTH_LONG).show();
                    //btn_controller.setEnabled(false);
                }
            }

        });

        setLabels();
        disable_all_Controls();
    }


    void setLabels(){
        //TextView zone = (TextView) getView().findViewById(R.id.label_zone_fragment);
        TextView line1 = (TextView) getView().findViewById(R.id.label_line1_fragment);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_garage_line, container, false);
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
        if (mIsClicked == true) {
            try {
                Subscriber.sendMsg(topic, action);
            } catch (MqttException e) {
                e.printStackTrace();
                mIsClicked = true;
            } catch (URISyntaxException e) {
                e.printStackTrace();
                mIsClicked = true;
            }
        }else{
            mIsClicked = true;
        }
        //Topic

    }

    public void disable_all_Controls(){
//        final RadioGroup radioGroup_1 = (RadioGroup) getView().findViewById(R.id.button_sensor_fragment_1);
//        for (int i = 0; i < radioGroup_1.getChildCount(); i++) {
//            radioGroup_1.getChildAt(i).setEnabled(false);
//        }
        //final RadioGroup radioGroup_2 = (RadioGroup) getView().findViewById(R.id.button_sensor_fragment_2);
        final MonitorActivity ma = (MonitorActivity) getActivity();

//        for (int i = 0; i < radioGroup_2.getChildCount(); i++) {
//            radioGroup_2.getChildAt(i).setEnabled(false);
//        }

        ma.textBanner.setText("Network Down");
        ma.textBanner.setTextColor(Color.RED);

        Button btn_1 = (Button) getView().findViewById(R.id.button_indicator_1);
        btn_1.setBackgroundResource(R.mipmap.ic_unknown_garage_round);
        TextView tv = (TextView) getView().findViewById(R.id.label_line1_fragment);
        tv.setText("Status of your garage door cannot be determined");
        state = -1;

        Button btn_controller = (Button) getView().findViewById(R.id.button_single_contoller);
        btn_controller.setText("");
        btn_controller.setBackgroundResource(R.mipmap.ic_unknown_button_round);

        //btn_controller.setEnabled(false);

        ma.textBanner_2.setText(getResources().getString(R.string.try_again_text));

    }


    public void enable_all_Controls(boolean iscallback){
//        final Button on_1 = (RadioButton) getView().findViewById(R.id.on_1);
//        on_1.setEnabled(true);
//        final Button off_1 = (RadioButton) getView().findViewById(R.id.off_1);
//        off_1.setEnabled(true);

        final MonitorActivity ma = (MonitorActivity) getActivity();
        if(iscallback) {
            ma.textBanner.setText("Network On T");
        }else{
            ma.textBanner.setText("Network On F");

        }
        ma.textBanner.setTextColor(Color.GREEN);
        ma.textBanner_2.setText("");

     }

     public void set_Indicators(){
         Button btn_1 = (Button) getView().findViewById(R.id.button_indicator_1);
         TextView tv = (TextView) getView().findViewById(R.id.label_line1_fragment);

         Button btn_controller = (Button) getView().findViewById(R.id.button_single_contoller);

         if (HeartBeatCallBack.isNetwork_up() == true) {
             if (HeartBeatCallBack.STATUS_R1 == true) {
                 btn_1.setBackgroundResource(R.mipmap.ic_open_garage_round);
                 tv.setText("Your garage door is opened");
                 state = 1;

                 btn_controller.setText("");
                 btn_controller.setBackgroundResource(R.mipmap.ic_close_button_round);
                 if( last_known_door_state == false) // If previous state was Close
                 {
                    btn_controller.setEnabled(true);
                    btn_controller.setAlpha(1.0f);
                    btn_1.setAlpha(1.0f);
                 }
             } else {
                 btn_1.setBackgroundResource(R.mipmap.ic_close_garage_round);
                 tv.setText("Your garage door is closed");
                 state = 0;

                 btn_controller.setText("");
                 btn_controller.setBackgroundResource(R.mipmap.ic_open_button_round);
                 if( last_known_door_state == true) // If previous state was Open
                 {
                     btn_controller.setEnabled(true);
                     btn_controller.setAlpha(1.0f);
                     btn_1.setAlpha(1.0f);
                 }
             }
         }else {
             if (state == -1) {
                 btn_1.setBackgroundResource(R.mipmap.ic_unknown_garage_round);
                 tv.setText("Status of your garage door cannot be determined");

                 btn_controller.setText("");
                 btn_controller.setBackgroundResource(R.mipmap.ic_unknown_button_round);
                 btn_controller.setEnabled(false);
             }
         }
//         Button btn_2 = (Button) getView().findViewById(R.id.button_indicator_2);
//         if(HeartBeatCallBack.STATUS_R2 == true) {
//             btn_2.setBackgroundResource(R.drawable.circle_indicator);
//         }else{
//             btn_2.setBackgroundResource(R.drawable.circle_indicator_off);
//         }
     }

    public void save_state(Bundle savedInstanceState){
        //RadioGroup radioGroup_1 = (RadioGroup) getView().findViewById(R.id.button_sensor_fragment_1);
        //savedInstanceState.putInt("line_1", radioGroup_1.getCheckedRadioButtonId());
        Button btn_controller = (Button) getView().findViewById(R.id.button_single_contoller);
        savedInstanceState.putInt("state", state);
        savedInstanceState.putBoolean("controller_enable", btn_controller.isEnabled());
        savedInstanceState.putBoolean("last_known_door_state", last_known_door_state);


//        RadioGroup radioGroup_2 = (RadioGroup) getView().findViewById(R.id.button_sensor_fragment_2);
//        savedInstanceState.putInt("line_2", radioGroup_2.getCheckedRadioButtonId());

    }

    public void restore_state(Bundle savedInstanceState) {
        //Dont Send a MQTT Messages when restoring
        Button btn_controller = (Button) getView().findViewById(R.id.button_single_contoller);
        last_known_door_state = savedInstanceState.getBoolean("last_known_door_state",true);
//        if (savedInstanceState.getInt("line_1") != 0) {
//
//            //Compare whether the Selected ID Now and previous is different
//            //If so change the state , make sure to set mIsClicked to false
//            // This will prevent firing another MQTT Msg
//            final RadioButton on_1 = (RadioButton) getView().findViewById(savedInstanceState.getInt("line_1"));
//            if (on_1.isChecked() == false) {
//                mIsClicked = false;
//                on_1.setChecked(true);
//            }
//            //radioGroup_1.check(savedInstanceState.getInt("line_1"));
//        }
//
//        if (savedInstanceState.getInt("line_2") != 0) {
//            final RadioButton on_2 = (RadioButton) getView().findViewById(savedInstanceState.getInt("line_2"));
//            if (on_2.isChecked() == false) {
//                mIsClicked = false;
//                on_2.setChecked(true);
//            }
//        }

        if (savedInstanceState.getInt("state", 100) != 100) {
            Button btn_1 = (Button) getView().findViewById(R.id.button_indicator_1);
            TextView tv = (TextView) getView().findViewById(R.id.label_line1_fragment);

            state = savedInstanceState.getInt("state", 100);
            if (state == -1) {
                btn_1.setBackgroundResource(R.mipmap.ic_unknown_garage_round);
                tv.setText("Status of your garage door cannot be determined");

                btn_controller.setText("");
                btn_controller.setBackgroundResource(R.mipmap.ic_unknown_button_round);

            } else if (state == 0) {
                btn_1.setBackgroundResource(R.mipmap.ic_close_garage_round);
                tv.setText("Your garage door is closed");

                btn_controller.setText("");
                btn_controller.setBackgroundResource(R.mipmap.ic_open_button_round);

            } else if (state == 1) {
                btn_1.setBackgroundResource(R.mipmap.ic_open_garage_round);
                tv.setText("Your garage door is opened");

                btn_controller.setText("");
                btn_controller.setBackgroundResource(R.mipmap.ic_close_button_round);
            }

            if (savedInstanceState.getBoolean("controller_enable",true) == true){
                btn_controller.setEnabled(true);
                btn_controller.setAlpha(1.0f);
                btn_1.setAlpha(1.0f);
            }else{
                btn_controller.setEnabled(false);
                btn_controller.setAlpha(.5f);
                btn_1.setAlpha(.5f);
            }
        }
    }
}
