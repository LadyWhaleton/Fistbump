package fistbumpstudios.fistbump;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

/**
 * Created by Stephanie Tong on 2/5/2016.
 *
 * This file contains stuff for the Buddy class.
 * Each buddy has a name, a unique ID (their MAC address), avatar, online status, etc.
 */
public class Buddy {

    private String name, id;
    private Uri profilePic;
    private String statusMessage;
    private boolean onlineStatus;


    Buddy (String name, String id, Uri image)
    {
        this.name = name;
        this.id = id;
        this.profilePic = image;
        this.onlineStatus = false;
    }

    public String getName()
    {
        return this.name;
    }
    public String getID () {return this.id; }
    public Uri getProficPic() { return this.profilePic; }

    public Bitmap getProfilePicBitmap ()
    {
        String folderName = Environment.getExternalStorageDirectory().toString() + "/FistBump/ProfilePics";
        File picJPG = new File (folderName + this.id + ".jpg");
        File picPNG = new File (folderName + this.id + ".png");

        if (picJPG.exists())
            return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(picJPG.toString()), 256, 256);
        else if (picPNG.exists())
            return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(picPNG.toString()), 256, 256);

        return null;
    }

    public String getStatusMessage() { return this.statusMessage; }
    public boolean isOnline() { return onlineStatus; }

    public void changeName(String newName)
    {
        this.name = newName;
    }
    public void changeStatusMessage (String newStatusMessage) { this.statusMessage = newStatusMessage; }
    public void changeProfilePic(Uri newProfilePic) { this.profilePic = newProfilePic; }
    public void changeOnlineStatus(boolean newOnlineStatus) { this.onlineStatus = newOnlineStatus; }

}
