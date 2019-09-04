package auto.ausiot.autosensor.widgets;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import auto.ausiot.autosensor.R;

public class CustomDialogClass extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes, no;
    View.OnClickListener onclick;
    String title;
    String details;
    public CustomDialogClass(Activity a, View.OnClickListener onclick, String title , String details) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.onclick = onclick;
        this.title = title;
        this.details = details;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alert_dialog);
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

        TextView txtTitle = (TextView)findViewById(R.id.txt_title);
        TextView txtDetails = (TextView)findViewById(R.id.txt_details);
        txtTitle.setText(title);
        txtDetails.setText(details);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                c.finish();
                onclick.onClick(v);
                break;
            case R.id.btn_no:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}