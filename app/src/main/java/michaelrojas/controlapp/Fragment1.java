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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
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


    //static String MQTTHOST = "tcp://192.168.0.150:1883";
    //static String MQTTHOST = "tcp://wifi-lights-control-broker.etowns.net:1883";
    //static String MQTTHOST = "tcp://wledscontrolbroker.ddns.net:1883";

    //static String MQTTHOST= "tcp://157.88.110.210:1883"; //Deberia ser un dominio...
    //static String MQTTHOST = "tcp://wifi-lights-control-broker.etowns.net:1883"; //Wifi
    static String MQTTHOST = "tcp://wledscontrolbroker.ddns.net:1883"; //Cable


    //static String MQTTHOST = "tcp://192.168.0.150:1883";
    static String USERNAME = "mike921217";
    static String PASSWORD = "921217";
    static String topicColor = "Color";
    static String topicMensaje = "Mensaje";
    static String topicModo = "Modo";


    MqttAndroidClient client;
    TextView subText, MensajeLeido, MensajeModo;
    Button btnRojo, btnVerde, btnAzul, btnConectar;
    Button btnEnviar,btnBorrar,btnDesconectar;
    EditText MensajeAEnviar;
    Switch   SwitchModo;

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



        btnConectar = (Button)view.findViewById(R.id.connBtn);
        btnEnviar = (Button)view.findViewById(R.id.btnEnviarTexto);
        btnBorrar = (Button)view.findViewById(R.id.btnBorrarMensaje);
        btnDesconectar = (Button)view.findViewById(R.id.disconnbtn);

        btnRojo = (Button)view.findViewById(R.id.btnRojo);
        btnVerde = (Button)view.findViewById(R.id.btnVerde);
        btnAzul = (Button)view.findViewById(R.id.btnAzul);

        SwitchModo = (Switch)view.findViewById(R.id.swtModo);






        MensajeModo = (TextView)view.findViewById(R.id.tvMensajeModoConf);
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
                            Toast.makeText(getActivity(), "Broker Connected!!", Toast.LENGTH_LONG).show();
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

                String topic = "Mensaje";
                String message = " ";



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

                String topic = "Mensaje";
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


                String topic = topicColor;
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


                String topic = topicColor;
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



                String topic = topicColor;
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


        SwitchModo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){


                    String topic = topicModo;
                    String message = "FF";

                    if(Conectado==1) {

                        try {

                            client.publish(topic, message.getBytes(), 0, false);
                        } catch (MqttException e) {
                            e.printStackTrace();
                       }

                        Toast.makeText(getActivity(), "Se arrancará en modo confiuración !!!", Toast.LENGTH_LONG).show();
                        MensajeModo.setText("Se reiniciará en modo configuracion!!");


                    }else{

                        Toast.makeText(getActivity(), "No connected", Toast.LENGTH_LONG).show();

                    }





                }else{ //Si el boton esta en off

                    String topic = topicModo;
                    String message = "00";

                    if(Conectado==1) {

                        try {

                           client.publish(topic, message.getBytes(), 0, false);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(getActivity(), "Se arrancará en modo confiuración !!!", Toast.LENGTH_LONG).show();
                        MensajeModo.setText(" ");


                    }else{

                        Toast.makeText(getActivity(), "No connected", Toast.LENGTH_LONG).show();

                    }



                }

            }
        });









        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(getActivity(), "Broker Connected!!", Toast.LENGTH_LONG).show();
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


                if(topic.equals(topicColor)) {
                    subText.setText(new String(message.getPayload()));
                    myRingtone.play();
                    vibrator.vibrate(500);
                }

                if(topic.equals(topicMensaje)) {
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


        String topic=topicColor;




        try{

            client.subscribe(topicColor, 0);
            client.subscribe(topicMensaje, 0);

        }catch(MqttException e){
            e.printStackTrace();
        }


    }





}
