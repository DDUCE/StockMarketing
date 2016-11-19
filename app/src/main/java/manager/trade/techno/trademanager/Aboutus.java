package manager.trade.techno.trademanager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class Aboutus extends Fragment {


    public Aboutus() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertview = inflater.inflate(R.layout.fragment_aboutus, container, false);


        Button img_fb= (Button)convertview.findViewById(R.id.img_f);

        Button img_t = (Button)convertview.findViewById(R.id.img_t);
        img_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Test", "onClickListener ist gestartet");
                String facebookUrl = "https://www.facebook.com/vimox.shah";
                try {
                    int versionCode = getActivity().getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
                    if (versionCode >= 3002850) {
                        Uri uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
                        startActivity(new Intent(Intent.ACTION_VIEW, uri));
                        ;
                    } else {
                        // open the Facebook app using the old method (fb://profile/id or fb://pro
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    // Facebook is not installed. Open the browser
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));
                }
            }
        });

        img_t.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    // Check if the Twitter app is installed on the phone.
                    getActivity().getPackageManager().getPackageInfo("com.twitter.android", 0);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setClassName("com.twitter.android", "com.twitter.android.ProfileActivity");
                    // Don't forget to put the "L" at the end of the id.
                    intent.putExtra("user_id", 280942481L);
                    startActivity(intent);
                } catch (PackageManager.NameNotFoundException e) {
                    // If Twitter app is not installed, start browser.
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/Vimox_shah")));
                }
            }
        });


        return convertview;
    }

}
