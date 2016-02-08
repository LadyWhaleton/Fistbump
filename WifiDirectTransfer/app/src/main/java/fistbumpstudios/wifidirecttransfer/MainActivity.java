package fistbumpstudios.wifidirecttransfer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private final IntentFilter intentFilter = new IntentFilter();
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    TextView message_area;
    TextView input_area;
    private ServerSocket serverSocket;
    public Socket clientSocket;
    private ServerSocket serverFileSocket;
    public Socket clientFileSocket;
    Thread serverThread = null;
    Thread clientThread = null;
    public static final int SERVERTEXTPORT = 8080;
    public static final int SERVERFILEPORT = 8081;
    private InetAddress serverAddr = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        message_area = (TextView) findViewById(R.id.message_area);
        input_area = (TextView) findViewById(R.id.input_area);

        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);



    }

    public void connect_button_press(View view) {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Context context = getApplicationContext();
                Toast.makeText(context, "Finding people to connect to!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int reasonCode) {

            }
        });
    }

    public void send_button_press(View view) {
        if (clientSocket != null && input_area.getText().length() > 0)
        {
            try {
                DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
                outputStream.writeUTF(input_area.getText().toString() + "\n");
                input_area.setText("");
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void send_file_button_press(View view) {
        if (clientFileSocket != null && input_area.getText().length() > 0)
        {
            try {
                DataOutputStream outputStream = new DataOutputStream(clientFileSocket.getOutputStream());
                outputStream.writeUTF(input_area.getText().toString() + "\n");
                input_area.setText("");
                clientFileSocket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }
    }



    public void make_server_thread()
    {
        display_message("I am a server!\n");
        this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();
    }
    public void make_client_thread(InetAddress server_addr)
    {
        display_message("I am a client!\n");
        serverAddr = server_addr;
        this.clientThread = new Thread(new ClientThread());
        this.clientThread.start();
    }





    class ServerThread implements Runnable {

        public void run() {
            Socket socket = null;
            try {
                serverSocket = new ServerSocket(SERVERTEXTPORT);
                serverFileSocket = new ServerSocket(SERVERFILEPORT);


                while (!Thread.currentThread().isInterrupted()) {

                    socket = serverSocket.accept();
                    clientFileSocket = serverFileSocket.accept();
                    display_message("I have connected with a client\n");
                    clientSocket = socket;


                    CommunicationThread commThread = new CommunicationThread(socket);
                    new Thread(commThread).start();

                    ReceiveFileThread readFileThread = new ReceiveFileThread(clientFileSocket);
                    new Thread(readFileThread).start();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    class CommunicationThread implements Runnable {

        private Socket clientSocket;

        public CommunicationThread(Socket clientSocket) {

            this.clientSocket = clientSocket;
        }

        public void run() {

            try {
                DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());

                while (!Thread.currentThread().isInterrupted()) {

                    String read = inputStream.readUTF();
                    display_message(read);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    class ReceiveFileThread implements Runnable {

        private Socket clientSocket;

        public ReceiveFileThread(Socket clientSocket) {

            this.clientSocket = clientSocket;
        }

        public void run() {



            try {
                String savedAs = "WDFL_File_" + System.currentTimeMillis() + ".txt";
                File file = new File(
                        Environment.getExternalStorageDirectory(),
                        savedAs);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                InputStream is = this.clientSocket.getInputStream();

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1)
                {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }

                display_message("File has been received!\n");


            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    class ClientThread implements Runnable {

        public void run() {

            try {
                clientSocket = new Socket(serverAddr, SERVERTEXTPORT);
                clientFileSocket = new Socket(serverAddr, SERVERFILEPORT);
                display_message("I have connected with server\n");

                ReceiveFileThread readFileThread = new ReceiveFileThread(clientSocket);
                new Thread(readFileThread).start();

                DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());

                while (!Thread.currentThread().isInterrupted()) {

                    String read = inputStream.readUTF();
                    display_message(read);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //public void broadcastIntent(){
    //  Intent intent = new Intent();
    //intent.setAction("WIFI_P2P_STATE_CHANGED_ACTION");
    //sendBroadcast(intent);
    //}
    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    public void display_message(String message) {
        final String msg = message;
        MainActivity.this.runOnUiThread(new Runnable() {


            @Override
            public void run() {
                message_area.append(msg);
            }
        });
    }
}
