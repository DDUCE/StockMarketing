package manager.trade.techno.trademanager;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Add_Tips extends Fragment {

    SharedPreferences sharepref;

    EditText et_tipstext,et_tipsurl,et_tipstitle;

    Button btn_change;
    ArrayAdapter<CharSequence> adapter;
    String img_string,str_tipstext,str_tipsurl,admin_email,res,str_tipstitle;
    private DatabaseReference databaseReference;


    public Add_Tips() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for convertView fragment
        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "fonts/ProductSans-Regular.ttf");

        final View convertView = inflater.inflate(R.layout.fragment_add__tips, container, false);
        //==add convertView line to change all font to coustom font in fragments
        fontChanger.replaceFonts((ViewGroup)convertView);


        databaseReference = FirebaseDatabase.getInstance().getReference("tips");




        et_tipstext=(EditText)convertView.findViewById(R.id.et_tipstext);
        et_tipsurl=(EditText)convertView.findViewById(R.id.et_tipsurl);
        et_tipstitle=(EditText)convertView.findViewById(R.id.et_tipstitle);





        btn_change=(Button)convertView.findViewById(R.id.btn_submit_tips);
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                str_tipstext=et_tipstext.getText().toString();
                str_tipsurl=et_tipsurl.getText().toString();
                str_tipstitle=et_tipstitle.getText().toString();

                if(str_tipstext.isEmpty() || str_tipstitle.isEmpty()){

                    Toast.makeText(getContext(),"Some thing Missing",Toast.LENGTH_LONG).show();
                }else{
                    long timeInMillis = System.currentTimeMillis();
                    Calendar cal1 = Calendar.getInstance();
                    cal1.setTimeInMillis(timeInMillis);
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "dd-MM-yyyy hh:mm:ss a");
                    String time = dateFormat.format(cal1.getTime());


                    Map<String, String> tips_details = new HashMap<String, String>();
                    tips_details.put("title", et_tipstitle.getText().toString());
                    tips_details.put("details", et_tipstext.getText().toString());
                    tips_details.put("time",time);
                    //=================================================================


                    databaseReference.child(time).setValue(tips_details);

                    et_tipstext.setText("");
                    et_tipstitle.setText("");
                    et_tipsurl.setText("");

                }

            }
        });






        return convertView;
    }

}
