package fistbumpstudios.fistbump;

import android.net.Uri;
import java.io.File;

/**
 * Created by Sephi on 3/1/2016.
 * This class is used for storing information about the file sent.
 * http://developer.android.com/reference/android/media/package-summary.html
 */
public class Media {

    String fileName;
    File file;
    String timeReceived;
    String ownerName;
    String id;
    Uri ownerProfilePic;
    Uri thumbnail;
    int mediaType;


    Media (String fileName, String timeReceived, Buddy sender)
    {
        this.fileName = fileName;
        this.timeReceived = timeReceived;
        this.ownerName = sender.getName();
        this.id = sender.getID();
        this.ownerProfilePic = sender.getProficPic();
    }

    public String getOwnerName()
    {
        return this.ownerName;
    }

    public Uri getOwnerProfilePic()
    {
        return this.ownerProfilePic;
    }

    public String getTimestamp()
    {
        return this.timeReceived;
    }

    public String getMediaPath()
    {
        return this.file.getPath();
    }

}
