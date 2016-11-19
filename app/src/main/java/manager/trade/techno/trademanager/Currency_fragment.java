package manager.trade.techno.trademanager;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class Currency_fragment extends Fragment {


    EditText et_input_digit,et_final_amonut;
    Spinner sp_first_curncy,sp_secnd_curncy;
    String[] curency_tag = {"AUD","BGN","BRL","CAD","CHF","CNY","CZK","DKK","EUR","GBP","HKD","HRK","HUF","IDR","ILS","INR","JPY","KRW","MXN","MYR","NOK","NZD","PHP","PLN","RON","RUB","SEK","SGD","THB","TRY","USD","ZAR"};
    String str_fst_cuurency,str_scnd_currency,res;
    Button btn_convert;
    Boolean isInternetPresent = false;


    public Currency_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "fonts/ProductSans-Regular.ttf");

        final View convertView = inflater.inflate(R.layout.fragment_currency_fragment, container, false);
        //==add this line to change all font to coustom font in fragments
        fontChanger.replaceFonts((ViewGroup)convertView);


        et_input_digit=(EditText)convertView.findViewById(R.id.et_input_digit);
        et_final_amonut=(EditText)convertView.findViewById(R.id.et_final_amount);
        sp_first_curncy= (Spinner)convertView.findViewById(R.id.sp_first_curency);
        sp_secnd_curncy= (Spinner)convertView.findViewById(R.id.sp_second_curency);
        btn_convert=(Button)convertView.findViewById(R.id.btn_convert);

        ArrayAdapter<String> ad = new ArrayAdapter<String>(getContext(),android.R.layout.simple_expandable_list_item_1,curency_tag);
        ad.setDropDownViewResource(android.R.layout.simple_list_item_1);
        sp_first_curncy.setAdapter(ad);
        sp_secnd_curncy.setAdapter(ad);
        sp_first_curncy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                str_fst_cuurency=curency_tag[i].toString();
                Log.d("curency_selected",str_fst_cuurency);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_secnd_curncy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                str_scnd_currency=curency_tag[i].toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // creating connection detector class instance
                ConnectionDetector cd = new ConnectionDetector(getContext());
                // get Internet status
                isInternetPresent = cd.isConnectingToInternet();

                // check for Internet status
                if (isInternetPresent && et_input_digit.getText().toString()!=null) {
                    // Internet Connection is Present
                    // make HTTP requests
                    if(str_fst_cuurency.equals(str_scnd_currency)){

                        et_final_amonut.setText(et_input_digit.getText().toString());
                    }else{
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            new GetCurrency().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            new GetCurrency().execute();
                        }
                    }


                } else {
                    // Internet connection is not present
                    // Ask user to connect to Internet
                    Snackbar.make(convertView, "No internet connection!", Snackbar.LENGTH_LONG)
                            .setAction("Setting", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                                }
                            }).show();


                }

            }
        });





        return convertView;
    }
    class GetCurrency extends AsyncTask<Object, Void, String> {



        @Override
        protected void onPreExecute()//execute thaya pela
        {

            super.onPreExecute();
            // Log.d("pre execute", "Executando onPreExecute ingredients");




        }

        @Override
        protected String doInBackground(Object... parametros) {

            // System.out.println("On do in back ground----done-------");


            //Log.d("post execute", "Executando doInBackground   ingredients");

// should be a singleton
            OkHttpClient client = new OkHttpClient();
            /*HttpUrl.Builder urlBuilder = HttpUrl.parse("https://ajax.googleapis.com/ajax/services/search/images").newBuilder();
            urlBuilder.addQueryParameter("v", "1.0");
            urlBuilder.addQueryParameter("q", "android");
            urlBuilder.addQueryParameter("rsz", "8");
            String url = urlBuilder.build().toString();*/



            Request request = new Request.Builder()
                    .url("http://api.fixer.io/latest?base="+str_fst_cuurency)
                    .build();



            try{
                //request mate nicheno code
                Response response = client.newCall(request).execute();

                res=response.body().string();
                // Log.d("okhtp==",res1);

            }catch(Exception e){
                e.printStackTrace();

            }



//            progressDialog.dismiss();
            return res;

        }



        @Override
        protected void onPostExecute(String result)
        {

            String response_string="";
            // System.out.println("OnpostExecute----done-------");
            super.onPostExecute(result);

            if (res == null || res.equals("")) {



                Toast.makeText(getContext(), "Network connection ERROR or ERROR", Toast.LENGTH_LONG).show();
                //

                return;
            }

            try {
                JSONObject obj = new JSONObject(res);

                String rates = obj.getString("rates");
                JSONObject all_rates= new JSONObject(rates);

                Log.i("rates", rates);

                String selected_curency = all_rates.getString(str_scnd_currency);

                if(selected_curency!=null){
                    int input_digit = Integer.parseInt(et_input_digit.getText().toString());
                    Double final_amount = Double.parseDouble(selected_curency)*input_digit;
                    et_final_amonut.setText(new DecimalFormat("#,##,###,###.##").format(final_amount));

                }else{
                    et_input_digit.setError("Enter Digit Here!");
                }









            }

            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }






        }
    }

}
