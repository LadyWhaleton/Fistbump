package fistbumpstudios.fistbump;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class conversation extends AppCompatActivity {
    public static String username, buddyName;
    String logFilename;
    String id;
    public static ListView convolistView;
    Context context;
    public static ArrayAdapter<Message> convAdapater;
    public static List<Message> messages;

    int RESULTCODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = preferences.getString("UserName", null);
        messages = new ArrayList<>();
        convolistView = (ListView) findViewById(R.id.convList);
        context = getApplicationContext();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            buddyName = extras.getString("name");
            id = extras.getString("id");
        }

        logFilename = "log.txt";
        setTitle(buddyName);

        //load messageLogs
        try {
            readConvLog();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //populate messages
        LiveUpdateConversaition();
        new Thread() {

            public void run() {
                int i = 0;
                while (i++ < 1000) {
                    try {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    readConvLog();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    public void LiveUpdateConversaition() {
        convAdapater = new convListAdapter(context);
        convolistView.setAdapter(convAdapater);
    }

    private void readConvLog() throws IOException, JSONException {
        messages.clear();

        String message;
        FileInputStream fis = this.openFileInput(logFilename);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        StringBuffer sb = new StringBuffer();

        while ((message = br.readLine()) != null) {
            JSONObject obj = new JSONObject(message);
            jsonToMsg(obj);
        }

        LiveUpdateConversaition();
    }

    private void jsonToMsg(JSONObject obj) throws JSONException {
        addMsg(obj.getString("name"), obj.getString("body"), obj.getString("time"));
    }

    private void addMsg(String name, String body, String timestamp) {
        Message msg = new Message(name, "2123123", body, timestamp);
        messages.add(msg);
    }



    public static class convListAdapter extends ArrayAdapter<Message> {
        public convListAdapter(Context context) {
            super(context, R.layout.listview_bubble_left, messages);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            Message currentMsg = messages.get(position);

            // if there's no instance of this view
            if (view == null) {
                WifiDirect.display_message(currentMsg.getSenderName() + "...." + conversation.username);

                if (currentMsg.getSenderName().equals(conversation.username)) {
                    view = inflater.inflate(R.layout.listview_bubble_right, parent, false);
                    ImageView img = (ImageView)view.findViewById(R.id.ProfilePic);
                    Bitmap b = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(tabbedMain.profilePicPath),256,256);
                    img.setImageBitmap(b);
                }
                else {
                    view = inflater.inflate(R.layout.listview_bubble_left, parent, false);

                    ImageView img = (ImageView)view.findViewById(R.id.ProfilePic);
//                    Bitmap b = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(),256,256);
//                    img.setImageBitmap(b);
                }
            }
            TextView body = (TextView) view.findViewById(R.id.msgBody);
            body.setText(currentMsg.getMessageBody());

            TextView date = (TextView) view.findViewById(R.id.txtDate);
            date.setText(currentMsg.getTimeStamp());
            return view;


        }


    }


    public void sendMessage(View view) throws IOException, JSONException {
        EditText mEdit = (EditText) findViewById(R.id.messageText);
        DateFormat df = new SimpleDateFormat("HH:mm MM/dd");
        String timeCreated = df.format(new Date());
        String msg = mEdit.getText().toString();
        JSONObject obj = new JSONObject();
        obj.put("name", username);
        obj.put("body", msg);
        obj.put("time", timeCreated);

        FileOutputStream fos = openFileOutput(logFilename, Context.MODE_PRIVATE | MODE_APPEND);
        OutputStreamWriter out = new OutputStreamWriter(fos);
        out.append(obj.toString());
        out.append(System.getProperty("line.separator"));
        out.flush();
        fos.close();

        readConvLog();
        mEdit.setText("");
        mEdit.clearFocus();

        WifiDirect.send_message(tabbedMain.userName, msg , null);
    }



    public void selectFile(View view){
        Intent chooseFile;
        Intent intent;
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("file/*");
        intent = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(intent, RESULTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            Uri uri = data.getData();
            String filePath = uri.getPath();
            String [] file_split = filePath.split("/");
            WifiDirect.display_message(filePath);
            WifiDirect.send_file(filePath, file_split[file_split.length - 1], null);

        }
    }
}


