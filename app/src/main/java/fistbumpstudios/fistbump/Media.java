package fistbumpstudios.fistbump;

import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * Created by Sephi on 3/1/2016.
 * This class is used for storing information about the file sent.
 * http://developer.android.com/reference/android/media/package-summary.html
 */
public class Media {

    File mediaFile; // not sure if we will need this.

    String fileName;
    String timeReceived;
    String mediaType;
    String mimeType;

    // info about the media's original sender
    String ownerName;
    String id;
    Uri ownerProfilePic;


    Media (String fileName, String timeReceived, Buddy sender)
    {
        this.fileName = fileName;
        this.timeReceived = timeReceived;
        this.ownerName = sender.getName();
        this.id = sender.getID();
        this.ownerProfilePic = sender.getProficPic();
    }

    // This function should be called when you are creating a new media object aft
    private void setMediaType()
    {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1,  fileName.length());
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        String[] parsedMimeType = mimeType.split("/");

        this.mediaType = parsedMimeType[0];
        this.mimeType = mimeType;
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

    public String getMediaType()
    {
        return this.mediaType;
    }

    public String getMediaPath()
    {
        // TODO: Implement this function with corresponding directory folder.
        /** Example:
         * File file = new File("test.txt");
         * String filePath = file.getAbsolutePath();
         * return filePath;
         */

        return "filepath";

    }

    /** This function takes the time the file was received and gets the difference between
     * now and then. So if a file was received on 2/1/13 at 1:00 PM and the current time is
     * 2/1/13 at 2:01, it should display "an hour ago".
     *
     */
    public String getElapsedTime()
    {
        return "time";
    }

}
