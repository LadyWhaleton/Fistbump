package fistbumpstudios.fistbump;

import android.net.Uri;

/**
 * Created by Ryota on 2/28/2016.
 */
public class Message {

    private String name;
    private String id;
    private Uri profilePic;
    private String body;
    private String timeCreated;

    Message(Buddy author, String body, String timeCreated){
        //DateFormat df = new SimpleDateFormat("HH:mm MM/dd");
        // timeCreated = df.format(new Date());
        this.name = author.getName();
        this.id = author.getID();
        this.body = body;
        this.profilePic = author.getProficPic();
        this.timeCreated = timeCreated;
    }

    Message(String name, String id , String body, String timeCreated){
        //DateFormat df = new SimpleDateFormat("HH:mm MM/dd");
        // timeCreated = df.format(new Date());
        this.name = name;
        this.id = id;
        this.body = body;
        this.profilePic = null;
        this.timeCreated = timeCreated;
    }


    public String getSenderName()
    {
        return this.name;
    }
    public String getSenderID()
    {
        return this.id;
    }
    public String getBody(){ return  this.body;}
    public Uri getSenderPic()
    {
        return this.profilePic;
    }
    public String getMessageBody()
    {
        return this.body;
    }
    public String getTimeStamp()
    {
        return this.timeCreated;
    }

}
