package fistbumpstudios.fistbump;

import android.net.Uri;

/**
 * Created by Sephi on 2/29/2016.
 */
public class Message {
    private String name; // indicates the sender's name
    private String macAddress; // indicates the sender's mac address
    private Uri profilePic;
    private int message_id; // message's identifier
    private int chat_id; // indicates which chat this message belongs to
    private String timestamp;

    Message (Buddy author, int mid, int cid, String timestamp)
    {
        this.name = author.getName();
        this.macAddress = author.getID();
        this.profilePic = author.getProficPic();
        this.message_id = mid;
        this.chat_id = cid;
        this.timestamp = timestamp;
    }
}
