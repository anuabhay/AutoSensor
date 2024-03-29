package auto.ausiot.autosensor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;

import auto.ausiot.autosensor.widgets.CustomDialogClass;
import auto.ausiot.schedule.ScheduleBO;
import auto.ausiot.schedule.ScheduleHelper;
import auto.ausiot.stroe.RestCallBack;
import auto.ausiot.stroe.RestStore;
import auto.ausiot.util.DateHelper;
import auto.ausiot.util.TimeIgnoringComparator;
import auto.ausiot.vo.ScheduleType;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScheduleLineFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScheduleLineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleLineFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    public ScheduleBO schedulebo = null;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private String mParamSI;
    private String mParamUnitID;
    private String mParamLineID;

    private OnFragmentInteractionListener mListener;

    private static boolean mIsClicked =true;
    public ScheduleLineFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static ScheduleLineFragment newInstance(String scheduleIndex, String unitID , String lineID) {
        ScheduleLineFragment fragment = new ScheduleLineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, scheduleIndex);
        args.putString(ARG_PARAM2, unitID);
        args.putString(ARG_PARAM3, lineID);
        fragment.setArguments(args);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamSI = getArguments().getString(ARG_PARAM1);
            mParamUnitID = getArguments().getString(ARG_PARAM2);
            mParamLineID = getArguments().getString(ARG_PARAM3);
        }
    }

//    public void onRadioButtonClicked(View view) {
//        mIsClicked = true;
//    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        schedulebo = RestStore.getScheduleByID(mParamSI);

        TextView tvName = (TextView) getView().findViewById(R.id.label_schedule_name);
        tvName.setText(schedulebo.getName());

        TextView tvStart = (TextView) getView().findViewById(R.id.label_date_start);
        tvStart.setText(DateHelper.getPrintableDate(schedulebo.getStartDate()));

        TextView tvEnd = (TextView) getView().findViewById(R.id.label_date_end);
        tvEnd.setText(DateHelper.getPrintableDate(schedulebo.getEndDate()));

        LinearLayout mLayout = (LinearLayout) getView().findViewById(R.id.mainlayout);
        if (schedulebo.getType() == ScheduleType.Weekly){
            mLayout.setBackgroundColor(getResources().getColor(R.color.repeated_schedule_color));
        }else{
            //tvType.setText("S");
            tvEnd.setVisibility(View.INVISIBLE);
            mLayout.setBackgroundColor(getResources().getColor(R.color.single_schedule_color));
        }
        // Give user indication on the end date
        if (TimeIgnoringComparator.before(schedulebo.getEndDate(),new Date())){
            FrameLayout imgValid = (FrameLayout) getView().findViewById(R.id.image_invalid);
            imgValid.setVisibility(View.VISIBLE);
        }

        Button editBtn = (Button) getView().findViewById(R.id.button_edit);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (schedulebo.getType() == ScheduleType.Weekly){
                    loadRepeatEdit();
                }else{
                    loadSingleEdit();
                }
            }
        });

        Button deleteBtn = (Button) getView().findViewById(R.id.button_delete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("You are trying to delete the Schedule.")
                        .setMessage("This will delete the Schedule. Are you really sure you want to do this ?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteSchedule();
                            }
                        }).setNegativeButton("Cancel", null).show();

//                View.OnClickListener onclick = new View.OnClickListener(){
//
//                    @Override
//                    public void onClick(View v) {
//                        int x = 1;
//                    }
//                };
//                CustomDialogClass cdd=new CustomDialogClass(getActivity(),onclick,
//                        "You are trying to delete the Schedule.",
//                        "This will delete the Schedule. Are you really sure you want to do this ?");
//                cdd.show();



            }
        });
    }

    void loadSingleEdit(){
        Intent i = new Intent(getContext(),SingleScheduleActivity.class);
        Bundle bundle = new Bundle();
        i.putExtra("scheduleid", mParamSI);
        i.putExtra("schedulenew", false);
        i.putExtra("lineID", mParamLineID);
        i.putExtra("unitID", mParamUnitID);
        startActivity(i);

    }

    void loadRepeatEdit(){
        Intent i = new Intent(getContext(),RepeatScheduleActivity.class);
        Bundle bundle = new Bundle();
        i.putExtra("scheduleid", mParamSI);
        i.putExtra("schedulenew", false);
        i.putExtra("lineID", mParamLineID);
        i.putExtra("unitID", mParamUnitID);
        startActivity(i);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule_list, container, false);
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

    void deleteSchedule(){
        ScheduleHelper sh = new ScheduleHelper();
        RestCallBack rcallback =  new RestCallBack() {
            int count = 0;
            @Override
            public void onResponse(Object obj) {
                //@TODO Count Actual responses
                int x = 1;
                //if(count++ == 2) {
//                config.reset();
                RestStore.deleteBO(mParamSI);
                Intent i = new Intent(getContext(), ManageSchedulesActivity.class);
                i.putExtra("lineID", mParamLineID);
                i.putExtra("unitID", mParamUnitID);
            startActivity(i);
                //}
            }
            @Override
            public void onResponse(String token, String user) {

            }
            @Override
            public void onFailure() {
                //@TODO
                int x = 1;
            }

        };
        sh.deleteScheduleFromService(getContext(), rcallback, mParamSI);


    }

}
