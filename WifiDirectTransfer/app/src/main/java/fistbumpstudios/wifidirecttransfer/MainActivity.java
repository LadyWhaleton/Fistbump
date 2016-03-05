package fistbumpstudios.wifidirecttransfer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

public class MainActivity extends AppCompatActivity {
    private final IntentFilter intentFilter = new IntentFilter();
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    TextView message_area;
    TextView input_area;
    private ServerSocket serverSocket;
    private Collection<Socket> clientSockets = new ArrayList<Socket>();
    Thread serverThread = null;
    Thread clientThread = null;
    public static final int SERVERPORT = 8080;
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
                Context context = getApplicationContext();
                Toast.makeText(context, "Stop spamming the button fool!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static byte[] int_To_Byte_Array(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    private static void copy_Byte_Array(byte[] dst, byte[] src, int dst_offset, int size) {
        for (int i = 0; i < size; ++i)
            dst[i + dst_offset] = src[i];
    }

    public void send_button_press(View view) {
        if (!clientSockets.isEmpty() && input_area.getText().length() > 0) {
            send_message("The Destroyer", input_area.getText().toString());
        }
    }

    public void send_file_button_press(View view) {
        // send_file takes in file path and file name
        if (!clientSockets.isEmpty())
            send_file(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + "test.jpg", "test.jpg");
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
                serverSocket = new ServerSocket(SERVERPORT);


                while (!Thread.currentThread().isInterrupted()) {
                    display_message("I am waiting for a client\n");

                    socket = serverSocket.accept();
                    display_message("I have connected with a client\n");
                    if (socket != null) {
                        clientSockets.add(socket);


                        CommunicationThread commThread = new CommunicationThread(socket);
                        new Thread(commThread).start();
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    class CommunicationThread implements Runnable {

        private Socket clientSocket;
        private byte[] buffer = new byte[1024];
        private int buffer_to_int(byte[] bytes, int offset)
        {
            return ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16)
                    | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        }

        public CommunicationThread(Socket clientSocket) {

            this.clientSocket = clientSocket;
        }

        public void run() {

            try {
                while(!Thread.currentThread().isInterrupted()) {


                     //String read = inputStream.readUTF();
                     //display_message(read);
                    InputStream is = this.clientSocket.getInputStream();
                    //ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    int bytesread;
                    String what = "";


                    bytesread = is.read(buffer, 0, 4);
                    int option = buffer_to_int(buffer, 0);
                    for (int i = 0; i < bytesread; ++i)
                      what += buffer[i] + ".";
                    what += " ";

                    bytesread = is.read(buffer, 0, 4);
                    int name_size = buffer_to_int(buffer, 0);
                    for (int i = 0; i < bytesread; ++i)
                        what += buffer[i] + ".";
                    what += " ";

                    bytesread = is.read(buffer, 0, 4);
                    int text_size = buffer_to_int(buffer, 0);
                    for (int i = 0; i < bytesread; ++i)
                        what += buffer[i] + ".";
                    what += "\n";
                    display_message(what);

                    byte name_byte_arr[] = new byte [name_size];
                    bytesread = is.read(name_byte_arr, 0, name_size);

                    String name = new String(name_byte_arr, "UTF-8");

                    if (option == 1)
                    {
                        byte text_byte_arr[] = new byte [text_size];
                        bytesread = is.read(text_byte_arr, 0, text_size);

                        String text = new String(text_byte_arr, "UTF-8");
                        display_message(name + ": " + text + "\n");
                    }
                    else if (option == 2)
                    {
                        byte file_buffer[] = new byte[1024];

                        File file = new File(Environment.getExternalStorageDirectory(), name);

                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        int bytes_read_so_far = 0;
                        int bytes_read_this_loop = 0;
                        while (bytes_read_so_far < text_size) {
                            bytes_read_this_loop = is.read(file_buffer, 0, Math.min(text_size - bytes_read_so_far, 1024));
                            bytes_read_so_far += bytes_read_this_loop;
                            fileOutputStream.write(file_buffer, 0, bytes_read_this_loop);
                        }
                        display_message(text_size + "\n");

                        display_message("Made a file called " + name + " of size " + bytes_read_so_far + " bytes\n");
                    }
                    else
                    {
                        display_message("Something weird happened\n");
                        if (serverSocket == null) {
                            clientThread = new Thread(new ClientThread());
                            clientThread.start();
                        }
                        return;
                    }

                    //for (int i = 0; i < bytesread; ++i)
                      //  what += (char)buffer[i];

                    //display_message(what + "\n");

                }
            } catch (IOException e) {
                e.printStackTrace();
                display_message("something messed up yo\n");
                return;
            }
        }

    }

    class ClientThread implements Runnable {
        private Socket clientSocket;

        public void run() {
            while (true) {
                try {
                    clientSocket = new Socket(serverAddr, SERVERPORT);
                    if (clientSocket != null) {
                        display_message("I have connected with server\n");
                        clientSockets.add(clientSocket);
                        CommunicationThread commThread = new CommunicationThread(clientSocket);
                        new Thread(commThread).start();
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(5000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

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

    private void send_file(String file_path, String file_name)
    {
        if (!clientSockets.isEmpty()) {
            int option = 2;

            String name = file_name;
            int name_length = name.length();
            File file = new File(file_path);
            // Get the size of the file
            int file_length = (int) file.length();
            if (file_length == 0) {
                display_message("woops, no file\n");
                return;
            }
            display_message(String.valueOf(file_length) + "\n");
            byte bytes[] = new byte[1024];
            copy_Byte_Array(bytes, int_To_Byte_Array(option), 0, 4);
            copy_Byte_Array(bytes, int_To_Byte_Array(name_length), 4, 4);
            copy_Byte_Array(bytes, int_To_Byte_Array(file_length), 8, 4);

            //for (int i = 0; i < 12; ++i)
            //display_message(String.valueOf(bytes[i]));

            //display_message(name + text);
            Collection<Socket> to_remove = new ArrayList<Socket>();

            for (Socket clientSocket : clientSockets) {
                try {
                    // add if not connected, add to to_remove
                    if (!clientSocket.isConnected())
                    {
                        to_remove.add(clientSocket);
                        continue;
                    }

                    OutputStream outputStream = clientSocket.getOutputStream();
                    outputStream.write(bytes, 0, 12);
                    outputStream.write(name.getBytes());

                    InputStream in = new FileInputStream(file);

                    int count;
                    int bytes_sent = 0;
                    while ((count = in.read(bytes)) > 0) {
                        outputStream.write(bytes, 0, count);
                        bytes_sent += count;
                    }
                    display_message(String.valueOf(bytes_sent) + "\n");


                } catch (IOException e) {
                    e.printStackTrace();
                    to_remove.add(clientSocket);
                }
            }
            for (Socket remove_socket : to_remove)
            {
                clientSockets.remove(remove_socket);
            }
            to_remove.clear();
        }
    }

    private void send_message(String name, String message)
    {
        int option = 1;
        int message_length = message.length();
        int name_length = name.length();
        input_area.setText("");
        byte bytes[] = new byte[1024];
        copy_Byte_Array(bytes, int_To_Byte_Array(option), 0, 4);
        copy_Byte_Array(bytes, int_To_Byte_Array(name_length), 4, 4);
        copy_Byte_Array(bytes, int_To_Byte_Array(message_length), 8, 4);

        //for (int i = 0; i < 12; ++i)
        //display_message(String.valueOf(bytes[i]));

        //display_message(name + text);
        Collection<Socket> to_remove = new ArrayList<Socket>();

        for (Socket clientSocket : clientSockets) {
            try {
                if (!clientSocket.isConnected())
                {
                    to_remove.add(clientSocket);
                    continue;
                }
                DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());

                outputStream.write(bytes, 0, 12);
                outputStream.write(name.getBytes());
                outputStream.write(message.getBytes());

            } catch (IOException e) {
                e.printStackTrace();
                to_remove.add(clientSocket);
            }
        }
        for (Socket remove_socket : to_remove)
        {
            clientSockets.remove(remove_socket);
        }
        to_remove.clear();
    }

}