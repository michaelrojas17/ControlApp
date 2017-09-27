package michaelrojas.controlapp;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;






public class Fragment2 extends Fragment {

    private OnFragmentInteractionListener mListener;

    static String IP_SERVER = "http://192.168.4.1/";


    Button btnEscanear, btnGuardar;
    TextView tvReinicio;
    EditText etSSID, etPass;


    ////////////*********************************

    String cadena;



    public Fragment2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_fragment2, container, false);


        btnGuardar = (Button)view.findViewById(R.id.btnGuardar);
        etPass = (EditText) view.findViewById(R.id.etPass);
        etSSID = (EditText) view.findViewById(R.id.etSSID);
        tvReinicio = (TextView) view.findViewById(R.id.tvReinicio);





        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String parametroSSID = etSSID.getText().toString();
                String parametroPasswd = etPass.getText().toString();

                ConnectivityManager conMngr = (ConnectivityManager)
                        getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo networkInfo = conMngr.getActiveNetworkInfo();

                if(networkInfo != null && networkInfo.isConnected()){

                    String url = IP_SERVER+"guardar_conf?ssid="+ parametroSSID +"&pass="+parametroPasswd;


                    new SolicitaDatos().execute(url);
                }else{
                    Toast.makeText(getActivity(), "Ninguna conexión detectada", Toast.LENGTH_LONG).show();

                }

            }
        });









        return view;
    }


    private class SolicitaDatos extends AsyncTask<String, Void, String > {

        @Override
        protected String doInBackground(String... url) {
            return Conexion.getData(url[0]);
        }

        @Override
        protected void onPostExecute(String resultado) {
            if(resultado != null){


                if(resultado.contains("Configuracion Guardada")){
                    Toast.makeText(getActivity(), "Configuración Guardada...", Toast.LENGTH_LONG).show();
                    tvReinicio.setText("Configuración WiFi Guardada. Por favor, reinicie el controlador ESP...");
                }

            }else{
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
            }
        }
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
}
