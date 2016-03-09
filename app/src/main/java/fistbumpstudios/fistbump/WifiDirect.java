package fistbumpstudios.fistbump;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class WifiDirect {

    public static WifiP2pManager mManager;
    public static WifiP2pManager.Channel mChannel;
    public static BroadcastReceiver mReceiver;
    public static IntentFilter mIntentFilter;
    //TextView message_area;
    //TextView input_area;
    public static ServerSocket serverSocket;
    public static Collection<Socket> clientSockets = new ArrayList<Socket>();
    public static  Map<String, Socket> mac_to_socket_map = new HashMap<String, Socket>();
    public static Thread serverThread = null;
    public static Thread clientThread = null;
    public static final int SERVERPORT = 8080;
    public static InetAddress serverAddr = null;
    public static String p2p_mac_address = null;
    public static String friend_mac_addr = null;


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
        /*if (!clientSockets.isEmpty() && input_area.getText().length() > 0) {
            send_message("The Destroyer", input_area.getText().toString(), null);
            input_area.setText("");
        }*/
        send_message("The Destroyer", "test", null);
    }

    public void send_file_button_press(View view) {
        // send_file takes in file path and file name
        if (!clientSockets.isEmpty())
            send_file(Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + File.separator + "test.jpg", "test.jpg", null);
    }
    public static void make_server_thread()
    {
        display_message("I am a server!\n");
        serverThread = new Thread(new ServerThread());
        serverThread.start();
    }
    public static void make_client_thread(InetAddress server_addr)
    {
        display_message("I am a client!\n");
        serverAddr = server_addr;
        clientThread = new Thread(new ClientThread());
        clientThread.start();
    }

    static class ServerThread implements Runnable {

        public void run() {
            Socket socket = null;
            try {
                serverSocket = new ServerSocket(SERVERPORT);


                while (!Thread.currentThread().isInterrupted()) {
                    display_message("I am waiting for a client\n");

                    socket = serverSocket.accept();

                    if (socket != null) {
                        display_message("I have connected with a client\n");
                        byte[] client_mac_address_buffer = new byte[17];
                        socket.getInputStream().read(client_mac_address_buffer, 0, 17);
                        String client_mac_address = new String(client_mac_address_buffer, "UTF-8");
                        mac_to_socket_map.put(client_mac_address, socket);
                        display_message(mac_to_socket_map.toString() + "\n");
                        clientSockets.add(socket);
                        if (friend_mac_addr != null) {
                            send_friend_request(friend_mac_addr);
                            send_profile_pic(friend_mac_addr);
                            friend_mac_addr = null;
                        }

                        CommunicationThread commThread = new CommunicationThread(socket);
                        new Thread(commThread).start();
                        Intent intent = new Intent(tabbedMain.context, conversation.class);
                        intent.putExtra("name", "Chat");
                        intent.putExtra("id", friend_mac_addr);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        tabbedMain.context.startActivity(intent);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    static class ClientThread implements Runnable {
        private Socket clientSocket;
        public void run() {
            while (true) {
                try {
                    clientSocket = new Socket(serverAddr, SERVERPORT);
                    if (clientSocket != null) {
                        display_message("I have connected with server\n");
                        //DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
                        clientSocket.getOutputStream().write(p2p_mac_address.getBytes(), 0, 17);
                        clientSockets.add(clientSocket);
                        if (friend_mac_addr != null) {
                            send_friend_request(friend_mac_addr);
                            send_profile_pic(friend_mac_addr);
                            friend_mac_addr = null;
                        }
                        CommunicationThread commThread = new CommunicationThread(clientSocket);
                        new Thread(commThread).start();
                        Intent intent = new Intent(tabbedMain.context, conversation.class);
                        intent.putExtra("name", "Chat");
                        intent.putExtra("id", friend_mac_addr);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        tabbedMain.context.startActivity(intent);
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

    // comm thread is what sits and listens to what gets sent to the socket
    // if the phone is a client, only one comm thread exists and listens to what the server sends
    // if the phone is a server, the phone spawns a comm thread for each client
    static class CommunicationThread implements Runnable {

        private Socket clientSocket;
        private byte[] buffer = new byte[1024];
        private int buffer_to_int(byte[] bytes)
        {
            return ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16)
                    | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        }

        private void openTxtLog(String name, String msg) throws JSONException, IOException {
            JSONObject obj = new JSONObject();
            DateFormat df = new SimpleDateFormat("HH:mm MM/dd");
            String timeCreated = df.format(new Date());
            obj.put("name", name);
            obj.put("body", msg);
            obj.put("time", timeCreated);

            FileOutputStream fos = tabbedMain.context.openFileOutput( "log.txt", Context.MODE_PRIVATE | Context.MODE_APPEND);
            OutputStreamWriter out = new OutputStreamWriter(fos);
            out.append(obj.toString());
            out.append(System.getProperty("line.separator"));
            out.flush();
            fos.close();

            String message;
            FileInputStream fis = tabbedMain.context.openFileInput("log.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuffer sb = new StringBuffer();
            while ((message = br.readLine()) != null) {
                obj = new JSONObject(message);
                Message m = new Message(obj.getString("name"), "id" , obj.getString("body"), obj.getString("time"));
                conversation.messages.add(m);
                WifiDirect.display_message(m.getBody());
            }
        }

        public CommunicationThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {

            try {
                // in order to understand what the hell is going on here, you need to know the protocol
                // the first four bytes contain then option integer, this specifies what kind of transmission this is
                // the second four bytes define int name_size
                // the third four bytes define int dst_size
                // the fourth four bytes define int text_size
                // the next name_size bytes define name of user who sent the message or the name of the file
                // the next dst_size bytes define the destination user if applicable
                // the next text_size bytes define the the contents of the message or file
                InputStream is = this.clientSocket.getInputStream();
                int bytesread;
                while(!Thread.currentThread().isInterrupted()) {

                    // int option is the type of message
                    // 1 means message to be distributed
                    // 2 means file to be distributed
                    // 3 means send friend request
                    // 4 means send friend picture
                    bytesread = is.read(buffer, 0, 4);
                    int option = buffer_to_int(buffer);

                    // int name_size is the size in bytes of the name portion
                    bytesread = is.read(buffer, 0, 4);
                    int name_size = buffer_to_int(buffer);

                    // int dst_size is the size in bytes of the destination portion
                    bytesread = is.read(buffer, 0, 4);
                    int dst_size = buffer_to_int(buffer);

                    // int text_size is the size in bytes of the text portion
                    bytesread = is.read(buffer, 0, 4);
                    int text_size = buffer_to_int(buffer);

                    // String name is the name of length name_size
                    byte name_byte_arr[] = new byte [name_size];
                    bytesread = is.read(name_byte_arr, 0, name_size);
                    String name = new String(name_byte_arr, "UTF-8");

                    // String destination is the mac of who the message goes to
                    byte aux_byte_arr[] = new byte [dst_size];
                    bytesread = is.read(aux_byte_arr, 0, dst_size);
                    String auxillary = new String(aux_byte_arr, "UTF-8");

                    if (option == 1) // message to all
                    {
                        byte text_byte_arr[] = new byte [text_size];
                        bytesread = is.read(text_byte_arr, 0, text_size);

                        String text = new String(text_byte_arr, "UTF-8");
                        display_message(name + ": " + text + "\n");


                        try {
                            openTxtLog(name, text);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        
                        if (serverSocket != null) // if server, redistribute
                            send_message(name, text, clientSocket);

                    }
                    else if (option == 2) // file to all
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
                        if (serverSocket != null) // redistribute file if server
                            send_file(file.getPath(), file.getName(), clientSocket);
                    }
                    else if (option == 3) // directed friend request
                    {
                        // name is their name
                        // aux is our request mac address
                        // text is their mac address
                        byte text_byte_arr[] = new byte [text_size];
                        bytesread = is.read(text_byte_arr, 0, text_size);

                        String text = new String(text_byte_arr, "UTF-8");

                        if (auxillary.equals(p2p_mac_address)) {
                            display_message("Received Friend Request " + name + " " + text);
                            boolean is_friend = false;
                            for (Buddy buddy : buddiesTab.Buddies)
                            {
                                if (buddy.getID().equals(text))
                                {
                                    is_friend = true;
                                    break;
                                }
                            }
                            if (!is_friend)
                            {
                                try {
                                    tabbedMain.add_friend(name, text);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                // add friend
                            }
                        }
                        else
                        {
                            if (serverSocket != null)
                            {
                                //TODO: make friend request redistribution
                                redirect_friend_request(name, auxillary, text);
                                display_message("Redistribute Friend Request" + name + " " + auxillary + " " + text);
                            }
                        }

                    }
                    else if (option == 4)
                    {
                        // TODO: receive friend picture
                        // name is picture name
                        // aux is request direction mac
                        // text is the picture file to be sent

                        byte file_buffer[] = new byte[1024];

                        File directory = new File(Environment.getExternalStorageDirectory() + "/FistBump/ProfilePics");
                        directory.mkdir();

                        File file = new File(Environment.getExternalStorageDirectory() + "/FistBump/ProfilePics/", name);

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
                        boolean buddy_found = false;
                        String[] file_split = name.split(Pattern.quote("."));

                        for (Buddy buddy : buddiesTab.Buddies)
                        {
                            if (buddy.getID().equals(file_split[0])) {
                                buddy.changeProfilePic(Uri.fromFile(file));
                                buddy_found = true;
                            }
                        }
                        if (!auxillary.equals(p2p_mac_address)) {
                            if (serverSocket != null) // redistribute file if server
                                ;//redirect_profile_pic();//TODO: make profile pic redistribution
                            if (!buddy_found)
                                file.delete();
                        }
                    }
                    else // error, we just kill the current thread and try again with a new one.
                    {
                        display_message("Something weird happened\n");
                        if (serverSocket == null) {
                            clientSockets.clear();
                            clientThread = new Thread(new ClientThread());
                            clientThread.start();
                        }
                        return;
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
                display_message("something messed up yo\n");
                return;
            }
        }

    }

    static public void display_message(String message) {
        Log.d("DISP_MSG", message);
    }

    // send_file sends a file to everybody
    // file_path is the path of the file
    // file_name is what the file will be called when created
    // to_exclude is the socket to be ignored on send, used by server
    static public void send_file(String file_path, String file_name, Socket to_exclude)
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


            //for (int i = 0; i < 12; ++i)
            //display_message(String.valueOf(bytes[i]));

            //display_message(name + text);
            Collection<Socket> to_remove = new ArrayList<Socket>();

            for (Socket clientSocket : clientSockets) {
                try {
                    if (to_exclude != null)
                        if (to_exclude.equals(clientSocket))
                            continue;
                    if (!clientSocket.isConnected())
                    {
                        to_remove.add(clientSocket);
                        continue;
                    }

                    OutputStream outputStream = clientSocket.getOutputStream();
                    copy_Byte_Array(bytes, int_To_Byte_Array(option), 0, 4);
                    copy_Byte_Array(bytes, int_To_Byte_Array(name_length), 4, 4);
                    copy_Byte_Array(bytes, int_To_Byte_Array(0), 8, 4); // dst_length
                    copy_Byte_Array(bytes, int_To_Byte_Array(file_length), 12, 4);
                    outputStream.write(bytes, 0, 16);
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

    // send_message sends a message to everybody
    // name is the name of the sender
    // message is the message to be sent
    // to_exclude is the socket to be ignored on send, used by server
    static public void send_message(String name, String message, Socket to_exclude)
    {
        int option = 1;
        int message_length = message.length();
        int name_length = name.length();
        byte bytes[] = new byte[16];
        copy_Byte_Array(bytes, int_To_Byte_Array(option), 0, 4);
        copy_Byte_Array(bytes, int_To_Byte_Array(name_length), 4, 4);
        copy_Byte_Array(bytes, int_To_Byte_Array(0), 8, 4); // dst_length
        copy_Byte_Array(bytes, int_To_Byte_Array(message_length), 12, 4);

        Collection<Socket> to_remove = new ArrayList<Socket>();

        for (Socket clientSocket : clientSockets) {
            try {
                if (to_exclude != null) // skips excluded sockets
                    if (to_exclude.equals(clientSocket))
                        continue;
                if (!clientSocket.isConnected()) // skips and removes dead sockets
                {
                    to_remove.add(clientSocket);
                    continue;
                }
                DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());

                outputStream.write(bytes, 0, 16); // write 16 protocol bytes
                outputStream.write(name.getBytes()); // writes name of sender
                outputStream.write(message.getBytes()); // writes message

            } catch (IOException e) {
                e.printStackTrace();
                // in the event of an exception, remove the socket
                to_remove.add(clientSocket);
            }
        }
        // removes bad sockets
        for (Socket remove_socket : to_remove)
        {
            clientSockets.remove(remove_socket);
        }
        to_remove.clear();
    }

    static public void send_friend_request(String friend_mac_address)
    {
        //TODO: check if server and use other func
        if (serverSocket != null)
            redirect_friend_request(tabbedMain.userName, p2p_mac_address, friend_mac_address);
        int option = 3;
        String name = tabbedMain.userName;
        int name_length = name.length();
        int dst_length = friend_mac_address.length();
        String our_mac_address = p2p_mac_address;

        int message_length = our_mac_address.length();

        byte bytes[] = new byte[16];
        copy_Byte_Array(bytes, int_To_Byte_Array(option), 0, 4);
        copy_Byte_Array(bytes, int_To_Byte_Array(name_length), 4, 4);
        copy_Byte_Array(bytes, int_To_Byte_Array(dst_length), 8, 4);
        copy_Byte_Array(bytes, int_To_Byte_Array(message_length), 12, 4);

        Collection<Socket> to_remove = new ArrayList<Socket>();

        for (Socket clientSocket : clientSockets) {
            try {
                if (!clientSocket.isConnected()) // skips and removes dead sockets
                {
                    to_remove.add(clientSocket);
                    continue;
                }
                DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());

                outputStream.write(bytes, 0, 16); // write 16 protocol bytes
                outputStream.write(name.getBytes()); // writes name of sender
                outputStream.write(friend_mac_address.getBytes()); // writes friend mac
                outputStream.write(our_mac_address.getBytes()); // writes message

            } catch (IOException e) {
                e.printStackTrace();
                // in the event of an exception, remove the socket
                to_remove.add(clientSocket);
            }
        }
        // removes bad sockets
        for (Socket remove_socket : to_remove)
            clientSockets.remove(remove_socket);
        to_remove.clear();
    }

    static public void send_profile_pic(String friend_mac)
    {
        //TODO: check if server and use other func
        if (!clientSockets.isEmpty()) {
            int option = 4;
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(tabbedMain.context);
            String picturePath = preferences.getString("profilePic", null);
            if (picturePath == null)
                return;

            String name = p2p_mac_address + picturePath.substring(picturePath.length() - 4);
            WifiDirect.display_message(name);
            int name_length = name.length();
            String auxillary = friend_mac;
            int aux_length = auxillary.length();
            File file = new File(picturePath);
            // Get the size of the file
            int file_length = (int) file.length();
            if (file_length == 0) {
                display_message("woops, no file\n");
                return;
            }
            display_message(String.valueOf(file_length) + "\n");
            byte bytes[] = new byte[1024];

            Collection<Socket> to_remove = new ArrayList<Socket>();

            for (Socket clientSocket : clientSockets) {
                try {
                    if (!clientSocket.isConnected())
                    {
                        to_remove.add(clientSocket);
                        continue;
                    }

                    OutputStream outputStream = clientSocket.getOutputStream();
                    copy_Byte_Array(bytes, int_To_Byte_Array(option), 0, 4);
                    copy_Byte_Array(bytes, int_To_Byte_Array(name_length), 4, 4);
                    copy_Byte_Array(bytes, int_To_Byte_Array(aux_length), 8, 4);
                    copy_Byte_Array(bytes, int_To_Byte_Array(file_length), 12, 4);
                    outputStream.write(bytes, 0, 16);
                    outputStream.write(name.getBytes());
                    outputStream.write(auxillary.getBytes());

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
                clientSockets.remove(remove_socket);
            to_remove.clear();
        }
    }

    static public void redirect_friend_request(String name, String origin_mac_address, String dest_mac_address)
    {
        int option = 3;
        int name_length = name.length();
        int dst_length = dest_mac_address.length();

        int message_length = origin_mac_address.length();

        byte bytes[] = new byte[16];
        copy_Byte_Array(bytes, int_To_Byte_Array(option), 0, 4);
        copy_Byte_Array(bytes, int_To_Byte_Array(name_length), 4, 4);
        copy_Byte_Array(bytes, int_To_Byte_Array(dst_length), 8, 4);
        copy_Byte_Array(bytes, int_To_Byte_Array(message_length), 12, 4);
        Socket clientSocket = mac_to_socket_map.get(dest_mac_address);
        if (clientSocket == null)
            return;

        try {

            if (!clientSocket.isConnected()) // skips and removes dead sockets
            {
                clientSockets.remove(clientSocket);
            }
            else
            {
                DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());

                outputStream.write(bytes, 0, 16); // write 16 protocol bytes
                outputStream.write(name.getBytes()); // writes name of sender
                outputStream.write(dest_mac_address.getBytes()); // writes dest mac
                outputStream.write(origin_mac_address.getBytes()); // writes orig message
            }

        } catch (IOException e) {
            e.printStackTrace();
            // in the event of an exception, remove the socket
        }
    }

}
