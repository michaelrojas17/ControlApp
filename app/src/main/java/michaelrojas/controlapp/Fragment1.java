package michaelrojas.controlapp;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import static android.content.Context.VIBRATOR_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class Fragment1 extends Fragment {

    private OnFragmentInteractionListener mListener;

    static String MQTTHOST = "tcp://io.adafruit.com:1883";
    static String USERNAME = "mike921217";
    static String PASSWORD = "62d2cbedd8c346cc8bfb4c029b26fcc3";
    static String topicStr = USERNAME +"/feeds/Color";
    static String topicStr2 = USERNAME +"/feeds/Mensaje";


    MqttAndroidClient client;
    TextView subText, MensajeLeido;
    EditText MensajeAEnviar;
    MqttConnectOptions options;
    Vibrator vibrator;
    Ringtone myRingtone;
    int Conectado;

    public Fragment1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_fragment1, container, false);



        Button btnConectar = (Button)view.findViewById(R.id.connBtn);
        Button btnEnviar = (Button)view.findViewById(R.id.btnEnviarTexto);
        Button btnBorrar = (Button)view.findViewById(R.id.btnBorrarMensaje);
        Button btnDesconectar = (Button)view.findViewById(R.id.disconnbtn);


        Button btnRojo = (Button)view.findViewById(R.id.btnRojo);
        Button btnVerde = (Button)view.findViewById(R.id.btnVerde);
        Button btnAzul = (Button)view.findViewById(R.id.btnAzul);





        //subText = (TextView)view.findViewById(R.id.subText);
        MensajeLeido = (TextView)view.findViewById(R.id.tvMensajeLeido);
        MensajeAEnviar = (EditText)view.findViewById(R.id.etTextoAEnviar);

        vibrator=(Vibrator)getActivity().getSystemService(VIBRATOR_SERVICE);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        myRingtone = RingtoneManager.getRingtone(getActivity().getApplicationContext(),uri);


        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(getActivity().getApplicationContext(), MQTTHOST, clientId);

        options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());



        btnConectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Conectado=1;

                try {
                    IMqttToken token = client.connect(options);
                    token.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // We are connected
                            Toast.makeText(getActivity(), "Connected(1)!!", Toast.LENGTH_LONG).show();
                            setSubscription();
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            // Something went wrong e.g. connection timeout or firewall problems

                            Toast.makeText(getActivity(), "Connection Failed", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }

            }
        });


        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String topic = USERNAME+"/feeds/Mensaje";
                String message = "***Deleted***";



                if(Conectado==1) {

                    try {

                        client.publish(topic, message.getBytes(), 0, false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }

                }else{

                    Toast.makeText(getActivity(), "No connected", Toast.LENGTH_LONG).show();

                }

            }
        });

        btnDesconectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Conectado=0;

                try {
                    IMqttToken token = client.disconnect();
                    token.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // We are connected
                            Toast.makeText(getActivity(), "Disconnected!!", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            // Something went wrong e.g. connection timeout or firewall problems

                            Toast.makeText(getActivity(), "DCould not disconnect..", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }

            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String topic = USERNAME+"/feeds/Mensaje";
                String message = MensajeAEnviar.getText().toString();



                if(Conectado==1) {

                    try {

                        client.publish(topic, message.getBytes(), 0, false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }

                }else{

                    Toast.makeText(getActivity(), "No connected", Toast.LENGTH_LONG).show();

                }

            }
        });



        btnRojo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String topic = topicStr;
                String message = "Rojo";

                if(Conectado==1) {

                    try {

                        client.publish(topic, message.getBytes(), 0, false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }

                }else{

                    Toast.makeText(getActivity(), "No connected", Toast.LENGTH_LONG).show();

                }

            }
        });

        btnVerde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String topic = topicStr;
                String message = "Verde";

                if(Conectado==1) {

                    try {

                        client.publish(topic, message.getBytes(), 0, false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }

                }else{

                    Toast.makeText(getActivity(), "No connected", Toast.LENGTH_LONG).show();

                }

            }
        });

        btnAzul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String topic = topicStr;
                String message = "Azul";

                if(Conectado==1) {

                    try {

                        client.publish(topic, message.getBytes(), 0, false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }

                }else{

                    Toast.makeText(getActivity(), "No connected", Toast.LENGTH_LONG).show();

                }

            }
        });



        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(getActivity(), "Connected!!", Toast.LENGTH_LONG).show();
                    Conectado=1;
                    setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems

                    Toast.makeText(getActivity(), "Connection Failed", Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {


                if(topic.equals(topicStr)) {
                    subText.setText(new String(message.getPayload()));
                    myRingtone.play();
                    vibrator.vibrate(500);
                }

                if(topic.equals(topicStr2)) {
                    MensajeLeido.setText(new String(message.getPayload()));
                    vibrator.vibrate(1000);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });







        return view;
        //return inflater.inflate(R.layout.fragment_fragment1, container, false);


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









    private void setSubscription(){

        //String[] topics={topicStr, topicStr2};
        String topic=topicStr;




        try{

            client.subscribe(topicStr, 0);
            client.subscribe(topicStr2, 0);

        }catch(MqttException e){
            e.printStackTrace();
        }


    }





}
