package fistbumpstudios.fistbump;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ryota on 2/28/2016.
 */
public class Message {

    public String name;
    public String id;
    public String msg;
    public String timeCreated;

    Message(String n, String i, String m){
        DateFormat df = new SimpleDateFormat("HH:mm MM/dd");
        timeCreated = df.format(new Date());
        name = n;
        id = i;
        msg = m;
    }

}
